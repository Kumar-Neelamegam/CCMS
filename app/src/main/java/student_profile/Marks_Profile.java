package student_profile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;

import utilities.Baseconfig;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/21/2017.
 */

public class Marks_Profile extends Fragment {

    WebView attendance_list;
    private static String TABLE_ID;

    //*********************************************************************************************
    public static Marks_Profile newInstance(String TABLEID) {
        Marks_Profile fragment = new Marks_Profile();
        TABLE_ID = TABLEID;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_common_student_details, container, false);

        try {

            GetInitialize(v);

            Controllisterners(v);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return v;
    }
    //*********************************************************************************************

    private void GetInitialize(View v) {

        attendance_list = v.findViewById(R.id.web_stu_list);

        attendance_list.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        attendance_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        attendance_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        try {
            LoadWebview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Controllisterners(View v) {
    }
//*********************************************************************************************


    public void LoadWebview() {

        attendance_list.getSettings().setJavaScriptEnabled(true);
        attendance_list.setLayerType(View.LAYER_TYPE_NONE, null);
        attendance_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        attendance_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        attendance_list.getSettings().setDefaultTextEncodingName("utf-8");

        attendance_list.setWebChromeClient(new WebChromeClient() {
        });

        attendance_list.setBackgroundColor(0x00000000);
        attendance_list.setVerticalScrollBarEnabled(true);
        attendance_list.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();

        //MyDynamicToast.informationMessage(getActivity(), "Please wait attendance details loading..");

        attendance_list.getSettings().setJavaScriptEnabled(true);

        attendance_list.getSettings().setAllowContentAccess(true);

        attendance_list.addJavascriptInterface(new Marks_Profile.WebAppInterface(getActivity()), "android");
        try {

            attendance_list.loadDataWithBaseURL("file:///android_asset/", LoadMarkRegister(), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //attendance_list.loadUrl("file:///android_asset/Student_Profile/Student_Marks.html"); //Loading from  assets


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

    public String LoadMarkRegister() {

        String values = "";
        String Subjects = "", Batch_Name = "-", Timing = "-";
        String SID = "", Name = "";


        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select Name,SID,Batch_Info,Subject from Bind_EnrollStudents where SID='" + TABLE_ID + "'";

        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    SID = c.getString(c.getColumnIndex("SID"));
                    Name = c.getString(c.getColumnIndex("Name"));
                    Subjects = c.getString(c.getColumnIndex("Subject"));
                    Batch_Name = c.getString(c.getColumnIndex("Batch_Info"));
                    Timing = Baseconfig.LoadValue("select Class_From ||'-'|| Class_To as dstatus  from Mstr_Batch where IsActive='True' and Batch_Name like '%" + Batch_Name + "%'");


                } while (c.moveToNext());
            }
        }

        c.close();


        boolean q = Baseconfig.LoadReportsBooleanStatus("select Batch_Name as dstatus1 from Bind_MarkEntry where  SID='" + SID + "'");
        if (!q) {
            Baseconfig.SweetDialgos(3, getActivity(), "Information", "No data available", "OK");

            return "";
        }

        MyDynamicToast.informationMessage(getActivity(), "Please wait marks details loading..");

        StringBuilder str=new StringBuilder();

        c = null;
        Query = "select (select Subject from Mstr_Test b where b.Id=a.Test_ID)as Subject,(select Date_Of_Test from Mstr_Test b where b.Id=a.Test_ID) as Date_Of_Test," +
                "(select Name_Of_Test from Mstr_Test b where b.Id=a.Test_ID) as Name_Of_Test," +
                "(select MaxMarks from Mstr_Test b where b.Id=a.Test_ID)as MaxMarks," +
                "IFNULL(a.Obtained_Marks,'-')as Obtained_Marks" +
                " from Bind_MarkEntry a where SID='" + SID + "'";
        c = db.rawQuery(Query, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    String obtainedmarks=c.getString(c.getColumnIndex("Obtained_Marks"));
                    if(obtainedmarks.toString().equalsIgnoreCase("0"))
                    {
                        obtainedmarks="Absent";
                    }

                    str.append("<tr>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">"+c.getString(c.getColumnIndex("Subject"))+"</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">"+c.getString(c.getColumnIndex("Name_Of_Test"))+"</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">"+c.getString(c.getColumnIndex("Date_Of_Test"))+"</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">"+c.getString(c.getColumnIndex("MaxMarks"))+"</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">"+obtainedmarks+"</td>\n" +
                            "  </tr>  ");

                } while (c.moveToNext());

            }
        }

        c.close();
        db.close();







        values="<!DOCTYPE html>\n" +
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
                "\n" +
                "</head>\n" +
                "<body>  \n" +

                "<font class=\"sub\">\n" +
                "<i class=\"fa fa-pencil-square-o fa-2x\" \n" +
                "aria-hidden=\"true\">\n" +
                "</i> Marks Details</font>" +
        "<div class=\"table-responsive\">"+
                "<table class=\"table table-bordered table-hover\" > " +
                "<tr>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SID</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Name</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Batch Info</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Subject Info</font></th>" +
                "  </tr><tr>" +
                "<td><font color=\"#000\">"+SID+"</td>" +
                "<td><font color=\"#000\">"+Name+"</td>" +
                "<td><font color=\"#000\">"+Batch_Name+"</td>" +
                "<td><font color=\"#000\">"+Subjects+"</td>" +

              /*  "<td><font color=\"#000\">"+Timing+"</td>" +*/

                "  </tr>"+
                "</div>" +

                "<div class=\"table-responsive\">" +
                "<table class=\"table table-bordered table-hover\" >" +
                "\n" +
                " <tr>\n" +
                " <th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Subject Name</font></th>\n" +
                " <th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Test Name</font></th>\n" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Test Date</font></th>\t\n" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">MaxMarks</font></th>\n" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Marks obtained</font></th>" +
                "  </tr>" +
                "   " +str.toString()+
                "</table>" +
                "</div>" +
                "</body>" +
                "</html> ";

        return values;

    }


    //#######################################################################################################

    //End
}
