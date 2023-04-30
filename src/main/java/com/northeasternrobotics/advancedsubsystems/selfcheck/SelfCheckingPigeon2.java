package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.sensors.Pigeon2_Faults;
import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.ArrayList;
import java.util.List;

/**
 * Pigeon 2 that automatically checks itself for faults.
 */
public class SelfCheckingPigeon2 implements SelfChecking {
    private final String label;
    private final Pigeon2 pigeon;

    /**
     * Creates a new SelfCheckingPigeon2.
     *
     * @param label  The name of the device
     * @param pigeon The device
     */
    public SelfCheckingPigeon2(String label, Pigeon2 pigeon) {
        this.label = label;
        this.pigeon = pigeon;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        Pigeon2_Faults f = new Pigeon2_Faults();
        pigeon.getFaults(f);

        if (f.HardwareFault) {
            faults.add(new SubsystemFault(String.format("[%s]: Hardware fault detected", label)));
        }
        if (f.ResetDuringEn) {
            faults.add(new SubsystemFault(String.format("[%s]: Device booted while enabled", label)));
        }
        if (f.BootIntoMotion) {
            faults.add(new SubsystemFault(String.format("[%s]: Device booted while in motion", label)));
        }
        if (f.GyroFault) {
            faults.add(new SubsystemFault(String.format("[%s]: Gyro fault detected", label)));
        }
        if (f.AccelFault) {
            faults.add(new SubsystemFault(String.format("[%s]: Accelerometer fault detected", label)));
        }
        if (f.UnderVoltage) {
            faults.add(new SubsystemFault(String.format("[%s]: Under voltage detected", label), true));
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
