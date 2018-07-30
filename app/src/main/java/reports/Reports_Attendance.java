package reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import utilities.Baseconfig;
import utilities.FButton;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/20/2017.
 */

public class Reports_Attendance extends AppCompatActivity {


    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, Options;
    WebView reports_value;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_reports);


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
        Options = findViewById(R.id.ic_options);
        TextView Title = findViewById(R.id.txvw_title);
        Title.setText("Attendance Wise\nReport");


        reports_value = findViewById(R.id.web_reports_cmmn);

        reports_value.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        reports_value.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        reports_value.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        try {
            String Query = "select b.IsUpdate,Id,Batch_Name,SID,(select Name from Bind_EnrollStudents a where a.SID=b.SID)as Name, " +
                    "IFNULL(Attendance_Date,'-')as Attendance_Date,IFNULL(Attendance_Status,'-')as Attendance_Status " +
                    "from Bind_Attendance b where Attendance_Date='"+Baseconfig.GetDate()+"' ";

            boolean q = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Bind_Attendance;");
            if (q == false) {
                MyDynamicToast.informationMessage(Reports_Attendance.this, "No attendance details available..");

                Options.setVisibility(View.INVISIBLE);
                return ;
            }


            LoadWebview(Query);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    //**********************************************************************************************

    private void Controllisteners() {
        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, Reports_Attendance.class));

        Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence[] items = {
                        "Generate PDF / Excel", "Email", "Filter","Refresh","Close"
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(Reports_Attendance.this);
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].toString().equalsIgnoreCase("Generate PDF / Excel")) {

                            try {
                                new ExportDatabaseCSVTask().execute("1");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (items[item].toString().equalsIgnoreCase("Email")) {


                            if (!Baseconfig.CheckNW(Reports_Attendance.this)) {
                                Baseconfig.SweetDialgos(3, Reports_Attendance.this, "Information", "No internet available\nEnable data connection from settings..", "OK");
                                return;
                            }
                            File report_file = new File(Report_AttendanceFilePath);

                            if (report_file.exists()) {

                                SendEmail();

                            } else {

                                //Baseconfig.SweetDialgos(4,Reports_Attendance.this,"Information","","OK");

                                new SweetAlertDialog(Reports_Attendance.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(Reports_Attendance.this.getResources().getString(R.string.information))
                                        .setContentText("Attendance report not generated today..\nDo you want to generate and send email?")
                                        .setCancelText(Reports_Attendance.this.getResources().getString(R.string.no))
                                        .setConfirmText(Reports_Attendance.this.getResources().getString(R.string.yes))
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
                                                new ExportDatabaseCSVTask().execute("2");


                                            }
                                        })
                                        .show();
                            }


                        } else if (items[item].toString().equalsIgnoreCase("Filter")) {
                            ShowOptions();
                        }
                        else if(items[item].toString().equalsIgnoreCase("Refresh"))
                        {

                            Search_Flag=0;
                                String Query = "select b.IsUpdate,Id,Batch_Name,SID,(select Name from Bind_EnrollStudents a where a.SID=b.SID)as Name, " +
                                        "IFNULL(Attendance_Date,'-')as Attendance_Date,IFNULL(Attendance_Status,'-')as Attendance_Status " +
                                        "from Bind_Attendance b;";// where ActDate='"+Baseconfig.GetDate_YYYY_DD_MM()+"' ";

                                boolean q = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Bind_Attendance;");
                                if (q == false) {
                                    MyDynamicToast.informationMessage(Reports_Attendance.this, "No attendance details available..");

                                    Options.setVisibility(View.INVISIBLE);
                                    return ;
                                }


                                LoadWebview(Query);
                        }



                    }
                });
                AlertDialog alert = builder.create();
                Dialog dialog = alert;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();


            }
        });
    }

    //**********************************************************************************************
    /**
     * Creating report attendance file
     */
    String Report_AttendanceFilePath_Excel = Baseconfig.DATABASE_FILE_PATH + "/report_attendance" + Baseconfig.GetDate() + ".csv";

    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(Reports_Attendance.this);
        int Id=0;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting student attendance list...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {

            Id= Integer.parseInt(args[0]);

            if(Search_Flag==1)
            {
                GeneratePDF(SearchQuery);
                Search_Flag=0;

            }else
            {
                GeneratePDF("select b.IsUpdate,Id,Batch_Name,SID,(select Name from Bind_EnrollStudents a where a.SID=b.SID)as Name, " +
                        "IFNULL(Attendance_Date,'-')as Attendance_Date,IFNULL(Attendance_Status,'-')as Attendance_Status " +
                        "from Bind_Attendance b;");

            }

            File file = new File(Report_AttendanceFilePath_Excel);
            if (file.exists())//if file iruntha delete panum
            {
                file.delete();
            }

            try {
                file.createNewFile();
                Baseconfig.CSVWriter csvWrite = new Baseconfig.CSVWriter(new FileWriter(file));
                SQLiteDatabase db = Baseconfig.GetDb();
                Cursor curCSV = db.rawQuery("select (select count(*) from Bind_Attendance a  where b.Id >= a.Id)" +
                        " as Id,Batch_Name,SID,(select Name from Bind_EnrollStudents a where a.SID=b.SID)as Name," +
                        " IFNULL(Attendance_Date,'-')as Attendance_Date,IFNULL(Attendance_Status,'-')as" +
                        " Attendance_Status from Bind_Attendance b;", null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)
                            , curCSV.getString(3), curCSV.getString(4), curCSV.getString(5)};

                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                return true;
            } catch (SQLException sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                return false;
            } catch (IOException e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            Toast.makeText(Reports_Attendance.this, "Attendance report generated successfully..", Toast.LENGTH_SHORT).show();


            if (Id == 1) {
                LoadPDF(file);
            } else if (Id == 2) {
                SendEmail();
            }


          /*  if (success) {
                Toast.makeText(Reports_Attendance.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Reports_Attendance.this, "Export failed", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    //**********************************************************************************************

    void SendEmail() {

        String InstituteMailId = Baseconfig.LoadValue("select Email as dstatus from Bind_InstituteInfo");

        BackgroundMail.newBuilder(this)
                .withUsername(Baseconfig.MailID)
                .withPassword(Baseconfig.MailPassword)
                .withMailto(InstituteMailId)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Report - Attendance / " + Baseconfig.GetDate())
                .withBody("Hi,\nHere with attached the attendance report of " + Baseconfig.GetDate() + ".\n")
                .withAttachments(Report_AttendanceFilePath,Report_AttendanceFilePath_Excel)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                        new SweetAlertDialog(Reports_Attendance.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(Reports_Attendance.this.getResources().getString(R.string.information))
                                .setContentText("Mail sent successfully..")
                                .setConfirmText("OK")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .show();

                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();

    }


    String Report_AttendanceFilePath = Baseconfig.DATABASE_FILE_PATH+ "/report_attendance_" + Baseconfig.GetDate() + ".pdf";


    File file;

    void GeneratePDF(String Query) {


        file = new File(Report_AttendanceFilePath);
        if (file.exists())//if file iruntha delete panum
        {
            file.delete();
        }
        StringBuilder str = new StringBuilder();
        SQLiteDatabase db = Baseconfig.GetDb();
        Cursor c = db.rawQuery(Query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    int position = c.getPosition();

                    String Batch_Name = c.getString(c.getColumnIndex("Batch_Name"));
                    String SID = c.getString(c.getColumnIndex("SID"));
                    String Name = c.getString(c.getColumnIndex("Name"));
                    String Attendance_Date = c.getString(c.getColumnIndex("Attendance_Date"));
                    String Attendance_Status = c.getString(c.getColumnIndex("Attendance_Status"));
                    String IsUpdate = c.getString(c.getColumnIndex("IsUpdate"));

                    String attstatus = "";

                    if(IsUpdate.toString().equalsIgnoreCase("0")){
                        attstatus="<td bgcolor=\"#ffe246\" ><font color=\"#000\">Not Taken</font></td>";
                    }
                    else if(IsUpdate.toString().equalsIgnoreCase("1"))
                    {
                        attstatus="<td bgcolor=\"#00cd92\" ><font color=\"#000\">Present</font></td>";
                    }
                    else //if(Attendance_Status.toString().equalsIgnoreCase("2"))
                    {
                        attstatus="<td bgcolor=\"#F08080\" ><font color=\"#000\">Absent</font></td>";
                    }

                    str.append(" <tr>" +
                            "<td bgcolor=\"#ffffff\" align=\"center\"><font color=\"#000\">" + (position + 1) + "</font></td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Batch_Name + "</font></td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + SID + "</font></td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Name + "</font></td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Attendance_Date + "</font></td>" +
                            "" + attstatus + "\n" +
                            "  </tr>");

                } while (c.moveToNext());
            }
        }

        c.close();
        c = null;

        String Tv_InsituteName = "", Tv_InstituteAddress = "", Mobile = "", Email = "";
        StringBuilder Logo = new StringBuilder();

        c = db.rawQuery("select * from Bind_InstituteInfo", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Tv_InsituteName = (c.getString(c.getColumnIndex("Institute_Name")));
                    Tv_InstituteAddress = (c.getString(c.getColumnIndex("Institute_Address")));
                    Mobile = (c.getString(c.getColumnIndex("Mobile")).toString());
                    Email = (c.getString(c.getColumnIndex("Email")).toString());
                    Logo.append(getImageBase64(c.getString(c.getColumnIndex("Logo"))));

                } while (c.moveToNext());
            }
        }

        c.close();
        db.close();

        try {


            String str1 = "";

            str1 = "<!DOCTYPE html>\n" +
                    "\n" +
                    "<html lang=\"en\">\n" +

                    "<style>\n" +
                    "table.roundedCorners { \n" +
                    "  border: 1px solid Black;\n" +
                    "  border-radius: 13px; \n" +
                    "  border-spacing: 0;\n" +
                    "  }\n" +
                    "table.roundedCorners td, \n" +
                    "table.roundedCorners th { \n" +
                    "  border-bottom: 1px solid Black;\n" +
                    "  padding: 3px; \n" +
                    "  }\n" +
                    "table.roundedCorners tr:last-child > td {\n" +
                    "  border-bottom: none;\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    " table.innertable {\n" +
                    "    border-collapse: collapse;\n" +
                    "  }\n" +
                    "  table.innertable th, table.innertable td {\n" +
                    "    border: 1px solid #ccc;\n" +
                    "    padding: 3px;\n" +
                    "    text-align: left;\n" +
                    "  }\n" +
                    "  table.innertable tr:nth-child(even) {\n" +
                    "    background-color: #eee;\n" +
                    "  }\n" +
                    "  table.innertable tr:nth-child(odd) {\n" +
                    "    background-color: #fff;\n" +
                    "  } \n" +
                    "  \n" +
                    "  \n" +
                    "</style>" +

                    "<body>  \n" +

                    "<table class=\"roundedCorners\" style=\"margin-left: auto; margin-right: auto;\">" +
                    "<tbody>\n" +
                    "<tr>\n" +
                    "<td><img width=\"25%\" height=\"25%\" src=\"data:image/png;base64," + Logo.toString() + "  " +
                    "align=\"right\"/></td>" +

                    "<td>\n" +

                    "<table>" +
                    "<tbody>\n" +
                    "<tr>" +
                    "<td style=\"text-align: center;\">\n" +
                    "<h3>" + Tv_InsituteName + "</h3></td></tr>\n" +
                    "<tr><td style=\"text-align: center;\">" + Tv_InstituteAddress + "</td></tr>\n" +
                    "<tr><td>Cell:<strong>" + Mobile + "</strong>, Mail:<strong>" + Email + "</strong></td></tr>\n" +
                    "</tbody>" +
                    "</table>\n" +
                    "</td>" +
                    "</tr>\n" +
                    "</tbody>" +
                    "</table>" +


                    "<br></br><table class=\"innertable\" >" +
                    "\n" +
                    "<tr>\n" +
                    "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#000\">Attendance Wise Report</font></th>\n" +
                    "</tr>" +
                    "</table> <br>\n\n</br>" +


                    "<div>\n" +
                    "\n" +
                    "<table width=\"100%\" class=\"innertable\">\n" +
                    "\n" +
                    " <tr>\n" +
                    "  <th bgcolor=\"#6A1B9A\" text-align=\"center\"><font color= \"#000\">SNo</font></th>\n" +
                    "\t<th bgcolor=\"#6A1B9A\" text-align=\"center\"><font color=\"#000\">Batch Name</font></th>\n" +
                    "\t<th bgcolor=\"#6A1B9A\" text-align=\"center\"><font color=\"#000\">SID</font></th>\n" +
                    "\t<th bgcolor=\"#6A1B9A\" text-align=\"center\"><font color=\"#000\">Student Name</font></th>\n" +
                    "\t<th bgcolor=\"#6A1B9A\" text-align=\"center\"><font color=\"#000\">Attendance Date</font></th>\n" +
                    "\t<th bgcolor=\"#6A1B9A\" text-align=\"center\"><font color=\"#000\">Attendance Status</font></th>\n" +
                    " </tr>\n" +
                    str.toString() +
                    "</table> " +
                    "</div>\n" +


                    "</body>\n" +
                    "</html> ";


            // step 1
            Document document = new Document();
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            // step 3
            document.open();
            // step 4

            // CSS
            CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);


            // HTML
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
            htmlContext.setImageProvider(new Base64ImageProvider());

            // Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

            // XML Worker
            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);
            p.parse(new ByteArrayInputStream(str1.getBytes()));


            // step 5
            document.close();


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // #######################################################################################################

    public void LoadPDF(File file) {

        if (file.exists()) {
            Uri filepath = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(filepath, "application/pdf");

            Intent intent1 = Intent
                    .createChooser(intent, "Open Attendance Report");

            try {
                startActivity(intent1);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
                Toast.makeText(this, "No pdf viewer found!",
                        Toast.LENGTH_LONG).show();

            }

        }

    }

    // #######################################################################################################///////////////

    class Base64ImageProvider extends AbstractImageProvider {

        @Override
        public com.itextpdf.text.Image retrieve(String src) {
            int pos = src.indexOf("base64,");
            try {
                if (src.startsWith("data") && pos > 0) {
                    byte[] img = com.itextpdf.text.pdf.codec.Base64.decode(src.substring(pos + 7));
                    return com.itextpdf.text.Image.getInstance(img);
                } else {
                    return com.itextpdf.text.Image.getInstance(src);
                }
            } catch (BadElementException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        public String getImageRootPath() {
            return null;
        }
    }


    private StringBuilder getImageBase64(String ImagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(ImagePath);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        StringBuilder imgString = new StringBuilder();
        imgString.append(Base64.encodeToString(byteFormat, Base64.NO_WRAP));
        return imgString;

    }

    //**********************************************************************************************

    final static int DATE_DIALOG_ID1 = 12323;
    int year;
    int month;
    int day;
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID1:

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
            Attendance_Date.setText(dateString.toString());
            Log.e("Selected DOB: ", dateString.toString());
        }


    };

    //**********************************************************************************************
    EditText Attendance_Date;
    int Search_Flag=0;
    String SearchQuery="";

    public void ShowOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Reports_Attendance.this);
        LayoutInflater inflater = Reports_Attendance.this.getLayoutInflater();
        View inflatedLayout = inflater.inflate(R.layout.filter_attendance, null);

        Spinner BatchList = inflatedLayout.findViewById(R.id.spn_bat_li);
        EditText StudentID = inflatedLayout.findViewById(R.id.edt_sid);
        EditText Student_Name = inflatedLayout.findViewById(R.id.edt_name);
        Attendance_Date = inflatedLayout.findViewById(R.id.att_date);

        RadioButton rbtn_present= inflatedLayout.findViewById(R.id.attendance_present);
        RadioButton rbtn_absent= inflatedLayout.findViewById(R.id.attendance_absent);

        FButton Close = inflatedLayout.findViewById(R.id.bttn_clear);
        FButton Search = inflatedLayout.findViewById(R.id.bttn_search);



        Attendance_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                showDialog(DATE_DIALOG_ID1);
            }
        });
        AlertDialog show;

        // Set the dialog layout
        builder.setView(inflatedLayout);
        show = builder.show();
        show.setCancelable(false);
        show.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;


        //Loading batch list
        Baseconfig.LoadValuesSpinner(BatchList, Reports_Attendance.this, "select distinct Batch_Name as dvalue from Mstr_Batch where IsActive='True'", "Select");


        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                show.dismiss();

            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String Str_BatchName = "", Str_StudentId = "", Str_StudentName = "", Str_AttendanceDate = "",Str_AttendanceStatus="";


                    String WhereCondition = "";

                    if (BatchList.getSelectedItemPosition() > 0 || StudentID.getText().toString().length() > 0 || Student_Name.getText().toString().length() > 0 || Attendance_Date.getText().toString().length() > 0||rbtn_present.isChecked() ||rbtn_absent.isChecked()) {

                        show.dismiss();

                        if(BatchList.getSelectedItemPosition()>0)
                        {
                            Str_BatchName = BatchList.getSelectedItem().toString();

                        }

                        Str_StudentId = StudentID.getText().toString();
                        Str_StudentName = Student_Name.getText().toString();
                        Str_AttendanceDate = Attendance_Date.getText().toString();
                        if(rbtn_present.isChecked()==true)
                        {
                            Str_AttendanceStatus="1";//1 na present

                        }
                        else  if(rbtn_absent.isChecked()==true)
                        {
                            Str_AttendanceStatus="2";//2 na absent
                        }


                        //first condition
                        if (!Str_BatchName.toString().equalsIgnoreCase("")) {
                            WhereCondition = "where b.Batch_Name like '%" + Str_BatchName + "%'";
                        }

                        //second condition
                        if (!Str_StudentId.toString().equalsIgnoreCase("")) {
                            if (!Str_BatchName.toString().equalsIgnoreCase("")) {
                                WhereCondition = WhereCondition + " and b.SID='" + Str_StudentId.toString() + "'";
                            } else {
                                WhereCondition = "where b.SID='" + Str_StudentId + "'";
                            }

                        }

                        //third condition
                        if (!Str_StudentName.toString().equalsIgnoreCase("")) {
                            if (!Str_BatchName.toString().equalsIgnoreCase("") || !Str_StudentId.toString().equalsIgnoreCase("")) {

                                WhereCondition = WhereCondition + " and a.Name like '%" + Str_StudentName.toString() + "'%";

                            } else {
                                WhereCondition = "where a.Name like '%" + Str_StudentName + "%'";
                            }

                        }


                        //fourth condition
                        if (!Str_AttendanceDate.toString().equalsIgnoreCase("")) {

                            if (!Str_BatchName.toString().equalsIgnoreCase("") || !Str_StudentId.toString().equalsIgnoreCase("") || !Str_StudentName.toString().equalsIgnoreCase("")) {

                                WhereCondition = WhereCondition + " and b.Attendance_Date='" + Str_AttendanceDate.toString() + "'";

                            } else {
                                WhereCondition = "where b.Attendance_Date='" + Str_AttendanceDate + "'";
                            }
                        }

                        //fifth condition - radio button
                        if (!Str_AttendanceStatus.toString().equalsIgnoreCase("")) {

                            if (!Str_BatchName.toString().equalsIgnoreCase("") || !Str_StudentId.toString().equalsIgnoreCase("") || !Str_StudentName.toString().equalsIgnoreCase("")|| !Str_AttendanceDate.toString().equalsIgnoreCase("")) {

                                WhereCondition = WhereCondition + " and b.IsUpdate='" + Str_AttendanceStatus.toString() + "'";

                            } else {
                                WhereCondition = "where b.IsUpdate='" + Str_AttendanceStatus + "'";
                            }
                        }


                        Log.e("Where conditions: ", WhereCondition);

                        String Query = "select b.IsUpdate,b.Id,b.Batch_Name,b.SID,(select Name from Bind_EnrollStudents a where a.SID=b.SID)as Name, " +
                                "IFNULL(Attendance_Date,'-')as Attendance_Date,IFNULL(Attendance_Status,'-')as Attendance_Status " +
                                "from Bind_Attendance b inner join Bind_EnrollStudents a on a.SID=b.SID " + WhereCondition + ";";

                        Log.e("Where condition srch: ", WhereCondition);

                        boolean q = Baseconfig.LoadReportsBooleanStatus("select b.Id as dstatus1 from Bind_Attendance b inner join Bind_EnrollStudents a on a.SID=b.SID " + WhereCondition + ";");
                        if (q == false) {
                            MyDynamicToast.informationMessage(Reports_Attendance.this, "No details available..");

                            return ;
                        }

                        LoadWebview(Query);

                        Search_Flag=1;
                        SearchQuery=Query;

                    }
                    else {

                        Toast.makeText(Reports_Attendance.this, "Select any one option..", Toast.LENGTH_SHORT).show();
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }


    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(this, Reports.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************

    public void LoadWebview(String Query) {

        reports_value.getSettings().setJavaScriptEnabled(true);
        reports_value.setLayerType(View.LAYER_TYPE_NONE, null);
        reports_value.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        reports_value.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        reports_value.getSettings().setDefaultTextEncodingName("utf-8");

        reports_value.setWebChromeClient(new WebChromeClient() {
        });

        reports_value.setBackgroundColor(0x00000000);
        reports_value.setVerticalScrollBarEnabled(true);
        reports_value.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();

        MyDynamicToast.informationMessage(Reports_Attendance.this, "Please wait attendance details loading..");

        reports_value.getSettings().setJavaScriptEnabled(true);

        reports_value.getSettings().setAllowContentAccess(true);

        reports_value.addJavascriptInterface(new WebAppInterface(Reports_Attendance.this), "android");
        try {
            reports_value.reload();
            reports_value.loadDataWithBaseURL("file:///android_asset/", LoadAttendanceRegister(Query), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //reports_value.loadUrl("file:///android_asset/Student_Profile/Reports_Attendance.html"); //Loading from  assets


    }
    //#######################################################################################################

    private String LoadAttendanceRegister(String Query) {

        String Values = "";
        String Batch_Name = "N/A";

        StringBuilder str = new StringBuilder();
        StringBuilder str1 = new StringBuilder();


        SQLiteDatabase db = Baseconfig.GetDb();


        Cursor c = db.rawQuery(Query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int position = c.getPosition();

                    Batch_Name = c.getString(c.getColumnIndex("Batch_Name"));
                    String SID = c.getString(c.getColumnIndex("SID"));
                    String Name = c.getString(c.getColumnIndex("Name"));
                    String Attendance_Date = c.getString(c.getColumnIndex("Attendance_Date"));
                    String Attendance_Status = c.getString(c.getColumnIndex("Attendance_Status"));
                    String IsUpdate = c.getString(c.getColumnIndex("IsUpdate"));


                    String attstatus = "";
                    if(IsUpdate.toString().equalsIgnoreCase("0")){
                        attstatus="<td bgcolor=\"#ffe246\" ><font color=\"#000\">Not Taken</font></td>";
                    }
                    else if(IsUpdate.toString().equalsIgnoreCase("1"))
                    {
                        attstatus="<td bgcolor=\"#00cd92\" ><font color=\"#000\">Present</font></td>";
                    }
                    else //if(Attendance_Status.toString().equalsIgnoreCase("2"))
                    {
                        attstatus="<td bgcolor=\"#F08080\" ><font color=\"#000\">Absent</font></td>";
                    }

                    str1.append("");


                    str.append(" <tr>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + (position + 1) + "</td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Batch_Name + "</td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + SID + "</td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Name + "</td>" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + Attendance_Date + "</td>" +
                            "" + attstatus + "\n" +
                            "  </tr>");

                } while (c.moveToNext());
            }
        }

        c.close();
        db.close();


        Values = "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n" +
                "<link rel=\"stylesheet\"  type=\"text/css\" href=\"file:///android_asset/Student_Profile/css/english.css\"/>\n" +
                "\n" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/Student_Profile/css/bootstrap.min.css\"/>\n" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/Student_Profile/css/bootstrap-theme.min.css\"/>\n" +
                "\n" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/Student_Profile/css/font-awesome.min.css\" type=\"text/css\"/>\n" +
                "\n" +
                "<script src=\"file:///android_asset/Student_Profile/css/jquery.min.js\"></script>\n" +
                "<script src=\"file:///android_asset/Student_Profile/css/bootstrap.min.js\"></script>\n" +
                "</head>" +
                "<body>  " +
                "<div class=\"table-responsive\">" +
                "<table class=\"table table-bordered\" >" +
                "  <tr>" +
                "    <th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SNo</font></th>" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Batch Name</font></th>" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SID</font></th>" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Student Name</font></th>" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Attendance Date</font></th>" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Attendance Status</font></th>" +
                "  </tr>" +
                "   " + str.toString() +
                "</table>" +
                "</div>" +
                "</body>" +
                "</html> ";


        return Values;
    }

    //#######################################################################################################

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();


        }
    }

    //#######################################################################################################





    //End
}
