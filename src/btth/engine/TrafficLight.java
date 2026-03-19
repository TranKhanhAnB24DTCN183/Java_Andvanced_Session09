package engine;

import entity.Vehicle;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import pattern.observer.TrafficLightObserver;
import pattern.state.TrafficLightState;
import util.SimulationLogger;

public class TrafficLight {
    private final List<TrafficLightObserver> observers = new CopyOnWriteArrayList<>();
    private final SimulationLogger logger;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile TrafficLightState currentState;
    private Thread controllerThread;

    public TrafficLight(TrafficLightState initialState, SimulationLogger logger) {
        if (initialState == null) {
            throw new IllegalArgumentException("Trang thai den ban dau khong duoc null.");
        }
        this.currentState = initialState;
        this.logger = logger;
    }

    public void registerObserver(TrafficLightObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(TrafficLightObserver observer) {
        observers.remove(observer);
    }

    public TrafficLightState getCurrentState() {
        return currentState;
    }

    public boolean canVehicleProceed(Vehicle vehicle) {
        return vehicle.hasPriority() || currentState.allowsStandardPassage();
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        logger.log("Den giao thong khoi dong o trang thai %s.", currentState.getName());
        controllerThread = new Thread(this::runStateMachine, "traffic-light-daemon");
        controllerThread.setDaemon(true);
        controllerThread.start();
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }

        if (controllerThread != null) {
            controllerThread.interrupt();
        }
    }

    public void forceState(TrafficLightState forcedState) {
        if (forcedState == null) {
            throw new IllegalArgumentException("Trang thai cuong buc khong duoc null.");
        }

        stop();
        currentState = forcedState;
        logger.log("Den giao thong chuyen sang che do xa hang doi voi trang thai %s.", currentState.getName());
        notifyObservers();
    }

    private void runStateMachine() {
        while (running.get()) {
            try {
                Thread.sleep(currentState.getDurationMillis());
                if (!running.get()) {
                    return;
                }
                currentState = currentState.nextState();
                logger.log("Den giao thong chuyen sang %s.", currentState.getName());
                notifyObservers();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void notifyObservers() {
        long timestampMillis = System.currentTimeMillis();
        for (TrafficLightObserver observer : observers) {
            observer.onLightChanged(currentState, timestampMillis);
        }
    }
}
