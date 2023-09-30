package dev.tenacity.anticheat;

import dev.tenacity.anticheat.checks.combat.*;
import dev.tenacity.anticheat.checks.movement.flight.*;
import dev.tenacity.anticheat.checks.movement.speed.*;
import dev.tenacity.anticheat.checks.movement.sprint.*;
import dev.tenacity.anticheat.checks.player.*;

import java.util.ArrayList;
import java.util.Arrays;

public class DetectionManager {

    private ArrayList<Detection> detections = new ArrayList<>();

    public DetectionManager() {
        addDetections(

                // Combat // Velocity
                new VelocityA(),

                // Movement // Flight
                new FlightA(),
                new FlightB(),

                // Movmeent // Sprint
                new SprintA(),
                new SprintB(),

                // Movmeent // Speed
                new SpeedA(),

                // Player // Timer
                new TimerA()

        );
    }

    public void addDetections(Detection... detections) {
        this.detections.addAll(Arrays.asList(detections));
    }

    public ArrayList<Detection> getDetections() {
        return detections;
    }
}
