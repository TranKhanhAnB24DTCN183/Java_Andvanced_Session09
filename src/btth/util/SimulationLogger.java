package util;

import java.util.Locale;

public class SimulationLogger {
    private final long startTimeMillis;
    private final boolean enabled;

    public SimulationLogger() {
        this(true);
    }

    public SimulationLogger(boolean enabled) {
        this.startTimeMillis = System.currentTimeMillis();
        this.enabled = enabled;
    }

    public synchronized void log(String template, Object... args) {
        if (!enabled) {
            return;
        }

        double elapsedSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
        String message = args.length == 0 ? template : String.format(template, args);
        System.out.printf(Locale.US, "[Time: %5.2fs] %s%n", elapsedSeconds, message);
    }
}
