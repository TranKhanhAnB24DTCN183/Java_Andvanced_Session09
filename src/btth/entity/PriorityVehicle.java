package entity;

import engine.Intersection;
import engine.TrafficLight;
import engine.TrafficMonitor;
import util.SimulationLogger;

public class PriorityVehicle extends Vehicle {
    public PriorityVehicle(
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
        super(
                id,
                type,
                direction,
                speedKph,
                approachDelayMillis,
                crossingDurationMillis,
                intersection,
                trafficLight,
                monitor,
                logger
        );
        if (!type.isPriorityType()) {
            throw new IllegalArgumentException("PriorityVehicle chi duoc dung cho xe uu tien.");
        }
    }

    @Override
    public boolean hasPriority() {
        return true;
    }
}
