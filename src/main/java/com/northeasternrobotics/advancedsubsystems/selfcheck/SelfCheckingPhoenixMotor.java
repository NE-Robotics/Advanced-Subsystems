package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.ArrayList;
import java.util.List;

/**
 * Phoenix Motor Controller that automatically checks itself for faults.
 */
public class SelfCheckingPhoenixMotor implements SelfChecking {
    private final String label;
    private final BaseMotorController motor;

    /**
     * Creates a new SelfCheckingNavX.
     *
     * @param label The name of the device
     * @param motor The device
     */
    public SelfCheckingPhoenixMotor(String label, BaseMotorController motor) {
        this.label = label;
        this.motor = motor;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        List<SubsystemFault> faults = new ArrayList<>();

        Faults f = new Faults();
        motor.getFaults(f);

        if (f.HardwareFailure) {
            faults.add(new SubsystemFault(String.format("[%s]: Hardware failure detected", label)));
        }
        if (f.ResetDuringEn) {
            faults.add(new SubsystemFault(String.format("[%s]: Device booted while enabled", label)));
        }
        if (f.SensorOutOfPhase) {
            faults.add(new SubsystemFault(String.format("[%s]: Sensor out of phase", label), true));
        }
        if (f.RemoteLossOfSignal) {
            faults.add(new SubsystemFault(String.format("[%s]: Lost signal from remote sensor", label)));
        }
        if (f.UnderVoltage) {
            faults.add(new SubsystemFault(String.format("[%s]: Under voltage", label), true));
        }

        ErrorCode err = motor.getLastError();
        if (err != ErrorCode.OK) {
            faults.add(
                    new SubsystemFault(
                            String.format("[%s]: Error Code (%s)", label, err.name()), err.value > 0));
        }

        return faults;
    }
}
