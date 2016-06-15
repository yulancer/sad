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
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteCoilsResponse;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class Modbus4jActor implements IModbusActor {

    public static final int MAX_RETRIES = 3;
    public String mHost;
    public int mPort;


    public static final String STATUS_FLAGS = "StatusFlags";
    public static final String STATUS_FLAGS2 = "StatusFlags2";
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

    public static final String POND_AUTO_SWITCH_CONDITIONS_AND_WEEKDAYS = "PondAutoSwitchConditionsAndWeekdays";
    public static final String POND_AUTO_SWITCH_HOUR_MINUTE = "PondAutoSwitchHourMinute";
    public static final String POND_AUTO_SWITCH_TEMPERATURE = "PondAutoSwitchTemperature";

    public static final ModbusRegisterData[] modbusRegisterData = new ModbusRegisterData[]{
            new ModbusRegisterData(STATUS_FLAGS, 1, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(STATUS_FLAGS2, 3, DataType.TWO_BYTE_INT_UNSIGNED),
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
            new ModbusRegisterData(POND_AUTO_SWITCH_CONDITIONS_AND_WEEKDAYS, 54, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(POND_AUTO_SWITCH_HOUR_MINUTE, 55, DataType.TWO_BYTE_INT_UNSIGNED),
            new ModbusRegisterData(POND_AUTO_SWITCH_TEMPERATURE, 56, DataType.FOUR_BYTE_FLOAT_SWAPPED),
    };

    public static final String SCHEDULE_INDEX_AND_FLAGS = "ScheduleIndexAndFlags";
    public static final ModbusRegisterData mScheduleIndexAndFlags = new ModbusRegisterData(SCHEDULE_INDEX_AND_FLAGS, 47, DataType.TWO_BYTE_INT_UNSIGNED);

    public static final String SCHEDULE_WEEK_DAYS = "ScheduleWeekDays";
    public static final ModbusRegisterData mScheduleWeekDays = new ModbusRegisterData(SCHEDULE_WEEK_DAYS, 48, DataType.TWO_BYTE_INT_UNSIGNED);


    public static final String SCHEDULE_HOUR_MINUTE = "ScheduleHourMinute";
    public static final ModbusRegisterData mScheduleHourMinute = new ModbusRegisterData(SCHEDULE_HOUR_MINUTE, 38, DataType.TWO_BYTE_INT_UNSIGNED);

    public static final String SCHEDULE_LITERS1 = "ScheduleLiters1";
    public static final ModbusRegisterData mScheduleLiters1 = new ModbusRegisterData(SCHEDULE_LITERS1, 39, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS2 = "ScheduleLiters2";
    public static final ModbusRegisterData mScheduleLiters2 = new ModbusRegisterData(SCHEDULE_LITERS2, 40, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS3 = "ScheduleLiters3";
    public static final ModbusRegisterData mScheduleLiters3 = new ModbusRegisterData(SCHEDULE_LITERS3, 41, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS4 = "ScheduleLiters4";
    public static final ModbusRegisterData mScheduleLiters4 = new ModbusRegisterData(SCHEDULE_LITERS4, 42, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS5 = "ScheduleLiters5";
    public static final ModbusRegisterData mScheduleLiters5 = new ModbusRegisterData(SCHEDULE_LITERS5, 43, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS6 = "ScheduleLiters6";
    public static final ModbusRegisterData mScheduleLiters6 = new ModbusRegisterData(SCHEDULE_LITERS6, 44, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS7 = "ScheduleLiters7";
    public static final ModbusRegisterData mScheduleLiters7 = new ModbusRegisterData(SCHEDULE_LITERS7, 45, DataType.TWO_BYTE_INT_UNSIGNED);
    public static final String SCHEDULE_LITERS8 = "ScheduleLiters8";
    public static final ModbusRegisterData mScheduleLiters8 = new ModbusRegisterData(SCHEDULE_LITERS8, 46, DataType.TWO_BYTE_INT_UNSIGNED);


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


            int flags2 = results.getIntValue(STATUS_FLAGS2);
            sadInfo.ErrorNoPressureWhenDrain = (flags2 & 1) == 1;//первый бит
            sadInfo.AutoDrainOn = (flags2 & 256) == 256;      //девятый бит

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

            int pondFlagsAndDays = results.getIntValue(POND_AUTO_SWITCH_CONDITIONS_AND_WEEKDAYS);
            sadInfo.pondAutoOnSettings.AutoOnWhenDark = (pondFlagsAndDays & 1) == 1;
            sadInfo.pondAutoOnSettings.OnlyWhenNoRain = (pondFlagsAndDays & 2) == 2;
            sadInfo.pondAutoOnSettings.OnlyWhenWarm = (pondFlagsAndDays & 4) == 4;
            sadInfo.pondAutoOnSettings.OnlyCertainWeekdays = (pondFlagsAndDays & 8) == 8;
            sadInfo.pondAutoOnSettings.OnlyWhenEarly = (pondFlagsAndDays & 16) == 16;
            sadInfo.pondAutoOnSettings.WeekdayFlags = (byte) (pondFlagsAndDays >> 8);

            int pondHourMinute = results.getIntValue(POND_AUTO_SWITCH_HOUR_MINUTE);
            sadInfo.pondAutoOnSettings.Hour = (byte) pondHourMinute;
            sadInfo.pondAutoOnSettings.Minute = (byte) (pondHourMinute >> 8);

            sadInfo.pondAutoOnSettings.MinTemperature = results.getFloatValue(POND_AUTO_SWITCH_TEMPERATURE);
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

    @Override
    public void SetNeededLiters(byte lineNumber, int neededLiters) {
        final int firstNeededLitersRegister = 30;
        int lineRegister = firstNeededLitersRegister + lineNumber - 1;

        ModbusMaster master = CreateMaster();


        int slaveId = 1;
        if (master.testSlaveNode(slaveId))
            try {
                WriteRegisterRequest request = new WriteRegisterRequest(slaveId, lineRegister, neededLiters);
                WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);
            } catch (ModbusTransportException e) {
                e.printStackTrace();
            }
        master.destroy();

    }

    @Override
    public int GetSchedulesCount() {
        ModbusMaster master = CreateMaster();

        int slaveId = 1;

        BatchResults<String> results = null;
        BatchRead<String> batch = new BatchRead<>();

        batch.addLocator(mScheduleIndexAndFlags.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleIndexAndFlags.RegisterNumber, mScheduleIndexAndFlags.RegisterType));

        try {
//            WriteCoilsRequest request = new WriteCoilsRequest(slaveId, COMMAND_OFFSET_SCHEDULES_GET_COUNT, new boolean[]{true});
//            WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);
            WriteCoilRequest request = new WriteCoilRequest(slaveId, COMMAND_OFFSET_SCHEDULES_GET_COUNT, true);
            WriteCoilResponse response = (WriteCoilResponse) master.send(request);
            master.init();
            results = master.send(batch);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            master.destroy();
        }

        if (results != null) {
            byte data = results.getIntValue(mScheduleIndexAndFlags.RegisterId).byteValue();
            return data;
        }
        return 0;
    }

    @Override
    public DrainSchedule GetDrainSchedule(int index) {
        DrainSchedule schedule = new DrainSchedule();
        schedule.Index = (byte) index;

        ModbusMaster master = CreateMaster();
        int slaveId = 1;


        try {
            /// пишем номер нужного регистра
            WriteRegisterRequest writeRegisterRequest = new WriteRegisterRequest(slaveId, mScheduleIndexAndFlags.RegisterNumber, index);
            WriteRegisterResponse writeRegisterResponse = (WriteRegisterResponse) master.send(writeRegisterRequest);

            /// посылаем команту на запись занных
            WriteCoilRequest coilRequest = new WriteCoilRequest(slaveId, COMMAND_OFFSET_SCHEDULES_GET, true);
            WriteCoilResponse coilResponse = (WriteCoilResponse) master.send(coilRequest);

            /// читаем результаты
            BatchResults<String> results = null;
            BatchRead<String> batch = new BatchRead<>();

            batch.addLocator(mScheduleIndexAndFlags.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleIndexAndFlags.RegisterNumber, mScheduleIndexAndFlags.RegisterType));
            batch.addLocator(mScheduleHourMinute.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleHourMinute.RegisterNumber, mScheduleHourMinute.RegisterType));
            batch.addLocator(mScheduleWeekDays.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleWeekDays.RegisterNumber, mScheduleHourMinute.RegisterType));

            batch.addLocator(mScheduleLiters1.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters1.RegisterNumber, mScheduleLiters1.RegisterType));
            batch.addLocator(mScheduleLiters2.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters2.RegisterNumber, mScheduleLiters2.RegisterType));
            batch.addLocator(mScheduleLiters3.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters3.RegisterNumber, mScheduleLiters3.RegisterType));
            batch.addLocator(mScheduleLiters4.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters4.RegisterNumber, mScheduleLiters4.RegisterType));
            batch.addLocator(mScheduleLiters5.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters5.RegisterNumber, mScheduleLiters5.RegisterType));
            batch.addLocator(mScheduleLiters6.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters6.RegisterNumber, mScheduleLiters6.RegisterType));
            batch.addLocator(mScheduleLiters7.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters7.RegisterNumber, mScheduleLiters7.RegisterType));
            batch.addLocator(mScheduleLiters8.RegisterId, BaseLocator.holdingRegister(slaveId, mScheduleLiters8.RegisterNumber, mScheduleLiters8.RegisterType));

            master.init();
            results = master.send(batch);

            if (results != null) {
                schedule.Enabled = ((results.getIntValue(SCHEDULE_INDEX_AND_FLAGS) >> 8) & 1) == 1;
                schedule.WeekDaysBitFlags = results.getIntValue(SCHEDULE_WEEK_DAYS).byteValue();
                Integer hm = results.getIntValue(SCHEDULE_HOUR_MINUTE);
                schedule.Minute = (byte) (hm >> 8);
                schedule.Hour = hm.byteValue();

                schedule.LitersNeeded.clear();
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS1));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS2));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS3));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS4));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS5));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS6));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS7));
                schedule.LitersNeeded.add(results.getIntValue(SCHEDULE_LITERS8));

                schedule.IOSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            schedule.ReceiveException = e;
        } finally {
            master.destroy();
        }


        return schedule;
    }

    @Override
    public void UpdateDrainSchedule(DrainSchedule schedule) {
//        ModbusMaster master = CreateMaster();
//        int slaveId = 1;
//
//        if (master.testSlaveNode(slaveId))
//            try {
//
//                /// пишем данные в регистры Modbus
//                /// пишем номер нужного расписания и флаги
//                int indexAndFlags = schedule.Index;
//                if (schedule.Enabled)
//                    indexAndFlags = indexAndFlags | 256;
//
//                NumericLocator locator = (NumericLocator) BaseLocator.holdingRegister(slaveId, 0, DataType.TWO_BYTE_INT_UNSIGNED);
//                WriteRegistersRequest writeRegisterRequestIndex = new WriteRegistersRequest(slaveId, mScheduleIndexAndFlags.RegisterNumber, new short[]{(short) indexAndFlags});
//                WriteRegistersResponse writeRegisterResponseIndex = (WriteRegistersResponse) master.send(writeRegisterRequestIndex);
//
//                //пишем часы и минуты
//                int HourMinute = (schedule.Minute << 8) + schedule.Hour;
//                WriteRegistersRequest writeRegisterRequestHourMinute = new WriteRegistersRequest(slaveId, mScheduleHourMinute.RegisterNumber, new short[]{schedule.Hour, schedule.Minute});
//                WriteRegistersResponse writeRegisterResponseHourMinute = (WriteRegistersResponse) master.send(writeRegisterRequestHourMinute);
//
//                //пишем флаги дней недели
//                int weekDayFlags = schedule.WeekDaysBitFlags;
//                WriteRegistersRequest writeRegisterRequestWeekDays = new WriteRegistersRequest(slaveId, mScheduleWeekDays.RegisterNumber, new short[]{schedule.WeekDaysBitFlags});
//                WriteRegistersResponse writeRegisterResponseWeekDays = (WriteRegistersResponse) master.send(writeRegisterRequestWeekDays);
//
//                /// пишем литры
//                WriteRegisterRequest writeRegisterRequestLiters1 = new WriteRegisterRequest(slaveId, mScheduleLiters1.RegisterNumber, schedule.LitersNeeded.get(0));
//                WriteRegisterResponse writeRegisterResponseLiters1 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters1);
//                WriteRegisterRequest writeRegisterRequestLiters2 = new WriteRegisterRequest(slaveId, mScheduleLiters2.RegisterNumber, schedule.LitersNeeded.get(1));
//                WriteRegisterResponse writeRegisterResponseLiters2 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters2);
//                WriteRegisterRequest writeRegisterRequestLiters3 = new WriteRegisterRequest(slaveId, mScheduleLiters3.RegisterNumber, schedule.LitersNeeded.get(2));
//                WriteRegisterResponse writeRegisterResponseLiters3 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters3);
//                WriteRegisterRequest writeRegisterRequestLiters4 = new WriteRegisterRequest(slaveId, mScheduleLiters4.RegisterNumber, schedule.LitersNeeded.get(3));
//                WriteRegisterResponse writeRegisterResponseLiters4 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters4);
//                WriteRegisterRequest writeRegisterRequestLiters5 = new WriteRegisterRequest(slaveId, mScheduleLiters5.RegisterNumber, schedule.LitersNeeded.get(4));
//                WriteRegisterResponse writeRegisterResponseLiters5 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters5);
//                WriteRegisterRequest writeRegisterRequestLiters6 = new WriteRegisterRequest(slaveId, mScheduleLiters6.RegisterNumber, schedule.LitersNeeded.get(5));
//                WriteRegisterResponse writeRegisterResponseLiters6 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters6);
//                WriteRegisterRequest writeRegisterRequestLiters7 = new WriteRegisterRequest(slaveId, mScheduleLiters7.RegisterNumber, schedule.LitersNeeded.get(6));
//                WriteRegisterResponse writeRegisterResponseLiters7 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters7);
//                WriteRegisterRequest writeRegisterRequestLiters8 = new WriteRegisterRequest(slaveId, mScheduleLiters8.RegisterNumber, schedule.LitersNeeded.get(7));
//                WriteRegisterResponse writeRegisterResponseLiters8 = (WriteRegisterResponse) master.send(writeRegisterRequestLiters8);
//
//                /// посылаем команту на чтение данных в память контроллера
//                WriteCoilRequest coilRequest = new WriteCoilRequest(slaveId, COMMAND_OFFSET_SCHEDULES_SET, true);
//                WriteCoilResponse coilResponse = (WriteCoilResponse) master.send(coilRequest);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                schedule = null;
//            }

        int retries = 0;
        DrainSchedule result;
        do {
            result = writeScheduleToModbus(schedule);
            retries++;
        } while (!result.IOSuccess && retries < MAX_RETRIES);

        boolean sendSuccess;
        retries = 0;
        if (result.IOSuccess)
            do {
                sendSuccess = sendReadCommandToController();
                retries++;
            } while (!sendSuccess && retries < MAX_RETRIES);

    }

    private boolean sendReadCommandToController() {
        ModbusMaster master = CreateMaster();
        int slaveId = 1;
        boolean sendSuccess = false;

        /// посылаем команту на чтение данных в память контроллера
        WriteCoilRequest coilRequest = null;
        try {
            coilRequest = new WriteCoilRequest(slaveId, COMMAND_OFFSET_SCHEDULES_SET, true);
            WriteCoilResponse coilResponse = (WriteCoilResponse) master.send(coilRequest);
            sendSuccess = true;
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        } finally {
            master.destroy();
        }
        return sendSuccess;
    }

    private DrainSchedule writeScheduleToModbus(DrainSchedule schedule) {
        ArrayList<Byte> scheduleBytes = new ArrayList<>();


        //пишем часы и минуты
        scheduleBytes.add(schedule.Hour);
        scheduleBytes.add(schedule.Minute);

        /// пишем литры
        for (int liters : schedule.LitersNeeded)
            scheduleBytes.addAll(litersToByteArray(liters));

        /// пишем номер нужного расписания и флаги
        scheduleBytes.add(schedule.Index);
        scheduleBytes.add((byte) (schedule.Enabled ? 1 : 0));

        //пишем флаги дней недели и 0 вместо команд
        scheduleBytes.add(schedule.WeekDaysBitFlags);
        scheduleBytes.add((byte) 0);

        ModbusMaster master = CreateMaster();
        int slaveId = 1;

        try {
            WriteRegistersRequest writeRegisterRequestHourMinute =
                    new WriteRegistersRequest(slaveId, mScheduleHourMinute.RegisterNumber, toShortArray(scheduleBytes));
            WriteRegistersResponse writeRegisterResponseHourMinute = (WriteRegistersResponse) master.send(writeRegisterRequestHourMinute);
            schedule.IOSuccess = true;
        } catch (ModbusTransportException e) {
            e.printStackTrace();
            schedule.IOSuccess = false;
            schedule.ReceiveException = e;
        } finally {
            master.destroy();
        }


        return schedule;
    }

    private ArrayList<Byte> litersToByteArray(int liters) {
        ArrayList<Byte> litersList = new ArrayList<>();
        litersList.add((byte) liters);
        litersList.add((byte) (liters >> 8));
        return litersList;
    }

    private short[] toShortArray(ArrayList<Byte> byteList) {
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++)
            byteArray[i] = byteList.get(i).byteValue();

        short[] shortArray = new short[byteArray.length / 2];
        ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortArray);
        return shortArray;
    }
}
