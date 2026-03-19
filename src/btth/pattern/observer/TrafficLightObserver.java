package pattern.observer;

import pattern.state.TrafficLightState;

public interface TrafficLightObserver {
    void onLightChanged(TrafficLightState newState, long timestampMillis);
}
