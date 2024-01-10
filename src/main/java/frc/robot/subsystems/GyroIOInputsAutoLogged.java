package frc.robot.subsystems;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class GyroIOInputsAutoLogged extends GyroIO.GyroIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("Connected", connected);
    table.put("YawDeg", yawDeg);
    table.put("YawDegPerSec", yawDegPerSec);
    table.put("PitchDeg", pitchDeg);
    table.put("PitchDegPerSec", pitchDegPerSec);
    table.put("RollDeg", rollDeg);
    table.put("RollDegPerSec", rollDegPerSec);
  }

  @Override
  public void fromLog(LogTable table) {
    connected = table.get("Connected", connected);
    yawDeg = table.get("YawDeg", yawDeg);
    yawDegPerSec = table.get("YawDegPerSec", yawDegPerSec);
    pitchDeg = table.get("PitchDeg", pitchDeg);
    pitchDegPerSec = table.get("PitchDegPerSec", pitchDegPerSec);
    rollDeg = table.get("RollDeg", rollDeg);
    rollDegPerSec = table.get("RollDegPerSec", rollDegPerSec);
  }

  public GyroIOInputsAutoLogged clone() {
    GyroIOInputsAutoLogged copy = new GyroIOInputsAutoLogged();
    copy.connected = this.connected;
    copy.yawDeg = this.yawDeg;
    copy.yawDegPerSec = this.yawDegPerSec;
    copy.pitchDeg = this.pitchDeg;
    copy.pitchDegPerSec = this.pitchDegPerSec;
    copy.rollDeg = this.rollDeg;
    copy.rollDegPerSec = this.rollDegPerSec;
    return copy;
  }
}
