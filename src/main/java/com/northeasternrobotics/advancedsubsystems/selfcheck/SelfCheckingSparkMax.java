package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.northeasternrobotics.advancedsubsystems.SubsystemFault;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;

import java.util.ArrayList;
import java.util.List;

/**
 * Spark Max that automatically checks itself for faults.
 */
public class SelfCheckingSparkMax implements SelfChecking {
    private final String label;
    private final CANSparkMax spark;

    /**
     * Creates a new SelfCheckingSparkMax.
     *
     * @param label The name of the device
     * @param spark The device
     */
    public SelfCheckingSparkMax(String label, CANSparkMax spark) {
        this.label = label;
        this.spark = spark;
    }

    @Override
    public List<SubsystemFault> checkForFaults() {
        ArrayList<SubsystemFault> faults = new ArrayList<>();

        REVLibError err = spark.getLastError();
        if (err != REVLibError.kOk) {
            faults.add(new SubsystemFault(String.format("[%s]: Error: %s", label, err.name())));
        }

        return faults;
    }
}
