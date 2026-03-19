package entity;

import engine.Intersection;
import engine.TrafficLight;
import engine.TrafficMonitor;
import exception.CollisionException;
import exception.TrafficJamException;
import pattern.observer.TrafficLightObserver;
import pattern.state.TrafficLightState;
import util.SimulationLogger;

public abstract class Vehicle implements Runnable, TrafficLightObserver {
    private final String id;
    private final VehicleType type;
    private final Direction direction;
    private final int speedKph;
    private final long approachDelayMillis;
    private final long crossingDurationMillis;
    private final Intersection intersection;
    private final TrafficLight trafficLight;
    private final TrafficMonitor monitor;
    private final SimulationLogger logger;
    private final Object waitMonitor = new Object();

    protected Vehicle(
            String id,
            VehicleType type,
            Direction direction,
            int speedKph,
            long approachDelayMillis,
            long crossingDurationMillis,
            Intersection intersection,
            TrafficLight trafficLight,
            TrafficMonitor monitor,
            SimulationLogger logger
    ) {
        if (id == null || type == null || direction == null || intersection == null || trafficLight == null || monitor == null) {
            throw new IllegalArgumentException("Thong tin phuong tien va phu thuoc he thong khong duoc null.");
        }
        if (speedKph <= 0 || approachDelayMillis < 0 || crossingDurationMillis <= 0) {
            throw new IllegalArgumentException("Toc do va thoi gian di chuyen cua xe phai hop le.");
        }

        this.id = id;
        this.type = type;
        this.direction = direction;
        this.speedKph = speedKph;
        this.approachDelayMillis = approachDelayMillis;
        this.crossingDurationMillis = crossingDurationMillis;
        this.intersection = intersection;
        this.trafficLight = trafficLight;
        this.monitor = monitor;
        this.logger = logger;
    }

    public String getId() {
        return id;
    }

    public VehicleType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSpeedKph() {
        return speedKph;
    }

    public long getApproachDelayMillis() {
        return approachDelayMillis;
    }

    public long getCrossingDurationMillis() {
        return crossingDurationMillis;
    }

    public final void signalToRecheck() {
        synchronized (waitMonitor) {
            waitMonitor.notifyAll();
        }
    }

    @Override
    public final void onLightChanged(TrafficLightState newState, long timestampMillis) {
        signalToRecheck();
    }

    @Override
    public final void run() {
        boolean queuedAtIntersection = false;
        boolean enteredIntersection = false;
        boolean completedTrip = false;

        logger.log(
                "%s %s xuat hien o huong %s, toc do %d km/h.",
                type.getDisplayName(),
                id,
                direction.getDisplayName(),
                speedKph
        );

        try {
            pause(approachDelayMillis);
            try {
                intersection.arrive(this);
            } catch (TrafficJamException exception) {
                monitor.recordTrafficJam(exception);
            }
            queuedAtIntersection = true;
            trafficLight.registerObserver(this);

            while (!Thread.currentThread().isInterrupted()) {
                if (intersection.tryEnter(this, trafficLight)) {
                    enteredIntersection = true;
                    queuedAtIntersection = false;
                    logger.log("%s %s dang di qua giao lo.", type.getDisplayName(), id);
                    pause(crossingDurationMillis);
                    completedTrip = true;
                    return;
                }
                waitForNextSignal();
            }
        } catch (CollisionException exception) {
            monitor.recordCollision(exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.log("%s %s bi gian doan truoc khi ket thuc hanh trinh.", type.getDisplayName(), id);
        } finally {
            trafficLight.unregisterObserver(this);
            if (enteredIntersection) {
                intersection.leave(this);
                if (completedTrip) {
                    monitor.recordVehiclePassed(this);
                    logger.log("%s %s da di qua nga tu thanh cong.", type.getDisplayName(), id);
                }
            } else if (queuedAtIntersection) {
                intersection.cleanupWaitingVehicle(this);
            }
            monitor.recordVehicleFinished(this);
        }
    }

    public abstract boolean hasPriority();

    private void waitForNextSignal() throws InterruptedException {
        synchronized (waitMonitor) {
            waitMonitor.wait(250L);
        }
    }

    private void pause(long millis) throws InterruptedException {
        if (millis > 0) {
            Thread.sleep(millis);
        }
    }
}
