package factory;

import engine.Intersection;
import engine.TrafficLight;
import engine.TrafficMonitor;
import entity.Direction;
import entity.PriorityVehicle;
import entity.StandardVehicle;
import entity.Vehicle;
import entity.VehicleType;
import java.util.concurrent.ThreadLocalRandom;
import util.IdGenerator;
import util.SimulationLogger;

public class VehicleFactory {
    private final IdGenerator idGenerator;

    public VehicleFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Vehicle createRandomVehicle(
            Intersection intersection,
            TrafficLight trafficLight,
            TrafficMonitor monitor,
            SimulationLogger logger
    ) {
        VehicleType vehicleType = pickRandomType();
        Direction direction = Direction.random();
        int speedKph = vehicleType.randomSpeedKph();
        long approachDelayMillis = Math.max(300L, Math.round(72_000.0 / speedKph));
        long crossingDurationMillis = calculateCrossingDurationMillis(vehicleType, speedKph);
        return createVehicle(
                vehicleType,
                direction,
                speedKph,
                approachDelayMillis,
                crossingDurationMillis,
                intersection,
                trafficLight,
                monitor,
                logger
        );
    }

    public Vehicle createVehicle(
            VehicleType vehicleType,
            Direction direction,
            int speedKph,
            long approachDelayMillis,
            long crossingDurationMillis,
            Intersection intersection,
            TrafficLight trafficLight,
            TrafficMonitor monitor,
            SimulationLogger logger
    ) {
        String vehicleId = idGenerator.nextId(vehicleType.getCode());
        if (vehicleType.isPriorityType()) {
            return new PriorityVehicle(
                    vehicleId,
                    vehicleType,
                    direction,
                    speedKph,
                    approachDelayMillis,
                    crossingDurationMillis,
                    intersection,
                    trafficLight,
                    monitor,
                    logger
            );
        }

        return new StandardVehicle(
                vehicleId,
                vehicleType,
                direction,
                speedKph,
                approachDelayMillis,
                crossingDurationMillis,
                intersection,
                trafficLight,
                monitor,
                logger
        );
    }

    private VehicleType pickRandomType() {
        int pick = ThreadLocalRandom.current().nextInt(100);
        if (pick < 30) {
            return VehicleType.MOTORBIKE;
        }
        if (pick < 60) {
            return VehicleType.CAR;
        }
        if (pick < 85) {
            return VehicleType.TRUCK;
        }
        return VehicleType.AMBULANCE;
    }

    private long calculateCrossingDurationMillis(VehicleType vehicleType, int speedKph) {
        long baseDuration = Math.max(350L, Math.round(30_000.0 / speedKph));
        if (vehicleType == VehicleType.TRUCK) {
            return baseDuration + 250L;
        }
        if (vehicleType.isPriorityType()) {
            return Math.max(280L, baseDuration - 120L);
        }
        return baseDuration;
    }
}
