package registers_activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import adapters.Getter_Setter;

import core_modules.Task_Navigation;
import entry_activities.Send_SMS;
import student_profile.StudentTabActivity;
import utilities.Baseconfig;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/21/2017.
 */

public class Students_Register extends AppCompatActivity {

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, Options;

    EditText Edt_Search;
    TextView Count;
    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_register);


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
        Options = findViewById(R.id.ic_options);

        boolean q = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Bind_EnrollStudents where IsActive='1';");
        if (q == false) {
            MyDynamicToast.informationMessage(Students_Register.this, "No details available..");

            Options.setVisibility(View.INVISIBLE);
            return;
        }

        Edt_Search = findViewById(R.id.search_edttxt);

        recyclerView = findViewById(R.id.recycler_view);

        Count = findViewById(R.id.txt_count);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_red_dark, android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Your refresh code here

                Toast.makeText(Students_Register.this, "refreshing...", Toast.LENGTH_SHORT).show();

                if (Baseconfig.CheckNW(Students_Register.this)) {

                    LoadRecyler(1, "");

                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Baseconfig.SweetDialgos(3, Students_Register.this, getString(R.string.str_information), getString(R.string.no_connection), getString(R.string.str_ok));
                }

            }
        });


        LoadRecyler(1, "");


        Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence[] items = {
                        "Generate Excel", "Email", "Send UnPaid Fee - SMS Notification", "Close"
                        //"Generate Excel", "Email", "Close"
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(Students_Register.this);
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].toString().equalsIgnoreCase("Generate Excel")) {


                            new ExportDatabaseCSVTask().execute("");

                        } else if (items[item].toString().equalsIgnoreCase("Email")) {


                            if (!Baseconfig.CheckNW(Students_Register.this)) {
                                Baseconfig.SweetDialgos(3, Students_Register.this, "Information", "No internet available\nEnable data connection from settings..", "OK");
                                return;
                            }
                            File report_file = new File(StudentRegister_FilePath);

                            if (report_file.exists()) {

                                SendEmail();

                            } else {

                                //Baseconfig.SweetDialgos(4,Reports_Attendance.this,"Information","","OK");

                                new SweetAlertDialog(Students_Register.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(Students_Register.this.getResources().getString(R.string.information))
                                        .setContentText("Student register not generated today..\nDo you want to generate and send email?")
                                        .setCancelText(Students_Register.this.getResources().getString(R.string.no))
                                        .setConfirmText(Students_Register.this.getResources().getString(R.string.yes))
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

                                                new ExportDatabaseCSVTask().execute("");

                                            }
                                        })
                                        .show();
                            }


                        } else if (items[item].toString().equalsIgnoreCase("Send UnPaid Fee - SMS Notification")) {

                            if (Baseconfig.CheckNW(Students_Register.this)) {
                                if(Baseconfig.SMS_Username.length()>0 && Baseconfig.SMS_Password.length()>0)
                                {
                                new SendSMS().execute();
                                }
                                else
                                {
                                    Baseconfig.SweetDialgos(3, Students_Register.this, "Information", "Get username and password from SMS INDIA HUB to send sms..\nadd it in profile..", "OK");

                                }
                            } else {
                                Baseconfig.SweetDialgos(3, Students_Register.this, "Information", "No internet connectivity available..\nEnable data connection from settings..", "OK");
                            }

                        }


                    }
                });
                AlertDialog alert = builder.create();
                Dialog dialog = alert;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();


            }
        });

    }

    //**********************************************************************************************

    public class SendSMS extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(Students_Register.this);
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

          /*  String Query = "select a.Id,a.Mobile_Number,a.SID,a.Name,a.Gender,a.Subject,Coaching_Fee,SUM(Paid_Fee)as Paid_Fee,\n" +
                    "            IFNULL(Coaching_Fee-(SUM(Paid_Fee)),'0')as Balance\n" +
                    "            from Bind_EnrollStudents a inner join Bind_FeeEntry b on a.SID=b.SID";*/

            String Query = "select Id,Mobile_Number,SID,Name,Gender,Subject,Coaching_Fee from Bind_EnrollStudents a";

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

                        //String Balance = c.getString(c.getColumnIndex("Balance"));

                        if (Balance > 0) {

                          /*  String Message = "Dear parent, kindly pay the remaining tuition fees of Rs. " + Balance + " " +
                                    "as early as possible. Thank you.";*/
                            String Name = c.getString(c.getColumnIndex("Name"));
                            String Gender = c.getString(c.getColumnIndex("Gender"));
                            if (Gender.toString().equalsIgnoreCase("Male")) {
                                Gender = "son";

                            } else if (Gender.toString().equalsIgnoreCase("Female")) {
                                Gender = "daughter";
                            }
                            String Subject = c.getString(c.getColumnIndex("Subject"));

                            String Message = "Dear parent, kindly pay the remaining tuition fees of Rs. " + Balance + " regarding " + Subject + " coaching of your " + Gender + " " + Name + " as early as possible. Thank you.";

                            String MobileNo = c.getString(c.getColumnIndex("Mobile_Number"));

                            SMSSender(Students_Register.this, Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNo, Message, Baseconfig.SMS_SID, "0", "2", ID, SID);

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

            new SweetAlertDialog(Students_Register.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(Students_Register.this.getResources().getString(R.string.information))
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
            db.update("Bind_EnrollStudents", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
            db.close();
            Log.e("Update Bind_EnrollStudents SMS Status: ", values + " / " + "SID='" + SID + "'");

        }

        return rsp;
    }

    //**********************************************************************************************
    void SendEmail() {

        String InstituteMailId = Baseconfig.LoadValue("select Email as dstatus from Bind_InstituteInfo");

        BackgroundMail.newBuilder(this)
                .withUsername(Baseconfig.MailID)
                .withPassword(Baseconfig.MailPassword)
                .withMailto(InstituteMailId)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Students - Register / " + Baseconfig.GetDate())
                .withBody("Hi,\nHere with attached the student register  (" + Baseconfig.GetDate() + ").\n")
                .withAttachments(StudentRegister_FilePath)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                        new SweetAlertDialog(Students_Register.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(Students_Register.this.getResources().getString(R.string.information))
                                .setContentText("Mail sent successfully..")
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
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();

    }
    //**********************************************************************************************

    /**
     * Generate CSV
     * 23-06-17
     */

    String StudentRegister_FilePath = Baseconfig.DATABASE_FILE_PATH + "/StudentRegister.csv";

    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(Students_Register.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting student register...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {

/*

            File exportDir = new File(Environment.getExternalStorageDirectory(), "");

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
*/

            File file = new File(StudentRegister_FilePath);

            if (file.exists())//if file iruntha delete panum
            {
                file.delete();
            }

            try {
                file.createNewFile();
                Baseconfig.CSVWriter csvWrite = new Baseconfig.CSVWriter(new FileWriter(file));
                SQLiteDatabase db = Baseconfig.GetDb();
                Cursor curCSV = db.rawQuery("select Id,Name,Gender,DOB,Father_Name,Father_Occupation,Mother_Name," +
                        "Address,Mobile_Number,Subject,Batch_Info,School_Name,CGPA,Coaching_Fee,Fee_Advance,ActDate," +
                        "SID,BoardExam_No,IsFullFee_Paid,Mother_Occupation,Standard,Joining_Date  from Bind_EnrollStudents", null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)
                            , curCSV.getString(3), curCSV.getString(4),
                            curCSV.getString(5),
                            curCSV.getString(6),
                            curCSV.getString(7),
                            curCSV.getString(8),
                            curCSV.getString(9),
                            curCSV.getString(10),
                            curCSV.getString(11),
                            curCSV.getString(12),
                            curCSV.getString(13),
                            curCSV.getString(14),
                            curCSV.getString(15),
                            curCSV.getString(16),
                            curCSV.getString(17),
                            curCSV.getString(18),
                            curCSV.getString(19),
                            curCSV.getString(20),
                            curCSV.getString(21)};

                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
                return true;
            } catch (SQLException sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                return false;
            } catch (IOException e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(Students_Register.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Students_Register.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //#######################################################################################################


    //#######################################################################################################
    private void Controllisteners() {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Students_Register.this, Students_Register.class));

        Edt_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {

                if (Edt_Search.getText().toString().length() > 0) {

                    LoadRecyler(2, cs.toString());
                } else {
                    LoadRecyler(1, "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    //**********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(Students_Register.this, Task_Navigation.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**************************************************************************************
    private RecyclerView recyclerView;

    public void LoadRecyler(int id, String str) {

        /**
         * To load recycler view old dashboard
         */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Students_Register.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.StudentItems> DataItems = prepareData(id, str);
        Student_Register_Adapter adapter = new Student_Register_Adapter(Students_Register.this, DataItems);
        recyclerView.setAdapter(adapter);

    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.StudentItems> prepareData(int id, String cs) {

        ArrayList<Getter_Setter.StudentItems> Dataitems = new ArrayList<>();
        Getter_Setter.StudentItems obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        int i = 0;

        String Query = "";

        if (id == 1) {
            Query = "select * from Bind_EnrollStudents where IsActive='1' order by Name";
        } else if (id == 2) {
            Query = "select * from Bind_EnrollStudents where IsActive='1'  and Name like '" + cs + "%' order by Name";
        }


        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    i++;
                    obj = new Getter_Setter.StudentItems();

                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setName(c.getString(c.getColumnIndex("Name")));
                    obj.setSID(c.getString(c.getColumnIndex("SID")));
                    obj.setPhoto(c.getString(c.getColumnIndex("Photo")));
                    obj.setBatch(c.getString(c.getColumnIndex("Batch_Info")));


                    Dataitems.add(obj);

                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();


        Count.setText(getString(R.string.str_total_count) + String.valueOf(i));

        return Dataitems;

    }


    //**********************************************************************************************


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    public class Student_Register_Adapter extends RecyclerView.Adapter<Student_Register_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.StudentItems> DataItems;
        private Context context;


        //**********************************************************************************************

        public Student_Register_Adapter(Context context, ArrayList<Getter_Setter.StudentItems> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Student_Register_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_register_rowitem_new, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Student_Register_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here

            viewHolder.Sno.setText((i + 1) + ".");

            viewHolder.Root_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((Activity) context).finishAffinity();
                    Intent back = new Intent(context, StudentTabActivity.class);
                    back.putExtra("SID", DataItems.get(i).getSID());
                    // Toast.makeText(context, "onBindViewHolder TABLE_ID:  " + DataItems.get(i).getSID(), Toast.LENGTH_SHORT).show();
                    context.startActivity(back);
                }
            });


            viewHolder.Name.setText(DataItems.get(i).getName());
            viewHolder.SID.setText(DataItems.get(i).getSID());
            viewHolder.BatchName.setText(DataItems.get(i).getBatch());

            if (DataItems.get(i).getPhoto().toString().length() > 0) {
                Glide.with(viewHolder.Photo.getContext()).load(new File(DataItems.get(i).getPhoto())).into(viewHolder.Photo);

            } else {
                Glide.with(context).load(Uri.parse("file:///android_asset/male_avatar.png")).into(viewHolder.Photo);

            }


        }
        //**********************************************************************************************

        @Override
        public int getItemCount() {
            return DataItems.size();
        }

        //**********************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView Sno;
            ImageView Options;
            ImageView Photo;
            TextView Name, SID, BatchName;
            LinearLayout Root_Layout;

            public ViewHolder(View view) {
                super(view);

                Sno = view.findViewById(R.id.rw_sno);
                Photo = view.findViewById(R.id.rw_icon);
                Name = view.findViewById(R.id.rw_name);
                SID = view.findViewById(R.id.rw_sid);
                BatchName = view.findViewById(R.id.rw_batch);
                Root_Layout = view.findViewById(R.id.parent_layout);

            }
        }

        //**********************************************************************************************

    }

    //End
}
