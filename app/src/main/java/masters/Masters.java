package masters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import utilities.Baseconfig;
import core_modules.Task_Navigation;
import vcc.coremodule.R;



/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */

public class Masters extends AppCompatActivity {

    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back,Exit;

    CardView batch,fee,occupation,school,subject,test,barcode;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masters);


        try {


            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    //**********************************************************************************************

    private void GetInitialize()
    {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Back= findViewById(R.id.toolbar_back);
        Exit= findViewById(R.id.ic_exit);

        batch= findViewById(R.id.bat_mst_card);
        fee= findViewById(R.id.fee_mst_card);
        occupation= findViewById(R.id.occ_mst_card);
        school= findViewById(R.id.schl_mst_card);
        subject= findViewById(R.id.sub_mst_card);
        test= findViewById(R.id.test_mst_card);
        barcode= findViewById(R.id.barcode_mst_card);

        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.bat_mst_card));
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.fee_mst_card));
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.occ_mst_card));
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.schl_mst_card));
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.sub_mst_card));
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.test_mst_card));
        YoYo.with(Techniques.FadeIn).duration(1500).playOn(findViewById(R.id.barcode_mst_card));



    }
    //**********************************************************************************************

    private void Controllisteners()
    {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(Masters.this,Masters.class));

        batch.setOnClickListener(view -> {

            Masters.this.finish();
            startActivity(new Intent(Masters.this, Mstr_Batch.class));

        });


        fee.setOnClickListener(view -> {

            Masters.this.finish();
            startActivity(new Intent(Masters.this, Mstr_Fee.class));

        });

        occupation.setOnClickListener(view -> {

            Masters.this.finish();
            startActivity(new Intent(Masters.this, Mstr_Occupation.class));

        });

        school.setOnClickListener(view -> {

            Masters.this.finish();
            startActivity(new Intent(Masters.this, Mstr_School.class));

        });

        subject.setOnClickListener(view -> {

            Masters.this.finish();
            startActivity(new Intent(Masters.this, Mstr_Subject.class));

        });


        test.setOnClickListener(view -> {

            Masters.this.finish();
            startActivity(new Intent(Masters.this, Mstr_Test.class));

        });


        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Masters.this.finish();
                startActivity(new Intent(Masters.this, Mstr_Barcodes.class));

            }
        });



    }
    //**********************************************************************************************

    public void LoadBack()
    {
        this.finishAffinity();
        Intent back=new Intent(Masters.this, Task_Navigation.class);
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
