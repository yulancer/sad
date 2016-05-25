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

        batch.addLocator(1, BaseLocator.holdingRegister(slaveId, 2, DataType.TWO_BYTE_INT_UNSIGNED));

        try {
            master.init();
            results = master.send(batch);
        } catch (Exception e) {
            sadInfo.exception = e;
        } finally {
            master.destroy();
        }

        if (results != null) {
            int flags = results.getIntValue(0);
            sadInfo.GardenWaterOn = (flags & 1) == 1;
            sadInfo.SaunaWaterOn  = (flags & 2) == 2;
            sadInfo.PumpPowerOn = (flags & 4) == 4;
            sadInfo.PondPowerOn =(flags & 8) == 8;
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
