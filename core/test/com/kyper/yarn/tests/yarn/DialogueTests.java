package com.kyper.yarn.tests.yarn;

import com.kyper.yarn.Dialogue;
import com.kyper.yarn.Program;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class DialogueTests extends TestBase {
	@org.junit.jupiter.api.Test
	public void TestNodeExists() throws IOException {
		System.out.println("TestNodeExists --");
		Path path = getSpaceDemoScriptsPath().resolve("Sally.yarn");

//        Compiler compiler = new Compiler(data);
		dialogue.loadFile(path, false, false, null);
//        dialogue.SetProgram(program);

		assertTrue(dialogue.nodeExists("Sally"));
		// Test clearing everything
		dialogue.unloadAll();

		assertFalse(dialogue.nodeExists("Sally"));

	}

	@org.junit.jupiter.api.Test
	public void TestOptionDestinations() throws IOException {
		System.out.println("TestOptionDestinations --");
		Path path = getTestDataPath().resolve("Options.yarn");
		dialogue.loadFile(path, true, true, null);

		

		AtomicBoolean callbackCalled = new AtomicBoolean(false);
		dialogue.setOptionsHandler((Dialogue.OptionResult optionSet) -> {
			assertEquals(2, optionSet.getOptions().size());
			// TODO original tests referenced DestinationNode but this has been erased from
			// runtime types
			assertEquals("Go to B", optionSet.getOptions().get(0));
			assertEquals("Go to C", optionSet.getOptions().get(1));
			callbackCalled.set(true);
			
		});
		dialogue.start("A");
		dialogue.checkNext();
		
		await().until(() -> callbackCalled.get());

	}

//
//
//    @org.junit.jupiter.api.Test
//    public void TestAnalysis() {
//
//        ICollection<Yarn.Analysis.Diagnosis> diagnoses;
//        Yarn.Analysis.Context context;
//
//        // this script has the following variables:
//        // $foo is read from and written to
//        // $bar is written to but never read
//        // $bas is read from but never written to
//        // this means that there should be two diagnosis results
//        context = new Yarn.Analysis.Context(typeof(Yarn.Analysis.UnusedVariableChecker));
//
//        var path = Path.Combine(TestDataPath, "AnalysisTest.yarn");
//
//        Compiler.CompileFile(path, out var program, out stringTable);
//
//        dialogue.SetProgram(program);
//        dialogue.Analyse(context);
//        diagnoses = new List<Yarn.Analysis.Diagnosis>(context.FinishAnalysis());
//
//        Assert.Equal(2, diagnoses.Count);
//
//        dialogue.UnloadAll();
//
//        context = new Yarn.Analysis.Context(typeof(Yarn.Analysis.UnusedVariableChecker));
//
//        string shipPath = Path.Combine(SpaceDemoScriptsPath, "Ship.yarn");
//        Compiler.CompileFile(shipPath, out var shipProgram, out var shipStringTable);
//
//        string sallyPath = Path.Combine(SpaceDemoScriptsPath, "Sally.yarn");
//        Compiler.CompileFile(sallyPath, out var sallyProgram, out var sallyStringTable);
//
//        stringTable = shipStringTable.Union(sallyStringTable).ToDictionary(k = > k.Key, v =>v.Value);
//
//        var combinedProgram = Program.Combine(shipProgram, sallyProgram);
//
//        dialogue.SetProgram(combinedProgram);
//
//        dialogue.Analyse(context);
//        diagnoses = new List<Yarn.Analysis.Diagnosis>(context.FinishAnalysis());
//
//        // This script should contain no unused variables
//        Assert.Empty(diagnoses);
//    }
//
//
//    @org.junit.jupiter.api.Test
//    public void TestDumpingCode() {
//
//        var path = Path.Combine(TestDataPath, "Example.yarn");
//
//        Compiler.CompileFile(path, out var program, out stringTable);
//
//        dialogue.SetProgram(program);
//
//        var byteCode = dialogue.GetByteCode();
//        Assert.NotNull(byteCode);
//
//    }
//
//
//    @org.junit.jupiter.api.Test
//    public void TestMissingNode() {
//        var path = Path.Combine(TestDataPath, "TestCases", "Smileys.yarn");
//
//        Compiler.CompileFile(path, out var program, out stringTable);
//
//        dialogue.SetProgram(program);
//
//        errorsCauseFailures = false;
//
//        Assert.Throws<DialogueException> (() = > dialogue.SetNode("THIS NODE DOES NOT EXIST"));
//    }
//
//
//    @org.junit.jupiter.api.Test
//    public void TestGettingCurrentNodeName() {
//
//        string path = Path.Combine(SpaceDemoScriptsPath, "Sally.yarn");
//        Compiler.CompileFile(path, out var program, out stringTable);
//
//        dialogue.SetProgram(program);
//
//        // dialogue should not be running yet
//        Assert.Null(dialogue.currentNode);
//
//        dialogue.SetNode("Sally");
//        Assert.Equal("Sally", dialogue.currentNode);
//
//        dialogue.Stop();
//        // Current node should now be null
//        Assert.Null(dialogue.currentNode);
//    }
//
//
//    @org.junit.jupiter.api.Test
//    public void TestGettingRawSource() {
//
//        var path = Path.Combine(TestDataPath, "Example.yarn");
//
//        Compiler.CompileFile(path, out var program, out stringTable);
//        dialogue.SetProgram(program);
//
//        var sourceID = dialogue.GetStringIDForNode("LearnMore");
//        var source = stringTable[sourceID].text;
//
//        Assert.NotNull(source);
//
//        Assert.Equal("A: HAHAHA\n", source);
//    }
//
	@org.junit.jupiter.api.Test
	public void TestGettingTags() throws IOException {
		System.out.println("TestGettingTags --");
		Path path = getTestDataPath().resolve("Example.yarn");
		dialogue.loadFile(path, true, true, null);

		Program.Node node = dialogue.program.getNodes().get("LearnMore");
		List<String> source = node.tags;

		assertNotNull(source);

		assertFalse(source.isEmpty());

		assertEquals("rawText", source.get(0));

//        var path = Path.Combine(TestDataPath, "Example.yarn");
//        Compiler.CompileFile(path, out var program, out stringTable);
//        dialogue.SetProgram(program);
//
//        var source = dialogue.GetTagsForNode("LearnMore");
//
//        Assert.NotNull(source);
//
//        Assert.NotEmpty(source);
//
//        Assert.Equal("rawText", source.First());
	}

}
