package com.northeasternrobotics.advancedsubsystems;

import edu.wpi.first.wpilibj.Timer;

/**
 * A fault detected by a subsystem.
 */
public class SubsystemFault {
    /**
     * A description of the fault.
     */
    public final String description;
    /**
     * The time at which the fault was detected.
     */
    public final double timestamp;
    /**
     * Whether the fault is a warning or an error.
     */
    public final boolean isWarning;
    /**
     * Whether the fault is sticky.
     */
    public final boolean sticky;

    /**
     * Creates a new SubsystemFault.
     *
     * @param description A description of the fault.
     * @param isWarning   Whether the fault is a warning or an error.
     */
    public SubsystemFault(String description, boolean isWarning) {
        this(description, isWarning, false);
    }

    /**
     * Creates a new SubsystemFault.
     *
     * @param description A description of the fault.
     */
    public SubsystemFault(String description) {
        this(description, false);
    }

    /**
     * Creates a new SubsystemFault
     *
     * @param description A description of the fault.
     * @param isWarning   Whether the fault is a warning or an error.
     * @param sticky      Whether the fault is sticky.
     */
    public SubsystemFault(String description, boolean isWarning, boolean sticky) {
        this.description = description;
        this.timestamp = Timer.getFPGATimestamp();
        this.isWarning = isWarning;
        this.sticky = sticky;
    }

    /**
     * Checks if an objects is equal.
     *
     * @param other The object to compare to.
     * @return Whether the objects are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other instanceof SubsystemFault) {
            SubsystemFault o = (SubsystemFault) other;

            return description.equals(o.description) && isWarning == o.isWarning;
        }
        return false;
    }
}