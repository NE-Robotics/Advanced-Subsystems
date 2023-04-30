package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.ctre.phoenixpro.StatusCode;
import com.ctre.phoenixpro.StatusSignalValue;
import com.ctre.phoenixpro.hardware.Pigeon2;
import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.ArrayList;
import java.util.List;

/**
 * Pigeon 2 Pro that automatically checks itself for faults.
 */
public class SelfCheckingPigeon2Pro implements SelfChecking {
    private final String label;
    private final StatusSignalValue<Integer> firmwareVersionSignal;
    private final StatusSignalValue<Boolean> hardwareFaultSignal;
    private final StatusSignalValue<Boolean> bootEnabledSignal;
    private final StatusSignalValue<Boolean> bootMotionSignal;
    private final StatusSignalValue<Boolean> accelFaultSignal;
    private final StatusSignalValue<Boolean> gyroFaultSignal;
    private final StatusSignalValue<Boolean> undervoltageFaultSignal;

    /**
     * Creates a new SelfCheckingPigeon2Pro.
     *
     * @param label  The name of the device
     * @param pigeon The device
     */
    public SelfCheckingPigeon2Pro(String label, Pigeon2 pigeon) {
        this.label = label;

        this.firmwareVersionSignal = pigeon.getVersion();
        this.hardwareFaultSignal = pigeon.getFault_Hardware();
        this.bootEnabledSignal = pigeon.getFault_BootDuringEnable();
        this.bootMotionSignal = pigeon.getFault_BootIntoMotion();
        this.accelFaultSignal = pigeon.getFault_BootupAccelerometer();
        this.gyroFaultSignal = pigeon.getFault_BootupGyroscope();
        this.undervoltageFaultSignal = pigeon.getFault_Undervoltage();
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        if (firmwareVersionSignal.refresh().getError() != StatusCode.OK) {
            faults.add(new SubsystemFault(String.format("[%s]: No communication with device", label)));
        }
        if (hardwareFaultSignal.refresh().getValue()) {
            faults.add(new SubsystemFault(String.format("[%s]: Hardware fault detected", label)));
        }
        if (bootEnabledSignal.refresh().getValue()) {
            faults.add(new SubsystemFault(String.format("[%s]: Device booted while enabled", label)));
        }
        if (bootMotionSignal.refresh().getValue()) {
            faults.add(new SubsystemFault(String.format("[%s]: Device booted while in motion", label)));
        }
        if (accelFaultSignal.refresh().getValue()) {
            faults.add(
                    new SubsystemFault(String.format("[%s]: Accelerometer boot checks failed", label)));
        }
        if (gyroFaultSignal.refresh().getValue()) {
            faults.add(new SubsystemFault(String.format("[%s]: Gyro boot checks failed", label)));
        }
        if (undervoltageFaultSignal.refresh().getValue()) {
            faults.add(new SubsystemFault(String.format("[%s]: Under voltage fault detected", label), true));
        }

        return faults;
    }
}
