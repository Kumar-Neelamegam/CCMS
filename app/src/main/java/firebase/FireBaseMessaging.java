package firebase;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import utilities.Baseconfig;

public class FireBaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {
            //if the message contains data payload
            //It is a map of custom keyvalues
            //we can read it easily
            if(remoteMessage.getData().size() > 0){



            }

            //handle the data message here
            //getting the title and the body
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            //then here we can use the title and body to build a notification
           // Log.e("Firebase Message: ",title+body);

            if(title!= null && title.toString().equalsIgnoreCase(Baseconfig.ADMIN_NOTIFICATION))//For DB Updated
            {

                try {
                    String Query=body;
                    Log.e("Admin Notification Query: ", body);
                    SQLiteDatabase db=Baseconfig.GetDb();
                    db.execSQL(Query);
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else
            {
                /*
                 * Displaying a notification locally
                 */
                Uri uriNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringTone = RingtoneManager.getRingtone(this, uriNotification);
                ringTone.play();
                MyNotificationManager.getInstance(this).displayNotification(title, body);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
