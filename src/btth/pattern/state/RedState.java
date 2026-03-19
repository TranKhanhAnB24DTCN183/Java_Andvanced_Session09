package pattern.state;

public final class RedState implements TrafficLightState {
    private final long greenDurationMillis;
    private final long yellowDurationMillis;
    private final long redDurationMillis;

    public RedState(long greenDurationMillis, long yellowDurationMillis, long redDurationMillis) {
        validateDurations(greenDurationMillis, yellowDurationMillis, redDurationMillis);
        this.greenDurationMillis = greenDurationMillis;
        this.yellowDurationMillis = yellowDurationMillis;
        this.redDurationMillis = redDurationMillis;
    }

    @Override
    public String getName() {
        return "DO";
    }

    @Override
    public long getDurationMillis() {
        return redDurationMillis;
    }

    @Override
    public boolean allowsStandardPassage() {
        return false;
    }

    @Override
    public TrafficLightState nextState() {
        return new GreenState(greenDurationMillis, yellowDurationMillis, redDurationMillis);
    }

    private void validateDurations(long green, long yellow, long red) {
        if (green <= 0 || yellow <= 0 || red <= 0) {
            throw new IllegalArgumentException("Thoi gian cua moi pha den phai lon hon 0.");
        }
    }
}
