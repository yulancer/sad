package ru.yulancer.sad;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilResponse;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class Modbus4jActor implements IModbusActor {

    public String mHost;
    public int mPort;


    public static final String STATUS_FLAGS = "StatusFlags";
    public static final String AIR_TEMPERATURE = "AirTemperature";
    public static final String FROST_SETPOINT_TEMPERATURE = "FrostSetpointTemperature";

    public static final String LITERS_DRAINED1 = "LitersDrained1";
    public static final String LITERS_DRAINED2 = "LitersDrained2";
    public static final String LITERS_DRAINED3 = "LitersDrained3";
    public static final String LITERS_DRAINED4 = "LitersDrained4";
    public static final String LITERS_DRAINED5 = "LitersDrained5";
    public static final String LITERS_DRAINED6 = "LitersDrained6";
    public static final String LITERS_DRAINED7 = "LitersDrained7";
    public static final String LITERS_DRAINED8 = "LitersDrained8";

    public static final String LITERS_NEEDED1 = "LitersNeeded1";
    public static final String LITERS_NEEDED2 = "LitersNeeded2";
    public static final String LITERS_NEEDED3 = "LitersNeeded3";
    public static final String LITERS_NEEDED4 = "LitersNeeded4";
    public static final String LITERS_NEEDED5 = "LitersNeeded5";
    public static final String LITERS_NEEDED6 = "LitersNeeded6";
    public static final String LITERS_NEEDED7 = "LitersNeeded7";
    public static final String LITERS_NEEDED8 = "LitersNeeded8";

    public static final String LINE_BIT_STATUS12 = "BitStatusLine12";
    public static final String LINE_BIT_STATUS34 = "BitStatusLine34";
    public static final String LINE_BIT_STATUS56 = "BitStatusLine56";
    public static final String LINE_BIT_STATUS78 = "BitStatusLine78";

    public static final ModbusRegisterData[] modbusRegisterData = new ModbusRegisterData[]{
            new ModbusRegisterData(STATUS_FLAGS, 1, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(AIR_TEMPERATURE, 4, DataType.FOUR_BYTE_FLOAT_SWAPPED),
            new ModbusRegisterData(FROST_SETPOINT_TEMPERATURE, 6, DataType.FOUR_BYTE_FLOAT_SWAPPED),
            new ModbusRegisterData(LITERS_DRAINED1, 10, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED2, 11, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED3, 12, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED4, 13, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED5, 14, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED6, 15, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED7, 16, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_DRAINED8, 17, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED1, 18, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED2, 19, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED3, 20, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED4, 21, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED5, 22, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED6, 23, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED7, 24, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LITERS_NEEDED8, 25, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LINE_BIT_STATUS12, 26, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LINE_BIT_STATUS34, 27, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LINE_BIT_STATUS56, 28, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(LINE_BIT_STATUS78, 29, DataType.TWO_BYTE_INT_UNSIGNED),
    };

    public Modbus4jActor(String host, int port) {
        mHost = host;
        mPort = port;
    }

    private ModbusMaster CreateMaster() {

        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(mHost);
        ipParameters.setPort(mPort);


        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, false);
        master.setTimeout(600);
        master.setRetries(10);
        return master;
    }

    @Override
    public SadInfo GetSadInfo() {
        SadInfo sadInfo = new SadInfo();

        ModbusMaster master = CreateMaster();

        BatchResults<String> results = null;
        BatchRead<String> batch = new BatchRead<>();
        int slaveId = 1;

        for (ModbusRegisterData registerData : modbusRegisterData)
            batch.addLocator(registerData.RegisterId, BaseLocator.holdingRegister(slaveId, registerData.RegisterNumber, registerData.RegisterType));

        try {
            master.init();
            results = master.send(batch);
        } catch (Exception e) {
            sadInfo.exception = e;
        } finally {
            master.destroy();
        }

        if (results != null) {

            int flags = results.getIntValue(STATUS_FLAGS);
            sadInfo.GardenWaterOn = (flags & 1) == 1;
            sadInfo.SaunaWaterOn = (flags & 2) == 2;
            sadInfo.PumpPowerOn = (flags & 4) == 4;
            sadInfo.PondPowerOn = (flags & 8) == 8;
            sadInfo.SadWaterPressureOK = (flags & 16) == 16;
            sadInfo.PhotoSensorDark = (flags & 32) == 32;
            sadInfo.RainSensorWet = (flags & 64) == 64;
            sadInfo.Frost = (flags & 128) == 128;

            sadInfo.ValveOpenStatuses = (byte) (flags >> 8);

            sadInfo.AirTemperature = results.getFloatValue(AIR_TEMPERATURE);
            sadInfo.FrostTemperature = results.getFloatValue(FROST_SETPOINT_TEMPERATURE);

            sadInfo.LineStatuses[0].LitersDrained = results.getIntValue(LITERS_DRAINED1);
            sadInfo.LineStatuses[1].LitersDrained = results.getIntValue(LITERS_DRAINED2);
            sadInfo.LineStatuses[2].LitersDrained = results.getIntValue(LITERS_DRAINED3);
            sadInfo.LineStatuses[3].LitersDrained = results.getIntValue(LITERS_DRAINED4);
            sadInfo.LineStatuses[4].LitersDrained = results.getIntValue(LITERS_DRAINED5);
            sadInfo.LineStatuses[5].LitersDrained = results.getIntValue(LITERS_DRAINED6);
            sadInfo.LineStatuses[6].LitersDrained = results.getIntValue(LITERS_DRAINED7);
            sadInfo.LineStatuses[7].LitersDrained = results.getIntValue(LITERS_DRAINED8);


            sadInfo.LineStatuses[0].LitersNeeded = results.getIntValue(LITERS_NEEDED1);
            sadInfo.LineStatuses[1].LitersNeeded = results.getIntValue(LITERS_NEEDED2);
            sadInfo.LineStatuses[2].LitersNeeded = results.getIntValue(LITERS_NEEDED3);
            sadInfo.LineStatuses[3].LitersNeeded = results.getIntValue(LITERS_NEEDED4);
            sadInfo.LineStatuses[4].LitersNeeded = results.getIntValue(LITERS_NEEDED5);
            sadInfo.LineStatuses[5].LitersNeeded = results.getIntValue(LITERS_NEEDED6);
            sadInfo.LineStatuses[6].LitersNeeded = results.getIntValue(LITERS_NEEDED7);
            sadInfo.LineStatuses[7].LitersNeeded = results.getIntValue(LITERS_NEEDED8);

            int lineBitStatus12 = results.getIntValue(LINE_BIT_STATUS12);
            updateLineInfo(lineBitStatus12, sadInfo.LineStatuses[0], sadInfo.LineStatuses[1]);
            int lineBitStatus34 = results.getIntValue(LINE_BIT_STATUS34);
            updateLineInfo(lineBitStatus34, sadInfo.LineStatuses[2], sadInfo.LineStatuses[3]);
            int lineBitStatus56 = results.getIntValue(LINE_BIT_STATUS56);
            updateLineInfo(lineBitStatus56, sadInfo.LineStatuses[4], sadInfo.LineStatuses[5]);
            int lineBitStatus78 = results.getIntValue(LINE_BIT_STATUS78);
            updateLineInfo(lineBitStatus78, sadInfo.LineStatuses[6], sadInfo.LineStatuses[7]);

        }

        return sadInfo;
    }

    private void updateLineInfo(int lineBitStatus, DrainLineInfo lineInfo1, DrainLineInfo lineInfo2) {
        byte lineBitStatus1 = (byte) lineBitStatus;
        byte lineBitStatus2 = (byte) (lineBitStatus >> 8);
        DrainLineBitStatus status1 = new DrainLineBitStatus(lineBitStatus1);
        status1.UpdateDrainLineInfo(lineInfo1);
        DrainLineBitStatus status2 = new DrainLineBitStatus(lineBitStatus2);
        status2.UpdateDrainLineInfo(lineInfo2);
    }

    @Override
    public void SendSwitchSignal(byte offset) {
        ModbusMaster master = CreateMaster();

        int slaveId = 1;
        if (master.testSlaveNode(slaveId))
            try {
                WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, true);
                WriteCoilResponse response = (WriteCoilResponse) master.send(request);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        master.destroy();
    }
}
