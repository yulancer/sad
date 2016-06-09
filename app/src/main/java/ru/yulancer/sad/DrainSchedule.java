package ru.yulancer.sad;

import android.text.TextUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by matveev_yuri on 07.06.2016.
 */
public class DrainSchedule {
    public static String[] dayNames = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};

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

    public String getDisplayDays() {
        String result = "";
        for(int i = 0; i < 8; i++){
            boolean dayIsSet = (WeekDaysBitFlags & (1 << i)) == (1 << i);
            if (dayIsSet)
                result = result + (i == 0 ? "" : ",") + dayNames[i];
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
                result = result + (i == 0 ? "" : ",") + String.format(Locale.getDefault(), "%d - %d", i + 1, liters);
        }
        return result;
    }

    public ArrayList<Integer> LitersNeeded = new ArrayList<>(8);

    public DrainSchedule() {
    }
}
