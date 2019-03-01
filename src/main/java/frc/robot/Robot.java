/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends IterativeRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private SendableChooser<String> m_chooser = new SendableChooser<>();
  private SendableChooser<InputType> inputChooser = new SendableChooser<>();

  private Talon left = new Talon(0);
  private Talon right = new Talon(1);

  private InputType inputType;
  private Joystick joystick0;
  private Joystick joystick1;
  private XboxController controller;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    left.setInverted(true);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    inputChooser.addOption("Joysticks", InputType.JOYSTICKS);
    inputChooser.setDefaultOption("Controller", InputType.CONTROLLER);
    SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putData("Input type", inputChooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  @Override
  public void teleopInit() {
    inputType = inputChooser.getSelected();
    if (inputType == InputType.JOYSTICKS) {
      joystick0 = new Joystick(0);
      joystick1 = new Joystick(1);
    } else if (inputType == InputType.CONTROLLER) {
      controller = new XboxController(0);
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if (inputType == InputType.JOYSTICKS) {
      arcade(joystick0.getY(), joystick1.getX());
    } else if (inputType == InputType.CONTROLLER) {
      arcade(controller.getY(Hand.kLeft), controller.getX(Hand.kRight));
    }
  }

  private static final double DEADZONE = 0.05;
  private static final double ACCELERATION = 0.7;

  private void arcade(double desiredMove, double desiredTurn) {
    double moveSpeed = 0;
    double turnSpeed = 0;
    double moveFinal = 0;
    double turnFinal = 0;

    if (Math.abs(desiredMove) < Math.abs(moveSpeed)) {
      moveSpeed = desiredMove;
    }

    if (Math.abs(desiredTurn) < Math.abs(turnSpeed)) {
      turnSpeed = desiredTurn;
    }

    turnSpeed += (desiredTurn - turnSpeed) * ACCELERATION * 0.7;
    moveSpeed += (desiredMove - moveSpeed) * ACCELERATION;

    if (Math.abs(moveSpeed) > DEADZONE) {
      moveFinal = moveSpeed * 0.7 + moveSpeed / Math.abs(moveSpeed) * 0.3;
      turnFinal = turnSpeed / Math.abs(turnSpeed) * Math.sqrt(Math.abs(turnSpeed));
    } else if (Math.abs(turnSpeed) > DEADZONE) {
      moveFinal = 0;
      turnFinal = turnSpeed * 0.7 + turnSpeed / Math.abs(turnSpeed) * 0.3;
    } else {
      moveFinal = 0;
      turnFinal = 0;
    }

    moveFinal = moveSpeed;
    turnFinal = turnSpeed;

    left.set(moveFinal - turnFinal);
    right.set(moveFinal + turnFinal);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  private enum InputType {
    JOYSTICKS, CONTROLLER
  }
}