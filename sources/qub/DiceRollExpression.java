package qub;

public class DiceRollExpression extends DiceExpressionBase
{
    private final int diceCount;
    private final int faceCount;

    private DiceRollExpression(int diceCount, int faceCount)
    {
        PreCondition.assertGreaterThanOrEqualTo(diceCount, 1, "diceCount");
        PreCondition.assertGreaterThanOrEqualTo(faceCount, 1, "faceCount");

        this.diceCount = diceCount;
        this.faceCount = faceCount;
    }

    public static DiceRollExpression create(int diceCount, int faceCount)
    {
        return new DiceRollExpression(diceCount, faceCount);
    }

    public int getDiceCount()
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
            .setNumber("diceCount", this.getDiceCount())
            .setNumber("faceCount", this.getFaceCount());
    }

    @Override
    public void acceptVisitor(DiceExpressionVisitor visitor)
    {
        PreCondition.assertNotNull(visitor, "visitor");

        visitor.visitRollExpression(this);
    }
}