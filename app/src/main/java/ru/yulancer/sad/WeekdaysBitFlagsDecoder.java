package ru.yulancer.sad;

/**
 * Created by matveev_yuri on 15.06.2016.
 */
public class WeekdaysBitFlagsDecoder {
    public static final String[] dayNames = {"вс", "пн", "вт", "ср", "чт", "пт", "сб" };

    public static String getDisplayDays(byte dayFlags) {
        String result = "";
        for(int i = 0; i < 7; i++){
            boolean dayIsSet = (dayFlags & (1 << i)) == (1 << i);
            if (dayIsSet)
                result = result + (result.length()  < 2 ? "" : ",") + dayNames[i];
        }
        return result;
    }
}
