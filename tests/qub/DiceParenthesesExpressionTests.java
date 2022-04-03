package qub;

public interface DiceParenthesesExpressionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceParenthesesExpression.class, () ->
        {
            runner.testGroup("create(DiceExpression)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> DiceParenthesesExpression.create(null),
                        new PreConditionFailure("innerExpression cannot be null."));
                });

                runner.test("with non-null", (Test test) ->
                {
                    final DiceConstantExpression innerExpression = DiceConstantExpression.create(1);
                    final DiceParenthesesExpression expression = DiceParenthesesExpression.create(innerExpression);
                    test.assertNotNull(expression);
                    test.assertSame(innerExpression, expression.getInner());
                    test.assertEqual(
                        JSONObject.create()
                            .setString("operator", "()")
                            .setObject("inner", innerExpression.toJson()),
                        expression.toJson());
                    test.assertEqual("(1)", expression.toString());
                });
            });
        });
    }
}
