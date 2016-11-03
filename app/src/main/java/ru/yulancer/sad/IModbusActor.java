package ru.yulancer.sad;

import java.util.ArrayList;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    SadInfo GetSadInfo();

    void SendSwitchSignal(byte offset);

    void SetNeededLiters(byte lineNumber, int neededLiters);

    int GetSchedulesCount();

    DrainSchedule GetDrainSchedule(int index);

    void UpdateDrainSchedule(DrainSchedule schedule);

    void UpdatePondAutoOnSettings(PondAutoOnSettings settings);

    byte ManualDrainOffset = 0;
    byte GardenWaterOffset = 1;
    byte SaunaWaterOffset = 2;
    byte PumpOffset = 3;
    byte PondOffset = 4;
    byte RebootOffset = 6;
    byte PRBaseOffset = 32;
    byte PrOffsetLight = PRBaseOffset;
    byte PrOffsetMosquito = PRBaseOffset + 1;
    byte PrOffsetLed = PRBaseOffset + 2;
    byte PrOffsetPath = PRBaseOffset + 3;
    byte PrOffsetHeat1 = PRBaseOffset + 4;
    byte PrOffsetHeat2 = PRBaseOffset + 5;
    byte PrOffsetHeat3 = PRBaseOffset + 6;
    int COMMAND_OFFSET_SCHEDULES_SET = 776;
    int COMMAND_OFFSET_SCHEDULES_GET = 777;
    int COMMAND_OFFSET_SCHEDULES_CLEAR = 778;
    int COMMAND_OFFSET_SCHEDULES_GET_COUNT = 779;

}
