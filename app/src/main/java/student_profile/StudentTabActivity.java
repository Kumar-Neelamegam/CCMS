package student_profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


import core_modules.DashboardNew;
import entry_activities.Send_SMS;
import registers_activities.Students_Register;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/**
 * Created by KUMAR on 6/9/2017.
 */

public class StudentTabActivity extends AppCompatActivity {

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, Options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        try {


            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //**********************************************************************************************

    String SID;

    private void GetInitialize() {


        Bundle b = getIntent().getExtras();
        SID = b.getString("SID");
        //Toast.makeText(this, "StudentTabActivity: "+SID, Toast.LENGTH_SHORT).show();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Back = findViewById(R.id.toolbar_back);
        Exit = findViewById(R.id.ic_exit);
        Options = findViewById(R.id.ic_options);


        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = Student_Profile.newInstance(SID);
                                break;
                            case R.id.action_item2:
                                selectedFragment = Attendance_Profile.newInstance(SID);
                                break;
                            case R.id.action_item3:
                                selectedFragment = Marks_Profile.newInstance(SID);
                                break;
                            case R.id.action_item4:
                                selectedFragment = Fee_Profile.newInstance(SID);
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, Student_Profile.newInstance(SID));
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    //**********************************************************************************************
    CharSequence[] items;
    int balance_Fee;

    private void Controllisteners() {

        String Str_Total_Fee1 = Baseconfig.LoadValue("select Coaching_Fee as dstatus from Bind_EnrollStudents where SID='" + SID + "'");
        String Str_PaidFee1 = Baseconfig.CheckValue(Baseconfig.LoadValue("select IFNULL(SUM(Paid_Fee),'0')as dstatus from Bind_FeeEntry where SID='" + SID + "'"));

        balance_Fee = Integer.parseInt(Str_Total_Fee1) - Integer.parseInt(Str_PaidFee1);

        //Toast.makeText(this, ""+String.valueOf(balance_Fee), Toast.LENGTH_SHORT).show();


        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, StudentTabActivity.class));

        Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (balance_Fee == 0)//payment ilaina fee update option - kidaiyathu
                {
                    items = new CharSequence[]{
                            "Update Profile", "Delete Profile", "Close"
                    };

                } else {//payment balance iruntha fee update option - irukum
                    items = new CharSequence[]{
                            "Update Profile", "Update Fee", "Send UnPaid Fee - SMS Notification", "Delete Profile", "Close"
                    };

                }


                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(StudentTabActivity.this);
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].toString().equalsIgnoreCase("Update Profile")) {

                            StudentTabActivity.this.finish();
                            Intent update = new Intent(StudentTabActivity.this, Student_Profile_Edit.class);
                            update.putExtra("SID", SID);
                            startActivity(update);

                        } else if (items[item].toString().equalsIgnoreCase("Update Fee")) {

                            //pop up la
                            LoadPOPUP_Fee(SID);

                        } else if (items[item].toString().equalsIgnoreCase("Delete Profile")) {
                            Delete(SID);
                        } else if (items[item].toString().equalsIgnoreCase("Send UnPaid Fee - SMS Notification")) {
                            if (Baseconfig.CheckNW(StudentTabActivity.this)) {

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
                                    Baseconfig.SweetDialgos(3, StudentTabActivity.this, "Information", "Get username and password from SMS INDIA HUB to send sms..\nadd it in profile..", "OK");

                                }
                            } else {
                                Baseconfig.SweetDialgos(3, StudentTabActivity.this, "Information", "No internet connectivity available..\nEnable data connection from settings..", "OK");
                            }

                        }

                    }
                });
                android.support.v7.app.AlertDialog alert = builder.create();
                Dialog dialog = alert;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();

            }
        });
    }


