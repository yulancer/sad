package ru.yulancer.sad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    private IModbusActor mActivityActor = new Modbus4jActor("192.168.1.78", 502);

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
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        byte offset;
        switch (buttonView.getId()) {
            case R.id.swPump:
                offset = IModbusActor.PumpOffset;
            case R.id.swPond:
                offset = IModbusActor.PondOffset;
            case R.id.swGardenWater:
                offset = IModbusActor.GardenWaterOffset;
            case R.id.swSaunaWater:
                offset = IModbusActor.SaunaWaterOffset;
            default:
                offset = -1;
        }
        if (offset > -1) {

        }
    }
}
