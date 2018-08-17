package firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import utilities.Baseconfig;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {



    SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefsCCMS";
    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        try {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            //now we will have the token
            String token = FirebaseInstanceId.getInstance().getToken();

            //for now we are displaying the token in the log
            //copy it as this method is called only when the new token is generated
            //and usually new token is only generated when the app is reinstalled or the data is cleared
            Log.e("MyRefreshedToken", token);
            Baseconfig.FirebaseToken=token;

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Baseconfig.FirebaseToken_Str, token);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
