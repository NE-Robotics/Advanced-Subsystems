package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_Faults;
import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.ArrayList;
import java.util.List;

/**
 * Pigeon IMU that automatically checks itself for faults.
 */
public class SelfCheckingPigeonIMU implements SelfChecking {
    private final String label;
    private final PigeonIMU pigeon;

    /**
     * Creates a new SelfCheckingPigeonIMU.
     *
     * @param label  The name of the device
     * @param pigeon The device
     */
    public SelfCheckingPigeonIMU(String label, PigeonIMU pigeon) {
        this.label = label;
        this.pigeon = pigeon;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        PigeonIMU_Faults f = new PigeonIMU_Faults();
        pigeon.getFaults(f);

        if (f.hasAnyFault()) {
            faults.add(new SubsystemFault(String.format("[%s]: Hardware fault detected", label)));
        }

        ErrorCode err = pigeon.getLastError();
        if (err != ErrorCode.OK) {
            faults.add(
                    new SubsystemFault(
                            String.format("[%s]: Error Code (%s)", label, err.name()), err.value > 0));
        }

        return faults;
    }
}
