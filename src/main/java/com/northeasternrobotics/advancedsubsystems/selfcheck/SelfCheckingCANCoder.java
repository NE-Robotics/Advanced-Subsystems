package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderFaults;
import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.ArrayList;
import java.util.List;

/**
 * CAN Coder that automatically checks itself for faults.
 */
public class SelfCheckingCANCoder implements SelfChecking {
    private final String label;
    private final CANCoder canCoder;

    /**
     * Creates a new SelfCheckingCANCoder.
     *
     * @param label    The name of the device
     * @param canCoder The device
     */
    public SelfCheckingCANCoder(String label, CANCoder canCoder) {
        this.label = label;
        this.canCoder = canCoder;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        CANCoderFaults f = new CANCoderFaults();
        canCoder.getFaults(f);

        if (f.HardwareFault) {
            faults.add(new SubsystemFault(String.format("[%s]: Hardware fault detected", label)));
        }
        if (f.ResetDuringEn) {
            faults.add(new SubsystemFault(String.format("[%s]: Device booted while enabled", label)));
        }
        if (f.MagnetTooWeak) {
            faults.add(new SubsystemFault(String.format("[%s]: Magnet too weak", label)));
        }

        ErrorCode err = canCoder.getLastError();
        if (err != ErrorCode.OK) {
            faults.add(
                    new SubsystemFault(
                            String.format("[%s]: Error Code (%s)", label, err.name()), err.value > 0));
        }

        return faults;
    }
}
