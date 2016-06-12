package ru.yulancer.sad;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        LitersNeededInputDialog.OnLitersNeededChangedListener,
        ScheduleEditDialog.OnScheduleChangedListener {

    private Timer mTimer;
    private IModbusActor mActivityActor = new Modbus4jActor("192.168.1.78", 502);
    //private IModbusActor mActivityActor = new Modbus4jActor("10.0.2.2", 502);
    private SadInfo mSadInfo = new SadInfo();

    private ArrayList<DrainLineControl> mDrainLineControls;

    private int mScheduleCount;
    private DrainSchedule mSchedule;
    private ArrayList<DrainSchedule> mScheduleArray = new ArrayList<>();
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
        spec.setContent(R.id.layoutSchedule);
        spec.setIndicator("Расписание");
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


        ImageButton ibScheduleRefresh = (ImageButton) findViewById(R.id.ibScheduleRefresh);
        if (ibScheduleRefresh != null)
            ibScheduleRefresh.setOnClickListener(this);

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

            TextView tvAirTemp = (TextView) findViewById(R.id.tvAirTemp);
            if (tvAirTemp != null) {
                String temperatureText = String.format(Locale.getDefault(), "%.1f °C", mSadInfo.AirTemperature);
                tvAirTemp.setText(temperatureText);
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

    @Override
    public void onScheduleChanged(int index, DrainSchedule schedule) {
        mScheduleArray.set(index - 1, schedule);
        refreshListView();
        SetScheduleTask task = new SetScheduleTask();
        task.execute(schedule);
    }

    @Override
    public void onClick(View v) {
        boolean switchNeeded = false;

        switch (v.getId()) {
            case R.id.ibScheduleRefresh:
                loadSchedules();
                switchNeeded = false;
                break;
            case R.id.btnScheduleEdit:
                int index = (int) v.getTag();
                editSchedule(index);
                break;
            default:
                switchNeeded = false;
        }
        if (switchNeeded) {
            DrainSchedule ds = new DrainSchedule();
            ds.Index = 1;
            ds.Hour = 23;
            ds.Minute = 59;
            ds.Enabled = true;
            ds.WeekDaysBitFlags = 15;
            ds.LitersNeeded.add(100);
            ds.LitersNeeded.add(200);
            ds.LitersNeeded.add(300);
            ds.LitersNeeded.add(400);
            ds.LitersNeeded.add(0);
            ds.LitersNeeded.add(666);
            ds.LitersNeeded.add(777);
            ds.LitersNeeded.add(888);

            //SetScheduleTask task = new SetScheduleTask();
            //task.execute(ds);
            //GetScheduleTask task = new GetScheduleTask();
            //task.execute(1);
        }


    }

    private void editSchedule(int index) {
        mTimer.cancel();
        FragmentManager fm = getSupportFragmentManager();
        ScheduleEditDialog dialog = ScheduleEditDialog.newInstance(index, mScheduleArray.get(index - 1));
        dialog.show(fm, "edit");
    }

    private void loadSchedules() {
        RefreshSchedulesTask task = new RefreshSchedulesTask();
        task.execute();
    }

    private void refreshListView() {
        // имена атрибутов для Map
        final String ATTRIBUTE_NAME_ENABLED = "scheduleEnabled";
        final String ATTRIBUTE_NAME_TIME = "scheduleTime";
        final String ATTRIBUTE_NAME_LITERS = "scheduleLiters";
        final String ATTRIBUTE_NAME_INDEX = "scheduleIndex";
        final String ATTRIBUTE_NAME_TITLE = "scheduleTitle";

        // упаковываем данные в понятную для адаптера структуру
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                mScheduleArray.size());
        Map<String, Object> m;
        for (int i = 0; i < mScheduleArray.size(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_ENABLED, mScheduleArray.get(i).Enabled);
            m.put(ATTRIBUTE_NAME_TIME, mScheduleArray.get(i).getDisplayTime() + " в " + mScheduleArray.get(i).getDisplayDays());
            m.put(ATTRIBUTE_NAME_LITERS, mScheduleArray.get(i).getDisplayLiters());
            m.put(ATTRIBUTE_NAME_TITLE, "Расписание " + (i + 1));
            m.put(ATTRIBUTE_NAME_INDEX, i + 1);
            data.add(m);
        }
        String[] from = {ATTRIBUTE_NAME_ENABLED, ATTRIBUTE_NAME_TIME,
                ATTRIBUTE_NAME_LITERS, ATTRIBUTE_NAME_TITLE, ATTRIBUTE_NAME_INDEX};
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = {R.id.cbScheduleOn, R.id.tvScheduleTime, R.id.tvScheduleLiters, R.id.tvScheduleTitle, R.id.btnScheduleEdit};

        // создаем адаптер
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.schedule_list_item,
                from, to);
        // Указываем адаптеру свой биндер
        sAdapter.setViewBinder(new IndexViewBinder());
        // определяем список и присваиваем ему адаптер
        ListView lvSchedule = (ListView) findViewById(R.id.lvSchedule);
        lvSchedule.setAdapter(sAdapter);
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

    class RefreshSchedulesTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Object... params) {
            mScheduleCount = mActivityActor.GetSchedulesCount();
            mScheduleArray.clear();
            for (int index = 1; index <= mScheduleCount; index++) {
                DrainSchedule schedule;
                int retryCount = 0;
                do {
                    schedule = mActivityActor.GetDrainSchedule(index);
                    retryCount++;
                } while (!schedule.IOSuccess && retryCount < 3);
                if (schedule.IOSuccess)
                    mScheduleArray.add(schedule);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            refreshListView();
        }
    }


    class IndexViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.btnScheduleEdit) {
                view.setTag(data);
                return true;
            }
            return false;
        }
    }

    class GetScheduleTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Object... params) {
            int index = (int) params[0];
            mSchedule = mActivityActor.GetDrainSchedule(index);
            return null;
        }
    }

    class GetSchedulesCountTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Object... params) {
            mScheduleCount = mActivityActor.GetSchedulesCount();
            return null;
        }

    }

    class SetScheduleTask extends BaseCommunicationTask {

        @Override
        protected Void doInBackground(Object... params) {
            DrainSchedule schedule = (DrainSchedule) params[0];
            mActivityActor.UpdateDrainSchedule(schedule);
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
