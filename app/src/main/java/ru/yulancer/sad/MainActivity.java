package ru.yulancer.sad;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Timer mTimer;
    private IModbusActor mActivityActor = new Modbus4jActor("192.168.1.78", 502);
    private SadInfo mSadInfo = new SadInfo();

    //////////////////////
    ///@Overridden methods
    //////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabs = (TabHost) findViewById(R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tagWater");

        spec.setContent(R.id.layoutWater);
        spec.setIndicator("Вода");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tagDrain");
        spec.setContent(R.id.layoutDrain);
        spec.setIndicator("Полив");
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
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        byte offset;
        switch (buttonView.getId()) {
            case R.id.swPump:
                offset = IModbusActor.PumpOffset;
                break;
            case R.id.swPond:
                offset = IModbusActor.PondOffset;
                break;
            case R.id.swGardenWater:
                offset = IModbusActor.GardenWaterOffset;
                break;
            case R.id.swSaunaWater:
                offset = IModbusActor.SaunaWaterOffset;
                break;
            default:
                offset = -1;
        }
        if (offset > -1) {
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
        SaunaQueryTask saunaQueryTask = new SaunaQueryTask();
        mTimer.schedule(saunaQueryTask, 1000, 30000);
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

    /*  классы тасков */
    class SaunaQueryTask extends TimerTask {

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
}
