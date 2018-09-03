package vcc.cretivemindsz.kumar.student_profile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import vcc.cretivemindsz.kumar.utilities.Baseconfig;
import vcc.cretivemindsz.kumar.R;;

/**
 * Created by Kumar on 5/21/2017.
 */

public class Student_Profile extends Fragment {
    private static String TABLE_ID;

    //*********************************************************************************************

    WebView student_details;
    ImageView student_photo;

    public static Student_Profile newInstance(String TABLEID) {
        Student_Profile fragment = new Student_Profile();
        TABLE_ID=TABLEID;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_common_student_profile, container, false);

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
        try {
        student_details = v.findViewById(R.id.web_stu_list);

        student_details.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        student_details.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        student_details.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        student_photo= v.findViewById(R.id.imgvw_student_photo);

            LoadWebview();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void Controllisterners(View v) {



        student_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Query="select Photo as dstatus from Bind_EnrollStudents where SID='"+TABLE_ID+"'";
                Baseconfig.Show_PhotoInDetail(Query, getActivity());

            }
        });


    }
    //*********************************************************************************************

    public void LoadWebview()
    {

        student_details.getSettings().setJavaScriptEnabled(true);
        student_details.setLayerType(View.LAYER_TYPE_NONE, null);
        student_details.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        student_details.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        student_details.getSettings().setDefaultTextEncodingName("utf-8");

        student_details.setWebChromeClient(new WebChromeClient() {
        });

        student_details.setBackgroundColor(0x00000000);
        student_details.setVerticalScrollBarEnabled(true);
        student_details.setHorizontalScrollBarEnabled(true);

        // Toast.makeText(this, "Please wait doctor profile loading..", Toast.LENGTH_SHORT).show();

        //MyDynamicToast.informationMessage(getActivity(), "Please wait attendance details loading..");

       student_details.getSettings().setJavaScriptEnabled(true);

       student_details.getSettings().setAllowContentAccess(true);

       student_details.addJavascriptInterface(new WebAppInterface(getActivity()), "android");
        try {

            student_details.loadDataWithBaseURL("file:///android_asset/", LoadStudentProfile(), "text/html", "utf-8", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

      //  student_details.loadUrl("file:///android_asset/Student_Profile/Student_Profile.html"); //Loading from  assets


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

    public StringBuilder GetSubjectInfo(String Batch_Info)
    {

        StringBuilder str=new StringBuilder();


        String[] batches=Batch_Info.toString().split(",");

        for(int i=0;i<batches.length;i++)
        {
            String str_batch=batches[i].toString();

            String str_total=GetBatchInfo(str_batch);

            str.append(str_total+"\n");

        }

        return str;
    }

    public String GetBatchInfo(String str)
    {
        String batch_info="";
        SQLiteDatabase db=Baseconfig.GetDb();
        Cursor c=db.rawQuery("select Batch_Name||' : '||Subject_Name||' - Every '|| Coaching_Days ||'. From '|| Class_From ||' till '||  Class_To ||'/' as info from Mstr_Batch where Batch_Name='"+str+"';",null);
        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do{

                    batch_info=c.getString(c.getColumnIndex("info"));

                }while(c.moveToNext());
            }
        }
        db.close();
        return batch_info;
    }

    //**********************************************************************************************



    public String LoadStudentProfile()
    {

        String Str_StudentId="",Str_StudentName="",Str_Gender="",Str_DOB="",Str_FatherName="",
                Str_FatherOccupation="",Str_MotherName="",Str_MotherOccupation="",
                Str_Address="",Str_MobileNo="",Str_Subject="",Str_BathcInfo="",Str_SchoolName="",Str_CGPA="",
                Str_ExamNo="",Str_CoachingFee="",Str_FeeAdvance="",Str_FeePaid="-",Str_Photo="";

        String Str_Standard="",Str_JoiningDate="";


        String Ret_Value="";
        SQLiteDatabase db= Baseconfig.GetDb();
        String Query="select * from Bind_EnrollStudents where SID='"+TABLE_ID+"'";
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
                    Str_BathcInfo=c.getString(c.getColumnIndex("Batch_Info"));
                    Str_SchoolName=c.getString(c.getColumnIndex("School_Name"));
                    Str_CGPA=c.getString(c.getColumnIndex("CGPA"));
                    Str_ExamNo=c.getString(c.getColumnIndex("BoardExam_No"));
                    Str_CoachingFee=c.getString(c.getColumnIndex("Coaching_Fee"));
                    Str_FeeAdvance=c.getString(c.getColumnIndex("Fee_Advance"));
                    //Str_FeePaid=c.getString(c.getColumnIndex(""));
                    Str_Photo=c.getString(c.getColumnIndex("Photo"));

                    Str_Standard=c.getString(c.getColumnIndex("Standard"));
                    Str_JoiningDate=c.getString(c.getColumnIndex("Joining_Date"));



                }while (c.moveToNext());

            }

        }


        c.close();
        db.close();




        if(Str_Photo.toString().length()>0)
        {
            Glide.with(student_photo.getContext()).load(new File(Str_Photo)).into(student_photo);

        }
        else
        {
            Glide.with(getActivity()).load(Uri.parse("file:///android_asset/male_avatar.png")).into(student_photo);
        }

        String[] SubjectInfoFull=GetSubjectInfo(Str_BathcInfo).toString().split("/");
        StringBuilder SubjectInfo=new StringBuilder();
        for(int k=0;k<SubjectInfoFull.length;k++)
        {
            SubjectInfo.append(SubjectInfoFull[k].toString()+"<br>");
        }
        //Toast.makeText(getActivity(), "SubjectInfo: "+SubjectInfo, Toast.LENGTH_SHORT).show();


        Ret_Value="<!DOCTYPE html>\n" +
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
                "\n" +
                " \n" +
                "<div class=\"table-responsive\">          \n" +
                "\n" +
                "<font class=\"sub\">\n" +
                "<i class=\"fa fa-user-circle-o fa-2x\" \n" +
                "aria-hidden=\"true\">\n" +
                "</i> Student Details</font>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<table class=\"table table-bordered table-hover\">\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"20%\" style=\"color:#6A1B9A;\"></i><b>  Student ID</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_StudentId+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Student Name</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_StudentName+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Gender</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_Gender+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  DOB</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_DOB+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Father Name</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_FatherName+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" style=\"color:#6A1B9A;\"></i><b>  Father Occupation</b></td> \n" +
                "<td height=\"20\" style=\"color:#000000;\">   "+Str_FatherOccupation+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Mother Name</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_MotherName+"</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Mother Occupation</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_MotherOccupation+"</td>\n" +
                "</tr>\n" +

                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Address</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_Address+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Mobile Number</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_MobileNo+"</td>\n" +
                "</tr>\n" +
               /* "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Subject</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_Subject+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Batch_Info</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_BathcInfo+"</td>\n" +
                "</tr>\n" +
                "\n" +*/
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  School Name</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_SchoolName+"</td>\n" +
                "</tr>\n" +

                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Standard</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_Standard+"</td>\n" +
                "</tr>\n" +


                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  CGPA</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_CGPA+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Board Exam No</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_ExamNo+"</td>\n" +
                "</tr>\n" +
                "\n" +

                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Tuition Joining Date</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_JoiningDate+"</td>\n" +
                "</tr>\n" +


                "\n" +
                "</table>\n" +
                "\n" +
                "</div>\n" +
                "<!---------------------------->\n" +
                "\n" +
                "<div class=\"table-responsive\"> \n" +
                "<font class=\"sub\">\n" +
                "<i class=\"fa fa-inr fa-2x\" \n" +
                "aria-hidden=\"true\">\n" +
                "</i> Fee details</font>\n" +
                "\n" +
                "\n" +
                "<table class=\"table table-bordered  table-hover\">\n" +
                " \n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Coaching Fee</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_CoachingFee+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Fee Advance</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_FeeAdvance+"</td>\n" +
                "</tr>\n" +
                "\n" +
              /*  "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Fee Paid Status</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_FeePaid+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "\n" +*/
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "<!---------------------------->\n" +
                "\n" +
                "<div class=\"table-responsive\"> \n" +
                "\n" +
                "<font class=\"sub\">\n" +
                "<i class=\"fa fa-graduation-cap fa-2x\" \n" +
                "aria-hidden=\"true\">\n" +
                "</i>  Coaching Details</font>\n" +
                "\n" +
                "\n" +
                "<table class=\"table table-bordered  table-hover\">\n" +
                " \n" +
                "<tr>\n" +


                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Batch Details</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+SubjectInfo+"</td>\n" +
                "</tr>\n" +
                "\n" +


               /* "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Batch Details</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_BathcInfo+"</td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#6A1B9A;\"></i><b>  Subject Details</b></td> \n" +
                "<td height=\"20\" width=\"50%\" style=\"color:#000000;\">   "+Str_Subject+"</td>\n" +
                */



                "\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "</table>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<!---------------------------->\n" +
                "\n" +
                " \n" +
                "\n" +
                "<!----------------------------------------------------------------->\n" +
                "\n" +
                "<br/>\n" +
                "<!----------------------------------------------------------------->\n" +
                "\n" +
                "<!----------------------------------------------------------------->\n" +
                "</body>\n" +
                "</html> ";

        return Ret_Value;
    }

    //#######################################################################################################

}
