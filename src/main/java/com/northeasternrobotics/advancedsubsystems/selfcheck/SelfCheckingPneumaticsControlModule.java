package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.northeasternrobotics.advancedsubsystems.SubsystemFault;
import edu.wpi.first.wpilibj.PneumaticsControlModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Pneumatics Control Module that automatically checks itself for faults.
 */
public class SelfCheckingPneumaticsControlModule implements SelfChecking {
    private final String label;
    private final PneumaticsControlModule pneumaticsControlModule;

    /**
     * Creates a new SelfCheckingPneumaticsControlModule.
     *
     * @param label                   The name of the device
     * @param pneumaticsControlModule The device
     */
    public SelfCheckingPneumaticsControlModule(String label, PneumaticsControlModule pneumaticsControlModule) {
        this.label = label;
        this.pneumaticsControlModule = pneumaticsControlModule;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        if (pneumaticsControlModule.getCompressorNotConnectedFault()) {
            faults.add(new SubsystemFault(String.format("[%s]: Compressor not connected", label)));
        }
        if (pneumaticsControlModule.getCompressorShortedFault()) {
            faults.add(new SubsystemFault(String.format("[%s]: Compressor shorted", label)));
        }
        if (pneumaticsControlModule.getCompressorCurrentTooHighFault()) {
            faults.add(new SubsystemFault(String.format("[%s]: Compressor current too high", label)));
        }
        if (pneumaticsControlModule.getSolenoidVoltageFault()) {
            faults.add(new SubsystemFault(String.format("[%s]: Solenoid voltage fault", label)));
        }

        return faults;
    }
}
