package adapters;

import android.app.Activity;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import utilities.Baseconfig;
import vcc.coremodule.R;

public class PaymentPage extends AppCompatActivity {


    Button Button_Basic, Button_Pro, Button_Ultimate;
    TextView CustomerName;
    InstapayListener listener;

    //*********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_layout);


        try {
            GetInitialize();
            Controllisteners();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void Controllisteners() {

        String email = Baseconfig.App_Email;
        String phone = Baseconfig.App_Mobile;
        String purpose = getString(R.string.app_name);
        String buyername = Baseconfig.App_Owner_Name;

        Button_Basic.setOnClickListener(v -> {
            String amount = "3250";
            callInstamojoPay(email, phone, amount, purpose, buyername);
        });
        Button_Pro.setOnClickListener(v -> {
            String amount = "5600";
            callInstamojoPay(email, phone, amount, purpose, buyername);

        });

        Button_Ultimate.setOnClickListener(v -> {
            String amount = "10600";
            callInstamojoPay(email, phone, amount, purpose, buyername);

        });

    }

    private void GetInitialize() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("PLANS");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Button_Basic = findViewById(R.id.bttn_pay1);
            Button_Pro = findViewById(R.id.bttn_pay2);
            Button_Ultimate = findViewById(R.id.bttn_pay3);
            CustomerName = (TextView) findViewById(R.id.txtvw_cardname);

            String GetCustomerName = Baseconfig.LoadValue("select Institute_Name as dstatus from Bind_InstituteInfo");
            CustomerName.setText(GetCustomerName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
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
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {


                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                SQLiteDatabase db = Baseconfig.GetDb();
                db.execSQL("Update Bind_InstituteInfo set IsPaid=1, PaidDate='', StudentCount=''");//if ispaid==1
                db.close();

            }

            @Override
            public void onFailure(int code, String reason) {

                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG).show();
                SQLiteDatabase db = Baseconfig.GetDb();
                db.execSQL("Update Bind_InstituteInfo set IsPaid=0");//if ispaid==1
                db.close();
            }
        };
    }


}//END
