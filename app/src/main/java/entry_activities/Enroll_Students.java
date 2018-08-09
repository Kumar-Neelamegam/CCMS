package entry_activities;

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
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import core_modules.PaymentPage;
import core_modules.Task_Navigation;
import utilities.Baseconfig;
import utilities.Imageutils;
import utilities.LocalSharedPreference;
import vcc.coremodule.R;

import static utilities.Baseconfig.GetDb;


public class Enroll_Students extends AppCompatActivity implements Imageutils.ImageAttachmentListener {

    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    LocalSharedPreference sharedPreference;


    private Toolbar toolbar;
    ImageView Back, Exit;

    //variable declaration

    ImageView photo;
    ImageButton camera;
    AutoCompleteTextView school, occupation, mother_occupation;
    AppCompatEditText name, dob, fat_name, mot_name, address, mobile, subject, choose_batch, cgpa, fee, advance, joining_date, board_examno;
    RadioButton female, male, rbtn_10th, rbtn_11th, rbtn_12th;
    //Spinner batch;
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

    private void GetInitialize() {
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Back = findViewById(R.id.toolbar_back);
            Exit = findViewById(R.id.ic_exit);

            photo = findViewById(R.id.img_photo);
            camera = findViewById(R.id.imgbtn_capture1);

            name = findViewById(R.id.edt_name);
            dob = findViewById(R.id.edt_dob);
            fat_name = findViewById(R.id.edt_fatherrname);
            mot_name = findViewById(R.id.edt_motname);
            occupation = (AppCompatAutoCompleteTextView) findViewById(R.id.edt_occupation);
            occupation.setThreshold(1);
            mother_occupation = (AppCompatAutoCompleteTextView) findViewById(R.id.edt_mother_occupation);
            mother_occupation.setThreshold(1);
            address = findViewById(R.id.edt_address);
            mobile = findViewById(R.id.edt_mobileno);
            subject = findViewById(R.id.edt_subject);
            choose_batch = findViewById(R.id.edt_batch);
            school = (AppCompatAutoCompleteTextView) findViewById(R.id.edt_school);
            school.setThreshold(1);
            cgpa = findViewById(R.id.edt_tenth);
            fee = findViewById(R.id.edt_fee);
            advance = findViewById(R.id.edt_advance);

            female = findViewById(R.id.radi_female);
            male = findViewById(R.id.radi_male);

            rbtn_10th = findViewById(R.id.rbtn_10th);
            rbtn_11th = findViewById(R.id.rbtn_11th);
            rbtn_12th = findViewById(R.id.rbtn_12th);

            board_examno = findViewById(R.id.edt_examno);

            //batch = (Spinner) findViewById(R.id.spn_batch);

            cancel = findViewById(R.id.btn_cancel);
            submit = findViewById(R.id.btn_submit);

            joining_date = findViewById(R.id.edt_joiningdate);

            final Calendar c1 = Calendar.getInstance();
            year = c1.get(Calendar.YEAR);
            month = c1.get(Calendar.MONTH);
            day = c1.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat dateformat1 = new SimpleDateFormat("dd-MM-yyyy");
            String str1 = dateformat1.format(c1.getTime());
            joining_date.setText(str1);


            //Loading batch list
            String str = "";
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
            str = dateformat.format(c.getTime());

            //Loading autocomplete school name
            Baseconfig.LoadValues(school, Enroll_Students.this, "select distinct School_Name as dvalue from Mstr_School where IsActive='1' order by School_Name;");


            //Loading autocomplete occupation name
            Baseconfig.LoadValues(occupation, Enroll_Students.this, "select distinct Occupation_Name as dvalue from Mstr_Occupation where IsActive='1' order by Occupation_Name;");
            Baseconfig.LoadValues(mother_occupation, Enroll_Students.this, "select distinct Occupation_Name as dvalue from Mstr_Occupation where IsActive='1' order by Occupation_Name;");
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                    selectedColours3 = new ArrayList<CharSequence>();
                    choose_batch.setText("");
                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Enroll_Students.this);
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

                int TotalFee = 0;

                if (stringBuilder.toString().toString().length() > 0) {
                    String[] SubjectNames = stringBuilder.toString().split(",");
                    for (int i = 0; i < SubjectNames.length; i++) {

                        String fee = Baseconfig.LoadValue("select IFNULL(Fee,'0') as dstatus from Mstr_Fee where Subject_Name='" + SubjectNames[i].toString() + "'");
                        if (fee == null || fee.length() == 0) {
                            fee = "0";
                        }

                        TotalFee += Integer.parseInt(fee);

                    }

                    fee.setText(String.valueOf(TotalFee));
                }

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

