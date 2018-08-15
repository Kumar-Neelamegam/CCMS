package masters;

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

import adapters.Getter_Setter;

import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/20/2017.
 */

public class Mstr_Subject extends AppCompatActivity {


    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    EditText subject, handleby;

    FButton clear, submit;


    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_subjects);

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

        subject = findViewById(R.id.edt_sub);
        handleby = findViewById(R.id.edt_handl);

        clear = findViewById(R.id.btn_cancel);
        submit = findViewById(R.id.btn_sub);

        LoadRecyler();

    }
    //**********************************************************************************************

    private void Controllisteners() {
        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, Mstr_Subject.class));

        submit.setOnClickListener(view -> {

            if (CheckValidations()) {

                if (submit.getText().toString().equalsIgnoreCase("Submit"))//Submit
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Subject where Subject_Name='" + subject.getText().toString() + "' and isActive='1'");
                    if (!b) {
                        SaveLocal();
                    } else {

                        Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");
                        Clear();
                    }
                }
                else if (submit.getText().toString().equalsIgnoreCase("Update"))//Update
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Subject where Subject_Name='" + subject.getText().toString() + "' and isActive='1'");
                    if (!b) {
                        //condition venum chk paniko
                        UpdateLocal();
                    } else {

                        //Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");

                        ShowDuplicateAlert();


                    }
                }
            } else {

                Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Please fill mandatory fields marked with (*) ", "OK");
            }


        });

        clear.setOnClickListener(view -> Clear());

    }

    void ShowDuplicateAlert() {
        new SweetAlertDialog(Mstr_Subject.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Mstr_Subject.this.getResources().getString(R.string.information))
                .setContentText(" Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list..\nDo you want to update again?")
                .setCancelText(Mstr_Subject.this.getResources().getString(R.string.no))
                .setConfirmText(Mstr_Subject.this.getResources().getString(R.string.yes))
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
    public boolean CheckValidations() {
        boolean ret = true;

        if (subject.getText().toString().length() == 0) {
            ret = false;
        }

        if (handleby.getText().toString().length() == 0) {
            ret = false;
        }


        return ret;
    }


    //**************************************************************************************
    public void SaveLocal() {

        String Str_Subject = "", Str_Handleby = "", Str_Isactive = "1";


        Str_Subject = subject.getText().toString();
        Str_Handleby = handleby.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();

        values.put("Subject_Name", Str_Subject);
        values.put("HandledBy", Str_Handleby);
        values.put("IsActive", Str_Isactive);     values.put("ServerIsUpdate",0);
        values.put("IsUpdate", "0");   values.put("FUID", Baseconfig.App_UID);

        values.put("ActDate", Baseconfig.GetDate());
        db.insert("Mstr_Subject", null, values);
        db.close();

        ShowSuccessDialog("Data added to masters successfully..");

    }


    public void UpdateLocal() {

        String Str_Subject = "", Str_Handleby = "", Str_Isactive = "1";


        Str_Subject = subject.getText().toString();
        Str_Handleby = handleby.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();

        values.put("Subject_Name", Str_Subject);
        values.put("HandledBy", Str_Handleby);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "1");
        values.put("ActDate", Baseconfig.GetDate());
        db.update("Mstr_Subject", values, "Id='" + LOCAL_UPDATE_ID + "'", null);

        db.execSQL("Update Bind_EnrollStudents set Subject=REPLACE(Subject,'" + OLD_SUBJECT + "','" + Str_Subject + "') where Subject like '%" + OLD_SUBJECT + "%'");
        db.execSQL("Update Mstr_Batch set Subject_Name=REPLACE(Subject_Name,'" + OLD_SUBJECT + "','" + Str_Subject + "') where Subject_Name like '%" + OLD_SUBJECT + "%'");
        db.execSQL("Update Mstr_Test set Subject=REPLACE(Subject,'" + OLD_SUBJECT + "','" + Str_Subject + "') where Subject like '%" + OLD_SUBJECT + "%'");

        db.close();

        ShowSuccessDialog("Data updated to masters successfully..");

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

    public void Clear() {

        subject.setText("");
        handleby.setText("");

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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_Subject.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_Subject> DataItems = prepareData();
        Mstr_Subject_Adapter adapter = new Mstr_Subject_Adapter(Mstr_Subject.this, DataItems);
        recyclerView.setAdapter(adapter);

    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_Subject> prepareData() {

        ArrayList<Getter_Setter.Mstr_Subject> Dataitems = new ArrayList<>();
        Getter_Setter.Mstr_Subject obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Mstr_Subject where IsActive='1'";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.Mstr_Subject();

                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setSubject(c.getString(c.getColumnIndex("Subject_Name")));
                    obj.setHandledBy(c.getString(c.getColumnIndex("HandledBy")));

                    Dataitems.add(obj);

                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();

        return Dataitems;

    }

    //**********************************************************************************************

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
    String OLD_SUBJECT;

    public class Mstr_Subject_Adapter extends RecyclerView.Adapter<Mstr_Subject_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.Mstr_Subject> DataItems;
        private Context context;


        //**********************************************************************************************

        public Mstr_Subject_Adapter(Context context, ArrayList<Getter_Setter.Mstr_Subject> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Mstr_Subject_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_subject_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_Subject_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here
            viewHolder.Sno.setText((i + 1) + ".");
            viewHolder.Subject.setText(DataItems.get(i).getSubject());
            viewHolder.HandledBy.setText(DataItems.get(i).getHandledBy());

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

                                //set the values for update
                                subject.setText(DataItems.get(i).getSubject());
                                handleby.setText(DataItems.get(i).getHandledBy());
                                submit.setText("Update");
                                LOCAL_UPDATE_ID = DataItems.get(i).getId();
                                OLD_SUBJECT = DataItems.get(i).getSubject();

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
            new SweetAlertDialog(Mstr_Subject.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Mstr_Subject.this.getResources().getString(R.string.information))
                    .setContentText("Are you sure want to delete?")
                    .setCancelText(Mstr_Subject.this.getResources().getString(R.string.no))
                    .setConfirmText(Mstr_Subject.this.getResources().getString(R.string.yes))
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
                            db.execSQL("Delete from Mstr_Subject where Id='" + DataItems.get(position).getId() + "'");

                            db.execSQL("Delete from Mstr_Batch where Subject_Id='"+ DataItems.get(position).getId() +"'");

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

            TextView Subject, HandledBy, Sno;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls
                Sno = view.findViewById(R.id.txt_sno);
                Subject = view.findViewById(R.id.txt_sub_name);
                HandledBy = view.findViewById(R.id.txt_handle);
                Options = view.findViewById(R.id.img_options);

            }
        }

        //**********************************************************************************************

    }
    //end
}
