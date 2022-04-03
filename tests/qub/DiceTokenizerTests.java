package qub;

public interface DiceTokenizerTests
{
    public static void test(TestRunner runner)
    {
        runner.testGroup(DiceTokenizer.class, () ->
        {
            runner.testGroup("create(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> DiceTokenizer.create(null),
                        new PreConditionFailure("characters cannot be null."));
                });

                runner.test("with non-null", (Test test) ->
                {
                    final DiceTokenizer tokenizer = DiceTokenizer.create(Iterator.create());
                    test.assertNotNull(tokenizer);
                    test.assertFalse(tokenizer.hasStarted());
                    test.assertFalse(tokenizer.hasCurrent());
                });
            });

            runner.testGroup("next()", () ->
            {
                final Action2<String,Iterable<DiceToken>> nextTest = (String characters, Iterable<DiceToken> expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(characters), (Test test) ->
                    {
                        final DiceTokenizer tokenizer = DiceTokenizer.create(Strings.iterate(characters));
                        final Iterator<DiceToken> expectedIterator = expected.iterate();

                        tokenizer.start();
                        expectedIterator.start();

                        while (tokenizer.hasCurrent() && expectedIterator.hasCurrent())
                        {
                            test.assertEqual(expectedIterator.getCurrent(), tokenizer.getCurrent());
                            test.assertEqual(expectedIterator.next(), tokenizer.next());
                        }

                        test.assertFalse(tokenizer.hasCurrent());
                        test.assertFalse(expectedIterator.hasCurrent());

                        test.assertFalse(tokenizer.next());
                        test.assertFalse(expectedIterator.next());

                        test.assertFalse(tokenizer.hasCurrent());
                        test.assertFalse(expectedIterator.hasCurrent());
                    });
                };

                nextTest.run(
                    "",
                    Iterable.create());
                nextTest.run(
                    " ",
                    Iterable.create(
                        DiceToken.whitespace(" ")));
                nextTest.run(
                    " \r\n\t ",
                    Iterable.create(
                        DiceToken.whitespace(" \r\n\t ")));
                nextTest.run(
                    "a",
                    Iterable.create(
                        DiceToken.letters("a")));
                nextTest.run(
                    "abcdef",
                    Iterable.create(
                        DiceToken.letters("abcdef")));
                nextTest.run(
                    "1",
                    Iterable.create(
                        DiceToken.digits("1")));
                nextTest.run(
                    "1234",
                    Iterable.create(
                        DiceToken.digits("1234")));
                nextTest.run(
                    "+",
                    Iterable.create(
                        DiceToken.plus()));
                nextTest.run(
                    "++",
                    Iterable.create(
                        DiceToken.plus(),
                        DiceToken.plus()));
                nextTest.run(
                    "*",
                    Iterable.create(
                        DiceToken.unrecognized("*")));
                nextTest.run(
                    "(",
                    Iterable.create(
                        DiceToken.unrecognized("(")));
                nextTest.run(
                    ")",
                    Iterable.create(
                        DiceToken.unrecognized(")")));
                nextTest.run(
                    "-",
                    Iterable.create(
                        DiceToken.unrecognized("-")));
                nextTest.run(
                    "_",
                    Iterable.create(
                        DiceToken.unrecognized("_")));
                nextTest.run(
                    "1d6",
                    Iterable.create(
                        DiceToken.digits("1"),
                        DiceToken.letters("d"),
                        DiceToken.digits("6")));
                nextTest.run(
                    "2d20 + 5",
                    Iterable.create(
                        DiceToken.digits("2"),
                        DiceToken.letters("d"),
                        DiceToken.digits("20"),
                        DiceToken.whitespace(" "),
                        DiceToken.plus(),
                        DiceToken.whitespace(" "),
                        DiceToken.digits("5")));
            });
        });
    }
}
