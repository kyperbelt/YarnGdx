package com.kyper.yarn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.kyper.yarn.compiler.YarnCompiler;

public class ProjectTests extends  TestBase {
    @ParameterizedTest
    @ValueSource(strings = { "Test.yarn" })
    public void TestLoadingNodes(String filename) throws IOException {
    	stringTable.clear();
        Path path = getTestDataPath().resolve(Paths.get("Projects", "Basic", filename));
        Program program = new Program();
		YarnCompiler.compileFile(path, program, stringTable);
		dialogue.setProgram(program);

        // high-level test: load the file, verify it has the nodes we want,
        // and run one

        assertEquals(3, dialogue.getAllNodes().size());

        assertTrue(dialogue.nodeExists("TestNode"));
        assertTrue(dialogue.nodeExists("AnotherTestNode"));
        assertTrue(dialogue.nodeExists("ThirdNode"));
    }
}
