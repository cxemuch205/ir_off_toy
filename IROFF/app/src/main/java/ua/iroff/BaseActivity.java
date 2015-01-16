package ua.iroff;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ua.iroff.Adapters.LogAdapter;
import ua.iroff.Constants.App;
import ua.iroff.Interfaces.OnProgressSendingAdapter;
import ua.iroff.Interfaces.OnProgressSendingListener;
import ua.iroff.Models.CodeData;
import ua.iroff.Tools.SendCodeManager;
import ua.iroff.Tools.Tools;


public class BaseActivity extends ActionBarActivity {

    private Button btnStart;
    private ProgressBar pbLoad;
    private TextView tvMessage;
    private ListView lvLog;
    private LogAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        btnStart = (Button) findViewById(R.id.btn_start);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        lvLog = (ListView) findViewById(R.id.lv_operation_log);

        btnStart.setOnClickListener(clickStartListener);
        initLogList();
        prefs = getSharedPreferences(App.Pref.NAME, 0);
        boolean isFirstStart = prefs.getBoolean(App.Pref.IS_FIRST, true);
        if (isFirstStart) {
            saveDataCodesToExternal();
        }
    }

    private void saveDataCodesToExternal() {
        Tools.saveDataCodesFromAssetsToExternal(this);
        prefs.edit().putBoolean(App.Pref.IS_FIRST, false).commit();
    }

    private void initLogList() {
        adapter = new LogAdapter(this, new ArrayList<CodeData>());
        lvLog.setAdapter(adapter);
    }

    private boolean enablePb = false;

    private void enablePB() {
        enablePb = !enablePb;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enablePb) {
                    pbLoad.setVisibility(ProgressBar.VISIBLE);
                    tvMessage.setVisibility(TextView.VISIBLE);
                    btnStart.setEnabled(false);
                } else {
                    pbLoad.setVisibility(ProgressBar.GONE);
                    tvMessage.setVisibility(TextView.GONE);
                    btnStart.setEnabled(true);
                }
            }
        });
    }

    private View.OnClickListener clickStartListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enablePB();
            runCodeList();
        }
    };

    private void runCodeList() {
        SendCodeManager.sendCodes(this, App.CommandType.POWER_ON_OFF, progressSendingCodesListener);
    }

    private OnProgressSendingListener progressSendingCodesListener = new OnProgressSendingAdapter() {
        @Override
        public void onProgress(int typeCodes, final String msg, final Object data) {
            super.onProgress(typeCodes, msg, data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data != null) {
                        tvMessage.setVisibility(TextView.GONE);
                        CodeData codeData = (CodeData) data;
                        adapter.add(codeData);
                        lvLog.smoothScrollToPosition(adapter.getCount());
                    } else {
                        tvMessage.setVisibility(TextView.VISIBLE);
                        tvMessage.setText(msg);
                    }
                }
            });
        }

        @Override
        public void onEnd(int typeCodes) {
            super.onEnd(typeCodes);
            enablePB();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CodeData c = new CodeData();
                    c.brand = "";
                    c.code = "";
                    c.color = Color.parseColor("#77979797");
                    adapter.add(c);
                    lvLog.smoothScrollToPosition(adapter.getCount());
                }
            });
        }
    };
}
