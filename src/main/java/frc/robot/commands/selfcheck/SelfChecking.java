package frc.robot.commands.selfcheck;

import java.util.List;

public interface SelfChecking {
  List<SubsystemFault> checkForFaults();
}
