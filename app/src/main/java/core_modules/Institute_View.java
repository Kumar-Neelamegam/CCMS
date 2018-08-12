package core_modules;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/**
 * Created by KUMAR on 6/14/2017.
 */

public class Institute_View extends AppCompatActivity{

    //*********************************************************************************************

    WebView institute_details;
    ImageView institute_logo;
    FButton Update;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_view);

        try {


            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    //**********************************************************************************************

    void GetInitialize()
    {
        try {

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ImageView Exit= findViewById(R.id.ic_exit);

            Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Institute_View.this,Institute_View.class));

            ImageView Back = toolbar.findViewById(R.id.toolbar_back);
            Back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Institute_View.this.finishAffinity();
                    Intent next=new Intent(Institute_View.this,Task_Navigation.class);
                    startActivity(next);

                }
            });


            institute_details = findViewById(R.id.web_instituteprofile);

            institute_details.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            institute_details.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            institute_details.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

            institute_logo= findViewById(R.id.imgvw_logo_photo);

            Update= findViewById(R.id.btn_update);


            LoadWebview();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //**********************************************************************************************


    @Override
    public void onBackPressed() {

        Institute_View.this.finishAffinity();
        Intent next=new Intent(Institute_View.this,Task_Navigation.class);
        startActivity(next);

    }

    void Controllisteners()
    {

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Institute_View.this.finish();
                Intent next=new Intent(Institute_View.this,Institute_Edit.class);
                startActivity(next);

            }
        });


        institute_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Query="select Logo as dstatus from Bind_InstituteInfo";
                Baseconfig.Show_PhotoInDetail(Query,Institute_View.this);

            }
        });

    }
    //*********************************************************************************************

    public void LoadWebview()
    {

        institute_details.getSettings().setJavaScriptEnabled(true);
        institute_details.setLayerType(View.LAYER_TYPE_NONE, null);
        institute_details.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        institute_details.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        institute_details.getSettings().setDefaultTextEncodingName("utf-8");

        institute_details.setWebChromeClient(new WebChromeClient() {
        });

        institute_details.setBackgroundColor(0x00000000);
        institute_details.setVerticalScrollBarEnabled(true);
        institute_details.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();

        //MyDynamicToast.informationMessage(getActivity(), "Please wait attendance details loading..");

        institute_details.getSettings().setJavaScriptEnabled(true);

        institute_details.getSettings().setAllowContentAccess(true);

        institute_details.addJavascriptInterface(new WebAppInterface(Institute_View.this), "android");
        try {

            institute_details.loadDataWithBaseURL("file:///android_asset/", LoadStudentProfile(), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //#######################################################################################################

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();


        }
    }

    //#######################################################################################################
    public String LoadStudentProfile()
    {

        String Logo="",Institute_Name="",Institute_Address="",Mobile="",OwnerName="",Email="", Password="",Reg_Date="";
        String SMSID="", SMSUsername="", SMSPassword="";

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
                    Password=c.getString(c.getColumnIndex("EmailPassword"));
                    SMSID=c.getString(c.getColumnIndex("SMSSID"));
                    SMSUsername=c.getString(c.getColumnIndex("SMSUsername"));
                    SMSPassword=c.getString(c.getColumnIndex("SMSPassword"));
                    Reg_Date=c.getString(c.getColumnIndex("ActDate"));

                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();




        if(institute_logo.toString().length()>0)
        {

            Glide.with(institute_logo.getContext()).load(new File(Logo)).into(institute_logo);
        }
        else
        {
            Glide.with(institute_logo.getContext()).load(Uri.parse("file:///android_asset/logo_vcc.png")).into(institute_logo);
        }



        Ret_Value="<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n" +
                "<link rel=\"stylesheet\"  type=\"text/css\" href=\"file:///android_asset/Student_Profile/css/english.css\"/>\n" +
                "\n" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/Student_Profile/css/bootstrap.min.css\"/>\n" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/Student_Profile/css/bootstrap-theme.min.css\"/>\n" +
                "\n" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/Student_Profile/css/font-awesome.min.css\" type=\"text/css\"/>\n" +
                "\n" +
                "<script src=\"file:///android_asset/Student_Profile/css/jquery.min.js\"></script>\n" +
                "<script src=\"file:///android_asset/Student_Profile/css/bootstrap.min.js\"></script>\n" +
                "\n" +
                "</head>\n" +
                "<body>  \n" +
                "\n" +
                " \n" +
                "<div class=\"table-responsive\">          \n" +
                "\n" +
                "<font class=\"sub\">\n" +
                "<i class=\"fa fa-user-circle-o fa-2x\" \n" +
                "aria-hidden=\"true\">\n" +
                "</i> Institute Details</font>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<table class=\"table table-bordered table-hover\">\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"20%\" style=\"color:#6A1B9A;\"></i><b>  Institute Name</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Institute_Name+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Institute Address</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Institute_Address+"</td>\n" +
                "</tr>\n" +

                "\n" +
                "<tr>\n" +
                "<td height=\"20\" style=\"color:#6A1B9A;\"></i><b>  Registration Date</b></td> \n" +
                "<td height=\"20\" style=\"color:#000000;\">   "+Reg_Date+"</td>\n" +
                "</tr>\n" +


                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Mobile</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Mobile+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Owner Name</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+OwnerName+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Email</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Email+"</td>\n" +
                "</tr>\n" +
                "\n" +


                "<tr>\n" +
                "<td height=\"20\" style=\"color:#6A1B9A;\"></i><b>  Email Password</b></td> \n" +
                "<td height=\"20\" style=\"color:#000000;\">   "+Password+"</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<td height=\"20\" style=\"color:#6A1B9A;\"></i><b>  SMS Sender ID</b></td> \n" +
                "<td height=\"20\" style=\"color:#000000;\">   "+SMSID+"</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<td height=\"20\" style=\"color:#6A1B9A;\"></i><b>  SMS Gateway Username</b></td> \n" +
                "<td height=\"20\" style=\"color:#000000;\">   "+SMSUsername+"</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<td height=\"20\" style=\"color:#6A1B9A;\"></i><b>  SMS Gateway Password</b></td> \n" +
                "<td height=\"20\" style=\"color:#000000;\">   "+SMSPassword+"</td>\n" +
                "</tr>\n" +

                " \n" +
                "</table>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html> ";



        return Ret_Value;

    }

    //**********************************************************************************************

}
