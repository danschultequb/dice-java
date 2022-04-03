package qub;

public class DiceExpressionToStringVisitor implements DiceExpressionVisitor
{
    private final CharacterWriteStream stream;
    private int charactersWritten;

    private DiceExpressionToStringVisitor(CharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");

        this.stream = stream;
    }

    public static DiceExpressionToStringVisitor create(CharacterWriteStream stream)
    {
        return new DiceExpressionToStringVisitor(stream);
    }

    private void addCharactersWritten(int charactersWritten)
    {
        PreCondition.assertGreaterThanOrEqualTo(charactersWritten, 0, "charactersWritten");

        this.charactersWritten += charactersWritten;

        PostCondition.assertGreaterThanOrEqualTo(this.getCharactersWritten(), charactersWritten, "this.getCharactersWritten()");
    }

    public int getCharactersWritten()
    {
        final int result = this.charactersWritten;

        PostCondition.assertGreaterThanOrEqualTo(result, 0, "result");

        return result;
    }

    @Override
    public void visitConstantExpression(DiceConstantExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        this.addCharactersWritten(this.stream.write(Integers.toString(expression.getConstant())).await());
    }

    @Override
    public void visitPlusExpression(DicePlusExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        expression.getLeft().acceptVisitor(this);

        this.addCharactersWritten(this.stream.write(" + ").await());

        expression.getRight().acceptVisitor(this);
    }

    @Override
    public void visitRollExpression(DiceRollExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        this.addCharactersWritten(this.stream.write(Integers.toString(expression.getDiceCount())).await());
        this.addCharactersWritten(this.stream.write('d').await());
        this.addCharactersWritten(this.stream.write(Integers.toString(expression.getFaceCount())).await());
    }

    @Override
    public void visitParenthesesExpression(DiceParenthesesExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        this.addCharactersWritten(this.stream.write('(').await());
        expression.getInner().acceptVisitor(this);
        this.addCharactersWritten(this.stream.write(')').await());
    }
}
