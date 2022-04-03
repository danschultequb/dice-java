package qub;

public interface DicePlusExpressionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DicePlusExpression.class, () ->
        {
            runner.testGroup("create(DiceExpression,DiceExpression)", () ->
            {
                final Action3<DiceExpression,DiceExpression,Throwable> createErrorTest = (DiceExpression left, DiceExpression right, Throwable expected) ->
                {
                    runner.test("with " + English.andList(left, right), (Test test) ->
                    {
                        test.assertThrows(() -> DicePlusExpression.create(left, right),
                            expected);
                    });
                };

                createErrorTest.run(null, DiceConstantExpression.create(2), new PreConditionFailure("left cannot be null."));
                createErrorTest.run(DiceConstantExpression.create(1), null, new PreConditionFailure("right cannot be null."));

                final Action2<DiceExpression,DiceExpression> createTest = (DiceExpression left, DiceExpression right) ->
                {
                    runner.test("with " + English.andList(left, right), (Test test) ->
                    {
                        final DicePlusExpression expression = DicePlusExpression.create(left, right);
                        test.assertNotNull(expression);
                        test.assertSame(left, expression.getLeft());
                        test.assertSame(right, expression.getRight());
                        test.assertEqual(
                            JSONObject.create()
                                .setString("operator", "+")
                                .setObject("left", left.toJson())
                                .setObject("right", right.toJson()),
                            expression.toJson());
                    });
                };

                createTest.run(DiceExpression.constant(1), DiceExpression.constant(2));
            });
        });
    }
}
