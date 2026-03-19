package pattern.state;

public final class YellowState implements TrafficLightState {
    private final long greenDurationMillis;
    private final long yellowDurationMillis;
    private final long redDurationMillis;

    public YellowState(long greenDurationMillis, long yellowDurationMillis, long redDurationMillis) {
        validateDurations(greenDurationMillis, yellowDurationMillis, redDurationMillis);
        this.greenDurationMillis = greenDurationMillis;
        this.yellowDurationMillis = yellowDurationMillis;
        this.redDurationMillis = redDurationMillis;
    }

    @Override
    public String getName() {
        return "VANG";
    }

    @Override
    public long getDurationMillis() {
        return yellowDurationMillis;
    }

    @Override
    public boolean allowsStandardPassage() {
        return false;
    }

    @Override
    public TrafficLightState nextState() {
        return new RedState(greenDurationMillis, yellowDurationMillis, redDurationMillis);
    }

    private void validateDurations(long green, long yellow, long red) {
        if (green <= 0 || yellow <= 0 || red <= 0) {
            throw new IllegalArgumentException("Thoi gian cua moi pha den phai lon hon 0.");
        }
    }
}
