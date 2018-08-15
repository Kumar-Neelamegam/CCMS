package registers_activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;


import core_modules.Task_Navigation;
import entry_activities.Send_SMS;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;


public class Attendance_Register extends AppCompatActivity {


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, SendSMS;

    Spinner att_bat;
    EditText date;
    FButton clear, search;
    WebView attendance_list;


    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_register);


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

        att_bat = findViewById(R.id.att_batch);
        //Loading batch list
        Baseconfig.LoadValuesSpinner(att_bat, Attendance_Register.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");


        date = findViewById(R.id.att_date);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        String str = dateformat.format(c.getTime());
        date.setText(str);


        clear = findViewById(R.id.btn_clr);
        search = findViewById(R.id.btn_search);

        SendSMS = findViewById(R.id.img_sendsms);

        attendance_list = findViewById(R.id.web_stu_list);

        attendance_list.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        attendance_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        attendance_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        attendance_list.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        attendance_list.setLongClickable(false);


    }
    //**********************************************************************************************

    public void LoadWebview() {

        attendance_list.getSettings().setJavaScriptEnabled(true);
        attendance_list.setLayerType(View.LAYER_TYPE_NONE, null);
        attendance_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        attendance_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        attendance_list.getSettings().setDefaultTextEncodingName("utf-8");

        attendance_list.setWebChromeClient(new WebChromeClient() {
        });

        attendance_list.setBackgroundColor(0x00000000);
        attendance_list.setVerticalScrollBarEnabled(true);
        attendance_list.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();

        attendance_list.getSettings().setJavaScriptEnabled(true);

        attendance_list.getSettings().setAllowContentAccess(true);

        attendance_list.addJavascriptInterface(new WebAppInterface(Attendance_Register.this), "android");
        try {

            attendance_list.loadDataWithBaseURL("file:///android_asset/", LoadAttendanceRegister(), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //attendance_list.loadUrl("file:///android_asset/Student_Profile/AttendanceRegister.html"); //Loading from  assets


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

    public String LoadAttendanceRegister() {

        String values = "";
        //MyDynamicToast.successMessage(Attendance_Register.this, "Attendance register loaded successfully..");

        StringBuilder str = new StringBuilder();
        StringBuilder str1 = new StringBuilder();

        String Str_Batch_Name = att_bat.getSelectedItem().toString();
        String Str_SelectedDate = date.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();

        String Query = "select IFNULL(IsUpdate,'0')as IsUpdate,Batch_Name,(select Class_From ||'-'|| Class_To  from Mstr_Batch where " +
                "Batch_Name='" + Str_Batch_Name + "') as Timing,\n" +
                "SID,(select Name from Bind_EnrollStudents b where b.SID=a.SID) as Student_Name, " +
                "IFNULL(Attendance_Status,'Absent')as Attendance_Status,Attendance_Date from Bind_Attendance a where \n" +
                "Attendance_Date='" + Str_SelectedDate + "' and Batch_Name like '%" + Str_Batch_Name + "%'";

        boolean q = Baseconfig.LoadReportsBooleanStatus("select Batch_Name as dstatus1 from Bind_Attendance where Attendance_Date='" + Str_SelectedDate + "' and Batch_Name like '%" + Str_Batch_Name + "%'");
        if (!q) {
            Baseconfig.SweetDialgos(3, Attendance_Register.this, "Information", "No data available", "OK");

            return "";
        }


        MyDynamicToast.informationMessage(Attendance_Register.this, "Please wait attendance register loading..");

        Log.e("Load Attendance Register: ", Query);

        String Batch_Name = "-", Timing = "-", Attendance_Date = "-";
        Batch_Name = att_bat.getSelectedItem().toString();
        Cursor c = db.rawQuery(Query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    //Batch_Name=c.getString(c.getColumnIndex("Batch_Name"));
                    Timing = c.getString(c.getColumnIndex("Timing"));
                    Attendance_Date = c.getString(c.getColumnIndex("Attendance_Date"));

                    String SID = c.getString(c.getColumnIndex("SID"));
                    String Name = c.getString(c.getColumnIndex("Student_Name"));
                    String IsUpdate = c.getString(c.getColumnIndex("IsUpdate"));
                    String Attendance_Status = c.getString(c.getColumnIndex("Attendance_Status"));


                    int position = c.getPosition();

                    String Att_status = "";
                    if (IsUpdate.toString().equalsIgnoreCase("0")) {
                        Att_status = "<td bgcolor=\"#ffe246\" ><font color=\"#000\">Not Taken</font></td>";
                    } else if (IsUpdate.toString().equalsIgnoreCase("1")) {
                        Att_status = "<td bgcolor=\"#00cd92\" ><font color=\"#000\">Present</font></td>";
                    } else //if(Attendance_Status.toString().equalsIgnoreCase("2"))
                    {
                        Att_status = "<td bgcolor=\"#F08080\" ><font color=\"#000\">Absent</font></td>";
                    }

                    str1.append(" <tr>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + (position + 1) + "</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + SID + "</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Name + "</td>\n" +
                            "" + Att_status + "\n" +
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
                "<div class=\"table-responsive\">" +
                "<table class=\"table table-bordered table-hover\" > " +
                "<tr>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Batch Name</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Attendance<br>Date</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Timing</font></th>" +
                "  </tr><tr>" +
                "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Batch_Name + "</td>" +
                "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Attendance_Date + "</td>" +
                "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Timing + "</td>" +
                "  </tr>" +
                "</div>" +
                "<div class=\"table-responsive\">" +
                "<table class=\"table table-bordered table-hover\" >" +
                "\n" +
                " <tr>\n" +
                " <th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SNo</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SID</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Student<br>Name</font></th>\n" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Attendance<br>Status</font></th>\n" +
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

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Attendance_Register.this, Attendance_Register.class));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectDate();

            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (att_bat.getSelectedItemPosition() > 0 && date.getText().length() > 0) {

                    try {
                        LoadWebview();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Baseconfig.SweetDialgos(3, Attendance_Register.this, "Information", "Select batch & date..", "OK");
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

                if (att_bat.getSelectedItemPosition() > 0 && date.getText().length() > 0) {

                    SelectedBatch = att_bat.getSelectedItem().toString();
                    SelectedDate = date.getText().toString();

                    LoadSMSSettings();

                } else {
                    Baseconfig.SweetDialgos(3, Attendance_Register.this, "Information", "Select batch & date..", "OK");
                }


            }
        });


    }

    //**********************************************************************************************
    String SelectedBatch, SelectedDate;
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
        private final ProgressDialog dialog = new ProgressDialog(Attendance_Register.this);

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
            String Query = "select a.Id,a.SID,b.Mobile_Number,Gender,Name,(select Subject_Name from Mstr_Batch where Batch_Name='" + SelectedBatch + "')as " +
                    "Subject,a.Attendance_Date from " +
                    "Bind_Attendance a inner join Bind_EnrollStudents b on a.SID=b.SID   \n" +
                    "where a.Attendance_Status='Absent' and a.Batch_Name='" + SelectedBatch + "'  and a.Attendance_Date='" + SelectedDate + "' " +
                    "and a.IsSMS_Sent=0";

            Cursor c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));
                        String Name = c.getString(c.getColumnIndex("Name"));
                        String SubjectName = c.getString(c.getColumnIndex("Subject"));
                        String Date = c.getString(c.getColumnIndex("Attendance_Date"));
                        String Gender = c.getString(c.getColumnIndex("Gender"));

                        if (Gender.toString().equalsIgnoreCase("Male")) {
                            Gender = "son";

                        } else if (Gender.toString().equalsIgnoreCase("Female")) {
                            Gender = "daughter";
                        }

                        String Message = "Dear parent, your " + Gender + " " + Name + " is absent for " + SubjectName + " class today " + Date + "";

                        String MobileNo = c.getString(c.getColumnIndex("Mobile_Number"));

                        SMSSender(Attendance_Register.this, Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNo, Message, Baseconfig.SMS_SID, "0", "2", ID, SID);

                    } while (c.moveToNext());

                }

            }


            db.close();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            new SweetAlertDialog(Attendance_Register.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(Attendance_Register.this.getResources().getString(R.string.information))
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

        }else {
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
            db.update("Bind_Attendance", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
            db.close();
            Log.e("Update Attendance SMS Status: ", values + " / " + "SID='" + SID + "'");

        }

        return rsp;
    }


    //********************************************************************************************

    public void Clear() {

        att_bat.setSelection(0);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        String str = dateformat.format(c.getTime());
        date.setText(str);

        attendance_list.loadUrl("about:blank");


    }

    //**********************************************************************************************
    /*
    Date picker
     */
    static final int DATE_DIALOG_ID = 1;
    private int year;
    private int month;
    private int day;

    private void SelectDate() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        //Toast.makeText(this, ""+str, Toast.LENGTH_SHORT).show();
        showDialog(DATE_DIALOG_ID);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                //start changes...
                DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, selectedMonth, selectedDay);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = format.format(calendar.getTime());
            date.setText(dateString);
            Log.e("Selected DOB: ", dateString.toString());
        }


    };

    //**********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(Attendance_Register.this, Task_Navigation.class);
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
