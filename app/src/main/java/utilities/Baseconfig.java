package utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import vcc.coremodule.R;

/**
 * Created by Android on 5/6/2017.
 */

public class Baseconfig {

    public static String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/vcc";
    public static String DATABASE_NAME = DATABASE_FILE_PATH + File.separator + "vcc.db";
    public static String LogoImgPath = "";
    public static String StudentImgPath = "";
    public static String MailID = "info.vetricoachingcentre@gmail.com";
    public static String MailPassword = "vcc2017$$";
    public static String SMS_Username = "";
    public static String SMS_Password = "";
    public static String SMS_SID = "";
    private static int InternetFlag;
    Typeface tfavv1;

    //*********************************************************************************
    public static Dialog showCustomDialog(String title, String message, Activity ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View inflatedLayout = inflater.inflate(R.layout.popup_layout, null, false);
        Dialog builderDialog = new Dialog(ctx);
        builderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView messageView = inflatedLayout.findViewById(R.id.message);
        TextView titleView = inflatedLayout.findViewById(R.id.title);
        ImageView image = inflatedLayout.findViewById(R.id.load_image);
        // Create an animation instance
        Animation an;// = new RotateAnimation(0.0f, 360.0f, 1, 1);
        an = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // Set the animation's parameters
        an.setDuration(700);               // duration in ms
        an.setRepeatCount(Animation.INFINITE);                // -1 = infinite repeated
        //an.setRepeatMode(Animation.RESTART); // reverses each repeat
        an.setFillAfter(true);               // keep rotation after animation

        // Aply animation to image view
        image.setAnimation(an);

        messageView.setText(message);
        titleView.setText(title);
        builderDialog.setContentView(inflatedLayout);
        builderDialog.setCancelable(false);
        builderDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;


        //builderDialog.show();
        return builderDialog;

    }

    /*public static String GetDate_YYYY_DD_MM() {
        String str = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-dd-MM");
        str = dateformat.format(c.getTime());

        return str;

    }*/

    //******************************************************************************************
    public static String GetDate() {
        //References
        //https://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android

        String str = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        str = dateformat.format(c.getTime());

        return str.trim();
    }
    // **************************************************************

    //******************************************************************************************
    public static void SweetDialgos(int Id, Context ctx, String Title, String Message1, String Message2) {

        switch (Id) {


            //A basic message
            case 1:

                new SweetAlertDialog(ctx)
                        .setTitleText(Title)
                        .show();

                break;

            //A title with a text under
            case 2:

                new SweetAlertDialog(ctx)
                        .setTitleText(Title)
                        .setContentText(Message1)
                        .show();

                break;

            //A error message
            case 3:

                new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(Title)
                        .setContentText(Message1)
                        .show();

                break;
            //A warning message
            case 4:

                new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(Title)
                        .setContentText(Message1)
                        .setConfirmText(Message2)
                        .show();

                break;

            case 5:

                new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,delete it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);


                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("Deleted!")
                                        .setContentText("Your imaginary file has been deleted!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();

                break;

        }


    }
    // **************************************************************

