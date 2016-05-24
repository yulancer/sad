package ru.yulancer.sad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matveev_yuri on 10.03.2016.
 */
public class SadInfo implements Parcelable {
    public Exception exception;

    public float SadCurrentTemp;
    public float SadWaterDrainTempSetpoint;
    public boolean SaunaWaterOn;
    public boolean SadWaterOn;
    public boolean PumpPowerOn;
    public boolean PondPowerOn;
    public boolean SadWaterPressureOK;
    public boolean PhotoSensorDark;
    public boolean RainSensorWet;

    public SadInfo() {

    }

    protected SadInfo(Parcel in) {
        SadCurrentTemp = in.readFloat();
        SadWaterDrainTempSetpoint = in.readFloat();

        SaunaWaterOn = in.readByte() != 0;
        SadWaterOn = in.readByte() != 0;
        PumpPowerOn = in.readByte() != 0;
        PondPowerOn = in.readByte() != 0;
        SadWaterPressureOK = in.readByte() != 0;
        PhotoSensorDark = in.readByte() != 0;
        RainSensorWet = in.readByte() != 0;
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
        dest.writeFloat(SadCurrentTemp);
        dest.writeFloat(SadWaterDrainTempSetpoint);

        dest.writeByte((byte) (SaunaWaterOn ? 1 : 0));
        dest.writeByte((byte) (SadWaterOn ? 1 : 0));
        dest.writeByte((byte) (PumpPowerOn ? 1 : 0));
        dest.writeByte((byte) (PondPowerOn ? 1 : 0));
        dest.writeByte((byte) (SadWaterPressureOK ? 1 : 0));
        dest.writeByte((byte) (PhotoSensorDark ? 1 : 0));
        dest.writeByte((byte) (RainSensorWet ? 1 : 0));

    }
}
