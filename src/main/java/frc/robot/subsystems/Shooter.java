// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.extensions.Talon;

public class Shooter extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */
  static WPI_TalonSRX shooterAim;
  Solenoid shooterSolenoid;
  Solenoid shooterMag;
  Double m_MaxSpeed = Constants.maxspeed;
   
  public Shooter() {
    shooterAim = Talon.createDefaultTalon(Constants.shooterMotorID);
    shooterAim.setInverted(true);

    shooterSolenoid = new Solenoid(1, PneumaticsModuleType.CTREPCM, 1);
    shooterMag = new Solenoid(1,PneumaticsModuleType.CTREPCM, 0);

  }

    // shoot with delay
    public void pewPew() {
      // System.out.println("-- Command Out --");
      // Open Air valve to shoot ball
      shooterSolenoid.set(true);
      Timer.delay(0.15);
      // Close valve 
      shooterSolenoid.set(false);
      // Open reload slot
      shooterMag.set(false);
      Timer.delay(0.4);
      // Close reload slot
      shooterMag.set(true);
    }

  public void Mag_and_Soleniod_Stop(){
    shooterMag.set(false);
    shooterSolenoid.set(false);

  }
  public void ShooterMag_Toggle(){
    shooterMag.toggle();
  }
  
  public void ShooterSolenoid_Toggle() {
    shooterSolenoid.toggle();
  }

  public void ShooterAim_Up() {
    shooterAim.set(.1);
  }

  public void shooterAim_Down() {
    shooterAim.set(-.1);
  }
  public void shooterAim_Stop(){
    shooterAim.stopMotor();
  }
  public void shooterEnable(){
    pewPew();
    ShooterMag_Toggle();
    ShooterSolenoid_Toggle();
  }
  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command shooterUp() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          ShooterAim_Up();
        });
  }

  public Command shooterDown() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          shooterAim_Down();
        });
  }
  public Command shooterStop() {
    return runOnce(
        () -> {
          shooterAim.stopMotor();
        });
  }
  public Command Run_Cannon_Motor_up() {
    return runEnd(
        () -> ShooterAim_Up(), 
        () -> shooterAim_Stop() 
        );
  }
  public Command Run_Cannon_Motor_down() {
    return runEnd(
        () -> shooterAim_Down(), 
        () -> shooterAim_Stop() 
        );
  }
  public Command shoot_ball() {
    return runOnce(() -> pewPew());
  }
 /*  public Command enable_shooter(){
    return runOnce(() -> )
  } */


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
 //   if (controller.getAButton() == true){
 //     System.out.println("motor up");
 //   }
 //   if (controller.getBButton()== true){
 //     System.out.println("motor down");
 //   }
 //   else{
 //     shooterStop();
 //   }
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}