        AlertDialog.Builder builder = new AlertDialog.Builder(Enroll_Students.this);
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

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Enroll_Students.this, Enroll_Students.class));

        cancel.setOnClickListener(view -> LoadBack());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int getCurrentPlanCount = Integer.parseInt(Baseconfig.LoadValueInt("select StudentCount as dstatus from Bind_InstituteInfo where IsPaid=1 and UID='" + Baseconfig.App_UID + "'"));
                int getStudentsCount = Integer.parseInt(Baseconfig.LoadValueInt("select count(Id) as dstatus from Bind_EnrollStudents"));
                if (getCurrentPlanCount != 0) {

                    if (getCurrentPlanCount == getStudentsCount) {

                       // Baseconfig.SweetDialgos(4, Enroll_Students.this, "Information", "As per plan you have reached maximum student enrollments..\nKindly renew your plan.."  , "OK");

                        new SweetAlertDialog(Enroll_Students.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information")
                                .setContentText("As per plan you have reached maximum student enrollments..\nKindly renew your plan..")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        Intent payment=new Intent(Enroll_Students.this, PaymentPage.class);
                                        Enroll_Students.this.startActivity(payment);
                                        Baseconfig.ExpiryStatus = true;
                                        sharedPreference.setBoolean(Baseconfig.Preference_ExpiryStatus, true);//Full data

                                    }
                                })
                                .show();


                        return;
                    }

                    if (checkValidation()) {

                        SaveLocal();

                    } else {

                        Baseconfig.SweetDialgos(4, Enroll_Students.this, "Information", " Please fill all mandatory fields marked with (*)\n" + strError.toString(), "OK");
                    }
                } else {

                    //Baseconfig.SweetDialgos(4, Enroll_Students.this, "Information", "Purchase any plan to continue..", "OK");
                    Toast.makeText(Enroll_Students.this, "Purchase any plan to continue..", Toast.LENGTH_SHORT).show();
                    Intent payment=new Intent(Enroll_Students.this, PaymentPage.class);
                    Enroll_Students.this.startActivity(payment);

                }

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageutils = new Imageutils(Enroll_Students.this);
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
                if (b) {
                    rbtn_12th.setChecked(false);
                    rbtn_11th.setChecked(false);
                }
            }
        });

        rbtn_11th.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rbtn_12th.setChecked(false);
                    rbtn_10th.setChecked(false);
                }
            }
        });

        rbtn_12th.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
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

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, selectedMonth, selectedDay);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String values = format.format(calendar.getTime());
            // Show selected date
            //StringBuilder values=new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" ");
            dob.setText(values.toString());
            Log.e("Selected DOB: ", values.toString());
        }


    };
    //**********************************************************************************************
    StringBuilder strError=new StringBuilder();

    public boolean checkValidation() {
        boolean ret = true;

        strError = new StringBuilder();

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

        String val1 = fee.getText().toString();
        String val2 = advance.getText().toString();

        if (fee.getText().length() == 0) {
            val1 = "0";
        }


        if (advance.getText().length() == 0) {
            val2 = "0";
        }


        //8500<8500
        if (Integer.parseInt(val1) < Integer.parseInt(val2)) {
            strError.append("Advance should not be greater than Fee\n");
            ret = false;
        }

        if (Integer.parseInt(val1) == 0) {
            strError.append("Fee should be greater than 0\n");
            ret = false;
        }

        if (fee.getText().length() == 0) {

            strError.append("Fee*\n");
            ret = false;
        }

       /* if (cgpa.getText().length()==0) {
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


        if (school.getText().length() == 0) {
            strError.append("School Name*\n");
            ret = false;
        }


        if (choose_batch.getText().length() == 0) {
            strError.append("Batch*\n");
            ret = false;
        }

        if (subject.getText().length() == 0) {
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

        //if (!Validation1.isName(fat_name, true)) {
        if (fat_name.getText().length() == 0) {
            strError.append("Father Name*\n");
            ret = false;
        }

        if (dob.getText().length() == 0) {
            strError.append("Date of birth*\n");
            ret = false;
        }

        //if (!Validation1.isName(name, true)) {
        if (name.getText().length() == 0) {
            strError.append("Student Name*\n");
            ret = false;
        }


        return ret;

    }

    //**********************************************************************************************
    String barcode_data;

    public void SaveLocal() {


        try {
            String Str_PhotoPath = "N/A", Str_Name = "N/A", Str_Gender = "N/A", Str_DOB = "N/A", Str_FatherName = "N/A", Str_FatherJob = "N/A", Str_MotherName = "N/A", Str_Address = "N/A", Str_MobileNo = "N/A", Str_Subject = "N/A", Str_BatchInfo = "N/A",
                    Str_NameofSchool = "N/A", Str_CPGA = "N/A", Str_CoachingFee = "N/A", Str_FeeAdvance = "N/A", Str_SID = "";

            String Str_Standard = "", Str_JoiningDate = "";

            String BoardExamNO = "";

            if (Baseconfig.StudentImgPath.toString().length() == 0) {
                Baseconfig.StudentImgPath = Environment.getExternalStorageDirectory() + "/vcc/male_avatar.jpg";
            }

            Str_PhotoPath = Baseconfig.StudentImgPath;

            Str_Name = name.getText().toString();//*
            if (male.isChecked() == true) {
                Str_Gender = "Male";
            } else {
                Str_Gender = "Female";
            }

            if (rbtn_10th.isChecked() == true) {
                Str_Standard = "10th";
            } else if (rbtn_11th.isChecked() == true) {
                Str_Standard = "11th";
            } else if (rbtn_12th.isChecked() == true) {
                Str_Standard = "12th";
            }

            Str_JoiningDate = joining_date.getText().toString();
            Str_DOB = dob.getText().toString();
            Str_FatherName = fat_name.getText().toString();
            Str_FatherJob = occupation.getText().toString();
            Str_MotherName = mot_name.getText().toString();
            Str_Address = address.getText().toString();
            Str_MobileNo = mobile.getText().toString();//*
            Str_Subject = subject.getText().toString();//*
            Str_BatchInfo = choose_batch.getText().toString();//*
            Str_NameofSchool = school.getText().toString();//*
            Str_CPGA = cgpa.getText().toString();//*
            Str_CoachingFee = fee.getText().toString();//*
            Str_FeeAdvance = advance.getText().toString();
            String Str_Mother_Occupation = mother_occupation.getText().toString();
            String Str_BoardExam = board_examno.getText().toString();

            try {
                //SIDCPM007510
                //Toast.makeText(this, "Selected subject: "+ Str_Subject, Toast.LENGTH_LONG).show();
                barcode_data = "SID" + LoadSubject(Str_Subject).toUpperCase() + UUID.randomUUID().toString().split("-")[1].toUpperCase();

                bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 500, 150);
                //iv.setImageBitmap(bitmap);
                SaveImage(bitmap);

            } catch (WriterException e) {
                e.printStackTrace();
            }

            Str_SID = barcode_data.toString();

            //String[] Selected_Subjects=Str_Subject.toString().split(",");
            //String[] Selected_Batch=Str_BatchInfo.toString().split(",");

            SQLiteDatabase db = Baseconfig.GetDb();
            ContentValues values = new ContentValues();
            values.put("Name", Str_Name);
            values.put("Gender", Str_Gender);
            values.put("DOB", Str_DOB);
            values.put("Father_Name", Str_FatherName);
            values.put("Father_Occupation", Str_FatherJob);
            values.put("Mother_Name", Str_MotherName);
            values.put("Mother_Occupation", Str_Mother_Occupation);
            values.put("Address", Str_Address);
            values.put("Mobile_Number", Str_MobileNo);
            values.put("Subject", Str_Subject);
            values.put("Batch_Info", Str_BatchInfo);
            //values.put("Batch_ID", Baseconfig.LoadValue("select Id as dstatus from Mstr_Batch where Batch_Name='"+Str_BatchInfo+"' and IsActive='True'"));
            values.put("School_Name", Str_NameofSchool);
            values.put("CGPA", Str_CPGA);
            values.put("Coaching_Fee", Str_CoachingFee);
            values.put("Fee_Advance", Str_FeeAdvance);
            values.put("IsActive", "1");
            values.put("IsUpdate", "0");
            values.put("Standard", Str_Standard);
            values.put("Joining_Date", Str_JoiningDate);
            values.put("ActDate", Baseconfig.GetDate());
            values.put("Photo", Str_PhotoPath);
            values.put("SID", Str_SID);
            values.put("BoardExam_No", Str_BoardExam);
            values.put("IsFullFee_Paid", "");
            values.put("IsSMS_Sent", "0");
            db.insert("Bind_EnrollStudents", null, values);


            if (Str_FeeAdvance != null && Str_FeeAdvance.length() > 0) {
                values = new ContentValues();
                values.put("SID", Str_SID);
                values.put("Paid_Fee", Str_FeeAdvance);
                values.put("Paid_Date", Baseconfig.GetDate());
                db.insert("Bind_FeeEntry", null, values);
            }


/*
        values = new ContentValues();
        values.put("Batch_Name", Str_BatchInfo);
        values.put("SID", Str_SID);
        values.put("IsActive", "1");
        values.put("IsUpdate", "0");
        values.put("ActDate", Baseconfig.GetDate());
        db.insert("Temp_Attendance",null,values);
        */

            //Insert Occupation Mstr if not available na - Mother occupation
            boolean b;
            if (Str_Mother_Occupation.length() > 0) {
                b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Occupation where Occupation_Name='" + Str_Mother_Occupation.toString() + "' and IsActive='1'");
                if (!b) {
                    values = new ContentValues();
                    values.put("Occupation_Name", Str_Mother_Occupation);
                    values.put("IsActive", "1");
                    values.put("IsUpdate", "0");
                    values.put("ActDate", Baseconfig.GetDate());
                    db.insert("Mstr_Occupation", null, values);
                }
            }

            //Father occupation
            if (Str_FatherJob.length() > 0) {
                b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_Occupation where Occupation_Name='" + Str_FatherJob.toString() + "' and IsActive='1'");
                if (!b) {
                    values = new ContentValues();
                    values.put("Occupation_Name", Str_FatherJob);
                    values.put("IsActive", "1");
                    values.put("IsUpdate", "0");
                    values.put("ActDate", Baseconfig.GetDate());
                    db.insert("Mstr_Occupation", null, values);
                }
            }

            //Insert School Mstr  if not available na
            if (Str_NameofSchool.length() > 0) {
                b = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Mstr_School where School_Name='" + Str_NameofSchool.toString() + "' and IsActive='1'");
                if (!b) {
                    values = new ContentValues();

                    values.put("School_Name", Str_NameofSchool);
                    values.put("IsActive", "1");
                    values.put("IsUpdate", "0");
                    values.put("ActDate", Baseconfig.GetDate());
                    db.insert("Mstr_School", null, values);
                }
            }


            db.close();

            Log.e("Inserted Values: ", String.valueOf(values));

            ShowSuccessDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //**************************************************************************************
    public String LoadSubject(String getstr) {

        //Log.e("LoadSubject 1: ", getstr);
        getstr = String.valueOf(getstr.toString().substring(0, getstr.length() - 1));
        //Log.e("LoadSubject 2: ", getstr);
        String[] values = getstr.toString().trim().split(",");
        StringBuilder ret = new StringBuilder();
        //Log.e("LoadSubject 3: ", String.valueOf(values));
        for (int i = 0; i < values.length; i++) {

            ret.append(values[i].toString().charAt(0));
            //Log.e("values[i].toString():  ",values[i].toString() );
            //Log.e("LoadSubject 4: ", ret.toString());
        }
        //Log.e("LoadedSubject: 5", ret.toString());
        return ret.toString();
    }
    //**************************************************************************************

    public void ShowSuccessDialog() {

        Baseconfig.StudentImgPath = "";

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(this.getResources().getString(R.string.information))
                .setContentText("Student Enrolled successfully..")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                        Enroll_Students.this.finish();
                        startActivity(new Intent(Enroll_Students.this, Task_Navigation.class));
                    }
                })
                .show();

        Clear();

    }

    //**********************v***************************************************************

    public void Clear() {


    }


    /**************************************************************


     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private void SaveImage(Bitmap finalBitmap) {

        //String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(Baseconfig.DATABASE_FILE_PATH + "/student_barcodes");
        myDir.mkdirs();

        //Random generator = new Random();
        //int n = 10000;
        //n = generator.nextInt(n);

        String fname = barcode_data + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            // Toast.makeText(Enroll_Students.this,"Image Saved In: "+file,Toast.LENGTH_LONG).show();

            Log.e("Save Barcode Image: ", String.valueOf(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //********************************************************************************


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
        // Toast.makeText(this, "image_attachement 1:"+path, Toast.LENGTH_SHORT).show();
        Baseconfig.StudentImgPath = path + filename;
        // Toast.makeText(this, "image_attachement 2:"+Baseconfig.StudentImgPath, Toast.LENGTH_SHORT).show();
        imageutils.createImage(file, filename, path, false);

    }
    //**********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(Enroll_Students.this, Task_Navigation.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    //End
}
