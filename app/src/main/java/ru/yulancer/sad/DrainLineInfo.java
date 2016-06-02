package ru.yulancer.sad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matveev_yuri on 02.06.2016.
 */
public class DrainLineInfo implements Parcelable {
    public int LineNumber;
    public int LitersNeeded;
    public int LitersDrained;

    public boolean Enabled;
    public boolean Working;
    public boolean Drained;
    public boolean ValveOpen;
    public boolean OnPause;

    public DrainLineInfo() {

    }

    protected DrainLineInfo(Parcel in) {
        LineNumber = in.readInt();
        LitersNeeded = in.readInt();
        LitersDrained = in.readInt();
        Enabled = in.readByte() != 0;
        Working = in.readByte() != 0;
        Drained = in.readByte() != 0;
        ValveOpen = in.readByte() != 0;
        OnPause = in.readByte() != 0;
    }

    public static final Creator<DrainLineInfo> CREATOR = new Creator<DrainLineInfo>() {
        @Override
        public DrainLineInfo createFromParcel(Parcel in) {
            return new DrainLineInfo(in);
        }

        @Override
        public DrainLineInfo[] newArray(int size) {
            return new DrainLineInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(LineNumber);
        dest.writeInt(LitersNeeded);
        dest.writeInt(LitersDrained);
        dest.writeByte((byte) (Enabled ? 1 : 0));
        dest.writeByte((byte) (Working ? 1 : 0));
        dest.writeByte((byte) (Drained ? 1 : 0));
        dest.writeByte((byte) (ValveOpen ? 1 : 0));
        dest.writeByte((byte) (OnPause ? 1 : 0));
    }
}
