package com.kyper.yarn;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.kyper.yarn.Dialogue.OptionSet;
import com.kyper.yarn.compiler.YarnCompiler;

public class DialogueTests extends TestBase {
	@ParameterizedTest
	@ValueSource(strings = { "Sally.yarn" })
	public void testNodeExists(String filename) throws IOException {
		System.out.println("TestNodeExists --");
		stringTable.clear();
		Path path = getSpaceDemoScriptsPath().resolve(filename);

		Program program = new Program();

		YarnCompiler.compileFile(path, program, stringTable);

		dialogue.setProgram(program);

		assertTrue(dialogue.nodeExists("Sally"));
		// Test clearing everything
		dialogue.unloadAll();

		assertFalse(dialogue.nodeExists("Sally"));

	}

	@Test
	public void testOptionDestinations() throws IOException {
		System.out.println("TestOptionDestinations --");
		stringTable.clear();
		Path path = getTestDataPath().resolve("Options.yarn");
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);

		AtomicBoolean callbackCalled = new AtomicBoolean(false);
		dialogue.setOptionsHandler((OptionSet optionSet) -> {
			assertEquals(2, optionSet.getOptions().length);
			// TODO original tests referenced DestinationNode but this has been erased from
			// runtime types
			assertEquals("B", optionSet.getOptions()[0].getDestination());
			assertEquals("C", optionSet.getOptions()[1].getDestination());
			callbackCalled.set(true);

		});
		dialogue.setNode("A");
		dialogue.continueRunning();

		await().until(() -> callbackCalled.get());

	}

	@Test
	public void testAnalysis() throws IOException {
		List<Analyser.Diagnosis> diagnoses;
		stringTable.clear();
		Analyser.Context context;

		// this script has the following variables:
		// $foo is read from and written to
		// $bar is written to but never read
		// $bas is read from but never written to
		// this means that there should be two diagnosis results
		context = new Analyser.Context(new Analyser.UnusedVariableChecker());

		Path path = getTestDataPath().resolve("AnalysisTest.yarn");
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);

		dialogue.setProgram(program);
		dialogue.analyse(context);
		diagnoses = context.finalAnalysis();

		assertEquals(2, diagnoses.size());

		dialogue.unloadAll();

		context = new Analyser.Context(new Analyser.UnusedVariableChecker());
		Path shipPath = getSpaceDemoScriptsPath().resolve("Ship.yarn");
		stringTable.clear();
		Program shipProgram = new Program();
		YarnCompiler.compileFile(shipPath, shipProgram, stringTable);
		Path sallyPath = getSpaceDemoScriptsPath().resolve("Sally.yarn");
		Program sallyProgram = new Program();
		YarnCompiler.compileFile(sallyPath, sallyProgram, stringTable);

		Program combinedProgram = VirtualMachine.combinePrograms(shipProgram, sallyProgram);

		dialogue.setProgram(combinedProgram);
		dialogue.analyse(context);
		diagnoses = context.finalAnalysis();

		assertEquals(0, diagnoses.size());

	}

	@Test
	public void testDumpingCode() throws IOException {
		Path path = getTestDataPath().resolve("Example.yarn");
		stringTable.clear();
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);
		String byteCode = dialogue.getByteCode();
		// assertNotNull(byteCode);
		assertFalse(byteCode.isEmpty());
	}

	@Test
	public void testMissingNode() throws IOException {
		stringTable.clear();
		Path path = getTestDataPath().resolve(Paths.get("TestCases", "Smileys.yarn"));
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);

		dialogue.setProgram(program);

		errorsCauseFailures = false;
		assertThrows(DialogueException.class, () -> {
			dialogue.setNode("THIS NODE DOES NOT EXIST");
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "Sally.yarn" })
	public void testGettingCurrentNodeName(String filename) throws IOException {
		Path path = getSpaceDemoScriptsPath().resolve(filename);
		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);
		// dialogue should not be running yet

		assertNull(dialogue.getCurrentNode());

		dialogue.setNode("Sally");
		assertEquals("Sally", dialogue.getCurrentNode().name);

		dialogue.setNode("Sally.Watch");
		assertEquals("Sally.Watch", dialogue.getCurrentNode().name);

		dialogue.stop();
		// Current node should now be null
		assertNull(dialogue.getCurrentNode());
	}

	@Test
	public void testGettingRawSource() {
		stringTable.clear();
		Path path = getTestDataPath().resolve("Example.yarn");

		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);

		String sourceID = dialogue.getStringIDForNode("LearnMore");
		String source = stringTable.get(sourceID).text;

		assertNotNull(source);

		assertEquals("A: HAHAHA\n", source);
	}

	@Test
	public void testGettingTags() throws IOException {
		System.out.println("TestGettingTags --");
		Path path = getTestDataPath().resolve("Example.yarn");
		stringTable.clear();

		Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);

		List<String> source = dialogue.getTagsForNode("LearnMore");
		
		assertNotNull(source);

		assertFalse(source.isEmpty());

		assertEquals("rawText", source.get(0));
	}

}
