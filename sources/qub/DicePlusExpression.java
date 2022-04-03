package qub;

public class DicePlusExpression extends DiceExpressionBase
{
    private final DiceExpression left;
    private final DiceExpression right;

    private DicePlusExpression(DiceExpression left, DiceExpression right)
    {
        PreCondition.assertNotNull(left, "left");
        PreCondition.assertNotNull(right, "right");

        this.left = left;
        this.right = right;
    }

    public static DicePlusExpression create(DiceExpression left, DiceExpression right)
    {
        return new DicePlusExpression(left, right);
    }

    public DiceExpression getLeft()
    {
        return this.left;
    }

    public DiceExpression getRight()
    {
        return this.right;
    }

    @Override
    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString("operator", "+")
            .set("left", this.getLeft().toJson())
            .set("right", this.getRight().toJson());
    }

    @Override
    public void acceptVisitor(DiceExpressionVisitor visitor)
    {
        PreCondition.assertNotNull(visitor, "visitor");

        visitor.visitPlusExpression(this);
    }
}
