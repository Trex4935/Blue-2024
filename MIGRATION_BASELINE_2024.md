# Blue-2024 baseline record

## Repository state

- Upstream: `https://github.com/Trex4935/Blue-2024`
- Baseline branch: `main`
- Baseline commit: `c85e72442d233c2a58e24c0618648cacc53b44e6`
- Commit date: `2024-11-21T18:36:43-05:00`
- Baseline working tree: clean
- Migration branch: `migration/wpilib-2026`, created from the baseline commit
- The separately supplied `Blue-2024-Imported` directory contains no `.git` metadata. Its Java files match the baseline Java sources byte-for-byte; its generated build files are treated only as migration-template references.

## Baseline file inventory

- Build/configuration: `build.gradle`, `settings.gradle`, `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat`, `.wpilib/wpilib_preferences.json`, `vendordeps/Phoenix5.json`, `vendordeps/Phoenix6.json`, `vendordeps/WPILibNewCommands.json`, `.github/workflows/build_gradle.yml`, `.gitignore`, `.vscode/launch.json`, `.vscode/settings.json`, `WPILib-License.md`
- Java: `src/main/java/frc/robot/{Constants,Main,Robot,RobotContainer}.java`; `src/main/java/frc/robot/commands/{MoveShooterDown,MoveShooterUp,ShooterMagToggle,ShooterSolenoidToggle}.java`; `src/main/java/frc/robot/extensions/Talon.java`; `src/main/java/frc/robot/subsystems/{Drivetrain,ExampleSubsystem,Shooter}.java`
- Deploy: `src/main/deploy/example.txt`
- No test source files were present at baseline.

## Behavior snapshot

- Entry point: `frc.robot.Main` starts `Robot`; `Robot` extends `TimedRobot`, runs `CommandScheduler` in `robotPeriodic`, schedules the container's autonomous command only when non-null, cancels it at teleop start, and cancels all commands at test start. The selected autonomous command is null.
- Operator devices: Xbox controller port 0; dance pad `CommandJoystick` port 1.
- Drivetrain: differential drive with eight Talon SRX controllers. Left IDs 11–14 and right IDs 21–24. Motors 12–14 follow 11; motors 22–24 follow 21. All right motors are inverted; left motors are not explicitly inverted. Masters feed `DifferentialDrive`.
- Talon configuration: factory default, PercentOutput zero, Brake neutral mode, open-loop ramp 0.2 s, nominal outputs 0/0, peak outputs +1/-1, current-limit config `(enabled=false, limit=20, trigger=60, triggerTime=0.2)`. Declared neutral deadband is 0.04 but is not applied in `createTalon`.
- Drivetrain commands: tank forward `(0.5, 0.5)`, backward `(-0.5, -0.5)`, left rotation `(-0.8, 0.8)`, right rotation `(0.8, -0.8)`. Each movement is a `runEnd` command whose end action stops the two drive masters. Default tank command repeatedly reads Xbox left Y/right Y and is assigned twice in the constructor. Scheduler disable/enable is triggered by dance-pad buttons 5/10 using `onTrue`.
- Shooter: aiming Talon SRX ID 2, inverted true, same default Talon configuration. CTRE PCM ID 1; firing solenoid channel 1 and magazine solenoid channel 0. Firing sequence: fire solenoid true, blocking delay 0.15 s, fire solenoid false, magazine solenoid false, blocking delay 0.4 s, magazine solenoid true. Shooter-aim commands output +0.1 and -0.1, stopping on command end.
- Bindings: Xbox POV up/down runs aim up/down; left/right bumpers drive forward/backward; A fires on `onTrue`; POV left/right rotates. Dance-pad 7/8 aim; 3/2 drive; 5/10 scheduler disable/enable on `onTrue`; 9 fires on `onTrue`; 1/4 rotate. Movement and aiming are `whileTrue` bindings.
- Source files contain other public command classes and methods; baseline behavior is left intact unless a compiler error proves a source correction is required.

## Known issues preserved for review

- Shooter firing blocks the robot scheduler thread for a total of 0.55 s by design in this source; the requested migration preserves this sequence.
- Current limiting is configured with `enableCurrentLimit=false`; it is preserved as an existing configuration choice.
- `neutralDeadband=0.04` is declared but not passed to the controller; preserved.
- `ShooterSolenoidToggle` calls `ShooterMag_Toggle()` rather than toggling the firing solenoid; the class is not bound in `RobotContainer`; preserved.
- `MoveShooterUp` and `MoveShooterDown` issue a motor output in their constructors rather than in a scheduler lifecycle method; preserved.
- `stopMotors()` stops only the differential-drive masters; followers depend on master behavior; preserved.
- The code contains unused helpers/imports and duplicate default-command assignment; preserved.

No hardware behavior was exercised while recording this baseline.
