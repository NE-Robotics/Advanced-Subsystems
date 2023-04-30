package com.northeasternrobotics.advancedsubsystems;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenixpro.hardware.CANcoder;
import com.kauailabs.navx.frc.AHRS;
import com.northeasternrobotics.advancedsubsystems.selfcheck.*;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A subsystem that can check for faults in its hardware and publish their status
 */
public abstract class AdvancedSubsystem extends SubsystemBase {
    private final List<SubsystemFault> faults = new ArrayList<>();
    private final List<SelfChecking> hardware = new ArrayList<>();
    private final String statusTable;
    private final boolean checkErrors;
    private final BiConsumer<Runnable, Double> periodicCallback;

    /**
     * @param periodicCallback By default, pass in TimedRobot.addPeriodic
     *                         (Runnable, double) to run the periodic checks
     *                         for faults and publish status without running
     *                         every single loop cycle to conserve CPU time.
     */
    public AdvancedSubsystem(BiConsumer<Runnable, Double> periodicCallback) {
        this.periodicCallback = periodicCallback;
        this.statusTable = "SystemStatus/" + this.getName();
        CommandBase systemCheck = getSystemCheckCommand();
        systemCheck.setName(getName() + "Check");
        SmartDashboard.putData(statusTable + "/SystemCheck", systemCheck);
        SmartDashboard.putBoolean(statusTable + "/CheckRan", false);
        checkErrors = RobotBase.isReal();

        setupCallbacks();
    }

    /**
     * @param periodicCallback By default, pass in TimedRobot.addPeriodic
     *                         (Runnable, double) to run the periodic checks
     *                         for faults and publish status without running
     *                         every single loop cycle to conserve CPU time.
     * @param name             The name of the subsystem to associate the hardware with.
     */
    public AdvancedSubsystem(BiConsumer<Runnable, Double> periodicCallback, String name) {
        this.periodicCallback = periodicCallback;
        this.setName(name);
        this.statusTable = "SystemStatus/" + name;
        CommandBase systemCheck = getSystemCheckCommand();
        systemCheck.setName(getName() + "Check");
        SmartDashboard.putData(statusTable + "/SystemCheck", systemCheck);
        SmartDashboard.putBoolean(statusTable + "/CheckRan", false);
        checkErrors = RobotBase.isReal();

        setupCallbacks();
    }

    /**
     * @return The command to check system status
     */
    public CommandBase getSystemCheckCommand() {
        return Commands.sequence(
                Commands.runOnce(
                        () -> {
                            SmartDashboard.putBoolean(statusTable + "/CheckRan", false);
                            clearFaults();
                            publishStatus();
                        }),
                systemCheckCommand(),
                Commands.runOnce(
                        () -> {
                            publishStatus();
                            SmartDashboard.putBoolean(statusTable + "/CheckRan", true);
                        }));
    }

    private void setupCallbacks() {
        periodicCallback.accept(this::checkForFaults, 0.25);
        periodicCallback.accept(this::publishStatus, 1.0);
    }

    /**
     * Publishes the status of the AdvancedSubsystem to SmartDashboard.
     */
    private void publishStatus() {
        SystemStatus status = getSystemStatus();
        SmartDashboard.putString(statusTable + "/Status", status.name());
        SmartDashboard.putBoolean(statusTable + "/SystemOK", status == SystemStatus.OK);

        String[] faultStrings = new String[this.faults.size()];
        for (int i = 0; i < this.faults.size(); i++) {
            SubsystemFault fault = this.faults.get(i);
            faultStrings[i] = String.format("[%.2f] %s", fault.timestamp, fault.description);
        }
        SmartDashboard.putStringArray(statusTable + "/Faults", faultStrings);

        if (faultStrings.length > 0) {
            SmartDashboard.putString(statusTable + "/LastFault", faultStrings[faultStrings.length - 1]);
        } else {
            SmartDashboard.putString(statusTable + "/LastFault", "");
        }
    }

    /**
     * @param fault The fault to add to the AdvancedSubsystem
     */
    protected void addFault(SubsystemFault fault) {
        if (!this.faults.contains(fault)) {
            this.faults.add(fault);
        }
    }

    /**
     * Checks for faults in the AdvancedSubsystem. This method is called periodically by
     *
     * @param description The description of the fault
     * @param isWarning   Whether the fault is a warning or an error
     */
    protected void addFault(String description, boolean isWarning) {
        this.addFault(new SubsystemFault(description, isWarning));
    }

    /**
     * @param description The description of the fault
     * @param isWarning   Whether the fault is a warning or an error
     * @param sticky      Whether the fault should be cleared after a certain amount of time
     */
    protected void addFault(String description, boolean isWarning, boolean sticky) {
        this.addFault(new SubsystemFault(description, isWarning, sticky));
    }

    /**
     * @param description The description of the fault
     */
    protected void addFault(String description) {
        this.addFault(description, false);
    }

    /**
     * @return A list of all recorded faults in an AdvancedSubsystem
     */
    public List<SubsystemFault> getFaults() {
        return this.faults;
    }

    /**
     * Clears recorded faults in an AdvancedSubsystem
     */
    public void clearFaults() {
        this.faults.clear();
    }

