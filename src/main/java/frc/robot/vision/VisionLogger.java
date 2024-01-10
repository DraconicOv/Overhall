package frc.robot.vision;


import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class VisionLogger extends VisionIO.VisionIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("EstimatedRobotPose", estimatedRobotPose);
    table.put("EstimatedRobotPoseTimestamp", estimatedRobotPoseTimestamp);
    table.put("EstimatedRobotPoseTags", estimatedRobotPoseTags);
    table.put("LastCameraTimestamp", lastCameraTimestamp);
  }

  @Override
  public void fromLog(LogTable table) {
    estimatedRobotPose = table.get("EstimatedRobotPose", estimatedRobotPose);
    estimatedRobotPoseTimestamp = table.get("EstimatedRobotPoseTimestamp", estimatedRobotPoseTimestamp);
    estimatedRobotPoseTags = table.get("EstimatedRobotPoseTags", estimatedRobotPoseTags);
    lastCameraTimestamp = table.get("LastCameraTimestamp", lastCameraTimestamp);
  }

  public VisionLogger clone() {
    VisionLogger copy = new VisionLogger();
    copy.estimatedRobotPose = this.estimatedRobotPose;
    copy.estimatedRobotPoseTimestamp = this.estimatedRobotPoseTimestamp;
    copy.estimatedRobotPoseTags = this.estimatedRobotPoseTags.clone();
    copy.lastCameraTimestamp = this.lastCameraTimestamp;
    return copy;
  }
}
