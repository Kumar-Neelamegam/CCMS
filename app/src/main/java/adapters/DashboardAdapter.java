package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


import entry_activities.Enroll_Students;
import entry_activities.Mark_Entry;
import entry_activities.Send_SMS;
import entry_activities.Take_Attendance;
import masters.Masters;
import registers_activities.Attendance_Register;
import registers_activities.Mark_Register;
import registers_activities.Students_Register;
import reports.Reports;
import utilities.Baseconfig;
import vcc.coremodule.R;



/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */
public class DashboardAdapter  extends RecyclerView.Adapter<DashboardAdapter.ViewHolder>
{

    private ArrayList<Getter_Setter.Dashboard_Dataobjects> DataItems;
    private Context context;
    Fragment fragment = null;
    Class fragmentClass;
    String SESSION_DATABASENAME;
    boolean Flag;

    //**********************************************************************************************

    public DashboardAdapter(Context context,ArrayList<Getter_Setter.Dashboard_Dataobjects> DataItems,String SESSION_DATABASENAME,boolean Flag) {
        this.DataItems = DataItems;
        this.context = context;
        this.SESSION_DATABASENAME = SESSION_DATABASENAME;
        this.Flag=Flag;
    }
    //**********************************************************************************************

    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_rowitem, viewGroup, false);
        return new ViewHolder(view);
    }
    //**********************************************************************************************

    @Override
    public void onBindViewHolder(DashboardAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.Title.setText(DataItems.get(i).getTitle_Name());
        viewHolder.ImageIcon.setImageDrawable(context.getResources().getDrawable(DataItems.get(i).getIcon()));

        viewHolder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int ID = DataItems.get(i).getId();

                if(Flag)
                {
                    ORIGINAL_ADAPTER(ID);
                }else
                {
                    DUMMY_ADAPTER(ID);
                }

            }
        });

    }
    //**********************************************************************************************

    private void DUMMY_ADAPTER(int ID) {


        switch(ID)
        {
            case 0:
                ((Activity)context).finish();
                Intent Next=new Intent(context, Enroll_Students.class);
                context.startActivity(Next);

                break;

            case 1:
                ((Activity)context).finish();
                Intent Next1=new Intent(context, Take_Attendance.class);
                context.startActivity(Next1);

                break;

            case 2:
                ((Activity)context).finish();
                Intent Next2=new Intent(context, Mark_Entry.class);
                context.startActivity(Next2);

                break;


            case 3:
                BlockUser();

                break;

            case 4:
                BlockUser();

                break;

            case 5:
                BlockUser();

                break;

            case 6:

                ((Activity)context).finish();
                Intent Next6=new Intent(context, Masters.class);
                context.startActivity(Next6);

                break;

            case 7:
                 BlockUser();

                break;
            case 8:

                  BlockUser();

                break;

        }

    }


    private void ORIGINAL_ADAPTER(int ID) {


        switch(ID)
        {
            case 0:
                ((Activity)context).finish();
                Intent Next=new Intent(context, Enroll_Students.class);
                context.startActivity(Next);

                break;

            case 1:
                ((Activity)context).finish();
                Intent Next1=new Intent(context, Take_Attendance.class);
                context.startActivity(Next1);

                break;

            case 2:
                ((Activity)context).finish();
                Intent Next2=new Intent(context, Mark_Entry.class);
                context.startActivity(Next2);

                break;


            case 3:
                ((Activity)context).finish();
                Intent Next3=new Intent(context, Students_Register.class);
                context.startActivity(Next3);
                break;

            case 4:
                ((Activity)context).finish();
                Intent Next4=new Intent(context, Attendance_Register.class);
                context.startActivity(Next4);
                break;

            case 5:
                ((Activity)context).finish();
                Intent Next5=new Intent(context, Mark_Register.class);
                context.startActivity(Next5);
                break;

            case 6:

                ((Activity)context).finish();
                Intent Next6=new Intent(context, Masters.class);
                context.startActivity(Next6);

                break;

            case 7:
                ((Activity)context).finish();
                Intent Next7=new Intent(context, Reports.class);
                context.startActivity(Next7);
                break;
            case 8:

                ((Activity)context).finish();
                Intent Next8=new Intent(context, Send_SMS.class);
                context.startActivity(Next8);
                break;

        }

    }

    //**********************************************************************************************
    public void BlockUser()
    {
       /* try {
            new SweetAlertDialog(context)
                    .setTitleText("Information")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setType("plain/text");
                            sendIntent.setData(Uri.parse("kumargtaiv@gmail.com"));
                            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "kumargtaiv@gmail.com" });
                            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "CCMS | FULL VERSION | PURCHASE | REQUEST");
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi, We are looking forward to purchase the full version of this CCMS android application");
                            ((Activity)context).startActivity(sendIntent);
                        }
                    })
                    .setContentText("This is demo version.. If you need full version\nContact: N.Muthukumar, kumargtaiv@gmail.com")
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Dialog dialog=Baseconfig.showCustomDialog("","",context);
        dialog.show();


    }
    @Override
    public int getItemCount() {
        return DataItems.size();
    }

    //**********************************************************************************************

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView Title;
        private ImageView ImageIcon;
        LinearLayout Layout;
        public ViewHolder(View view) {
            super(view);

            Title = view.findViewById(R.id.title_txt);
            ImageIcon = view.findViewById(R.id.img_icon);
            Layout= view.findViewById(R.id.layout_item);

        }
    }

    //**********************************************************************************************

}
