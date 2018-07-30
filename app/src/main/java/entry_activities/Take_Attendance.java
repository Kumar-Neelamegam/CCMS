package entry_activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import adapters.Getter_Setter;

import core_modules.Task_Navigation;
import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/* Import ZBar Class files */


public class Take_Attendance extends AppCompatActivity {


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, ScanBarcode;

    //variable declaration

    Spinner batch;
    TextView sid, name;
    ToggleButton att;

    EditText Barcode_Str;

    Button Confirm,ContinueLater;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);


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
        Back = toolbar.findViewById(R.id.toolbar_back);
        Exit = toolbar.findViewById(R.id.ic_exit);

        batch = findViewById(R.id.spn_bat_list);

        Confirm= findViewById(R.id.btn_saveall);
        ContinueLater= findViewById(R.id.btn_continue_later);

        Confirm.setVisibility(View.GONE);
        ContinueLater.setVisibility(View.GONE);

        sid = findViewById(R.id.stu_id);
        name = findViewById(R.id.name);
        att = findViewById(R.id.btn_att_view);

        Barcode_Str= findViewById(R.id.edt_barcode);
        Barcode_Str.setVisibility(View.GONE);

        Handler myHandler = new Handler();
        Runnable myRun = new Runnable(){
            public void run()
            {
                Toast.makeText(Take_Attendance.this, "Barcode_Result: "+Barcode_Str.getText().toString(), Toast.LENGTH_SHORT).show();
                Barcode_Str.setText("");
                Barcode_Str.requestFocus();

            }
        };

        Barcode_Str.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                myHandler.removeCallbacks(myRun);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length()>0)
                {

                    UpdateAttendance(Barcode_Str.getText().toString());
                    LoadStudentsList(batch.getSelectedItem().toString(), 2);
                    myHandler.postDelayed(myRun,1000);
                }

            }
        });






        ScanBarcode = findViewById(R.id.btn_scanbarcode);


        ScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //scanBarcodeCustomOptions(v); //-- Working

                if (batch.getSelectedItemPosition() > 0) {
                    ScanBarContinuous();
                } else {
                        Baseconfig.SweetDialgos(3, Take_Attendance.this, "Information", "Select batch..", "OK");
                }


            }
        });


        //Loading batch list

        Baseconfig.LoadValuesSpinner(batch, Take_Attendance.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");


    }
    //**********************************************************************************************


    private static final String TAG = Take_Attendance.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    FButton CloseScan;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            //ImageView imageView = (ImageView) inflatedLayout.findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            UpdateAttendance(lastText);

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    public void UpdateAttendance(String BarCodeResult) {

        SQLiteDatabase db = Baseconfig.GetDb();
        ContentValues values = new ContentValues();
        values.put("Attendance_Date", Baseconfig.GetDate());
        values.put("Attendance_Status", "Present");
        values.put("IsActive", 1);
        values.put("IsUpdate", 1);//Present==1
        values.put("IsSMS_Sent", 0);
        db.update("Bind_Attendance", values, "SID='" + BarCodeResult + "' and Batch_Name like '%"+batch.getSelectedItem().toString()+"%' and ActDate='" + Baseconfig.GetDate() + "'", null);
        db.close();
        Log.e("Update Attendance: ", values + " / " + "SID='" + BarCodeResult + "'");

        //Barcode_Str.setText("");
        //Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
    }


    View inflatedLayout;

    void ScanBarContinuous() {

        LayoutInflater inflater = LayoutInflater.from(Take_Attendance.this);
        inflatedLayout = inflater.inflate(R.layout.popup_barcodescanner_continuos, null, false);
        Dialog builderDialog = new Dialog(Take_Attendance.this);

        barcodeView = inflatedLayout.findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
        barcodeView.resume();


        CloseScan = inflatedLayout.findViewById(R.id.btn_close);


        CloseScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builderDialog.dismiss();

                LoadStudentsList(batch.getSelectedItem().toString(), 2);
            }
        });

        builderDialog.setContentView(inflatedLayout);
        builderDialog.setCancelable(false);
        builderDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
        builderDialog.show();


    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }


    //**********************************************************************************************


    private void Controllisteners() {





        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Take_Attendance.this, Take_Attendance.class));


        batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (batch.getSelectedItemPosition() > 0) {

                    LoadStudentsList(batch.getSelectedItem().toString(), 1);
                    Barcode_Str.requestFocus();
                    Barcode_Str.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckAbsentList(recyclerView);
                Take_Attendance.this.finish();
                Intent back = new Intent(Take_Attendance.this, Task_Navigation.class);
                startActivity(back);

            }
        });

        ContinueLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Take_Attendance.this.finish();
                Intent back = new Intent(Take_Attendance.this, Task_Navigation.class);
                startActivity(back);
            }
        });



    }

    //**********************************************************************************************
    private RecyclerView recyclerView;

    void LoadStudentsList(String selected_batch, int Id) {

        /**
         * To load recycler view old dashboard
         */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Take_Attendance.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        ArrayList<Getter_Setter.TakeAttendance> DataItems = prepareData(selected_batch, Id);
        Take_Attendance_Adapter adapter = new Take_Attendance_Adapter(Take_Attendance.this, DataItems);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



    }

    //*********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.TakeAttendance> prepareData(String selected_batch, int Id) {

        ArrayList<Getter_Setter.TakeAttendance> Dataitems = new ArrayList<>();
        Getter_Setter.TakeAttendance obj;

        SQLiteDatabase db = Baseconfig.GetDb();

        String Query = "";
        if (Id == 1) {

          /*  String Query_InsertToAttendance = "Insert into Bind_Attendance (Batch_Name,SID,Attendance_Status,Attendance_Date,ActDate,IsUpdate,IsSMS_Sent)  " +
                    "select '"+selected_batch+"',SID,'N/A','" + Baseconfig.GetDate() + "','" + Baseconfig.GetDate() + "',0,0 " +
                    "from Bind_EnrollStudents WHERE NOT EXISTS(select * from Bind_Attendance where \n" +
                    "Bind_Attendance.SID=Bind_EnrollStudents.SID and ActDate='" + Baseconfig.GetDate() + "' and " +
                    "Batch_Name='"+selected_batch+"') and Batch_Info like '%"+selected_batch+"%' and IsActive='1' \n";*/

            String Query_InsertToAttendance = "Insert into Bind_Attendance (Batch_Name,SID,Attendance_Status,Attendance_Date,ActDate,IsUpdate,IsSMS_Sent)  " +
                    "select '"+selected_batch+"',SID,'Present','" + Baseconfig.GetDate() + "','" + Baseconfig.GetDate() + "',0,0 " +
                    "from Bind_EnrollStudents WHERE NOT EXISTS(select * from Bind_Attendance where \n" +
                    "Bind_Attendance.SID=Bind_EnrollStudents.SID and ActDate='" + Baseconfig.GetDate() + "' and " +
                    "Batch_Name='"+selected_batch+"') and Batch_Info like '%"+selected_batch+",%' and IsActive='1' \n";

            db.execSQL(Query_InsertToAttendance);

            Query = "select Id,(select Name from Bind_EnrollStudents b where b.SID=a.SID)as Name,SID,IFNULL(Attendance_Status,'Present')as Attendance_Status from " +
                    "Bind_Attendance a where Batch_Name like '%" + selected_batch + "%' and ActDate='" + Baseconfig.GetDate() + "' order by Name";

        } else if (Id == 2) {
            Query = "select Id,(select Name from Bind_EnrollStudents b where b.SID=a.SID)as Name,SID,IFNULL(Attendance_Status,'Present')as Attendance_Status from " +
                    "Bind_Attendance a where Batch_Name like '%" + selected_batch + "%' and ActDate='" + Baseconfig.GetDate() + "' order by Name";
        }


        //Query="select Id,(select Name from Bind_EnrollStudents b where b.SID=a.SID)as Name,SID,IFNULL(Attendance_Status,'Absent')as Attendance_Status from Temp_Attendance a where Batch_Name='" + selected_batch + "'";// and substr(ActDate,0,11)='"+Baseconfig.GetDate_YYYY_DD_MM()+"'";


        Log.e("Loading Query: ", Query);
        Log.e("Loading Query: ", Query);

        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    obj = new Getter_Setter.TakeAttendance();
                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setName(c.getString(c.getColumnIndex("Name")));
                    obj.setSID(c.getString(c.getColumnIndex("SID")));
                    obj.setStatus(c.getString(c.getColumnIndex("Attendance_Status")));

                    Dataitems.add(obj);

                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();

        if(Dataitems.size()>0)
        {
            Confirm.setVisibility(View.VISIBLE);
            ContinueLater.setVisibility(View.VISIBLE);
        }

        return Dataitems;

    }


    //**********************************************************************************************


    public void LoadBack() {


        if(recyclerView==null)
        {

            Take_Attendance.this.finish();
            Intent back = new Intent(Take_Attendance.this, Task_Navigation.class);
            startActivity(back);
        }
        else if(recyclerView!=null && recyclerView.getAdapter().getItemCount()>0)
        {
            new SweetAlertDialog(Take_Attendance.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Take_Attendance.this.getResources().getString(R.string.information))
                    .setContentText("Do you want continue to take attendance later?(Press Yes)\n\nOtherwise (Press No) - " +
                            "To finalize the attendance entry..")
                    .setCancelText(Take_Attendance.this.getResources().getString(R.string.no))
                    .setConfirmText(Take_Attendance.this.getResources().getString(R.string.yes))
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.dismiss();
                            /*CheckAbsentList(recyclerView);
                            Take_Attendance.this.finish();
                            Intent back = new Intent(Take_Attendance.this, Task_Navigation.class);
                            startActivity(back);*/

                            Confirm.performClick();


                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.dismiss();
                            Take_Attendance.this.finish();
                            Intent back = new Intent(Take_Attendance.this, Task_Navigation.class);
                            startActivity(back);
                        }
                    })
                    .show();

        }



    }



    void CheckAbsentList(RecyclerView recyclerView)
    {


                int childCount = recyclerView.getChildCount();
        int i;
        for (i = 0; i < childCount; i++)
        {
            if (recyclerView.findViewHolderForLayoutPosition(i) instanceof Take_Attendance_Adapter.ViewHolder)
            {
                Take_Attendance_Adapter.ViewHolder childHolder = (Take_Attendance_Adapter.ViewHolder)
                        recyclerView.findViewHolderForLayoutPosition(i);

                String Id=childHolder.Sno.getText().toString();
                String SID = childHolder.SID.getText().toString();
                //boolean Attendance_Status = childHolder.Status.isChecked();
                String Txt_Status=childHolder.Txt_Value.getText().toString();

                //Toast.makeText(this, "Toggle: "+Id+"/"+SID+"/"+String.valueOf(Attendance_Status), Toast.LENGTH_SHORT).show();
                Log.e("CheckAbsentList: ",  Id+"/"+SID+"/"+String.valueOf(Txt_Status));

                if(Txt_Status.toString().equalsIgnoreCase("Absent"))//==false
                {
                    SQLiteDatabase db = Baseconfig.GetDb();
                    ContentValues values = new ContentValues();
                    values.put("Attendance_Date", Baseconfig.GetDate());
                    values.put("Attendance_Status", "Absent");
                    values.put("IsActive", 1);
                    values.put("IsUpdate", 2);//ABSENT==2
                    values.put("IsSMS_Sent", 0);
                    db.update("Bind_Attendance", values, "SID='" + SID + "' and Batch_Name='"+batch.getSelectedItem().toString()+"' and ActDate='" + Baseconfig.GetDate() + "'", null);
                    db.close();
                    Log.e("Update Attendance: ", values + " / " + "SID='" + SID + "'");

                }else if(Txt_Status.toString().equalsIgnoreCase("Present"))//==false
                {
                    SQLiteDatabase db = Baseconfig.GetDb();
                    ContentValues values = new ContentValues();
                    values.put("Attendance_Date", Baseconfig.GetDate());
                    values.put("Attendance_Status", "Present");
                    values.put("IsActive", 1);
                    values.put("IsUpdate", 1);//PRESENT==1
                    values.put("IsSMS_Sent", 0);
                    db.update("Bind_Attendance", values, "SID='" + SID + "' and Batch_Name='"+batch.getSelectedItem().toString()+"' and ActDate='" + Baseconfig.GetDate() + "'", null);
                    db.close();
                    Log.e("Update Attendance: ", values + " / " + "SID='" + SID + "'");

                }

            }
        }




    }

    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    //End

    //**********************************************************************************************

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    public class Take_Attendance_Adapter extends RecyclerView.Adapter<Take_Attendance_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.TakeAttendance> DataItems;
        private Context context;


        //**********************************************************************************************

        public Take_Attendance_Adapter(Context context, ArrayList<Getter_Setter.TakeAttendance> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Take_Attendance_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendance_rowitem, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Take_Attendance_Adapter.ViewHolder viewHolder, final int i) {


            viewHolder.Sno.setText((i + 1) + ".");
            viewHolder.Name.setText(DataItems.get(i).getName());
            viewHolder.SID.setText(DataItems.get(i).getSID());

            String Att_Str=DataItems.get(i).getStatus().toString();
            if(Att_Str.equalsIgnoreCase("Present"))
            {
                viewHolder.Txt_Value.setText(Att_Str);
                viewHolder.Txt_Value.setTextColor(getResources().getColor(R.color.green));

            }else if(Att_Str.equalsIgnoreCase("Absent"))
            {
                viewHolder.Txt_Value.setText(Att_Str);
                viewHolder.Txt_Value.setTextColor(getResources().getColor(R.color.red));
            }else if(Att_Str.equalsIgnoreCase("N/A"))
            {
                viewHolder.Txt_Value.setText(Att_Str);
                viewHolder.Txt_Value.setTextColor(getResources().getColor(R.color.fbutton_color_orange));
            }


            viewHolder.Txt_Value.setOnClickListener(new View.OnClickListener() {
                int count = 0;
                @Override
                public void onClick(View view) {

                    if (count == 1) {
                        count = 0;
                        viewHolder.Txt_Value.setText("Absent");
                        viewHolder.Txt_Value.setTextColor(getResources().getColor(R.color.red));
                    }
                    else
                    {
                        viewHolder.Txt_Value.setText("Present");
                        viewHolder.Txt_Value.setTextColor(getResources().getColor(R.color.green));
                        count++;
                    }


                }
            });


            /*if (DataItems.get(i).getStatus().toString().equalsIgnoreCase("Present")) {
                viewHolder.Status.setChecked(true);
            } else {
                viewHolder.Status.setChecked(false);
            }*/


        }
        //**********************************************************************************************

        @Override
        public int getItemCount() {
            return DataItems.size();
        }

        //**********************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView Sno;
            TextView Name, SID;
            EditText Txt_Value;
            ToggleButton Status;

            public ViewHolder(View view) {
                super(view);

                Sno = view.findViewById(R.id.serial_no);
                Name = view.findViewById(R.id.name);
                SID = view.findViewById(R.id.stu_id);
                Status = view.findViewById(R.id.btn_att_view);
                Txt_Value= view.findViewById(R.id.sample_txt);

            }
        }

        //**********************************************************************************************

    }


    /**
     * ZBAR CODE SCANNER
     *
     * @return
     */


    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    public void scanBarcodeCustomOptions(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Sample of scanning from a Fragment
     */
    public static class ScanFragment extends Fragment {
        private String toast;

        public ScanFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            displayToast();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.popup_barcodescanner, container, false);
            Button scan = view.findViewById(R.id.ScanButton);
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanFromFragment();
                }
            });
            return view;
        }

        public void scanFromFragment() {
            IntentIntegrator.forFragment(this).initiateScan();
        }

        private void displayToast() {
            if (getActivity() != null && toast != null) {
                Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                toast = null;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    toast = "Cancelled from fragment";
                } else {
                    toast = "Scanned from fragment: " + result.getContents();
                }

                // At this point we may or may not have a reference to the activity
                displayToast();
            }
        }
    }
    //*****************************************

    //End
}
