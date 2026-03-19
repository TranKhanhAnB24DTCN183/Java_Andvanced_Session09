package app;

import engine.SimulationConfig;
import engine.SimulationEngine;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SimulationConfig config = SimulationConfig.defaultConfig();
        SimulationEngine engine = new SimulationEngine(config);
        engine.run();
    }
}
