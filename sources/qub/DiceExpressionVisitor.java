package qub;

public interface DiceExpressionVisitor
{
    public void visitConstantExpression(DiceConstantExpression expression);

    public void visitPlusExpression(DicePlusExpression expression);

    public void visitRollExpression(DiceRollExpression expression);

    public void visitParenthesesExpression(DiceParenthesesExpression expression);
}
