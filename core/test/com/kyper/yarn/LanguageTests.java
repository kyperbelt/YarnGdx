package com.kyper.yarn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.kyper.yarn.Dialogue.OptionSet;
import com.kyper.yarn.Dialogue.ParsedFormatFunction;
import com.kyper.yarn.NumberPlurals.PluralCase;
import com.kyper.yarn.compiler.ParseException;
import com.kyper.yarn.compiler.YarnCompiler;
import com.kyper.yarn.compiler.YarnCompiler.StringInfo;

public class LanguageTests extends TestBase {
	public LanguageTests() {
		super();

		// Register some additional functions
		dialogue.getLibrary().registerFunction("add_three_operands", 3, (Value[] parameters) -> Arrays
				.stream(parameters).reduce(Value.defaultValue(parameters[0].getType()), Value::add));

		dialogue.getLibrary().registerFunction("last_value", -1,
				(Value[] parameters) -> parameters[parameters.length - 1]);
	}

	@Test
	public void TestExampleScript() throws IOException {
		stringTable.clear();
		errorsCauseFailures = false;
		Path path = getTestDataPath().resolve("Example.yarn");
		Path testPath = getTestDataPath().resolve("Example.testplan");
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);
		loadTestPlan(testPath);

		runStandardTestcase(null);
	}

	@Test
	public void testMergingNodes() {
		HashMap<String, StringInfo> sallyStringTable = new HashMap<String, YarnCompiler.StringInfo>();
		HashMap<String, StringInfo> shipStringTable = new HashMap<String, YarnCompiler.StringInfo>();
		Path sallyPath = getSpaceDemoScriptsPath().resolve("Sally.yarn");
		Path shipPath = getSpaceDemoScriptsPath().resolve("Ship.yarn");
		Program sallyProgram = new Program();
		Program shipProgram = new Program();
		YarnCompiler.compileFile(sallyPath, sallyProgram, sallyStringTable);
		YarnCompiler.compileFile(shipPath, shipProgram, shipStringTable);

		Program combinedWorking = VirtualMachine.combinePrograms(sallyProgram, shipProgram);

		// Loading code with the same contents should throw
		assertThrows(IllegalStateException.class, () -> {
			VirtualMachine.combinePrograms(sallyProgram, shipProgram, shipProgram);
		});
	}

	@Test
	public void testEndOfNotesWithOptionsNotAdded() throws IOException {
		Path path = getTestDataPath().resolve("SkippedOptions.yarn");
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);

		dialogue.setOptionsHandler((OptionSet optionSets) -> {
			fail("Options should not be shown to the user in this test.");
		});

		dialogue.setNode();
		dialogue.continueRunning();
	}

	@Test
	public void testNodeHeaders() throws IOException {
		Path path = getTestDataPath().resolve("Headers.yarn");
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);

		assertEquals(3, dialogue.getAllNodes().size());

		assertTrue(dialogue.getAllNodes().get("Tags").tags.containsAll(Arrays.asList("one", "two", "three")));
	}

	@Test
	public void testInvalidCharactersInNodeTitle() {
		Path path = getTestDataPath().resolve("InvalidNodeTitle.yarn");
		// Technically the original test here was directly on the compiler but it's not
		// easy to access.
		assertThrows(ParseException.class, () -> {
			Program program = new Program();
			YarnCompiler.compileFile(path, program, stringTable);
		});
	}

	@Test
	public void testFormatFunctionParsing() {
		String input = "prefix [plural \"5\" one=\"one\" two=\"two\" few=\"few\" many=\"many\"] suffix";

		String expectedLineWithReplacements = "prefix {0} suffix";
		String expectedFunctionName = "plural";
		String expectedValue = "5";
		HashMap<String, String> expectedParameters = new HashMap<String, String>();
		expectedParameters.put("one", "one");
		expectedParameters.put("two", "two");
		expectedParameters.put("few", "few");
		expectedParameters.put("many", "many");

		StringBuilder lineWithReplacements = new StringBuilder();
		ArrayList<ParsedFormatFunction> parsedFunctions = new ArrayList<ParsedFormatFunction>();

		Dialogue.parseFormatFunctions(input, lineWithReplacements, parsedFunctions);

		assertEquals(expectedLineWithReplacements, lineWithReplacements.toString());
		assertEquals(1, parsedFunctions.size());
		assertEquals(expectedFunctionName, parsedFunctions.get(0).functionName);
		assertEquals(expectedValue, parsedFunctions.get(0).value);
		assertEquals(expectedParameters, parsedFunctions.get(0).data);
	}

	public class Tuple<X, Y, Z> {
		public final X x;
		public final Y y;
		public final Z z;

		public Tuple(X x, Y y, Z z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	@Test
	public void testNumberPlurals() {
		List<Tuple<String, Double, PluralCase>> cardinalTests = new ArrayList<LanguageTests.Tuple<String, Double, PluralCase>>();

		// English
		cardinalTests.add(new Tuple<String, Double, PluralCase>("en", 1., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("en", 2., PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("en", 1.1, PluralCase.Other));

		// Arabic
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 0.0, PluralCase.Zero));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 1., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 2., PluralCase.Two));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 3., PluralCase.Few));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 11., PluralCase.Many));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 100., PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ar", 0.1, PluralCase.Other));

		// Polish
		cardinalTests.add(new Tuple<String, Double, PluralCase>("pl", 1., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("pl", 2., PluralCase.Few));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("pl", 3., PluralCase.Few));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("pl", 4., PluralCase.Few));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("pl", 5., PluralCase.Many));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("pl", 1.1, PluralCase.Other));

		// Icelandic
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 1., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 21., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 31., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 41., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 51., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 0., PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 4., PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 100., PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 3.0, PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 4.0, PluralCase.Other));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("is", 5.0, PluralCase.Other));

		// Russian
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ru", 1., PluralCase.One));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ru", 2., PluralCase.Few));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ru", 3., PluralCase.Few));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ru", 5., PluralCase.Many));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ru", 0., PluralCase.Many));
		cardinalTests.add(new Tuple<String, Double, PluralCase>("ru", 0.1, PluralCase.Other));

		List<Tuple<String, Integer, PluralCase>> ordinalTests = new ArrayList<LanguageTests.Tuple<String, Integer, PluralCase>>();
