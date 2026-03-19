package entity;

import java.util.concurrent.ThreadLocalRandom;

public enum VehicleType {
    MOTORBIKE("XM", "Xe may", false, 45, 65),
    CAR("OT", "O to", false, 35, 55),
    TRUCK("XT", "Xe tai", false, 25, 40),
    AMBULANCE("XC", "Xe cuu thuong", true, 50, 70);

    private final String code;
    private final String displayName;
    private final boolean priorityType;
    private final int minSpeedKph;
    private final int maxSpeedKph;

    VehicleType(String code, String displayName, boolean priorityType, int minSpeedKph, int maxSpeedKph) {
        this.code = code;
        this.displayName = displayName;
        this.priorityType = priorityType;
        this.minSpeedKph = minSpeedKph;
        this.maxSpeedKph = maxSpeedKph;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPriorityType() {
        return priorityType;
    }

    public int randomSpeedKph() {
        return ThreadLocalRandom.current().nextInt(minSpeedKph, maxSpeedKph + 1);
    }
}
