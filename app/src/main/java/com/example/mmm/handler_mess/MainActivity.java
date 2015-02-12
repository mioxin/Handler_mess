package com.example.mmm.handler_mess;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    final String LOG_TAG = "myLogs";
    final int STATUS_NONE = 0; // нет подключения
    final int STATUS_CONNECTING = 1; // подключаемся
    final int STATUS_CONNECTED = 2; // подключено
    Handler h;
    TextView tv;
    ProgressBar pbbar;
    Button but_con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        but_con = (Button) findViewById(R.id.but_con);

        h = new Handler() {
            public void handleMessage(Message msg){
                switch (msg.what){
                    case STATUS_NONE:
                        but_con.setEnabled(true);
                        tv.setText("Not connected.");
                        break;
                    case STATUS_CONNECTED:
                        pbbar.setVisibility(View.GONE);
                        tv.setText("Connected.");
                        break;
                    case STATUS_CONNECTING:
                        but_con.setEnabled(false);
                        pbbar.setVisibility(View.VISIBLE);
                        tv.setText("Connecting...");
                        break;
                }
            }
        };
        h.sendEmptyMessage(STATUS_NONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onclick(View view) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    h.sendEmptyMessage(STATUS_CONNECTING);
                    TimeUnit.SECONDS.sleep(2);
                    // установлено
                    h.sendEmptyMessage(STATUS_CONNECTED);
// выполняется какая-то работа
                    TimeUnit.SECONDS.sleep(3);
// разрываем подключение
                    h.sendEmptyMessage(STATUS_NONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
