enum Direction {
    N(0, -1),
    S(0, 1),
    E(1, 0),
    W(-1, 0);

    final int diffX;
    final int diffY;

    Direction(final int diffX, final int diffY) {
        this.diffX = diffX;
        this.diffY = diffY;
    }
}

public enum Blah {
    A("text1"),
    B("text2"),
    C("text3"),
    D("text4");

    private String text;

    Blah(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

 /* car le value of est sensible à la casse */
    public static Blah fromString(String text) {
        for (Blah b : Blah.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        //return null;
throw new IllegalArgumentException(v.toString());
    }
}