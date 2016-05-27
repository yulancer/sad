package ru.yulancer.sad;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteCoilsResponse;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class Modbus4jActor implements IModbusActor {

    public String mHost;
    public int mPort;

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

        BatchResults<Integer> results = null;
        BatchRead<Integer> batch = new BatchRead<>();
        int slaveId = 1;

        batch.addLocator(1, BaseLocator.holdingRegister(slaveId, 1, DataType.TWO_BYTE_INT_UNSIGNED));
        // температуры
        batch.addLocator(2, BaseLocator.holdingRegister(slaveId, 4, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        batch.addLocator(3, BaseLocator.holdingRegister(slaveId, 6, DataType.FOUR_BYTE_FLOAT_SWAPPED));
        // Литры вылитые
        batch.addLocator(4, BaseLocator.holdingRegister(slaveId, 10, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(5, BaseLocator.holdingRegister(slaveId, 11, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(6, BaseLocator.holdingRegister(slaveId, 12, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(7, BaseLocator.holdingRegister(slaveId, 13, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(8, BaseLocator.holdingRegister(slaveId, 14, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(9, BaseLocator.holdingRegister(slaveId, 15, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(10, BaseLocator.holdingRegister(slaveId, 16, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(11, BaseLocator.holdingRegister(slaveId, 17, DataType.TWO_BYTE_INT_UNSIGNED));
        // Литры требуемые
        batch.addLocator(12, BaseLocator.holdingRegister(slaveId, 18, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(13, BaseLocator.holdingRegister(slaveId, 19, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(14, BaseLocator.holdingRegister(slaveId, 20, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(15, BaseLocator.holdingRegister(slaveId, 21, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(16, BaseLocator.holdingRegister(slaveId, 22, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(17, BaseLocator.holdingRegister(slaveId, 23, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(18, BaseLocator.holdingRegister(slaveId, 24, DataType.TWO_BYTE_INT_UNSIGNED));
        batch.addLocator(19, BaseLocator.holdingRegister(slaveId, 25, DataType.TWO_BYTE_INT_UNSIGNED));

        try {
            master.init();
            results = master.send(batch);
        } catch (Exception e) {
            sadInfo.exception = e;
        } finally {
            master.destroy();
        }

        if (results != null) {

            int flags = results.getIntValue(1);
            sadInfo.GardenWaterOn = (flags & 1) == 1;
            sadInfo.SaunaWaterOn = (flags & 2) == 2;
            sadInfo.PumpPowerOn = (flags & 4) == 4;
            sadInfo.PondPowerOn = (flags & 8) == 8;
            sadInfo.SadWaterPressureOK = (flags & 16) == 16;
            sadInfo.PhotoSensorDark = (flags & 32) == 32;
            sadInfo.RainSensorWet = (flags & 64) == 64;
            sadInfo.Frost = (flags & 128) == 128;

            sadInfo.ValveOpenStatuses =  (byte) (flags >> 8);

            sadInfo.AirTemperature = results.getFloatValue(2);
            sadInfo.FrostTemperature = results.getFloatValue(3);

            sadInfo.LitersDrained1 = results.getIntValue(10);
            sadInfo.LitersDrained2 = results.getIntValue(11);
            sadInfo.LitersDrained3 = results.getIntValue(12);
            sadInfo.LitersDrained4 = results.getIntValue(13);
            sadInfo.LitersDrained5 = results.getIntValue(14);
            sadInfo.LitersDrained6 = results.getIntValue(15);
            sadInfo.LitersDrained7 = results.getIntValue(16);
            sadInfo.LitersDrained8 = results.getIntValue(17);

            sadInfo.LitersDrained8 = results.getIntValue(18);
            sadInfo.LitersDrained8 = results.getIntValue(19);
            sadInfo.LitersDrained8 = results.getIntValue(20);
            sadInfo.LitersDrained8 = results.getIntValue(21);
            sadInfo.LitersDrained8 = results.getIntValue(22);
            sadInfo.LitersDrained8 = results.getIntValue(23);
            sadInfo.LitersDrained8 = results.getIntValue(24);
            sadInfo.LitersDrained8 = results.getIntValue(25);

        }


        return sadInfo;
    }

    @Override
    public void SendSwitchSignal(byte offset) {
        ModbusMaster master = CreateMaster();

        int slaveId = 1;
        if (master.testSlaveNode(slaveId))
            try {
                WriteCoilsRequest request = new WriteCoilsRequest(slaveId, offset, new boolean[]{true});
                master.send(request);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        master.destroy();
    }
}
