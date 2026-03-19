package engine;

import entity.Vehicle;
import factory.VehicleFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import pattern.state.GreenState;
import util.IdGenerator;
import util.SimulationLogger;

public class SimulationEngine {
    private final SimulationConfig config;
    private final SimulationLogger logger;
    private final TrafficMonitor monitor;
    private final VehicleFactory vehicleFactory;
    private final Intersection intersection;
    private final TrafficLight trafficLight;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService vehicleExecutor;

    public SimulationEngine(SimulationConfig config) {
        this.config = config;
        this.logger = new SimulationLogger();
        this.monitor = new TrafficMonitor(logger);
        this.vehicleFactory = new VehicleFactory(new IdGenerator());
        this.intersection = new Intersection(config.getMaxVehiclesInIntersection(), config.getJamThreshold(), logger);
        this.trafficLight = new TrafficLight(
                new GreenState(
                        config.getGreenDurationMillis(),
                        config.getYellowDurationMillis(),
                        config.getRedDurationMillis()
                ),
                logger
        );
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.vehicleExecutor = Executors.newFixedThreadPool(config.getVehicleWorkerThreads());
    }

    public void run() {
        logger.log("Bat dau mo phong giao thong thong minh trong %d giay.", config.getSimulationDurationSeconds());
        trafficLight.start();
        scheduler.scheduleAtFixedRate(this::spawnVehicleSafely, 0L, config.getVehicleSpawnIntervalMillis(), TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::logSnapshotSafely, 0L, config.getSnapshotIntervalMillis(), TimeUnit.MILLISECONDS);

        try {
            Thread.sleep(config.getSimulationDurationSeconds() * 1_000L);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.log("Mo phong bi dung som do thread chinh bi gian doan.");
        } finally {
            shutdown();
        }
    }

    private void spawnVehicleSafely() {
        if (vehicleExecutor.isShutdown()) {
            return;
        }

        try {
            Vehicle vehicle = vehicleFactory.createRandomVehicle(intersection, trafficLight, monitor, logger);
            monitor.recordVehicleGenerated(vehicle);
            logger.log(
                    "%s %s vua duoc sinh ngau nhien tu huong %s.",
                    vehicle.getType().getDisplayName(),
                    vehicle.getId(),
                    vehicle.getDirection().getDisplayName()
            );
            vehicleExecutor.submit(vehicle);
        } catch (RejectedExecutionException ignored) {
            logger.log("Bo qua mot phuong tien moi vi bo xu ly dang dong.");
        } catch (RuntimeException exception) {
            logger.log("Khong tao duoc phuong tien moi: %s", exception.getMessage());
        }
    }

    private void logSnapshotSafely() {
        try {
            monitor.logRealtimeStatus(intersection, trafficLight);
        } catch (RuntimeException exception) {
            logger.log("Khong the ghi snapshot: %s", exception.getMessage());
        }
    }

    private void shutdown() {
        logger.log("Dung sinh them phuong tien moi va tien hanh ket thuc mo phong.");
        scheduler.shutdownNow();
        trafficLight.forceState(
                new GreenState(
                        config.getGreenDurationMillis(),
                        config.getYellowDurationMillis(),
                        config.getRedDurationMillis()
                )
        );
        vehicleExecutor.shutdown();

        try {
            if (!vehicleExecutor.awaitTermination(config.getShutdownWaitSeconds(), TimeUnit.SECONDS)) {
                logger.log(
                        "Qua %d giay van con xe chua ket thuc, thuc hien ngat co kiem soat.",
                        config.getShutdownWaitSeconds()
                );
                vehicleExecutor.shutdownNow();
                vehicleExecutor.awaitTermination(2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            vehicleExecutor.shutdownNow();
        } finally {
            monitor.logRealtimeStatus(intersection, trafficLight);
            monitor.printFinalReport();
        }
    }
}
