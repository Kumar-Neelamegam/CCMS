package vcc.cretivemindsz.kumar.reports;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import vcc.cretivemindsz.kumar.utilities.Baseconfig;
import vcc.cretivemindsz.kumar.core_modules.Task_Navigation;
import vcc.cretivemindsz.kumar.R;;



/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */
public class Reports  extends AppCompatActivity {

    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back,Exit;

    CardView attendance, fee, mark;
    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


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
        Back= findViewById(R.id.toolbar_back);
        Exit= findViewById(R.id.ic_exit);

        attendance= findViewById(R.id.att_rep_card);
        fee= findViewById(R.id.fee_rep_card);
        mark= findViewById(R.id.mark_rep_card);

        YoYo.with(Techniques.FadeIn).duration(2500).playOn(findViewById(R.id.att_rep_card));
        YoYo.with(Techniques.FadeIn).duration(2500).playOn(findViewById(R.id.fee_rep_card));
        YoYo.with(Techniques.FadeIn).duration(2500).playOn(findViewById(R.id.mark_rep_card));



    }
    //**********************************************************************************************

    private void Controllisteners() {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Reports.this,Reports.class));

        attendance.setOnClickListener(view -> {
            Reports.this.finish();
            startActivity(new Intent(Reports.this, Reports_Attendance.class));

        });


        fee.setOnClickListener(view -> {
            Reports.this.finish();
            startActivity(new Intent(Reports.this, Reports_Fee.class));

        });


        mark.setOnClickListener(view -> {
            Reports.this.finish();
            startActivity(new Intent(Reports.this, Reports_Mark.class));

        });


    }
    //**********************************************************************************************

    public void LoadBack()
    {
        this.finishAffinity();
        Intent back=new Intent(Reports.this, Task_Navigation.class);
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
