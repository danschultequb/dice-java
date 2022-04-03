package qub;

public class DiceConstantExpression extends DiceExpressionBase
{
    private final int constant;

    private DiceConstantExpression(int constant)
    {
        this.constant = constant;
    }

    public static DiceConstantExpression create(int constant)
    {
        return new DiceConstantExpression(constant);
    }

    public int getConstant()
    {
        return this.constant;
    }

    @Override
    public JSONObject toJson()
    {
        return JSONObject.create()
            .setNumber("constant", this.getConstant());
    }

    @Override
    public void acceptVisitor(DiceExpressionVisitor visitor)
    {
        PreCondition.assertNotNull(visitor, "visitor");

        visitor.visitConstantExpression(this);
    }
}
