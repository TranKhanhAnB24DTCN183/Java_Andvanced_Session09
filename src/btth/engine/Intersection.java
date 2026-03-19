package engine;

import entity.Direction;
import entity.Vehicle;
import exception.CollisionException;
import exception.TrafficJamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import util.SimulationLogger;

public class Intersection {
    private final Map<Direction, LinkedBlockingDeque<Vehicle>> laneQueues = new EnumMap<>(Direction.class);
    private final ReentrantLock laneLock = new ReentrantLock(true);
    private final Semaphore crossingSlots;
    private final Set<String> vehiclesInside = ConcurrentHashMap.newKeySet();
    private final AtomicInteger vehiclesInsideCount = new AtomicInteger();
    private final int maxVehiclesInIntersection;
    private final int jamThreshold;
    private final SimulationLogger logger;

    public Intersection(int maxVehiclesInIntersection, int jamThreshold, SimulationLogger logger) {
        if (maxVehiclesInIntersection <= 0 || jamThreshold <= 0) {
            throw new IllegalArgumentException("Suc chua giao lo va nguong ket xe phai lon hon 0.");
        }

        this.maxVehiclesInIntersection = maxVehiclesInIntersection;
        this.jamThreshold = jamThreshold;
        this.logger = logger;
        this.crossingSlots = new Semaphore(maxVehiclesInIntersection, true);

        for (Direction direction : Direction.values()) {
            laneQueues.put(direction, new LinkedBlockingDeque<>());
        }
    }

    public void arrive(Vehicle vehicle) throws TrafficJamException {
        laneLock.lock();
        try {
            LinkedBlockingDeque<Vehicle> lane = laneQueues.get(vehicle.getDirection());
            if (vehicle.hasPriority()) {
                lane.offerFirst(vehicle);
            } else {
                lane.offerLast(vehicle);
            }

            logger.log(
                    "%s %s xep hang o huong %s. So xe dang cho cung huong: %d.",
                    vehicle.getType().getDisplayName(),
                    vehicle.getId(),
                    vehicle.getDirection().getDisplayName(),
                    lane.size()
            );

            signalLaneHeads();

            if (lane.size() > jamThreshold) {
                throw new TrafficJamException(String.format(
                        "Hang doi huong %s da dat %d xe, vuot nguong %d.",
                        vehicle.getDirection().getDisplayName(),
                        lane.size(),
                        jamThreshold
                ));
            }
        } finally {
            laneLock.unlock();
        }
    }

    public boolean tryEnter(Vehicle vehicle, TrafficLight trafficLight) throws CollisionException {
        laneLock.lock();
        try {
            LinkedBlockingDeque<Vehicle> lane = laneQueues.get(vehicle.getDirection());
            Vehicle laneHead = lane.peekFirst();
            if (laneHead != vehicle) {
                return false;
            }

            if (!trafficLight.canVehicleProceed(vehicle)) {
                return false;
            }

            if (!crossingSlots.tryAcquire()) {
                return false;
            }

            Vehicle grantedVehicle = lane.pollFirst();
            if (grantedVehicle != vehicle) {
                crossingSlots.release();
                return false;
            }

            int activeVehicles = vehiclesInsideCount.incrementAndGet();
            vehiclesInside.add(vehicle.getId());

            if (activeVehicles > maxVehiclesInIntersection) {
                vehiclesInside.remove(vehicle.getId());
                vehiclesInsideCount.decrementAndGet();
                crossingSlots.release();
                throw new CollisionException("Phat hien vuot qua suc chua cua giao lo.");
            }

            logger.log(
                    "%s %s duoc cap quyen di vao giao lo.",
                    vehicle.getType().getDisplayName(),
                    vehicle.getId()
            );
            return true;
        } finally {
            laneLock.unlock();
        }
    }

    public void leave(Vehicle vehicle) {
        laneLock.lock();
        try {
            if (!vehiclesInside.remove(vehicle.getId())) {
                return;
            }

            vehiclesInsideCount.decrementAndGet();
            crossingSlots.release();
            logger.log("%s %s da roi giao lo an toan.", vehicle.getType().getDisplayName(), vehicle.getId());
            signalLaneHeads();
        } finally {
            laneLock.unlock();
        }
    }

    public void cleanupWaitingVehicle(Vehicle vehicle) {
        laneLock.lock();
        try {
            if (vehiclesInside.contains(vehicle.getId())) {
                return;
            }

            LinkedBlockingDeque<Vehicle> lane = laneQueues.get(vehicle.getDirection());
            if (lane.remove(vehicle)) {
                signalLaneHeads();
            }
        } finally {
            laneLock.unlock();
        }
    }

    public List<Vehicle> snapshotWaitingVehicles() {
        laneLock.lock();
        try {
            return laneQueues.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(ArrayList::new));
        } finally {
            laneLock.unlock();
        }
    }

    public Map<Direction, Integer> snapshotQueueSizes() {
        laneLock.lock();
        try {
            Map<Direction, Integer> snapshot = new EnumMap<>(Direction.class);
            for (Map.Entry<Direction, LinkedBlockingDeque<Vehicle>> entry : laneQueues.entrySet()) {
                snapshot.put(entry.getKey(), entry.getValue().size());
            }
            return snapshot;
        } finally {
            laneLock.unlock();
        }
    }

    public int getVehiclesInsideCount() {
        return vehiclesInsideCount.get();
    }

    private void signalLaneHeads() {
        for (LinkedBlockingDeque<Vehicle> lane : laneQueues.values()) {
            Vehicle headVehicle = lane.peekFirst();
            if (headVehicle != null) {
                headVehicle.signalToRecheck();
            }
        }
    }
}
