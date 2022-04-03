package qub;

public class DiceExpressionEvaluateVisitor implements DiceExpressionVisitor
{
    private final Random random;
    private Integer replacementValue;

    private DiceExpressionEvaluateVisitor(Random random)
    {
        PreCondition.assertNotNull(random, "random");

        this.random = random;
    }

    public static DiceExpressionEvaluateVisitor create(Random random)
    {
        return new DiceExpressionEvaluateVisitor(random);
    }

    private void setValue(DiceExpression expression, int value)
    {
        PreCondition.assertNotNull(expression, "previousExpression");

        this.replacementValue = value;
    }

    public int takeValue(DiceExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");
        PreCondition.assertNotNull(this.replacementValue, "this.replacementValue");

        final int result = this.replacementValue;
        this.replacementValue = null;

        return result;
    }

    @Override
    public void visitConstantExpression(DiceConstantExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        this.setValue(expression, expression.getConstant());
    }

    @Override
    public void visitPlusExpression(DicePlusExpression expression)
    {
        final DiceExpression left = expression.getLeft();
        left.acceptVisitor(this);
        final int leftValue = this.takeValue(left);

        final DiceExpression right = expression.getRight();
        right.acceptVisitor(this);
        final int rightValue = this.takeValue(right);

        this.setValue(expression, leftValue + rightValue);
    }

    @Override
    public void visitRollExpression(DiceRollExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");
        
        final int faceCount = expression.getFaceCount();
        final int diceCount = expression.getDiceCount();
        
        int value = 0;
        for (int i = 0; i < diceCount; i++)
        {
            value += this.random.getRandomIntegerBetween(1, faceCount);
        }
        this.setValue(expression, value);
    }

    @Override
    public void visitParenthesesExpression(DiceParenthesesExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        final DiceExpression inner = expression.getInner();
        inner.acceptVisitor(this);
        this.setValue(expression, this.takeValue(inner));
    }
}
