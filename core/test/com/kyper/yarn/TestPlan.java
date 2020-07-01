package com.kyper.yarn.tests.yarn;

import com.kyper.yarn.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlan
{
    public enum Type
    {
        // expecting to see this specific line
        Line,

        // expecting to see this specific option (if '*' is given,
        // means 'see an option, don't care about text')
        Option,

        // expecting options to have been presented; value = the
        // index to select
        Select,

        // expecting to see this specific command
        Command,

        // expecting to stop the test here (this is optional - a
        // 'stop' at the end of a test plan is assumed)
        Stop
    }

    public class Step
    {
        public Type type;

        public String stringValue;
        public int intValue;

        public Step(String s) {
            intValue = -1;
            stringValue = null;

            Reader reader = new Reader(s);

            try {
                type = reader.ReadNext(Type::valueOf);

                char delimiter = (char)reader.read();
                if (delimiter != ':') {
                    throw new IllegalArgumentException("Expected ':' after step type");
                }

                switch (type) {
                    // for lines, options and commands: we expect to
                    // see the rest of this line
                    case Line:
                    case Option:
                    case Command:
                        stringValue = reader.readToEnd().trim();
                        if (stringValue == "*") {
                            // '*' represents "we want to see an option
                            // but don't care what its text is" -
                            // represent this as the null value
                            stringValue = null;
                        }
                        break;

                    case Select:
                        intValue = reader.ReadNext(Integer::parseInt);

                        if (intValue < 1) {
                            throw new IllegalArgumentException("Cannot select option "+intValue+" - must be >= 1");
                        }

                        break;
                }
            } catch (Exception e) {
                // there was a syntax or semantic error
                throw new IllegalArgumentException("Failed to parse step line: '"+s+"' (reason: "+e.getMessage()+")");
            }



        }

        private class Reader extends StringReader {
            // hat tip to user Dennis from Stackoverflow:
            // https://stackoverflow.com/a/26669930/2153213
            public Reader(String s) {
                super(s);
            }

            // Parse the next T from this String, ignoring leading
            // whitespace
            public <T> T ReadNext(Function<String,T> converter) throws IOException//where T : System.IConvertible
            {
                StringBuilder sb = new StringBuilder();

                do
                {
                    int current = read();
                    if (current < 0)
                        break;

                    // eat leading whitespace
                    if (Character.isWhitespace((char)current))
                        continue;

                    sb.append((char)current);

                    mark(Integer.MAX_VALUE);
                    char next = (char)read();
                    reset();
                    if (Character.isLetterOrDigit(next) == false)
                        break;

                } while (true);

                String value = sb.toString();

                return converter.apply(value);

//                Type type = T.class;
//                if (type.IsEnum)
//                    return (T)Enum.Parse(type, value, true);
//
//                return (T)((IConvertible)value).ToType(
//                        typeof(T),
//                        System.Globalization.CultureInfo.InvariantCulture
//                );
            }


            public String readToEnd() throws IOException {
                StringBuilder result = new StringBuilder();
                char next = 0;
                while (next != -1) {
                    next = (char)read();
                    result.append(next);
                }
                return result.toString();
            }
        }
    }

    private List<Step> steps;

    private int currentTestPlanStep = 0;

    public TestPlan.Type nextExpectedType;
    public ArrayList<String> nextExpectedOptions = new ArrayList<String>();
    public int nextOptionToSelect = -1;
    public String nextExpectedValue = null;

    public TestPlan(Path path) throws IOException
    {
//        steps = File.ReadAllLines(path)
//                .Where(line => line.TrimStart().StartsWith("#") == false) // skip commented lines
//                .Where(line => line.Trim() != "") // skip empty or blank lines
//                .Select(line => new Step(line)) // convert remaining lines to steps
//                .ToList();
        steps = Files.lines(path)
                .filter(line -> StringUtils.trimStart(line).startsWith("#") == false)
                .filter(line -> line.trim() != "")
                .map(Step::new)
                .collect(Collectors.toList());
    }

    public void Next() {
        // step through the test plan until we hit an expectation to
        // see a line, option, or command. specifically, we're waiting
        // to see if we got a Line, Select, Command or Assert step
        // type.

        if (nextExpectedType == Type.Select) {
            // our previously-notified task was to select an option.
            // we've now moved past that, so clear the list of expected
            // options.
            nextExpectedOptions.clear();
            nextOptionToSelect = 0;
        }

        while (currentTestPlanStep < steps.size()) {

            Step currentStep = steps.get(currentTestPlanStep);

            currentTestPlanStep += 1;

            switch (currentStep.type) {
                case Line:
                case Command:

                case Stop:
                    nextExpectedType = currentStep.type;
                    nextExpectedValue = currentStep.stringValue;
                    return;
                case Select:
                    nextExpectedType = currentStep.type;
                    nextOptionToSelect = currentStep.intValue;
                    return;
                case Option:
                    nextExpectedOptions.add(currentStep.stringValue);
                    continue;
            }
        }

        // We've fallen off the end of the test plan step list. We
        // expect a stop here.
        nextExpectedType = Type.Stop;

        return;
    }


}
