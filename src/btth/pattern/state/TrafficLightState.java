package pattern.state;

public interface TrafficLightState {
    String getName();

    long getDurationMillis();

    boolean allowsStandardPassage();

    TrafficLightState nextState();
}
