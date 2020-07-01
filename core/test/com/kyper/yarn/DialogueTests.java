package com.kyper.yarn;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class DialogueTests extends TestBase {
	@Test
	public void testNodeExists() throws IOException {
		System.out.println("TestNodeExists --");
		Path path = getSpaceDemoScriptsPath().resolve("Sally.yarn");

		dialogue.loadFile(path, false, false, null);

		assertTrue(dialogue.nodeExists("Sally"));
		// Test clearing everything
		dialogue.unloadAll();

		assertFalse(dialogue.nodeExists("Sally"));

	}

	@Test
	public void testOptionDestinations() throws IOException {
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

   	@Test
    public void testAnalysis() throws IOException {
		List<Analyser.Diagnosis> diagnoses;
		Analyser.Context context;

		// this script has the following variables:
		// $foo is read from and written to
		// $bar is written to but never read
		// $bas is read from but never written to
		// this means that there should be two diagnosis results
		context = new Analyser.Context(new Analyser.UnusedVariableChecker());

		Path path = getTestDataPath().resolve("AnalysisTest.yarn");
		dialogue.loadFile(path);
		dialogue.analyse(context);
		diagnoses = context.finalAnalysis();

		assertEquals(2, diagnoses.size());

		dialogue.unloadAll();
	}

	@Test @Disabled("I don't think this is possible yet")
	public void testCombinedProgramAnalysis() {
		// TODO I don't think the lower tests are possible given current API
//        context = new Analyser.Context(new Analyser.UnusedVariableChecker());
//
//		Path path = getSpaceDemoScriptsPath().resolve("Ship.yarn");
//
//		Path sallyPath = getSpaceDemoScriptsPath().resolve("Sally.yarn");
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
    }


    @Test
    public void testDumpingCode() throws IOException {
		Path path = getTestDataPath().resolve("Example.yarn");

		dialogue.loadFile(path, true, true, null);

        String byteCode = dialogue.getByteCode();
        assertNotNull(byteCode);
        assertFalse(byteCode.isEmpty());
    }


    @Test
    public void testMissingNode() throws IOException {
		Path path = getTestDataPath().resolve(Paths.get("TestCases", "Smileys.yarn"));

		dialogue.loadFile(path);
        dialogue.start();

		errorsCauseFailures = false;
		boolean result = dialogue.setNode("THIS NODE DOES NOT EXIST");
		assertFalse(result);
		assertEquals("no node named THIS NODE DOES NOT EXIST", getLastError());
    }


    @Test
    public void testGettingCurrentNodeName() throws IOException {
		Path path = getSpaceDemoScriptsPath().resolve("Sally.yarn");
		dialogue.loadFile(path);

        // dialogue should not be running yet
        assertNull(dialogue.getCurrentNode());

        dialogue.start("Sally");
		assertEquals("Sally", dialogue.getCurrentNode().name);

		dialogue.setNode("Sally.Watch");
        assertEquals("Sally.Watch", dialogue.getCurrentNode().name);

        dialogue.stop();
        // Current node should now be null
        assertNull(dialogue.getCurrentNode());
	}


    @Test @Disabled("Not sure if this is possible yet")
    public void testGettingRawSource() {
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
    }

	@Test
	public void testGettingTags() throws IOException {
		System.out.println("TestGettingTags --");
		Path path = getTestDataPath().resolve("Example.yarn");
		dialogue.loadFile(path, true, true, null);

		Program.Node node = dialogue.program.getNodes().get("LearnMore");
		List<String> source = node.tags;

		assertNotNull(source);

		assertFalse(source.isEmpty());

		assertEquals("rawText", source.get(0));
	}

}
