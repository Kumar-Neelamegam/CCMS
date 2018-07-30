package core_modules;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.File;

import utilities.Baseconfig;
import utilities.FButton;
import utilities.Imageutils;
import utilities.Validation1;
import vcc.coremodule.R;

/**
 * Created by KUMAR on 6/14/2017.
 */

public class Institute_Edit extends AppCompatActivity implements Imageutils.ImageAttachmentListener {


    private Toolbar toolbar;

    ImageButton imgbtn_capture;//, imgbtn_browse;

    ImageView iv_attachment;

    //For Image Attachment

    private Bitmap bitmap;
    private String file_name;

    Imageutils imageutils;

    FButton Cancel, Submit;

    ImageView Back;

    EditText Ins_name, Ins_add, Ins_own, Ins_mobile, Ins_email;


    ImageView Logo_Imgvw;
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

    void GetInitialize() {

        try {

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ImageView Exit= findViewById(R.id.ic_exit);

            Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Institute_Edit.this,Institute_Edit.class));

            imgbtn_capture = findViewById(R.id.imgbtn_capture);
            iv_attachment = findViewById(R.id.img_logo);
            imageutils = new Imageutils(this);

            Submit = findViewById(R.id.btn_submit);
            Submit.setText("Update");
            Cancel = findViewById(R.id.btn_cancel);

            Back = toolbar.findViewById(R.id.ic_back);
            Back.setVisibility(View.VISIBLE);

            Ins_name = findViewById(R.id.edt_name);
            Ins_add = findViewById(R.id.edt_address);
            Ins_own = findViewById(R.id.edt_ownername);
            Ins_mobile = findViewById(R.id.edt_mobileno);
            Ins_email = findViewById(R.id.edt_email);
            Logo_Imgvw = findViewById(R.id.img_logo);

            LoadValues();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void LoadValues()
    {

        String Logo="",Institute_Name="",Institute_Address="",Mobile="",OwnerName="",Email="",Reg_Date="";

        String Ret_Value="";
        SQLiteDatabase db= Baseconfig.GetDb();
        String Query="select * from Bind_InstituteInfo";
        Cursor c=db.rawQuery(Query,null);
        if(c!=null) {
            if (c.moveToFirst()) {
                do {

                    Logo=c.getString(c.getColumnIndex("Logo"));
                    Institute_Name=c.getString(c.getColumnIndex("Institute_Name"));
                    Institute_Address=c.getString(c.getColumnIndex("Institute_Address"));
                    Mobile=c.getString(c.getColumnIndex("Mobile"));
                    OwnerName=c.getString(c.getColumnIndex("Owner_Name"));
                    Email=c.getString(c.getColumnIndex("Email"));
                    Reg_Date=c.getString(c.getColumnIndex("ActDate"));

                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();



        Ins_name.setText(Institute_Name);
        Ins_add.setText(Institute_Address);
        Ins_own.setText(OwnerName);
        Ins_mobile.setText(Mobile);
        Ins_email.setText(Email);

        Baseconfig.LogoImgPath=Logo;

        if(iv_attachment.toString().length()>0)
        {
            Glide.with(iv_attachment.getContext()).load(new File(Logo)).into(iv_attachment);

        }
        else
        {
            Glide.with(iv_attachment.getContext()).load(Uri.parse("file:///android_asset/logo_vcc.png")).into(iv_attachment);
        }




    }

    //**********************************************************************************************


    void Controllisteners() {
        imgbtn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageutils.imagepicker(1);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Institute_Edit.this.finishAffinity();
                Intent next=new Intent(Institute_Edit.this,Task_Navigation.class);
                startActivity(next);

            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Institute_Edit.this.finishAffinity();
                Intent next=new Intent(Institute_Edit.this,Task_Navigation.class);
                startActivity(next);

            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {

                    SaveLocal();

                } else {

                    Baseconfig.SweetDialgos(4, Institute_Edit.this, "Information", " Please fill all mandatory fields marked with (*)\n"+str.toString(), "OK");
                }


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
        imageutils.createImage(file, filename, path, false);

    }
    //**********************************************************************************************

    StringBuilder str;

    public boolean checkValidation() {

        str=new StringBuilder();

        boolean ret = true;

        if (!Validation1.isEmailAddress((AppCompatEditText) Ins_email, true)) {
            str.append("Enter Valid Mail *");
            ret = false;
        }

        if (!Validation1.isMobileNumber((AppCompatEditText) Ins_mobile, true)) {
            str.append("Enter Valid Mobile No *");
            ret = false;
        }

        if(Ins_own.getText().length()==0) {
            str.append("Enter Owner Name *");
            ret = false;
        }

        if(Ins_add.getText().length()==0) {
            str.append("Enter Address *");
            ret = false;
        }

        if(Ins_name.getText().length()==0) {
            str.append("Enter Institute Name *");
            ret = false;
        }


        return ret;

    }
    //**********************************************************************************************

    public void SaveLocal() {

        String Str_InsName = "", Str_Address = "", Str_OwnName = "", Str_Mobile = "", Str_mail = "", Str_Isactive = "1";

        Str_InsName = Ins_name.getText().toString();
        Str_Address = Ins_add.getText().toString();
        Str_OwnName = Ins_own.getText().toString();
        Str_Mobile = Ins_mobile.getText().toString();
        Str_mail = Ins_email.getText().toString();

        if(Baseconfig.LogoImgPath.toString().length()==0)
        {
            Baseconfig.LogoImgPath= Environment.getExternalStorageDirectory()+"/vcc/logo_vcc.jpg";
        }


        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Institute_Name", Str_InsName);
        values.put("Institute_Address", Str_Address);
        values.put("Owner_Name", Str_OwnName);
        values.put("Mobile", Str_Mobile);
        values.put("Email", Str_mail);
        values.put("Logo", Baseconfig.LogoImgPath);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "0");
        values.put("ModifiedDate", Baseconfig.GetDate());

        db.update("Bind_InstituteInfo", values, null, null);
        db.close();

        ShowSuccessDialog();

    }


    //**************************************************************************************

    public void ShowSuccessDialog() {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("Institute details updated successfully..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                        Institute_Edit.this.finish();
                        startActivity(new Intent(Institute_Edit.this, Task_Navigation.class));
                    }
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

        Institute_Edit.this.finishAffinity();
        Intent next=new Intent(Institute_Edit.this,Task_Navigation.class);
        startActivity(next);

    }



//**********************************************************************************************
    //Ends
}