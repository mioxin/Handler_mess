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

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    final String LOG_TAG = "myLogs";
    final int STATUS_NONE = 0; // нет подключения
    final int STATUS_CONNECTING = 1; // подключаемся
    final int STATUS_CONNECTED = 2; // подключено
    final int STATUS_DOWNLOAD_START = 3; // загрузка началась
    final int STATUS_DOWNLOAD_FILE = 4; // файл загружен
    final int STATUS_DOWNLOAD_END = 5; // загрузка закончена
    final int STATUS_DOWNLOAD_NONE = 6; // загрузка закончена
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
                        pbbar.setVisibility(View.GONE);
                        break;
                    case STATUS_CONNECTED:
                        //
                        tv.setText("Connected.");
                        break;
                    case STATUS_CONNECTING:
                        but_con.setEnabled(false);
                        //pbbar.setVisibility(View.VISIBLE);
                        tv.setText("Connecting...");
                        break;
                    case STATUS_DOWNLOAD_START:
                        tv.setText("Start download " + msg.arg1 + " files");
                        pbbar.setMax(msg.arg1);
                        pbbar.setProgress(0);
                        pbbar.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_DOWNLOAD_FILE:
                        tv.setText("Downloading. Left " + msg.arg2 + " files");
                        pbbar.setProgress(msg.arg1);
                        saveFile((byte[]) msg.obj);
                        break;
                    case STATUS_DOWNLOAD_END:
                        tv.setText("Download complete!");
                        break;
                    case STATUS_DOWNLOAD_NONE:
                        tv.setText("No files for download");
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
            Message m;
            byte[] file;
            Random rand = new Random();
            @Override
            public void run() {
                try {
                    h.sendEmptyMessage(STATUS_CONNECTING);
                    TimeUnit.SECONDS.sleep(2);
                    // установлено
                    h.sendEmptyMessage(STATUS_CONNECTED);
// выполняется какая-то работа
                    TimeUnit.SECONDS.sleep(3);
                    int filesCount = rand.nextInt(5);
                    if (filesCount == 0) {
// сообщаем, что файлов для загрузки нет
                        h.sendEmptyMessage(STATUS_DOWNLOAD_NONE);
// и отключаемся
                        TimeUnit.MILLISECONDS.sleep(1500);
                        h.sendEmptyMessage(STATUS_NONE);
                        return;
                    }
// загрузка начинается
// создаем сообщение, с информацией о количестве файлов
                    m = h.obtainMessage(STATUS_DOWNLOAD_START, filesCount, 0);
// отправляем
                    h.sendMessage(m);
                    for (int i = 1; i <= filesCount; i++) {
// загружается файл
                        file = downloadFile();
// создаем сообщение с информацией о порядковом номере
// файла,
// кол-вом оставшихся и самим файлом
                        m = h.obtainMessage(STATUS_DOWNLOAD_FILE, i,
                                filesCount - i, file);
// отправляем
                        h.sendMessage(m);
                    }
// загрузка завершена
                    h.sendEmptyMessage(STATUS_DOWNLOAD_END);
// отключаемся
                    TimeUnit.MILLISECONDS.sleep(1500);
                    h.sendEmptyMessage(STATUS_NONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    byte[] downloadFile() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return new byte[1024];
    }

    void saveFile(byte[] file) {
    }
}
