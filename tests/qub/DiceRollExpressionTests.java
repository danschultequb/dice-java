package qub;

public interface DiceRollExpressionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceRollExpression.class, () ->
        {
            runner.testGroup("create(int,int)", () ->
            {
                final Action3<Integer,Integer,Throwable> createErrorTest = (Integer diceCount, Integer faceCount, Throwable expected) ->
                {
                    runner.test("with " + English.andList(diceCount, faceCount), (Test test) ->
                    {
                        test.assertThrows(() -> DiceRollExpression.create(diceCount, faceCount),
                            expected);
                    });
                };

                createErrorTest.run(-1, 1, new PreConditionFailure("diceCount (-1) must be greater than or equal to 1."));
                createErrorTest.run(0, 1, new PreConditionFailure("diceCount (0) must be greater than or equal to 1."));
                createErrorTest.run(1, -1, new PreConditionFailure("faceCount (-1) must be greater than or equal to 1."));
                createErrorTest.run(1, 0, new PreConditionFailure("faceCount (0) must be greater than or equal to 1."));

                final Action2<Integer,Integer> createTest = (Integer diceCount, Integer faceCount) ->
                {
                    runner.test("with " + English.andList(diceCount, faceCount), (Test test) ->
                    {
                        final DiceRollExpression expression = DiceRollExpression.create(diceCount, faceCount);
                        test.assertNotNull(expression);
                        test.assertEqual(diceCount, expression.getDiceCount());
                        test.assertEqual(faceCount, expression.getFaceCount());
                        test.assertEqual(
                            JSONObject.create()
                                .setNumber("diceCount", diceCount)
                                .setNumber("faceCount", faceCount),
                            expression.toJson());
                        test.assertEqual(
                            diceCount + "d" + faceCount,
                            expression.toString());
                    });
                };

                createTest.run(1, 1);
                createTest.run(2, 6);
                createTest.run(100, 4);
            });
        });
    }
}
