package ru.yulancer.sad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;


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

    private EditText mLitersEditor;

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
        View form = getActivity().getLayoutInflater().inflate(R.layout.fragment_schedule_edit_input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String newText = String.format("Расписание номер %d", mScheduleIndex);
        Dialog dialog = builder.setTitle(newText).setView(form)
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

    }

    private void updateSchedule() {

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
