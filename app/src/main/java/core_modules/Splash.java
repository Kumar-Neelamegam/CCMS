package core_modules;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import utilities.Baseconfig;
import utilities.RuntimePermissionsActivity;
import vcc.coremodule.R;

public class Splash extends RuntimePermissionsActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_PERMISSIONS = 200;
    //****************************************************************************
    //Copying database
    public Copydatabase copydb = new Copydatabase();
    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //****************************************************************************
    //Declaration
    ProgressBar progressBar;
    int progress;
    TextView progress_status;


    //****************************************************************************
    int progressStatus = 0;
    Handler handler = new Handler();
    //****************************************************************************
    //Initialization
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);


        try {

            //For permission
            isStoragePermissionGranted();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //****************************************************************************
    //After permission granted
    @Override
    public void onPermissionsGranted(int requestCode) {
        try {
            GetInitialize();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //****************************************************************************
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    public void init() {


        File f = new File(Baseconfig.DATABASE_FILE_PATH);

        if (!f.exists()) {


            File file = new File(Baseconfig.DATABASE_FILE_PATH);
            file.mkdirs();

        }


        // Check if the database exists before copying
        boolean initialiseDatabase = (new File(Baseconfig.DATABASE_NAME)).exists();

        Log.e("DB Found: ", String.valueOf(initialiseDatabase));

        if (initialiseDatabase == false) {

            copydb.execute();

        } else {


            LoadNextActivity();

        }

    }

    //****************************************************************************

    public void GetInitialize() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        YoYo.with(Techniques.BounceIn).duration(2500).playOn(findViewById(R.id.logo));

        progressBar = findViewById(R.id.spin_kit);

        progress_status = findViewById(R.id.textprgrs);


        try {
            Baseconfig.getInstitueValues();
        } catch (Exception e) {

        }


    }

    public void LoadNextActivity() {


        new Thread(new Runnable() {
            public void run() {

                while (progressStatus < 100) {
                    progressStatus = doSomeWork();

                    handler.post(new Runnable() {
                        public void run() {
                            //progressBar.setProgress(progressStatus);
                            progress_status.setText((String.valueOf(progressStatus)));
                        }
                    });
                }

                handler.post(new Runnable() {
                    public void run() {

                        try {



                            String Query = "select Id as dstatus from Bind_InstituteInfo";
                            boolean Registration = Baseconfig.LoadBooleanStatus(Query);

                            if (mFirebaseUser != null && Registration) { //both case passed {login, registration}

                                finish();
                                Intent intent = new Intent(Splash.this, Task_Navigation.class);
                                startActivity(intent);

                            } else if (mFirebaseUser != null) //
                            {

                                finish();
                                Intent intent = new Intent(Splash.this, Institute_Registration.class);

                                startActivity(intent);

                            } else {

                                DoLogin();

                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }
                });
            }

            private int doSomeWork() {
                try {
                    // ---simulate doing some work---
                    Thread.sleep(20);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return
                        ++progress;
            }
        }).start();

    }

    private void DoLogin() {

        try {
            //send user to the login page
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());


            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.logo_vcc)
                            .setTheme(R.style.AppTheme_NoActionBar)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

//****************************************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Toast.makeText(Splash.this, "Signing In Success", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Splash.this, Institute_Registration.class));
                finish();
            } else {
                // Sign in failed, check response for error code
                // Baseconfig.SweetDialgos(4,Splash.this, "Information", "Signin Failed (or) check your data connection...", "OK");
                new SweetAlertDialog(Splash.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information")
                        .setContentText("Signin Failed (or) check your data connection...")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                Splash.this.finishAffinity();
                            }
                        })
                        .show();

            }
        }
    }

    public void isStoragePermissionGranted() {

        Splash.super.requestAppPermissions(new
                        String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE
                }, R.string
                        .runtime_permissions_txt
                , REQUEST_PERMISSIONS);


    }

    //***************************************************************************************************

    private void copyLogoFileAssets() {

        try {
            InputStream is = Splash.this.getApplicationContext().getAssets().open("logo_vcc.jpg");

            // Copy the database into the destination
            OutputStream os = new FileOutputStream(Baseconfig.DATABASE_FILE_PATH + "/logo_vcc.jpg");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();


            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            InputStream is = Splash.this.getApplicationContext().getAssets().open("male_avatar.png");

            // Copy the database into the destination
            OutputStream os = new FileOutputStream(Baseconfig.DATABASE_FILE_PATH + "/male_avatar.jpg");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();


            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class Copydatabase extends AsyncTask<Void, Void, Void> {

        protected void onPostExecute(Void v) {

            init();

        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {


                shipdb();


            } catch (IOException e) {


                e.printStackTrace();

            }


            return null;

        }


        //***************************************************************************************************

        private void shipdb() throws IOException {

            copyLogoFileAssets();

            final String DB_DESTINATION = Baseconfig.DATABASE_FILE_PATH + "/vcc.db";

            // Check if the database exists before copying
            boolean initialiseDatabase = (new File(DB_DESTINATION)).exists();

            if (initialiseDatabase == false) {
                Log.i("Processing...", "Copying Database");
                // Open the .db file in your assets directory
                InputStream is = Splash.this.getApplicationContext().getAssets().open("vcc.db");

                // Copy the database into the destination
                OutputStream os = new FileOutputStream(DB_DESTINATION);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();

                os.close();
                is.close();


            }


        }


    }

    //***************************************************************************************************
}
