package qub;

public class DiceParenthesesExpression extends DiceExpressionBase
{
    private final DiceExpression innerExpression;

    private DiceParenthesesExpression(DiceExpression innerExpression)
    {
        PreCondition.assertNotNull(innerExpression, "innerExpression");

        this.innerExpression = innerExpression;
    }

    public static DiceParenthesesExpression create(DiceExpression innerExpression)
    {
        return new DiceParenthesesExpression(innerExpression);
    }

    public DiceExpression getInner()
    {
        return this.innerExpression;
    }

    @Override
    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString("operator", "()")
            .setObject("inner", this.innerExpression.toJson());
    }

    @Override
    public void acceptVisitor(DiceExpressionVisitor visitor)
    {
        PreCondition.assertNotNull(visitor, "visitor");

        visitor.visitParenthesesExpression(this);
    }
}
