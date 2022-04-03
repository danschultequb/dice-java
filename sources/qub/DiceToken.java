package qub;

public class DiceToken
{
    private final String text;
    private final DiceTokenType type;

    private DiceToken(String text, DiceTokenType type)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");
        PreCondition.assertNotNull(type, "type");

        this.text = text;
        this.type = type;
    }

    private static DiceToken create(String text, DiceTokenType type)
    {
        return new DiceToken(text, type);
    }
    
    public String getText()
    {
        return this.text;
    }
    
    public DiceTokenType getType()
    {
        return this.type;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof DiceToken && this.equals((DiceToken)rhs);
    }

    public boolean equals(DiceToken rhs)
    {
        return rhs != null &&
            this.type == rhs.type &&
            Comparer.equal(this.text, rhs.text);
    }

    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString("type", this.getType().toString())
            .setString("text", this.getText());
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    public static DiceToken digits(String text)
    {
        return DiceToken.create(text, DiceTokenType.Digits);
    }

    public static DiceToken letters(String text)
    {
        return DiceToken.create(text, DiceTokenType.Letters);
    }

    public static DiceToken plus()
    {
        return DiceToken.create("+", DiceTokenType.Plus);
    }

    public static DiceToken whitespace(String text)
    {
        return DiceToken.create(text, DiceTokenType.Whitespace);
    }

    public static DiceToken unrecognized(String text)
    {
        return DiceToken.create(text, DiceTokenType.Unrecognized);
    }
}
