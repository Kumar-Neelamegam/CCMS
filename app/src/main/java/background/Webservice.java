package background;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import utilities.Baseconfig;

public class Webservice {

    public static String[] tableNames = new String[]{"Bind_EnrollStudents",
            "Bind_FeeEntry",
            "Bind_MarkEntry",
            "Bind_SMSEntry",
            "Mstr_Batch",
            "Mstr_Fee",
            "Mstr_Occupation",
            "Mstr_School",
            "Mstr_Subject",
            "Mstr_Test",
            "Temp_Attendance",
            "Temp_MarkEntry",
            "Bind_Attendance"};

    static ScheduledExecutorService scheduledExecutorService;

    public static void startWebservice() {

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                for (int i = 0; i < tableNames.length; i++) {


                    Export_FirebaseServer(tableNames[i]);

                   Import_FirebaseServer(tableNames[i]);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.gc();
        }, 0, 10, TimeUnit.SECONDS);

    }



    public static void stopWebservice() {

        if(scheduledExecutorService!=null)
        {
            scheduledExecutorService.shutdown();
        }

    }





        public static void Export_FirebaseServer(String tableName)
    {
        try {
            Log.e("Export Started","**********************************************");

            String query = "Select * from " + tableName + " where ServerIsUpdate='0'";
            String LocalId;
            @SuppressLint("Recycle") Cursor cursor = Baseconfig.GetDb().rawQuery(query, null);

            if (cursor!=null) {

                if (cursor.moveToFirst()) {

                    do {
                        LocalId = cursor.getString(cursor.getColumnIndex("Id"));
                        Map<String,Object> Hasvalue=new HashMap<>();
                        for (String s : cursor.getColumnNames()) {
                            Hasvalue.put(s, cursor.getString(cursor.getColumnIndex(s)));
                        }
                        Date date=Baseconfig.getFirebaseServerDate();
                        Hasvalue.put("TimeStamp", date);

                        FirebaseFirestore.getInstance().collection(tableName).document().set(Hasvalue);

                        Baseconfig.GetDb().execSQL("UPDATE " + tableName + " SET  ServerIsUpdate='1' WHERE Id='" + LocalId + "';");

                    }while (cursor.moveToNext());

                }
            }

            Log.e("Export Completed","**********************************************");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }





    public static void Import_FirebaseServer(String tableName) {

        try {

            String MaxValue = Baseconfig.LoadValue("select IFNULL(max(Id),0) as dstatus from "+tableName);

            FirebaseFirestore.getInstance()
                    .collection(tableName)
                    .whereEqualTo("FUID", Baseconfig.App_UID)
                    .whereGreaterThan("Id", MaxValue)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {

                                Map<String, Object> values = documentChange.getDocument().getData();

                                String uid = (String) values.get("FUID");

                                //ContentValue Preparing
                                ContentValues contentValues=new ContentValues();
                                for (String s : values.keySet()) {
                                    contentValues.put(s,values.get(s).toString());
                                }

                                //Insert Table Value
                                Baseconfig.GetDb().insert(tableName,null,contentValues);
                                Log.e("User Id", uid);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}//END




