package vcc.cretivemindsz.kumar.masters;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;

import vcc.cretivemindsz.kumar.adapters.Getter_Setter;
import vcc.cretivemindsz.kumar.utilities.Baseconfig;
import vcc.cretivemindsz.kumar.utilities.FButton;
import vcc.cretivemindsz.kumar.R;;

/**
 * Created by Kumar on 5/20/2017.
 */

public class Mstr_School  extends AppCompatActivity {


    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back,Exit;

    EditText School;
    FButton clear,submit;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_school);

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

        School= findViewById(R.id.edt_schl);

        clear= findViewById(R.id.btn_cancel);
        submit= findViewById(R.id.btn_submit);

        LoadRecyler();


    }
    //**********************************************************************************************

    private void Controllisteners()
    {
        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this,Mstr_School.class));

        submit.setOnClickListener(view -> {

            if (CheckValidations()) {

                if(submit.getText().toString().equalsIgnoreCase("Submit"))//Submit
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_School where School_Name='" + School.getText().toString() + "' and IsActive='1'");
                    if (!b) {
                        SaveLocal();
                    } else {

                        Baseconfig.SweetDialgos(4, Mstr_School.this, "Information", " Duplicate entry.. already school name (" + School.getText().toString() + ") exists in list", "OK");
                        Clear();
                    }
                }
                else if(submit.getText().toString().equalsIgnoreCase("Update"))//Update
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_School where School_Name='" + School.getText().toString() + "' and IsActive='1'");
                    if (!b) {
                        //condition venum chk paniko
                        UpdateLocal();
                    } else {

                        //Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");

                        ShowDuplicateAlert();


                    }

                }
            } else {

                Baseconfig.SweetDialgos(4, Mstr_School.this, "Information", " Please fill mandatory fields marked with (*) ", "OK");
            }


        });


        clear.setOnClickListener(view -> Clear());


    }


    void ShowDuplicateAlert()
    {
        new SweetAlertDialog(Mstr_School.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Mstr_School.this.getResources().getString(R.string.information))
                .setContentText(" Duplicate entry.. already school (" + School.getText().toString() + ") exists in list..\nDo you want to update again?")
                .setCancelText(Mstr_School.this.getResources().getString(R.string.no))
                .setConfirmText(Mstr_School.this.getResources().getString(R.string.yes))
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
    public boolean CheckValidations()
    {
        boolean ret = true;

        if ((School.getText().toString().length() == 0))
        {
            ret = false;
        }

        return ret;
    }


    //**************************************************************************************
    public void SaveLocal()
    {

        String Str_School= "",Str_Isactive="1";


        Str_School=School.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();

        values.put("School_Name", Str_School);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "0");     values.put("ServerIsUpdate",0);
        values.put("ActDate", Baseconfig.GetDate());   values.put("FUID", Baseconfig.App_UID);

        db.insert("Mstr_School", null, values);
        db.close();


        ShowSuccessDialog("Data added to vcc.cretivemindsz.kumar.masters successfully..");

    }


    public void UpdateLocal()
    {

        String Str_School= "",Str_Isactive="1";


        Str_School=School.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();

        values.put("School_Name", Str_School);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "1");
        values.put("ActDate", Baseconfig.GetDate());
        db.update("Mstr_School",  values, "Id='"+LOCAL_UPDATE_ID+"'",null);

        db.execSQL("update Bind_EnrollStudents set School_Name=REPLACE(School_Name,'"+OLD_SCHOOL+"','"+Str_School+"') where School_Name like '%"+OLD_SCHOOL+"%'");
        db.close();

        ShowSuccessDialog("Data updated to vcc.cretivemindsz.kumar.masters successfully..");
        submit.setText("Submit");
    }
    //**********************************************************************************************


    public void ShowSuccessDialog(String Msg) {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText(Msg)
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog -> sDialog.dismiss())
                .show();

        Clear();

        LoadRecyler();
    }

    //**************************************************************************************

    public void Clear()
    {

        School.setText("");
        submit.setText("Submit");
    }

    //**************************************************************************************
    private RecyclerView recyclerView;

    public void LoadRecyler() {

        /**
         * To load recycler view old dashboard
         */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_School.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_School> DataItems = prepareData();
        Mstr_School_Adapter adapter = new Mstr_School_Adapter(Mstr_School.this, DataItems);
        recyclerView.setAdapter(adapter);

    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_School> prepareData()
    {

        ArrayList<Getter_Setter.Mstr_School> Dataitems = new ArrayList<>();
        Getter_Setter.Mstr_School obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Mstr_School where IsActive='1'";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.Mstr_School();

                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setSchool(c.getString(c.getColumnIndex("School_Name")));

                    Dataitems.add(obj);

                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();

        return Dataitems;

    }


    //**********************************************************************************************
    public void LoadBack()
    {
        this.finishAffinity();
        Intent back=new Intent(this, Masters.class);
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
    String OLD_SCHOOL;
    public class Mstr_School_Adapter extends RecyclerView.Adapter<Mstr_School_Adapter.ViewHolder>
    {

        private ArrayList<Getter_Setter.Mstr_School> DataItems;
        private Context context;


        //**********************************************************************************************

        public Mstr_School_Adapter(Context context, ArrayList<Getter_Setter.Mstr_School> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Mstr_School_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_school_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_School_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here

            viewHolder.Sno.setText((i+1)+".");
            viewHolder.School.setText(DataItems.get(i).getSchool());

            viewHolder.Options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final CharSequence[] items = {
                             "Update","Delete", "Close"
                    };


                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Options");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (items[item].toString().equalsIgnoreCase("Update")){
                                School.setText(DataItems.get(i).getSchool());
                                submit.setText("Update");
                                LOCAL_UPDATE_ID=DataItems.get(i).getId();
                                OLD_SCHOOL=DataItems.get(i).getSchool();

                            }
                            else if(items[item].toString().equalsIgnoreCase("Delete"))
                            {
                                Delete(i);

                            }
                            else if(items[item].toString().equalsIgnoreCase("Close"))
                            {

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

        public void Delete(int position)
        {
            new SweetAlertDialog(Mstr_School.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Mstr_School.this.getResources().getString(R.string.information))
                    .setContentText("Are you sure want to delete?")
                    .setCancelText(Mstr_School.this.getResources().getString(R.string.no))
                    .setConfirmText(Mstr_School.this.getResources().getString(R.string.yes))
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
                            db.execSQL("Delete from Mstr_School where Id='" + DataItems.get(position).getId() + "'");
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

        public class ViewHolder extends RecyclerView.ViewHolder
        {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView School,Sno;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls
                Options= view.findViewById(R.id.img_options);
                Sno= view.findViewById(R.id.txt_sno);

                School= view.findViewById(R.id.txt_school_name);

            }
        }

        //**********************************************************************************************

    }
//end
}
