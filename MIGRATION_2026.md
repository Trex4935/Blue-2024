# WPILib 2026 migration report

## Result

- Migration branch: `migration/wpilib-2026` (local only; not pushed)
- Baseline: `main` at `c85e72442d233c2a58e24c0618648cacc53b44e6`; initial working tree was clean.
- WPILib/GradleRIO target: GradleRIO `2026.2.1`, Gradle `8.11`, Java 17, team 4935, entry point `frc.robot.Main`.
- Build: successful with the local WPILib 2026 Temurin JDK `17.0.16`.
- Java source changes: none. Every Java file in this branch matches the baseline source. No behavior changes were made.
- Logical equivalence: **partially verified**. Source-level control logic is unchanged and compiles against the 2026 dependencies. Desktop build does not verify actual robot hardware behavior; no hardware was activated or tested.

The detailed pre-edit behavior record is in [MIGRATION_BASELINE_2024.md](MIGRATION_BASELINE_2024.md).

## Changes by class

### A. Build configuration migration

- `build.gradle`: GradleRIO `2024.3.2` → `2026.2.1`; added the current WPILib annotations processor and explicitly retained `deleteOldFiles = false` for deploy files. The original jar source layout and all existing task/dependency configurations were retained.
- `settings.gradle`: WPILib local Maven tooling path now uses year 2026; updated repository assignment syntax to match the generated 2026 project.
- `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar`: updated to the generated Gradle 8.11 wrapper.
- `.wpilib/wpilib_preferences.json`: project year set to 2026; team 4935 and Java project type preserved.
- `.github/workflows/build_gradle.yml`: CI continues to use Java 17 and now explicitly runs clean, Java compilation, tests, and build through the wrapper.

### B. Vendor dependency migration

- `vendordeps/Phoenix5-frc2026-latest.json`: official 2026 vendordep, CTRE Phoenix 5 `5.36.0`.
- `vendordeps/Phoenix6-frc2026-latest.json`: official 2026 vendordep, CTRE Phoenix 6 `26.3.0`; Phoenix 5 requires Phoenix 6 low-level libraries.
- `vendordeps/WPILibNewCommands.json`: updated to the generated WPILib 2026 vendordep. Resolved `wpilibNewCommands-java` version is `2026.2.1`, confirmed with Gradle `dependencyInsight`. Removing this vendordep caused the `edu.wpi.first.wpilibj2.command` package to be unavailable, so it is required and was restored before the successful build.
- The two 2024 CTRE JSON files were replaced by the official 2026 JSON files. Their dependency coordinates contain fixed versions (`5.36.0` and `26.3.0`); the `latest` text in the official JSON filenames/metadata refers to CTRE's vendordep feed endpoint.
- Existing JUnit dependency remains `org.junit.jupiter:junit-jupiter:5.10.1`.

### C. Necessary source compatibility corrections

- None. Talon SRX Phoenix 5 APIs used by the source, including `WPI_TalonSRX`, configured successfully against Phoenix 5 `5.36.0`.

### D. Documentation

- Added `MIGRATION_BASELINE_2024.md` before code/build edits and this report.

## Build and test evidence

All successful commands below were run from the migration branch with `JAVA_HOME=C:\Users\Public\wpilib\2026\jdk` (Temurin 17.0.16). No deploy task or hardware task was run.

| Command | Result |
|---|---|
| `gradlew.bat --version` | Gradle 8.11; launcher and daemon JVM 17.0.16 |
| `gradlew.bat clean compileJava` | `BUILD SUCCESSFUL`; one compiler warning that `Command.schedule()` is deprecated and marked for removal in WPILib 2026 |
| `gradlew.bat test` | `BUILD SUCCESSFUL`; `test NO-SOURCE`, because the original project has no test sources |
| `gradlew.bat build` | `BUILD SUCCESSFUL` |
| `gradlew.bat dependencyInsight --dependency wpilibNewCommands-java --configuration compileClasspath` | Resolved `edu.wpi.first.wpilibNewCommands:wpilibNewCommands-java:2026.2.1` |

The wrapper initially ran under a system Java 25 and Gradle 8.11 could not configure under that JVM. Rerunning with the installed WPILib Java 17 toolchain succeeded. This required only choosing the supported local JDK; no project Java compatibility setting was changed.

