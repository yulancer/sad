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


/**
 * A simple {@link DialogFragment } subclass.
 * Activities that contain this fragment must implement the
 * {@link OnScheduleChangedListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleEditDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleEditDialog extends DialogFragment implements DialogInterface.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCHEDULE_INDEX = "ScheduleIndex";
    private static final String ARG_DRAIN_SCHEDULE = "DrainSchedule";

    // TODO: Rename and change types of parameters
    private int mScheduleIndex;
    private DrainSchedule mDrainSchedule;

    private OnScheduleChangedListener mListener;

    private View mView;

    public ScheduleEditDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param scheduleIndex номер расписания.
     * @param drainSchedule расписание.
     * @return A new instance of fragment ScheduleEditDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleEditDialog newInstance(int scheduleIndex, DrainSchedule drainSchedule) {
        ScheduleEditDialog fragment = new ScheduleEditDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SCHEDULE_INDEX, scheduleIndex);
        args.putParcelable(ARG_DRAIN_SCHEDULE, drainSchedule);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScheduleIndex = getArguments().getInt(ARG_SCHEDULE_INDEX);
            mDrainSchedule = getArguments().getParcelable(ARG_DRAIN_SCHEDULE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = getActivity().getLayoutInflater().inflate(R.layout.fragment_schedule_edit_input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String newText = String.format("Расписание номер %d", mScheduleIndex);
        Dialog dialog = builder.setTitle(newText).setView(mView)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();

        updateControls();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScheduleChangedListener) {
            mListener = (OnScheduleChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScheduleChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        updateSchedule();
        mListener.onScheduleChanged(mScheduleIndex, mDrainSchedule);
    }

    private void updateControls() {
        CheckBox cbScheduleOn = (CheckBox) mView.findViewById(R.id.cbScheduleEditEnabled);
        if (cbScheduleOn != null)
            cbScheduleOn.setChecked(mDrainSchedule.Enabled);

        EditText etScheduleEditHour = (EditText) mView.findViewById(R.id.etScheduleEditHour);
        if (etScheduleEditHour != null)
            etScheduleEditHour.setText(mDrainSchedule.Hour + "");

        EditText etScheduleEditMinute = (EditText) mView.findViewById(R.id.etScheduleEditMinute);
        if (etScheduleEditMinute != null)
            etScheduleEditMinute.setText(mDrainSchedule.Minute + "");


        EditText etScheduleEditLiters1 = (EditText) mView.findViewById(R.id.etScheduleEditLiters1);
        if (etScheduleEditLiters1 != null)
            etScheduleEditLiters1.setText(mDrainSchedule.LitersNeeded.get(0) + "");
        EditText etScheduleEditLiters2 = (EditText) mView.findViewById(R.id.etScheduleEditLiters2);
        if (etScheduleEditLiters2 != null)
            etScheduleEditLiters2.setText(mDrainSchedule.LitersNeeded.get(1) + "");
        EditText etScheduleEditLiters3 = (EditText) mView.findViewById(R.id.etScheduleEditLiters3);
        if (etScheduleEditLiters3 != null)
            etScheduleEditLiters3.setText(mDrainSchedule.LitersNeeded.get(2) + "");
        EditText etScheduleEditLiters4 = (EditText) mView.findViewById(R.id.etScheduleEditLiters4);
        if (etScheduleEditLiters4 != null)
            etScheduleEditLiters4.setText(mDrainSchedule.LitersNeeded.get(3) + "");
        EditText etScheduleEditLiters5 = (EditText) mView.findViewById(R.id.etScheduleEditLiters5);
        if (etScheduleEditLiters5 != null)
            etScheduleEditLiters5.setText(mDrainSchedule.LitersNeeded.get(4) + "");
        EditText etScheduleEditLiters6 = (EditText) mView.findViewById(R.id.etScheduleEditLiters6);
        if (etScheduleEditLiters6 != null)
            etScheduleEditLiters6.setText(mDrainSchedule.LitersNeeded.get(5) + "");
        EditText etScheduleEditLiters7 = (EditText) mView.findViewById(R.id.etScheduleEditLiters7);
        if (etScheduleEditLiters7 != null)
            etScheduleEditLiters7.setText(mDrainSchedule.LitersNeeded.get(6) + "");
        EditText etScheduleEditLiters8 = (EditText) mView.findViewById(R.id.etScheduleEditLiters8);
        if (etScheduleEditLiters8 != null)
            etScheduleEditLiters8.setText(mDrainSchedule.LitersNeeded.get(7) + "");

        CheckBox cbScheduleEditSunday = (CheckBox) mView.findViewById(R.id.cbScheduleEditSunday);
        if (cbScheduleEditSunday != null)
            cbScheduleEditSunday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 0));
        CheckBox cbScheduleEditMonday = (CheckBox) mView.findViewById(R.id.cbScheduleEditMonday);
        if (cbScheduleEditMonday != null)
            cbScheduleEditMonday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 1));
        CheckBox cbScheduleEditTuesday = (CheckBox) mView.findViewById(R.id.cbScheduleEditTuesday);
        if (cbScheduleEditTuesday != null)
            cbScheduleEditTuesday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 2));
        CheckBox cbScheduleEditWednesday = (CheckBox) mView.findViewById(R.id.cbScheduleEditWednesday);
        if (cbScheduleEditWednesday != null)
            cbScheduleEditWednesday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 3));
        CheckBox cbScheduleEditThursday = (CheckBox) mView.findViewById(R.id.cbScheduleEditThursday);
        if (cbScheduleEditThursday != null)
            cbScheduleEditThursday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 4));
        CheckBox cbScheduleEdiFriday = (CheckBox) mView.findViewById(R.id.cbScheduleEditFriday);
        if (cbScheduleEdiFriday != null)
            cbScheduleEdiFriday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 5));
        CheckBox cbScheduleEditSaturday = (CheckBox) mView.findViewById(R.id.cbScheduleEditSaturday);
        if (cbScheduleEditSaturday != null)
            cbScheduleEditSaturday.setChecked(isBitSet(mDrainSchedule.WeekDaysBitFlags, 6));

    }

    private boolean isBitSet(byte flags, int i) {
        return ((flags >> i) & 1) == 1;
    }

    private void updateSchedule() {
        CheckBox cbScheduleOn = (CheckBox) getDialog().getWindow().findViewById(R.id.cbScheduleEditEnabled);
        if (cbScheduleOn != null)
            mDrainSchedule.Enabled = cbScheduleOn.isChecked();

        EditText etScheduleEditHour = (EditText) mView.findViewById(R.id.etScheduleEditHour);
        if (etScheduleEditHour != null)
            mDrainSchedule.Hour = (byte) Integer.parseInt(etScheduleEditHour.getText().toString());

        EditText etScheduleEditMinute = (EditText) mView.findViewById(R.id.etScheduleEditMinute);
        if (etScheduleEditMinute != null)
            mDrainSchedule.Minute = (byte) Integer.parseInt(etScheduleEditMinute.getText().toString());

        EditText etScheduleEditLiters1 = (EditText) mView.findViewById(R.id.etScheduleEditLiters1);
        if (etScheduleEditLiters1 != null)
            mDrainSchedule.LitersNeeded.set(0, Integer.parseInt(etScheduleEditLiters1.getText().toString()));
        EditText etScheduleEditLiters2 = (EditText) mView.findViewById(R.id.etScheduleEditLiters2);
        if (etScheduleEditLiters2 != null)
            mDrainSchedule.LitersNeeded.set(1, Integer.parseInt(etScheduleEditLiters2.getText().toString()));
        EditText etScheduleEditLiters3 = (EditText) mView.findViewById(R.id.etScheduleEditLiters3);
        if (etScheduleEditLiters3 != null)
            mDrainSchedule.LitersNeeded.set(2, Integer.parseInt(etScheduleEditLiters3.getText().toString()));
        EditText etScheduleEditLiters4 = (EditText) mView.findViewById(R.id.etScheduleEditLiters4);
        if (etScheduleEditLiters4 != null)
            mDrainSchedule.LitersNeeded.set(3, Integer.parseInt(etScheduleEditLiters4.getText().toString()));
        EditText etScheduleEditLiters5 = (EditText) mView.findViewById(R.id.etScheduleEditLiters5);
        if (etScheduleEditLiters5 != null)
            mDrainSchedule.LitersNeeded.set(4, Integer.parseInt(etScheduleEditLiters5.getText().toString()));
        EditText etScheduleEditLiters6 = (EditText) mView.findViewById(R.id.etScheduleEditLiters6);
        if (etScheduleEditLiters6 != null)
            mDrainSchedule.LitersNeeded.set(5, Integer.parseInt(etScheduleEditLiters6.getText().toString()));
        EditText etScheduleEditLiters7 = (EditText) mView.findViewById(R.id.etScheduleEditLiters7);
        if (etScheduleEditLiters7 != null)
            mDrainSchedule.LitersNeeded.set(6, Integer.parseInt(etScheduleEditLiters7.getText().toString()));
        EditText etScheduleEditLiters8 = (EditText) mView.findViewById(R.id.etScheduleEditLiters8);
        if (etScheduleEditLiters8 != null)
            mDrainSchedule.LitersNeeded.set(7, Integer.parseInt(etScheduleEditLiters8.getText().toString()));

        CheckBox cbScheduleEditSunday = (CheckBox) mView.findViewById(R.id.cbScheduleEditSunday);
        if (cbScheduleEditSunday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 0, cbScheduleEditSunday.isChecked());
        CheckBox cbScheduleEditMonday = (CheckBox) mView.findViewById(R.id.cbScheduleEditMonday);
        if (cbScheduleEditMonday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 1, cbScheduleEditMonday.isChecked());
        CheckBox cbScheduleEditTuesday = (CheckBox) mView.findViewById(R.id.cbScheduleEditTuesday);
        if (cbScheduleEditTuesday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 2, cbScheduleEditTuesday.isChecked());
        CheckBox cbScheduleEditWednesday = (CheckBox) mView.findViewById(R.id.cbScheduleEditWednesday);
        if (cbScheduleEditWednesday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 3, cbScheduleEditWednesday.isChecked());
        CheckBox cbScheduleEditThursday = (CheckBox) mView.findViewById(R.id.cbScheduleEditThursday);
        if (cbScheduleEditThursday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 4, cbScheduleEditThursday.isChecked());
        CheckBox cbScheduleEditFriday = (CheckBox) mView.findViewById(R.id.cbScheduleEditFriday);
        if (cbScheduleEditFriday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 5, cbScheduleEditFriday.isChecked());
        CheckBox cbScheduleEditSaturday = (CheckBox) mView.findViewById(R.id.cbScheduleEditSaturday);
        if (cbScheduleEditSaturday != null)
            mDrainSchedule.WeekDaysBitFlags = setBit(mDrainSchedule.WeekDaysBitFlags, 6, cbScheduleEditSaturday.isChecked());


    }

    private byte setBit(byte flags, int number, boolean checked) {
        if (checked)
            return (byte) (flags | (1 << number));
        else
            return (byte) (flags & ~(1 << number));
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
    public interface OnScheduleChangedListener {
        // TODO: Update argument type and name
        void onScheduleChanged(int index, DrainSchedule schedule);
    }
}
