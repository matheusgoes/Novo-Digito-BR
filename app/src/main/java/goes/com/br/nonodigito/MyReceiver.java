package goes.com.br.nonodigito;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Calendar;

public class MyReceiver extends BroadcastReceiver {
    Notification n;
    NotificationManager mNotificationManager;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int mContactCount;

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context , MainActivity.class);
        //Define acão da intent
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        i.setAction("contacts_edited");
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
        //Constroi notificação
        n  = new Notification.Builder(context)
                .setContentTitle("Verifique seus contatos")
                .setContentText("Verifique se algum de seus contatos precisa de atualização")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        mNotificationManager.notify(0, n);
    }


    public void setAlarm(Context context){
        Log.i("ALARM RECEIVER", "SETTING ALARM");
        if (alarmMgr == null){
            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_DAY * 7, alarmIntent);
        }else{
            if(n!=null){
                mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
            }
        }
    }
    public void cancelAlarm(Context context){
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            Intent intent = new Intent(context, MyReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.cancel(alarmIntent);
            Log.i("ALARM RECEIVER", "Cancel: Alarm Cancelled");
        }else{
            Log.i("ALARM RECEIVER", "Cancel: alarm = null");
        }
    }
}
