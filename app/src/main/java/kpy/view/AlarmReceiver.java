package kpy.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import kpy.bracelet.PlayAlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(PendingIntent.getBroadcast(context,getResultCode(), new Intent(context,AlarmReceiver.class),0));
        Intent i = new Intent(context,PlayAlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(i);
    }
}
