package frc.robot.configs;

import java.util.HashMap;

/**
 * The state of the motor controller bridge when output is neutral or disabled.
 */
public enum NeutralModeValue
{
    Coast(0),
    Brake(1),;

    public final int value;

    NeutralModeValue(int initValue)
    {
        this.value = initValue;
    }

    private static HashMap<Integer, NeutralModeValue> _map = null;
    static
    {
        _map = new HashMap<Integer, NeutralModeValue>();
        for (NeutralModeValue type : NeutralModeValue.values())
        {
            _map.put(type.value, type);
        }
    }

    /**
     * Gets NeutralModeValue from specified value
     * @param value Value of NeutralModeValue
     * @return NeutralModeValue of specified value
     */
    public static NeutralModeValue valueOf(int value)
    {
        NeutralModeValue retval = _map.get(value);
        if (retval != null) return retval;
        return NeutralModeValue.values()[0];
    }
}
