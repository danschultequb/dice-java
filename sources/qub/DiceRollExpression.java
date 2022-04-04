package qub;

public class DiceRollExpression extends DiceExpressionBase
{
    private final Integer diceCount;
    private final int faceCount;

    private DiceRollExpression(Integer diceCount, int faceCount)
    {
        PreCondition.assertNullOrGreaterThanOrEqualTo(diceCount, 1, "diceCount");
        PreCondition.assertGreaterThanOrEqualTo(faceCount, 1, "faceCount");

        this.diceCount = diceCount;
        this.faceCount = faceCount;
    }

    public static DiceRollExpression create(int faceCount)
    {
        return DiceRollExpression.create(null, faceCount);
    }

    public static DiceRollExpression create(Integer diceCount, int faceCount)
    {
        return new DiceRollExpression(diceCount, faceCount);
    }

    public Integer getDiceCount()
    {
        return this.diceCount;
    }

    public int getFaceCount()
    {
        return this.faceCount;
    }

    @Override
    public JSONObject toJson()
    {
        return JSONObject.create()
            .setNumberOrNull("diceCount", this.getDiceCount())
            .setNumber("faceCount", this.getFaceCount());
    }

    @Override
    public void acceptVisitor(DiceExpressionVisitor visitor)
    {
        PreCondition.assertNotNull(visitor, "visitor");

        visitor.visitRollExpression(this);
    }
}