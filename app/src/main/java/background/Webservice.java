package background;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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

    public static void startWebservice() {

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                for (int i = 0; i < tableNames.length; i++) {


                    Export_FirebaseServer_Multi(tableNames[i]);

                   //Import_FirebaseServer(tableNames[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.gc();
        }, 0, 10, TimeUnit.SECONDS);

    }



    public void insert_EnrollStudent() {

        Cursor cursor = Baseconfig.GetDb().rawQuery("Select * from Bind_EnrollStudents where IsUpdate='0'", null);

        List<Map<String, Object>> Hashvalue = new ArrayList<>();

        if (cursor!=null) {

            if (cursor.moveToFirst()) {

                do {

                    for (String s : cursor.getColumnNames()) {
                        Hashvalue.get(cursor.getPosition()).put(s, cursor.getString(cursor.getColumnIndex(s)));
                    }
                    Hashvalue.get(cursor.getPosition()).put("TimeStamp", Timestamp.now());

                }while (cursor.moveToNext());

            }
        }

        FirebaseFirestore.getInstance().collection("Bind_EnrollStudents").document().set(Hashvalue);

    }

    public static void Export_FirebaseServer_Multi(String tableName)
    {
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
                    Hasvalue.put("TimeStamp", Timestamp.now());

                    FirebaseFirestore.getInstance().collection(tableName).document().set(Hasvalue);

                    Baseconfig.GetDb().execSQL("UPDATE " + tableName + " SET  ServerIsUpdate='1' WHERE Id='" + LocalId + "';");


                }while (cursor.moveToNext());

            }
        }

        Log.e("Export Completed","**********************************************");


    }



    public static void Export_FirebaseServer(String tableName) {

        String query = "Select * from " + tableName + " where ServerIsUpdate='0' limit 1";
        String LocalId;
        Cursor cursor = Baseconfig.GetDb().rawQuery(query, null);
        Map<String, Object> Hashvalue = new HashMap<>();
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            try {
                LocalId = cursor.getString(cursor.getColumnIndex("Id"));
                for (String s : cursor.getColumnNames()) {
                    Hashvalue.put(s, cursor.getString(cursor.getColumnIndex(s)));
                }
                Hashvalue.put("TimeStamp", Timestamp.now());

                FirebaseFirestore.getInstance().collection(tableName).document().set(Hashvalue);


                Baseconfig.GetDb().execSQL("UPDATE " + tableName + " SET  ServerIsUpdate='1' WHERE Id='" + LocalId + "';");
            } catch (Exception e) {

                Log.e(tableName, "Error Query=" + query);
            }

        }


    }

    public static void Import_FirebaseServer(String tableName) {



        FirebaseFirestore.getInstance()
                .collection(tableName)
                .whereEqualTo("FUID", Baseconfig.App_UID)
             //   .whereLessThan("TimeStamp", Timestamp.now())
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
                           // Baseconfig.GetDb().insert(tableName,null,contentValues);


                            Log.e("User Id", uid);
                        }
                    }
                });



    }

}




