package registers_activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import core_modules.Task_Navigation;
import entry_activities.Send_SMS;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;


public class Mark_Register extends AppCompatActivity {

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, SendSMS;

    //variable declaration

    Spinner batch, testname;
   FButton clear, search;
    WebView marks_list;


    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_register);


        try {


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
        Back = findViewById(R.id.toolbar_back);
        Exit = findViewById(R.id.ic_exit);

        batch = findViewById(R.id.spn_bat_li);
        //Loading batch list
        Baseconfig.LoadValuesSpinner(batch, Mark_Register.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");

        testname = findViewById(R.id.spn_test_list);

        clear = findViewById(R.id.bttn_clear);
        search = findViewById(R.id.bttn_search);
        SendSMS = findViewById(R.id.img_sendsms);


        marks_list = findViewById(R.id.web_mark_list);

        marks_list.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        marks_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        marks_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        marks_list.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        marks_list.setLongClickable(false);

        try {
            LoadWebview();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //**********************************************************************************************
    public void LoadWebview() {
        marks_list.getSettings().setJavaScriptEnabled(true);
        marks_list.setLayerType(View.LAYER_TYPE_NONE, null);
        marks_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        marks_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        marks_list.getSettings().setDefaultTextEncodingName("utf-8");

        marks_list.setWebChromeClient(new WebChromeClient() {
        });

        marks_list.setBackgroundColor(0x00000000);
        marks_list.setVerticalScrollBarEnabled(true);
        marks_list.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();


        marks_list.getSettings().setJavaScriptEnabled(true);

        marks_list.getSettings().setAllowContentAccess(true);

        marks_list.addJavascriptInterface(new WebAppInterface(Mark_Register.this), "android");

        // marks_list.loadUrl("file:///android_asset/Student_Profile/MarkRegister.html"); //Loading from  assets


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

    public String LoadMarksRegister() {

        String values = "";

        // MyDynamicToast.successMessage(Mark_Register.this, "Marks register loaded successfully..");

        StringBuilder str = new StringBuilder();
        StringBuilder str1 = new StringBuilder();


        SQLiteDatabase db = Baseconfig.GetDb();

        String Query = "\n" +
                "select (select Subject from Mstr_Test where Id=Test_ID)as Subject,\n" +
                "Batch_Name,\n" +
                "(select Date_Of_Test from Mstr_Test b where b.Id=a.Test_ID) as Date_Of_Test,\n" +
                "(select Name_Of_Test from Mstr_Test b where b.Id=a.Test_ID) as Name_Of_Test,\n" +
                "(select MaxMarks from Mstr_Test b where b.Id=a.Test_ID)as MaxMarks," +
                "SID,\n" +
                "(select Name from Bind_EnrollStudents c where c.SID=a.SID) as Student_Name,a.Obtained_Marks" +
                " from Bind_MarkEntry a where a.Batch_Name like '%" + batch.getSelectedItem().toString() + "%' " +
                "and a.Name_Of_Test='" + testname.getSelectedItem().toString() + "'";

        boolean q = Baseconfig.LoadReportsBooleanStatus("select Subject as dstatus1 from Bind_MarkEntry where Batch_Name like '%" + batch.getSelectedItem().toString() + "%' and Name_Of_Test='" + testname.getSelectedItem().toString() + "'");

        if (!q) {
            Baseconfig.SweetDialgos(3, Mark_Register.this, "Information", "No data available", "OK");

            return "";
        }
        String Str_Subject = "", Str_Batch = "", Str_Dateoftest = "", Str_Nameoftest = "", Str_MaxMarks = "";


        MyDynamicToast.informationMessage(Mark_Register.this, "Please wait marks register loading..");

        Log.e("Load Mark Register: ", Query);

        Cursor c = db.rawQuery(Query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Str_Subject = c.getString(c.getColumnIndex("Subject"));
                    Str_Batch = c.getString(c.getColumnIndex("Batch_Name"));
                    Str_Dateoftest = c.getString(c.getColumnIndex("Date_Of_Test"));
                    Str_Nameoftest = c.getString(c.getColumnIndex("Name_Of_Test"));
                    Str_MaxMarks = c.getString(c.getColumnIndex("MaxMarks"));

                    String SID = c.getString(c.getColumnIndex("SID"));
                    String Name = c.getString(c.getColumnIndex("Student_Name"));
                    String Obtained_Marks = c.getString(c.getColumnIndex("Obtained_Marks"));

                    if (Obtained_Marks == null) {
                        Obtained_Marks = "";
                    }

                    if (Obtained_Marks != null) {
                        if (Obtained_Marks.toString().equalsIgnoreCase("0")) {
                            Obtained_Marks = "Absent";
                        }

                    }
                    int position = c.getPosition();


                    str1.append(" <tr>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + (position + 1) + "</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + SID + "</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Name + "</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Obtained_Marks + "</td>\n" +
                            "  </tr>");


                } while (c.moveToNext());
            }
        }

        c.close();
        db.close();


        values = "<!DOCTYPE html>\n" +
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
                "<div class=\"table-responsive\">          \n" +
                "<table class=\"table table-bordered table-hover\" >\n" +
                "\n" +
                "  <tr>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Subject</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Batch<br>Name</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Date Of<br>Test</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Name Of<br>Test</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">MaxMarks</font></th>\n" +
                "  </tr> <tr>" +
                "<td><font color=\"#000\">" + Str_Subject + "</td>\n" +
                "<td><font color=\"#000\">" + Str_Batch + "</td>\n" +
                "<td><font color=\"#000\">" + Str_Dateoftest + "</td>\n" +
                "<td><font color=\"#000\">" + Str_Nameoftest + "</td>\n" +
                "<td><font color=\"#000\">" + Str_MaxMarks + "</td>\n" +
                "</tr>" +
                "</table>" +
                "</div>" +
                "<div class=\"table-responsive\">          \n" +
                "<table class=\"table table-bordered table-hover\" >\n" +
                "\n" +
                "  <tr>\n" +
                "    <th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SNo</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SID</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Student<br>Name</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Obtained<br>Marks</font></th>\n" +
                "  </tr>" +
                "   " + str1.toString() +
                "</table>" +

                "</div>" +

                "</body>" +
                "</html> ";


        return values;

    }

    //#######################################################################################################

    private void Controllisteners() {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Mark_Register.this, Mark_Register.class));


        batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (batch.getSelectedItemPosition() > 0) {
                    Baseconfig.LoadValuesSpinner(testname, Mark_Register.this, "select distinct Name_Of_Test as dvalue from Mstr_Test where IsActive='1' and Batch_Name='" + batch.getSelectedItem().toString() + "'", "Select");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (batch.getSelectedItemPosition() > 0 && testname.getSelectedItemPosition() > 0) {

                    try {

                        marks_list.loadDataWithBaseURL("file:///android_asset/", LoadMarksRegister(), "text/html", "utf-8", null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Baseconfig.SweetDialgos(3, Mark_Register.this, "Information", "Select batch & test..", "OK");
                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Clear();
            }
        });

        SendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (batch.getSelectedItemPosition() > 0 && testname.getSelectedItemPosition() > 0) {

                    SelectedBatch = batch.getSelectedItem().toString();
                    Selected_Test = testname.getSelectedItem().toString();

                    LoadSMSSettings();

                } else {
                    Baseconfig.SweetDialgos(3, Mark_Register.this, "Information", "Select batch & test..", "OK");
                }


            }
        });

    }

    //**********************************************************************************************
    String SelectedBatch;
    String Selected_Test;

    void LoadSMSSettings() {

        if (Baseconfig.CheckNW(this)) {

            String SMSOption_Query="select SMSOption as dstatus from Bind_InstituteInfo";
            int SMSOption = Integer.parseInt(Baseconfig.LoadValue(SMSOption_Query));

            if(SMSOption==1)
            {
                new SendSMS().execute();
            }else if(SMSOption==2 && Baseconfig.SMS_Username.length()>0 && Baseconfig.SMS_Password.length()>0)
            {
                new SendSMS().execute();
            }
            else
            {
                Baseconfig.SweetDialgos(3, this, "Information", "Get username and password from SMS INDIA HUB to send sms..\nadd it in profile..", "OK");

            }

        } else {
            Baseconfig.SweetDialgos(3, this, "Information", "No internet connectivity available..\nEnable data connection from settings..", "OK");
        }


    }
    //**********************************************************************************************

    public class SendSMS extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(Mark_Register.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.dialog.setMessage("Sending SMS...\nPlease wait..");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {


            SQLiteDatabase db = Baseconfig.GetDb();

            /**
             * 1. Sending marks to parents
             */
            String Query = "select a.Id,a.SID,b.Mobile_Number,b.Gender,b.Name,(a.Obtained_Marks||'/'||c.MaxMarks)as Marks,c.Subject," +
                    "c.Name_Of_Test,c.Date_Of_Test from Bind_MarkEntry a inner join Bind_EnrollStudents b on a.SID=b.SID " +
                    "inner join Mstr_Test c on a.Test_ID=c.Id \n" +
                    "where a.Batch_Name='" + SelectedBatch + "' and a.Name_Of_Test='" + Selected_Test + "' and Absent_Status='0' and a.IsSMS_Sent=0";

            Cursor c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));
                        String MobileNumber = c.getString(c.getColumnIndex("Mobile_Number"));
                        String Name = c.getString(c.getColumnIndex("Name"));
                        String Mark = c.getString(c.getColumnIndex("Marks"));
                        String SubjectName = c.getString(c.getColumnIndex("Subject"));
                        String NameOfTest = c.getString(c.getColumnIndex("Name_Of_Test"));
                        String Date = c.getString(c.getColumnIndex("Date_Of_Test"));

                        String Gender = c.getString(c.getColumnIndex("Gender"));

                        if (Gender.toString().equalsIgnoreCase("Male")) {
                            Gender = "son";

                        } else if (Gender.toString().equalsIgnoreCase("Female")) {
                            Gender = "daughter";
                        }


                        String Message = "Dear parent, your " + Gender + " " + Name + " has secured " +
                                "" + Mark + " in " + SubjectName + " test " + NameOfTest + " held on " + Date + "";

                        SMSSender(Mark_Register.this, Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNumber, Message, Baseconfig.SMS_SID, "0", "2", ID, SID);

                    } while (c.moveToNext());

                }

            }


            /**
             * 2. Sending absent for test - list for parents
             */
            c = null;
            String Query1 = "select a.Id,a.SID,b.Mobile_Number,b.Gender,b.Name,(a.Obtained_Marks||'/'||c.MaxMarks)as Marks,c.Subject," +
                    "c.Name_Of_Test,c.Date_Of_Test from Bind_MarkEntry a inner join Bind_EnrollStudents b on a.SID=b.SID " +
                    "inner join Mstr_Test c on a.Test_ID=c.Id \n" +
                    "where a.Batch_Name='" + SelectedBatch + "' and a.Name_Of_Test='" + Selected_Test + "' and Absent_Status='1' and a.IsSMS_Sent=0";

            c = db.rawQuery(Query1, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));
                        String MobileNumber = c.getString(c.getColumnIndex("Mobile_Number"));
                        String Name = c.getString(c.getColumnIndex("Name"));
                        String Mark = c.getString(c.getColumnIndex("Marks"));
                        String SubjectName = c.getString(c.getColumnIndex("Subject"));
                        String NameOfTest = c.getString(c.getColumnIndex("Name_Of_Test"));
                        String Date = c.getString(c.getColumnIndex("Date_Of_Test"));

                        String Gender = c.getString(c.getColumnIndex("Gender"));

                        if (Gender.toString().equalsIgnoreCase("Male")) {
                            Gender = "son";

                        } else if (Gender.toString().equalsIgnoreCase("Female")) {
                            Gender = "daughter";
                        }


                        String Message = "Dear parent, you " + Gender + " " + Name + " is absent for " + SubjectName + " test " + NameOfTest + " held on " + Date + "";

                        SMSSender(Mark_Register.this, Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNumber, Message, Baseconfig.SMS_SID, "0", "2", ID, SID);

                    } while (c.moveToNext());

                }

            }


            c.close();
            db.close();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            new SweetAlertDialog(Mark_Register.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(Mark_Register.this.getResources().getString(R.string.information))
                    .setContentText("SMS Sent Successfully..")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismiss();

                        }
                    })
                    .show();

        }


    }


    //**********************************************************************************************
    public static String SMSSender(Context ctx, String user, String password, String msisdn, String msg, String sid, String fl, String gwid, String TableId, String SID) {
        String rsp = "";
        String retval = "N/A";
        String SMSOption_Query="select SMSOption as dstatus from Bind_InstituteInfo";
        int SMSOption = Integer.parseInt(Baseconfig.LoadValue(SMSOption_Query));

        if(SMSOption==1)//MOBILE SMS
        {

            Baseconfig.sendSMS(msisdn, msg, ctx);
            rsp = "Success";

        }else{
            try {
                // Construct The Post Data
                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("msisdn", "UTF-8") + "=" + URLEncoder.encode(msisdn, "UTF-8");
                data += "&" + URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8");
                data += "&" + URLEncoder.encode("sid", "UTF-8") + "=" + URLEncoder.encode(sid, "UTF-8");
                data += "&" + URLEncoder.encode("fl", "UTF-8") + "=" + URLEncoder.encode(fl, "UTF-8");
                data += "&" + URLEncoder.encode("gwid", "UTF-8") + "=" + URLEncoder.encode(gwid, "UTF-8");

                //Push the HTTP Request
                URL url = new URL("http://cloud.smsindiahub.in/vendorsms/pushsms.aspx");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                //Read The Response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    // Process line...
                    retval += line;
                }
                wr.close();
                rd.close();

                //System.out.println(retval);
                rsp = retval;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Toast.makeText(ctx, "SMS Response: "+retval, Toast.LENGTH_SHORT).show();
        Log.e("SMSSender Response: ", rsp);
        if (rsp.toString().contains("Success")) {

            SQLiteDatabase db = Baseconfig.GetDb();
            ContentValues values = new ContentValues();
            values.put("IsSMS_Sent", 1);
            db.update("Bind_MarkEntry", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
            db.close();
            Log.e("Update Bind_MarkEntry SMS Status: ", values + " / " + "SID='" + SID + "'");

        }

        return rsp;
    }


    //********************************************************************************************

    //**********************************************************************************************

    public void Clear() {

        batch.setSelection(0);

        testname.setAdapter(null);

        marks_list.loadUrl("about:blank");


    }
    //**********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(Mark_Register.this, Task_Navigation.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    //End
}
