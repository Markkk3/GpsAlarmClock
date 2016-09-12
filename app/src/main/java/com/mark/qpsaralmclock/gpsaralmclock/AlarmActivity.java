package com.mark.qpsaralmclock.gpsaralmclock;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnstop;
    final String LOG_TAG = "myLogs";
    private SharedPreferences sp;
    private boolean vibro;
    private String uri;
    Ringtone r;
    MediaPlayer mediaPlayer;
    float volume = 0.1f;
    int id;
    SQLiteDatabase db;
    MyApplication myApplication;
    int itemnum = -1;
    private  boolean isStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_alarm);

         id = getIntent().getIntExtra("ID", 0);
        Log.d(LOG_TAG, "AlarmActivity onCreate " +  id);
        itemnum = getIntent().getIntExtra("pos", -1);

        myApplication =(MyApplication) getApplicationContext();
        Log.d(LOG_TAG, "AlarmActivity size" +   myApplication.alarmItem.size());



        btnstop = (Button) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        String radius = sp.getString("example_list", "100");
        uri = sp.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound");
        vibro = sp.getBoolean("notifications_new_message_vibrate", true);
        int r = Integer.parseInt(radius);
        playAlarm();
    }


    public void saveRun() {
        if (id!=0 && itemnum!=-1) {

            myApplication.alarmItem.get(itemnum).setRun(false);

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            String g = Integer.toString(id);
            values.put(DatabaseHelper.RUN, 0);
            // обновляем по id
            int updCount = db.update(DatabaseHelper.DATABASE_TABLE, values, "id = ?",
                    new String[]{g});
            Log.d(LOG_TAG, "updated rows count = " + updCount);
            db.close();
        }
    }


    public void playAlarm() {

        Log.d(LOG_TAG, "playAlarm " + volume);
        isStart = true;



        try {
            Uri notify = Uri.parse(uri);

            Log.d(LOG_TAG, "звук = " + notify);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, notify);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(volume, volume);
            //  Uri notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

           // r = RingtoneManager.getRingtone(getApplicationContext(), notify);

          //  Log.d(LOG_TAG, "Uri notify= " + notify);
          //  r.play();



            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int i = 1; i <= 10; i++) {
                        if(volume<1) {
                            volume+=0.1f;
                        }
                        else {
                            volume = 1;
                        }

                        if(mediaPlayer!= null) {
                            mediaPlayer.setVolume(volume, volume);

                            if(vibro) {
                                long mills = 200L;
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(mills);
                            }
                        }



                        Log.d(LOG_TAG, "увеличение громкости = " + volume);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(!isStart) {
            super.onBackPressed();

            startActivity(new Intent(this, MainActivity.class));
        }
        else  {
            Toast.makeText(this, "Нажмите стоп", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnstop:
                Log.d(LOG_TAG, "AlarmActivity bntstop" );
                isStart = false;

                saveRun();
                if(mediaPlayer!= null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
                break;
        }

    }
}
