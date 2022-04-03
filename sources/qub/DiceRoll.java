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

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            try (final LogStreams logStreams = CommandLineLogsAction.getLogStreamsFromDesktopProcess(process, verboseParameter.getVerboseCharacterToByteWriteStream().await()))
            {
                final CharacterWriteStream output = logStreams.getOutput();
                final VerboseCharacterToByteWriteStream verbose = logStreams.getVerbose();
                final DiceConfiguration configuration = DiceConfiguration.parse(process).await();

                final Iterable<String> expressionStrings = expressionParameters.getValues().await()
                    .map((String expression) -> expression.trim())
                    .where(Functions.not(Strings::isNullOrEmpty))
                    .toList();
                String expressionText = Strings.join(' ', expressionStrings);

                final Action1<String> processExpressionText = (String expression) ->
                {
                    PreCondition.assertNotNullAndNotEmpty(expression, "expression");

                    configuration.writeExpressionTextTo("Expression text: " + Strings.escapeAndQuote(expression), output, verbose).await();

                    final DiceExpression parsedExpression = DiceExpression.parse(expression)
                        .onValue((DiceExpression e) -> configuration.writeParsedExpressionTo("Parsed expression: " + e.toString(), output, verbose).await())
                        .catchError((Throwable e) -> output.writeLine("Error: " + e.getMessage()).await())
                        .await();
                    if (parsedExpression != null)
                    {
                        final Random random = process.getRandom();
        
                        final DiceExpression appliedRollsExpression = parsedExpression.applyRolls(random);
                        configuration.writeAfterRollsTextTo("After rolls: " + appliedRollsExpression.toString(), output, verbose).await();
                        output.writeLine("Result: " + Integers.toString(appliedRollsExpression.evaluate(random))).await();
                    }
                };
                
                if (!Strings.isNullOrEmpty(expressionText))
                {
                    processExpressionText.run(expressionText);
                }
                else
                {
                    final CharacterReadStream input = process.getInputReadStream();
                    final CharacterWriteStream logStream = logStreams.getLogStream();
                    final BooleanValue done = BooleanValue.create(false);
                    while (!done.getAsBoolean())
                    {
                        output.write("> ").await();
                        expressionText = input.readLine()
                            .then((String line) ->
                            {
                                logStream.writeLine(line).await();
                                
                                return line.trim();
                            })
                            .catchError((Throwable e) ->
                            {
                                output.write("Error: ").await();
                                if (Strings.isNullOrEmpty(e.getMessage()))
                                {
                                    output.write(Types.getFullTypeName(e)).await();
                                }
                                else
                                {
                                    output.write(e.getMessage()).await();
                                }
                                output.writeLine().await();

                                done.set(true);
                            })
                            .await();

                        if (!Strings.isNullOrEmpty(expressionText))
                        {
                            if (Iterable.create("quit", "exit", "done").contains(expressionText.toLowerCase()))
                            {
                                done.set(true);
                            }
                            else
                            {
                                processExpressionText.run(expressionText);

                                output.writeLine().await();
                            }
                        }
                    }
                }
            }
        }
    }
}