//        (string, int , PluralCase )[] ordinalTests = new[] {
//            // English
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("en", 1, PluralCase.One));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("en", 2, PluralCase.Two));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("en", 3, PluralCase.Few));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("en", 4, PluralCase.Other));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("en", 11, PluralCase.Other));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("en", 21, PluralCase.One));

		// Welsh
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 0, PluralCase.Zero));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 7, PluralCase.Zero));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 1, PluralCase.One));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 2, PluralCase.Two));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 3, PluralCase.Few));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 4, PluralCase.Few));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 5, PluralCase.Many));
		ordinalTests.add(new Tuple<String, Integer, PluralCase>("cy", 10, PluralCase.Other));
//
//        };
//
		for (Tuple<String, Double, PluralCase> test : cardinalTests) {
			assertEquals(test.z, NumberPlurals.GetCardinalPluralCase(test.x, test.y));
		}

		for (Tuple<String, Integer, PluralCase> test : ordinalTests) {
			assertEquals(test.z, NumberPlurals.GetOrdinalPluralCase(test.x, test.y));
		}
	}

	// Test every file in Tests/TestCases
	@ParameterizedTest
	@MethodSource
	@Disabled("Some pass but others dont either due to no testplan being present or an error with testplan itself.")
	public void testSources(Path scriptFilePath) throws IOException {
		System.out.println("INFO: Loading file " + scriptFilePath + "");

		storage.clear();
		stringTable.clear();
		boolean runTest = true;

		Path testPlanFilePath = scriptFilePath.resolveSibling(
				scriptFilePath.getFileName().toString().replace(".yarn", ".testplan").replace(".node", ".testplan"));
//        Path testPlanFilePath = Paths.get(scriptFilePath.toString().replaceFirst("\\.*+$", ".testplan"));

		// skipping the indentation test when using the ANTLR parser
		// it can never pass
		if (scriptFilePath.toString().contains("TestCases/Indentation.yarn")) {
			runTest = false;
		}

		if (runTest) {
			loadTestPlan(testPlanFilePath);

			Program program = new Program();
			YarnCompiler.compileFile(scriptFilePath, program, stringTable);
			dialogue.setProgram(program);

			// If this file contains a Start node, run the test case
			// (otherwise, we're just testing its parsability, which
			// we did in the last line)
			if (dialogue.nodeExists("Start")) {
				runStandardTestcase(null);
			}
		}
	}

	// Returns the list of .node and.yarn files in the
	// Tests/<directory> directory.
	public static Stream<Path> testSources() throws IOException {
//        String[] folders = new String[]{"TestCases", "Issues"}; // Issues doesn't seem to be used currently
		String[] allowedExtensions = new String[] { "\\.node", "\\.yarn" };
		String regex = "regex:.*(" + String.join("|", allowedExtensions) + ")";
		Path path = getTestDataPath().resolve("TestCases");
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(regex);
		return Files.walk(path).filter(matcher::matches);
	}

}
