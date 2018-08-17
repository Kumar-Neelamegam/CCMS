package firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import core_modules.Splash;
import vcc.coremodule.R;

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);




        }
        return mInstance;
    }

    public void displayNotification(String title, String body) {

        try {
            Intent launchIntent = new Intent(mCtx, Splash.class);
            launchIntent.setAction(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0 /* R    equest code */, launchIntent,PendingIntent.FLAG_ONE_SHOT);

            long when = System.currentTimeMillis();

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mCtx);
            mNotifyBuilder.setVibrate(new long[]{1000, 1000});
            boolean lollipop = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);


            if (lollipop) {

                mNotifyBuilder = new NotificationCompat.Builder(mCtx)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setContentText(body)
                        .setColor(Color.TRANSPARENT)
                        .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_action_notify)
                        .setWhen(when).setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{1000, 1000})
                        .setContentIntent(pendingIntent);


            } else {

                mNotifyBuilder = new NotificationCompat.Builder(mCtx)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_action_notify)
                        .setWhen(when).setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{1000, 1000})
                        .setContentIntent(pendingIntent);

            }

            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(new Random().nextInt(100) /* ID of notification */, mNotifyBuilder.build());

            Uri uriNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringTone = RingtoneManager.getRingtone(mCtx, uriNotification);
            ringTone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
