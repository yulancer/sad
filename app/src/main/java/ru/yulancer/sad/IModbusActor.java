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

    DrainSchedule LoadDrainSchedule(int index);

    void UpdateDrainSchedule(DrainSchedule schedule);

    public static final byte ManualDrainOffset = 0;
    public static final byte GardenWaterOffset = 1;
    public static final byte SaunaWaterOffset = 2;
    public static final byte PumpOffset = 3;
    public static final byte PondOffset = 4;
    public static final int COMMAND_OFFSET_GET_SCHEDULES_COUNT = 763;

}
