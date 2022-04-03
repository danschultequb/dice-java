package qub;

public interface DiceTokenTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceToken.class, () ->
        {
            runner.testGroup("digits(String)", () ->
            {
                final Action2<String,Throwable> digitsErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> DiceToken.digits(text),
                            expected);
                    });
                };

                digitsErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                digitsErrorTest.run("", new PreConditionFailure("text cannot be empty."));

                final Action1<String> digitsTest = (String text) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final DiceToken token = DiceToken.digits(text);
                        test.assertNotNull(token);
                        test.assertEqual(text, token.getText());
                        test.assertEqual(DiceTokenType.Digits, token.getType());
                    });
                };

                digitsTest.run("1");
                digitsTest.run("234");
                digitsTest.run("abc");
            });

            runner.testGroup("letters(String)", () ->
            {
                final Action2<String,Throwable> lettersErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> DiceToken.letters(text),
                            expected);
                    });
                };

                lettersErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                lettersErrorTest.run("", new PreConditionFailure("text cannot be empty."));

                final Action1<String> lettersTest = (String text) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final DiceToken token = DiceToken.letters(text);
                        test.assertNotNull(token);
                        test.assertEqual(text, token.getText());
                        test.assertEqual(DiceTokenType.Letters, token.getType());
                    });
                };

                lettersTest.run("a");
                lettersTest.run("bcd");
                lettersTest.run("1");
            });

            runner.test("plus()", (Test test) ->
            {
                final DiceToken token = DiceToken.plus();
                test.assertNotNull(token);
                test.assertEqual("+", token.getText());
                test.assertEqual(DiceTokenType.Plus, token.getType());
            });

            runner.testGroup("whitespace(String)", () ->
            {
                final Action2<String,Throwable> whitespaceErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> DiceToken.whitespace(text),
                            expected);
                    });
                };

                whitespaceErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                whitespaceErrorTest.run("", new PreConditionFailure("text cannot be empty."));

                final Action1<String> whitespaceTest = (String text) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final DiceToken token = DiceToken.whitespace(text);
                        test.assertNotNull(token);
                        test.assertEqual(text, token.getText());
                        test.assertEqual(DiceTokenType.Whitespace, token.getType());
                    });
                };

                whitespaceTest.run(" ");
                whitespaceTest.run(" \r\n\t ");
                whitespaceTest.run("abc");
                whitespaceTest.run("23");
            });

            runner.testGroup("unrecognized(String)", () ->
            {
                final Action2<String,Throwable> unrecognizedErrorTest = (String text, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        test.assertThrows(() -> DiceToken.unrecognized(text),
                            expected);
                    });
                };

                unrecognizedErrorTest.run(null, new PreConditionFailure("text cannot be null."));
                unrecognizedErrorTest.run("", new PreConditionFailure("text cannot be empty."));

                final Action1<String> unrecognizedTest = (String text) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(text), (Test test) ->
                    {
                        final DiceToken token = DiceToken.unrecognized(text);
                        test.assertNotNull(token);
                        test.assertEqual(text, token.getText());
                        test.assertEqual(DiceTokenType.Unrecognized, token.getType());
                    });
                };

                unrecognizedTest.run("%");
                unrecognizedTest.run("^");
                unrecognizedTest.run("*");
                unrecognizedTest.run("23");
            });

            runner.testGroup("equals(Object)", () ->
            {
                final Action3<DiceToken,Object,Boolean> equalsTest = (DiceToken token, Object rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(token, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, token.equals(rhs));
                    });
                };

                equalsTest.run(DiceToken.plus(), null, false);
                equalsTest.run(DiceToken.plus(), "+", false);
                equalsTest.run(DiceToken.plus(), DiceToken.plus(), true);
                equalsTest.run(DiceToken.plus(), DiceToken.letters("+"), false);
                equalsTest.run(DiceToken.letters("a"), DiceToken.letters("b"), false);
            });

            runner.testGroup("equals(DiceToken)", () ->
            {
                final Action3<DiceToken,DiceToken,Boolean> equalsTest = (DiceToken token, DiceToken rhs, Boolean expected) ->
                {
                    runner.test("with " + English.andList(token, rhs), (Test test) ->
                    {
                        test.assertEqual(expected, token.equals(rhs));
                    });
                };

                equalsTest.run(DiceToken.plus(), null, false);
                equalsTest.run(DiceToken.plus(), DiceToken.plus(), true);
                equalsTest.run(DiceToken.plus(), DiceToken.letters("+"), false);
                equalsTest.run(DiceToken.letters("a"), DiceToken.letters("b"), false);
            });

            runner.testGroup("toString()", () ->
            {
                final Action2<DiceToken,String> equalsTest = (DiceToken token, String expected) ->
                {
                    runner.test("with " + token, (Test test) ->
                    {
                        test.assertEqual(expected, token.toString());
                    });
                };

                equalsTest.run(DiceToken.plus(), "{\"type\":\"Plus\",\"text\":\"+\"}");
                equalsTest.run(DiceToken.whitespace("   "), "{\"type\":\"Whitespace\",\"text\":\"   \"}");
            });
        });
    }
}
