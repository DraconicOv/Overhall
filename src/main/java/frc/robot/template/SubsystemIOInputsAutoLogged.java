package frc.robot.template;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class SubsystemIOInputsAutoLogged extends SubsystemIO.SubsystemIOInputs
    implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("PositionDeg", positionDeg);
    table.put("VelocityRPM", velocityRPM);
    table.put("ClosedLoopError", closedLoopError);
    table.put("Setpoint", setpoint);
    table.put("Power", power);
    table.put("ControlMode", controlMode);
    table.put("StatorCurrentAmps", statorCurrentAmps);
    table.put("TempCelsius", tempCelsius);
    table.put("SupplyCurrentAmps", supplyCurrentAmps);
  }

  @Override
  public void fromLog(LogTable table) {
    positionDeg = table.get("PositionDeg", positionDeg);
    velocityRPM = table.get("VelocityRPM", velocityRPM);
    closedLoopError = table.get("ClosedLoopError", closedLoopError);
    setpoint = table.get("Setpoint", setpoint);
    power = table.get("Power", power);
    controlMode = table.get("ControlMode", controlMode);
    statorCurrentAmps = table.get("StatorCurrentAmps", statorCurrentAmps);
    tempCelsius = table.get("TempCelsius", tempCelsius);
    supplyCurrentAmps = table.get("SupplyCurrentAmps", supplyCurrentAmps);
  }

  public SubsystemIOInputsAutoLogged clone() {
    SubsystemIOInputsAutoLogged copy = new SubsystemIOInputsAutoLogged();
    copy.positionDeg = this.positionDeg;
    copy.velocityRPM = this.velocityRPM;
    copy.closedLoopError = this.closedLoopError;
    copy.setpoint = this.setpoint;
    copy.power = this.power;
    copy.controlMode = this.controlMode;
    copy.statorCurrentAmps = this.statorCurrentAmps;
    copy.tempCelsius = this.tempCelsius;
    copy.supplyCurrentAmps = this.supplyCurrentAmps;
    return copy;
  }
}
