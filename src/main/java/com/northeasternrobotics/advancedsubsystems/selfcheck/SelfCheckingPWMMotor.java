package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.northeasternrobotics.advancedsubsystems.SubsystemFault;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;

import java.util.ArrayList;
import java.util.List;

/**
 * PWM Motor Controller that automatically checks itself for faults.
 */
public class SelfCheckingPWMMotor implements SelfChecking {
    private final String label;
    private final PWMMotorController motor;

    /**
     * Creates a new SelfCheckingPWMMotor.
     *
     * @param label The name of the device
     * @param motor The device
     */
    public SelfCheckingPWMMotor(String label, PWMMotorController motor) {
        this.label = label;
        this.motor = motor;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        if (!motor.isAlive()) {
            faults.add(new SubsystemFault(String.format("[%s]: Device timed out", label)));
        }

        return faults;
    }
}