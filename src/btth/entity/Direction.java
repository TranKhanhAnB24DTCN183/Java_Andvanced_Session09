package entity;

import java.util.concurrent.ThreadLocalRandom;

public enum Direction {
    NORTH("Bac"),
    SOUTH("Nam"),
    EAST("Dong"),
    WEST("Tay");

    private final String displayName;

    Direction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Direction random() {
        Direction[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
