package ru.yulancer.sad;

import java.util.ArrayList;

/**
 * Created by matveev_yuri on 07.06.2016.
 */
public class DrainSchedule {
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

    public DrainSchedule() {
    }
}
