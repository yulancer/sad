package ru.yulancer.sad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matveev_yuri on 14.06.2016.
 */
public class PondAutoOnSettings implements Parcelable {
    public boolean AutoOnWhenDark;
    public boolean OnlyWhenNoRain;
    public boolean OnlyWhenWarm;
    public boolean OnlyCertainWeekdays;
    public boolean OnlyWhenEarly;

    public byte WeekdayFlags;
    public byte Hour;
    public byte Minute;
    public float MinTemperature;

    public PondAutoOnSettings(){

    }

    public String getDisplayDays() {
        return WeekdaysBitFlagsDecoder.getDisplayDays(WeekdayFlags);
    }

    protected PondAutoOnSettings(Parcel in) {
        AutoOnWhenDark = in.readByte() != 0;
        OnlyWhenNoRain = in.readByte() != 0;
        OnlyWhenWarm = in.readByte() != 0;
        OnlyCertainWeekdays = in.readByte() != 0;
        OnlyWhenEarly = in.readByte() != 0;
        WeekdayFlags = in.readByte();
        Hour = in.readByte();
        Minute = in.readByte();
        MinTemperature = in.readFloat();
    }

    public static final Creator<PondAutoOnSettings> CREATOR = new Creator<PondAutoOnSettings>() {
        @Override
        public PondAutoOnSettings createFromParcel(Parcel in) {
            return new PondAutoOnSettings(in);
        }

        @Override
        public PondAutoOnSettings[] newArray(int size) {
            return new PondAutoOnSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (AutoOnWhenDark ? 1 : 0));
        dest.writeByte((byte) (OnlyWhenNoRain ? 1 : 0));
        dest.writeByte((byte) (OnlyWhenWarm ? 1 : 0));
        dest.writeByte((byte) (OnlyCertainWeekdays ? 1 : 0));
        dest.writeByte((byte) (OnlyWhenEarly ? 1 : 0));
        dest.writeByte(WeekdayFlags);
        dest.writeByte(Hour);
        dest.writeByte(Minute);
        dest.writeFloat(MinTemperature);
    }
}
