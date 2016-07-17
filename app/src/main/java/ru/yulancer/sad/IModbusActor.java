package ru.yulancer.sad;

import java.util.ArrayList;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    public SadInfo GetSadInfo();

    public void SendSwitchSignal(byte offset);

    public void SetNeededLiters(byte lineNumber, int neededLiters);

    int GetSchedulesCount();

    DrainSchedule GetDrainSchedule(int index);

    void UpdateDrainSchedule(DrainSchedule schedule);

    void UpdatePondAutoOnSettings(PondAutoOnSettings settings);

    public static final byte ManualDrainOffset = 0;
    public static final byte GardenWaterOffset = 1;
    public static final byte SaunaWaterOffset = 2;
    public static final byte PumpOffset = 3;
    public static final byte PondOffset = 4;
    public static final byte PRBaseOffset = 32;
    public static final byte PrOffsetLight = PRBaseOffset;
    public static final byte PrOffsetMosquito = PRBaseOffset + 1;
    public static final byte PrOffsetLed = PRBaseOffset + 2;
    public static final byte PrOffsetPath = PRBaseOffset + 3;
    public static final byte PrOffsetHeat1 = PRBaseOffset + 4;
    public static final byte PrOffsetHeat2 = PRBaseOffset + 5;
    public static final byte PrOffsetHeat3 = PRBaseOffset + 6;
    public static final int COMMAND_OFFSET_SCHEDULES_SET = 776;
    public static final int COMMAND_OFFSET_SCHEDULES_GET = 777;
    public static final int COMMAND_OFFSET_SCHEDULES_CLEAR = 778;
    public static final int COMMAND_OFFSET_SCHEDULES_GET_COUNT = 779;

}
