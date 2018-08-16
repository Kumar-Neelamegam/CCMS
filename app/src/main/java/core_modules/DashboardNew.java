package core_modules;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adapters.DashboardAdapter;
import adapters.Getter_Setter;
import utilities.Baseconfig;
import vcc.coremodule.R;


/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */

public class DashboardNew extends Fragment {

    private static String SESSION_DATABASE = "";


    //*********************************************************************************************

    public GridLayoutManager lLayout;


    public final String title_name[] = {
            "Enroll Students",
            "Take Attendance",
            "Mark List Entry",
            "Students Profile",
            "Attendance Register",
            "Mark Register",
            "Masters",
            "Reports",
            "Send SMS"
    };

/*    public final int image_drawables[] = {
            R.drawable.ic_dashboard_enroll_student,
            R.drawable.ic_dashboard_attendance,
            R.drawable.ic_dashboard_mark_entry,
            R.drawable.ic_dashboard_student_profile,
            R.drawable.ic_dashboard_attendance_register,
            R.drawable.ic_dashboard_marks_register,
            R.drawable.ic_dashboard_master,
            R.drawable.ic_dashboard_reports,
            R.drawable.ic_dashboard_sms,
    };*/


    public final int image_drawables[] = {
            R.drawable.ic_fab_dashboard_1,
            R.drawable.ic_fab_dashboard_2,
            R.drawable.ic_fab_dashboard_3,
            R.drawable.ic_fab_dashboard_4,
            R.drawable.ic_fab_dashboard_5,
            R.drawable.ic_fab_dashboard_6,
            R.drawable.ic_fab_dashboard_7,
            R.drawable.ic_fab_dashboard_8,
            R.drawable.ic_fab_dashboard_9
    };

    public final int id[] = {
            0, 1, 2, 3, 4, 5, 6, 7, 8
    };

    CardView Student_Info, Student_Due, Student_Collection, Student_Attendance, Student_Admission, Student_SMS;

    TextView TotalCount, Male, Female, Total_Collection, Txt_BillCount;

    String MAC_ADDRESS = "", UUID = "";