//**********************************************************************************************

    public class SendSMS extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(StudentTabActivity.this);
        int Status;

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

            /*String Query = "select a.Id,a.Mobile_Number,a.SID,a.Name,a.Gender,a.Subject,Coaching_Fee,SUM(Paid_Fee)as Paid_Fee,\n" +
                    "            IFNULL(Coaching_Fee-(SUM(Paid_Fee)),'0')as Balance\n" +
                    "            from Bind_EnrollStudents a inner join Bind_FeeEntry b on a.SID=b.SID where a.SID='" + SID + "'\n";
*/
            String Query = "select Id,Mobile_Number,SID,Name,Gender,Subject,Coaching_Fee from Bind_EnrollStudents a where a.SID='" + SID + "'";


            Cursor c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));

                        String Coaching_Fee = c.getString(c.getColumnIndex("Coaching_Fee"));
                        String FeePaid = Baseconfig.LoadValue("select IFNULL(SUM(Paid_Fee),'0')as dstatus from Bind_FeeEntry where SID='" + SID + "'");

                        int Balance = 0;
                        if (FeePaid != null && Integer.parseInt(FeePaid) > 0) {
                            Balance = Integer.parseInt(Coaching_Fee) - Integer.parseInt(FeePaid);
                        }
                        if (Balance > 0) {

                          /*  String Message = "Dear parent, kindly pay the remaining tuition fees of Rs. " + Balance + " " +
                                    "as early as possible. Thank you.";*/
                            String Name = c.getString(c.getColumnIndex("Name"));
                            String Gender = c.getString(c.getColumnIndex("Gender"));
                            if(Gender.toString().equalsIgnoreCase("Male"))
                            {
                                Gender="son";

                            }else if(Gender.toString().equalsIgnoreCase("Female"))
                            {
                                Gender="daughter";
                            }
                            String Subject=c.getString(c.getColumnIndex("Subject"));

                            //Dear parent, kindly pay the remaining tuition fees of Rs. ##pending amount## regarding ##subject name##
                            // coaching of your ##son/daughter## ##student name## as early as possible. Thank you.

                            String Message="Dear parent, kindly pay the remaining tuition fees of Rs. " + Balance + " regarding "+Subject+" coaching of your "+Gender+" "+Name+" as early as possible. Thank you.";

                            String MobileNo = c.getString(c.getColumnIndex("Mobile_Number"));

                            SMSSender(StudentTabActivity.this, Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNo, Message, Baseconfig.SMS_SID, "0", "2", ID, SID);

                            Status = 1;
                        }


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

            new SweetAlertDialog(StudentTabActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(StudentTabActivity.this.getResources().getString(R.string.information))
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

        }else
        {
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

           /* SQLiteDatabase db = Baseconfig.GetDb();
            ContentValues values = new ContentValues();
            values.put("IsSMS_Sent", 1);
            db.update("Bind_EnrollStudents", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
            db.close();
            Log.e("Update Bind_EnrollStudents SMS Status: ", values + " / " + "SID='" + SID + "'");
*/
        }

        return rsp;
    }

    //**********************************************************************************************
    void LoadPOPUP_Fee(String SID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentTabActivity.this);
        LayoutInflater inflater = StudentTabActivity.this.getLayoutInflater();
        View inflatedLayout = inflater.inflate(R.layout.update_fee, null);

        TextView Total_Fee = inflatedLayout.findViewById(R.id.total_fee);
        TextView Paid_Fee = inflatedLayout.findViewById(R.id.total_paid_fee);
        TextView Balance_Fee = inflatedLayout.findViewById(R.id.balance_fee);

        EditText Fee_Amount = inflatedLayout.findViewById(R.id.edt_feee);

        FButton Close = inflatedLayout.findViewById(R.id.bttn_clear);
        FButton Pay = inflatedLayout.findViewById(R.id.bttn_pay);


        AlertDialog show;

        // Set the dialog layout
        builder.setView(inflatedLayout);
        show = builder.show();
        show.setCancelable(false);
        show.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;

        String Str_Total_Fee1 = Baseconfig.LoadValue("select Coaching_Fee as dstatus from Bind_EnrollStudents where SID='" + SID + "'");
        String Str_PaidFee1 = Baseconfig.CheckValue(Baseconfig.LoadValue("select IFNULL(SUM(Paid_Fee),'0')as dstatus from Bind_FeeEntry where SID='" + SID + "'"));

        int balance_Fee = Integer.parseInt(Str_Total_Fee1) - Integer.parseInt(Str_PaidFee1);

        Total_Fee.setText(Str_Total_Fee1);
        Paid_Fee.setText(String.valueOf(Str_PaidFee1));
        Balance_Fee.setText(String.valueOf(balance_Fee));


        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                show.dismiss();

            }
        });


        Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Fee_Amount.getText().length() > 0) {

                    if (Integer.parseInt(Fee_Amount.getText().toString()) <= balance_Fee) {
                        String Str_SID = "", Str_FeeAmount = "0", Str_FeeDate = "";

                        Str_SID = SID;
                        Str_FeeAmount = Fee_Amount.getText().toString();
                        Str_FeeDate = Baseconfig.GetDate();

                        SQLiteDatabase db = Baseconfig.GetDb();
                        ContentValues values = new ContentValues();
                        values.put("SID", Str_SID);
                        values.put("Paid_Fee", Str_FeeAmount);
                        values.put("Paid_Date", Str_FeeDate);
                        values.put("IsSMS_Sent", 0);
                        db.insert("Bind_FeeEntry", null, values);

                        db.close();

                        show.dismiss();

                        new SweetAlertDialog(StudentTabActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(StudentTabActivity.this.getResources().getString(R.string.information))
                                .setContentText("Fee Amount Updated successfully..")
                                .setConfirmText("OK")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismiss();

                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(StudentTabActivity.this, "Enter valid fee amount..", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Fee_Amount.setError("Required");
                }


            }
        });


    }


    //**********************************************************************************************

    public void Delete(String SID) {
        new SweetAlertDialog(StudentTabActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(StudentTabActivity.this.getResources().getString(R.string.information))
                .setContentText("Are you sure want to delete?")
                .setCancelText(StudentTabActivity.this.getResources().getString(R.string.no))
                .setConfirmText(StudentTabActivity.this.getResources().getString(R.string.yes))
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        sDialog.dismiss();

                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismiss();
                        SQLiteDatabase db = Baseconfig.GetDb();
                        db.execSQL("Delete from Bind_EnrollStudents where SID='" + SID + "'");
                        db.execSQL("Delete from Bind_Attendance where SID='" + SID + "'");
                        db.execSQL("Delete from Bind_FeeEntry where SID='" + SID + "'");
                        db.execSQL("Delete from Bind_MarkEntry where SID='" + SID + "'");
                        db.execSQL("Delete from Bind_SMSEntry where SID='" + SID + "'");
                        db.close();

                        StudentTabActivity.this.finishAffinity();
                        Intent list = new Intent(StudentTabActivity.this, Students_Register.class);
                        startActivity(list);

                    }
                })
                .show();

    }

    //**********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(this, Students_Register.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


}