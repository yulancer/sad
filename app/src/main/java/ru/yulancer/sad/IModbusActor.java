package ru.yulancer.sad;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    public SadInfo GetSadInfo();
    public void SendSwitchSignal(byte offset);

    public static final byte ManualDrainOffset = 0;
    public static final byte GardenWaterOffset = 1;
    public static final byte SaunaWaterOffset = 2;
    public static final byte PumpOffset = 3;
    public static final byte PondOffset = 4;
}
