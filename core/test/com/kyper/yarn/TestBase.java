package com.kyper.yarn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyper.yarn.Dialogue.Command;
import com.kyper.yarn.Dialogue.Line;
import com.kyper.yarn.Dialogue.MemoryVariableStorage;
import com.kyper.yarn.Dialogue.Option;
import com.kyper.yarn.Dialogue.OptionSet;
import com.kyper.yarn.Dialogue.VariableStorage;
import com.kyper.yarn.Dialogue.YarnLogger;
import com.kyper.yarn.VirtualMachine.HandlerExecutionType;
import com.kyper.yarn.compiler.YarnCompiler.StringInfo;

public class TestBase {
    protected VariableStorage storage = new MemoryVariableStorage();
    protected Dialogue dialogue;
    protected Map<String, StringInfo> stringTable = new HashMap<String, StringInfo>();
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

    public String GetComposedTextForLine(Line line) {
        // TODO stringTable localization handling?
//        var baseText = stringTable[line.ID].text;
        String baseText = stringTable.get(line.id).text;
        for (int i = 0; i < line.substitutions.length; i++) {
            String substitution = line.substitutions[i];
            baseText = baseText.replace("{" + i + "}", substitution);
        }
        
        return Dialogue.expandFormatFunctions(baseText, locale);
    }
//    public String GetComposedTextForLine(String line) {
//        return line;
//        // TODO substitution handling?
////        for (int i = 0; i < line.Substitutions.Length; i++) {
////            String substitution = line.Substitutions[i];
////            baseText = baseText.Replace("{" + i + "}", substitution);
////        }
//
////        return Dialogue.ExpandFormatFunctions(baseText, locale);
//    }

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
        dialogue.setLineHandler((Line line) -> {
//            var id = line.ID;
//            assertTrue(stringTable.containsKey(id));

            String text = GetComposedTextForLine(line);

            System.out.println("Line: " + text);

            if (testPlan != null) {
                testPlan.Next();

                if (testPlan.nextExpectedType == TestPlan.Type.line) {
                    assertEquals(testPlan.nextExpectedValue, text);
                } else {
                    fail("Received line "+text+", but was expecting a "+testPlan.nextExpectedType.toString());
                }
            }

            return HandlerExecutionType.ContinueExecution;
        });

        dialogue.setOptionsHandler((OptionSet optionSet) -> {
            int optionCount = optionSet.getOptions().length;

            System.out.println("Options:");
            for(Option option : optionSet.getOptions()) {
                // TODO localization handling?
//                var optionText = GetComposedTextForLine(option.Line);
                String optionText = GetComposedTextForLine(option.getLine());
                System.out.println(" - " + optionText);
            }

            if (testPlan != null) {
                testPlan.Next();

                if (testPlan.nextExpectedType != TestPlan.Type.select) {
                    fail("Received "+optionCount+" options, but wasn't expecting them (was expecting "+testPlan.nextExpectedType.toString()+")");
                }

                // Assert that the list of options we were given is
                // identical to the list of options we expect
                
                List<String> actualOptionList = Arrays.stream(optionSet.getOptions()).map(o->this.GetComposedTextForLine(o.getLine())).collect(Collectors.toList());
                if(!testPlan.nextExpectedOptions.equals(actualOptionList)) {
                    System.out.println(testPlan.nextExpectedOptions);
                }
                assertEquals(testPlan.nextExpectedOptions, actualOptionList);

                int expectedOptionCount = testPlan.nextExpectedOptions.size();

                assertEquals(expectedOptionCount, optionCount);

                if (testPlan.nextOptionToSelect != -1) {
                    dialogue.setSelectedOption(testPlan.nextOptionToSelect - 1);
                } else {
                    dialogue.setSelectedOption(0);
                }
            }
        });

        dialogue.setCommandHandler((Command command) -> {
            System.out.println("Command: " + command.getCommand());

            if (testPlan != null) {
                testPlan.Next();
                if (testPlan.nextExpectedType != TestPlan.Type.command)
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

            return HandlerExecutionType.ContinueExecution;
        });

        dialogue.getLibrary().registerFunction("assert", 1, (Value[] parameters) -> {
            if (parameters[0].asBool() == false) {
                fail("Assertion failed");
            }
        });


        // When a node is complete, just indicate that we want to continue execution
        dialogue.setDialogueCompleteHandler(() -> {
            //

                // When dialogue is complete, check that we expected a stop
                if (testPlan != null) {
                    testPlan.Next();

                    if (testPlan.nextExpectedType != TestPlan.Type.stop) {
                        fail("Stopped dialogue, but wasn't expecting to select it (was expecting "+testPlan.nextExpectedType.toString()+")");
                    }
                }
        });
        dialogue.setNodeStartHandler((String name)-> {return HandlerExecutionType.ContinueExecution;});
        dialogue.setNodeCompleteHandler((String name)-> {return HandlerExecutionType.ContinueExecution;});
    }

    // Executes the named node, and checks any assertions made during
    // execution. Fails the test if an assertion made in Yarn fails.
    protected void runStandardTestcase(String nodeName) {
        if (nodeName == null) nodeName = "Start";

        assertNotNull(testPlan, "Cannot run test: no test plan provided.");

//        dialogue.vm.setNode(nodeName);
        dialogue.setNode(nodeName);

        do {
            dialogue.continueRunning();
        } while (dialogue.isActive());

    }

    protected String CreateTestNode(String source) {
        return "title: Start\n---\n"+source+"\n===";

    }

    public void loadTestPlan(Path path) throws IOException {
        this.testPlan = new TestPlan(path);
    }

    String getLastError() {
        return errorLogs.size() > 0 ? errorLogs.get(errorLogs.size() - 1) : null;
    }
}

