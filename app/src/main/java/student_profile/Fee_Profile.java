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

import utilities.Baseconfig;
import vcc.coremodule.R;

/**
 * Created by Kumar on 5/21/2017.
 */

public class Fee_Profile extends Fragment {

    WebView fee_list;
    private static String TABLE_ID;

    //*********************************************************************************************
    public static Fee_Profile newInstance(String TABLEID) {
        Fee_Profile fragment = new Fee_Profile();
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

        fee_list = v.findViewById(R.id.web_stu_list);

        fee_list.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        fee_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        fee_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

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

        fee_list.getSettings().setJavaScriptEnabled(true);
        fee_list.setLayerType(View.LAYER_TYPE_NONE, null);
        fee_list.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        fee_list.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        fee_list.getSettings().setDefaultTextEncodingName("utf-8");

        fee_list.setWebChromeClient(new WebChromeClient() {
        });

        fee_list.setBackgroundColor(0x00000000);
        fee_list.setVerticalScrollBarEnabled(true);
        fee_list.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();

        //MyDynamicToast.informationMessage(getActivity(), "Please wait attendance details loading..");

        fee_list.getSettings().setJavaScriptEnabled(true);

        fee_list.getSettings().setAllowContentAccess(true);

        fee_list.addJavascriptInterface(new Fee_Profile.WebAppInterface(getActivity()), "android");
        try {

            fee_list.loadDataWithBaseURL("file:///android_asset/", LoadFeeDetails(), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // attendance_list.loadUrl("file:///android_asset/Student_Profile/Student_Fees.html"); //Loading from  assets


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
    public String LoadFeeDetails() {

        String Ret_Value = "";
        String Str_StudentId = "", Str_StudentName = "", Str_BathcInfo = "", Str_CoachingFee = "", Str_FeeAdvance = "";

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Bind_EnrollStudents where SID='" + TABLE_ID + "'";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Str_StudentId = c.getString(c.getColumnIndex("SID"));
                    Str_StudentName = c.getString(c.getColumnIndex("Name"));
                    Str_BathcInfo = c.getString(c.getColumnIndex("Batch_Info"));
                    Str_CoachingFee = c.getString(c.getColumnIndex("Coaching_Fee"));
                    Str_FeeAdvance = c.getString(c.getColumnIndex("Fee_Advance"));


                } while (c.moveToNext());
            }
        }
        c.close();


        String Str_PaidDate = "", Str_PaidFee = "";
        StringBuilder str = new StringBuilder();
        c = null;
        int totalFee_Paid = 0;

        c = db.rawQuery("select Paid_Date,IFNULL(Paid_Fee,'0')as Paid_Fee from Bind_FeeEntry where SID='" + Str_StudentId + "'", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {


                    str.append("<tr>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + c.getString(c.getColumnIndex("Paid_Date")) + "</td>\n" +
                            "<td bgcolor=\"#ffffff\" ><font color=\"#000\">" + c.getString(c.getColumnIndex("Paid_Fee")) + "</td>\n" +
                            "  </tr>  ");

                    String Paid_Fee="0";
                    if(!c.getString(c.getColumnIndex("Paid_Fee")).toString().equalsIgnoreCase(""))
                    {
                        Paid_Fee=c.getString(c.getColumnIndex("Paid_Fee"));
                    }

                    totalFee_Paid += Integer.parseInt(Paid_Fee);


                } while (c.moveToNext());
            }
        }
        c.close();

        String Str_Total_Fee1 = Baseconfig.LoadValue("select Coaching_Fee as dstatus from Bind_EnrollStudents where SID='" + Str_StudentId + "'");
        String Str_PaidFee1 = Baseconfig.CheckValue(Baseconfig.LoadValue("select IFNULL(SUM(Paid_Fee),'0')as dstatus from Bind_FeeEntry where SID='" + Str_StudentId + "'"));

        int balance_Fee = Integer.parseInt(Str_Total_Fee1) - Integer.parseInt(Str_PaidFee1);

        StringBuilder total_str = new StringBuilder();
        String Fee_Status="Pending";

        if (totalFee_Paid > 0) {
            total_str.append("<table class=\"table table-bordered table-hover\" > " +
                    "<tr bgcolor=\"#6A1B9A\">\n" +

                    "<td height=\"20\" width=\"30%\" style=\"color:#6A1B9A;\"></i><b><font color=\"#fff\">Total Paid(Rs.)</font></b></td> \n" +
                    "<td height=\"20\" width=\"50%\" style=\"color:#000000;\"><font color=\"#fff\">   " + totalFee_Paid + "</font></td>\n" +

                    "<td height=\"20\" width=\"30%\" style=\"color:#6A1B9A;\"></i><b><font color=\"#fff\">Balance Fee(Rs.)</font></b></td> \n" +
                    "<td height=\"20\" width=\"50%\" style=\"color:#000000;\"><font color=\"#fff\"> "+ balance_Fee + "</font></td>\n" +

                    "</tr>\n"+
                    "</table>");
        }

        if(balance_Fee==0)
        {
            Fee_Status = "Paid";
        }



        Ret_Value = "<!DOCTYPE html>\n" +
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

                "<font class=\"sub\">" +
                "<i class=\"fa fa-money fa-2x\"" +
                "aria-hidden=\"true\">" +
                "</i> Fee Details</font>" +
                "<div class=\"table-responsive\">" +
                "<table class=\"table table-bordered table-hover\" > " +
                "<tr>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">SID</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Name</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Batch Info</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Coaching Fee</font></th>" +
                "\t<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Fee Status</font></th>" +
                "  </tr><tr>" +
                "<td><font color=\"#000\">" + Str_StudentId + "</td>" +
                "<td><font color=\"#000\">" + Str_StudentName + "</td>" +
                "<td><font color=\"#000\">" + Str_BathcInfo + "</td>" +
                "<td><font color=\"#000\">" + Str_CoachingFee + "</td>" +
                "<td><font color=\"#000\">" + Fee_Status + "</td>" +
                "</tr>" +
                "</div>" +

                "<div class=\"table-responsive\">" +
                "<table class=\"table table-bordered table-hover\" >" +
                "\n" +
                " <tr>\n" +
                " <th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Paid Date</font></th>\n" +
                "<th bgcolor=\"#6A1B9A\" align=\"center\"><font color=\"#fff\">Paid Amount(Rs.)</font></th>\t\n" +
                "  </tr>" +
                "   " + str.toString() +
                "</table>" +

                total_str +//total calculation

                "</div>" +
                "</body>" +
                "</html> ";


        return Ret_Value;

    }

    //#######################################################################################################


    //End
}
