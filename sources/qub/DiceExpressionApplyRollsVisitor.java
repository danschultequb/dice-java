package qub;

public class DiceExpressionApplyRollsVisitor implements DiceExpressionVisitor
{
    private final Random random;
    private DiceExpression replacementExpression;

    private DiceExpressionApplyRollsVisitor(Random random)
    {
        PreCondition.assertNotNull(random, "random");

        this.random = random;
    }

    public static DiceExpressionApplyRollsVisitor create(Random random)
    {
        return new DiceExpressionApplyRollsVisitor(random);
    }

    private void setReplacement(DiceExpression previousExpression, DiceExpression newExpression)
    {
        PreCondition.assertNotNull(previousExpression, "previousExpression");
        PreCondition.assertNotNull(newExpression, "newExpression");

        this.replacementExpression = newExpression;

        PostCondition.assertSame(this.replacementExpression, newExpression, "this.replacementExpression");
    }

    public DiceExpression takeReplacement(DiceExpression toReplace)
    {
        PreCondition.assertNotNull(toReplace, "toReplace");

        final DiceExpression result;
        if (this.replacementExpression != null)
        {
            result = this.replacementExpression;
            this.replacementExpression = null;
        }
        else
        {
            result = toReplace;
        }

        PostCondition.assertNotNull(result, "result");
        PostCondition.assertNull(this.replacementExpression, "this.replacementExpression");

        return result;
    }

    @Override
    public void visitConstantExpression(DiceConstantExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        // Nothing to do.
    }

    @Override
    public void visitPlusExpression(DicePlusExpression expression)
    {
        final DiceExpression left = expression.getLeft();
        left.acceptVisitor(this);
        final DiceExpression newLeft = this.takeReplacement(left);

        final DiceExpression right = expression.getRight();
        right.acceptVisitor(this);
        final DiceExpression newRight = this.takeReplacement(right);

        if (!Comparer.same(left, newLeft) || !Comparer.same(right, newRight))
        {
            this.setReplacement(expression, DicePlusExpression.create(newLeft, newRight));
        }
    }

    @Override
    public void visitRollExpression(DiceRollExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");
        
        final int faceCount = expression.getFaceCount();
        final Function0<DiceConstantExpression> createRandomConstantExpression = () ->
        {
            return DiceExpression.constant(this.random.getRandomIntegerBetween(1, faceCount));
        };

        DiceExpression newExpression = createRandomConstantExpression.run();

        final Integer diceCount = expression.getDiceCount();
        if (diceCount != null)
        {
            for (int i = 1; i < diceCount.intValue(); i++)
            {
                newExpression = DiceExpression.plus(newExpression, createRandomConstantExpression.run());
            }
        }

        this.setReplacement(expression, DiceParenthesesExpression.create(newExpression));
    }

    @Override
    public void visitParenthesesExpression(DiceParenthesesExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        final DiceExpression inner = expression.getInner();
        inner.acceptVisitor(this);
        final DiceExpression newInner = this.takeReplacement(inner);

        if (!Comparer.same(inner, newInner))
        {
            this.setReplacement(expression, DiceParenthesesExpression.create(newInner));
        }
    }
}
