package ru.yulancer.sad;

import com.serotonin.modbus4j.code.DataType;

/**
 * Created by matveev_yuri on 02.06.2016.
 */
public class ModbusRegisterData {
    public ModbusRegisterData(String id, int number, int type) {
        RegisterId = id;
        RegisterNumber = number;
        RegisterType = type;
    }

    ;

    public String RegisterId;
    public int RegisterNumber;
    public int RegisterType;
}
