package vcc.cretivemindsz.kumar.masters;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import vcc.cretivemindsz.kumar.adapters.Getter_Setter;
import vcc.cretivemindsz.kumar.utilities.Baseconfig;
import vcc.cretivemindsz.kumar.utilities.FButton;
import vcc.cretivemindsz.kumar.R;;

/**
 * Created by Kumar on 5/17/2017.
 */

public class Mstr_Test extends AppCompatActivity {

//eruma wait

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    //variable declaration

    Spinner batch;//, subject;
    EditText test_name, test_date, max_mark;
    FButton clear, submit;
    TextView stu_id, name, marks;
    CheckBox Chkbx_Active;
    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_test);


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

        batch = findViewById(R.id.spn_batch_list);
        //subject = (Spinner) findViewById(R.id.spn_sub_list);

        test_name = findViewById(R.id.edt_test_name);
        test_date = findViewById(R.id.edt_test_date);
        max_mark = findViewById(R.id.edt_max_marks);

        clear = findViewById(R.id.btn_clear);
        submit = findViewById(R.id.btn_subm);

        stu_id = findViewById(R.id.txt_stu_id);
        name = findViewById(R.id.txt_stu_name);
        marks = findViewById(R.id.edt_mark_opt);

        Chkbx_Active= findViewById(R.id.chk_active);


        //Loading batch list
        Baseconfig.LoadValuesSpinner(batch, Mstr_Test.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");


        //Loading subject list
        //Baseconfig.LoadValuesSpinner(subject, Mstr_Test.this, "select distinct Subject_Name as dvalue from Mstr_Subject where IsActive='1';", "Select");


        LoadRecyler();
    }

    //**************************************************************************************
    private RecyclerView recyclerView;

    public void LoadRecyler() {

        /**
         * To load recycler view old dashboard
         */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_Test.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_Test> DataItems = prepareData();
        Mstr_Test_Adapter adapter = new Mstr_Test_Adapter(Mstr_Test.this, DataItems, recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);


    }
    //**********************************************************************************************

    private void Controllisteners() {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Mstr_Test.this, Mstr_Test.class));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {


                    if (submit.getText().toString().equalsIgnoreCase("Submit"))//Submit
                    {

                        boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Test where Name_Of_Test='" + test_name.getText().toString() + "' and Batch_Name='" + batch.getSelectedItem().toString() + "' and isActive='1'");
                        if (!b) {
                            SaveLocal();
                        } else {

                            Baseconfig.SweetDialgos(4, Mstr_Test.this, "Information", " Duplicate entry.. already test name (" + test_name.getText().toString() + ") exists in list", "OK");
                            Clear();
                        }


                    } else if (submit.getText().toString().equalsIgnoreCase("Update"))//Update
                    {

                        boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Test where Name_Of_Test='" + test_name.getText().toString() + "' and Batch_Name='" + batch.getSelectedItem().toString() + "' and isActive='1'");
                        if (!b) {
                            //condition venum chk paniko
                            UpdateLocal();
                        } else {

                            //Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");

                            ShowDuplicateAlert();


                        }

                    }
                } else {

                    Baseconfig.SweetDialgos(4, Mstr_Test.this, "Information", " Please fill all mandatory fields marked with (*)\n" + strError.toString(), "OK");
                }


            }
        });


        test_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectDate();

            }
        });

    }

    //**********************************************************************************************
    void ShowDuplicateAlert() {
        new SweetAlertDialog(Mstr_Test.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Mstr_Test.this.getResources().getString(R.string.information))
                .setContentText(" Duplicate entry.. already test name (" + test_name.getText().toString() + ") exists in list")
                .setCancelText(Mstr_Test.this.getResources().getString(R.string.no))
                .setConfirmText(Mstr_Test.this.getResources().getString(R.string.yes))
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
                //dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

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

            // Show selected date
            //StringBuilder values = new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" ");
            test_date.setText(dateString.toString());
            Log.e("Selected DOB: ", dateString.toString());
        }


    };

    //**********************************************************************************************
    StringBuilder strError;

    public boolean checkValidation() {
        boolean ret = true;

        strError = new StringBuilder();


        if (max_mark.getText().length() == 0) {
            strError.append("Enter max marks*\n");
            ret = false;
        }


        if (test_date.getText().length() == 0) {
            strError.append("Select test date*\n");
            ret = false;
        }


        if (test_name.getText().length() == 0) {
            strError.append("Enter test name*\n");
            ret = false;
        }


       /* if (subject.getSelectedItemPosition() == 0) {
            strError.append("Choose subject*\n");
            ret = false;
        }*/


        if (batch.getSelectedItemPosition() == 0) {
            strError.append("Choose batch*\n");
            ret = false;
        }


        return ret;

    }

    public void SaveLocal() {


        String Str_Batch = "", Str_Subject="", Str_TestName = "", Str_DateOfTest = "", Str_MaxMarks = "";
        String Str_Isactive = "1";
        Str_Batch = batch.getSelectedItem().toString();
       // Str_Subject = subject.getSelectedItem().toString();
        Str_TestName = test_name.getText().toString();
        Str_DateOfTest = test_date.getText().toString();
        Str_MaxMarks = max_mark.getText().toString();

        if (Baseconfig.CheckBox(Chkbx_Active)) {
            Str_Isactive = "1";
        } else {
            Str_Isactive = "0";
        }

        Str_Subject=Baseconfig.LoadValue("select Subject_Name as dstatus from Mstr_Batch where Batch_Name='"+Str_Batch+"'");

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Batch_Name", Str_Batch);
        values.put("Subject", Str_Subject);
        values.put("Name_Of_Test", Str_TestName);
        values.put("Date_Of_Test", Str_DateOfTest);
        values.put("MaxMarks", Str_MaxMarks);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", 0);     values.put("ServerIsUpdate",0);
        values.put("ActDate", Baseconfig.GetDate());   values.put("FUID", Baseconfig.App_UID);

        db.insert("Mstr_Test", null, values);
        Log.e("Inserted Values: ", String.valueOf(values));

        db.close();
        ShowSuccessDialog();


    }



    public void UpdateLocal() {


        String Str_Batch = "", Str_Subject = "", Str_TestName = "", Str_DateOfTest = "", Str_MaxMarks = "";
        String Str_Isactive = "1";
        Str_Batch = batch.getSelectedItem().toString();
        //Str_Subject = subject.getSelectedItem().toString();
        Str_TestName = test_name.getText().toString();
        Str_DateOfTest = test_date.getText().toString();
        Str_MaxMarks = max_mark.getText().toString();
        Str_Subject=Baseconfig.LoadValue("select Subject_Name as dstatus from Mstr_Batch where Batch_Name='"+Str_Batch+"'");
        if (Baseconfig.CheckBox(Chkbx_Active)) {
            Str_Isactive = "1";
        } else {
            Str_Isactive = "0";
        }

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Batch_Name", Str_Batch);
        values.put("Subject", Str_Subject);
        values.put("Name_Of_Test", Str_TestName);
        values.put("Date_Of_Test", Str_DateOfTest);
        values.put("MaxMarks", Str_MaxMarks);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", 0);
        values.put("ActDate", Baseconfig.GetDate());
        db.update("Mstr_Test",values,  "Id='" + LOCAL_UPDATE_ID + "'", null);

        db.execSQL("Update Bind_MarkEntry set Name_Of_Test=REPLACE(Name_Of_Test,'" + OLD_TESTNAME + "','" + Str_TestName + "') where Name_Of_Test like '%" + OLD_TESTNAME + "%'");


        db.close();
        ShowSuccessDialog();


    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_Test> prepareData() {

        try {
            ArrayList<Getter_Setter.Mstr_Test> Dataitems = new ArrayList<>();
            Getter_Setter.Mstr_Test obj;

            SQLiteDatabase db = Baseconfig.GetDb();
            String Query = "select * from Mstr_Test";
            Cursor c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {


                        obj = new Getter_Setter.Mstr_Test();
                        obj.setId(c.getInt(c.getColumnIndex("Id")));
                        obj.setBatch(c.getString(c.getColumnIndex("Batch_Name")));
                        obj.setSubject(c.getString(c.getColumnIndex("Subject")));
                        obj.setNameOfTest(c.getString(c.getColumnIndex("Name_Of_Test")));
                        obj.setDateOfTest(c.getString(c.getColumnIndex("Date_Of_Test")));
                        obj.setMaxMarks(c.getString(c.getColumnIndex("MaxMarks")));
                        obj.setIsActive(c.getString(c.getColumnIndex("IsActive")));
                        Dataitems.add(obj);


                    } while (c.moveToNext());

                }

            }
            c.close();
            db.close();


            return Dataitems;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //*********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(Mstr_Test.this, Masters.class);
        startActivity(back);
    }


    public void ShowSuccessDialog() {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("Test Added successfully..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                    }
                })
                .show();

        Clear();
        LoadRecyler();
    }

    //**********************************************************************************************
    public void Clear() {

        batch.setSelection(0);
        //subject.setSelection(0);
        test_name.setText("");
        test_date.setText("");
        max_mark.setText("");


    }
    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    /**
     * Test Adapter
     */


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    int LOCAL_UPDATE_ID;
    String OLD_TESTNAME;

    public class Mstr_Test_Adapter extends RecyclerView.Adapter<Mstr_Test_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.Mstr_Test> DataItems;
        private Context context;
        RecyclerView recyclerView;


        //**********************************************************************************************

        public Mstr_Test_Adapter(Context context, ArrayList<Getter_Setter.Mstr_Test> DataItems, RecyclerView recyclerView) {
            this.DataItems = DataItems;
            this.context = context;
            this.recyclerView = recyclerView;
        }
        //**********************************************************************************************

        @Override
        public Mstr_Test_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_test_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_Test_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here


            viewHolder.TestName.setText(DataItems.get(i).getNameOfTest());
            viewHolder.Date.setText(DataItems.get(i).getDateOfTest());
            viewHolder.MaxMarks.setText(DataItems.get(i).getMaxMarks());


            viewHolder.BatchName.setText(DataItems.get(i).getBatch());
            viewHolder.SubjectName.setText(DataItems.get(i).getSubject());

            viewHolder.Sno.setText((i + 1) + ".");

            String values = "";
            if (DataItems.get(i).getIsActive().toString().equalsIgnoreCase("1")) {
                values = "Active";
            } else {
                values = "Not Active";
            }

            viewHolder.Status.setText(values);

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

                                //Selecting batch
                                String batchname = DataItems.get(i).getBatch();

                                if (batch.getAdapter() != null) {
                                    for (int j = 0; j <=batch.getAdapter().getCount()-1; j++) {
                                        String compareValue = batch.getAdapter().getItem(j).toString();

                                        if (compareValue.equalsIgnoreCase(batchname)) {
                                            batch.setSelection(j);
                                        }
                                    }
                                }

                                //selecting subject name
                               // String subjectname = DataItems.get(i).getSubject();

                               /* if (subject.getAdapter() != null) {
                                    for (int j = 0; j <= subject.getAdapter().getCount() - 1; j++) {
                                        String compareValue = subject.getAdapter().getItem(j).toString();

                                        if (compareValue.equalsIgnoreCase(subjectname)) {
                                            subject.setSelection(j);


                                        }
                                    }
                                }*/

                                //test name
                                test_name.setText(DataItems.get(i).getNameOfTest());
                                test_date.setText(DataItems.get(i).getDateOfTest());
                                max_mark.setText(DataItems.get(i).getMaxMarks());

                                submit.setText("Update");
                                LOCAL_UPDATE_ID = DataItems.get(i).getId();
                                OLD_TESTNAME = DataItems.get(i).getSubject();


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
        //**********************************************************************************************

        public void Delete(int position) {
            new SweetAlertDialog(Mstr_Test.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Mstr_Test.this.getResources().getString(R.string.information))
                    .setContentText("Are you sure want to delete?")
                    .setCancelText(Mstr_Test.this.getResources().getString(R.string.no))
                    .setConfirmText(Mstr_Test.this.getResources().getString(R.string.yes))
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
                            db.execSQL("Delete from Mstr_Test where Id='" + DataItems.get(position).getId() + "'");
                            db.close();

                            LoadRecyler();
                        }
                    })
                    .show();

        }

        @Override
        public int getItemCount() {
            return DataItems.size();
        }

        //**********************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView BatchName, SubjectName, TestName, Date, MaxMarks, Sno,Status;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls

                BatchName = view.findViewById(R.id.txt_bat_name);
                SubjectName = view.findViewById(R.id.txt_sub_name);
                TestName = view.findViewById(R.id.txt_testname);
                Date = view.findViewById(R.id.txt_date);
                MaxMarks = view.findViewById(R.id.txt_maxmarks);
                Sno = view.findViewById(R.id.txt_sno);
                Options = view.findViewById(R.id.img_options);
                Status = view.findViewById(R.id.txt_status);
            }
        }

        //**********************************************************************************************

    }

    //End
}
