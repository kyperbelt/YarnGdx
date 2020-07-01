package com.kyper.yarn;

import com.kyper.yarn.Dialogue.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestBase {
    protected VariableStorage storage = new MemoryVariableStorage();
    protected Dialogue dialogue;
    protected Map<String, Program.LineInfo> stringTable;
    protected ArrayList<String> errorLogs = new ArrayList<>();

    public String locale = "en";

    protected boolean errorsCauseFailures = true;

    // Returns the path that contains the test case files.

    public static Path getProjectRootPath() {
//        List<String> path = Assembly.GetCallingAssembly().Location.Split(Path.DirectorySeparatorChar).ToList();
        Path resourceDirectory = Paths.get("test","resources").toAbsolutePath();
        return resourceDirectory;
//        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

//        var index = path.inde(x => x == "YarnSpinner.Tests");
//
//        if (index == -1)
//        {
//            throw new System.IO.DirectoryNotFoundException("Cannot find test data directory");
//        }

//        var testDataDirectory = path.Take(index).ToList();

//        String pathToTestData = String.Join(Path.DirectorySeparatorChar.ToString(CultureInfo.InvariantCulture), testDataDirectory.ToArray());

//        return pathToTestData;
}


    public static Path getTestDataPath() {
        return getProjectRootPath().resolve("Tests");
//        return Path.Combine(ProjectRootPath, "Tests");
    }

    public static Path getSpaceDemoScriptsPath() {
        return getTestDataPath().resolve(Paths.get("Projects", "Space"));
//        return Path.Combine(ProjectRootPath, "Tests/Projects/Space");
    }

    private TestPlan testPlan;

    public String GetComposedTextForLine(LineResult line) {
        // TODO stringTable localization handling?
//        var baseText = stringTable[line.ID].text;
        String baseText = line.getText();
        return GetComposedTextForLine(line.getText());
    }
    public String GetComposedTextForLine(String line) {
        return line;
        // TODO substitution handling?
//        for (int i = 0; i < line.Substitutions.Length; i++) {
//            String substitution = line.Substitutions[i];
//            baseText = baseText.Replace("{" + i + "}", substitution);
//        }

//        return Dialogue.ExpandFormatFunctions(baseText, locale);
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        errorLogs.clear();
    }

    public TestBase() {
        YarnLogger errorLogger = (String message) -> {
            errorLogs.add(message);
            System.err.println("ERROR: " + message);
            if (errorsCauseFailures == true) {
                assertNotNull(message);
            }
        };

        dialogue = new Dialogue(storage, System.out::println, errorLogger);
        registerListeners();
    }

    void registerListeners() {
        // TODO VM used to be private so may need to add another layer of callbacks on Dialogue so we don't clobber them
        dialogue.setLineHandler((LineResult line) -> {
//            var id = line.ID;
//            assertTrue(stringTable.containsKey(id));

            String text = GetComposedTextForLine(line);

            System.out.println("Line: " + text);

            if (testPlan != null) {
                testPlan.Next();

                if (testPlan.nextExpectedType == TestPlan.Type.Line) {
                    assertEquals(testPlan.nextExpectedValue, text);
                } else {
                    fail("Received line "+text+", but was expecting a "+testPlan.nextExpectedType.toString());
                }
            }

//            return Dialogue.HandlerExecutionType.ContinueExecution;
        });

        dialogue.setOptionsHandler((OptionResult optionSet) -> {
            int optionCount = optionSet.getOptions().size();

            System.out.println("Options:");
            for(String option : optionSet.getOptions()) {
                // TODO localization handling?
//                var optionText = GetComposedTextForLine(option.Line);
                String optionText = GetComposedTextForLine(option);
                System.out.println(" - " + optionText);
            }

            if (testPlan != null) {
                testPlan.Next();

                if (testPlan.nextExpectedType != TestPlan.Type.Select) {
                    fail("Received "+optionCount+" options, but wasn't expecting them (was expecting "+testPlan.nextExpectedType.toString()+")");
                }

                // Assert that the list of options we were given is
                // identical to the list of options we expect
                List<String> actualOptionList = optionSet.getOptions().stream().map(this::GetComposedTextForLine).collect(Collectors.toList());
                assertEquals(testPlan.nextExpectedOptions, actualOptionList);

                int expectedOptionCount = testPlan.nextExpectedOptions.size();

                assertEquals(expectedOptionCount, optionCount);

                if (testPlan.nextOptionToSelect != -1) {
                    optionSet.choose(testPlan.nextOptionToSelect - 1);
//                    dialogue.SetSelectedOption(testPlan.nextOptionToSelect - 1);
                } else {
                    optionSet.choose(0);
//                    dialogue.SetSelectedOption(0);
                }
            }
        });

        dialogue.setCommandHandler((CommandResult command) -> {
            System.out.println("Command: " + command.getCommand());

            if (testPlan != null) {
                testPlan.Next();
                if (testPlan.nextExpectedType != TestPlan.Type.Command)
                {
                    fail("Received command "+command.getCommand()+", but wasn't expecting to select one (was expecting "+testPlan.nextExpectedType.toString()+")");
                }
                else
                {
                    // We don't need to get the composed String for a
                    // command because it's been done for us in the
                    // virtual machine. The VM can do this because
                    // commands are not localised, so we don't need to
                    // refer to the String table to get the text.
                    assertEquals(testPlan.nextExpectedValue, command.getCommand());
                }
            }

//            return Dialogue.HandlerExecutionType.ContinueExecution;
        });

        dialogue.getLibrary().registerFunction("assert", 1, (Value[] parameters) -> {
            if (parameters[0].asBool() == false) {
                fail("Assertion failed");
            }
        });


        // When a node is complete, just indicate that we want to continue execution
        dialogue.setCompleteHandler((NodeCompleteResult result) -> {
            System.out.println("NodeCompleteHandler " + result);
            //return Dialogue.HandlerExecutionType.ContinueExecution;

            if (result == null) {
                // When dialogue is complete, check that we expected a stop
                if (testPlan != null) {
                    testPlan.Next();

                    if (testPlan.nextExpectedType != TestPlan.Type.Stop) {
                        fail("Stopped dialogue, but wasn't expecting to select it (was expecting "+testPlan.nextExpectedType.toString()+")");
                    }
                }
            }
        });

//        dialogue.dialogueCompleteHandler = () => {
    }

    // Executes the named node, and checks any assertions made during
    // execution. Fails the test if an assertion made in Yarn fails.
    protected void RunStandardTestcase(String nodeName) {
        if (nodeName == null) nodeName = "Start";

        assertNotNull(testPlan, "Cannot run test: no test plan provided.");

//        dialogue.vm.setNode(nodeName);
        dialogue.start(nodeName);

        do {
            dialogue.update();
        } while (dialogue.isRunning());

    }

    protected String CreateTestNode(String source) {
        return "title: Start\n---\n"+source+"\n===";

    }

    public void LoadTestPlan(Path path) throws IOException {
        this.testPlan = new TestPlan(path);
    }

    String getLastError() {
        return errorLogs.size() > 0 ? errorLogs.get(errorLogs.size() - 1) : null;
    }
}

