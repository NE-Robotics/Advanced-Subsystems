package com.northeasternrobotics.advancedsubsystems.selfcheck;

import com.northeasternrobotics.advancedsubsystems.SubsystemFault;

import java.util.List;

/**
 * A device that can check itself for faults.
 */
public interface SelfChecking {
    /**
     * @return a list of faults detected by this device.
     */
    List<SubsystemFault> checkForFaults();
}