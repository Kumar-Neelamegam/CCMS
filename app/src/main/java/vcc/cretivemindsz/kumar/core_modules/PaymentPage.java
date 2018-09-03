package vcc.cretivemindsz.kumar.core_modules;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vcc.cretivemindsz.kumar.adapters.Plans_Adapter;
import vcc.cretivemindsz.kumar.adapters.Plans_Data;
import vcc.cretivemindsz.kumar.utilities.Baseconfig;
import vcc.cretivemindsz.kumar.R;;;

public class PaymentPage extends AppCompatActivity {


    RecyclerView recycler_view;

    //*********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_layout);


        try {
            GetInitialize();
            Controllisteners();
            LoadRecycler();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    ProgressDialog dialog;
    private void LoadRecycler() {

        dialog=new ProgressDialog(this);
       dialog.setMessage("Loading plans");
       dialog.setCancelable(false);
       dialog.show();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Baseconfig.FIREBASE_PLANS).orderBy("payid",Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<Plans_Data> plans_data = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        plans_data.add(document.toObject(Plans_Data.class));
                    }
                    Log.e("List of plans: ", plans_data.toString());

                    recycler_view = findViewById(R.id.recycler_view);
                    recycler_view.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(PaymentPage.this, 1);
                    recycler_view.setLayoutManager(layoutManager);

                    Plans_Adapter adapter = new Plans_Adapter(plans_data, PaymentPage.this);
                    recycler_view.setAdapter(adapter);

                    dialog.dismiss();


                } else {
                    Log.e("Getting documents: ", "Error getting documents: ", task.getException());
                }
            }
        });





    }


    private void Controllisteners() {


    }

    private void GetInitialize() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("PLANS");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

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


}//END
