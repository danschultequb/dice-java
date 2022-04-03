package qub;

public interface DiceRollTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceRoll.class, () ->
        {
            runner.testGroup("addAction(CommandLineActions)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> DiceRoll.addAction(null),
                        new PreConditionFailure("actions cannot be null."));
                });

                runner.test("with non-null",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineActions actions = Dice.createCommandLineActions(process);
                    final CommandLineAction action = DiceRoll.addAction(actions);
                    test.assertNotNull(action);
                    test.assertEqual("roll", action.getName());
                    test.assertEqual("qub-dice [roll]", action.getFullName());
                    test.assertEqual("Roll dice based on the provided dice expression.", action.getDescription());
                    test.assertEqual(Iterable.create(), action.getAliases());
                    test.assertSame(process, action.getProcess());
                });
            });

            runner.testGroup("addExpression(CommandLineParameters)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> DiceRoll.addExpression(null),
                        new PreConditionFailure("parameters cannot be null."));
                });

                runner.test("with non-null", (Test test) ->
                {
                    final CommandLineParameters parameters = CommandLineParameters.create();
                    final CommandLineParameterList<String> expression = DiceRoll.addExpression(parameters);
                    test.assertNotNull(expression);
                    test.assertEqual("expression", expression.getName());
                    test.assertTrue(expression.isRequired());
                    test.assertTrue(expression.isValueRequired());
                    test.assertEqual("<expression-value>", expression.getValueName());
                    test.assertEqual(Iterable.create(), expression.getAliases());
                    test.assertEqual("The expression that describes how many and what kind of dice to roll (plus any constant modifiers).", expression.getDescription());
                });
            });

            runner.testGroup("run(DesktopProcess,CommandLineAction)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    final CommandLineAction action = CommandLineAction.create("fake-action", (DesktopProcess p) -> {});
                    test.assertThrows(() -> DiceRoll.run(null, action),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with null action",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    test.assertThrows(() -> DiceRoll.run(process, null),
                        new PreConditionFailure("action cannot be null."));
                });

                runner.test("with no arguments",
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess()),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Usage: qub-dice [roll] [--expression=]<expression-value> [--help] [--verbose]",
                            "  Roll dice based on the provided dice expression.",
                            "  --expression: The expression that describes how many and what kind of dice to roll (plus any constant modifiers).",
                            "  --help(?):    Show the help message for this application.",
                            "  --verbose(v): Whether or not to show verbose logs."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(-1, process.getExitCode());
                });

                runner.test("with " + Iterable.create(" ").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess(" ")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Usage: qub-dice [roll] [--expression=]<expression-value> [--help] [--verbose]",
                            "  Roll dice based on the provided dice expression.",
                            "  --expression: The expression that describes how many and what kind of dice to roll (plus any constant modifiers).",
                            "  --help(?):    Show the help message for this application.",
                            "  --verbose(v): Whether or not to show verbose logs."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(-1, process.getExitCode());
                });

                runner.test("with " + Iterable.create("1").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("1")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 1"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"1\"",
                            "VERBOSE: Parsed expression: 1",
                            "VERBOSE: After rolls: 1",
                            "Result: 1"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Iterable.create("1d6").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("1d6")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 2"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"1d6\"",
                            "VERBOSE: Parsed expression: 1d6",
                            "VERBOSE: After rolls: (2)",
                            "Result: 2"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Iterable.create("3d20").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("3d20")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 9"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"3d20\"",
                            "VERBOSE: Parsed expression: 3d20",
                            "VERBOSE: After rolls: (2 + 3 + 4)",
                            "Result: 9"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Iterable.create("3d10 + 5").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("3d10 + 5")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 14"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"3d10 + 5\"",
                            "VERBOSE: Parsed expression: 3d10 + 5",
                            "VERBOSE: After rolls: (2 + 3 + 4) + 5",
                            "Result: 14"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Iterable.create("3d10", "+", "5").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("3d10", "+", "5")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 14"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"3d10 + 5\"",
                            "VERBOSE: Parsed expression: 3d10 + 5",
                            "VERBOSE: After rolls: (2 + 3 + 4) + 5",
                            "Result: 14"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Iterable.create("1d8", "+", "1d4", "+", "3").map(Strings::escapeAndQuote),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("1d8", "+", "1d4", "+", "3")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    final CommandLineAction action = DiceRollTests.createCommandLineAction(process);
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    DiceRoll.run(process, action);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 8"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"1d8 + 1d4 + 3\"",
                            "VERBOSE: Parsed expression: 1d8 + 1d4 + 3",
                            "VERBOSE: After rolls: (2) + (3) + 3",
                            "Result: 8"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });
            });
        });
    }

    public static CommandLineAction createCommandLineAction(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineActions actions = Dice.createCommandLineActions(process);
        return DiceRoll.addAction(actions);
    }
}