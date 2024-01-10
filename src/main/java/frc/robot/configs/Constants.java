// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

 package frc.robot.configs;

 import edu.wpi.first.wpilibj.RobotBase;
 import frc.robot.util.Alert;
import frc.robot.util.Alert.AlertType;
 
 /**
  * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
  * constants. This class should not be used for any other purpose. All constants should be declared
  * globally (i.e. public static). Do not put anything functional in this class.
  *
  * <p>Subsystem-specific constants should be defined in the subsystem's own constant class.
  * Constants that vary from robot to robot should be defined in the config classes.
  *
  * <p>It is advised to statically import this class (or one of its inner classes) wherever the
  * constants are needed, to reduce verbosity.
  */
 public final class Constants {
 
   // set to true in order to change all Tunable values via Shuffleboard
   public static final boolean TUNING_MODE = false;
 
   private static final RobotType ROBOT = RobotType.ROBOT_DEFAULT;
 
   private static final Alert invalidRobotAlert =
       new Alert("Invalid robot selected, using competition robot as default.", AlertType.ERROR);
 
   // FIXME: update for various robots
   public enum RobotType {
     ROBOT_DEFAULT,
     ROBOT_SIMBOT
   }
 

   public enum Mode {
     REAL,
     REPLAY,
     SIM
   }
 
   public static final double LOOP_PERIOD_SECS = 0.02;

   public static Mode getMode() {
    switch (ROBOT) {
      case ROBOT_DEFAULT:

        return RobotBase.isReal() ? Mode.REAL : Mode.REPLAY;

      case ROBOT_SIMBOT:
        return Mode.SIM;

      default:
        return Mode.REAL;
    }
  }
}

 