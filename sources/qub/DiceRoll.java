package qub;

public interface DiceRoll
{
    public static CommandLineAction addAction(CommandLineActions actions)
    {
        PreCondition.assertNotNull(actions, "actions");

        return actions.addAction("roll", DiceRoll::run)
            .setDescription("Roll dice based on the provided dice expression.")
            .setDefaultAction();
    }

    public static CommandLineParameterList<String> addExpression(CommandLineParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        return parameters.addPositionStringList("expression")
            .setDescription("The expression that describes how many and what kind of dice to roll (plus any constant modifiers).")
            .setRequired(true);
    }

    public static void run(DesktopProcess process, CommandLineAction action)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(action, "action");

        final CommandLineParameters parameters = action.createCommandLineParameters();
        final CommandLineParameterHelp helpParameter = parameters.addHelp();
        final CommandLineParameterList<String> expressionParameters = DiceRoll.addExpression(parameters);
        final CommandLineParameterVerbose verboseParameter = parameters.addVerbose(process);

        final Iterable<String> expressionStrings = expressionParameters.getValues().await()
            .map((String expression) -> expression.trim())
            .where(Functions.not(Strings::isNullOrEmpty))
            .toList();
        helpParameter.setForceShowApplicationHelpLines(!expressionStrings.any());

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            try (final LogStreams streams = CommandLineLogsAction.getLogStreamsFromDesktopProcess(process, verboseParameter.getVerboseCharacterToByteWriteStream().await()))
            {
                final CharacterWriteStream output = streams.getOutput();
                final VerboseCharacterToByteWriteStream verbose = streams.getVerbose();

                final String expressionText = Strings.join(' ', expressionStrings);
                verbose.writeLine("Expression text: " + Strings.escapeAndQuote(expressionText)).await();

                final DiceExpression parsedExpression = DiceExpression.parse(expressionText)
                    .onValue((DiceExpression e) -> verbose.writeLine("Parsed expression: " + e.toString()).await())
                    .catchError((Throwable e) -> output.writeLine("Error: " + e.getMessage()).await())
                    .await();
                if (parsedExpression != null)
                {
                    final Random random = process.getRandom();
    
                    final DiceExpression appliedRollsExpression = parsedExpression.applyRolls(random);
                    verbose.writeLine("After rolls: " + appliedRollsExpression.toString()).await();
                    output.writeLine("Result: " + Integers.toString(appliedRollsExpression.evaluate(random))).await();
                }
            }
        }
    }
}
