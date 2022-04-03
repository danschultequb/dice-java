package qub;

public interface Dice
{
    public static void main(String[] args)
    {
        DesktopProcess.run(args, Dice::run);
    }

    public static CommandLineActions createCommandLineActions(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        return process.createCommandLineActions()
            .setApplicationName("qub-dice")
            .setApplicationDescription("An application that can be used to roll different types of dice.");
    }

    public static void run(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        Dice.createCommandLineActions(process)
            .addAction(DiceRoll::addAction)
            .addAction(CommandLineLogsAction::addAction)
            .addAction(CommandLineConfigurationAction::addAction)
            .run();
    }
}