    public static void ExitSweetDialog(final Context ctx, final Class<?> className) {

        new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(ctx.getResources().getString(R.string.information))
                .setContentText(ctx.getResources().getString(R.string.message))
                .setCancelText(ctx.getResources().getString(R.string.no))
                .setConfirmText(ctx.getResources().getString(R.string.yes))
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
                        ((Activity) ctx).finishAffinity();


                    }
                })
                .show();

    }

    //*********************************************************************************

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    //*********************************************************************************

    public static boolean CheckNW(Context ctx) {
        ConnectivityManager cn = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {

            Baseconfig.InternetFlag = 1;
            return true;
        } else {

            Baseconfig.InternetFlag = 0;
            return false;
        }
    }


    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Convert number to currency
     *
     * @param Value
     * @return
     */
    public static String ConvertToCurrency(int Value) {

        String return_str = "0";

        return_str = NumberFormat.getNumberInstance(Locale.ENGLISH).format(Value);

        return return_str;
    }

    public static SQLiteDatabase GetDb() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null, null);
        return db;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean CheckTextView(TextView tv) {
        return tv.getText().length() != 0;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean CheckSpinner(Spinner sp) {
        return sp.getSelectedItemPosition() > 0;
    }
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean CheckBox(Checkable sp) {
        return sp.isChecked();
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean EditTextInput(EditText edt) {
        return !edt.getText().toString().startsWith(".") && !edt.getText().toString().endsWith(".");
    }

    //To check EditTextInputLength
    public static boolean EditTextInputLength(EditText edt, int maxLength, Context cntx, String msg) {
        edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

        if (!edt.getText().toString().equals("") && edt.length() >= maxLength) {
            return true;
        } else {
            edt.setError(msg);
            edt.requestFocus();
            return false;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sqlite Helper Implementation
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Loading Autocomplete values */
    public static void LoadValues(AutoCompleteTextView autotxt, Context cntxt, String Query) {
        try {

            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);
            List<String> list = new ArrayList<String>();

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String counrtyname = c.getString(c.getColumnIndex("dvalue"));
                        list.add(counrtyname);

                    } while (c.moveToNext());
                }
            }

            spinner2meth(cntxt, list, autotxt);

            c.close();
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Loading Autocomplete values */
    public static void LoadValuesListView(ListView autotxt, Context cntxt, String Query) {
        try {

            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);
            List<String> list = new ArrayList<String>();

            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String counrtyname = c.getString(c.getColumnIndex("dvalue"));
                        list.add(counrtyname);

                    } while (c.moveToNext());
                }
            }

            ArrayAdapter<String> CountryDataAdapter = new ArrayAdapter<String>(cntxt, android.R.layout.simple_list_item_single_choice, list);
            CountryDataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

            autotxt.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            autotxt.setAdapter(CountryDataAdapter);

            c.close();
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Boolen Return Status
    public static boolean LoadBooleanStatus(String Query) {
        try {
            int havcount = 0;

            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        //PinStatus = c.getString(c.getColumnIndex("dstatus"));
                        havcount++;

                    } while (c.moveToNext());
                }
            }

            c.close();
            db.close();

            return havcount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String LoadValue(String Query) {
        try {
            String value = "";
            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        value = c.getString(c.getColumnIndex("dstatus"));

                    } while (c.moveToNext());
                }
            }

            c.close();
            db.close();

            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean LoadReportsBooleanStatus(String Query) {
        try {
            int havcount = 0;

            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        c.getString(c.getColumnIndex("dstatus1"));
                        havcount++;

                    } while (c.moveToNext());
                }
            }

            c.close();
            db.close();

            return havcount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Boolen Return Status
    public static boolean LoadBooleanStatusCount(String Query, Context context) {
        try {

            String PinStatus = "";
            int havcount = 0;

            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        PinStatus = c.getString(c.getColumnIndex("dstatus"));
                        if (PinStatus.trim().equalsIgnoreCase("0")) {
                            havcount++;
                        }

                    } while (c.moveToNext());
                }
            }

            c.close();
            db.close();

            return havcount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* Loading spinner values */
    public static void LoadValuesSpinner(Spinner spinnertxt, Context cntxt, String Query, String lstadd) {


        try {
            SQLiteDatabase db = GetDb();
            Cursor c = db.rawQuery(Query, null);
            List<String> list = new ArrayList<String>();

            list.add(lstadd);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String counrtyname = c.getString(c.getColumnIndex("dvalue"));
                        list.add(counrtyname);

                    } while (c.moveToNext());
                }
            }

            spinner2meth(cntxt, list, spinnertxt);

            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static <ViewGroup> void spinner2meth(final Context cntxt, List<String> list, Spinner spinnertxt) {


        try {

            // final Typeface tfavv = Typeface.createFromAsset(cntxt.getAssets(), null);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(cntxt, android.R.layout.simple_list_item_1, list) {
                public View getView(int position, View convertView, android.view.ViewGroup parent) {

                    TextView v = (TextView) super.getView(position, convertView, parent);
                    // v.setTypeface(tfavv);
                    v.setTextSize(14);

                    return v;
                }

                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    // v.setTypeface(tfavv);
                    v.setTextSize(14);
                    return v;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            spinnertxt.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static <ViewGroup> void spinner2meth(final Context cntxt, List<String> list, AutoCompleteTextView spinnertxt) {

        try {
            //final Typeface tfavv = Typeface.createFromAsset(cntxt.getAssets(), null);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(cntxt, android.R.layout.simple_dropdown_item_1line, list) {
                public View getView(int position, View convertView, android.view.ViewGroup parent) {

                    TextView v = (TextView) super.getView(position, convertView, parent);
                    // v.setTypeface(tfavv);
                    v.setTextSize(14);
                    return v;
                }

                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    //  v.setTypeface(tfavv);
                    v.setTextSize(14);
                    return v;
                }
            };

            //adapter.setDropDownViewResource(R.layout.simple_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnertxt.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void showSimplePopUp(String msg, String head, Context cntxt) {

        new SweetAlertDialog(cntxt).setTitleText(head).setContentText(msg).show();

    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void Is_Valid_Email(EditText patientname)
            throws NumberFormatException {
        if (patientname.getText().toString().length() <= 0) {

        } else if (!patientname
                .getText()
                .toString()
                .matches(
                        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            patientname.setError("Invalid Email");
        }

    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void customtoast(Context cnt, Activity activity, String textval) {

        // Custom Toast
        LayoutInflater inflator = (LayoutInflater) cnt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflator.inflate(R.layout.layout_toast, activity.findViewById(R.id.custom_toast_id));

        TextView text = layout.findViewById(R.id.texttoast);

        text.setText(textval);

        text.setTextColor(Color.rgb(204, 0, 0));
        text.setBackgroundDrawable(cnt.getResources().getDrawable(R.drawable.bk));
        text.setPadding(10, 10, 10, 10);
        Toast toast = new Toast(cnt.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

    // **************************************************************

    public static boolean CheckSpinner2(Spinner spinner) {
        if (spinner.getSelectedItemPosition() > 0) {
            return true;
        } else {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("*");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Required");//changes the selected item text to this
            return false;
        }

    }

    //***********************************************************************************************
    public static String saveImageToSDCard(String base64image, String imagename, String pathname) {
        try {
            byte[] imageBytes = Base64.decode(base64image, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(imageBytes);
            Bitmap image = BitmapFactory.decodeStream(is);

            String mBaseFolderPath = Baseconfig.DATABASE_FILE_PATH + "/" + pathname + "";

            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdir();
            }

            String mFilePath = mBaseFolderPath + "/" + imagename + ".jpg";

            File file = new File(mFilePath);

            FileOutputStream stream = new FileOutputStream(file);

            if (!file.exists()) {
                file.createNewFile();
            }

            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            is.close();
            image.recycle();

            stream.flush();
            stream.close();

            return mFilePath;
        } catch (Exception e) {
            Log.e("SaveFile", "" + e);
        }

        return "";
    }

    //***********************************************************************************************
    public static String saveURLImageToSDCard(Bitmap img, String imagename) {
        try {

            Bitmap image = img;

            String mBaseFolderPath = Baseconfig.DATABASE_FILE_PATH + "/Patient_Photos/";

            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdir();
            }

            String mFilePath = mBaseFolderPath + "/" + imagename + ".jpg";

            File file = new File(mFilePath);

            FileOutputStream stream = new FileOutputStream(file);

            if (!file.exists()) {
                file.createNewFile();
            }

            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            image.recycle();

            stream.flush();
            stream.close();

            return mFilePath;

        } catch (Exception e) {
            Log.e("SaveFile", "" + e);
        }

        return "";
    }

    //*********************************************************************************

    /**
     * Changing Date format
     * //Receving Date from server as below
     * "4/9/2017 7:11:10 AM"
     * So chaning it into below
     * "dd-MMM-yyyy / hh:mm a" as 09-Apr-2017 / 07:11 AM
     * 09/04/2017
     *
     * @return
     * @Muthukumar N
     */

    public static String DateFormatter(String ServerDate) {
        String return_date = "";

        //9/4/2017 6:16:09 PM
        SimpleDateFormat dateformt = new SimpleDateFormat("MMM-dd-yyyy / hh:mm a");
        return_date = dateformt.format(Date.parse(ServerDate));

        return return_date;

    }


    /**
     * Passing Actdate as below format
     * "yyyy/MM/dd HH:mm:ss"
     * 2017-04-09 16:06:31
     *
     * @return
     * @Muthukumar N
     * 09/04/2017
     */
    public static String DeviceDate() {
        String date = "";

        //2017/04/09 19:51:10
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat dateformt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = dateformt.format(Date.parse(currentDateTimeString));

        return date;
    }


    public static String Device_OnlyDate() {
        String date = "";

        //2017/04/09 19:51:10
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat dateformt = new SimpleDateFormat("yyyy/MM/dd");
        date = dateformt.format(Date.parse(currentDateTimeString));

        return date;
    }


    /**
     * Comparing Actdate as below format
     * "dd-MMM-yyyy / hh:mm a"
     * 09-pr-2017 / 05:11 PM
     *
     * @return
     * @Muthukumar N
     * 09/04/2017
     */
    public static String CompareWithDeviceDate() {
        String date = "";

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        //SimpleDateFormat dateformt = new SimpleDateFormat("MMM-dd-yyyy / hh:mm a");
        SimpleDateFormat dateformt = new SimpleDateFormat("MMM-dd-yyyy");
        date = dateformt.format(Date.parse(currentDateTimeString));

        return date;

    }


    public static String ChangeDateFormat(String date) {
        String date_return = "";

        SimpleDateFormat dateformt = new SimpleDateFormat("MMM-dd-yyyy");
        date_return = dateformt.format(Date.parse(date));

        return date_return;
    }


    public static String GetCurrentYear() {

        String str = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
        str = dateformat.format(c.getTime());

        return str;


    }

    /**
     * Created to show image in detail
     *
     * @Muthukumar 06/04/2017
     */

    public static void Show_PhotoInDetail(String Query, Context ctx) {
        View promptView = LayoutInflater.from(ctx).inflate(R.layout.imageview_zoom_dialog, null);
        final ImageView Photo_imgvw = promptView.findViewById(R.id.imvw_patientphoto);

        final Button Close = promptView.findViewById(R.id.cancel);

        String PhotoPath = Baseconfig.LoadValue(Query);
        Baseconfig.LoadImage(PhotoPath, Photo_imgvw);

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(promptView);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

    }

    public static void LoadImage(String FilePath, ImageView imgvw) {

        //Loading image from any file location into imageView
        Glide.with(imgvw.getContext()).load(new File(FilePath)).into(imgvw);

    }

    //**********************************************************************************************

    public static String CheckValue(String s) {

        if (s != null || s.length() > 0) {
            return s;
        } else {
            s = "0";
            return s;
        }
    }
    //*********************************************************************************

    public static String CheckString(String str) {

        String str_val = "-";

        if (str.toString() != null && !str.toString().isEmpty()) {
            str_val = str;
        } else {
            str_val = "-";
        }


        return str_val;

    }

    public static void getInstitueValues() {
        String str = "";
        SQLiteDatabase db = GetDb();
        String Query = "select * from Bind_InstituteInfo";
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    Baseconfig.MailID = c.getString(c.getColumnIndex("Email"));
                    Baseconfig.MailPassword = c.getString(c.getColumnIndex("EmailPassword"));
                    Baseconfig.SMS_Username = c.getString(c.getColumnIndex("SMSUsername"));
                    Baseconfig.SMS_Password = c.getString(c.getColumnIndex("SMSPassword"));
                    Baseconfig.SMS_SID = c.getString(c.getColumnIndex("SMSSID"));

                } while (c.moveToNext());
            }
        }

        c.close();
        db.close();

    }

    public static String GetMacAddress(Context ctx) {
        String str = "";

        try {
            WifiManager manager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            str = info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String GetUUIDAddress(Context ctx) {
        String str = "";

        try {
            TelephonyManager tManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            String uuid = tManager.getDeviceId();
            str = uuid;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }
    //#######################################################################################################

    /**
     * http://www.itcuties.com/java/hashmap-example/
     * Java HashMap
     */

    void HashMap() {
        // Create the HashMap
        HashMap<String, String> hm = new HashMap<String, String>();

// Put data
        hm.put("Katie", "Android, WordPress");
        hm.put("Magda", "Facebook");
        hm.put("Vanessa", "Tools");
        hm.put("Ania", "Java");
        hm.put("Ania", "JEE");    // !! Put another data under the same key, old value is overridden

// HashMap iteration
        for (String key : hm.keySet())
            System.out.println(key + ":" + hm.get(key));

        //output
        /*Magda:Facebook
        Vanessa:Tools
        Ania:JEE
        Katie:Android, WordPress*/


        // =============================================
        //For checking
        if (hm.containsKey("Katie"))
            System.out.println("HashMap contains key 'Katie'");

        System.out.println();

        if (hm.containsValue("Cooking"))
            System.out.println("HashMap contains value 'Cooking'");
        else
            System.out.println("HashMap DOESN't contain value 'Cooking'");

        //HashMap contains key 'Katie'
        //HashMap DOESN't contain value 'Cooking'
    }

    public void HtmlToPDF(String PDfFileName, String Datavalue) {

        try {


            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.LETTER);
            PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory() + PDfFileName));
            document.open();
            document.addCreationDate();

            HTMLWorker htmlWorker = new HTMLWorker(document);

            htmlWorker.parse(new StringReader(Datavalue));

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getValues(String Query) {
        String str = "";
        SQLiteDatabase db = GetDb();
        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    str = c.getString(c.getColumnIndex("dvalues"));

                } while (c.moveToNext());
            }
        }

        c.close();
        db.close();

        return str;
    }

    public static class CSVWriter {

        /**
         * The character used for escaping quotes.
         */
        public static final char DEFAULT_ESCAPE_CHARACTER = '"';
        /**
         * The default separator to use if none is supplied to the constructor.
         */
        public static final char DEFAULT_SEPARATOR = ',';
        /**
         * The default quote character to use if none is supplied to the
         * constructor.
         */
        public static final char DEFAULT_QUOTE_CHARACTER = '"';
        /**
         * The quote constant to use when you wish to suppress all quoting.
         */
        public static final char NO_QUOTE_CHARACTER = '\u0000';
        /**
         * The escape constant to use when you wish to suppress all escaping.
         */
        public static final char NO_ESCAPE_CHARACTER = '\u0000';
        /**
         * Default line terminator uses platform encoding.
         */
        public static final String DEFAULT_LINE_END = "\n";
        private PrintWriter pw;
        private char separator;
        private char quotechar;
        private char escapechar;
        private String lineEnd;

        /**
         * Constructs CSVWriter using a comma for the separator.
         *
         * @param writer the writer to an underlying CSV source.
         */
        public CSVWriter(Writer writer) {
            this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                    DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
        }

        /**
         * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
         *
         * @param writer     the writer to an underlying CSV source.
         * @param separator  the delimiter to use for separating entries
         * @param quotechar  the character to use for quoted elements
         * @param escapechar the character to use for escaping quotechars or escapechars
         * @param lineEnd    the line feed terminator to use
         */
        public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
            this.pw = new PrintWriter(writer);
            this.separator = separator;
            this.quotechar = quotechar;
            this.escapechar = escapechar;
            this.lineEnd = lineEnd;
        }

        /**
         * Writes the next line to the file.
         *
         * @param nextLine a string array with each comma-separated element as a separate
         *                 entry.
         */
        public void writeNext(String[] nextLine) {

            if (nextLine == null)
                return;

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nextLine.length; i++) {

                if (i != 0) {
                    sb.append(separator);
                }

                String nextElement = nextLine[i];
                if (nextElement == null)
                    continue;
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
                for (int j = 0; j < nextElement.length(); j++) {
                    char nextChar = nextElement.charAt(j);
                    if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                        sb.append(escapechar).append(nextChar);
                    } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                        sb.append(escapechar).append(nextChar);
                    } else {
                        sb.append(nextChar);
                    }
                }
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
            }

            sb.append(lineEnd);
            pw.write(sb.toString());

        }

        /**
         * Flush underlying stream to writer.
         *
         * @throws IOException if bad things happen
         */
        public void flush() {

            pw.flush();

        }

        /**
         * Close the underlying stream writer flushing any buffered content.
         *
         * @throws IOException if bad things happen
         */
        public void close() {
            pw.flush();
            pw.close();
        }
    }

    //#######################################################################################################

    public static void sendSMS(String phoneNo, String msg, Context ctx) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } //#######################################################################################################



    public static Dialog showCustomDialog(String title, String message, Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View inflatedLayout = inflater.inflate(R.layout.activity_purchase_layout, null, false);
        Dialog builderDialog = new Dialog(ctx);
        builderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView messageView = inflatedLayout.findViewById(R.id.message);
        TextView titleView = inflatedLayout.findViewById(R.id.title);


        builderDialog.setContentView(inflatedLayout);
        builderDialog.setCancelable(false);
        builderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builderDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
        return builderDialog;
    }


    //ends
}
