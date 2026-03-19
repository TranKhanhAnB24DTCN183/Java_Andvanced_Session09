package engine;

import entity.Vehicle;
import entity.VehicleType;
import exception.CollisionException;
import exception.TrafficJamException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import util.SimulationLogger;

public class TrafficMonitor {
    private final SimulationLogger logger;
    private final ConcurrentMap<VehicleType, AtomicInteger> generatedByType = new ConcurrentHashMap<>();
    private final ConcurrentMap<VehicleType, AtomicInteger> passedByType = new ConcurrentHashMap<>();
    private final AtomicInteger totalGenerated = new AtomicInteger();
    private final AtomicInteger totalPassed = new AtomicInteger();
    private final AtomicInteger completedVehicles = new AtomicInteger();
    private final AtomicInteger trafficJamCount = new AtomicInteger();
    private final AtomicInteger collisionCount = new AtomicInteger();

    public TrafficMonitor(SimulationLogger logger) {
        this.logger = logger;
        for (VehicleType type : VehicleType.values()) {
            generatedByType.put(type, new AtomicInteger());
            passedByType.put(type, new AtomicInteger());
        }
    }

    public void recordVehicleGenerated(Vehicle vehicle) {
        totalGenerated.incrementAndGet();
        generatedByType.get(vehicle.getType()).incrementAndGet();
    }

    public void recordVehiclePassed(Vehicle vehicle) {
        totalPassed.incrementAndGet();
        passedByType.get(vehicle.getType()).incrementAndGet();
    }

    public void recordVehicleFinished(Vehicle vehicle) {
        completedVehicles.incrementAndGet();
    }

    public void recordTrafficJam(TrafficJamException exception) {
        trafficJamCount.incrementAndGet();
        logger.log("TRAFFIC JAM: %s", exception.getMessage());
    }

    public void recordCollision(CollisionException exception) {
        collisionCount.incrementAndGet();
        logger.log("COLLISION: %s", exception.getMessage());
    }

    public int getTotalGenerated() {
        return totalGenerated.get();
    }

    public int getTotalPassed() {
        return totalPassed.get();
    }

    public int getTrafficJamCount() {
        return trafficJamCount.get();
    }

    public int getCollisionCount() {
        return collisionCount.get();
    }

    public int getCompletedVehicles() {
        return completedVehicles.get();
    }

    public void logRealtimeStatus(Intersection intersection, TrafficLight trafficLight) {
        List<Vehicle> waitingVehicles = intersection.snapshotWaitingVehicles();
        String waitingByType = waitingVehicles.stream()
                .collect(Collectors.groupingBy(
                        vehicle -> vehicle.getType().getDisplayName(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        if (waitingByType.isEmpty()) {
            waitingByType = "khong co xe dang cho";
        }

        String queueByDirection = intersection.snapshotQueueSizes()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().name()))
                .map(entry -> entry.getKey().getDisplayName() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));

        logger.log(
                "Snapshot | Den=%s | Cho=%d | Trong giao lo=%d | Da tao=%d | Da qua=%d | Ket xe=%d | Theo loai=%s | Theo huong=%s",
                trafficLight.getCurrentState().getName(),
                waitingVehicles.size(),
                intersection.getVehiclesInsideCount(),
                totalGenerated.get(),
                totalPassed.get(),
                trafficJamCount.get(),
                waitingByType,
                queueByDirection
        );
    }

    public void printFinalReport() {
        logger.log("========== BAO CAO TONG KET ==========");
        logger.log("Tong so xe duoc tao: %d (%s)", totalGenerated.get(), summarizeByType(generatedByType));
        logger.log("Tong so xe qua nga tu thanh cong: %d (%s)", totalPassed.get(), summarizeByType(passedByType));
        logger.log("So luong thread xe hoan tat: %d", completedVehicles.get());
        logger.log("So lan ket xe: %d", trafficJamCount.get());
        logger.log("So su co collision: %d", collisionCount.get());
        logger.log("======================================");
    }

    private String summarizeByType(Map<VehicleType, AtomicInteger> metrics) {
        return metrics.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey().getDisplayName() + "=" + entry.getValue().get())
                .collect(Collectors.joining(", "));
    }
}
