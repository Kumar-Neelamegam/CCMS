package core_modules;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import utilities.Baseconfig;
import utilities.FButton;
import utilities.Imageutils;
import utilities.Validation1;
import vcc.coremodule.R;


/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */
public class Institute_Registration extends AppCompatActivity implements Imageutils.ImageAttachmentListener {

    //*********************************************************************************************

    private Toolbar toolbar;

    ImageButton imgbtn_capture;//, imgbtn_browse;

    ImageView iv_attachment;

    //For Image Attachment

    private Bitmap bitmap;
    private String file_name;

    Imageutils imageutils;

    FButton Cancel, Submit;

    EditText Ins_name, Ins_add, Ins_own, Ins_mobile, Ins_email, Ins_password, SMS_SID, SMS_username, SMS_password;


    ImageView Logo_Imgvw;

    RadioGroup SMSOptions;
    RadioButton MobileSMS, GatewaySMS;
    LinearLayout SMSGatewayLayout;

    FirebaseAuth auth;
    FirebaseUser mFirebaseUser;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        try {


            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    //**********************************************************************************************
    private void GetInitialize() {

        auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView Exit = findViewById(R.id.ic_exit);


        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Institute_Registration.this, Institute_Registration.class));

        imgbtn_capture = findViewById(R.id.imgbtn_capture);
        iv_attachment = findViewById(R.id.img_logo);
        imageutils = new Imageutils(this);

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

        imgbtn_capture.setOnClickListener(v -> imageutils.imagepicker(1));

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

    //**********************************************************************************************
    @Override
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

    }
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


            values.put("IsPaid",0);
            values.put("PayId", "");
            values.put("PaidDate", "");
            values.put("StudentCount", "");


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
