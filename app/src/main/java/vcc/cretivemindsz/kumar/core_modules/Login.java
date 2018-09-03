package vcc.cretivemindsz.kumar.core_modules;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vcc.cretivemindsz.kumar.firebase.MyNotificationManager;
import vcc.cretivemindsz.kumar.utilities.Baseconfig;
import vcc.cretivemindsz.kumar.R;;

public class Login extends AppCompatActivity {


    //****************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        try {
            LoadNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void LoadNotification() {
        /*
         * If the device is having android oreo we will create a notification channel
         * */

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Baseconfig.CHANNEL_ID, getString(R.string.app_name), importance);
            mChannel.setDescription(getString(R.string.app_name_extension));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        /*
         * Displaying a notification locally
         */
        MyNotificationManager.getInstance(this).displayNotification("Greetings", "Hello how are you?");

    }

    //****************************************************************************

}//END