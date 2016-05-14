package com.wuxinle.touchimageview;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    RelativeLayout rlDesign;
    TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        show();
    }

    private void show() {

        touchImageView = new TouchImageView(this);
        touchImageView.setBitmap(R.mipmap.ic_launcher);
        touchImageView.setDesignWidth(700);
        touchImageView.setDesignHeight(900);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        touchImageView.setLayoutParams(layoutParams);
        rlDesign.addView(touchImageView);
    }

    private void initView() {

        rlDesign = (RelativeLayout) findViewById(R.id.rl_design);
    }
}
