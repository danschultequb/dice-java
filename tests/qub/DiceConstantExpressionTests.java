package qub;

public interface DiceConstantExpressionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceConstantExpression.class, () ->
        {
            runner.testGroup("create(int)", () ->
            {
                final Action1<Integer> createTest = (Integer constant) ->
                {
                    runner.test("with " + constant, (Test test) ->
                    {
                        final DiceConstantExpression expression = DiceConstantExpression.create(constant);
                        test.assertNotNull(expression, "expression");
                        test.assertEqual(constant, expression.getConstant());
                        test.assertEqual(
                            JSONObject.create()
                                .setNumber("constant", constant),
                            expression.toJson());
                        test.assertEqual(Integers.toString(constant), expression.toString());
                    });
                };

                createTest.run(-1);
                createTest.run(0);
                createTest.run(1);
                createTest.run(4);
            });

            runner.testGroup("applyRolls(Random)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final DiceConstantExpression expression = DiceConstantExpression.create(1);
                    test.assertThrows(() -> expression.applyRolls(null),
                        new PreConditionFailure("random cannot be null."));
                });

                runner.test("with non-null", (Test test) ->
                {
                    final Random random = FakeRandom.create(5);
                    final DiceConstantExpression expression = DiceConstantExpression.create(1);
                    final DiceExpression applyRollsResult = expression.applyRolls(random);
                    test.assertSame(expression, applyRollsResult);
                });
            });
        });
    }
}
