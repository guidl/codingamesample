/* utilisé pour le singleton et les machines à états
Name() au lieu de getText mais plus sensibl à la casse 
*/

import java.util.Random;

public enum Color {
    RED, GREEN, BLUE, YELLOW, ORANGE;

    // Method to get a random Color value
    public static Color getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(Color.values().length); // Generate a random index
        return Color.values()[index]; // Return the Color at that index
    }

    // Method to get the ordinal of a specific Color
    public static int getOrdinal(Color color) {
        return color.ordinal(); // Return the ordinal of the provided color
    }

    // Method to compare two Color values
    public static int compareColors(Color c1, Color c2) {
        return Integer.compare(c1.ordinal(), c2.ordinal()); // Compare based on ordinal values
        // Alternatively, you can compare based on names: 
        // return c1.name().compareTo(c2.name());
    }
}

public class EnumExample {
    public static void main(String[] args) {
        // Get a random color
        Color randomColor1 = Color.getRandomColor();
        System.out.println("Random Color 1: " + randomColor1);

        // Get a second random color
        Color randomColor2 = Color.getRandomColor();
        System.out.println("Random Color 2: " + randomColor2);

        // Get the ordinal value of the first random color
        int ordinalValue1 = Color.getOrdinal(randomColor1);
        System.out.println("Ordinal of " + randomColor1 + ": " + ordinalValue1);

        // Get the ordinal value of the second random color
        int ordinalValue2 = Color.getOrdinal(randomColor2);
        System.out.println("Ordinal of " + randomColor2 + ": " + ordinalValue2);

        // Compare the two colors
        int comparisonResult = Color.compareColors(randomColor1, randomColor2);
        if (comparisonResult == 0) {
            System.out.println(randomColor1 + " is equal to " + randomColor2);
        } else if (comparisonResult < 0) {
            System.out.println(randomColor1 + " comes before " + randomColor2);
        } else {
            System.out.println(randomColor1 + " comes after " + randomColor2);
        }
    }
}

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

/* Domain class for player. */
enum PlayerType {
    Me(1),
    Enemy(2);

    private int nb;

    PlayerType(int nbParam) {
        this.nb = nbParam;
    }

    public int getNb() {
        return this.nb;
    }

    /*
     * car le PlayerType.valueOf est sensible à la casse mais est ce que cela ne
     * fonctionne
     * qu'avec Me, Enemy ?
     */
    public static PlayerType fromInt(int nb) {
        for (PlayerType p : PlayerType.values()) {
            if (p.nb == nb) {
                return p;
            }
        }
        throw new IllegalArgumentException(String.valueOf(nb));
    }
}
