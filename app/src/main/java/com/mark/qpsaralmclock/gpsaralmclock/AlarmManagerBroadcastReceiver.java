package com.mark.qpsaralmclock.gpsaralmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {


    final String LOG_TAG = "myLogs";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "BroadcastReceiver  onReceive" );

/*
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");

// Осуществляем блокировку

        wl.acquire();
*/

    //    nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    //   Notification notification = new Notification(R.drawable.icon, "Test", System.currentTimeMillis());
//Интент для активити, которую мы хотим запускать при нажатии на уведомление
       Intent intentTL = new Intent(context, MainActivity.class);
       /* notification.setLatestEventInfo(context, "Test", "Do something!",
                PendingIntent.getActivity(context, 0, intentTL,
                        PendingIntent.FLAG_CANCEL_CURRENT));
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        nm.notify(1, notification);
        */
// Установим следующее напоминание.
/*
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intentTL, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
*/
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent1 = new Intent(context, AlarmManagerBroadcastReceiver.class);

        intent.putExtra("ONE_TIME", Boolean.TRUE); // Задаем параметр интента

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent1, 0);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pi);

    }

    /*
    public void setOnetimeTimer(Context context) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);

        intent.putExtra("ONE_TIME", Boolean.TRUE); // Задаем параметр интента

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);

    }
    */
}
