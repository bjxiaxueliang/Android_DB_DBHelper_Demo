package com.example.scalephoto;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.scalephoto.cache.models.SnsBiLogData;
import com.example.scalephoto.cache.SnsBiDbManager;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {


    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);
        //
        mTextView = (TextView) findViewById(R.id.textView001);

        findViewById(R.id.button01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnsBiLogData mSnsBiData = new SnsBiLogData();
                mSnsBiData.ctime = "123";
                mSnsBiData.pos = "456";
                mSnsBiData.event = "789";
                mSnsBiData.ip = "123";
                mSnsBiData.network = "456";
                mSnsBiData.app_version = "789";
                mSnsBiData.operator = "123";

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("00000", "value0");
                map.put("11111", "value1");
                map.put("22222", "value2");
                mSnsBiData.data = map;


                SnsBiDbManager.getInstance().insert(MainActivity.this, mSnsBiData);

            }
        });

        findViewById(R.id.button02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<SnsBiLogData> mList = SnsBiDbManager.getInstance().query(MainActivity.this);
                //
                mTextView.setText(mList.toString());

            }
        });


    }
}