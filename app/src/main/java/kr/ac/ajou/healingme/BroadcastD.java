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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;


public class BroadcastD extends BroadcastReceiver {
//    private int s = (int) System.currentTimeMillis();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        Bundle bundle=new Bundle();
        intent=new Intent(context,GetLetterActivity.class);
        intent.putExtra("notificationId", LetterFragment.getDaydiff); //전달할 값
        bundle.putInt("notificationId",LetterFragment.getDaydiff);
        Log.e("broadcastId",LetterFragment.getDaydiff+"");

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,LetterFragment.id,intent, PendingIntent.FLAG_UPDATE_CURRENT);//다른 액티비티 오픈, 두번째가 구분하는 건데 왜 못하지..

        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.alarm).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("힐링한 나").setContentText("편지가 왔어요")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);

        notificationmanager.notify(LetterFragment.id, builder.build());

    }
}


