package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.northeasternrobotics.advancedsubsystems.SubsystemFault;
import edu.wpi.first.wpilibj.PowerDistribution;

import java.util.ArrayList;
import java.util.List;

/**
 * Power Distribution system that automatically checks itself for faults.
 */
public class SelfCheckingPowerDistribution implements SelfChecking {
    private final String label;
    private final PowerDistribution powerDistribution;

    /**
     * Creates a new SelfCheckingPowerDistribution.
     *
     * @param label             The name of the device
     * @param powerDistribution The device
     */
    public SelfCheckingPowerDistribution(String label, PowerDistribution powerDistribution) {
        this.label = label;
        this.powerDistribution = powerDistribution;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        if (powerDistribution.getFaults().HardwareFault) {
            faults.add(new SubsystemFault(String.format("[%s]: Hardware fault detected", label)));
        }
        if (powerDistribution.getFaults().Brownout) {
            faults.add(new SubsystemFault(String.format("[%s]: Brownout detected", label), true));
        }
        if (powerDistribution.getFaults().CanWarning) {
            faults.add(new SubsystemFault(String.format("[%s]: CAN warning detected", label), true));
        }

        return faults;
    }
}
