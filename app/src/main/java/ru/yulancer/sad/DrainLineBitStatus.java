package ru.yulancer.sad;

/**
 * Created by matveev_yuri on 02.06.2016.
 */
public class DrainLineBitStatus {
    private int _status;

    public DrainLineBitStatus(){}

    public DrainLineBitStatus(int status){
        _status = status;
    }

    public boolean isEnabled(){
        return (_status & 1) == 1;
    }
    public boolean isWorking(){
        return (_status & 2) == 2;
    }
    public boolean isDrained(){
        return (_status & 4) == 4;
    }
    public boolean isValveOpen(){
        return (_status & 16) == 16;
    }
    public boolean isOnPause(){
        return (_status & 32) == 32;
    }

    public void UpdateDrainLineInfo(DrainLineInfo lineInfo ){
        lineInfo.Enabled = isEnabled();
        lineInfo.Working = isWorking();
        lineInfo.Drained = isDrained();
        lineInfo.ValveOpen = isValveOpen();
        lineInfo.OnPause = isOnPause();
    }
}
