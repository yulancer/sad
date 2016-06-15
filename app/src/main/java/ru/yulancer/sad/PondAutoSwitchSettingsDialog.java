package ru.yulancer.sad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Locale;


/**
 * A simple {@link DialogFragment } subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSettingsChangedListener} interface
 * to handle interaction events.
 * Use the {@link PondAutoSwitchSettingsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PondAutoSwitchSettingsDialog extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POND_AUTO_SWITCH_SETTINGS = "PondAutoSwitchSettings";

    // TODO: Rename and change types of parameters
    private PondAutoOnSettings mAutoOnSettings;

    private OnSettingsChangedListener mListener;

    private View mView;

    public PondAutoSwitchSettingsDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param settings PondAutoOnSettings.
     * @return A new instance of fragment ScheduleEditDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static PondAutoSwitchSettingsDialog newInstance(PondAutoOnSettings settings) {
        PondAutoSwitchSettingsDialog fragment = new PondAutoSwitchSettingsDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POND_AUTO_SWITCH_SETTINGS, settings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAutoOnSettings = getArguments().getParcelable(ARG_POND_AUTO_SWITCH_SETTINGS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = getActivity().getLayoutInflater().inflate(R.layout.pond_auto_switch_settings_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("Настройки автовключения пруда").setView(mView)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        CheckBox cbTurnOnPondAuto = (CheckBox) mView.findViewById(R.id.cbTurnOnPondAuto);
        if (cbTurnOnPondAuto != null)
            cbTurnOnPondAuto.setOnClickListener(this);
        CheckBox cbTurnOnPondIfWarm = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWarm);
        if (cbTurnOnPondIfWarm != null)
            cbTurnOnPondIfWarm.setOnClickListener(this);
        CheckBox cbTurnOnPondIfWeekdays = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWeekdays);
        if (cbTurnOnPondIfWeekdays != null)
            cbTurnOnPondIfWeekdays.setOnClickListener(this);
        CheckBox cbTurnOnPondIfTimeLessThan = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfTimeLessThan);
        if (cbTurnOnPondIfTimeLessThan != null)
            cbTurnOnPondIfTimeLessThan.setOnClickListener(this);

        updateControls();
        enableControls();

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsChangedListener) {
            mListener = (OnSettingsChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        updateSettings();
        mListener.onSettingsChanged(mAutoOnSettings);
    }

    private void updateControls() {
        CheckBox cbTurnOnPondAuto = (CheckBox) mView.findViewById(R.id.cbTurnOnPondAuto);
        if (cbTurnOnPondAuto != null)
            cbTurnOnPondAuto.setChecked(mAutoOnSettings.AutoOnWhenDark);

        CheckBox cbTurnOnPondIfNoRain = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfNoRain);
        if (cbTurnOnPondIfNoRain != null)
            cbTurnOnPondIfNoRain.setChecked(mAutoOnSettings.OnlyWhenNoRain);

        CheckBox cbTurnOnPondIfWarm = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWarm);
        if (cbTurnOnPondIfWarm != null)
            cbTurnOnPondIfWarm.setChecked(mAutoOnSettings.OnlyWhenWarm);
        EditText etTurnOnPondIfWarm = (EditText) mView.findViewById(R.id.etTurnOnPondIfWarm);
        if (etTurnOnPondIfWarm != null)
            etTurnOnPondIfWarm.setText(String.format(Locale.getDefault(), "%.0f", mAutoOnSettings.MinTemperature));

        CheckBox cbTurnOnPondIfTimeLessThan = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfTimeLessThan);
        if (cbTurnOnPondIfTimeLessThan != null)
            cbTurnOnPondIfTimeLessThan.setChecked(mAutoOnSettings.OnlyWhenEarly);
        EditText etTurnOnPondIfTimeLessThanHour = (EditText) mView.findViewById(R.id.etTurnOnPondIfTimeLessThanHour);
        if (etTurnOnPondIfTimeLessThanHour != null)
            etTurnOnPondIfTimeLessThanHour.setText(String.format("%d", mAutoOnSettings.Hour));
        EditText etTurnOnPondIfTimeLessThanMinute = (EditText) mView.findViewById(R.id.etTurnOnPondIfTimeLessThanMinute);
        if (etTurnOnPondIfTimeLessThanMinute != null)
            etTurnOnPondIfTimeLessThanMinute.setText(String.format("%d", mAutoOnSettings.Minute));

        CheckBox cbTurnOnPondIfWeekdays = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWeekdays);
        if (cbTurnOnPondIfWeekdays != null)
            cbTurnOnPondIfWeekdays.setChecked(mAutoOnSettings.OnlyCertainWeekdays);
        CheckBox cbTurnOnPondOnSunday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnSunday);
        if (cbTurnOnPondOnSunday != null)
            cbTurnOnPondOnSunday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 0));
        CheckBox cbTurnOnPondOnMonday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnMonday);
        if (cbTurnOnPondOnMonday != null)
            cbTurnOnPondOnMonday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 1));
        CheckBox cbTurnOnPondOnTuesday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnTuesday);
        if (cbTurnOnPondOnTuesday != null)
            cbTurnOnPondOnTuesday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 2));
        CheckBox cbTurnOnPondOnWednesday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnWednesday);
        if (cbTurnOnPondOnWednesday != null)
            cbTurnOnPondOnWednesday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 3));
        CheckBox cbTurnOnPondOnThursday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnThursday);
        if (cbTurnOnPondOnThursday != null)
            cbTurnOnPondOnThursday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 4));
        CheckBox cbTurnOnPondOnFriday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnFriday);
        if (cbTurnOnPondOnFriday != null)
            cbTurnOnPondOnFriday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 5));
        CheckBox cbTurnOnPondOnSaturday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnSaturday);
        if (cbTurnOnPondOnSaturday != null)
            cbTurnOnPondOnSaturday.setChecked(isBitSet(mAutoOnSettings.WeekdayFlags, 6));
    }

    private boolean isBitSet(byte flags, int i) {
        return ((flags >> i) & 1) == 1;
    }

    private void updateSettings() {

        CheckBox cbTurnOnPondAuto = (CheckBox) mView.findViewById(R.id.cbTurnOnPondAuto);
        if (cbTurnOnPondAuto != null)
            mAutoOnSettings.AutoOnWhenDark = cbTurnOnPondAuto.isChecked();

        CheckBox cbTurnOnPondIfNoRain = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfNoRain);
        if (cbTurnOnPondIfNoRain != null)
            mAutoOnSettings.OnlyWhenNoRain = cbTurnOnPondIfNoRain.isChecked();

        CheckBox cbTurnOnPondIfWarm = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWarm);
        if (cbTurnOnPondIfWarm != null)
            mAutoOnSettings.OnlyWhenWarm = cbTurnOnPondIfWarm.isChecked();
        EditText etTurnOnPondIfWarm = (EditText) mView.findViewById(R.id.etTurnOnPondIfWarm);
        if (etTurnOnPondIfWarm != null)
            mAutoOnSettings.MinTemperature = Float.parseFloat(etTurnOnPondIfWarm.getText().toString());

        CheckBox cbTurnOnPondIfTimeLessThan = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfTimeLessThan);
        if (cbTurnOnPondIfTimeLessThan != null)
            mAutoOnSettings.OnlyWhenEarly = cbTurnOnPondIfTimeLessThan.isChecked();
        EditText etTurnOnPondIfTimeLessThanHour = (EditText) mView.findViewById(R.id.etTurnOnPondIfTimeLessThanHour);
        if (etTurnOnPondIfTimeLessThanHour != null)
            mAutoOnSettings.Hour = (byte) Integer.parseInt(etTurnOnPondIfTimeLessThanHour.getText().toString());
        EditText etTurnOnPondIfTimeLessThanMinute = (EditText) mView.findViewById(R.id.etTurnOnPondIfTimeLessThanMinute);
        if (etTurnOnPondIfTimeLessThanMinute != null)
            mAutoOnSettings.Minute = (byte) Integer.parseInt(etTurnOnPondIfTimeLessThanMinute.getText().toString());

        CheckBox cbTurnOnPondIfWeekdays = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWeekdays);
        if (cbTurnOnPondIfWeekdays != null)
            mAutoOnSettings.OnlyCertainWeekdays = cbTurnOnPondIfWeekdays.isChecked();
        CheckBox cbTurnOnPondOnSunday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnSunday);
        if (cbTurnOnPondOnSunday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 0, cbTurnOnPondOnSunday.isChecked());
        CheckBox cbTurnOnPondOnMonday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnMonday);
        if (cbTurnOnPondOnMonday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 1, cbTurnOnPondOnMonday.isChecked());
        CheckBox cbTurnOnPondOnTuesday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnTuesday);
        if (cbTurnOnPondOnTuesday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 2, cbTurnOnPondOnTuesday.isChecked());
        CheckBox cbTurnOnPondOnWednesday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnWednesday);
        if (cbTurnOnPondOnWednesday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 3, cbTurnOnPondOnWednesday.isChecked());
        CheckBox cbTurnOnPondOnThursday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnThursday);
        if (cbTurnOnPondOnThursday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 4, cbTurnOnPondOnThursday.isChecked());
        CheckBox cbTurnOnPondOnFriday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnFriday);
        if (cbTurnOnPondOnFriday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 5, cbTurnOnPondOnFriday.isChecked());
        CheckBox cbTurnOnPondOnSaturday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnSaturday);
        if (cbTurnOnPondOnSaturday != null)
            mAutoOnSettings.WeekdayFlags = setBit(mAutoOnSettings.WeekdayFlags, 6, cbTurnOnPondOnSaturday.isChecked());
    }

    private byte setBit(byte flags, int number, boolean checked) {
        if (checked)
            return (byte) (flags | (1 << number));
        else
            return (byte) (flags & ~(1 << number));
    }

    @Override
    public void onClick(View v) {
        enableControls();
    }

    private void enableControls() {
        CheckBox cbTurnOnPondAuto = (CheckBox) mView.findViewById(R.id.cbTurnOnPondAuto);
        boolean mainChecked =  (cbTurnOnPondAuto != null) && cbTurnOnPondAuto.isChecked();


        CheckBox cbTurnOnPondIfNoRain = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfNoRain);
        if (cbTurnOnPondIfNoRain != null)
            cbTurnOnPondIfNoRain.setEnabled(mainChecked);

        CheckBox cbTurnOnPondIfWarm = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWarm);
        if (cbTurnOnPondIfWarm != null) {
            cbTurnOnPondIfWarm.setEnabled(mainChecked);
            EditText etTurnOnPondIfWarm = (EditText) mView.findViewById(R.id.etTurnOnPondIfWarm);
            if (etTurnOnPondIfWarm != null)
                etTurnOnPondIfWarm.setEnabled(mainChecked && cbTurnOnPondIfWarm.isChecked());
        }

        CheckBox cbTurnOnPondIfTimeLessThan = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfTimeLessThan);
        if (cbTurnOnPondIfTimeLessThan != null) {
            cbTurnOnPondIfTimeLessThan.setEnabled(mainChecked);
            EditText etTurnOnPondIfTimeLessThanHour = (EditText) mView.findViewById(R.id.etTurnOnPondIfTimeLessThanHour);
            if (etTurnOnPondIfTimeLessThanHour != null)
                etTurnOnPondIfTimeLessThanHour.setEnabled(mainChecked && cbTurnOnPondIfTimeLessThan.isChecked());
            EditText etTurnOnPondIfTimeLessThanMinute = (EditText) mView.findViewById(R.id.etTurnOnPondIfTimeLessThanMinute);
            if (etTurnOnPondIfTimeLessThanMinute != null)
                etTurnOnPondIfTimeLessThanMinute.setEnabled(mainChecked && cbTurnOnPondIfTimeLessThan.isChecked());
        }

        CheckBox cbTurnOnPondIfWeekdays = (CheckBox) mView.findViewById(R.id.cbTurnOnPondIfWeekdays);
        if (cbTurnOnPondIfWeekdays != null) {
            cbTurnOnPondIfWeekdays.setEnabled(mainChecked);
            CheckBox cbTurnOnPondOnSunday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnSunday);
            if (cbTurnOnPondOnSunday != null)
                cbTurnOnPondOnSunday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
            CheckBox cbTurnOnPondOnMonday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnMonday);
            if (cbTurnOnPondOnMonday != null)
                cbTurnOnPondOnMonday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
            CheckBox cbTurnOnPondOnTuesday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnTuesday);
            if (cbTurnOnPondOnTuesday != null)
                cbTurnOnPondOnTuesday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
            CheckBox cbTurnOnPondOnWednesday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnWednesday);
            if (cbTurnOnPondOnWednesday != null)
                cbTurnOnPondOnWednesday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
            CheckBox cbTurnOnPondOnThursday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnThursday);
            if (cbTurnOnPondOnThursday != null)
                cbTurnOnPondOnThursday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
            CheckBox cbTurnOnPondOnFriday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnFriday);
            if (cbTurnOnPondOnFriday != null)
                cbTurnOnPondOnFriday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
            CheckBox cbTurnOnPondOnSaturday = (CheckBox) mView.findViewById(R.id.cbTurnOnPondOnSaturday);
            if (cbTurnOnPondOnSaturday != null)
                cbTurnOnPondOnSaturday.setEnabled(mainChecked && cbTurnOnPondIfWeekdays.isChecked());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSettingsChangedListener {
        // TODO: Update argument type and name
        void onSettingsChanged(PondAutoOnSettings settings);
    }
}
