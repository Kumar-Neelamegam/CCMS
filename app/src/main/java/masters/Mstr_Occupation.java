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

public class Mstr_Occupation extends AppCompatActivity {


    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    EditText Occupation;
    FButton clear, submit;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_occupation);


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

        Occupation = findViewById(R.id.edt_occ);

        clear = findViewById(R.id.btn_cancel);
        submit = findViewById(R.id.btn_submit);

        LoadRecyler();

    }
    //**********************************************************************************************

    private void Controllisteners() {
        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, Mstr_Occupation.class));

        submit.setOnClickListener(view -> {

            if (CheckValidations()) {

                if (submit.getText().toString().equalsIgnoreCase("Submit"))//Submit
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Occupation where Occupation_Name='" + Occupation.getText().toString() + "' and IsActive='1'");
                    if (!b) {
                        SaveLocal();
                    } else {

                        Baseconfig.SweetDialgos(4, Mstr_Occupation.this, "Information", " Duplicate entry.. already occupation exists in list", "OK");
                        Clear();
                    }
                } else if (submit.getText().toString().equalsIgnoreCase("Update"))//Update
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Occupation where Occupation_Name='" + Occupation.getText().toString() + "' and IsActive='1'");
                    if (!b) {
                        //condition venum chk paniko
                        UpdateLocal();
                    } else {

                        //Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");

                        ShowDuplicateAlert();

                    }
                }
            } else {

                Baseconfig.SweetDialgos(4, Mstr_Occupation.this, "Information", " Please fill mandatory fields marked with (*) ", "OK");
            }


        });

        clear.setOnClickListener(view -> Clear());

    }


    void ShowDuplicateAlert() {
        new SweetAlertDialog(Mstr_Occupation.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Mstr_Occupation.this.getResources().getString(R.string.information))
                .setContentText(" Duplicate entry.. already occupation exists in list")
                .setCancelText(Mstr_Occupation.this.getResources().getString(R.string.no))
                .setConfirmText(Mstr_Occupation.this.getResources().getString(R.string.yes))
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

        if ((Occupation.getText().toString().length() == 0)) {
            ret = false;
        }


        return ret;
    }


    //**************************************************************************************
    public void SaveLocal() {

        String Str_Occupation = "", Str_Isactive = "1";


        Str_Occupation = Occupation.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();

        values.put("Occupation_Name", Str_Occupation);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "0");
        values.put("ServerIsUpdate",0);
        values.put("ActDate", Baseconfig.GetDate());   values.put("FUID", Baseconfig.App_UID);

        db.insert("Mstr_Occupation", null, values);
        db.close();
        ShowSuccessDialog("Data added to masters successfully..");

    }

    public void UpdateLocal() {

        String Str_Occupation = "", Str_Isactive = "1";


        Str_Occupation = Occupation.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();

        values.put("Occupation_Name", Str_Occupation);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "1");
        values.put("ActDate", Baseconfig.GetDate());
        db.update("Mstr_Occupation", values, "Id='" + LOCAL_UPDATE_ID + "'", null);
        db.close();

        ShowSuccessDialog("Data updated to masters successfully..");
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

    public void Clear() {

        Occupation.setText("");
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_Occupation.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_Occupation> DataItems = prepareData();
        Mstr_Occupation_Adapter adapter = new Mstr_Occupation_Adapter(Mstr_Occupation.this, DataItems);
        recyclerView.setAdapter(adapter);

    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_Occupation> prepareData() {

        ArrayList<Getter_Setter.Mstr_Occupation> Dataitems = new ArrayList<>();
        Getter_Setter.Mstr_Occupation obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Mstr_Occupation where IsActive='1'";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.Mstr_Occupation();

                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setOccupation(c.getString(c.getColumnIndex("Occupation_Name")));

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
    String OLD_OCCUPATION;


    public class Mstr_Occupation_Adapter extends RecyclerView.Adapter<Mstr_Occupation_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.Mstr_Occupation> DataItems;
        private Context context;


        //**********************************************************************************************

        public Mstr_Occupation_Adapter(Context context, ArrayList<Getter_Setter.Mstr_Occupation> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Mstr_Occupation_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_occupation_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_Occupation_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here

            viewHolder.Sno.setText((i + 1) + ".");
            viewHolder.Occupation.setText(DataItems.get(i).getOccupation());


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

                                Occupation.setText(DataItems.get(i).getOccupation());
                                submit.setText("Update");
                                LOCAL_UPDATE_ID = DataItems.get(i).getId();
                                OLD_OCCUPATION = DataItems.get(i).getOccupation();

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
            new SweetAlertDialog(Mstr_Occupation.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Mstr_Occupation.this.getResources().getString(R.string.information))
                    .setContentText("Are you sure want to delete?")
                    .setCancelText(Mstr_Occupation.this.getResources().getString(R.string.no))
                    .setConfirmText(Mstr_Occupation.this.getResources().getString(R.string.yes))
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
                            db.execSQL("Delete from Mstr_Occupation where Id='" + DataItems.get(position).getId() + "'");
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

            TextView Occupation, Sno;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls
                Sno = view.findViewById(R.id.txt_sno);
                Occupation = view.findViewById(R.id.txt_occupation);
                Options = view.findViewById(R.id.img_options);

            }
        }

        //**********************************************************************************************

    }
    //end
}
