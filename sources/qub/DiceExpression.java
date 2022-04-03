package qub;

public interface DiceExpression
{
    public static final String constantValueText = "constant value";
    public static final String diceExpressionText = "dice expression";

    public JSONObject toJson();

    public static String toString(DiceExpression expression)
    {
        PreCondition.assertNotNull(expression, "expression");

        final InMemoryCharacterStream stream = InMemoryCharacterStream.create();
        expression.toString(stream).await();
        final String result = stream.getText().await();

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    public default Result<Integer> toString(CharacterWriteStream stream)
    {
        PreCondition.assertNotNull(stream, "stream");

        return Result.create(() ->
        {
            final DiceExpressionToStringVisitor visitor = DiceExpressionToStringVisitor.create(stream);
            this.acceptVisitor(visitor);
            return visitor.getCharactersWritten();
        });
    }

    public static boolean equals(DiceExpression expression, Object rhs)
    {
        PreCondition.assertNotNull(expression, "expression");

        return rhs instanceof DiceExpression && expression.equals((DiceExpression)rhs);
    }

    public default boolean equals(DiceExpression rhs)
    {
        return rhs != null &&
            this.getClass().equals(rhs.getClass()) &&
            this.toJson().equals(rhs.toJson());
    }

    public void acceptVisitor(DiceExpressionVisitor visitor);

    public default DiceExpression applyRolls(Random random)
    {
        PreCondition.assertNotNull(random, "random");

        final DiceExpressionApplyRollsVisitor visitor = DiceExpressionApplyRollsVisitor.create(random);
        this.acceptVisitor(visitor);
        return visitor.takeReplacement(this);
    }

    public default int evaluate(Random random)
    {
        PreCondition.assertNotNull(random, "random");

        final DiceExpressionEvaluateVisitor visitor = DiceExpressionEvaluateVisitor.create(random);
        this.acceptVisitor(visitor);
        return visitor.takeValue(this);
    }

    public static DiceConstantExpression constant(int constant)
    {
        return DiceConstantExpression.create(constant);
    }

    public static DiceRollExpression roll(int diceCount, int faceCount)
    {
        return DiceRollExpression.create(diceCount, faceCount);
    }

    public static DicePlusExpression plus(DiceExpression left, DiceExpression right)
    {
        return DicePlusExpression.create(left, right);
    }

    public static Result<DiceExpression> parse(String text)
    {
        PreCondition.assertNotNull(text, "text");

        return DiceExpression.parse(Strings.iterate(text));
    }

    public static Result<DiceExpression> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return DiceExpression.parse(DiceTokenizer.create(characters));
    }

    public static Result<DiceExpression> parse(DiceTokenizer tokenizer)
    {
        PreCondition.assertNotNull(tokenizer, "tokenizer");

        return Result.create(() ->
        {
            Iterator<DiceToken> tokens = tokenizer.start()
                // Ignore all whitespace tokens.
                .where((DiceToken token) -> token.getType() != DiceTokenType.Whitespace);

            final Stack<DiceExpression> expressionStack = Stack.create();

            while (tokens.hasCurrent())
            {
                switch (tokens.getCurrent().getType())
                {
                    case Digits:
                        if (expressionStack.any())
                        {
                            DiceExpression.throwExpectedError("plus sign ('+')", tokens);
                        }
                        else
                        {
                            DiceExpression.parseTerm(tokens, expressionStack);
                        }
                        break;

                    case Plus:
                        if (!expressionStack.any())
                        {
                            DiceExpression.throwExpectedError(Iterable.create(DiceExpression.constantValueText, DiceExpression.diceExpressionText), tokens);
                        }
                        else if (!tokens.next()) // Get past the plus sign
                        {
                            DiceExpression.throwMissingError("right side of plus ('+') expression");
                        }
                        else
                        {
                            // Parse right side of expression.
                            DiceExpression.parseTerm(tokens, expressionStack);

                            final DiceExpression right = expressionStack.pop().await();
                            final DiceExpression left = expressionStack.pop().await();
                            expressionStack.push(DiceExpression.plus(left, right));
                        }
                        break;

                    default:
                        DiceExpression.throwExpectedError(Iterable.create(DiceExpression.constantValueText, "dice count of a roll"), tokens);
                }
            }

            if (!expressionStack.any())
            {
                DiceExpression.throwMissingError(Iterable.create(DiceExpression.constantValueText, DiceExpression.diceExpressionText));
            }

            final DiceExpression result = expressionStack.pop().await();

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    private static void parseTerm(Iterator<DiceToken> tokens, Stack<DiceExpression> expressionStack)
    {
        PreCondition.assertNotNull(tokens, "tokens");
        PreCondition.assertTrue(tokens.hasCurrent(), "tokens.hasCurrent()");
        PreCondition.assertEqual(DiceTokenType.Digits, tokens.getCurrent().getType(), "tokens.getCurrent().getType()");
        PreCondition.assertNotNull(expressionStack, "expressionStack");

        final DiceToken firstDigitToken = tokens.getCurrent();
        if (!tokens.next())
        {
            expressionStack.push(DiceExpression.constant(Integers.parse(firstDigitToken.getText()).await()));
        }
        else
        {
            switch (tokens.getCurrent().getType())
            {
                case Plus:
                    expressionStack.push(DiceExpression.constant(Integers.parse(firstDigitToken.getText()).await()));
                    // Leave plus token for main switch statement.
                    break;

                case Letters:
                    if (!tokens.getCurrent().getText().equals("d"))
                    {
                        DiceExpression.throwExpectedError(Iterable.create("dice roll separator ('d')", "plus sign ('+')"), tokens);
                    }
                    else if (!tokens.next())
                    {
                        DiceExpression.throwMissingError("dice roll face count value");
                    }
                    else if (tokens.getCurrent().getType() != DiceTokenType.Digits)
                    {
                        DiceExpression.throwExpectedError("dice roll face count value", tokens);
                    }
                    else
                    {
                        final int diceCount = Integers.parse(firstDigitToken.getText()).await();
                        final int faceCount = Integers.parse(tokens.getCurrent().getText()).await();
                        expressionStack.push(DiceExpression.roll(diceCount, faceCount));
                        tokens.next();
                    }
                    break;

                default:
                    DiceExpression.throwExpectedError(Iterable.create("dice roll separator ('d')", "plus sign ('+')"), tokens);
            }
        }
    }

    private static void throwExpectedError(String expected, Iterator<DiceToken> tokens)
    {
        DiceExpression.throwExpectedError(Iterable.create(expected), tokens);
    }

    private static void throwExpectedError(Iterable<String> expectedOptions, Iterator<DiceToken> tokens)
    {
        throw new ParseException("Expected " + English.orList(expectedOptions) + ", but found " + Strings.escapeAndQuote(tokens.getCurrent().getText()) + " instead.");
    }

    private static void throwMissingError(String expected)
    {
        DiceExpression.throwMissingError(Iterable.create(expected));
    }

    private static void throwMissingError(Iterable<String> expectedOptions)
    {
        throw new ParseException("Missing " + English.orList(expectedOptions) + ".");
    }
}
