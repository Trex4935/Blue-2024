// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;



/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  // private final boolean x;
  private final CommandJoystick m_dancePad = new CommandJoystick(1);

  private final Drivetrain m_Drivetrain = new Drivetrain();
  private final Shooter m_Shooter = new Shooter();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController = new CommandXboxController(
      OperatorConstants.kDriverControllerPort);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the trigger bindings
    
    configureBindings();
  

    // Declares type of command
    m_Drivetrain.setDefaultCommand(Commands.run(
        // Executes Tank Drive using drivetrainController
        () -> m_Drivetrain.diffDrive.tankDrive(m_driverController.getLeftY(), m_driverController.getRightY()),
    m_Drivetrain));   



  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {

    
    // Schedule `exampleMethodCommand` when the Xbox controller's B button is
    // pressed,
    // cancelling on release.
    //m_driverController.b().onTrue(m_Shooter.shooterDown());
    //m_driverController.b().whileFalse(getAutonomousCommand());
     {
  
    m_driverController.y().whileTrue(m_Shooter.Run_Cannon_Motor_up());
    m_driverController.a().whileTrue(m_Shooter.Run_Cannon_Motor_down());
    // m_driverController.rightBumper().onTrue(m_Shooter.shoot_ball());
    m_driverController.rightBumper().and(m_dancePad.button(7)).whileTrue(m_Shooter.shoot_ball());
   // m_driverController.x().onTrue();
   //  m_driverController.x().OnFalse(CommandScheduler.getInstance().enable());
    
    // m_driverController.x().whileTrue(m_Drivetrain.triggerEnable());
    // m_driver
    //Moves the cannon up and down using X and O respectfully
    m_dancePad.button(7).whileTrue(m_Shooter.Run_Cannon_Motor_up());
    m_dancePad.button(8).whileTrue(m_Shooter.Run_Cannon_Motor_down());

    // Moves robot forawrd and backwards with the up and down key repsectfully 
    m_dancePad.button(3).whileTrue(m_Drivetrain.moveMotorsForward());
    m_dancePad.button(2).whileTrue(m_Drivetrain.moveMotorsBackward());
    
    // Emergency Stop shenanigans
    m_dancePad.button(5).onTrue(m_Drivetrain.DisableRobot());
    m_dancePad.button(10).onTrue(m_Drivetrain.EnableRobot());

    // Shoots ball using triangle
    m_dancePad.button(6).onTrue(m_Shooter.shoot_ball());

    // Turns the robot left or roght based on the left or right buttons
    m_dancePad.button(1).whileTrue(m_Drivetrain.rotateMotorsLeft());
    m_dancePad.button(4).whileTrue(m_Drivetrain.rotateMotorsRight());

 //   m_dancePad.button(4).whileFalse(m_Drivetrain.rotateMotorsRight());
      }
    }
    
    


    //m_dancePad.
   // m_driverController.a().whileTrue(System.out.println("Button A"));
  

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return null;
  }
}