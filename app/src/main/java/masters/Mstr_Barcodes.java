package masters;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import adapters.Getter_Setter;
import reports.Reports_Attendance;
import utilities.Baseconfig;
import vcc.coremodule.R;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by KUMAR on 6/14/2017.
 */

public class Mstr_Barcodes extends AppCompatActivity {

    /**
     * Created at 20/05/2017
     * Muthukumar N & Vidhya K
     */
    //*********************************************************************************************
    private Toolbar toolbar;
    ImageView Back, Exit, Options;


    EditText Edt_Search;
    TextView Count;

//*********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mstr_barcodes);

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
        Back = findViewById(R.id.toolbar_back);
        Exit = findViewById(R.id.ic_exit);
        Options = findViewById(R.id.ic_options);
        Edt_Search = findViewById(R.id.search_edttxt);
        Count = findViewById(R.id.txt_count);

        boolean q = Baseconfig.LoadReportsBooleanStatus("select Id as dstatus1 from Bind_EnrollStudents where IsActive='1';");
        if (q == false) {
            MyDynamicToast.informationMessage(Mstr_Barcodes.this, "No details available..");

            Options.setVisibility(View.INVISIBLE);
            return ;
        }
        LoadRecyler(1,"");
    }


    //**************************************************************************************
    private RecyclerView recyclerView;

    public void LoadRecyler(int id, String str) {

        /**
         * To load recycler view old dashboard
         */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Mstr_Barcodes.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Getter_Setter.Mstr_barcodes> DataItems = prepareData(id, str);
        Mstr_BarCode_Adapter adapter = new Mstr_BarCode_Adapter(Mstr_Barcodes.this, DataItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);


    }
    //**********************************************************************************************

    private void Controllisteners() {

        Back.setOnClickListener(view -> LoadBack());

        Exit.setOnClickListener(view -> Baseconfig.ExitSweetDialog(this, Mstr_Batch.class));

        Edt_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) {

                if (Edt_Search.getText().toString().length() > 0) {

                    LoadRecyler(2, cs.toString());
                }else
                {
                    LoadRecyler(1, "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence[] items = {
                        "Generate PDF", "Email", "Close"
                };


                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Mstr_Barcodes.this);
                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].toString().equalsIgnoreCase("Generate PDF")) {

                            GeneratePDF(1);

                        } else if (items[item].toString().equalsIgnoreCase("Email")) {


                            if (!Baseconfig.CheckNW(Mstr_Barcodes.this)) {
                                Baseconfig.SweetDialgos(3, Mstr_Barcodes.this, "Information", "No internet available\nEnable data connection from settings..", "OK");
                                return;
                            }
                            File report_file = new File(Barcode_List_FilePath);

                            if (report_file.exists()) {

                                SendEmail();

                            } else {

                                //Baseconfig.SweetDialgos(4,Reports_Attendance.this,"Information","","OK");

                                new SweetAlertDialog(Mstr_Barcodes.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(Mstr_Barcodes.this.getResources().getString(R.string.information))
                                        .setContentText("Student barcode list not generated..\nDo you want to generate and send email?")
                                        .setCancelText(Mstr_Barcodes.this.getResources().getString(R.string.no))
                                        .setConfirmText(Mstr_Barcodes.this.getResources().getString(R.string.yes))
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

                                                GeneratePDF(2);


                                            }
                                        })
                                        .show();
                            }


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
    void SendEmail() {
        String InstituteMailId = Baseconfig.LoadValue("select Email as dstatus from Bind_InstituteInfo");

        BackgroundMail.newBuilder(this)
                .withUsername(Baseconfig.MailID)
                .withPassword(Baseconfig.MailPassword)
                .withMailto(InstituteMailId)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Students - Bar Code List /  " + Baseconfig.GetDate())
                .withBody("Hi,\nHere with attached the student barcode list. ")
                .withAttachments(Barcode_List_FilePath)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                        new SweetAlertDialog(Mstr_Barcodes.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(Mstr_Barcodes.this.getResources().getString(R.string.information))
                                .setContentText("Mail sent successfully..")
                                .setConfirmText("OK")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .show();

                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }


    String Barcode_List_FilePath = getExternalStorageDirectory() + "/student_barcodes_" + Baseconfig.GetDate() + ".pdf";


    //**********************************************************************************************

    public StringBuilder TableColumnSet()
    {
        StringBuilder stringBuilder=new StringBuilder();

        int i=0;

        SQLiteDatabase db = Baseconfig.GetDb();
        String Query = "select Id,Name,SID,('/student_barcodes/'||SID||'.jpg')as BarCode from Bind_EnrollStudents where IsActive='1'";
        Cursor c = db.rawQuery(Query, null);

        if(c!=null)
        {
            if (c.moveToFirst()) {


                do {

                    int n=c.getCount();
                    int rowcount=4;//need to change this line


                    int addfinalvalue=0;

                    if(n%rowcount==0)
                    {
                        addfinalvalue=0;
                    }
                    else
                    {
                        addfinalvalue=rowcount-((n%rowcount));
                    }
                    Log.e("Final Add columns=",addfinalvalue+"\n");



                    //Table Row and Data Adding in String Builter
                    if(i!=0&&i%rowcount!=0)
                    {
                        //Middle Process
                        stringBuilder.append(addTableData(getImageBase64(c.getString(c.getColumnIndex("BarCode"))).toString(),c.getString(c.getColumnIndex("SID"))));
                    }
                    else if (i==0) {
                        //FirstValue
                        stringBuilder.append("<tr>");
                        stringBuilder.append(addTableData(getImageBase64(c.getString(c.getColumnIndex("BarCode"))).toString(),c.getString(c.getColumnIndex("SID"))));
                    }else if(i%rowcount==0)
                    {
                        //if five column is true close tr

                        stringBuilder.append("</tr>");
                        stringBuilder.append("<tr>");
                        stringBuilder.append(addTableData(getImageBase64(c.getString(c.getColumnIndex("BarCode"))).toString(),c.getString(c.getColumnIndex("SID"))));
                    }




                    if((i==n) && ((n+addfinalvalue)%rowcount==0))
                    {
                        //empty Columns
                        if (addfinalvalue!=0) {

                            for (int i1 = 0; i1 < addfinalvalue; i1++) {
                                //Empty Column Process
                                stringBuilder.append(addTableData("",""));
                            }
                        }

                        stringBuilder.append("</tr>");
                    }



                    //increment i value
                    ++i;



                }while (c.moveToNext());


            }
        }

        //Log.e("stringBuilder=",stringBuilder+"\n");

        return stringBuilder;
    }

    public String addTableData(String value,String SID)
    {

        if(value.toString().equalsIgnoreCase(""))
        {
            value="<td></td>";

            return value;
        }


        return  "<td><img align=\"center\" src=\"data:image/png;base64," + resizeBase64Image(value) +  "\"/><p width=\"80%\" align=\"center\"><h6>"+SID+"</h6></p></td>\n" ;

    }

    //**********************************************************************************************

    void GeneratePDF(int Id) {

        try {


            StringBuffer str1 = new StringBuffer();
            StringBuffer str2= new StringBuffer();
            StringBuilder str3=new StringBuilder();
            StringBuilder str4=new StringBuilder();

            /*SQLiteDatabase db = Baseconfig.GetDb();
            String Query = "select Id,Name,SID,('/student_barcodes/'||SID||'.jpg')as BarCode from Bind_EnrollStudents where IsActive='1'";
            Cursor c = db.rawQuery(Query, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {


                        String BarcodePath = c.getString(c.getColumnIndex("BarCode"));

                        if ((c.getCount() % 5) == 0) {
                            // number is even

                            Log.e("c.getCount() % 5: ", String.valueOf((c.getCount() % 5)));
                            Log.e("c.getCount() % 5: ", String.valueOf((c.getCount() % 5)));
                            Log.e("c.getCount() % 5: ", String.valueOf((c.getCount() % 5)));

                            Toast.makeText(this, "5%"+c.getPosition(), Toast.LENGTH_LONG).show();
                            str1.append("<td><img src=\"data:image/png;base64," + resizeBase64Image(getImageBase64(BarcodePath).toString()) + "\"/><br>" + c.getString(c.getColumnIndex("SID")) + "</br></td>");

                        }

                        else {

                            // number is odd
                            Toast.makeText(this, "ELSE %5"+c.getPosition(), Toast.LENGTH_LONG).show();
                            str1.append("<td><img src=\"data:image/png;base64," + resizeBase64Image(getImageBase64(BarcodePath).toString()) + "\"/><br>" + c.getString(c.getColumnIndex("SID")) + "</br></td>");

                        }



                    } while (c.moveToNext());

                }

            }
            c.close();
            db.close();*/


            //str1.append("<tr>"+ str1.toString()+"</tr>");



           // str3.append(str1.toString());

         /*   String str = "<html>" +
                    "<table  width=\"100%\" style=\"border-color: #000;\" border=\"1\" >\n" +
                    "<tbody>\n" +
                    str3.toString()+
                    str4.toString()+
                    "</tbody>\n" +
                    "</table>" +
                    "</html>";

            Log.e("GeneratePDF: ",str );

*/

            String str = "<html>" +

                    "<style>\n" +
                    "table.roundedCorners { \n" +
                    "  border: 1px solid DarkOrange;\n" +
                    "  border-radius: 13px; \n" +
                    "  border-spacing: 0;\n" +
                    "  }\n" +
                    "table.roundedCorners td, \n" +
                    "table.roundedCorners th { \n" +
                    "  border-bottom: 1px solid DarkOrange;\n" +
                    "  padding: 5px; \n" +
                    "  }\n" +
                    "table.roundedCorners tr:last-child > td {\n" +
                    "  border-bottom: none;\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    " table.innertable {\n" +
                    "    border-collapse: collapse;\n" +
                    "  }\n" +
                    "  table.innertable th, table.innertable td {\n" +
                    "    border: 1px solid #ccc;\n" +
                    "    padding: 10px;\n" +
                    "    text-align: center;\n" +
                    "  }\n" +
                    "  table.innertable tr:nth-child(even) {\n" +
                    "    background-color: #eee;\n" +
                    "  }\n" +
                    "  table.innertable tr:nth-child(odd) {\n" +
                    "    background-color: #fff;\n" +
                    "  } \n" +
                    "  \n" +
                    "  \n" +
                    "</style><body>" +

                    "<table width=\"100%\" class=\"innertable\">\n" +
                    "<tbody>\n" +

                    TableColumnSet()+

                    "</tr></tbody>\n" +
                    "</table>" +

                    "</body></html>";

            // step 1
            Document document = new Document();

            document = new com.itextpdf.text.Document(PageSize.LETTER);
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(Barcode_List_FilePath));
            // step 3
            document.open();
            // step 4

            // CSS
            CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);


            // HTML
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
            htmlContext.setImageProvider(new Base64ImageProvider());

            // Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

            // XML Worker
            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);
            p.parse(new ByteArrayInputStream(str.getBytes()));

            // step 5
            document.close();


            if (Id == 1) {
                LoadPDF(new File(Barcode_List_FilePath));
            } else if (Id == 2) {
                SendEmail();
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //**********************************************************************************************


    class Base64ImageProvider extends AbstractImageProvider {

        @Override
        public com.itextpdf.text.Image retrieve(String src) {
            int pos = src.indexOf("base64,");
            try {
                if (src.startsWith("data") && pos > 0) {
                    byte[] img = com.itextpdf.text.pdf.codec.Base64.decode(src.substring(pos + 7));
                    return com.itextpdf.text.Image.getInstance(img);
                } else {
                    return com.itextpdf.text.Image.getInstance(src);
                }
            } catch (BadElementException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        public String getImageRootPath() {
            return null;
        }
    }


    public StringBuilder getImageBase64(String ImagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(Baseconfig.DATABASE_FILE_PATH+""+ImagePath);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        StringBuilder imgString = new StringBuilder();
        imgString.append(Base64.encodeToString(byteFormat, Base64.NO_WRAP));
        return imgString;
    }


    public String resizeBase64Image(String base64image) {

        byte[] encodeByte = Base64.decode(base64image.toString().getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);


        if (image.getHeight() <= 400 && image.getWidth() <= 400) {
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, 480, 240, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }

    // #######################################################################################################

    public void LoadPDF(File file) {

        if (file.exists()) {
            Uri filepath = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(filepath, "application/pdf");

            Intent intent1 = Intent
                    .createChooser(intent, "Open Barcode list");

            try {
                startActivity(intent1);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
                Toast.makeText(this, "No pdf viewer found!",
                        Toast.LENGTH_LONG).show();

            }

        }

    }

    // #######################################################################################################///////////////

    public static PdfPCell createTextCell(String text) {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text);
        p.setAlignment(Element.ALIGN_RIGHT);
        cell.addElement(p);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static PdfPCell createImageCell(String path) throws DocumentException, IOException {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell(img, true);
        return cell;
    }
    //**********************************************************************************************

    /**
     * Muthukumar N & Vidhya K
     * 24/05/2017
     *
     * @return To get data from local database
     */
    private ArrayList<Getter_Setter.Mstr_barcodes> prepareData(int id, String cs) {

        ArrayList<Getter_Setter.Mstr_barcodes> Dataitems = new ArrayList<>();
        Getter_Setter.Mstr_barcodes obj;

        SQLiteDatabase db = Baseconfig.GetDb();
        //String Query = "select Id,Name,SID,('/student_barcodes/'||SID||'.jpg')as BarCode from Bind_EnrollStudents where IsActive='1'";

        String Query = "";

        if (id == 1) {
            Query = "select Id,Name,SID,('/student_barcodes/'||SID||'.jpg')as BarCode from Bind_EnrollStudents where IsActive='1'";
        } else if (id == 2) {
            Query = "select Id,Name,SID,('/student_barcodes/'||SID||'.jpg')as BarCode from Bind_EnrollStudents where IsActive='1'  and Name like '" + cs + "%' order by Id desc";
        }

        int i=0;

        Cursor c = db.rawQuery(Query, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    i++;
                    obj = new Getter_Setter.Mstr_barcodes();
                    obj.setId(c.getInt(c.getColumnIndex("Id")));
                    obj.setName(c.getString(c.getColumnIndex("Name")));
                    obj.setSID(c.getString(c.getColumnIndex("SID")));
                    obj.setSID_PATH(c.getString(c.getColumnIndex("BarCode")));

                    Dataitems.add(obj);


                } while (c.moveToNext());

            }

        }
        c.close();
        db.close();

        Count.setText(getString(R.string.str_total_count) + String.valueOf(i));

        return Dataitems;

    }

    //*********************************************************************************************

    public void LoadBack() {
        this.finishAffinity();
        Intent back = new Intent(this, Masters.class);
        startActivity(back);
    }


    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        LoadBack();
    }


    //**********************************************************************************************


    /**
     * Created at 15/05/2017
     * Muthukumar N & Vidhya K
     */
    public class Mstr_BarCode_Adapter extends RecyclerView.Adapter<Mstr_BarCode_Adapter.ViewHolder> {

        private ArrayList<Getter_Setter.Mstr_barcodes> DataItems;
        private Context context;


        //**********************************************************************************************

        public Mstr_BarCode_Adapter(Context context, ArrayList<Getter_Setter.Mstr_barcodes> DataItems) {
            this.DataItems = DataItems;
            this.context = context;
        }
        //**********************************************************************************************

        @Override
        public Mstr_BarCode_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mstr_barcode_item, viewGroup, false);
            return new ViewHolder(view);
        }
        //**********************************************************************************************

        @Override
        public void onBindViewHolder(Mstr_BarCode_Adapter.ViewHolder viewHolder, final int i) {


            viewHolder.Sno.setText((i + 1) + ".");
            viewHolder.Name.setText(DataItems.get(i).getName() + " / " + DataItems.get(i).getSID());

            if (DataItems.get(i).getSID_PATH().toString().length() > 0) {

                Glide.with(viewHolder.Barcode.getContext()).load(new
                        File(getExternalStorageDirectory() + "/vcc" +
                        DataItems.get(i).getSID_PATH())).into(viewHolder.Barcode);

            }

            viewHolder.Options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final CharSequence[] items = {
                            "Email", "Close"
                    };


                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Options");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (items[item].toString().equalsIgnoreCase("Email")) {

                                String InstituteMailId = Baseconfig.LoadValue("select Email as dstatus from Bind_InstituteInfo");

                                String FilePath=getExternalStorageDirectory() + "/vcc" +DataItems.get(i).getSID_PATH().toString();

                                BackgroundMail.newBuilder(Mstr_Barcodes.this)
                                        .withUsername(Baseconfig.MailID)
                                        .withPassword(Baseconfig.MailPassword)
                                        .withMailto(InstituteMailId)
                                        .withType(BackgroundMail.TYPE_PLAIN)
                                        .withSubject("Students ("+DataItems.get(i).getName()+"/"+DataItems.get(i).getSID()+") Bar Code Copy - " + Baseconfig.GetDate())
                                        .withBody("Hi,\nHere with attached the student ("+DataItems.get(i).getName()+") barcode. ")
                                        .withAttachments(FilePath.toString())
                                        .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                            @Override
                                            public void onSuccess() {
                                                //do some magic
                                                new SweetAlertDialog(Mstr_Barcodes.this, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText(Mstr_Barcodes.this.getResources().getString(R.string.information))
                                                        .setContentText("Mail sent successfully..")
                                                        .setConfirmText("OK")
                                                        .showCancelButton(true)
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                                sweetAlertDialog.dismiss();
                                                            }
                                                        })
                                                        .show();

                                            }
                                        })
                                        .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                            @Override
                                            public void onFail() {
                                                //do some magic
                                            }
                                        })
                                        .send();

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

        @Override
        public int getItemCount() {
            return DataItems.size();
        }

        //**********************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            //Create Layout controls here like [Ex: TextView Name;]

            TextView Name, Sno;
            ImageView Barcode;
            ImageView Options;

            public ViewHolder(View view) {
                super(view);

                //Here Initialize Those controls
                Sno = view.findViewById(R.id.txt_sno);
                Options = view.findViewById(R.id.img_options);
                Name = view.findViewById(R.id.txt_bat_name);
                Barcode = view.findViewById(R.id.rw_icon);

            }
        }

        //**********************************************************************************************

    }

    //Ends
}
