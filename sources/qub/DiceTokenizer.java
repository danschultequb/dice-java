package qub;

public class DiceTokenizer implements Iterator<DiceToken>
{
    private final Iterator<Character> characters;
    private boolean started;
    private DiceToken current;

    private DiceTokenizer(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        this.characters = characters;
    }

    public static DiceTokenizer create(Iterator<Character> characters)
    {
        return new DiceTokenizer(characters);
    }

    @Override
    public boolean hasStarted()
    {
        return this.started;
    }

    @Override
    public boolean hasCurrent()
    {
        return this.current != null;
    }

    @Override
    public DiceToken getCurrent()
    {
        PreCondition.assertTrue(this.hasCurrent(), "this.hasCurrent()");

        return this.current;
    }

    @Override
    public boolean next()
    {
        if (!this.hasStarted())
        {
            this.characters.start();
            this.started = true;
        }

        if (!this.characters.hasCurrent())
        {
            this.current = null;
        }
        else
        {
            switch (this.characters.getCurrent())
            {
                case '+':
                    this.characters.next();
                    this.current = DiceToken.plus();
                    break;

                default:
                    if (Characters.isDigit(this.characters.getCurrent()))
                    {
                        final String text = CharacterList.create(this.characters.takeWhile(Characters::isDigit)).toString();
                        this.current = DiceToken.digits(text);
                    }
                    else if (Characters.isLetter(this.characters.getCurrent()))
                    {
                        final String text = CharacterList.create(this.characters.takeWhile(Characters::isLetter)).toString();
                        this.current = DiceToken.letters(text);
                    }
                    else if (Characters.isWhitespace(this.characters.getCurrent()))
                    {
                        final String text = CharacterList.create(this.characters.takeWhile(Characters::isWhitespace)).toString();
                        this.current = DiceToken.whitespace(text);
                    }
                    else
                    {
                        final String text = Characters.toString(this.characters.takeCurrent());
                        this.current = DiceToken.unrecognized(text);
                    }
                    break;
            }
        }
        
        return this.hasCurrent();
    }
}
