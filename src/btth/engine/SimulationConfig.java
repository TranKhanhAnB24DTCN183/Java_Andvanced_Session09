package engine;

public class SimulationConfig {
    private final int simulationDurationSeconds;
    private final long vehicleSpawnIntervalMillis;
    private final long snapshotIntervalMillis;
    private final long greenDurationMillis;
    private final long yellowDurationMillis;
    private final long redDurationMillis;
    private final int maxVehiclesInIntersection;
    private final int jamThreshold;
    private final int vehicleWorkerThreads;
    private final int shutdownWaitSeconds;

    public SimulationConfig(
            int simulationDurationSeconds,
            long vehicleSpawnIntervalMillis,
            long snapshotIntervalMillis,
            long greenDurationMillis,
            long yellowDurationMillis,
            long redDurationMillis,
            int maxVehiclesInIntersection,
            int jamThreshold,
            int vehicleWorkerThreads,
            int shutdownWaitSeconds
    ) {
        if (simulationDurationSeconds <= 0
                || vehicleSpawnIntervalMillis <= 0
                || snapshotIntervalMillis <= 0
                || greenDurationMillis <= 0
                || yellowDurationMillis <= 0
                || redDurationMillis <= 0
                || maxVehiclesInIntersection <= 0
                || jamThreshold <= 0
                || vehicleWorkerThreads <= 0
                || shutdownWaitSeconds <= 0) {
            throw new IllegalArgumentException("Tat ca tham so cau hinh phai lon hon 0.");
        }

        this.simulationDurationSeconds = simulationDurationSeconds;
        this.vehicleSpawnIntervalMillis = vehicleSpawnIntervalMillis;
        this.snapshotIntervalMillis = snapshotIntervalMillis;
        this.greenDurationMillis = greenDurationMillis;
        this.yellowDurationMillis = yellowDurationMillis;
        this.redDurationMillis = redDurationMillis;
        this.maxVehiclesInIntersection = maxVehiclesInIntersection;
        this.jamThreshold = jamThreshold;
        this.vehicleWorkerThreads = vehicleWorkerThreads;
        this.shutdownWaitSeconds = shutdownWaitSeconds;
    }

    public static SimulationConfig defaultConfig() {
        return new SimulationConfig(
                20,
                700L,
                3_000L,
                4_000L,
                1_500L,
                3_500L,
                1,
                6,
                12,
                20
        );
    }

    public int getSimulationDurationSeconds() {
        return simulationDurationSeconds;
    }

    public long getVehicleSpawnIntervalMillis() {
        return vehicleSpawnIntervalMillis;
    }

    public long getSnapshotIntervalMillis() {
        return snapshotIntervalMillis;
    }

    public long getGreenDurationMillis() {
        return greenDurationMillis;
    }

    public long getYellowDurationMillis() {
        return yellowDurationMillis;
    }

    public long getRedDurationMillis() {
        return redDurationMillis;
    }

    public int getMaxVehiclesInIntersection() {
        return maxVehiclesInIntersection;
    }

    public int getJamThreshold() {
        return jamThreshold;
    }

    public int getVehicleWorkerThreads() {
        return vehicleWorkerThreads;
    }

    public int getShutdownWaitSeconds() {
        return shutdownWaitSeconds;
    }
}
