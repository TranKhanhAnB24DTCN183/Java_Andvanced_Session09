package pattern.state;

public final class GreenState implements TrafficLightState {
    private final long greenDurationMillis;
    private final long yellowDurationMillis;
    private final long redDurationMillis;

    public GreenState(long greenDurationMillis, long yellowDurationMillis, long redDurationMillis) {
        validateDurations(greenDurationMillis, yellowDurationMillis, redDurationMillis);
        this.greenDurationMillis = greenDurationMillis;
        this.yellowDurationMillis = yellowDurationMillis;
        this.redDurationMillis = redDurationMillis;
    }

    @Override
    public String getName() {
        return "XANH";
    }

    @Override
    public long getDurationMillis() {
        return greenDurationMillis;
    }

    @Override
    public boolean allowsStandardPassage() {
        return true;
    }

    @Override
    public TrafficLightState nextState() {
        return new YellowState(greenDurationMillis, yellowDurationMillis, redDurationMillis);
    }

    private void validateDurations(long green, long yellow, long red) {
        if (green <= 0 || yellow <= 0 || red <= 0) {
            throw new IllegalArgumentException("Thoi gian cua moi pha den phai lon hon 0.");
        }
    }
}
