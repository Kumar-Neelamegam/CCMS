package entry_activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.regex.Pattern;

import adapters.Getter_Setter;

import core_modules.Task_Navigation;
import masters.Mstr_Batch;
import masters.Mstr_Fee;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/17/2017.
 */

public class Mark_Entry extends AppCompatActivity {


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    //variable declaration
    Spinner batch, testname;
    FButton clear, search, saveall;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_entry);


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
        testname = findViewById(R.id.spn_sub);

        clear = findViewById(R.id.bttn_clear);
        search = findViewById(R.id.bttn_search);
        saveall = findViewById(R.id.btn_saveall);

        BatchName  = findViewById(R.id.txtvw_batchname);
        SubjectName= findViewById(R.id.txtvw_subname);
        TestName   = findViewById(R.id.txtvw_testname);
        TestDate   = findViewById(R.id.txtvw_testdate);
        MaxMarks   = findViewById(R.id.txtvw_maxmarks);


        recyclerView = findViewById(R.id.recycler_view);

        //Loading batch list
        Baseconfig.LoadValuesSpinner(batch, Mark_Entry.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");



    }
    //**********************************************************************************************

    int Insert_Flag=0;
    private void Controllisteners() {
        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Mark_Entry.this, Mark_Entry.class));

        batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (batch.getSelectedItemPosition() > 0) {
                    Baseconfig.LoadValuesSpinner(testname, Mark_Entry.this, "select distinct Name_Of_Test as dvalue from Mstr_Test where IsActive='1' and Batch_Name='"+batch.getSelectedItem().toString()+"'", "Select");
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

                    LoadStudentsList(batch.getSelectedItem().toString(), testname.getSelectedItem().toString().toString(), 1);

                } else {
                    Baseconfig.SweetDialgos(3, Mark_Entry.this, "Information", "Select batch & test..", "OK");
                }

            }
        });


        saveall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    new SweetAlertDialog(Mark_Entry.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Confirmation")
                            .setContentText("Are you sure want to save all?")
                            .setCancelText(Mark_Entry.this.getResources().getString(R.string.no))
                            .setConfirmText(Mark_Entry.this.getResources().getString(R.string.yes))
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


                                    printAllEditTextValues(recyclerView);

                                }
                            })
                            .show();



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Clear();
            }
        });


    }

    void Clear()
    {

        batch.setSelection(0);
        testname.setAdapter(null);

        BatchName.setText("");
        SubjectName.setText("");
        TestName.setText("");
        TestDate.setText("");
        MaxMarks.setText("");

        recyclerView.removeAllViews();
        recyclerView.setAdapter(null);


    }


    int k=0;

    public void printAllEditTextValues(RecyclerView recyclerView) {

        Insert_Flag=1;

        int childCount = recyclerView.getChildCount();
        int i;
        for (i = 0; i < childCount; i++)
        {

             if (recyclerView.findViewHolderForLayoutPosition(i) instanceof MarksEntry_Adapter.ViewHolder)
            {
                MarksEntry_Adapter.ViewHolder childHolder = (MarksEntry_Adapter.ViewHolder)
                        recyclerView.findViewHolderForLayoutPosition(i);

                String MaxMarks = Baseconfig.LoadValue("select MaxMarks as dstatus from Mstr_Test where " +
                        "Batch_Name='" + batch.getSelectedItem().toString() + "' " +
                        "and Name_Of_Test='" + testname.getSelectedItem() + "'");

                String pass = childHolder.Mark.getText().toString();

                if(pass==null || pass.toString().length()==0)
                {
                    pass="0";
                }

                if (TextUtils.isEmpty(pass) || pass.length()==0)
                {

                    k++;

                }

                if(Integer.parseInt(pass)>Integer.parseInt(MaxMarks))
                {
                     childHolder.Mark.setError("Exceeds Maximum Marks - Invalid *");

                    return;
                }

                String SID= childHolder.SID.getText().toString();
                String MarksObtained= childHolder.Mark.getText().toString();

                UpdateMarks(SID,MarksObtained);

            }
        }

        if(childCount==k)
        {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(this.getResources().getString(R.string.information))
                    .setContentText("No entry found do you want to continue later?")
                    .setCancelText(this.getResources().getString(R.string.no))
                    .setConfirmText(this.getResources().getString(R.string.yes))
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {


                            sDialog.dismiss();
                            Mark_Entry.this.finish();
                            startActivity(new Intent(Mark_Entry.this, Task_Navigation.class));

                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.dismiss();

                        }
                    })
                    .show();

            return;
        }

        if(i==childCount)
        {
            ShowSuccessDialog();
        }


    }

    public void ShowSuccessDialog()
    {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("Students marks added successfully..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                        Mark_Entry.this.finish();
                        startActivity(new Intent(Mark_Entry.this, Task_Navigation.class));
                    }
                })
                .show();
    }


    public void UpdateMarks(String SID,String ObtainedMarks) {

        String Str_Batch_Name=batch.getSelectedItem().toString();
        String Str_TestName=testname.getSelectedItem().toString();
        String Absent_Status="0";

        if(ObtainedMarks.toString().equalsIgnoreCase("0"))
        {
            Absent_Status="1";
        }

        if(ObtainedMarks.toString().length()==0 || ObtainedMarks==null || ObtainedMarks.toString().equalsIgnoreCase(""))
        {
            ObtainedMarks="";
        }


        String Str_TestId=Baseconfig.LoadValue("select Id as dstatus from Mstr_Test where Name_Of_Test='"+Str_TestName+"' and Batch_Name='"+Str_Batch_Name+"'");

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Batch_Name", Str_Batch_Name);
        values.put("Name_Of_Test", Str_TestName);
        values.put("SID",SID);
        values.put("Obtained_Marks",ObtainedMarks);
        values.put("ActDate",Baseconfig.GetDate());
        values.put("Test_ID", Str_TestId);
        values.put("IsActive", 1);
        values.put("IsUpdate", 0);
        values.put("IsSMS_Sent", 0);
        values.put("Absent_Status",Absent_Status);


        boolean q=Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Bind_MarkEntry where " +
                "Batch_Name='"+Str_Batch_Name+"' and " +
                "Name_Of_Test='"+Str_TestName+"' and " +
                "SID='"+SID+"' and " +
                "Test_ID='"+Baseconfig.LoadValue("select Id as dstatus from Mstr_Test where Name_Of_Test='"+Str_TestName+"' and Batch_Name='"+Str_Batch_Name+"'")+"'");

        //Toast.makeText(this, "Mark Entry Status: "+String.valueOf(q), Toast.LENGTH_SHORT).show();

        if(q)//data iruntha update
        {

            db.update("Bind_MarkEntry", values, "SID='" + SID + "' and Test_ID='"+Str_TestId+"'", null);
            Log.e("Update Marks: ", values.toString());
        }else//data ilaina update
        {
            db.insert("Bind_MarkEntry", null, values);
            Log.e("Insert Marks: ", values.toString());
        }




        db.close();



    }

    //**********************************************************************************************
    private RecyclerView recyclerView;

    TextView BatchName,SubjectName,TestName,TestDate,MaxMarks;

    void LoadStudentsList(String selected_batch, String selected_testname, int Id) {


        /**
         * To load recycler view old dashboard
         */
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mark_Entry.this, 1);
        recyclerView.setLayoutManager(layoutManager);

      /*  boolean b=Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Bind_MarkEntry where " +
                "Batch_Name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"' and Obtained_marks!=''");
*/

        ArrayList<Getter_Setter.MarkEntry> DataItems = prepareData(selected_batch, selected_testname, Id);
        MarksEntry_Adapter adapter = new MarksEntry_Adapter(Mark_Entry.this, DataItems);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        BatchName.setText(selected_batch);
        SubjectName.setText(Baseconfig.LoadValue("select Subject as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));
        TestName.setText(selected_testname);
        TestDate.setText(Baseconfig.LoadValue("select Date_Of_Test as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));
        MaxMarks.setText(Baseconfig.LoadValue("select MaxMarks as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));

        recyclerView.setNestedScrollingEnabled(false);


        /*if(!b)
        {
            ArrayList<Getter_Setter.MarkEntry> DataItems = prepareData(selected_batch, selected_testname, Id);
            MarksEntry_Adapter adapter = new MarksEntry_Adapter(Mark_Entry.this, DataItems);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            BatchName.setText(selected_batch);
            SubjectName.setText(Baseconfig.LoadValue("select Subject as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));
            TestName.setText(selected_testname);
            TestDate.setText(Baseconfig.LoadValue("select Date_Of_Test as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));
            MaxMarks.setText(Baseconfig.LoadValue("select MaxMarks as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));

            recyclerView.setNestedScrollingEnabled(false);

        }else
        {

          *//*  Baseconfig.SweetDialgos(3,this,"Information","Already marks updated for selected batch and test","OK");
            Clear();
            return ;*//*

            ArrayList<Getter_Setter.MarkEntry> DataItems = prepareData2(selected_batch, selected_testname, Id);
            MarksEntry_Adapter adapter = new MarksEntry_Adapter(Mark_Entry.this, DataItems);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            BatchName.setText(selected_batch);
            SubjectName.setText(Baseconfig.LoadValue("select Subject as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));
            TestName.setText(selected_testname);
            TestDate.setText(Baseconfig.LoadValue("select Date_Of_Test as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));
            MaxMarks.setText(Baseconfig.LoadValue("select MaxMarks as dstatus from Mstr_Test where Batch_name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"'"));

            recyclerView.setNestedScrollingEnabled(false);



        }*/



    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.MarkEntry> prepareData(String selected_batch, String selected_testname, int Id) {

        ArrayList<Getter_Setter.MarkEntry> Dataitems = new ArrayList<>();
        Getter_Setter.MarkEntry obj;

        SQLiteDatabase db = Baseconfig.GetDb();

        String Query = "";

      /*  String Insert_Query="Insert into Bind_MarkEntry (Batch_Name,SID,Name_Of_Test,Test_ID,ActDate,IsActive,IsUpdate,IsSMS_Sent)\n" +
                " select '"+selected_batch+"',SID,'"+selected_testname+"','"+GetTestId(selected_testname,selected_batch)+"','" + Baseconfig.GetDate() + "',1,0,0" +
                " from Bind_EnrollStudents WHERE NOT EXISTS(select SID from Bind_MarkEntry where" +
                " Bind_MarkEntry.SID=Bind_EnrollStudents.SID and ActDate='" + Baseconfig.GetDate() + "' and" +
                " Batch_Name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"') and Batch_Info like '%"+selected_batch+"%' and IsActive='1'";*/

        String Insert_Query="Insert into Bind_MarkEntry (Batch_Name,SID,Name_Of_Test,Test_ID,ActDate,IsActive,IsUpdate,IsSMS_Sent)\n" +
                " select '"+selected_batch+"',SID,'"+selected_testname+"','"+GetTestId(selected_testname,selected_batch)+"','" + Baseconfig.GetDate() + "',1,0,0" +
                " from Bind_EnrollStudents WHERE NOT EXISTS(select SID from Bind_MarkEntry where" +
                " Bind_MarkEntry.SID=Bind_EnrollStudents.SID and" +
                " Batch_Name='"+selected_batch+"' and Name_Of_Test='"+selected_testname+"') and Batch_Info like '%"+selected_batch+",%' and IsActive='1'";

        db.execSQL(Insert_Query);





        Log.e("Insert_Query: ", Insert_Query);


        //Query = "select Id,SID,Name from Bind_EnrollStudents where Batch_Info like '%" + selected_batch + "%'";
        Query = "select Id,SID,IFNULL(Obtained_Marks,'')as  Obtained_Marks from Bind_MarkEntry  where Batch_Name='" + selected_batch + "' and Name_Of_Test='"+selected_testname+"'";


        Log.e("Loading Query: ", Query);

        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.MarkEntry();
                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setName(Baseconfig.LoadValue("Select Name as dstatus from Bind_EnrollStudents where SID='"+c.getString(c.getColumnIndex("SID"))+"'"));
                    obj.setSID(c.getString(c.getColumnIndex("SID")));
                    obj.setMarks(c.getString(c.getColumnIndex("Obtained_Marks")));

                    Dataitems.add(obj);

                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();

        return Dataitems;

    }

    String GetTestId(String TestName,String BatchName)
    {

        String str="";
        str=Baseconfig.LoadValue("select Id as dstatus from Mstr_Test where Name_Of_Test='"+TestName+"' " +
                "and Batch_Name='"+BatchName+"'");

        //Toast.makeText(this, "TestId:"+str, Toast.LENGTH_SHORT).show();
        return str;
    }


    private ArrayList<Getter_Setter.MarkEntry> prepareData2(String selected_batch, String selected_testname, int Id) {

        ArrayList<Getter_Setter.MarkEntry> Dataitems = new ArrayList<>();
        Getter_Setter.MarkEntry obj;

        SQLiteDatabase db = Baseconfig.GetDb();

        String Query = "";

        Query = "select Id,SID,IFNULL(Obtained_Marks,'')as  Obtained_Marks from Bind_MarkEntry  where Batch_Name='" + selected_batch + "'";


        Log.e("Loading Query: ", Query);

        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.MarkEntry();
                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setName(Baseconfig.LoadValue("Select Name as dstatus from Bind_EnrollStudents where SID='"+c.getString(c.getColumnIndex("SID"))+"'"));
                    obj.setSID(c.getString(c.getColumnIndex("SID")));
                    obj.setMarks(c.getString(c.getColumnIndex("Obtained_Marks")));


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

       /* if(Insert_Flag==0)
        {
            SQLiteDatabase db = Baseconfig.GetDb();
            db.execSQL("delete from Temp_MarkEntry where ActDate='"+Baseconfig.GetDate()+"'");
            db.close();

        }*/
        this.finishAffinity();
        Intent back = new Intent(Mark_Entry.this, Task_Navigation.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    /**
     * Mark list Adapters
     *
     */

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    public class MarksEntry_Adapter extends RecyclerView.Adapter<MarksEntry_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.MarkEntry> DataItems;
        private Context context;


        //**********************************************************************************************

        public MarksEntry_Adapter(Context context, ArrayList<Getter_Setter.MarkEntry> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public MarksEntry_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.marklist_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(MarksEntry_Adapter.ViewHolder viewHolder, final int i) {


            viewHolder.Sno.setText((i + 1) + ".");
            viewHolder.SID.setText(DataItems.get(i).getSID());
            viewHolder.Name.setText(DataItems.get(i).getName());
            viewHolder.Mark.setText(DataItems.get(i).getMarks());

            //viewHolder.Mark.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(3)});
            /*String MaxMarks = Baseconfig.LoadValue("select MaxMarks as dstatus from Mstr_Test where Batch_Name='" + batch.getSelectedItem().toString() + "' " +
                    "and Name_Of_Test='" + testname.getSelectedItem() + "'");
*/
            //viewHolder.Mark.setFilters(new InputFilter[]{ new InputFilterMinMax("1", MaxMarks)});


        }

        //**********************************************************************************************
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; ++i) {
                    if (!Pattern.compile("1234567890").matcher(String.valueOf(source.charAt(i))).matches()) {
                        return "";
                    }
                }

                return null;
            }
        };

        @Override
        public int getItemCount() {
            return DataItems.size();
        }

        //**********************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView Sno, SID, Name;
            EditText Mark;

            public ViewHolder(View view) {
                super(view);

                Sno = view.findViewById(R.id.serial_no);
                SID = view.findViewById(R.id.txt_stu_id);
                Name = view.findViewById(R.id.txt_stu_name);
                Mark = view.findViewById(R.id.edt_mark_opt);

            }
        }


        public class InputFilterMinMax implements InputFilter {

            private int min, max;

            public InputFilterMinMax(int min, int max) {
                this.min = min;
                this.max = max;
            }

            public InputFilterMinMax(String min, String max) {
                this.min = Integer.parseInt(min);
                this.max = Integer.parseInt(max);
            }

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    int input = Integer.parseInt(dest.toString() + source.toString());
                    if (isInRange(min, max, input))


                        for (int i = start; i < end; ++i) {
                            if (!Pattern.compile("1234567890").matcher(String.valueOf(source.charAt(i))).matches()) {
                                return "";
                            }
                        }


                    return null;
                } catch (NumberFormatException nfe) {
                }
                return "";
            }

            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        }
        //**********************************************************************************************

    }

    //End
}

