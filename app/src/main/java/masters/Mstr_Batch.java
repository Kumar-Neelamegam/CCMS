package masters;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.Calendar;

import adapters.Getter_Setter;

import utilities.Baseconfig;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/20/2017.
 */

public class Mstr_Batch extends AppCompatActivity {


    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    Spinner Spn_subject;
    EditText Edt_BatchName, Edt_year, Edt_From_Time, Edt_To_Time;
    CheckBox Chkbx_Mon, Chkbx_Tue, Chkbx_Wed, Chkbx_Thur, Chkbx_Fri, Chkbx_Sat, Chkbx_Sun, Chkbx_Active;

    Button Btn_Clear, Btn_Submit;


    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_batch);

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

        Btn_Submit = findViewById(R.id.btn_submit);
        Btn_Clear = findViewById(R.id.btn_cancel);


        Spn_subject = findViewById(R.id.spn_choosesubject);

        Edt_BatchName = findViewById(R.id.edt_batchname);
        Edt_year = findViewById(R.id.edt_year);
        Edt_From_Time = findViewById(R.id.edt_fromtime);
        Edt_To_Time = findViewById(R.id.edt_totime);

        Chkbx_Mon = findViewById(R.id.chk_mon);
        Chkbx_Tue = findViewById(R.id.chk_tue);
        Chkbx_Wed = findViewById(R.id.chk_wed);
        Chkbx_Thur = findViewById(R.id.chk_thur);
        Chkbx_Fri = findViewById(R.id.chk_fri);
        Chkbx_Sat = findViewById(R.id.chk_sat);
        Chkbx_Sun = findViewById(R.id.chk_sun);
        Chkbx_Active = findViewById(R.id.chk_active);

        Baseconfig.LoadValuesSpinner(Spn_subject, Mstr_Batch.this, "select distinct Subject_Name as dvalue from Mstr_Subject where IsActive='1';", "Select");

        LoadRecyler();


    }

    //**********************************************************************************************
    private int pHour;
    private int pMinute;
    /**
     * This integer will uniquely define the dialog to be used for displaying time picker.
     */
    static final int TIME_DIALOG_ID1 = 1;
    static final int TIME_DIALOG_ID2 = 2;

    /**
     * Callback received when the user "picks" a time in the dialog
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener1 =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    pHour = hourOfDay;
                    pMinute = minute;
                    updateDisplay(1);
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener2 =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    pHour = hourOfDay;
                    pMinute = minute;
                    updateDisplay(2);
                }
            };

    /**
     * Updates the time in the TextView
     */
    private void updateDisplay(int id) {
        if (id == 1) {
            Edt_From_Time.setText(new StringBuilder().append(pad(pHour)).append(":").append(pad(pMinute)));
        } else {
            Edt_To_Time.setText(new StringBuilder().append(pad(pHour)).append(":").append(pad(pMinute)));
        }

    }


    /**
     * Add padding to numbers less than ten
     */
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    /**
     * Create a new dialog for time picker
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID1:
                return new TimePickerDialog(this,
                        mTimeSetListener1, pHour, pMinute, false);

            case TIME_DIALOG_ID2:
                return new TimePickerDialog(this,
                        mTimeSetListener2, pHour, pMinute, false);
        }
        return null;
    }
    //**********************************************************************************************

    private void Controllisteners() {

        /** Get the current time */
        final Calendar cal = Calendar.getInstance();
        pHour = cal.get(Calendar.HOUR_OF_DAY);
        pMinute = cal.get(Calendar.MINUTE);


        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, Mstr_Batch.class));

        Btn_Submit.setOnClickListener(view -> {

            if (CheckValidations()) {

                if (Btn_Submit.getText().toString().equalsIgnoreCase("Submit"))//Submit
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Batch where Batch_Name='" + Edt_BatchName.getText().toString() + "' and IsActive='True'");
                    if (!b) {
                        SaveLocal();
                    } else {

                        Baseconfig.SweetDialgos(4, Mstr_Batch.this, "Information", " Duplicate entry.. already batch name exists in list", "OK");
                        Clear();
                    }
                } else if (Btn_Submit.getText().toString().equalsIgnoreCase("Update"))//Update
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Batch where Batch_Name='" + Edt_BatchName.getText().toString() + "' and IsActive='True'");
                    if (!b) {
                        //condition venum chk paniko
                        UpdateLocal();
                    } else {

                        //Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");

                        ShowDuplicateAlert();

                    }
                }


            } else {

                Baseconfig.SweetDialgos(4, Mstr_Batch.this, "Information", " Please fill all mandatory fields marked with (*) ", "OK");
            }


        });

        Edt_From_Time.setOnClickListener(view -> {

            showDialog(TIME_DIALOG_ID1);


        });


        Edt_To_Time.setOnClickListener(view -> {

            showDialog(TIME_DIALOG_ID2);


        });

        Btn_Clear.setOnClickListener(view -> Clear());


    }


    //**************************************************************************************
    void ShowDuplicateAlert() {
        new SweetAlertDialog(Mstr_Batch.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Mstr_Batch.this.getResources().getString(R.string.information))
                .setContentText(" Duplicate entry.. already batch name exists in list..\nDo you want to update again?")
                .setCancelText(Mstr_Batch.this.getResources().getString(R.string.no))
                .setConfirmText(Mstr_Batch.this.getResources().getString(R.string.yes))
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
                        UpdateLocal();

                    }
                })
                .show();
    }

    //**************************************************************************************
    public boolean CheckValidations() {
        boolean ret = true;

        if (Spn_subject.getSelectedItemPosition() == 0) {
            ret = false;
        }

        if (Edt_BatchName.getText().toString().length() == 0) {
            ret = false;
        }

        if (Edt_year.getText().toString().length() == 0) {
            ret = false;
        }

        if ((Edt_From_Time.getText().toString().length() == 0) && (Edt_To_Time.getText().toString().length() == 0)) {
            ret = false;
        }

        if (Chkbx_Mon.isChecked() == false && Chkbx_Tue.isChecked() == false && Chkbx_Wed.isChecked() == false &&
                Chkbx_Thur.isChecked() == false && Chkbx_Fri.isChecked() == false && Chkbx_Sat.isChecked() == false &&
                Chkbx_Sun.isChecked() == false) {
            ret = false;
        }


        return ret;
    }


    //**************************************************************************************
    public void SaveLocal() {

        String Str_SubName = "", Str_SubId = "", Str_BatchName = "", Str_Year = "", Str_Days = "", Str_ClassFrom = "", Str_ClassTo = "", Str_Isactive = "1";

        String Str_Mon = "", Str_Tue = "", Str_Wed = "", Str_Thu = "", Str_Fri = "", Str_Sat = "", Str_Sun = "";


        boolean b = Baseconfig.CheckSpinner(Spn_subject);
        if (b) {
            Str_SubName = Spn_subject.getSelectedItem().toString();
        } else {
            Str_SubName = "N/A";
        }


        Str_SubId = Baseconfig.LoadValue("select Id as dstatus from Mstr_Subject where Subject_Name='" + Str_SubName + "'");

        Str_BatchName = Edt_BatchName.getText().toString();
        Str_Year = Edt_year.getText().toString();

        Str_ClassFrom = Edt_From_Time.getText().toString();
        Str_ClassTo = Edt_To_Time.getText().toString();

        if (Baseconfig.CheckBox(Chkbx_Active)) {
            Str_Isactive = "True";
        } else {
            Str_Isactive = "False";
        }

        Str_Days = GetCheckAllStatus();

        if (Chkbx_Mon.isChecked()) {
            Str_Mon = "Mon";
        }
        if (Chkbx_Tue.isChecked()) {
            Str_Tue = "Tue";
        }
        if (Chkbx_Wed.isChecked()) {
            Str_Wed = "Wed";
        }
        if (Chkbx_Thur.isChecked()) {
            Str_Thu = "Thu";
        }
        if (Chkbx_Fri.isChecked()) {
            Str_Fri = "Fri";
        }
        if (Chkbx_Sat.isChecked()) {
            Str_Sat = "Sat";
        }
        if (Chkbx_Sun.isChecked()) {
            Str_Sun = "Sun";
        }

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Subject_Name", Str_SubName);
        values.put("Subject_Id", Str_SubId);
        values.put("Batch_Name", Str_BatchName);
        values.put("YearOfExam", Str_Year);
        values.put("Coaching_Days", Str_Days);
        values.put("Class_From", Str_ClassFrom);
        values.put("Class_To", Str_ClassTo);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "0");
        values.put("ActDate", Baseconfig.GetDate());
        values.put("Monday", Str_Mon);
        values.put("Tuesday", Str_Tue);
        values.put("Wednesday", Str_Wed);
        values.put("Thursday", Str_Thu);
        values.put("Friday", Str_Fri);
        values.put("Sat", Str_Sat);
        values.put("Sun", Str_Sun);
        db.insert("Mstr_Batch", null, values);
        db.close();

        ShowSuccessDialog("Data added to masters successfully..");

    }


    public void UpdateLocal() {

        String Str_SubName = "", Str_SubId = "", Str_BatchName = "", Str_Year = "", Str_Days = "", Str_ClassFrom = "", Str_ClassTo = "", Str_Isactive = "1";

        String Str_Mon = "", Str_Tue = "", Str_Wed = "", Str_Thu = "", Str_Fri = "", Str_Sat = "", Str_Sun = "";


        boolean b = Baseconfig.CheckSpinner(Spn_subject);
        if (b) {
            Str_SubName = Spn_subject.getSelectedItem().toString();
        } else {
            Str_SubName = "N/A";
        }


        Str_SubId = Baseconfig.LoadValue("select Id as dstatus from Mstr_Subject where Subject_Name='" + Str_SubName + "'");

        Str_BatchName = Edt_BatchName.getText().toString();
        Str_Year = Edt_year.getText().toString();

        Str_ClassFrom = Edt_From_Time.getText().toString();
        Str_ClassTo = Edt_To_Time.getText().toString();

        if (Baseconfig.CheckBox(Chkbx_Active)) {
            Str_Isactive = "True";
        } else {
            Str_Isactive = "False";
        }

        Str_Days = GetCheckAllStatus();

        if (Chkbx_Mon.isChecked()) {
            Str_Mon = "Mon";
        }
        if (Chkbx_Tue.isChecked()) {
            Str_Tue = "Tue";
        }
        if (Chkbx_Wed.isChecked()) {
            Str_Wed = "Wed";
        }
        if (Chkbx_Thur.isChecked()) {
            Str_Thu = "Thu";
        }
        if (Chkbx_Fri.isChecked()) {
            Str_Fri = "Fri";
        }
        if (Chkbx_Sat.isChecked()) {
            Str_Sat = "Sat";
        }
        if (Chkbx_Sun.isChecked()) {
            Str_Sun = "Sun";
        }

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Subject_Name", Str_SubName);
        values.put("Subject_Id", Str_SubId);
        values.put("Batch_Name", Str_BatchName);
        values.put("YearOfExam", Str_Year);
        values.put("Coaching_Days", Str_Days);
        values.put("Class_From", Str_ClassFrom);
        values.put("Class_To", Str_ClassTo);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "0");
        values.put("ActDate", Baseconfig.GetDate());
        values.put("Monday", Str_Mon);
        values.put("Tuesday", Str_Tue);
        values.put("Wednesday", Str_Wed);
        values.put("Thursday", Str_Thu);
        values.put("Friday", Str_Fri);
        values.put("Sat", Str_Sat);
        values.put("Sun", Str_Sun);
        db.update("Mstr_Batch", values, "Id='" + LOCAL_UPDATE_ID + "'", null);

        //Bind_EnrollStudents
        db.execSQL("Update Bind_EnrollStudents set Batch_Info=REPLACE(Batch_Info,'" + OLD_BATCH + "','" + Str_BatchName + "') where Batch_Info like '%" + OLD_BATCH + ",%'");

        //Bind_Attendance
        db.execSQL("Update Bind_Attendance set Batch_Name=REPLACE(Batch_Name,'" + OLD_BATCH + "','" + Str_BatchName + "') where Batch_Name like '%" + OLD_BATCH + ",%'");

        //Bind_MarkEntry
        db.execSQL("Update Bind_MarkEntry set Batch_Name=REPLACE(Batch_Name,'" + OLD_BATCH + "','" + Str_BatchName + "') where Batch_Name like '%" + OLD_BATCH + ",%'");

        //Bind_SMSEntry
        db.execSQL("Update Bind_SMSEntry set Batch_Name=REPLACE(Batch_Name,'" + OLD_BATCH + "','" + Str_BatchName + "') where Batch_Name like '%" + OLD_BATCH + ",%'");

        db.close();

        ShowSuccessDialog("Data updated to masters successfully..");


    }
    //**************************************************************************************

    public String GetCheckAllStatus() {
        StringBuilder str = new StringBuilder();
        if (Chkbx_Mon.isChecked()) {
            str.append("Mon,");
        }
        if (Chkbx_Tue.isChecked()) {
            str.append("Tue,");
        }
        if (Chkbx_Wed.isChecked()) {
            str.append("Wed,");
        }
        if (Chkbx_Thur.isChecked()) {
            str.append("Thu,");
        }
        if (Chkbx_Fri.isChecked()) {
            str.append("Fri,");
        }
        if (Chkbx_Sat.isChecked()) {
            str.append("Sat,");
        }
        if (Chkbx_Sun.isChecked()) {
            str.append("Sun,");
        }
        return str.toString();
    }

    //**************************************************************************************

    public void ShowSuccessDialog(String Msg) {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText(Msg)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener((sweetAlertDialog) -> sweetAlertDialog.dismiss())
                .show();

        Clear();

        LoadRecyler();
    }

    //**************************************************************************************

    public void Clear() {

        Spn_subject.setSelection(0);

        Edt_BatchName.setText("");
        Edt_year.setText("");
        Edt_From_Time.setText("");
        Edt_To_Time.setText("");

        Chkbx_Mon.setChecked(false);
        Chkbx_Tue.setChecked(false);
        Chkbx_Wed.setChecked(false);
        Chkbx_Thur.setChecked(false);
        Chkbx_Fri.setChecked(false);
        Chkbx_Sat.setChecked(false);
        Chkbx_Sun.setChecked(false);

        Btn_Submit.setText("Submit");

    }

    //**************************************************************************************
    private RecyclerView recyclerView;

    public void LoadRecyler() {

        /**
         * To load recycler view old dashboard
         */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_Batch.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_Batch> DataItems = prepareData();
        Mstr_Batch_Adapter adapter = new Mstr_Batch_Adapter(Mstr_Batch.this, DataItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);


    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_Batch> prepareData() {

        ArrayList<Getter_Setter.Mstr_Batch> Dataitems = new ArrayList<>();
        Getter_Setter.Mstr_Batch obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Mstr_Batch";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.Mstr_Batch();
                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setSubject(c.getString(c.getColumnIndex("Subject_Name")));
                    obj.setSubjectId(c.getString(c.getColumnIndex("Subject_Id")));
                    obj.setBatch_Name(c.getString(c.getColumnIndex("Batch_Name")));
                    obj.setYear(c.getString(c.getColumnIndex("YearOfExam")));
                    obj.setCoaching_Days(c.getString(c.getColumnIndex("Coaching_Days")));
                    obj.setClass_Timing(c.getString(c.getColumnIndex("Class_From")) + " to " + c.getString(c.getColumnIndex("Class_To")));
                    obj.setStatus(c.getString(c.getColumnIndex("IsActive")));
                    Dataitems.add(obj);


                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();


        return Dataitems;

    }

    //*********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(this, Masters.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    int LOCAL_UPDATE_ID;
    String OLD_BATCH;

    public class Mstr_Batch_Adapter extends RecyclerView.Adapter<Mstr_Batch_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.Mstr_Batch> DataItems;
        private Context context;


        //**********************************************************************************************

        public Mstr_Batch_Adapter(Context context, ArrayList<Getter_Setter.Mstr_Batch> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Mstr_Batch_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_batch_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_Batch_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here

            viewHolder.SubjectName.setText(DataItems.get(i).getSubject());
            viewHolder.BatchName.setText(DataItems.get(i).getBatch_Name());
            viewHolder.Year.setText(DataItems.get(i).getYear());
            viewHolder.Days.setText(DataItems.get(i).getCoaching_Days().substring(0, DataItems.get(i).getCoaching_Days().length() - 1));
            viewHolder.Timing.setText(DataItems.get(i).getClass_Timing());
            String values = "";
            if (DataItems.get(i).getStatus().toString().equalsIgnoreCase("True")) {
                values = "Active";
            } else {
                values = "Not Active";
            }

            viewHolder.Status.setText(values);
            viewHolder.Sno.setText((i + 1) + ".");

            viewHolder.Options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final CharSequence[] items = {
                            "Update", "Delete", "Close"
                    };


                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Options");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (items[item].toString().equalsIgnoreCase("Update")) {


                                String subjectname = DataItems.get(i).getSubject();

                                if (Spn_subject.getAdapter() != null) {
                                    for (int j = 0; j <= Spn_subject.getAdapter().getCount() - 1; j++) {
                                        String compareValue = Spn_subject.getAdapter().getItem(j).toString();

                                        if (compareValue.equalsIgnoreCase(subjectname)) {
                                            Spn_subject.setSelection(j);


                                        }
                                    }
                                }

                                Edt_BatchName.setText(DataItems.get(i).getBatch_Name());
                                Edt_year.setText(DataItems.get(i).getYear());
                                String Str_Days = DataItems.get(i).getCoaching_Days().toString().substring(0, DataItems.get(i).getCoaching_Days().length() - 1);
                                String[] days = Str_Days.toString().split(",");

                                for (int k = 0; k < days.length; k++) {

                                    String str = days[k].toString();
                                    Log.e("onClick: ",str );
                                    Log.e("onClick: ",str );
                                    if (str.contains("Mon")) {
                                        Chkbx_Mon.setChecked(true);
                                    } else if (str.contains("Tue")) {
                                        Chkbx_Tue.setChecked(true);
                                    } else if (str.contains("Wed")) {
                                        Chkbx_Wed.setChecked(true);
                                    } else if (str.contains("Thu")) {
                                        Chkbx_Thur.setChecked(true);
                                    } else if (str.contains("Fri")) {
                                        Chkbx_Fri.setChecked(true);
                                    } else if (str.contains("Sat")) {
                                        Chkbx_Sat.setChecked(true);
                                    } else if (str.contains("Sun")) {
                                        Chkbx_Sun.setChecked(true);
                                    }
                                }

                                Edt_From_Time.setText(DataItems.get(i).getClass_Timing().split("to")[0].toString().trim());
                                Edt_To_Time.setText(DataItems.get(i).getClass_Timing().split("to")[1].toString().trim());

                                if (DataItems.get(i).getStatus().toString().equalsIgnoreCase("True")) {
                                    Chkbx_Active.setChecked(true);
                                } else {
                                    Chkbx_Active.setChecked(false);
                                }

                                Btn_Submit.setText("Update");

                                LOCAL_UPDATE_ID = DataItems.get(i).getId();
                                OLD_BATCH = DataItems.get(i).getBatch_Name();


                            } else if (items[item].toString().equalsIgnoreCase("Delete")) {


                                Delete(i);

                            } else if (items[item].toString().equalsIgnoreCase("Close")) {

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


        public void Delete(int position) {

            new SweetAlertDialog(Mstr_Batch.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Mstr_Batch.this.getResources().getString(R.string.information))
                    .setContentText("Are you sure want to delete?")
                    .setCancelText(Mstr_Batch.this.getResources().getString(R.string.no))
                    .setConfirmText(Mstr_Batch.this.getResources().getString(R.string.yes))
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
                            db.execSQL("Delete from Mstr_Batch where Id='" + DataItems.get(position).getId() + "'");
                            db.close();

                            LoadRecyler();
                        }
                    })
                    .show();

        }
        //**********************************************************************************************

        @Override
        public int getItemCount() {
            return DataItems.size();
        }

        //**********************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView SubjectName, BatchName, Year, Days, Timing, Status, Sno;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls
                SubjectName = view.findViewById(R.id.txt_sub_name);
                BatchName = view.findViewById(R.id.txt_bat_name);
                Year = view.findViewById(R.id.txt_year);
                Days = view.findViewById(R.id.txt_days);
                Timing = view.findViewById(R.id.txt_time);
                Status = view.findViewById(R.id.txt_status);
                Sno = view.findViewById(R.id.txt_sno);
                Options = view.findViewById(R.id.img_options);

            }
        }

        //**********************************************************************************************

    }
    //End
}
