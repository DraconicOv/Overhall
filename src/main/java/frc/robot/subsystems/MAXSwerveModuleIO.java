// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.configs.RobotConfig;
import frc.robot.configs.SwerveConstants;
import frc.robot.util.Alert;
import frc.robot.util.Alert.AlertType;
import frc.robot.util.FaultReporter;
import frc.robot.util.TunableNumber;
import java.util.ArrayList;
import java.util.List;

public class MAXSwerveModuleIO implements SwerveModuleIO {
  private final CANSparkMax m_drivingSparkMax;
  private final CANSparkMax m_turningSparkMax;

  private final RelativeEncoder m_drivingEncoder;
  private final AbsoluteEncoder m_turningEncoder;

  private final SparkMaxPIDController m_drivingPIDController;
  private final SparkMaxPIDController m_turningPIDController;

  private double m_chassisAngularOffset = 0;
  private SwerveModuleState m_desiredState = new SwerveModuleState(0.0, new Rotation2d());
  private final TunableNumber driveKp =
      new TunableNumber("Drive/DriveKp", RobotConfig.getInstance().getSwerveDriveKP());
  private final TunableNumber driveKi =
      new TunableNumber("Drive/DriveKi", RobotConfig.getInstance().getSwerveDriveKI());
  private final TunableNumber driveKd =
      new TunableNumber("Drive/DriveKd", RobotConfig.getInstance().getSwerveDriveKD());
  private final TunableNumber turnKp =
      new TunableNumber("Drive/TurnKp", RobotConfig.getInstance().getSwerveAngleKP());
  private final TunableNumber turnKi =
      new TunableNumber("Drive/TurnKi", RobotConfig.getInstance().getSwerveAngleKI());
  private final TunableNumber turnKd =
      new TunableNumber("Drive/TurnKd", RobotConfig.getInstance().getSwerveAngleKD());

  // Set active in any potential error senario (alert.set(true))
  private Alert angleEncoderConfigAlert =
      new Alert("Failed to apply configuration for angle encoder.", AlertType.ERROR);
  private Alert angleMotorConfigAlert =
      new Alert("Failed to apply configuration for angle motor.", AlertType.ERROR);
  private Alert driveMotorConfigAlert =
      new Alert("Failed to apply configuration for drive motor.", AlertType.ERROR);
  private double angleOffsetRot;

  /**
   * Constructs a MAXSwerveModule and configures the driving and turning motor, encoder, and PID
   * controller
   *
   * @param moduleNumber the module number (0-3); primarily used for logging
   * @param driveMotorID the CAN ID of the drive motor
   * @param angleMotorID the CAN ID of the angle motor
   * @param angleOffsetRot the absolute offset of the angle encoder in rotations
   */
  public MAXSwerveModuleIO(
      int moduleNumber, int drivingCANId, int turningCANId, double chassisAngularOffset) {

    // Factory reset, so we get the SPARKS MAX to a known state before configuring
    // them. This is useful in case a SPARK MAX is swapped out.

    m_drivingSparkMax = new CANSparkMax(drivingCANId, MotorType.kBrushless);
    m_drivingSparkMax.restoreFactoryDefaults();
    m_drivingPIDController = m_drivingSparkMax.getPIDController();
    configDriveMotor();
    configTurnMotor();
    m_turningSparkMax = new CANSparkMax(turningCANId, MotorType.kBrushless);
    m_turningSparkMax.restoreFactoryDefaults();
    m_turningPIDController = m_turningSparkMax.getPIDController();
    this.angleOffsetRot = chassisAngularOffset;
    // Setup encoders and PID controllers for the driving and turning SPARKS MAX.
    m_drivingEncoder = m_drivingSparkMax.getEncoder();
    m_turningEncoder = m_turningSparkMax.getAbsoluteEncoder(Type.kDutyCycle);
    setupEncoders(chassisAngularOffset);
    String subsystemName = "SwerveModule" + moduleNumber;
    FaultReporter.getInstance().registerHardware(subsystemName, "angle motor", m_turningSparkMax);
    FaultReporter.getInstance().registerHardware(subsystemName, "drive motor", m_drivingSparkMax);
  }

  private void configTurnMotor() {
    // Enable PID wrap around for the turning motor. This will allow the PID
    // controller to go through 0 to get to the setpoint i.e. going from 350 degrees
    // to 10 degrees will go through 0 rather than the other direction which is a
    // longer route.
    m_turningPIDController.setPositionPIDWrappingEnabled(true);
    m_turningPIDController.setPositionPIDWrappingMinInput(
        SwerveConstants.kTurningEncoderPositionPIDMinInput);
    m_turningPIDController.setPositionPIDWrappingMaxInput(
        SwerveConstants.kTurningEncoderPositionPIDMaxInput);

    // Set the PID gains
    m_turningPIDController.setP(turnKp.get());
    m_turningPIDController.setI(turnKi.get());
    m_turningPIDController.setD(turnKd.get());

    m_turningPIDController.setFF(SwerveConstants.kTurningFF);
    m_turningPIDController.setOutputRange(
        SwerveConstants.kTurningMinOutput, SwerveConstants.kTurningMaxOutput);

    // Set the PID gains for the driving motor.

    m_turningSparkMax.setIdleMode(SwerveConstants.kTurningMotorIdleMode);
    m_turningSparkMax.setSmartCurrentLimit(SwerveConstants.kTurningMotorCurrentLimit);

    // Save the SPARK MAX configurations. If a SPARK MAX browns out during
    // operation, it will maintain the above configurations.
    m_turningSparkMax.burnFlash();
  }

