# Advanced Subsystems [![Gradle Package](https://github.com/NE-Robotics/Advanced-Subsystems/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/NE-Robotics/Advanced-Subsystems/actions/workflows/gradle-publish.yml)

Self checking hardware in subsystems, based on 3015's 2023 code

## Installation
Hop on over to build.gradle inside of repositories add
```gradle
maven {
        url = uri("https://maven.pkg.github.com/NE-Robotics/Advanced-Subsystems")
        credentials {
            username = "Mechanical-Advantage-Bot"
            password = "\u0067\u0068\u0070\u005f\u006e\u0056\u0051\u006a\u0055\u004f\u004c\u0061\u0079\u0066\u006e\u0078\u006e\u0037\u0051\u0049\u0054\u0042\u0032\u004c\u004a\u006d\u0055\u0070\u0073\u0031\u006d\u0037\u004c\u005a\u0030\u0076\u0062\u0070\u0063\u0051"
        }
}
```
and inside of dependencies add
```gradle
implementation 'com.northeasternrobotics.advancedsubsystems:advancedsubsystems:0.0.+'
```
If the release isn't available just clone the repository & run ./gradlew publishToMavenLocal

## Use
#### Creating an Advanced Subsystem
When using SubyststemBase in your robot code swap in AdvancedSubsystem, in the creation of the subsystem you will need to pass in a BiConsumer of a Runnable & Double by default when using TimedRobot this should just your Robot.java instance/singleton::addPeriodic & a String for the subsystem name. 

Finally you need to impliment a systemCheckCommand(); This will include all other checks you want for the subsystem, if you do not want any others set it to Command.none;

#### Registering Hardware
To register hardware for an AdvancedSubsystem for automated hardware checks just add registerHardware(String name, hardware *NavX, Talon FX, etc*); for each device in a subsystem

## How It Works
All registered subsystems appear under Smartdashboard/SystemStatus/Name in network tables with data for if checks have run, if there are errors in the subsystem hardware, and more. This can integrate easily with 3015's pit display system with an open fork of it in development https://github.com/Alex-idk/frc_team_pit_display with plants for improved support and dynamic addition of subsystems instead of having them hardcoded. Feel free to build any other system around this data and share it with others.

## Notes
- Feature requests are more than welcomed
- Support is available if you're having any issues
- If you are using AdvantageKit LoggedRobot does not have a periodic callback by default but we have a fork which supports this for any teams needing this, or you can add your own callback system.
- yes I'm borrowing Mechanical Advantage's bot so anyone can use this, you can also put your own username and a token with package read privileges if you would prefer
