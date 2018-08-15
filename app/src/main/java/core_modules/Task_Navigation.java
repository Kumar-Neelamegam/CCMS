package core_modules;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import bolts.Task;
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


public class Task_Navigation extends AppCompatActivity {


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    Bundle b;

    //*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        try {


            GetInitialize();

            Controllisteners();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    //**********************************************************************************************

    static TextView Title_TextVw;

    String PASSING_DBNAME;

    private void GetInitialize() {

        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Title_TextVw = toolbar.findViewById(R.id.txvw_title);


            navigationView = findViewById(R.id.navigation_view);
            drawerLayout = findViewById(R.id.drawer);


            //Baseconfig.SweetDialgos(3, Task_Navigation.this, "Information2", jsonArray.toString()+"/"+EmployeeName, "Ok");
            View header = navigationView.getHeaderView(0);
            LoadProfile(header);


            setupDrawerContent(navigationView);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //**********************************************************************************************

    void LoadProfile(View header) {
        ImageView Logo = header.findViewById(R.id.img_banner);
        ImageView Option = header.findViewById(R.id.more_option);
        TextView Tv_InsituteName = header.findViewById(R.id.txt_employeename);
        TextView Tv_InstituteAddress = header.findViewById(R.id.txt_employeeemail);

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select * from Bind_InstituteInfo";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Tv_InsituteName.setText(c.getString(c.getColumnIndex("Institute_Name")));
                    Tv_InstituteAddress.setText(c.getString(c.getColumnIndex("Institute_Address")));

                    if (c.getString(c.getColumnIndex("Logo")).toString().length() > 0) {
                        //Glide.with(this).load(Uri.parse(c.getString(c.getColumnIndex("Logo")))).into(Logo);
                        Glide.with(Logo.getContext()).load(new File(c.getString(c.getColumnIndex("Logo")))).into(Logo);

                    } else {
                        Glide.with(Task_Navigation.this).load(Uri.parse("file:///android_asset/logo_vcc.jpg")).into(Logo);
                    }


                } while (c.moveToNext());
            }
        }

        c.close();
        db.close();


        Option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items = {
                        "View", "Edit", "Backup database", "Close"
                };


                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Task_Navigation.this);
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].toString().equalsIgnoreCase("View")) {
                            Task_Navigation.this.finish();
                            Intent next = new Intent(Task_Navigation.this, Institute_View.class);
                            startActivity(next);
                        } else if (items[item].toString().equalsIgnoreCase("Edit")) {
                            Task_Navigation.this.finish();
                            Intent next = new Intent(Task_Navigation.this, Institute_Edit.class);
                            startActivity(next);
                        } else if (items[item].toString().equalsIgnoreCase("Backup database")) {
                            BackupDatabase();
                        } else if (items[item].toString().equalsIgnoreCase("Close")) {

                        }


                    }
                });
                android.support.v7.app.AlertDialog alert = builder.create();
                Dialog dialog = alert;
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();

            }
        });

    }

    //**********************************************************************************************

    public void BackupDatabase() {

        new  CompressFolder().execute();
    }


    //*********************************************************************************
    String Backupfolder;
    String CompressedFileName;

    public class CompressFolder extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog=new ProgressDialog(Task_Navigation.this);

        @Override
        protected void onPreExecute() {

            dialog.setMessage("Backup is inprocess..");
            dialog.setCancelable(false);
            this.dialog.show();
            super.onPreExecute();
        }



        @Override
        protected Void doInBackground(Void... voids) {

            Backupfolder = Environment.getExternalStorageDirectory().getPath() + "/backup_vcc";

            File f = new File(Backupfolder);

            if (!f.exists()) {

                File file = new File(Backupfolder);
                file.mkdirs();

            }
            CompressedFileName= Backupfolder+"/vcc"+Baseconfig.GetDate()+".zip";
            zipFileAtPath(Baseconfig.DATABASE_FILE_PATH, CompressedFileName);

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            new SweetAlertDialog(Task_Navigation.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(Task_Navigation.this.getResources().getString(R.string.information))
                    .setContentText("Backup completed and stored in \n"+CompressedFileName+"\n\nDont delete it..")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();

            super.onPostExecute(aVoid);
        }
    }

    //*********************************************************************************

    /*
 *
 * Zips a file at a location and places the resulting zip file at the toLocation
 * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
 */

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
 *
 * Zips a subfolder
 *
 */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }
    //**********************************************************************************************


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.


        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //*********************************************************************************************

    private void setupDrawerContent(NavigationView navigationView) {


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {


                        selectDrawerItem(menuItem);


                        return true;
                    }
                });


        // Initializing Drawer Layout and ActionBarToggle
        // drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(Task_Navigation.this, drawerLayout, toolbar, 0, 0) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //  //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        selectDrawerItem(navigationView.getMenu().getItem(0));//Myths & Facts New Intent
        navigationView.getMenu().getItem(0).setChecked(true);


    }

    //*********************************************************************************************
    public void selectDrawerItem(MenuItem menuItem) {


        switch (menuItem.getItemId()) {

            case R.id.items0:

                // fragmentClass = DashboardNew.class;
                onSectionAttached(0, menuItem);


                break;

            case R.id.items1:

                // fragmentClass = DashboardNew.class;
                onSectionAttached(1, menuItem);


                break;

            case R.id.items2:

                // fragmentClass = About.class;
                onSectionAttached(2, menuItem);

                break;

            case R.id.items3:

                // fragmentClass = BC.class;
                onSectionAttached(3, menuItem);

                break;

            case R.id.items4:

                // fragmentClass = BC_ReadMore.class;
                onSectionAttached(4, menuItem);

                break;

            case R.id.items5:
                onSectionAttached(5, menuItem);

                //  fragmentClass = BBI.class;

                break;

            case R.id.items6:
                onSectionAttached(6, menuItem);

                // fragmentClass = PinkConnection.class;

                break;

            case R.id.items7:
                onSectionAttached(7, menuItem);

                // fragmentClass = PinkConnection.class;

                break;


            case R.id.items8:
                onSectionAttached(8, menuItem);

                // fragmentClass = PinkConnection.class;

                break;


            case R.id.items9:
                onSectionAttached(9, menuItem);

                // fragmentClass = PinkConnection.class;

                break;


            case R.id.items10:
                onSectionAttached(10, menuItem);

                // fragmentClass = PinkConnection.class;

                break;

            case R.id.items11:
                onSectionAttached(11, menuItem);

                // fragmentClass = PinkConnection.class;

                break;


        }


    }

    //*********************************************************************************************
    Fragment fragment = null;
    Class fragmentClass;

    public void onSectionAttached(int number, MenuItem menuItem) {

        // update the main content by replacing fragments
        Fragment fragment = null;
        FragmentManager fragmentManager;

        String Query = "select IsPaid as dstatus1 from Bind_InstituteInfo where IsPaid=1 and UID='" + Baseconfig.App_UID + "'";
        boolean getPaidStatus = Baseconfig.LoadReportsBooleanStatus(Query);

        switch (number) {

            case 0:


                fragmentClass = DashboardNew.class;
                try {


                    fragment = (Fragment) fragmentClass.newInstance();


                } catch (Exception e) {
                    e.printStackTrace();

                }

                // Insert the fragment by replacing any existing fragment
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();


                break;

            case 1:

                Task_Navigation.this.finish();
                startActivity(new Intent(Task_Navigation.this, Enroll_Students.class));

                break;

            case 2:

                Task_Navigation.this.finish();
                startActivity(new Intent(Task_Navigation.this, Take_Attendance.class));

                break;


            case 3:

                Task_Navigation.this.finish();
                startActivity(new Intent(Task_Navigation.this, Mark_Entry.class));

                break;

            case 4:

                if (getPaidStatus) {
                    Task_Navigation.this.finish();
                    startActivity(new Intent(Task_Navigation.this, Students_Register.class));

                }else
                {
                    BlockUser();


                }

                break;


            case 5:


                if (getPaidStatus) {
                    Task_Navigation.this.finish();
                    startActivity(new Intent(Task_Navigation.this, Attendance_Register.class));
                }else
                {
                    BlockUser();


                }

                break;


            case 6:

                if (getPaidStatus) {
                    Task_Navigation.this.finish();
                    startActivity(new Intent(Task_Navigation.this, Mark_Register.class));
                }else
                {
                    BlockUser();


                }

                break;


            case 7:

                Task_Navigation.this.finish();
                startActivity(new Intent(Task_Navigation.this, Masters.class));

                break;


            case 8:

                if (getPaidStatus) {
                    Task_Navigation.this.finish();
                    startActivity(new Intent(Task_Navigation.this, Reports.class));
                }else
                {
                    BlockUser();


                }

                break;

            case 9:

                if (getPaidStatus) {
                    Task_Navigation.this.finish();
                    startActivity(new Intent(Task_Navigation.this, Send_SMS.class));
                }else
                {
                    BlockUser();


                }

                break;


            case 10:


                try {
                    Intent payment=new Intent(Task_Navigation.this, PaymentPage.class);
                    startActivity(payment);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case 11:

                new SweetAlertDialog(Task_Navigation.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(this.getResources().getString(R.string.alert))
                        .setContentText(getString(R.string.message))
                        .setCancelText(this.getResources().getString(R.string.no))
                        .setConfirmText(this.getResources().getString(R.string.yes))
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
                                Task_Navigation.this.finishAffinity();

                            }
                        })
                        .show();


                break;


        }


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        //setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();


    }

    public void BlockUser()
    {
        /*new SweetAlertDialog(Task_Navigation.this)
                .setTitleText("Information")
                .setContentText("This is demo version.. If need you full version\nContact: N.Muthukumar, 9940741229, kumargtaiv@gmail.com")
                .show();*/


        try {
            Intent payment=new Intent(Task_Navigation.this, PaymentPage.class);
            startActivity(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    //*******************************************************************************************************

    ImageView exit;

    public void Controllisteners() {

        exit = toolbar.findViewById(R.id.ic_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Baseconfig.ExitSweetDialog(Task_Navigation.this, Task_Navigation.class);


            }
        });

    }
    //*******************************************************************************************************


    @Override
    public void onBackPressed() {


        selectDrawerItem(navigationView.getMenu().getItem(0));

    }

    //*******************************************************************************************************


}