  private void configDriveMotor() {
    // Set the PID gains.

    m_drivingPIDController.setP(driveKp.get());
    m_drivingPIDController.setI(driveKi.get());
    m_drivingPIDController.setD(driveKd.get());
    m_drivingPIDController.setFF(SwerveConstants.kDrivingFF);
    m_drivingPIDController.setOutputRange(
        SwerveConstants.kDrivingMinOutput, SwerveConstants.kDrivingMaxOutput);

    m_drivingSparkMax.setIdleMode(SwerveConstants.kDrivingMotorIdleMode);
    m_drivingSparkMax.setSmartCurrentLimit(SwerveConstants.kDrivingMotorCurrentLimit);
    // Save the SPARK MAX configurations. If a SPARK MAX browns out during
    // operation, it will maintain the above configurations.
    m_drivingSparkMax.burnFlash();
  }

  private void setupEncoders(double chassisAngularOffset) {

    m_drivingPIDController.setFeedbackDevice(m_drivingEncoder);
    m_turningPIDController.setFeedbackDevice(m_turningEncoder);
    // Apply position and velocity conversion factors for the driving encoder. The
    // native units for position and velocity are rotations and RPM, respectively,
    // but we want meters and meters per second to use with WPILib's swerve APIs.
    m_drivingEncoder.setPositionConversionFactor(SwerveConstants.kDrivingEncoderPositionFactor);
    m_drivingEncoder.setVelocityConversionFactor(SwerveConstants.kDrivingEncoderVelocityFactor);

    // Apply position and velocity conversion factors for the turning encoder. We
    // want these in radians and radians per second to use with WPILib's swerve
    // APIs.
    m_turningEncoder.setPositionConversionFactor(SwerveConstants.kTurningEncoderPositionFactor);
    m_turningEncoder.setVelocityConversionFactor(SwerveConstants.kTurningEncoderVelocityFactor);

    // Invert the turning encoder, since the output shaft rotates in the opposite direction of
    // the steering motor in the MAXSwerve Module.
    m_turningEncoder.setInverted(SwerveConstants.kTurningEncoderInverted);

    m_chassisAngularOffset = chassisAngularOffset;
    m_desiredState.angle = new Rotation2d(m_turningEncoder.getPosition());
    m_drivingEncoder.setPosition(0);
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    // Apply chassis angular offset to the encoder position to get the position
    // relative to the chassis.
    return new SwerveModuleState(
        m_drivingEncoder.getVelocity(),
        new Rotation2d(m_turningEncoder.getPosition() - m_chassisAngularOffset));
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    // Apply chassis angular offset to the encoder position to get the position
    // relative to the chassis.
    return new SwerveModulePosition(
        m_drivingEncoder.getPosition(),
        new Rotation2d(m_turningEncoder.getPosition() - m_chassisAngularOffset));
  }

  /**
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    // Apply chassis angular offset to the desired state.
    SwerveModuleState correctedDesiredState = new SwerveModuleState();
    correctedDesiredState.speedMetersPerSecond = desiredState.speedMetersPerSecond;
    correctedDesiredState.angle =
        desiredState.angle.plus(Rotation2d.fromRadians(m_chassisAngularOffset));

    // Optimize the reference state to avoid spinning further than 90 degrees.
    SwerveModuleState optimizedDesiredState =
        SwerveModuleState.optimize(
            correctedDesiredState, new Rotation2d(m_turningEncoder.getPosition()));

    // Command driving and turning SPARKS MAX towards their respective setpoints.
    m_drivingPIDController.setReference(
        optimizedDesiredState.speedMetersPerSecond, CANSparkMax.ControlType.kVelocity);
    m_turningPIDController.setReference(
        optimizedDesiredState.angle.getRadians(), CANSparkMax.ControlType.kPosition);
    m_desiredState = desiredState;
  }

  /** Zeroes all the SwerveModule encoders. */
  public void resetEncoders() {
    m_drivingEncoder.setPosition(0);
  }
  /**
   * Returns a list of status signals for the gyro related to odometry. This can be used to
   * synchronize the gyro and swerve modules to improve the accuracy of pose estimation.
   *
   * @return the status signals for the gyro
   */
  public List<Double> getOdometryStatusSignals() {
    ArrayList<Double> signals = new ArrayList<>();
    signals.add(m_drivingEncoder.getPosition());
    signals.add(m_drivingEncoder.getVelocity());
    signals.add(m_turningEncoder.getPosition());
    signals.add(m_turningEncoder.getVelocity());
    return signals;
  }
}
