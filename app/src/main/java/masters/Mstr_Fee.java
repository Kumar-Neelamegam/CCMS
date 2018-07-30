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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;

import adapters.Getter_Setter;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/20/2017.
 */

public class Mstr_Fee extends AppCompatActivity {


    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    Spinner subject;
    EditText fee;

  FButton clear, submit;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_fee);

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

        subject = findViewById(R.id.spn_mst_sub);
        fee = findViewById(R.id.edt_fees);

        clear = findViewById(R.id.btn_clear);
        submit = findViewById(R.id.btn_sub);

        Baseconfig.LoadValuesSpinner(subject, Mstr_Fee.this, "select distinct Subject_Name as dvalue from Mstr_Subject  where IsActive='1';", "Select");
        //LoadValues(medihistrytremt, context, "select distinct treatmentfor as dvalue from trmntfor where (isactive='True' or isactive='true' or isactive='1') order by treatmentfor;", 1);

        LoadRecyler();

    }
    //**********************************************************************************************

    private void Controllisteners() {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, Mstr_Fee.class));

        submit.setOnClickListener(view -> {


            if (CheckValidations()) {

                if (submit.getText().toString().equalsIgnoreCase("Submit"))//Submit
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Fee where Subject_Name='" + subject.getSelectedItem().toString() + "' and Fee='" + fee.getText().toString() + "' and IsActive='1'");
                    if (!b) {
                        SaveLocal();
                    } else {

                        Baseconfig.SweetDialgos(4, Mstr_Fee.this, "Information", " Duplicate entry.. already subject and fee exists in list", "OK");
                        Clear();
                    }


                } else if (submit.getText().toString().equalsIgnoreCase("Update"))//Update
                {
                    boolean b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Fee where Subject_Name='" + subject.getSelectedItem().toString() + "' and Fee='" + fee.getText().toString() + "' and IsActive='1'");
                    if (!b) {
                        //condition venum chk paniko
                        UpdateLocal();
                    } else {

                        //Baseconfig.SweetDialgos(4, Mstr_Subject.this, "Information", " Duplicate entry.. already subject (" + subject.getText().toString() + ") exists in list", "OK");

                        ShowDuplicateAlert();


                    }
                }

            } else {

                Baseconfig.SweetDialgos(4, Mstr_Fee.this, "Information", " Please fill mandatory fields marked with (*) ", "OK");
            }


        });
        clear.setOnClickListener(view -> Clear());

    }


    void ShowDuplicateAlert() {
        new SweetAlertDialog(Mstr_Fee.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Mstr_Fee.this.getResources().getString(R.string.information))
                .setContentText(" Duplicate entry.. already subject and fee exists in list")
                .setCancelText(Mstr_Fee.this.getResources().getString(R.string.no))
                .setConfirmText(Mstr_Fee.this.getResources().getString(R.string.yes))
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


    public void UpdateLocal() {


        String Str_Subject = "", Str_Fee = "", Str_SubId = "", Str_Isactive = "1";

        boolean b = Baseconfig.CheckSpinner(subject);
        if (b) {
            Str_Subject = subject.getSelectedItem().toString();
        } else {
            Str_Subject = "N/A";
        }

        Str_SubId = Baseconfig.LoadValue("select Id as dstatus from Mstr_Subject where Subject_Name='" + Str_Subject + "'");

        Str_Fee = fee.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Subject_Name", Str_Subject);
        values.put("Subject_Id", Str_SubId);
        values.put("Fee", Str_Fee);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "1");
        values.put("ActDate", Baseconfig.GetDate());
        db.update("Mstr_Fee", values, "Id='" + LOCAL_UPDATE_ID + "'", null);
        db.close();

        ShowSuccessDialog("Data updated to masters successfully..");
        submit.setText("Submit");
    }


    //**********************************************************************************************

    public boolean CheckValidations() {
        boolean ret = true;

        if (subject.getSelectedItemPosition() == 0) {
            ret = false;
        }

        if (fee.getText().toString().length() == 0) {
            ret = false;
        }

        return ret;
    }


    //**************************************************************************************
    public void SaveLocal() {

        String Str_Subject = "", Str_Fee = "", Str_SubId = "", Str_Isactive = "1";

        boolean b = Baseconfig.CheckSpinner(subject);
        if (b) {
            Str_Subject = subject.getSelectedItem().toString();
        } else {
            Str_Subject = "N/A";
        }

        Str_SubId = Baseconfig.LoadValue("select Id as dstatus from Mstr_Subject where Subject_Name='" + Str_Subject + "'");

        Str_Fee = fee.getText().toString();

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Subject_Name", Str_Subject);
        values.put("Subject_Id", Str_SubId);
        values.put("Fee", Str_Fee);
        values.put("IsActive", Str_Isactive);
        values.put("IsUpdate", "0");
        values.put("ActDate", Baseconfig.GetDate());
        db.insert("Mstr_Fee", null, values);
        db.close();

        ShowSuccessDialog("Data added to masters successfully..");

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

        subject.setSelection(0);
        fee.setText("");
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_Fee.this, 1);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_Fee> DataItems = prepareData();
        Mstr_Fee_Adapter adapter = new Mstr_Fee_Adapter(Mstr_Fee.this, DataItems);
        recyclerView.setAdapter(adapter);

    }
    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_Fee> prepareData() {

        ArrayList<Getter_Setter.Mstr_Fee> Dataitems = new ArrayList<>();
        Getter_Setter.Mstr_Fee obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Mstr_Fee where IsActive='1'";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.Mstr_Fee();

                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setSubject(c.getString(c.getColumnIndex("Subject_Name")));
                    obj.setSubjectId(c.getString(c.getColumnIndex("Subject_Id")));
                    obj.setFee(c.getString(c.getColumnIndex("Fee")));

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
    String OLD_FEE;

    public class Mstr_Fee_Adapter extends RecyclerView.Adapter<Mstr_Fee_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.Mstr_Fee> DataItems;
        private Context context;


        //**********************************************************************************************

        public Mstr_Fee_Adapter(Context context, ArrayList<Getter_Setter.Mstr_Fee> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Mstr_Fee_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_fee_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_Fee_Adapter.ViewHolder viewHolder, final int i) {

            //Sample - viewHolder.Title.setText(DataItems.get(i).getTitle_Name());

            //set values here
            viewHolder.Subject_Name.setText(DataItems.get(i).getSubject());
            viewHolder.Fee.setText(DataItems.get(i).getFee());
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

                                if (subject.getAdapter() != null) {
                                    for (int j = 0; j <= subject.getAdapter().getCount() - 1; j++) {
                                        String compareValue = subject.getAdapter().getItem(j).toString();

                                        if (compareValue.equalsIgnoreCase(subjectname)) {
                                            subject.setSelection(j);


                                        }
                                    }
                                }

                                fee.setText(DataItems.get(i).getFee());
                                submit.setText("Update");
                                LOCAL_UPDATE_ID = DataItems.get(i).getId();
                                OLD_SUBJECT = DataItems.get(i).getSubject();
                                OLD_FEE = DataItems.get(i).getFee();


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
            new SweetAlertDialog(Mstr_Fee.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Mstr_Fee.this.getResources().getString(R.string.information))
                    .setContentText("Are you sure want to delete?")
                    .setCancelText(Mstr_Fee.this.getResources().getString(R.string.no))
                    .setConfirmText(Mstr_Fee.this.getResources().getString(R.string.yes))
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
                            db.execSQL("Delete from Mstr_Fee where Id='" + DataItems.get(position).getId() + "'");
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

            TextView Subject_Name, Fee, Sno;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls
                Subject_Name = view.findViewById(R.id.txt_subject_name);
                Fee = view.findViewById(R.id.txt_fees);
                Sno = view.findViewById(R.id.txt_sno);
                Options = view.findViewById(R.id.img_options);
            }
        }

        //**********************************************************************************************

    }
//End
}