package com.kyper.yarn;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTests extends  TestBase {
    @ParameterizedTest
    @ValueSource(strings = { "Test.yarn", "Test.json" })
    public void TestLoadingNodes(String filename) throws IOException {
        Path path = getTestDataPath().resolve(Paths.get("Projects", "Basic", filename));
        dialogue.loadFile(path);

        // high-level test: load the file, verify it has the nodes we want,
        // and run one

        assertEquals(3, dialogue.getAllNodes().size());

        assertTrue(dialogue.nodeExists("TestNode"));
        assertTrue(dialogue.nodeExists("AnotherTestNode"));
        assertTrue(dialogue.nodeExists("ThirdNode"));
    }
}