There were no baseline tests to extend. I did not add tests that would need to mock unabstracted hardware objects or alter production logic. The Gradle test task was run and accurately reported `NO-SOURCE`.

## Source and behavior comparison

Every Java source diff against baseline was reviewed. There are no Java source diffs. Therefore these remain exactly as in the baseline: eight Talon SRX IDs and follower groups, right-side inversion, brake/ramp/output/current-limit settings, differential drive and output values, the two duplicated Xbox tank-drive default assignments, all Xbox and dance-pad mappings and trigger semantics, shooter CAN/PCM/channel IDs, blocking 0.15 s and 0.4 s shooting sequence, subsystem requirements and command factories, scheduler enable/disable actions, null autonomous command, and TimedRobot lifecycle.

## Existing issues and safety concerns deliberately left unchanged

- Shooter firing blocks the command scheduler thread for approximately 0.55 s across its two delays.
- The default CTRE configuration sets current limiting disabled; this setting is unchanged.
- `neutralDeadband` is set to 0.04 in the helper configuration object but is not applied to controllers.
- `ShooterSolenoidToggle` calls the magazine toggle method; it is currently not bound in `RobotContainer`.
- `MoveShooterUp` and `MoveShooterDown` apply motor output in their constructors, and the public toggle command classes also act during construction; the bindings do not use those classes.
- Drive stop behavior calls `stopMotor()` on the two masters only; followers remain in their original follow relationships.
- The compiler reports that `Command.schedule()` is deprecated and marked for removal. The lifecycle call is retained unchanged to preserve scheduling behavior; this is a future upgrade concern, not a compile failure.
- No firmware changes were made. Existing Talon SRX and PCM firmware compatibility remains to be checked during supervised hardware validation.

## Supervised hardware validation checklist

This checklist is for a later supervised session. It was not performed as part of this migration.

1. Confirm robot wiring, Talon SRX CAN IDs 11–14, 21–24, and 2, CTRE PCM ID 1, pneumatic channels 1 and 0, and current firmware compatibility. Do not update firmware as part of this checklist without a separate approved plan.
2. Inspect drivetrain and shooter mechanisms, clear personnel and loose objects, secure the robot, and keep drive wheels off the floor for initial motor checks.
3. With the robot disabled, verify controller connection, expected device discovery, and absence of unintended actuator movement.
4. In a controlled low-risk setup, verify drivetrain master/follower behavior, left/right response, right-side inversion, and the original forward/backward ±0.5 and rotation ±0.8 commands.
5. Verify Xbox port 0 and dance-pad port 1 mappings one at a time, including `whileTrue` release behavior, the two `onTrue` firing bindings, and dance-pad scheduler disable/enable buttons 5/10.
6. Verify shooter aiming at +0.1 and -0.1 and that release invokes the original stop action.
7. With no projectile present and the pneumatic area controlled, verify firing solenoid channel 1 then magazine channel 0 in the exact original order and timing: 0.15 s then 0.4 s.
8. Confirm disabled, autonomous (currently null), teleop transition/cancellation, and test-mode scheduler cancellation behavior.
9. Record observed behavior and any differences for review. Do not deploy this branch until the team completes its normal approval and deployment process.

## References

- [WPILib stable documentation](https://docs.wpilib.org/en/stable/)
- [WPILib GradleRIO project migration guidance](https://docs.wpilib.org/en/stable/docs/software/vscode-overview/creating-robot-program.html)
- [GradleRIO 2026.2.1 tag](https://github.com/wpilibsuite/GradleRIO/tree/v2026.2.1)
- [CTRE Phoenix 5 2026 vendordep](https://maven.ctr-electronics.com/release/com/ctre/phoenix/Phoenix5-frc2026-latest.json)
- [CTRE Phoenix 6 2026 vendordep](https://maven.ctr-electronics.com/release/com/ctre/phoenix6/latest/Phoenix6-frc2026-latest.json)
- [CTRE Phoenix 2026 release notes and vendor installation guidance](https://github.com/CrossTheRoadElec/Phoenix-Releases/releases/)
