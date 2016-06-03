package ru.yulancer.sad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link DialogFragment } subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLitersNeededChangedListener} interface
 * to handle interaction events.
 * Use the {@link LitersNeededInputDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LitersNeededInputDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LineNumber = "LineNumber";
    private static final String ARG_LitersNeeded = "LitersNeeded";

    // TODO: Rename and change types of parameters
    private byte mLineNumber;
    private int mLitersNeeded;

    private OnLitersNeededChangedListener mListener;

    public LitersNeededInputDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lineNumber номер линии орошения.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_needed_liters_input_dialog, container, false);

        TextView tvNeededLitersDialogLabel = (TextView) v.findViewById(R.id.tvNeededLitersDialogLabel);
        if (tvNeededLitersDialogLabel != null){
            String newText = String.format("литров для линии %d надо", mLineNumber);
            tvNeededLitersDialogLabel.setText(newText);
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onLitersNeededChanged(mLineNumber, mLitersNeeded);
        }
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
