package frc.robot.subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj.simulation.SimHooks;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class ShooterFiringCommandTest {
  private static final double TIME_EPSILON_SECONDS = 1e-6;

  private final CommandScheduler scheduler = CommandScheduler.getInstance();
  private final TestShooterRequirement shooter = new TestShooterRequirement();
  private final List<OutputEvent> outputs = new ArrayList<>();
  private double startTimeSeconds;

  @BeforeAll
  static void initializeHal() {
    assertTrue(HAL.initialize(500, 0));
  }

  @AfterAll
  static void shutdownHal() {
    HAL.shutdown();
  }

  @BeforeEach
  void setUp() {
    scheduler.enable();
    scheduler.cancelAll();
    SimHooks.restartTiming();
    SimHooks.pauseTiming();
    DriverStationSim.setEnabled(true);
    DriverStationSim.notifyNewData();
    startTimeSeconds = Timer.getFPGATimestamp();
  }

  @AfterEach
  void tearDown() {
    scheduler.enable();
    scheduler.cancelAll();
    scheduler.unregisterSubsystem(shooter);
    SimHooks.resumeTiming();
    DriverStationSim.setEnabled(false);
    DriverStationSim.notifyNewData();
  }

  @Test
  @Timeout(value = 2, unit = TimeUnit.SECONDS)
  void runsAsynchronouslyAndPreservesTheOutputOrderAndDelays() {
    Command firing = createCommand();
    assertTrue(firing.getRequirements().contains(shooter));

    scheduler.schedule(firing);
    assertTrue(scheduler.isScheduled(firing));
    scheduler.run();
    assertOutput(0, "firing", true, 0.0);

    SimHooks.stepTiming(0.149);
    scheduler.run();
    assertEquals(1, outputs.size(), "firing solenoid must stay on for 150 ms");

    SimHooks.stepTiming(0.001);
    scheduler.run();
    scheduler.run();
    assertOutput(1, "firing", false, 0.150);
    assertOutput(2, "magazine", false, 0.150);

    SimHooks.stepTiming(0.399);
    scheduler.run();
    assertEquals(3, outputs.size(), "magazine must stay off for 400 ms");

    SimHooks.stepTiming(0.001);
    scheduler.run();
    scheduler.run();
    assertOutput(3, "magazine", true, 0.550);
    assertFalse(scheduler.isScheduled(firing), "firing command should complete normally");
  }

  @Test
  void cancellationCommandsTheFiringSolenoidOff() {
    Command firing = createCommand();
    scheduler.schedule(firing);
    scheduler.run();
    assertOutput(0, "firing", true, 0.0);

    scheduler.cancel(firing);

    assertOutput(1, "firing", false, 0.0);
    assertFalse(scheduler.isScheduled(firing));
  }

  @Test
  void interruptionDuringMagazineIntervalLeavesMagazineStateUnchanged() {
    Command firing = createCommand();
    scheduler.schedule(firing);
    scheduler.run();

    SimHooks.stepTiming(0.15);
    scheduler.run();
    scheduler.run();
    assertOutput(1, "firing", false, 0.150);
    assertOutput(2, "magazine", false, 0.150);

    scheduler.cancel(firing);

    assertOutput(3, "firing", false, 0.150);
    assertEquals(4, outputs.size(), "interruption must not guess a magazine safe state");
    assertFalse(scheduler.isScheduled(firing));
  }

  @Test
  void disablingTheRobotInterruptsTheCommandAndTurnsFiringOff() {
    Command firing = createCommand();
    scheduler.schedule(firing);
    scheduler.run();
    assertOutput(0, "firing", true, 0.0);

    DriverStationSim.setEnabled(false);
    DriverStationSim.notifyNewData();
    scheduler.run();

    assertOutput(1, "firing", false, 0.0);
    assertFalse(scheduler.isScheduled(firing));
  }

  @Test
  void aSecondFiringCommandInterruptsTheFirstWithoutOverlap() {
    Command first = createCommand();
    Command second = createCommand();
    scheduler.schedule(first);
    scheduler.run();
    assertOutput(0, "firing", true, 0.0);

    scheduler.schedule(second);

    assertFalse(scheduler.isScheduled(first));
    assertTrue(scheduler.isScheduled(second));
    assertOutput(1, "firing", false, 0.0);

    scheduler.run();
    assertOutput(2, "firing", true, 0.0);
  }

  private Command createCommand() {
    return Shooter.createFiringSequence(
        shooter,
        state -> outputs.add(new OutputEvent(
            Timer.getFPGATimestamp() - startTimeSeconds, "firing", state)),
        state -> outputs.add(new OutputEvent(
            Timer.getFPGATimestamp() - startTimeSeconds, "magazine", state)));
  }

  private void assertOutput(int index, String solenoid, boolean state, double timeSeconds) {
    OutputEvent event = outputs.get(index);
    assertEquals(solenoid, event.solenoid());
    assertEquals(state, event.state());
    assertEquals(timeSeconds, event.timeSeconds(), TIME_EPSILON_SECONDS);
  }

  private static class TestShooterRequirement extends SubsystemBase {}

  private record OutputEvent(double timeSeconds, String solenoid, boolean state) {}
}
