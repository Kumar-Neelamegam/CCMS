package entry_activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import core_modules.Task_Navigation;
import registers_activities.Students_Register;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

import static utilities.Baseconfig.GetDb;
import static utilities.Baseconfig.spinner2meth;

/**
 * Created by Kumar on 5/17/2017.
 */

public class Send_SMS extends AppCompatActivity  {



    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back,Exit;

    //variable declaration

    Spinner batch;
    EditText to,msg;
 FButton cancel,send;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);


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
        Back= findViewById(R.id.toolbar_back);
        Exit= findViewById(R.id.ic_exit);

        batch = findViewById(R.id.sms_batch);
        to = findViewById(R.id.edt_to);
        msg = findViewById(R.id.edt_msg);
        cancel = findViewById(R.id.btn_cancel);
        send = findViewById(R.id.btn_send);


        //Loading batch list
        LoadValuesSpinner(batch, Send_SMS.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");


    }


    public void LoadValuesSpinner(Spinner spinnertxt, Context cntxt, String Query, String lstadd) {


        try {
            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);
            List<String> list = new ArrayList<String>();

            list.add(lstadd);
            list.add("To All Batches");
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String counrtyname = c.getString(c.getColumnIndex("dvalue"));
                        list.add(counrtyname);

                    } while (c.moveToNext());
                }
            }

            spinner2meth(cntxt, list, spinnertxt);

            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //**********************************************************************************************

    private void Controllisteners() {
        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Send_SMS.this,Send_SMS.class));

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(batch.getSelectedItemPosition()>0)
                {

                    LoadStudentsList();
                }else
                {
                    MyDynamicToast.informationMessage(Send_SMS.this, "Select Batch..");

                }


            }
        });

        batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i>0)
                {
                    selectedColours1 = new ArrayList<CharSequence>();

                    to.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Clear();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckValidations()) {

                        SaveLocal();

                } else {

                    Baseconfig.SweetDialgos(4, Send_SMS.this, "Information", " Please fill all mandatory fields marked with (*)\n"+strError.toString(), "OK");
                }

            }
        });

    }


    //**************************************************************************************

    public void SaveLocal()
    {

        SQLiteDatabase db=Baseconfig.GetDb();
        String Str_Batch="",Str_To="",Str_Message="";

        Str_Batch =batch.getSelectedItem().toString();//*
        Str_To = to.getText().toString();
        Str_Message = msg.getText().toString();


        ContentValues values=new ContentValues();

        String[] StudentID=Str_To.toString().split(",");

        if(StudentID[0].toString().equalsIgnoreCase("To All"))//SMS To All
        {
            //get all batch sid
            Cursor c=db.rawQuery("select SID from Bind_EnrollStudents where Batch_Info='"+Str_Batch+"'",null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do{

                        values.put("Batch_Name", Str_Batch);
                        values.put("Batch_Id", Baseconfig.LoadValue("select Id as dstatus from Mstr_Batch where Batch_Name='"+Str_Batch+"' and IsActive='True'"));
                        values.put("SID",c.getString(c.getColumnIndex("SID")));
                        values.put("SMS",Str_Message);
                        values.put("SMS_For","");
                        values.put("IsSend",0);
                        values.put("IsActive",1);
                        values.put("IsSMS_Sent", 0);
                        values.put("IsUpdate",0);
                        values.put("ActDate",Baseconfig.GetDate());
                        db.insert("Bind_SMSEntry",null,values);

                        Log.e("Inserted Values 1: ", String.valueOf(values));

                    }while(c.moveToNext());
                }
            }


        }
        else//Selected SID
        {
            for(int i=0;i<StudentID.length;i++)
            {
                String SID=StudentID[i].toString().split("\\-")[1];

                values.put("Batch_Name", Str_Batch);
                values.put("Batch_Id", Baseconfig.LoadValue("select Id as dstatus from Mstr_Batch where Batch_Name='"+Str_Batch+"' and IsActive='True'"));
                values.put("SID",SID);
                values.put("SMS",Str_Message);
                values.put("SMS_For","");
                values.put("IsSend",0);
                values.put("IsActive",1);
                values.put("IsUpdate",0);
                values.put("IsSMS_Sent", 0);
                values.put("ActDate",Baseconfig.GetDate());
                db.insert("Bind_SMSEntry",null,values);

                Log.e("Inserted Values 2: ", String.valueOf(values));

            }
        }



        db.close();


        ShowSuccessDialog();


    }

    public void ShowSuccessDialog() {

        if (Baseconfig.CheckNW(Send_SMS.this)) {

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
                Baseconfig.SweetDialgos(3, Send_SMS.this, "Information", "Get username and password from SMS INDIA HUB to send sms..\nadd it in profile..", "OK");

            }

        } else {
            Baseconfig.SweetDialgos(3, Send_SMS.this, "Information", "No internet connectivity available..\nEnable data connection from settings..", "OK");
        }


        /*new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("SMS Send successfully..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                        Send_SMS.this.finish();
                        startActivity(new Intent(Send_SMS.this, Task_Navigation.class));
                    }
                })
                .show();

        Clear();
*/


    }
    //**********************************************************************************************

    public class SendSMS extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(Send_SMS.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.dialog.setMessage("Sending SMS...\nPlease wait..");
            this.dialog.setCancelable(false);
            this.dialog.show();

        }


        @Override
        protected Void doInBackground(Void... voids) {


            SQLiteDatabase db=Baseconfig.GetDb();
            String Query="select * from Bind_SMSEntry where IsSMS_Sent='0'";

            Cursor c=db.rawQuery(Query,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do{

                        String ID=c.getString(c.getColumnIndex("Id"));
                        String SID=c.getString(c.getColumnIndex("SID"));
                        String MessageContent=c.getString(c.getColumnIndex("SMS"));


                        String Message = "Dear parent, we hereby notify you that "+MessageContent+"";

                        String GetMobileNo=Baseconfig.LoadValue("select Mobile_Number as dstatus from Bind_EnrollStudents where SID='"+SID+"'");
                        String MobileNo = GetMobileNo;


                        SMSSender(Send_SMS.this, Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNo, Message, Baseconfig.SMS_SID, "0", "2",ID,SID);

                    }while(c.moveToNext());

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

            Clear();

            new SweetAlertDialog(Send_SMS.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(Send_SMS.this.getResources().getString(R.string.information))
                    .setContentText("SMS Sent Successfully..")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismiss();
                            Send_SMS.this.finish();
                            startActivity(new Intent(Send_SMS.this, Task_Navigation.class));
                        }
                    })
                    .show();

        }


    }
    //**********************************************************************************************
    @SuppressLint("LongLogTag")
    public static String SMSSender(Context ctx, String user, String password, String msisdn, String msg, String sid, String fl, String gwid, String TableId, String SID)
    {
        String rsp="";
        String retval="N/A";
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
        if(rsp.toString().contains("Success"))
        {

            SQLiteDatabase db = Baseconfig.GetDb();
            ContentValues values = new ContentValues();
            values.put("IsSMS_Sent", 1);
            db.update("Bind_SMSEntry", values, "SID='" + SID + "' and Id='"+TableId+"'", null);
            db.close();
            Log.e("Update Bind_SMSEntry SMS Status: ", values + " / " + "SID='" + SID + "'");

        }

        return rsp;
    }


    //********************************************************************************************

    //**************************************************************************************
    StringBuilder strError;
    public boolean CheckValidations()
    {

        boolean ret = true;

        strError=new StringBuilder();
        if (msg.getText().length()==0) {
            strError.append("Type Message*\n");
            ret = false;
        }

        if (to.getText().length()==0) {
            strError.append("Add To List*\n");
            ret = false;
        }

        if (batch.getSelectedItemPosition() == 0) {
            strError.append("Choose batch*\n");

            ret = false;
        }


        return ret;

    }

    //**********************************************************************************************

    void Clear()
    {
        batch.setSelection(0);
        to.setText("");
        msg.setText("");
    }

    //**********************************************************************************************

    /**
     * This becomes false when "Select All" is selected while deselecting some other item on list.
     */
    boolean selectAll = true;
    ArrayList<CharSequence> selectedColours1 = new ArrayList<CharSequence>();

    void LoadStudentsList() {

        CharSequence[] items1;


        SQLiteDatabase db = GetDb();

        String SelectedBatch=batch.getSelectedItem().toString();
        String Query="";
        if(SelectedBatch.toString().equalsIgnoreCase("To All Batches"))
        {
            Query="select distinct Name||'-'||SID as dvalue from Bind_EnrollStudents where IsActive='1'";
        }
        else
        {
            Query="select distinct Name||'-'||SID as dvalue from Bind_EnrollStudents where IsActive='1' and Batch_Info like '%"+SelectedBatch+",%'";
        }

        Cursor c = db.rawQuery(Query, null);
        List<String> list = new ArrayList<String>();

        list.add("To All");

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String counrtyname = c.getString(c.getColumnIndex("dvalue"));

                    list.add(counrtyname);

                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();

        new ArrayAdapter<String>(Send_SMS.this, android.R.layout.simple_dropdown_item_1line, list);
        items1 = list.toArray(new String[list.size()]);

        boolean[] checkedColours = new boolean[items1.length];
        int count = items1.length;

        for (int i = 0; i < count; i++) {
            checkedColours[i] = selectedColours1.contains(items1[i]);
        }

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
              if (isChecked) {
                    selectedColours1.add(items1[which]);

                } else {
                    selectedColours1.remove(items1[which]);
                }

            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(Send_SMS.this);

        builder.setTitle("Select Subject");//.setIcon(R.drawable.medical_history_icon);


        builder.setMultiChoiceItems(items1, checkedColours,
                coloursDialogListener);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();

                for (CharSequence colour : selectedColours1) {
                    stringBuilder.append(colour + ",");
                }

                to.setText(stringBuilder.toString());

            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new StringBuilder();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation3;
        dialog.show();




        // we get the ListView from already shown dialog
        final ListView listView = dialog.getListView();
        // ListView Item Click Listener that enables "Select all" choice
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isChecked = listView.isItemChecked(position);


                if (position == 0) {
                    if(selectAll) {
                        for (int i = 1; i < items1.length; i++)
                        { // we start with first element after "Select all" choice
                            if (isChecked && !listView.isItemChecked(i) || !isChecked && listView.isItemChecked(i)) {

                                listView.performItemClick(listView, i, 0);

                               // selectedColours1.add(items1[i].toString());

                              /*  if (isChecked) {
                                    selectedColours1.add(items1[i]);

                                } else {
                                    selectedColours1.remove(items1[i]);
                                }*/

                            }
                        }
                    }
                } else {



                    if (isChecked) {

                        if(!selectedColours1.contains(items1[position]))
                        {
                            selectedColours1.add(items1[position]);

                        }

                    } else {
                        selectedColours1.remove(items1[position]);
                    }

                    if (!isChecked && listView.isItemChecked(0)) {
                        // if other item is unselected while "Select all" is selected, unselect "Select all"
                        // false, performItemClick, true is a must in order for this code to work
                        selectAll = false;
                        listView.performItemClick(listView, 0, 0);

                        selectAll = true;
                    }
                }
            }
        });





    }



    //**********************************************************************************************

    public void LoadBack()
    {
        this.finishAffinity();
        Intent back=new Intent(Send_SMS.this, Task_Navigation.class);
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

