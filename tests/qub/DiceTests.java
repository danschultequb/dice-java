package qub;

public interface DiceTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(Dice.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Dice.main(null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("run(DesktopProcess)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Dice.run(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with " + Strings.escapeAndQuote("-?"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("-?")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    Dice.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Usage: qub-dice [--action=]<action-name> [--help]",
                            "  An application that can be used to roll different types of dice.",
                            "  --action(a): The name of the action to invoke.",
                            "  --help(?):   Show the help message for this application.",
                            "",
                            "Actions:",
                            "  configuration:  Open the configuration file for this application.",
                            "  logs:           Show the logs folder.",
                            "  roll (default): Roll dice based on the provided dice expression."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(-1, process.getExitCode());
                });

                runner.test("with " + Strings.escapeAndQuote("5"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("5")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    Dice.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 5"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"5\"",
                            "VERBOSE: Parsed expression: 5",
                            "VERBOSE: After rolls: 5",
                            "Result: 5"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Strings.escapeAndQuote("1d6"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("1d6")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    Dice.run(process);

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

                runner.test("with " + Strings.escapeAndQuote("d10"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("d10")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    Dice.run(process);

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
                            "VERBOSE: Expression text: \"d10\"",
                            "VERBOSE: Parsed expression: d10",
                            "VERBOSE: After rolls: (2)",
                            "Result: 2"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Strings.escapeAndQuote("3d6 + 4"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("3d6 + 4")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    Dice.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 13"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"3d6 + 4\"",
                            "VERBOSE: Parsed expression: 3d6 + 4",
                            "VERBOSE: After rolls: (2 + 3 + 4) + 4",
                            "Result: 13"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + English.andList(Iterable.create("3d6", "+", "4").map(Strings::escapeAndQuote)),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("3d6", "+", "4")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    process.setRandom(DiceTests.createIncrementalRandom());
                    
                    Dice.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Result: 13"),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"3d6 + 4\"",
                            "VERBOSE: Parsed expression: 3d6 + 4",
                            "VERBOSE: After rolls: (2 + 3 + 4) + 4",
                            "Result: 13"),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });

                runner.test("with " + Strings.escapeAndQuote("fireball"),
                    (TestResources resources) -> Tuple.create(resources.createFakeDesktopProcess("fireball")),
                    (Test test, FakeDesktopProcess process) ->
                {
                    Dice.run(process);

                    test.assertLinesEqual(
                        Iterable.create(
                            "Error: Expected constant value or dice roll separator ('d'), but found \"fireball\" instead."),
                        process.getOutputWriteStream());
                    test.assertLinesEqual(
                        Iterable.create(),
                        process.getErrorWriteStream());
                    test.assertEqual(0, process.getExitCode());

                    test.assertLinesEqual(
                        Iterable.create(
                            "VERBOSE: Expression text: \"fireball\"",
                            "Error: Expected constant value or dice roll separator ('d'), but found \"fireball\" instead."),
                        process.getQubProjectDataFolder().await()
                            .getFile("logs/1.log").await()
                            .getContentsAsString().await());
                });
            });
        });
    }

    public static FakeRandom createIncrementalRandom()
    {
        final IntegerValue value = IntegerValue.create(0);
        return FakeRandom.create(value::incrementAndGetAsInt);
    }
}
