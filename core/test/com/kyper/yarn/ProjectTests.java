package com.kyper.yarn;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTests extends  TestBase {
    @Test
    public void TestLoadingNodes() throws IOException {
        Path path = getTestDataPath().resolve(Paths.get("Projects", "Basic", "Test.yarn"));
        dialogue.loadFile(path);

        // high-level test: load the file, verify it has the nodes we want,
        // and run one

        assertEquals(3, dialogue.getAllNodes().size());

        assertTrue(dialogue.nodeExists("TestNode"));
        assertTrue(dialogue.nodeExists("AnotherTestNode"));
        assertTrue(dialogue.nodeExists("ThirdNode"));
    }
}