    /**
     * Check for faults in the AdvancedSubsystem
     *
     * @return The worst status of the devices in the AdvancedSubsystem
     */
    public SystemStatus getSystemStatus() {
        SystemStatus worstStatus = SystemStatus.OK;

        for (SubsystemFault f : this.faults) {
            if (f.sticky || f.timestamp > Timer.getFPGATimestamp() - 10) {
                if (f.isWarning) {
                    if (worstStatus != SystemStatus.ERROR) {
                        worstStatus = SystemStatus.WARNING;
                    }
                } else {
                    worstStatus = SystemStatus.ERROR;
                }
            }
        }
        return worstStatus;
    }

    /**
     * Register a base TalonSRX or VictorSPX for self-checking
     *
     * @param label        The name of the hardware to be registered
     * @param phoenixMotor The TalonSRX or VictorSPX to be registered
     */
    public void registerHardware(String label, BaseMotorController phoenixMotor) {
        hardware.add(new SelfCheckingPhoenixMotor(label, phoenixMotor));
    }

    /**
     * Register a TalonFX for self-checking
     *
     * @param label The name of the hardware to be registered
     * @param talon The TalonFX to be registered
     */
    public void registerHardware(String label, com.ctre.phoenixpro.hardware.TalonFX talon) {
        hardware.add(new SelfCheckingTalonFXPro(label, talon));
    }

    /**
     * Register a PWM Motor Controller for self-checking
     *
     * @param label    The name of the hardware to be registered
     * @param pwmMotor The PWM Motor Controller to be registered
     */
    public void registerHardware(String label, PWMMotorController pwmMotor) {
        hardware.add(new SelfCheckingPWMMotor(label, pwmMotor));
    }

    /**
     * Register a CANSparkMax for self-checking
     *
     * @param label The name of the hardware to be registered
     * @param spark The CANSparkMax to be registered
     */
    public void registerHardware(String label, CANSparkMax spark) {
        hardware.add(new SelfCheckingSparkMax(label, spark));
    }

    /**
     * Register a Pigeon2 for self-checking
     *
     * @param label   The name of the hardware to be registered
     * @param pigeon2 The Pigeon2 to be registered
     */
    public void registerHardware(String label, com.ctre.phoenix.sensors.Pigeon2 pigeon2) {
        hardware.add(new SelfCheckingPigeon2(label, pigeon2));
    }

    /**
     * Register a Pigeon2Pro for self-checking
     *
     * @param label   The name of the hardware to be registered
     * @param pigeon2 The Pigeon2Pro to be registered
     */
    public void registerHardware(String label, com.ctre.phoenixpro.hardware.Pigeon2 pigeon2) {
        hardware.add(new SelfCheckingPigeon2Pro(label, pigeon2));
    }

    /**
     * Register a PigeonIMU for self-checking
     *
     * @param label  The name of the hardware to be registered
     * @param pigeon The PigeonIMU to be registered
     */
    public void registerHardware(String label, PigeonIMU pigeon) {
        hardware.add(new SelfCheckingPigeonIMU(label, pigeon));
    }

    /**
     * Register a CANCoder for self-checking
     *
     * @param label    The name of the hardware to be registered
     * @param canCoder The CANCoder to be registered
     */
    public void registerHardware(String label, CANCoder canCoder) {
        hardware.add(new SelfCheckingCANCoder(label, canCoder));
    }

    /**
     * Register a CANCoderPro for self-checking
     *
     * @param label    The name of the hardware to be registered
     * @param canCoder The CANCoderPro to be registered
     */
    public void registerHardware(String label, CANcoder canCoder) {
        hardware.add(new SelfCheckingCANCoderPro(label, canCoder));
    }

    /**
     * Register a Power Distribution Module for self-checking
     *
     * @param label             The name of the hardware to be registered
     * @param powerDistribution The Power Distribution Module to be registered
     */
    public void registerHardware(String label, PowerDistribution powerDistribution) {
        hardware.add(new SelfCheckingPowerDistribution(label, powerDistribution));
    }

    /**
     * Register a PCM for self-checking
     *
     * @param label                   The name of the hardware to be registered
     * @param pneumaticsControlModule The PCM to be registered
     */
    public void registerHardware(String label, PneumaticsControlModule pneumaticsControlModule) {
        hardware.add(new SelfCheckingPneumaticsControlModule(label, pneumaticsControlModule));
    }

    /**
     * Register a NavX for self-checking
     *
     * @param label The name of the hardware to be registered
     * @param navx  The NavX to be registered
     */
    public void registerHardware(String label, AHRS navx) {
        hardware.add(new SelfCheckingNavX(label, navx));
    }

    /**
     * Command to run a full systems check
     *
     * @return A command to run a full systems check
     */
    protected abstract CommandBase systemCheckCommand();

    // Method to check for faults while the robot is operating normally
    private void checkForFaults() {
        if (checkErrors) {
            for (SelfChecking device : hardware) {
                for (SubsystemFault fault : device.checkForFaults()) {
                    addFault(fault);
                }
            }
        }
    }

    /**
     * The status of the AdvancedSubsystem
     */
    public enum SystemStatus {
        /**
         * No faults detected
         */
        OK,
        /**
         * A fault that is recoverable without a robot restart occurred
         */
        WARNING,
        /**
         * A fault that is not recoverable without a robot restart occurred
         */
        ERROR
    }
}