    //*********************************************************************************************

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dashboardnew, container, false);

        try {



            MAC_ADDRESS = Baseconfig.GetMacAddress(getActivity());
            UUID = Baseconfig.GetUUIDAddress(getActivity());


            try {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                firestore.setFirestoreSettings(settings);
            } catch (Exception e) {
                e.printStackTrace();
            }


            GetInitialize(v);

            Controllisterners(v);

            SendSMSNotification();

            //  addNewContact();

            //   ReadSingleContact();

            // addPlans();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return v;
    }


    /**
     * Create cities collection and add sample documents.
     */
    private void addNewContact() {

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> newContact = new HashMap<>();
            newContact.put("Name", "John");
            newContact.put("Email", "john@gmail.com");
            newContact.put("PhoneNO", "080-0808-009");

            db.collection("Purchases").document("ponnar@gmail.com").set(newContact)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "User Registered", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addPlans() {

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> newContact = new HashMap<>();

            newContact = new HashMap<>();
            newContact.put("cardcolor", "#fed330");
            newContact.put("payid", "");
            newContact.put("perstudent", "10.Rs");
            newContact.put("plantitle", "BASIC");
            newContact.put("totalprice", "3250");
            newContact.put("validupto", "350 students");

            db.collection(Baseconfig.FIREBASE_PLANS).document().set(newContact)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Plans added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.toString());
                        }
                    });


            newContact = new HashMap<>();
            newContact.put("cardcolor", "#fd9644");
            newContact.put("payid", "");
            newContact.put("perstudent", "8.Rs");
            newContact.put("plantitle", "PRO");
            newContact.put("totalprice", "5600");
            newContact.put("validupto", "700 students");

            db.collection(Baseconfig.FIREBASE_PLANS).document().set(newContact)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Plans added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.toString());
                        }
                    });


            newContact = new HashMap<>();
            newContact.put("cardcolor", "#26de81");
            newContact.put("payid", "");
            newContact.put("perstudent", "N/A");
            newContact.put("plantitle", "ULTIMATE");
            newContact.put("totalprice", "10600");
            newContact.put("validupto", "Unlimited students");

            db.collection(Baseconfig.FIREBASE_PLANS).document().set(newContact)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Plans added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.toString());
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //*********************************************************************************************

    public void SendSMSNotification() {
        try {
            if (Baseconfig.CheckNW(getActivity())) {

                    new SendSMS2().execute();//Paid fee sms
                    Log.e("SendSMSNotification", "SendSMSNotification");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //**********************************************************************************************

    /**
     * Sending sms to paid fee
     */
    public class SendSMS2 extends AsyncTask<Void, Void, Void> {
        int Status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {


            SQLiteDatabase db = Baseconfig.GetDb();


            /*********************************************************************************************
             * *******************************************************************************************
             * 1. Sending sms to parent of paid fee amount
             */
            String Query = "select  b.Id,a.Mobile_Number,a.Coaching_Fee,a.SID,a.Name,a.Gender,b.Paid_Fee,b.Paid_Date \n" +
                    "from Bind_EnrollStudents a inner join Bind_FeeEntry b on a.SID=b.SID where b.IsSMS_Sent='0'  " +
                    "order by b.Id desc\n";

            Cursor c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));

                        String Name = c.getString(c.getColumnIndex("Name"));
                        String Gender = c.getString(c.getColumnIndex("Gender"));
                        String Paid_Fee = "." + c.getString(c.getColumnIndex("Paid_Fee"));
                        String Paid_Date = c.getString(c.getColumnIndex("Paid_Date"));


                        if (Gender.toString().equalsIgnoreCase("Male")) {
                            Gender = "son";

                        } else if (Gender.toString().equalsIgnoreCase("Female")) {
                            Gender = "daughter";
                        }
                        String Balance = "Rs." + GetBalanceFromSID(SID);

                        String Message = "Dear parent, We have received a payment of Rs " + Paid_Fee + " " +
                                "from your " + Gender + " " + Name + " as coaching fees on " + Paid_Date + ". " +
                                "Balance payable fee is " + Balance + ". Thank you.";

                        Log.e("Fee Paid Message: ", Message.toString());
                        Log.e("Fee Paid Message: ", Message.toString());

                        String MobileNo = c.getString(c.getColumnIndex("Mobile_Number"));

                        SMSSender(getActivity(), Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNo, Message, Baseconfig.SMS_SID, "0", "2", ID, SID, 0);
                        //0 = Fee Entry table update

                    } while (c.moveToNext());

                }

            }


            /**********************************************************************************************
             * ********************************************************************************************
             * 2. Sending Student Registration SMS
             */
            c = null;
            Query = "select Id,SID,Gender,Name,Mobile_Number,Subject,Batch_Info,Coaching_Fee,IFNULL(Fee_Advance,'0')as Fee_Advance from Bind_EnrollStudents \n" +
                    "where IsSMS_Sent='0'";

            c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));
                        String Name = c.getString(c.getColumnIndex("Name"));
                        String Gender = c.getString(c.getColumnIndex("Gender"));
                        String Mobile_Number = c.getString(c.getColumnIndex("Mobile_Number"));
                        String Subject = c.getString(c.getColumnIndex("Subject")).toString();
                        String Batch_Info = c.getString(c.getColumnIndex("Batch_Info"));
                        String Coaching_Fee = c.getString(c.getColumnIndex("Coaching_Fee"));

                        String Fee_Advance = "0";

                        Fee_Advance = c.getString(c.getColumnIndex("Fee_Advance"));

                        if (Gender.toString().equalsIgnoreCase("Male")) {
                            Gender = "son";

                        } else if (Gender.toString().equalsIgnoreCase("Female")) {
                            Gender = "daughter";
                        }

                        if (Fee_Advance != null && !Fee_Advance.toString().equalsIgnoreCase("0") && Fee_Advance.length() != 0) {
                            Fee_Advance = "Rs." + Fee_Advance + ".";
                        } else {
                            Fee_Advance = "Rs.0.";
                        }

                        String SubjectInfo = GetSubjectInfo(Batch_Info).toString();

                        String Message = "Dear parent, Your " + Gender + " " + Name + " has enrolled with us for " +
                                "" + Subject + " coaching. Total payable fee for coaching is Rs " + Coaching_Fee + " " +
                                "and the class timings will be as follows. " + SubjectInfo + " Fee Received - " + Fee_Advance + " Kindly ensure " +
                                "that your " + Gender + " don't skip classes often and you will get regular " +
                                "notifications regarding your " + Gender + " Attendance and Test Marks details. " +
                                "Thank you. Regards, Vetri Coaching Center, KK Nagar";

                        Log.e("Enroll Message: ", Message.toString());
                        Log.e("Enroll Message: ", Message.toString());

                        SMSSender(getActivity(), Baseconfig.SMS_Username, Baseconfig.SMS_Password, Mobile_Number, Message, Baseconfig.SMS_SID, "0", "2", ID, SID, 1);
                        //1 = Bind_EnrollStudents update

                    } while (c.moveToNext());

                }

            }


            /**********************************************************************************************
             * ********************************************************************************************
             * 3. Sending Student SMS
             */
            Query = "select * from Bind_SMSEntry where IsSMS_Sent='0'";

            c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String ID = c.getString(c.getColumnIndex("Id"));
                        String SID = c.getString(c.getColumnIndex("SID"));
                        String MessageContent = c.getString(c.getColumnIndex("SMS"));

                        String GetMobileNo = Baseconfig.LoadValue("select Mobile_Number as dstatus from Bind_EnrollStudents where SID='" + SID + "'");
                        String MobileNo = GetMobileNo;

                        String Message = "Dear parent, we hereby notify you that " + MessageContent + "";
                        Log.e("Bind_SMSEntry Message: ", Message.toString());
                        Log.e("Bind_SMSEntry Message: ", Message.toString());


                        SMSSender(getActivity(), Baseconfig.SMS_Username, Baseconfig.SMS_Password, MobileNo, Message, Baseconfig.SMS_SID, "0", "2", ID, SID, 2);

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


        }


    }
    //**********************************************************************************************

    public StringBuilder GetSubjectInfo(String Batch_Info) {

        StringBuilder str = new StringBuilder();


        String[] batches = Batch_Info.toString().split(",");

        for (int i = 0; i < batches.length; i++) {
            String str_batch = batches[i].toString();

            String str_total = GetBatchInfo(str_batch);

            str.append(str_total + "\n");

        }

        return str;
    }

    public String GetBatchInfo(String str) {
        String batch_info = "";
        SQLiteDatabase db = Baseconfig.GetDb();
        Cursor c = db.rawQuery("select Subject_Name||' - Every '|| Coaching_Days ||'. From '|| Class_From ||' till '||  Class_To as info from Mstr_Batch where Batch_Name='" + str + "';", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    batch_info = c.getString(c.getColumnIndex("info"));

                } while (c.moveToNext());
            }
        }
        db.close();
        return batch_info;
    }

    //**********************************************************************************************

    @SuppressLint("LongLogTag")
    public static String SMSSender(Context ctx, String user, String password, String msisdn, String msg, String sid, String fl, String gwid, String TableId, String SID, int InsertId) {


        String rsp = "";
        String retval = "N/A";

        String SMSOption_Query = "select SMSOption as dstatus from Bind_InstituteInfo";
        int SMSOption = Integer.parseInt(Baseconfig.LoadValue(SMSOption_Query));

        if (SMSOption == 1)//MOBILE SMS
        {

            Baseconfig.sendSMS(msisdn, msg, ctx);
            rsp = "Success";

        } else//SMS GATEWAY
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


        Log.e("SMSSender Response: ", rsp);
        if (rsp.toString().contains("Success")) {

            if (InsertId == 0)//To update Isms_sent in fee entry
            {
                SQLiteDatabase db = Baseconfig.GetDb();
                ContentValues values = new ContentValues();
                values.put("IsSMS_Sent", 1);
                db.update("Bind_FeeEntry", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
                db.close();
                Log.e("Update Bind_FeeEntry SMS Status: ", values + " / " + "SID='" + SID + "'");

            } else if (InsertId == 1)//TO update Isms_sent in enrollment entry
            {
                SQLiteDatabase db = Baseconfig.GetDb();
                ContentValues values = new ContentValues();
                values.put("IsSMS_Sent", 1);
                db.update("Bind_EnrollStudents", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
                db.close();
                Log.e("Update Bind_EnrollStudents SMS Status: ", values + " / " + "SID='" + SID + "'");

            } else if (InsertId == 2)//TO update Isms_sent in enrollment entry
            {
                SQLiteDatabase db = Baseconfig.GetDb();
                ContentValues values = new ContentValues();
                values.put("IsSMS_Sent", 1);
                db.update("Bind_SMSEntry", values, "SID='" + SID + "' and Id='" + TableId + "'", null);
                db.close();
                Log.e("Update Bind_SMSEntry SMS Status: ", values + " / " + "SID='" + SID + "'");

            }

        }

        return rsp;
    }
    //*********************************************************************************************

    public String GetBalanceFromSID(String SID) {

        String Balance = "0";
        SQLiteDatabase db = Baseconfig.GetDb();
        Cursor c = db.rawQuery("select   \n" +
                "IFNULL(Coaching_Fee-(SUM(Paid_Fee)),'0')as Balance\n" +
                "from Bind_EnrollStudents a inner join Bind_FeeEntry b on a.SID=b.SID where a.SID='" + SID + "'", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Balance = c.getString(c.getColumnIndex("Balance"));

                } while (c.moveToNext());

            }
        }

        c.close();
        db.close();


        return Balance;

    }


    //*********************************************************************************************

    RecyclerView recyclerView;

    public void GetInitialize(View v) {


        /**
         * To load recycler view old dashboard
         */
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);


        String Query = "select IsPaid as dstatus1 from Bind_InstituteInfo where IsPaid=1 and UID='" + Baseconfig.App_UID + "'";
        boolean getPaidStatus = Baseconfig.LoadReportsBooleanStatus(Query);
        if (getPaidStatus)//IF PAID NA GO HEAD
        {
           /* //get total count and check for expiry
            int getStudentsCount = Integer.parseInt(Baseconfig.LoadValueInt("select StudentCount as dstatus from Bind_InstituteInfo where IsPaid=1 and UID='" + Baseconfig.App_UID + "'"));
            int getCurrentPlanCount = Integer.parseInt(Baseconfig.LoadValueInt("select count(Id) as dstatus from Bind_EnrollStudents"));
            if (getCurrentPlanCount == getStudentsCount) {

            } else//inform user about expiry
            {

            }*/
            ArrayList<Getter_Setter.Dashboard_Dataobjects> DataItems = prepareData();
            DashboardAdapter adapter = new DashboardAdapter(getActivity(), DataItems, SESSION_DATABASE, true);
            recyclerView.setAdapter(adapter);

        } else//TRAIL USERS
        {

            ArrayList<Getter_Setter.Dashboard_Dataobjects> DataItems = prepareData();
            DashboardAdapter adapter = new DashboardAdapter(getActivity(), DataItems, SESSION_DATABASE, false);
            recyclerView.setAdapter(adapter);
        }


    }//END

    //*********************************************************************************************

    private ArrayList<Getter_Setter.Dashboard_Dataobjects> prepareData() {

        ArrayList<Getter_Setter.Dashboard_Dataobjects> Dataitems = new ArrayList<>();
        for (int i = 0; i < title_name.length; i++) {
            Getter_Setter.Dashboard_Dataobjects obj = new Getter_Setter.Dashboard_Dataobjects();
            obj.setTitle_Name(title_name[i]);
            obj.setIcon(image_drawables[i]);
            obj.setId(id[i]);
            Dataitems.add(obj);
        }
        return Dataitems;

    }

    //*********************************************************************************************

    public void Controllisterners(View v) {


    }
    //*********************************************************************************************

}