package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.kauailabs.navx.frc.AHRS;
import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.ArrayList;
import java.util.List;

/**
 * Nav X that automatically checks itself for faults.
 */
public class SelfCheckingNavX implements SelfChecking {
    private final String label;
    private final AHRS navx;

    /**
     * Creates a new SelfCheckingNavX.
     *
     * @param label The name of the device
     * @param navx  The device
     */
    public SelfCheckingNavX(String label, AHRS navx) {
        this.label = label;
        this.navx = navx;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        if (!navx.isConnected()) {
            faults.add(new SubsystemFault(String.format("[%s]: NavX is disconnected", label)));
        }
        if (navx.isCalibrating()) {
            faults.add(new SubsystemFault(String.format("[%s]: NavX is calibrating", label)));
        }
        if (!navx.isMagnetometerCalibrated()) {
            faults.add(new SubsystemFault(String.format("[%s]: NavX magnetometer is not calibrated", label), true));
        }
        if (navx.isMagneticDisturbance()) {
            faults.add(new SubsystemFault(String.format("[%s]: NavX is experiencing a magnetic disturbance", label), true));
        }
        if (navx.isAltitudeValid()) {
            faults.add(new SubsystemFault(String.format("[%s]: NavX altitude is invalid", label)));
        }

        return faults;
    }
}
