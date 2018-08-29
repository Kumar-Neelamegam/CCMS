package core_modules;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import entry_activities.Enroll_Students;
import utilities.Baseconfig;
import utilities.FButton;
import utilities.Imageutils;
import utilities.LocalSharedPreference;
import utilities.Validation1;
import vcc.coremodule.R;


/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */
public class Institute_Registration extends AppCompatActivity {

    //*********************************************************************************************

    private Toolbar toolbar;

    ImageButton imgbtn_capture;//, imgbtn_browse;

    ImageView iv_attachment;

    //For Image Attachment

    private Bitmap bitmap;
    private String file_name;

    //Imageutils imageutils;

    FButton Cancel, Submit;

    EditText Ins_name, Ins_add, Ins_own, Ins_mobile, Ins_email, Ins_password, SMS_SID, SMS_username, SMS_password;


    ImageView Logo_Imgvw;

    RadioGroup SMSOptions;
    RadioButton MobileSMS, GatewaySMS;
    LinearLayout SMSGatewayLayout;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        try {

            //Go to next
            String Query = "select Id as dstatus from Bind_InstituteInfo";
            boolean Registration = Baseconfig.LoadBooleanStatus(Query);
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

            if (mFirebaseUser != null && Registration) {     //Data insert
                Toast.makeText(Institute_Registration.this, "Signing In Success", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Institute_Registration.this, Task_Navigation.class));
                finish();
                LocalSharedPreference sharedPreference;
                sharedPreference = new LocalSharedPreference(Institute_Registration.this);
                sharedPreference.setBoolean(Baseconfig.Preference_TrailStatus, true);//Full data
            }

            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    //**********************************************************************************************
    private void GetInitialize() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView Exit = findViewById(R.id.ic_exit);


        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Institute_Registration.this, Institute_Registration.class));

        imgbtn_capture = findViewById(R.id.imgbtn_capture);
        iv_attachment = findViewById(R.id.img_logo);
     //   imageutils = new Imageutils(this);

        Submit = findViewById(R.id.btn_submit);
        Cancel = findViewById(R.id.btn_cancel);


        Ins_name = findViewById(R.id.edt_name);
        Ins_add = findViewById(R.id.edt_address);
        Ins_own = findViewById(R.id.edt_ownername);
        Ins_mobile = findViewById(R.id.edt_mobileno);
        Ins_email = findViewById(R.id.edt_email);
        Logo_Imgvw = findViewById(R.id.img_logo);

        Ins_password = findViewById(R.id.edt_email_password);
        SMS_username = findViewById(R.id.edt_sms_username);
        SMS_password = findViewById(R.id.edt_sms_password);
        SMS_SID = findViewById(R.id.edt_sms_sid);


        SMSOptions = findViewById(R.id.radiogroup_smsoptions);
        MobileSMS = findViewById(R.id.radiobutton_mobile_sms);
        GatewaySMS = findViewById(R.id.radiobutton_smsgateway);
        SMSGatewayLayout = findViewById(R.id.smsgateway_layout);

        try {
            Ins_own.setText(mFirebaseUser.getDisplayName());
            Ins_email.setText(mFirebaseUser.getEmail());
            Ins_email.setFocusableInTouchMode(false);
            Ins_mobile.setText(mFirebaseUser.getPhoneNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //**********************************************************************************************
    private void Controllisteners() {

        SMSOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (MobileSMS.isChecked()) {
                    SMSGatewayLayout.setVisibility(View.GONE);
                } else {
                    SMSGatewayLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        imgbtn_capture.setOnClickListener(v ->

                showPictureDialog()

        );

        Cancel.setOnClickListener(view -> Institute_Registration.this.finishAffinity());

        Submit.setOnClickListener(view -> {

            if (Baseconfig.CheckNW(Institute_Registration.this)) {


                if (checkValidation()) {

                    SaveLocal();

                } else {

                    Baseconfig.SweetDialgos(4, Institute_Registration.this, "Information", " Please fill all mandatory fields marked with (*) ", "OK");
                }


            }
            else
            {

                Baseconfig.SweetDialgos(4, Institute_Registration.this, "Information", " No internet connection found..try later ", "OK");

            }


        });

    }


    private int GALLERY = 1, CAMERA = 2;
    public void showPictureDialog(){


        try {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("Select Action");
            String[] pictureDialogItems = {
                    "Select photo from gallery",
                    "Capture photo from camera" };
            pictureDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    choosePhotoFromGallary();
                                    break;
                                case 1:
                                    takePhotoFromCamera();
                                    break;
                            }
                        }
                    });
            pictureDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(Institute_Registration.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    iv_attachment.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Institute_Registration.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            iv_attachment.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(Institute_Registration.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File wallpaperDirectory = new File(Baseconfig.DATABASE_FILE_PATH + File.separator + "Logo" + File.separator);
            // have the object build the directory structure, if needed.
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            try {
                File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                MediaScannerConnection.scanFile(this,
                        new String[]{f.getPath()},
                        new String[]{"image/jpeg"}, null);
                fo.close();
                Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
                Baseconfig.LogoImgPath = f.getAbsolutePath();

                return f.getAbsolutePath();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //**********************************************************************************************

    //**********************************************************************************************
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        iv_attachment.setImageBitmap(file);

        String path = Baseconfig.DATABASE_FILE_PATH + File.separator + "Logo" + File.separator;
        Baseconfig.LogoImgPath = path + filename;
        imageutils.createImage(file, filename, path, false);

    }*/
    //**********************************************************************************************

    public boolean checkValidation() {
        boolean ret = true;


        if (Ins_password.getText().length() == 0)
            ret = false;

        if (!Validation1.isEmailAddress((AppCompatEditText) Ins_email, true))
            ret = false;

        if (!Validation1.isMobileNumber((AppCompatEditText) Ins_mobile, true))
            ret = false;

       /* if (!Validation1.isName(Ins_own, true))
            ret = false;

        if (!Validation1.isName(Ins_add, true))
            ret = false;

        if (!Validation1.isName(Ins_name, true))
            ret = false;*/


        if (Ins_own.getText().length() == 0)
            ret = false;

        if (Ins_add.getText().length() == 0)
            ret = false;

        if (Ins_name.getText().length() == 0)
            ret = false;


        return ret;

    }
    //**********************************************************************************************

    public void SaveLocal() {

        try {
            String Str_InsName = "", Str_Address = "", Str_OwnName = "", Str_Mobile = "", Str_mail = "", Str_Isactive = "1";

            Str_InsName = Ins_name.getText().toString();
            Str_Address = Ins_add.getText().toString();
            Str_OwnName = Ins_own.getText().toString();
            Str_Mobile = Ins_mobile.getText().toString();
            Str_mail = Ins_email.getText().toString();

            String Str_Password = "";
            String Str_SMS_Username = "", Str_SMS_Password = "", Str_SMS_SID = "";

            Str_Password = Ins_password.getText().toString();
            Str_SMS_Username = SMS_username.getText().toString();
            Str_SMS_Password = SMS_password.getText().toString();
            Str_SMS_SID = SMS_SID.getText().toString();


            if (Baseconfig.LogoImgPath.toString().length() == 0) {
                Baseconfig.LogoImgPath = Environment.getExternalStorageDirectory() + "/vcc/logo_vcc.jpg";
            }

            int SMS_OPTION = 1;//1==mobile sms and 2=sms gateway

            if (MobileSMS.isChecked()) {
                SMS_OPTION = 1;//Mobile
            } else {
                SMS_OPTION = 2;//Gateway
            }

            SQLiteDatabase db = Baseconfig.GetDb();
            ContentValues values = new ContentValues();

            values.put("Institute_Name", Str_InsName);
            values.put("Institute_Address", Str_Address);
            values.put("Owner_Name", Str_OwnName);
            values.put("Mobile", Str_Mobile);
            values.put("Email", Str_mail);
            values.put("EmailPassword", Str_Password);
            values.put("SMSUsername", Str_SMS_Username);
            values.put("SMSPassword", Str_SMS_Password);
            values.put("SMSSID", Str_SMS_SID);
            values.put("SMSOption", SMS_OPTION);
            values.put("Logo", Baseconfig.LogoImgPath);
            values.put("IsActive", Str_Isactive);
            values.put("IsUpdate", "0");
            values.put("ActDate", Baseconfig.GetDate());
            values.put("UID", mFirebaseUser.getUid());
            values.put("LocalId", 1);


            values.put("IsPaid",1);
            values.put("PayId", "0");
            values.put("PaidDate",  Baseconfig.Device_OnlyDate());
            values.put("StudentCount", "5");
            values.put("FirebaseToken", Baseconfig.FirebaseToken);


            db.insert("Bind_InstituteInfo", null, values);
            db.close();

            PushtoFireBase(values);

            ShowSuccessDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void PushtoFireBase(ContentValues values) {

        try {
            LocalSharedPreference sharedPreference;
            sharedPreference = new LocalSharedPreference(Institute_Registration.this);
            sharedPreference.setBoolean(Baseconfig.Preference_TrailStatus, true);//Full data

            Map<String,String> Hashvalue=new HashMap<>();

            for (String s : values.keySet()) {
                Hashvalue.put(s,values.get(s).toString());
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(Baseconfig.FIREBASE_INSTITUTE_USERS).document().set(Hashvalue)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Toast.makeText(Institute_Registration.this, "User Registered", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Toast.makeText(Institute_Registration.this, "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.toString());
                        }
                    });



        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //**************************************************************************************

    public void ShowSuccessDialog() {

        Baseconfig.LogoImgPath = "";

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("Institute details saved successfully..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(sweetAlertDialog -> {

                    sweetAlertDialog.dismiss();
                    Institute_Registration.this.finish();
                    startActivity(new Intent(Institute_Registration.this, Task_Navigation.class));
                })
                .show();

        Clear();

    }

    //**********************v***************************************************************

    public void Clear() {

        Ins_name.setText("");
        Ins_add.setText("");
        Ins_own.setText("");
        Ins_mobile.setText("");
        Ins_email.setText("");

    }

    //********************************************************************************

    @Override
    public void onBackPressed() {

    }


    //**********************************************************************************************


}
