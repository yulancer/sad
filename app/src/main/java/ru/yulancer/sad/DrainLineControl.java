package ru.yulancer.sad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static ru.yulancer.sad.R.id.swPump;

/**
 * Created by matveev_yuri on 01.06.2016.
 */
public class DrainLineControl extends RelativeLayout {

    private TextView mTitleView;
    private ProgressTextView mProgressBar;
    private ImageButton mEditButton;

    public DrainLineControl(Context context) {
        super(context);

        String inflatorservice = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflatorservice);
        li.inflate(R.layout.drain_line_control_layout, this, true);

        findViews();
    }
    public DrainLineControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        String inflatorservice = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflatorservice);
        li.inflate(R.layout.drain_line_control_layout, this, true);

        findViews();
    }
    public DrainLineControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        String inflatorservice = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflatorservice);
        li.inflate(R.layout.drain_line_control_layout, this, true);
        findViews();
    }

    private void findViews(){
        mTitleView = (TextView) findViewById(R.id.tv_LineTitle);
        mProgressBar = (ProgressTextView) findViewById(R.id.pbDrainProgress);
        mEditButton = (ImageButton) findViewById(R.id.btn_EditNeeded);
    }
}
