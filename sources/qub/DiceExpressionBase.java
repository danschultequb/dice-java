package qub;

public abstract class DiceExpressionBase implements DiceExpression
{
    @Override
    public boolean equals(Object rhs)
    {
        return DiceExpression.equals(this, rhs);
    }

    @Override
    public String toString()
    {
        return DiceExpression.toString(this);
    }
}
