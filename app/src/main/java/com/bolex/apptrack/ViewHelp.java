package com.bolex.apptrack;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by liushenen on 2017/12/8.
 */

public class ViewHelp {

    static int logViewid = 0x7722648;
    private static TextView msgTextView;

    public static View creactMsgView(Activity mActivity) {
        LinearLayout logView = new LinearLayout(mActivity);


        logView.setTag(logViewid);

        logView.setOrientation(LinearLayout.VERTICAL);
        logView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        final ScrollView msgScrollView = new ScrollView(mActivity);
        msgScrollView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                350));
        msgScrollView.setBackgroundColor(Color.parseColor("#88000000"));
        msgTextView = new TextView(mActivity);
        msgTextView.setTextColor(Color.WHITE);
        msgTextView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        msgTextView.setBackgroundColor(Color.parseColor("#88000000"));
        msgTextView.setTextSize(10f);

        final Button btLog = new Button(mActivity);
        btLog.setText(Config.isTrack ?"关闭":"跟踪");
        btLog.setTextColor(Color.WHITE);
        btLog.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        btLog.setLayoutParams(layoutParams);
        TextView title = new TextView(mActivity);
        title.setText("AppTrack");
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextColor(Color.WHITE);
        title.setBackgroundColor(Color.parseColor("#88000000"));

        msgScrollView.addView(msgTextView);
        logView.addView(title);
        logView.addView(msgScrollView);
        logView.addView(btLog);
        msgScrollView.setVisibility(Config.isTrack ? View.VISIBLE : View.GONE);
        btLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msgScrollView.getVisibility() == View.GONE) {
                    msgScrollView.setVisibility(View.VISIBLE);
                    Config.isTrack = true;
                    btLog.setText("关闭");

                } else {
                    msgScrollView.setVisibility(View.GONE);
                    Config.isTrack = false;
                    btLog.setText("跟踪");
                }
            }
        });

        return logView;
    }

    public static TextView getMsgTextView() {
        return msgTextView;
    }

    public static int getLogViewid() {
        return logViewid;
    }


}

