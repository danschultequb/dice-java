package qub;

public class DiceConfiguration extends JSONObjectWrapperBase
{
    private static final String configurationFileName = "configuration.json";

    private static final String outputVerbosityPropertyName = "outputVerbosity";
    private static final String expressionTextPropertyName = "expressionText";
    private static final String parsedExpressionPropertyName = "parsedExpression";
    private static final String afterRollsPropertyName = "afterRolls";
    private static final String outputPropertyValue = "output";
    private static final String verbosePropertyValue = "verbose";
    private static final String hiddenPropertyValue = "hidden";
    
    private DiceConfiguration(JSONObject json)
    {
        super(json);
    }
    
    public static DiceConfiguration create()
    {
        return DiceConfiguration.create(JSONObject.create());
    }

    public static DiceConfiguration create(JSONObject json)
    {
        return new DiceConfiguration(json);
    }

    private static File getConfigurationFile(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        return process.getQubProjectDataFolder().await()
            .getFile(DiceConfiguration.configurationFileName).await();
    }

    public static Result<Void> setConfigurationFile(DesktopProcess process, DiceConfiguration configuration)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(configuration, "configuration");

        return Result.create(() ->
        {
            final File configurationFile = DiceConfiguration.getConfigurationFile(process);
            configurationFile.setContentsAsString(configuration.toString(JSONFormat.pretty)).await();
        });
    }

    public static Result<DiceConfiguration> parse(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        return Result.create(() ->
        {
            final File configurationFile = DiceConfiguration.getConfigurationFile(process);
            return DiceConfiguration.parse(configurationFile).await();
        });
    }

    public static Result<DiceConfiguration> parse(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return Result.create(() ->
        {
            final JSONObject json = JSON.parseObject(file)
                .catchError(() -> { return JSONObject.create(); })
                .await();
            return DiceConfiguration.create(json);
        });
    }

    private DiceConfigurationOutputVerbosity getOutputVerbosity(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        DiceConfigurationOutputVerbosity result = DiceConfigurationOutputVerbosity.Verbose;

        final JSONObject outputVerbosityJson = this.toJson().getObject(DiceConfiguration.outputVerbosityPropertyName).catchError().await();
        if (outputVerbosityJson != null)
        {
            final String expressionTextOutputVerbosityString = outputVerbosityJson.getString(propertyName).catchError().await();
            if (expressionTextOutputVerbosityString != null)
            {
                final DiceConfigurationOutputVerbosity parsedOutputVerbosity = Enums.parse(DiceConfigurationOutputVerbosity.class, expressionTextOutputVerbosityString).catchError().await();
                if (parsedOutputVerbosity != null)
                {
                    result = parsedOutputVerbosity;
                }
            }
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    private DiceConfiguration setOutputVerbosity(String propertyName, DiceConfigurationOutputVerbosity outputVerbosity)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertNotNull(outputVerbosity, "outputVerbosity");

        final JSONObject outputVerbosityJson = this.toJson().getOrCreateObject(DiceConfiguration.outputVerbosityPropertyName).await();
        outputVerbosityJson.setString(propertyName, outputVerbosity.toString());

        return this;
    }

    private Result<Integer> writeTextTo(String text, String textName, CharacterWriteStream output, VerboseCharacterToByteWriteStream verbose)
    {
        return Result.create(() ->
        {
            int result;

            switch (this.getOutputVerbosity(textName))
            {
                case Output:
                    result = output.writeLine(text).await();
                    break;

                case Verbose:
                    result = verbose.writeLine(text).await();
                    break;

                default:
                    result = 0;
                    break;
            }

            return result;
        });
    }

    public DiceConfigurationOutputVerbosity getExpressionTextOutputVerbosity()
    {
        return this.getOutputVerbosity(DiceConfiguration.expressionTextPropertyName);
    }

    public DiceConfiguration setExpressionTextOutputVerbosity(DiceConfigurationOutputVerbosity outputVerbosity)
    {
        PreCondition.assertNotNull(outputVerbosity, "outputVerbosity");

        return this.setOutputVerbosity(DiceConfiguration.expressionTextPropertyName, outputVerbosity);
    }

    public Result<Integer> writeExpressionTextTo(String expressionText, CharacterWriteStream output, VerboseCharacterToByteWriteStream verbose)
    {
        return this.writeTextTo(expressionText, DiceConfiguration.expressionTextPropertyName, output, verbose);
    }

    public DiceConfigurationOutputVerbosity getParsedExpressionOutputVerbosity()
    {
        return this.getOutputVerbosity(DiceConfiguration.parsedExpressionPropertyName);
    }

    public DiceConfiguration setParsedExpressionOutputVerbosity(DiceConfigurationOutputVerbosity outputVerbosity)
    {
        PreCondition.assertNotNull(outputVerbosity, "outputVerbosity");

        return this.setOutputVerbosity(DiceConfiguration.parsedExpressionPropertyName, outputVerbosity);
    }

    public Result<Integer> writeParsedExpressionTo(String parsedExpressionText, CharacterWriteStream output, VerboseCharacterToByteWriteStream verbose)
    {
        return this.writeTextTo(parsedExpressionText, DiceConfiguration.parsedExpressionPropertyName, output, verbose);
    }

    public DiceConfigurationOutputVerbosity getAfterRollsOutputVerbosity()
    {
        return this.getOutputVerbosity(DiceConfiguration.afterRollsPropertyName);
    }

    public DiceConfiguration setAfterRollsOutputVerbosity(DiceConfigurationOutputVerbosity outputVerbosity)
    {
        PreCondition.assertNotNull(outputVerbosity, "outputVerbosity");

        return this.setOutputVerbosity(DiceConfiguration.afterRollsPropertyName, outputVerbosity);
    }

    public Result<Integer> writeAfterRollsTextTo(String afterRollsText, CharacterWriteStream output, VerboseCharacterToByteWriteStream verbose)
    {
        return this.writeTextTo(afterRollsText, DiceConfiguration.afterRollsPropertyName, output, verbose);
    }

    public static CommandLineAction addAction(CommandLineActions actions)
    {
        PreCondition.assertNotNull(actions, "actions");

        final String configurationSchemaJsonFilePath = "./configuration.schema.json";

        return CommandLineConfigurationAction.addAction(actions, CommandLineConfigurationActionParameters.create()
            .setConfigurationFileRelativePath(DiceConfiguration.configurationFileName)
            .setConfigurationSchemaFileRelativePath(configurationSchemaJsonFilePath)
            .setDefaultConfiguration(JSONObject.create()
                .setString("$schema", configurationSchemaJsonFilePath))
            .setConfigurationSchema(JSONSchema.create()
                .setSchema("http://json-schema.org/draft-04/schema")
                .setType(JSONSchemaType.Object)
                .addProperty(JSONSchema.schemaPropertyName, JSONSchema.create()
                    .setDescription("The schema that defines how a dice configuration file should be structured.")
                    .setEnum(configurationSchemaJsonFilePath)
                )
                .addProperty(DiceConfiguration.outputVerbosityPropertyName, JSONSchema.create()
                    .setDescription("Where specific lines of text will be output to.")
                    .setType(JSONSchemaType.Object)
                    .addProperty(DiceConfiguration.expressionTextPropertyName, JSONSchema.create().setRef("#/definitions/outputVerbosityValue"))
                    .addProperty(DiceConfiguration.parsedExpressionPropertyName, JSONSchema.create().setRef("#/definitions/outputVerbosityValue"))
                    .addProperty(DiceConfiguration.afterRollsPropertyName, JSONSchema.create().setRef("#/definitions/outputVerbosityValue"))
                    .setAdditionalProperties(false)
                )
                .setRequired("$schema")
                .setAdditionalProperties(false)
                .addDefinition("outputVerbosityValue", JSONSchema.create()
                    .setOneOf(
                        JSONSchema.create()
                            .setEnum(DiceConfiguration.outputPropertyValue)
                            .setDescription("This text will be output to the application's standard output stream."),
                        JSONSchema.create()
                            .setEnum(DiceConfiguration.verbosePropertyValue)
                            .setDescription("This text will be output to the application's verbose output stream."),
                        JSONSchema.create()
                            .setEnum(DiceConfiguration.hiddenPropertyValue)
                            .setDescription("This text will not be output to any of the application's output streams.")
                    )
                )));
    }
}
