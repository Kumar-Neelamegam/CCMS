package student_profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import core_modules.Task_Navigation;
import entry_activities.Enroll_Students;
import utilities.Baseconfig;
import utilities.Imageutils;
import utilities.Validation1;
import vcc.coremodule.R;

import static utilities.Baseconfig.GetDb;

/**
 * Created by KUMAR on 6/14/2017.
 */

public class Student_Profile_Edit extends AppCompatActivity implements Imageutils.ImageAttachmentListener {


    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit;

    //variable declaration

    ImageView photo;
    ImageButton camera;
    AutoCompleteTextView school, occupation,mother_occupation;
    AppCompatEditText name, dob, fat_name, mot_name, address, mobile, subject,choose_batch, cgpa, fee, advance, joining_date,board_examno;
    RadioButton female, male,rbtn_10th,rbtn_11th,rbtn_12th;

    Button cancel, submit;

    Imageutils imageutils;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_students);


        try {


            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    //**********************************************************************************************
    String SID;

    private void GetInitialize() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Back = findViewById(R.id.toolbar_back);
        Exit = findViewById(R.id.ic_exit);

        Bundle b = getIntent().getExtras();
        SID = b.getString("SID");


        photo = findViewById(R.id.img_photo);
        camera = findViewById(R.id.imgbtn_capture1);

        name        = findViewById(R.id.edt_name);
        dob         = findViewById(R.id.edt_dob);
        fat_name    = findViewById(R.id.edt_fatherrname);
        mot_name    = findViewById(R.id.edt_motname);
        occupation  = (AppCompatAutoCompleteTextView) findViewById(R.id.edt_occupation);
        occupation.setThreshold(1);
        mother_occupation=(AppCompatAutoCompleteTextView)findViewById(R.id.edt_mother_occupation);
        mother_occupation.setThreshold(1);
        address     = findViewById(R.id.edt_address);
        mobile      = findViewById(R.id.edt_mobileno);
        subject     = findViewById(R.id.edt_subject);
        choose_batch= findViewById(R.id.edt_batch);
        school      = (AppCompatAutoCompleteTextView) findViewById(R.id.edt_school);
        school.setThreshold(1);
        cgpa        = findViewById(R.id.edt_tenth);
        fee         = findViewById(R.id.edt_fee);
        advance     = findViewById(R.id.edt_advance);
        board_examno    = findViewById(R.id.edt_examno);

        female = findViewById(R.id.radi_female);
        male = findViewById(R.id.radi_male);

        //batch = (Spinner) findViewById(R.id.spn_batch);


        cancel = findViewById(R.id.btn_cancel);
        submit = findViewById(R.id.btn_submit);
        submit.setText("Update");


        rbtn_10th= findViewById(R.id.rbtn_10th);
        rbtn_11th= findViewById(R.id.rbtn_11th);
        rbtn_12th= findViewById(R.id.rbtn_12th);
        joining_date= findViewById(R.id.edt_joiningdate);

        //Loading batch list
        String str = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
        str = dateformat.format(c.getTime());

        //Loading autocomplete school name
        Baseconfig.LoadValues(school, Student_Profile_Edit.this, "select distinct School_Name as dvalue from Mstr_School where IsActive='1' order by School_Name;");


        //Loading autocomplete occupation name
        Baseconfig.LoadValues(occupation, Student_Profile_Edit.this, "select distinct Occupation_Name as dvalue from Mstr_Occupation where IsActive='1' order by Occupation_Name;");
        Baseconfig.LoadValues(mother_occupation, Student_Profile_Edit.this, "select distinct Occupation_Name as dvalue from Mstr_Occupation where IsActive='1' order by Occupation_Name;");


        try {
            LoadStudent_Details();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //**********************************************************************************************

    public void LoadStudent_Details()
    {

        try {
            String Str_StudentId="",Str_StudentName="",Str_Gender="",Str_DOB="",Str_FatherName="",
                    Str_FatherOccupation="",Str_MotherName="",Str_MotherOccupation="",
                    Str_Address="",Str_MobileNo="",Str_Subject="",Str_BatchInfo="",Str_SchoolName="",Str_CGPA="",
                    Str_CoachingFee="",Str_FeeAdvance="",Str_FeePaid="-",Str_Photo="",Str_ExamNo="";

            String Str_Standard="",Str_JoiningDate="";

            String Ret_Value="";
            SQLiteDatabase db= Baseconfig.GetDb();
            String Query="select * from Bind_EnrollStudents where SID='"+SID+"'";
            Cursor c=db.rawQuery(Query,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do{

                        Str_StudentId=c.getString(c.getColumnIndex("SID"));
                        Str_StudentName=c.getString(c.getColumnIndex("Name"));
                        Str_Gender=c.getString(c.getColumnIndex("Gender"));
                        Str_DOB=c.getString(c.getColumnIndex("DOB"));
                        Str_FatherName=c.getString(c.getColumnIndex("Father_Name"));
                        Str_FatherOccupation=c.getString(c.getColumnIndex("Father_Occupation"));
                        Str_MotherName=c.getString(c.getColumnIndex("Mother_Name"));
                        Str_MotherOccupation=c.getString(c.getColumnIndex("Mother_Occupation"));
                        Str_Address=c.getString(c.getColumnIndex("Address"));
                        Str_MobileNo=c.getString(c.getColumnIndex("Mobile_Number"));
                        Str_Subject=c.getString(c.getColumnIndex("Subject"));
                        Str_BatchInfo=c.getString(c.getColumnIndex("Batch_Info"));
                        Str_SchoolName=c.getString(c.getColumnIndex("School_Name"));
                        Str_CGPA=c.getString(c.getColumnIndex("CGPA"));
                        Str_CoachingFee=c.getString(c.getColumnIndex("Coaching_Fee"));
                        Str_FeeAdvance=c.getString(c.getColumnIndex("Fee_Advance"));
                        Str_Photo=c.getString(c.getColumnIndex("Photo"));
                        Str_ExamNo=c.getString(c.getColumnIndex("BoardExam_No"));
                        Str_Standard=c.getString(c.getColumnIndex("Standard"));
                        Str_JoiningDate=c.getString(c.getColumnIndex("Joining_Date"));

                        String[] arr = c.getString(c.getColumnIndex("Subject")).split(",");
                        if (arr.length > 0) {
                            for (int h = 0; h < arr.length; h++) {

                                selectedColours2.add(arr[h]);
                            }
                        }

                        arr = c.getString(c.getColumnIndex("Batch_Info")).split(",");
                        if (arr.length > 0) {
                            for (int h = 0; h < arr.length; h++) {

                                selectedColours3.add(arr[h]);
                            }
                        }

                    }while (c.moveToNext());

                }

            }


            c.close();
            db.close();


            if(Str_Photo.toString().length()>0)
            {
                Glide.with(photo.getContext()).load(new File(Str_Photo)).into(photo);

            }
            else
            {
                Glide.with(Student_Profile_Edit.this).load(Uri.parse("file:///android_asset/male_avatar.png")).into(photo);
            }

            Baseconfig.StudentImgPath=Str_Photo;

            name.setText(Str_StudentName);
            if(Str_Gender.toString().equalsIgnoreCase("Male"))
            {
                male.setChecked(true);
            }
            else
            {
                female.setChecked(true);
            }
            dob.setText(Str_DOB);
            fat_name.setText(Str_FatherName);
            mot_name.setText(Str_MotherName);
            mother_occupation.setText(Str_MotherOccupation);
            occupation.setText(Str_FatherOccupation);
            address.setText(Str_Address);
            mobile.setText(Str_MobileNo);
            subject.setText(Str_Subject);
            school.setText(Str_SchoolName);
            cgpa.setText(Str_CGPA);
            fee.setText(Str_CoachingFee);
            advance.setText(Str_FeeAdvance);
            fee.setEnabled(false);
            advance.setEnabled(false);
            board_examno.setText(Str_ExamNo);
            choose_batch.setText(Str_BatchInfo);
            joining_date.setText(Str_JoiningDate);

            if(Str_Standard.toString().equalsIgnoreCase("10th"))
            {
                rbtn_10th.setChecked(true);
            }
            else if(Str_Standard.toString().equalsIgnoreCase("11th"))
            {
                rbtn_11th.setChecked(true);
            }else if(Str_Standard.toString().equalsIgnoreCase("12th"))
            {
                rbtn_12th.setChecked(true);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //**********************************************************************************************
    /*
    Date picker
     */

    private void SelectDate2() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        showDialog(DATE_DIALOG_ID2);


    }


    private DatePickerDialog.OnDateSetListener pickerListener2 = new DatePickerDialog.OnDateSetListener() {

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
            joining_date.setText(dateString.toString());
            Log.e("Selected DOB: ", dateString.toString());
        }


    };

    //**********************************************************************************************

    private void Controllisteners() {

        joining_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDate2();
            }
        });

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Student_Profile_Edit.this, Enroll_Students.class));

        cancel.setOnClickListener(view -> LoadBack());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {

                    SaveLocal();

                } else {

                    Baseconfig.SweetDialgos(4, Student_Profile_Edit.this, "Information", " Please fill all mandatory fields marked with (*)\n"+strError.toString(), "OK");
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageutils =new Imageutils(Student_Profile_Edit.this);
                imageutils.imagepicker(1);
            }
        });

        male.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                female.setChecked(false);
            }
        });

        female.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                male.setChecked(false);
            }
        });

        rbtn_10th.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    rbtn_12th.setChecked(false);
                    rbtn_11th.setChecked(false);
                }
            }
        });

        rbtn_11th.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    rbtn_12th.setChecked(false);
                    rbtn_10th.setChecked(false);
                }
            }
        });

        rbtn_12th.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    rbtn_11th.setChecked(false);
                    rbtn_10th.setChecked(false);
                }
            }
        });


        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadSubjects();

            }
        });

        choose_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoadBatch();

            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectDate();

            }
        });

    }


    //**********************************************************************************************

    CharSequence[] items;
    CharSequence[] items2;


    void LoadSubjects() {


        SQLiteDatabase db = GetDb();

        Cursor c = db.rawQuery("select distinct Subject_Name as dvalue from Mstr_Subject where IsActive='1'", null);
        List<String> list = new ArrayList<String>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String counrtyname = c.getString(c.getColumnIndex("dvalue"));

                    list.add(counrtyname);

                } while (c.moveToNext());
            }
        }
        c.close();
        db.close();

        items = list.toArray(new String[list.size()]);
        LoadMultiChoiceSubject();
    }


    //==============================================================================================

    public ArrayList<CharSequence> selectedColours2 = new ArrayList<CharSequence>();
    public ArrayList<CharSequence> selectedColours3 = new ArrayList<CharSequence>();

    void LoadMultiChoiceSubject() {

        boolean[] checkedColours = new boolean[items.length];
        int count = items.length;

        for (int i = 0; i < count; i++)
            checkedColours[i] = selectedColours2.contains(items[i]);

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
                if (isChecked) {
                    selectedColours2.add(items[which]);

                } else {
                    selectedColours2.remove(items[which]);
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Student_Profile_Edit.this);
        builder.setTitle("Select Subject");

        builder.setMultiChoiceItems(items, checkedColours,
                coloursDialogListener);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();

                for (CharSequence colour : selectedColours2) {
                    stringBuilder.append(colour + ",");

                }

                subject.setText(stringBuilder.toString());
                choose_batch.setText("");
                selectedColours3 = new ArrayList<CharSequence>();
                Toast.makeText(Student_Profile_Edit.this, "Select batch..", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new StringBuilder();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //==============================================================================================


    void LoadBatch() {



        SQLiteDatabase db = GetDb();
        String Query = "";
        Cursor c;
        List<String> list = new ArrayList<String>();


        if (subject.getText().length() > 0) {

            String[] allsubjects = subject.getText().toString().split(",");

            for (int i = 0; i < allsubjects.length; i++) {
                Query = "select a.Batch_Name as dvalue from Mstr_Batch a inner join Mstr_Subject b on a.Subject_Id=b.Id\n" +
                        "        where a.Subject_Name='" + allsubjects[i].toString() + "' and a.IsActive='True' group by a.Batch_Name,a.Subject_Name;";

                Log.e("LoadBatch Query: ", Query);

                c = db.rawQuery(Query, null);
                //list = new ArrayList<String>();

                if (c != null) {
                    if (c.moveToFirst()) {
                        do {
                            String counrtyname = c.getString(c.getColumnIndex("dvalue"));

                            list.add(counrtyname);

                        } while (c.moveToNext());
                    }
                }
                c.close();
            }

            db.close();

            items2 = list.toArray(new String[list.size()]);

            //selectedColours3 = new ArrayList<CharSequence>();

            LoadBatchInfo();

        } else {
            Baseconfig.SweetDialgos(3, this, "Information", "Select Subject..", "OK");

            return;
        }

    }

    void LoadBatchInfo() {

        boolean[] checkedColours = new boolean[items2.length];
        int count = items2.length;

        for (int i = 0; i < count; i++)
            checkedColours[i] = selectedColours3.contains(items2[i]);

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which,
                                boolean isChecked) {
                if (isChecked) {
                    selectedColours3.add(items2[which]);

                } else {
                    selectedColours3.remove(items2[which]);
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Student_Profile_Edit.this);
        builder.setTitle("Select Batch");

        builder.setMultiChoiceItems(items2, checkedColours,
                coloursDialogListener);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();

                for (CharSequence colour : selectedColours3) {
                    stringBuilder.append(colour + ",");

                }

                choose_batch.setText(stringBuilder.toString());

            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new StringBuilder();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    //**********************************************************************************************
    /*
    Date picker
     */
    static final int DATE_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID2 = 2;
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

    DatePickerDialog dialog1;
    DatePickerDialog dialog2;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                //start changes...
                dialog1 = new DatePickerDialog(this, pickerListener, year, month, day);
                dialog1.getDatePicker().setMaxDate(System.currentTimeMillis());
                return dialog1;

            case DATE_DIALOG_ID2:

                dialog2 = new DatePickerDialog(this, pickerListener2, year, month, day);
                //dialog2.getDatePicker().setMaxDate(System.currentTimeMillis());
                return dialog2;

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, selectedMonth, selectedDay);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String values = format.format(calendar.getTime());
            // Show selected date
            //tringBuilder values=new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" ");
            dob.setText(values.toString());
            Log.e("Selected DOB: ", values.toString());
        }


    };
    //**********************************************************************************************
    StringBuilder strError;
    public boolean checkValidation() {
        boolean ret = true;

        strError=new StringBuilder();

        /*if (!Validation1.hasText(fee)) {
            ret = false;
        }

        if (!Validation1.hasText(cgpa)) {
            ret = false;
        }


        if (!Validation.hasText(school)) {
            ret = false;
        }

        if (batch.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Choose batch..", Toast.LENGTH_LONG).show();
            ret = false;
        }

        if (!Validation1.hasText(subject)) {
            ret = false;
        }

        if (!Validation1.isMobileNumber(mobile, true)) {
            ret = false;
        }

        if (male.isChecked() == false && female.isChecked() == false) {
            Toast.makeText(this, "Select gender..", Toast.LENGTH_LONG).show();
            ret = false;
        }

        if (!Validation1.isName(name, true)) {
            ret = false;
        }*/


        if (fee.getText().length()==0) {

            strError.append("Fee*\n");
            ret = false;
        }

      /*  if (cgpa.getText().length()==0) {
            strError.append("CGPA*\n");
            ret = false;
        }*/

        if (joining_date.getText().length() == 0) {
            strError.append("Joining Date*\n");
            ret = false;
        }


        if (rbtn_10th.isChecked() == false && rbtn_11th.isChecked() == false && rbtn_12th.isChecked() == false) {
            strError.append("Standard*\n");
            ret = false;
        }

        if (school.getText().length()==0) {
            strError.append("School Name*\n");
            ret = false;
        }

        if (choose_batch.getText().length()==0) {
            strError.append("Batch*\n");
            ret = false;
        }


        if (subject.getText().length()==0) {
            strError.append("Subject*\n");
            ret = false;
        }

        if (!Pattern.matches("\\d{10}", mobile.getText().toString())) {
            strError.append("Mobile No*\n");
            ret = false;
        }

        if (male.isChecked() == false && female.isChecked() == false) {
            strError.append("Gender*\n");
            ret = false;
        }
        if (fat_name.getText().length() == 0) {
            strError.append("Father Name*\n");
            ret = false;
        }
        if (dob.getText().length()==0) {
            strError.append("Date of birth*\n");
            ret = false;
        }

        if (!Validation1.isName(name, true)) {
            strError.append("Student Name*\n");
            ret = false;
        }


        return ret;

    }
    //**********************************************************************************************
    String barcode_data="";

    public void SaveLocal() {


        //String LOCAL_SID=Baseconfig.LoadValue("select SID as dstatus from Bind_EnrollStudents where SID='"+SID+"'");

        String Str_PhotoPath="N/A", Str_Name="N/A", Str_Gender="N/A", Str_DOB="N/A", Str_FatherName="N/A", Str_FatherJob="N/A", Str_MotherName="N/A", Str_Address="N/A", Str_MobileNo="N/A", Str_Subject="N/A", Str_BatchInfo="N/A",
                Str_NameofSchool="N/A", Str_CPGA="N/A", Str_CoachingFee="N/A", Str_FeeAdvance="N/A", Str_SID="";

        String Str_Standard="",Str_JoiningDate="";

        Str_PhotoPath = Baseconfig.StudentImgPath;
        Str_Name = name.getText().toString();//*
        if (male.isChecked() == true) {
            Str_Gender = "Male";
        } else {
            Str_Gender = "Female";
        }

        Str_JoiningDate=joining_date.getText().toString();
        Str_DOB =dob.getText().toString();
        Str_FatherName =fat_name.getText().toString();
        Str_FatherJob =occupation.getText().toString();
        Str_MotherName =mot_name.getText().toString();
        Str_Address =address.getText().toString();
        Str_MobileNo =mobile.getText().toString();//*
        Str_Subject =subject.getText().toString();//*
        Str_BatchInfo =choose_batch.getText().toString();//*
        Str_NameofSchool =school.getText().toString();//*
        Str_CPGA =cgpa.getText().toString();//*
        Str_CoachingFee =fee.getText().toString();//*
        Str_FeeAdvance =advance.getText().toString();
        String Str_BoardExam = board_examno.getText().toString();
        String Str_Mother_Occupation=mother_occupation.getText().toString();

        if(rbtn_10th.isChecked()==true)
        {
            Str_Standard="10th";
        }
        else if(rbtn_11th.isChecked()==true){
            Str_Standard="11th";
        }else if(rbtn_12th.isChecked()==true)
        {
            Str_Standard="12th";
        }

       // Str_SID = barcode_data.toString();

        SQLiteDatabase db = GetDb();
        ContentValues values = new ContentValues();
        values.put("Name", Str_Name);
        values.put("Gender", Str_Gender);
        values.put("DOB", Str_DOB);
        values.put("Father_Name", Str_FatherName);
        values.put("Father_Occupation",Str_FatherJob );
        values.put("Mother_Name", Str_MotherName);
        values.put("Mother_Occupation",Str_Mother_Occupation);
        values.put("Address",Str_Address );
        values.put("Mobile_Number",Str_MobileNo );
        values.put("Subject", Str_Subject);
        values.put("Batch_Info", Str_BatchInfo);
        values.put("Batch_ID", Baseconfig.LoadValue("select Id as dstatus from Mstr_Batch where Batch_Name='"+Str_BatchInfo+"' and IsActive='True'"));
        values.put("School_Name", Str_NameofSchool);
        values.put("CGPA",Str_CPGA );
        values.put("Coaching_Fee", Str_CoachingFee);
        values.put("Fee_Advance",Str_FeeAdvance );
        values.put("IsActive", "1");
        values.put("IsUpdate", "0");
        values.put("Standard", Str_Standard);
        values.put("Joining_Date", Str_JoiningDate);
        values.put("ActDate", Baseconfig.GetDate());
        values.put("Photo",Str_PhotoPath );
        //values.put("SID", Str_SID);
        values.put("BoardExam_No", Str_BoardExam);
        values.put("IsFullFee_Paid", "");
        db.update("Bind_EnrollStudents", values,"SID='"+SID+"'", null);


       /* values = new ContentValues();
        values.put("Batch_Name", Str_BatchInfo);
        values.put("SID", Str_SID);
        values.put("IsActive", "1");
        values.put("IsUpdate", "0");
        values.put("ActDate", Baseconfig.GetDate());
        db.update("Temp_Attendance",values,"where SID='"+LOCAL_SID+"'",null);
*/

       //String Del_Attendance="delete from Bind_Attendance where SID='"+SID+"' and ActDate='"+Baseconfig.GetDate()+"'";
       //db.execSQL(Del_Attendance);

        //String Del_Marks="delete from Bind_MarkEntry where SID='"+SID+"' and ActDate='"+Baseconfig.GetDate()+"'";
        //db.execSQL(Del_Marks);


        db.close();

        Log.e("Inserted Values: ", String.valueOf(values));

        ShowSuccessDialog();


    }
    //**************************************************************************************

    public void ShowSuccessDialog() {

        Baseconfig.StudentImgPath="";

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("Student Details Updated..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                        Student_Profile_Edit.this.finish();
                        startActivity(new Intent(Student_Profile_Edit.this, Task_Navigation.class));
                    }
                })
                .show();

        Clear();

    }

    //**********************v***************************************************************

    public void Clear() {


    }
    //**********************v***************************************************************

    //For Image Attachment

    private Bitmap bitmap;
    private String file_name;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        photo.setImageBitmap(file);

        String path = Baseconfig.DATABASE_FILE_PATH + File.separator + "Student_Photos" + File.separator;
        Toast.makeText(this, "image_attachement 1:"+path, Toast.LENGTH_SHORT).show();
        Baseconfig.StudentImgPath=path+filename;
        //Toast.makeText(this, "image_attachement 2:"+Baseconfig.StudentImgPath, Toast.LENGTH_SHORT).show();
        imageutils.createImage(file, filename, path, false);

    }
    //**********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(Student_Profile_Edit.this, Task_Navigation.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


}
