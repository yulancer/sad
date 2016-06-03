package ru.yulancer.sad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.opengl.ETC1Util;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import java.util.Locale;


/**
 * A simple {@link DialogFragment } subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLitersNeededChangedListener} interface
 * to handle interaction events.
 * Use the {@link LitersNeededInputDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LitersNeededInputDialog extends DialogFragment implements DialogInterface.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LineNumber = "LineNumber";
    private static final String ARG_LitersNeeded = "LitersNeeded";

    // TODO: Rename and change types of parameters
    private byte mLineNumber;
    private int mLitersNeeded;

    private OnLitersNeededChangedListener mListener;

    private EditText mLitersEditor;

    public LitersNeededInputDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lineNumber   номер линии орошения.
     * @param LitersNeeded необходимое количество литров.
     * @return A new instance of fragment LitersNeededInputDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static LitersNeededInputDialog newInstance(byte lineNumber, int LitersNeeded) {
        LitersNeededInputDialog fragment = new LitersNeededInputDialog();
        Bundle args = new Bundle();
        args.putByte(ARG_LineNumber, lineNumber);
        args.putInt(ARG_LitersNeeded, LitersNeeded);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLineNumber = getArguments().getByte(ARG_LineNumber);
            mLitersNeeded = getArguments().getInt(ARG_LitersNeeded);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View form = getActivity().getLayoutInflater().inflate(R.layout.fragment_needed_liters_input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String newText = String.format("литров для линии %d надо", mLineNumber);
        Dialog dialog = builder.setTitle(newText).setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create();
        mLitersEditor = (EditText) form.findViewById(R.id.etNeededLitersDialog);
        if (mLitersEditor != null)
            mLitersEditor.setText(String.format(Locale.getDefault(), "%d", mLitersNeeded));
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLitersNeededChangedListener) {
            mListener = (OnLitersNeededChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLitersNeededChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String newText = String.valueOf(mLitersEditor.getText());
        mLitersNeeded = Integer.parseInt(newText);
        mListener.onLitersNeededChanged(mLineNumber, mLitersNeeded);
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
    public interface OnLitersNeededChangedListener {
        // TODO: Update argument type and name
        void onLitersNeededChanged(byte lineNumber, int LitersNeeded);
    }
}
