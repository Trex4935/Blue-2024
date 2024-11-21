// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.math.proto.System;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.extensions.Talon;

public class Drivetrain extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */

  WPI_TalonSRX leftMotorOne;
  WPI_TalonSRX leftMotorTwo;
  WPI_TalonSRX leftMotorThree;
  WPI_TalonSRX leftMotorFour;
  WPI_TalonSRX rightMotorOne;
  WPI_TalonSRX rightMotorTwo;
  WPI_TalonSRX rightMotorThree;
  WPI_TalonSRX rightMotorFour;


  public DifferentialDrive diffDrive;

  Double m_MaxSpeed = Constants.maxspeed;
  public Object differentialDrive;


  public Drivetrain() {
    leftMotorOne = Talon.createDefaultTalon(Constants.leftMotorOneID);
    leftMotorTwo = Talon.createDefaultTalon(Constants.leftMotorTwoID);
    leftMotorThree = Talon.createDefaultTalon(Constants.leftMotorThreeID);
    leftMotorFour = Talon.createDefaultTalon(Constants.leftMotorFourID);
    rightMotorOne = Talon.createDefaultTalon(Constants.rightMotorOneID);
    rightMotorTwo = Talon.createDefaultTalon(Constants.rightMotorTwoID);
    rightMotorThree = Talon.createDefaultTalon(Constants.rightMotorThreeID);
    rightMotorFour = Talon.createDefaultTalon(Constants.rightMotorFourID);

    leftMotorTwo.follow(leftMotorOne);
    leftMotorThree.follow(leftMotorOne);
    leftMotorFour.follow(leftMotorOne);

    rightMotorTwo.follow(rightMotorOne);
    rightMotorThree.follow(rightMotorOne);
    rightMotorFour.follow(rightMotorOne);

    rightMotorOne.setInverted(true);
    rightMotorTwo.setInverted(true);
    rightMotorThree.setInverted(true);
    rightMotorFour.setInverted(true);

    diffDrive = new DifferentialDrive(leftMotorOne, rightMotorOne);
  }

  public void stopMotors(){
    leftMotorOne.stopMotor();
    rightMotorOne.stopMotor();
  }

  public void motorBackward(){
    diffDrive.tankDrive(-0.5, -0.5);
  }

  public void motorForward(){
    diffDrive.tankDrive(0.5, 0.5);
  }

  public void rotateLeft(){
    diffDrive.tankDrive(-0.8, 0.8);
  }

  public void rotateRight(){
    diffDrive.tankDrive(0.8, -0.8);   
  }
  public void disableRobot(){
    CommandScheduler.getInstance().disable();
  }

  public void enableRobot(){
    CommandScheduler.getInstance().enable();
  }
  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }
  public Command StopMotors(){
    return runOnce( 
        () -> stopMotors() 
        );
  }
  public Command moveMotorsForward(){
    return runEnd(
        () -> motorForward(), 
        () -> stopMotors() 
        );
  }

  public Command moveMotorsBackward(){
    return runEnd(
        () -> motorBackward(), 
        () -> stopMotors() 
        );
  }

  public Command rotateMotorsRight(){
    return runEnd(
        () -> rotateRight(), 
        () -> stopMotors() 
        );
  }

  public Command rotateMotorsLeft(){
    return runEnd(
        () -> rotateLeft(), 
        () -> stopMotors() 
        );
  }

  public Command DisableRobot(){
    return runOnce(
      () -> disableRobot()
    );
  }

   public Command EnableRobot(){
    return runOnce(
      () -> enableRobot()
    ); 
  }
  
 public Command triggerEnable(){
  return runOnce(
    () -> enableRobot()
  );
 }
 //
  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

}