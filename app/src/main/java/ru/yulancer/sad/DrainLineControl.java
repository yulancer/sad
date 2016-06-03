package ru.yulancer.sad;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by matveev_yuri on 01.06.2016.
 */
public class DrainLineControl extends RelativeLayout implements View.OnClickListener {

    private TextView mTitleView;
    private ProgressTextView mProgressBar;
    private ImageButton mEditButton;
    private CheckBox mStartCheckBox;

    private byte mLineNumber;

    public DrainLineControl(Context context) {
        super(context);

        String inflatorservice = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflatorservice);
        li.inflate(R.layout.drain_line_control_layout, this, true);

        findViews();
    }

    public DrainLineControl(Context context, AttributeSet attrs) throws Exception {
        super(context, attrs);

        String inflatorservice = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflatorservice);
        li.inflate(R.layout.drain_line_control_layout, this, true);

        findViews();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DrainLineControl,
                0, 0);

        try {
            mTitleView.setText(a.getString(R.styleable.DrainLineControl_lineTitle));
            mLineNumber = (byte) a.getInt(R.styleable.DrainLineControl_lineNumber, 0);
        } finally {
            a.recycle();
        }

        if (mLineNumber < 1 || mLineNumber > 8)
            mTitleView.setText("номер линии должен быть от 1 до 8");

    }

    public DrainLineControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        String inflatorservice = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflatorservice);
        li.inflate(R.layout.drain_line_control_layout, this, true);
        findViews();
    }

    public int getLineNumber() {
        return mLineNumber;
    }

    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getContext();
        if (mainActivity != null) {
            /// если это кнопка редактирования литров
            if (v instanceof ImageButton) {
                ImageButton ib = (ImageButton) v;
                if (ib != null) {
                    mainActivity.ShowNeededLitersDialog(mLineNumber);
                }
            }
            /// если это кнопка ручного включения линии
            if (v instanceof CheckBox) {
                CheckBox cb = (CheckBox) v;
                if (cb != null) {
                    mainActivity.StartDrainLine(mLineNumber, cb.isChecked());
                }
            }
        }
    }

    private void findViews() {
        mTitleView = (TextView) findViewById(R.id.tv_LineTitle);
        mProgressBar = (ProgressTextView) findViewById(R.id.pbDrainProgress);
        mEditButton = (ImageButton) findViewById(R.id.btn_EditNeeded);
        if (mEditButton != null)
            mEditButton.setOnClickListener(this);
        mStartCheckBox = (CheckBox) findViewById(R.id.cbStart);
        if (mStartCheckBox != null)
            mStartCheckBox.setOnClickListener(this);
    }
}
