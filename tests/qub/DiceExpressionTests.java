package qub;

public interface DiceExpressionTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceExpression.class, () ->
        {
            runner.testGroup("constant(int)", () ->
            {
                final Action1<Integer> constantTest = (Integer constant) ->
                {
                    runner.test("with " + constant, (Test test) ->
                    {
                        final DiceConstantExpression expression = DiceExpression.constant(constant);
                        test.assertNotNull(expression);
                        test.assertEqual(constant, expression.getConstant());
                    });
                };

                constantTest.run(-1);
                constantTest.run(0);
                constantTest.run(1);
            });

            runner.testGroup("roll(int,int)", () ->
            {
                final Action3<Integer,Integer,Throwable> rollErrorTest = (Integer diceCount, Integer faceCount, Throwable expected) ->
                {
                    runner.test("with " + English.andList(diceCount, faceCount), (Test test) ->
                    {
                        test.assertThrows(() -> DiceExpression.roll(diceCount, faceCount),
                            expected);
                    });
                };

                rollErrorTest.run(-1, 1, new PreConditionFailure("diceCount (-1) must be null or greater than or equal to 1."));
                rollErrorTest.run(0, 1, new PreConditionFailure("diceCount (0) must be null or greater than or equal to 1."));
                rollErrorTest.run(1, -1, new PreConditionFailure("faceCount (-1) must be greater than or equal to 1."));
                rollErrorTest.run(1, 0, new PreConditionFailure("faceCount (0) must be greater than or equal to 1."));

                final Action2<Integer,Integer> rollTest = (Integer diceCount, Integer faceCount) ->
                {
                    runner.test("with " + English.andList(diceCount, faceCount), (Test test) ->
                    {
                        final DiceRollExpression expression = DiceExpression.roll(diceCount, faceCount);
                        test.assertNotNull(expression);
                        test.assertEqual(diceCount, expression.getDiceCount());
                        test.assertEqual(faceCount, expression.getFaceCount());
                    });
                };

                rollTest.run(null, 10);
                rollTest.run(1, 1);
                rollTest.run(1, 2);
                rollTest.run(3, 4);
            });

            runner.testGroup("plus(DiceExpression,DiceExpression)", () ->
            {
                final Action3<DiceExpression,DiceExpression,Throwable> plusErrorTest = (DiceExpression left, DiceExpression right, Throwable expected) ->
                {
                    runner.test("with " + English.andList(left, right), (Test test) ->
                    {
                        test.assertThrows(() -> DiceExpression.plus(left, right),
                            expected);
                    });
                };

                plusErrorTest.run(null, DiceConstantExpression.create(2), new PreConditionFailure("left cannot be null."));
                plusErrorTest.run(DiceConstantExpression.create(1), null, new PreConditionFailure("right cannot be null."));

                final Action2<DiceExpression,DiceExpression> plusTest = (DiceExpression left, DiceExpression right) ->
                {
                    runner.test("with " + English.andList(left, right), (Test test) ->
                    {
                        final DicePlusExpression expression = DiceExpression.plus(left, right);
                        test.assertNotNull(expression);
                        test.assertSame(left, expression.getLeft());
                        test.assertSame(right, expression.getRight());
                    });
                };

                plusTest.run(DiceExpression.constant(1), DiceExpression.constant(2));
            });

            runner.testGroup("parse(String)", () ->
            {
                final Action2<String,Throwable> parseErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> DiceExpression.parse(text).await(),
                            expected);
                    });
                };

                parseErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                parseErrorTest.run("", new ParseException("Missing constant value or dice expression."));
                parseErrorTest.run("    ", new ParseException("Missing constant value or dice expression."));
                parseErrorTest.run("1e", new ParseException("Expected dice roll separator ('d') or plus sign ('+'), but found \"e\" instead."));
                parseErrorTest.run("1 e", new ParseException("Expected dice roll separator ('d') or plus sign ('+'), but found \"e\" instead."));
                parseErrorTest.run("+", new ParseException("Expected constant value or dice expression, but found \"+\" instead."));
                parseErrorTest.run("d", new ParseException("Missing dice roll face count value."));
                parseErrorTest.run("*", new ParseException("Expected constant value or dice count of a roll, but found \"*\" instead."));
                parseErrorTest.run("1d", new ParseException("Missing dice roll face count value."));
                parseErrorTest.run("1d+", new ParseException("Expected dice roll face count value, but found \"+\" instead."));
                parseErrorTest.run("1d*", new ParseException("Expected dice roll face count value, but found \"*\" instead."));
                parseErrorTest.run("1&", new ParseException("Expected dice roll separator ('d') or plus sign ('+'), but found \"&\" instead."));
                parseErrorTest.run("1  2", new ParseException("Expected dice roll separator ('d') or plus sign ('+'), but found \"2\" instead."));
                parseErrorTest.run("d  d", new ParseException("Expected dice roll face count value, but found \"d\" instead."));
                parseErrorTest.run("1+", new ParseException("Missing right side of plus ('+') expression."));
                parseErrorTest.run("1d6 5", new ParseException("Expected plus sign ('+'), but found \"5\" instead."));
                parseErrorTest.run("1d6 d", new ParseException("Expected plus sign ('+'), but found \"d\" instead."));
                parseErrorTest.run("d4 + fireball", new ParseException("Expected constant value or dice roll separator ('d'), but found \"fireball\" instead."));

                final Action2<String,DiceExpression> parseTest = (String text, DiceExpression expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final DiceExpression expression = DiceExpression.parse(text).await();
                        test.assertEqual(expected, expression);
                    });
                };

                parseTest.run("1", DiceExpression.constant(1));
                parseTest.run("1d4", DiceExpression.roll(1, 4));
                parseTest.run("d4", DiceExpression.roll(null, 4));
                parseTest.run(
                    "1 +    3",
                    DiceExpression.plus(
                        DiceExpression.constant(1),
                        DiceExpression.constant(3)));
                parseTest.run(
                    "5d6 + 4",
                    DiceExpression.plus(
                        DiceExpression.roll(5, 6),
                        DiceExpression.constant(4)));
                parseTest.run(
                    "2d6 + 3 + 1d4",
                    DiceExpression.plus(
                        DiceExpression.plus(
                            DiceExpression.roll(2, 6),
                            DiceExpression.constant(3)),
                        DiceExpression.roll(1, 4)));
                parseTest.run(
                    "d4 + d20",
                    DiceExpression.plus(
                        DiceExpression.roll(4),
                        DiceExpression.roll(20)));
            });
        });
    }
}
