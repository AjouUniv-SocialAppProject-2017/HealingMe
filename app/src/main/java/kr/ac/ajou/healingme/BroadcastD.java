package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-09-25.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class BroadcastD extends BroadcastReceiver {
//    private int s = (int) System.currentTimeMillis();

    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        intent=new Intent(context,GetLetterActivity.class);
        intent.putExtra("notificationId", LetterActivity.id); //전달할 값




        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, LetterActivity.id,intent, PendingIntent.FLAG_UPDATE_CURRENT);//다른 액티비티 오픈, 두번째가 구분하는 건데 왜 못하지..

        Notification.Builder builder = new Notification.Builder(context);

        Toast.makeText(context,"등러?",Toast.LENGTH_LONG).show();
        builder.setSmallIcon(R.drawable.alarm).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("힐링한 나").setContentText("편지가 왔어요")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);

        notificationmanager.notify(LetterActivity.id, builder.build());

    }
}


