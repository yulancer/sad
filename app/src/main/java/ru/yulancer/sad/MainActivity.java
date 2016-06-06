package ru.yulancer.sad;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, LitersNeededInputDialog.OnLitersNeededChangedListener {

    private Timer mTimer;
    private IModbusActor mActivityActor = new Modbus4jActor("192.168.1.78", 502);
    //private IModbusActor mActivityActor = new Modbus4jActor("10.0.2.2", 502);
    private SadInfo mSadInfo = new SadInfo();

    private ArrayList<DrainLineControl> mDrainLineControls;

    //////////////////////
    ///@Overridden methods
    //////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabs = (TabHost) findViewById(R.id.tabhost);

        tabs.setup();


        TabHost.TabSpec spec = tabs.newTabSpec("tagDrain");
        spec.setContent(R.id.layoutDrain);
        spec.setIndicator("Полив");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tagWater");
        spec.setContent(R.id.layoutWater);
        spec.setIndicator("Вода");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tagSettings");
        spec.setContent(R.id.layoutSettings);
        spec.setIndicator("Настройки");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);


        Switch swGardenWater = (Switch) findViewById(R.id.swGardenWater);
        if (swGardenWater != null) {
            swGardenWater.setOnCheckedChangeListener(this);
        }
        Switch swSaunaWater = (Switch) findViewById(R.id.swSaunaWater);
        if (swSaunaWater != null) {
            swSaunaWater.setOnCheckedChangeListener(this);
        }
        Switch swPump = (Switch) findViewById(R.id.swPump);
        if (swPump != null) {
            swPump.setOnCheckedChangeListener(this);
        }
        Switch swPond = (Switch) findViewById(R.id.swPond);
        if (swPond != null) {
            swPond.setOnCheckedChangeListener(this);
        }

        Switch swAutoDrain = (Switch) findViewById(R.id.swAutoDrain);
        if (swAutoDrain != null) {
            swAutoDrain.setOnCheckedChangeListener(this);
        }

        ViewGroup root = (ViewGroup) findViewById(R.id.layoutDrain);
        mDrainLineControls = drainLineControlList(root);
        recreateRefreshTimer();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        byte offset = -1;
        boolean switchNeeded = false;

        switch (buttonView.getId()) {
            case R.id.swPump:
                offset = IModbusActor.PumpOffset;
                switchNeeded = isChecked != mSadInfo.PumpPowerOn;
                break;
            case R.id.swPond:
                offset = IModbusActor.PondOffset;
                switchNeeded = isChecked != mSadInfo.PondPowerOn;
                break;
            case R.id.swGardenWater:
                offset = IModbusActor.GardenWaterOffset;
                switchNeeded = isChecked != mSadInfo.GardenWaterOn;
                break;
            case R.id.swSaunaWater:
                offset = IModbusActor.SaunaWaterOffset;
                switchNeeded = isChecked != mSadInfo.SaunaWaterOn;
                break;
            case R.id.swAutoDrain:
                offset = IModbusActor.ManualDrainOffset;
                switchNeeded = isChecked != mSadInfo.AutoDrainOn;
                break;
            default:
                switchNeeded = false;
        }
        if (switchNeeded) {
            StartStopSomethingTask task = new StartStopSomethingTask();
            task.execute(offset);
        }
    }


    //////////////////
    ///public
    /////////////////
    public void ShowNeededLitersDialog(int lineNumber) {
        int litersNeeded = mSadInfo.LineStatuses[lineNumber - 1].LitersNeeded;
        mTimer.cancel();
        FragmentManager fm = getSupportFragmentManager();
        LitersNeededInputDialog dialog = LitersNeededInputDialog.newInstance((byte) lineNumber, litersNeeded);
        dialog.show(fm, "start");
    }

    public void StartDrainLine(byte lineNumber, boolean isChecked) {
        boolean isValveOpen = mSadInfo.LineStatuses[lineNumber].ValveOpen;
        boolean switchNeeded = isChecked != isValveOpen;
        if (switchNeeded) {
            byte offset = (byte) (lineNumber + 7);
            StartStopSomethingTask task = new StartStopSomethingTask();
            task.execute(offset);
        }
    }


    //////////////////
    ///private
    /////////////////

    private void switchProgress(boolean on) {
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
        if (connectIcon != null)
            connectIcon.setVisibility(on ? View.GONE : View.VISIBLE);
        if (bar != null)
            bar.setVisibility(on ? View.VISIBLE : View.GONE);
    }

    private void destroyRefreshTimer() {
        if (mTimer != null)
            mTimer.cancel();
    }

    private void recreateRefreshTimer() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        SadQueryTask sadQueryTask = new SadQueryTask();
        mTimer.schedule(sadQueryTask, 1000, 30000);
    }

    private void RefreshSadInfo() {

        TextView tvException = (TextView) findViewById(R.id.tvException);

        if (mSadInfo.exception == null) {
            Switch swGardenWater = (Switch) findViewById(R.id.swGardenWater);
            if (swGardenWater != null) {
                swGardenWater.setChecked(mSadInfo.GardenWaterOn);
            }
            Switch swSaunaWater = (Switch) findViewById(R.id.swSaunaWater);
            if (swSaunaWater != null) {
                swSaunaWater.setChecked(mSadInfo.SaunaWaterOn);
            }
            Switch swPump = (Switch) findViewById(R.id.swPump);
            if (swPump != null) {
                swPump.setChecked(mSadInfo.PumpPowerOn);
            }
            Switch swPond = (Switch) findViewById(R.id.swPond);
            if (swPond != null) {
                swPond.setChecked(mSadInfo.PondPowerOn);
            }

            CheckBox cbPressure = (CheckBox) findViewById(R.id.cbPressure);
            if (cbPressure != null) {
                cbPressure.setChecked(mSadInfo.SadWaterPressureOK);
            }
            CheckBox cbIsNight = (CheckBox) findViewById(R.id.cbIsNight);
            if (cbIsNight != null) {
                cbIsNight.setChecked(mSadInfo.PhotoSensorDark);
            }
            CheckBox cbIsRain = (CheckBox) findViewById(R.id.cbIsRain);
            if (cbIsRain != null) {
                cbIsRain.setChecked(mSadInfo.RainSensorWet);
            }
            CheckBox cbIsFrost = (CheckBox) findViewById(R.id.cbIsFrost);
            if (cbIsFrost != null) {
                cbIsFrost.setChecked(mSadInfo.Frost);
            }

            Switch swAutoDrain = (Switch) findViewById(R.id.swAutoDrain);
            if (swAutoDrain != null) {
                swAutoDrain.setChecked(mSadInfo.AutoDrainOn);
            }

            for (int i = 1; i <= 8; i++) {
                DrainLineControl lineControl = findByLineNumber(i);
                if (lineControl != null) {
                    updateDrainLineControl(lineControl, mSadInfo.LineStatuses[i - 1]);
                }
            }

            if (tvException != null && tvException.getVisibility() != View.GONE) {
                tvException.setVisibility(View.GONE);
                tvException.setText("");
            }


            ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
            if (connectIcon != null)
                connectIcon.setImageResource(R.drawable.ic_connect);
            TextView tvLastSuccessfulQuery = (TextView) findViewById(R.id.tvLastSuccessfulQuery);
            if (tvLastSuccessfulQuery != null) {
                DateTimeFormatter fmt = DateTimeFormat.fullTime().withLocale(getResources().getConfiguration().locale);
                tvLastSuccessfulQuery.setText(String.format("Данные на %s", fmt.print(new LocalTime())));
            }

        } else {
            if (tvException != null) {
                tvException.setText(mSadInfo.exception.getLocalizedMessage());
                tvException.setVisibility(View.VISIBLE);
            }
            ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
            if (connectIcon != null)
                connectIcon.setImageResource(R.drawable.ic_disconnect);
        }
    }

    private void updateDrainLineControl(DrainLineControl lineControl, DrainLineInfo lineStatus) {
        TextView tvLitersNeeded = (TextView) lineControl.findViewById(R.id.tvLitersNeeded);
        if (tvLitersNeeded != null)
            tvLitersNeeded.setText(String.format(Locale.getDefault(), "%d", lineStatus.LitersNeeded));

        CheckBox cbStart = (CheckBox) lineControl.findViewById(R.id.cbStart);
        if (cbStart != null) {
            boolean isValveOpen = cbStart.isChecked();
            if (isValveOpen != lineStatus.ValveOpen)
                cbStart.setChecked(lineStatus.ValveOpen);
        }

        ProgressTextView pbDrainProgress = (ProgressTextView) lineControl.findViewById(R.id.pbDrainProgress);
        if (pbDrainProgress != null) {
            if (pbDrainProgress.getMaxValue() != lineStatus.LitersNeeded)
                pbDrainProgress.setMaxValue(lineStatus.LitersNeeded);
            if (pbDrainProgress.getValue() != lineStatus.LitersDrained)
                pbDrainProgress.setValue(lineStatus.LitersDrained);

            if (pbDrainProgress.getVisibility() == View.VISIBLE) {
                if (!lineStatus.Working)  //спрячем неработающую, но видимую
                    pbDrainProgress.setVisibility(View.INVISIBLE);
            } else {
                if (lineStatus.Working)  //покажем работающую, но невидимую
                    pbDrainProgress.setVisibility(View.VISIBLE);
            }
        }

    }

    private DrainLineControl findByLineNumber(int lineNumber) {
        DrainLineControl controlFounded = null;
        for (DrainLineControl drainLineControl : mDrainLineControls

                ) {
            if (drainLineControl.getLineNumber() == lineNumber)
                controlFounded = drainLineControl;
        }
        return controlFounded;
    }

    private ArrayList<DrainLineControl> drainLineControlList(ViewGroup root) {
        ArrayList<DrainLineControl> drainLineControlList = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof DrainLineControl) {
                drainLineControlList.add((DrainLineControl) child);
            } else if (child instanceof ViewGroup) {
                drainLineControlList.addAll(drainLineControlList((ViewGroup) child));
            }
        }
        return drainLineControlList;
    }

    @Override
    public void onLitersNeededChanged(byte lineNumber, int litersNeeded) {

        // сразу обновим показания на экране в расчете на передачу без ошибок
        DrainLineControl lineControl = mDrainLineControls.get(lineNumber - 1);
        if (lineControl != null) {
            int newLiters = litersNeeded > 0 ? litersNeeded : 0;
            TextView tvLitersNeeded = (TextView) lineControl.findViewById(R.id.tvLitersNeeded);
            if (tvLitersNeeded != null)
                tvLitersNeeded.setText(newLiters + "");
        }

        SetNeededLitersTaskParams taskParams = new SetNeededLitersTaskParams(lineNumber, litersNeeded);
        SetNeededLitersTask task = new SetNeededLitersTask();
        task.execute(taskParams);
    }


    /******************/
    /*  классы тасков */

    /*****************/
    class SadQueryTask extends TimerTask {

        private void switchProgress(boolean on) {
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            ImageView connectIcon = (ImageView) findViewById(R.id.ivConnectStatus);
            if (connectIcon != null)
                connectIcon.setVisibility(on ? View.GONE : View.VISIBLE);
            if (bar != null)
                bar.setVisibility(on ? View.VISIBLE : View.GONE);
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchProgress(true);
                }
            });

            MainActivity.this.mSadInfo = mActivityActor.GetSadInfo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.RefreshSadInfo();
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchProgress(false);
                }
            });
        }

    }

    abstract class BaseCommunicationTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            destroyRefreshTimer();
            switchProgress(true);
        }

        @Override
        protected void onPostExecute(Void params) {
            switchProgress(false);
            recreateRefreshTimer();
        }
    }

    class StartStopSomethingTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Object... params) {
            byte offset = (byte) params[0];
            mActivityActor.SendSwitchSignal(offset);
            return null;
        }
    }

    class SetNeededLitersTaskParams {
        public byte mLineNumber;
        public int mNeededLiters;

        public SetNeededLitersTaskParams(byte lineNumber, int neededLiters) {
            mLineNumber = lineNumber;
            mNeededLiters = neededLiters;
        }
    }

    class SetNeededLitersTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Object... params) {
            SetNeededLitersTaskParams param = (SetNeededLitersTaskParams) params[0];
            mActivityActor.SetNeededLiters(param.mLineNumber, param.mNeededLiters);
            return null;
        }
    }
}
