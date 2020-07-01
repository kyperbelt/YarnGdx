package com.kyper.yarn;

import org.junit.jupiter.api.*;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class LanguageTests extends TestBase {
    public LanguageTests() {
        super();

        // Register some additional functions
        dialogue.getLibrary().registerFunction("add_three_operands", 3, (Value[] parameters) ->
                Arrays.stream(parameters).reduce(Value.defaultValue(parameters[0].getType()), Value::add));

        dialogue.getLibrary().registerFunction("last_value", -1, (Value[] parameters) ->
                parameters[parameters.length - 1]);
    }

    @Test
    public void TestExampleScript() throws IOException {
        errorsCauseFailures = false;
        Path path = getTestDataPath().resolve("Example.yarn");
        Path testPath = getTestDataPath().resolve("Example.testplan");

        dialogue.loadFile(path);
        loadTestPlan(testPath);

        runStandardTestcase(null);
    }

    @Test @Disabled("Node merging is not currently possible")
    public void testMergingNodes()
    {
//        var sallyPath = Path.Combine(SpaceDemoScriptsPath, "Sally.yarn");
//        var shipPath = Path.Combine(SpaceDemoScriptsPath, "Ship.yarn");
//
//        Compiler.CompileFile(sallyPath, out var sally, out var sallyStringTable);
//        Compiler.CompileFile(shipPath, out var ship, out var shipStringTable);
//
//
//        var combinedWorking = Program.Combine(sally, ship);
//
//        // Loading code with the same contents should throw
//        Assert.Throws<InvalidOperationException>(delegate ()
//        {
//            var combinedNotWorking = Program.Combine(sally, ship, ship);
//        });
    }



    @Test
    public void testEndOfNotesWithOptionsNotAdded() throws IOException {
        Path path = getTestDataPath().resolve("SkippedOptions.yarn");
        dialogue.loadFile(path);

        dialogue.setOptionsHandler((Dialogue.OptionResult optionSets) -> {
            fail("Options should not be shown to the user in this test.");
        });

        dialogue.start();
        dialogue.update();
    }

    @Test
    public void testNodeHeaders() throws IOException {
        Path path = getTestDataPath().resolve("Headers.yarn");
        dialogue.loadFile(path);

        assertEquals(3, dialogue.getAllNodes().size());

        assertTrue(dialogue.getAllNodes().get("Tags").tags.containsAll(Arrays.asList("one", "two", "three")));
    }

    @Test
    public void testInvalidCharactersInNodeTitle() {
        Path path = getTestDataPath().resolve("InvalidNodeTitle.yarn");
        // Technically the original test here was directly on the compiler but it's not easy to access.
        assertThrows(Program.ParseException.class, () -> dialogue.loadFile(path));
    }
//
//        @Test
//    public void TestFormatFunctionParsing()
//    {
//        var input = @"prefix [plural ""5"" one=""one"" two=""two"" few=""few"" many=""many""] suffix";
//
//
//        var expectedLineWithReplacements = @"prefix {0} suffix";
//        var expectedFunctionName = "plural";
//        var expectedValue = "5";
//        var expectedParameters = new Dictionary<string,string>() {
//            {"one", "one"},
//            {"two", "two"},
//            {"few", "few"},
//            {"many", "many"},
//        };
//
//        Dialogue.ParseFormatFunctions(input, out string lineWithReplacements, out ParsedFormatFunction[] parsedFunctions);
//
//        Assert.Equal(expectedLineWithReplacements, lineWithReplacements);
//        Assert.Equal(1, parsedFunctions.Length);
//        Assert.Equal(expectedFunctionName, parsedFunctions[0].functionName);
//        Assert.Equal(expectedValue, parsedFunctions[0].value);
//        Assert.Equal(expectedParameters, parsedFunctions[0].data);
//
//    }
//
//        @Test
//    public void TestNumberPlurals() {
//
//        (string, double , PluralCase )[] cardinalTests = new[] {
//
//            // English
//            ("en", 1, PluralCase.One),
//            ("en", 2, PluralCase.Other),
//            ("en", 1.1, PluralCase.Other),
//
//            // Arabic
//            ("ar", 0, PluralCase.Zero),
//            ("ar", 1, PluralCase.One),
//            ("ar", 2, PluralCase.Two),
//            ("ar", 3, PluralCase.Few),
//            ("ar", 11, PluralCase.Many),
//            ("ar", 100, PluralCase.Other),
//            ("ar", 0.1, PluralCase.Other),
//
//            // Polish
//            ("pl", 1, PluralCase.One),
//            ("pl", 2, PluralCase.Few),
//            ("pl", 3, PluralCase.Few),
//            ("pl", 4, PluralCase.Few),
//            ("pl", 5, PluralCase.Many),
//            ("pl", 1.1, PluralCase.Other),
//
//            // Icelandic
//            ("is", 1, PluralCase.One),
//            ("is", 21, PluralCase.One),
//            ("is", 31, PluralCase.One),
//            ("is", 41, PluralCase.One),
//            ("is", 51, PluralCase.One),
//            ("is", 0, PluralCase.Other),
//            ("is", 4, PluralCase.Other),
//            ("is", 100, PluralCase.Other),
//            ("is", 3.0, PluralCase.Other),
//            ("is", 4.0, PluralCase.Other),
//            ("is", 5.0, PluralCase.Other),
//
//            // Russian
//            ("ru", 1, PluralCase.One),
//            ("ru", 2, PluralCase.Few),
//            ("ru", 3, PluralCase.Few),
//            ("ru", 5, PluralCase.Many),
//            ("ru", 0, PluralCase.Many),
//            ("ru", 0.1, PluralCase.Other),
//
//
//        };
//
//        (string, int , PluralCase )[] ordinalTests = new[] {
//            // English
//            ("en", 1, PluralCase.One),
//            ("en", 2, PluralCase.Two),
//            ("en", 3, PluralCase.Few),
//            ("en", 4, PluralCase.Other),
//            ("en", 11, PluralCase.Other),
//            ("en", 21, PluralCase.One),
//
//            // Welsh
//            ("cy", 0, PluralCase.Zero),
//            ("cy", 7, PluralCase.Zero),
//            ("cy", 1, PluralCase.One),
//            ("cy", 2, PluralCase.Two),
//            ("cy", 3, PluralCase.Few),
//            ("cy", 4, PluralCase.Few),
//            ("cy", 5, PluralCase.Many),
//            ("cy", 10, PluralCase.Other),
//
//        };
//
//        foreach (var test in cardinalTests) {
//            Assert.Equal(test.Item3, CLDRPlurals.NumberPlurals.GetCardinalPluralCase(test.Item1, test.Item2));
//        }
//
//        foreach (var test in ordinalTests) {
//            Assert.Equal(test.Item3, CLDRPlurals.NumberPlurals.GetOrdinalPluralCase(test.Item1, test.Item2));
//        }
//
//
//    }
//
//    // Test every file in Tests/TestCases
//        [Theory]
//            [MemberData(nameof(FileSources), "TestCases")]
//            [MemberData(nameof(FileSources), "Issues")]
//    public void TestSources(string file) {
//
//        Console.ForegroundColor = ConsoleColor.Blue;
//        Console.WriteLine ($"INFO: Loading file {file}");
//
//        storage.Clear();
//        bool runTest = true;
//
//        var scriptFilePath = Path.Combine(TestDataPath, file);
//        var testPlanFilePath = Path.ChangeExtension(scriptFilePath, ".testplan");
//
//        // skipping the indentation test when using the ANTLR parser
//        // it can never pass
//        if (file == "TestCases/Indentation.yarn")
//        {
//            runTest = false;
//        }
//
//        if (runTest)
//        {
//            loadTestPlan(testPlanFilePath);
//
//            Compiler.CompileFile(scriptFilePath, out var program, out stringTable);
//            dialogue.SetProgram(program);
//
//            // If this file contains a Start node, run the test case
//            // (otherwise, we're just testing its parsability, which
//            // we did in the last line)
//            if (dialogue.NodeExists("Start"))
//                runStandardTestcase();
//        }
//    }
//
//    // Returns the list of .node and.yarn files in the
//    // Tests/<directory> directory.
//    public static IEnumerable<object[]> FileSources(string directory) {
//
//        var allowedExtensions = new[] { ".node", ".yarn" };
//
//        var path = Path.Combine(TestDataPath, directory);
//
//        var files = GetFilesInDirectory(path);
//
//        return files.Where(p => allowedExtensions.Contains(Path.GetExtension(p)))
//                        .Select(p => new[] {Path.Combine(directory, Path.GetFileName(p))});
//    }
//
//    // Returns the list of files in a directory. If that directory doesn't
//    // exist, returns an empty list.
//    static IEnumerable<string> GetFilesInDirectory(string path)
//    {
//        try
//        {
//            return Directory.EnumerateFiles(path);
//        }
//        catch (DirectoryNotFoundException)
//        {
//            return new string[] { };
//        }
//    }
}
