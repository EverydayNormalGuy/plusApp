//package com.plusapp.pocketbiceps.app;
//
///**
// * Created by guemuesm on 02.03.2017.
// */
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.app.NotificationCompat;
//
//import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
//import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
//
//import java.util.Calendar;
//import java.util.List;
//
//
///**
// * Created by guemuesm on 02.03.2017.
// */
//public class NotificationReceiver extends BroadcastReceiver {
//
//    private MarkerDataSource data;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        data = new MarkerDataSource(context);
//        data.open();  //
//
//        List<MyMarkerObj> m = data.getMyMarkers(1);
//
//        for(int i = 0; i<m.size();i++) {
//
//            MyMarkerObj mmo = m.get(i);
//
//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            Intent intent1 = new Intent(context, MainActivity.class); // Welche Klasse soll gestartet werden?
//            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            //if we want ring on notifcation then uncomment below line//
////        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) mmo.getTimestamp(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).
//                    setSmallIcon(R.drawable.cast_ic_notification_small_icon).
//                    setContentIntent(pendingIntent).
//                    setContentText("this is my notification").
//                    setContentTitle("my notificaton for id "+mmo.getTimestamp()).
////                setSound(alarmSound).
//        setAutoCancel(true);
//            notificationManager.notify((int) mmo.getTimestamp(), builder.build());
//
//        }
//    }
//
//
//
//
//}
