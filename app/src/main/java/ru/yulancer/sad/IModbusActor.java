package ru.yulancer.sad;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public interface IModbusActor {
    public SadInfo GetSadInfo();
    public void SendSwitchSignal(byte offset);

    public static final byte GardenWaterOffset = 0;
    public static final byte SaunaWaterOffset = 1;
    public static final byte PumpOffset = 2;
    public static final byte PondOffset = 3;
}
