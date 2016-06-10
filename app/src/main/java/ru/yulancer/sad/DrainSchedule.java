package ru.yulancer.sad;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by matveev_yuri on 07.06.2016.
 */
public class DrainSchedule implements Parcelable {
    public static final String[] dayNames = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};

    public static final byte WD_MONDAY = 1;
    public static final byte WD_TUESDAY = 2;
    public static final byte WD_WEDNESDAY = 3;
    public static final byte WD_THURSDAY = 4;
    public static final byte WD_FRIDAY = 5;
    public static final byte WD_SATURDAY = 6;
    public static final byte WD_SUNDAY = 7;


    public boolean Enabled;
    public byte Index;
    public String Name;
    public byte WeekDaysBitFlags;
    public byte Hour;
    public byte Minute;
    public ArrayList<Integer> LitersNeeded = new ArrayList<>(8);

    public boolean ReceivedSuccessfully;
    public Exception ReceiveException;

    protected DrainSchedule(Parcel in) {
        Enabled = in.readByte() != 0;
        Index = in.readByte();
        Name = in.readString();
        WeekDaysBitFlags = in.readByte();
        Hour = in.readByte();
        Minute = in.readByte();
        ReceivedSuccessfully = in.readByte() != 0;
    }

    public static final Creator<DrainSchedule> CREATOR = new Creator<DrainSchedule>() {
        @Override
        public DrainSchedule createFromParcel(Parcel in) {
            return new DrainSchedule(in);
        }

        @Override
        public DrainSchedule[] newArray(int size) {
            return new DrainSchedule[size];
        }
    };

    public String getDisplayDays() {
        String result = "";
        for(int i = 0; i < 8; i++){
            boolean dayIsSet = (WeekDaysBitFlags & (1 << i)) == (1 << i);
            if (dayIsSet)
                result = result + (result.length()  < 2 ? "" : ",") + dayNames[i];
        }
        return result;
    }
    public String getDisplayTime() {
        return String.format(Locale.getDefault(), "%d:%d", Hour, Minute);
    }
    public String getDisplayLiters() {
        String result = "";
        for (int i = 0; i < LitersNeeded.size(); i++) {
            int liters = LitersNeeded.get(i);
            if (liters > 0)
                result = result + (result.length() < 2 ? "" : ", ") + String.format(Locale.getDefault(), "%d - %d", i + 1, liters);
        }
        return result;
    }


    public DrainSchedule() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (Enabled ? 1 : 0));
        dest.writeByte(Index);
        dest.writeString(Name);
        dest.writeByte(WeekDaysBitFlags);
        dest.writeByte(Hour);
        dest.writeByte(Minute);
        dest.writeByte((byte) (ReceivedSuccessfully ? 1 : 0));
    }
}
