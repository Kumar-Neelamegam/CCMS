package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import utilities.Baseconfig;
import utilities.LocalSharedPreference;
import vcc.coremodule.R;



public class Plans_Adapter extends RecyclerView.Adapter<Plans_Adapter.PriceViewHolder> {

    List<Plans_Data> pricingList;
    String email="";
    String phone="";
    String purpose="";
    String buyername="";
    String GetCustomerName = "";
    Context ctx;
    LocalSharedPreference sharedPreference;

    public Plans_Adapter(List<Plans_Data> pricingList, Context ctx) {
        this.pricingList = pricingList;

        this.ctx= ctx;
         email = Baseconfig.App_Email;
         phone = Baseconfig.App_Mobile;
         purpose = ctx.getString(R.string.app_name);
         buyername = Baseconfig.App_Owner_Name;
        GetCustomerName = Baseconfig.LoadValue("select Institute_Name as dstatus from Bind_InstituteInfo");
        sharedPreference = new LocalSharedPreference(ctx);
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PriceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_rowitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {


        Plans_Data pricing=pricingList.get(position);


        holder.cardName.setText(GetCustomerName);
        holder.cardType.setText(pricing.plantitle);
        holder.noOfStuent.setText(pricing.validupto);
        holder.perStudent.setText(pricing.perstudent);
        holder.totalPrice.setText(pricing.totalprice);
        holder.cardView.setCardBackgroundColor(Color.parseColor(pricing.cardcolor));

        if(!Baseconfig.ExpiryStatus || !sharedPreference.getBoolean(Baseconfig.Preference_ExpiryStatus))
        {
            if(position==0)
            {
                holder.Payment.setText("Accept");

            }

        }else//true na
        {
            if(position==0) {
                holder.Payment.setVisibility(View.GONE);
            }

        }

        holder.Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    if(position==0 && holder.Payment.getText().equals("Accept"))//Trail user
                    {
                        UpdateResultStatus("5","0");

                        return;
                    }else
                    {
                        if (Baseconfig.CheckNW(ctx)) {

                            callInstamojoPay(email, phone, String.valueOf(holder.totalPrice.getText()), purpose, buyername, pricing.validupto.split(" ")[0], String.valueOf(pricing.payid));

                        }
                        else
                        {

                            Baseconfig.SweetDialgos(4, ctx, "Information", " No internet connection found..try later ", "OK");

                        }

                    }


                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

     //   UpdateToFirebase("1","350");


    }

    @Override
    public int getItemCount() {
        return pricingList.size();
    }


    static class PriceViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.parentcard)
        CardView cardView;
        @BindView(R.id.noOfStuent)
        TextView noOfStuent;
        @BindView(R.id.perStudent)
        TextView perStudent;
        @BindView(R.id.cardName)
        TextView cardName;
        @BindView(R.id.totalPrice)
        TextView totalPrice;
        @BindView(R.id.cardType)
        TextView cardType;
        @BindView(R.id.bttn_pay1)
        Button Payment;

        public PriceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }



    InstapayListener listener;

    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername, String StudentCount, String PayId) {
        try {
            final Activity activity = (Activity) this.ctx;

            InstamojoPay instamojoPay = new InstamojoPay();
            IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
            ctx.registerReceiver(instamojoPay, filter);
            JSONObject pay = new JSONObject();
            try {
                pay.put("email", email);
                pay.put("phone", phone);
                pay.put("purpose", purpose);
                pay.put("amount", amount);
                pay.put("name", buyername);
                pay.put("send_sms", true);
                pay.put("send_email", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            initListener(StudentCount, PayId);
            instamojoPay.start(activity, pay, listener);
            UpdateResultStatus(StudentCount, PayId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateResultStatus(String StudentCount, String PayId) {
        try {
            SQLiteDatabase db = Baseconfig.GetDb();

            String UpdateStudentCount="Update Bind_InstituteInfo set IsPaid=1, PaidDate='"+Baseconfig.GetDate()+"', " +"StudentCount=StudentCount+'"+StudentCount+"', " +"PayId='"+PayId+"'";
            Log.e("UpdateStudentCountQuery: ", UpdateStudentCount);
            db.execSQL(UpdateStudentCount);//if ispaid==1
            db.close();

            UpdateToFirebase(PayId,StudentCount);

            ((Activity)ctx).finish();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void initListener(String StudentCount, String PayId) {

        try {

            listener = new InstapayListener() {
                @Override
                public void onSuccess(String response) {

                    Toast.makeText(ctx, response, Toast.LENGTH_LONG).show();

                    UpdateResultStatus(StudentCount, PayId);

                }

                @Override
                public void onFailure(int code, String reason) {

                    Toast.makeText(ctx, "Failed: " + reason, Toast.LENGTH_LONG).show();
                    SQLiteDatabase db = Baseconfig.GetDb();
                    db.execSQL("Update Bind_InstituteInfo set IsPaid=0");//if ispaid==1
                    db.close();
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void UpdateToFirebase(String PAYID, String STUDENTCOUNT) {


        try {
            FirebaseFirestore db=FirebaseFirestore.getInstance();

            Map<String, Object> newContact = new HashMap<>();
            newContact.put("IsPaid", 1);
            newContact.put("PayId", PAYID);
            newContact.put("PaidDate", Baseconfig.Device_OnlyDate());
            int getCurrentPlanCount = Integer.parseInt(Baseconfig.LoadValueInt("select StudentCount as dstatus from Bind_InstituteInfo where IsPaid=1 and UID='" + Baseconfig.App_UID + "'"));
            newContact.put("StudentCount", getCurrentPlanCount+STUDENTCOUNT);

            db.collection(Baseconfig.FIREBASE_INSTITUTE_USERS).document(Baseconfig.App_UID).update(newContact)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("TAG", e.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}//END

