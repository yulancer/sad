package ru.yulancer.sad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class SadInfo implements Parcelable {
    public Exception exception;

    public float CurrentAirTemp;
    public float GardenWaterDrainTempSetpoint;
    public boolean SaunaWaterOn;
    public boolean GardenWaterOn;
    public boolean PumpPowerOn;
    public boolean PondPowerOn;
    public boolean SadWaterPressureOK;
    public boolean PhotoSensorDark;
    public boolean RainSensorWet;
    public boolean Frost;

    public byte ValveOpenStatuses;

    public float AirTemperature;
    public float FrostTemperature;

    public int LitersDrained1;
    public int LitersDrained2;
    public int LitersDrained3;
    public int LitersDrained4;
    public int LitersDrained5;
    public int LitersDrained6;
    public int LitersDrained7;
    public int LitersDrained8;

    public int LitersNeeded1;
    public int LitersNeeded2;
    public int LitersNeeded3;
    public int LitersNeeded4;
    public int LitersNeeded5;
    public int LitersNeeded6;
    public int LitersNeeded7;
    public int LitersNeeded8;


    public SadInfo() {

    }

    protected SadInfo(Parcel in) {
        CurrentAirTemp = in.readFloat();
        GardenWaterDrainTempSetpoint = in.readFloat();

        SaunaWaterOn = in.readByte() != 0;
        GardenWaterOn = in.readByte() != 0;
        PumpPowerOn = in.readByte() != 0;
        PondPowerOn = in.readByte() != 0;
        SadWaterPressureOK = in.readByte() != 0;
        PhotoSensorDark = in.readByte() != 0;
        RainSensorWet = in.readByte() != 0;
        Frost = in.readByte() != 0;

        ValveOpenStatuses = in.readByte();

        AirTemperature = in.readFloat();
        FrostTemperature = in.readFloat();

        LitersDrained1 = in.readInt();
        LitersDrained2 = in.readInt();
        LitersDrained3 = in.readInt();
        LitersDrained4 = in.readInt();
        LitersDrained5 = in.readInt();
        LitersDrained6 = in.readInt();
        LitersDrained7 = in.readInt();
        LitersDrained8 = in.readInt();

        LitersNeeded1 = in.readInt();
        LitersNeeded2 = in.readInt();
        LitersNeeded3 = in.readInt();
        LitersNeeded4 = in.readInt();
        LitersNeeded5 = in.readInt();
        LitersNeeded6 = in.readInt();
        LitersNeeded7 = in.readInt();
        LitersNeeded8 = in.readInt();

    }

    public static final Creator<SadInfo> CREATOR = new Creator<SadInfo>() {
        @Override
        public SadInfo createFromParcel(Parcel in) {
            return new SadInfo(in);
        }

        @Override
        public SadInfo[] newArray(int size) {
            return new SadInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(CurrentAirTemp);
        dest.writeFloat(GardenWaterDrainTempSetpoint);

        dest.writeByte((byte) (SaunaWaterOn ? 1 : 0));
        dest.writeByte((byte) (GardenWaterOn ? 1 : 0));
        dest.writeByte((byte) (PumpPowerOn ? 1 : 0));
        dest.writeByte((byte) (PondPowerOn ? 1 : 0));
        dest.writeByte((byte) (SadWaterPressureOK ? 1 : 0));
        dest.writeByte((byte) (PhotoSensorDark ? 1 : 0));
        dest.writeByte((byte) (RainSensorWet ? 1 : 0));
        dest.writeByte((byte) (Frost ? 1 : 0));

        dest.writeByte(ValveOpenStatuses);

        dest.writeFloat(AirTemperature);
        dest.writeFloat(FrostTemperature);
    }
}
