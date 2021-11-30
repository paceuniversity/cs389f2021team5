package android.libraryeditdocactivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.libraryactivity.document;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.FileProvider;

import com.example.covid_onetool.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditDocActivity extends AppCompatActivity implements View.OnClickListener {

    //接收上个Activity传入的内容
    private String fileType;
    private String fileName;
    private String startDate;
    private String expirationDate;
    private boolean fileStatus;
    private String fileDescription;
    private TextView tv_startdate;
    private TextView tv_expirationdate;
    private TextView tv_uploadFile;

    //接收上个Activity传入的标志
    private int signal = 0;

    //声明控件
    private Spinner typeSpinner;
    //给下拉框的值
    String [] types = new String[]{"Pass","Acid Proof","Vaccination","Others"};
    private Switch simpleSwitch;
    private static final String[] documentTypeArr = { ".pdf", ".png",".jpg",".jpeg"};//允许上传的资料文件类型
    private String filepath="";
    private File fileDir;
    //打开相册的请求码
    public static final  int GALLERY_REQUEST_CODE = 0x01;
    //打开摄像头的请求码
    private static final int CAMERA_REQUEST_CODE = 0x02;

    //加载菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.editdoc_toolbar, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doc);
        simpleSwitch = (Switch) findViewById(R.id.switchStatus); // initiate Switch
        simpleSwitch.setTextOn("Valid"); // displayed text of the Switch whenever it is in checked or on state
        simpleSwitch.setTextOff("Invalid"); // displayed text of the Switch whenever it is in unchecked i.e. off state
        //接受由LibraryActivity传来的doc信息
        Intent intent=getIntent();
        fileType=intent.getStringExtra("fileType");
        fileName=intent.getStringExtra("fileName");
        fileStatus=intent.getBooleanExtra("fileStatus", true);
        startDate=intent.getStringExtra("startDate");
        expirationDate = intent.getStringExtra("expirationDate");
        fileDescription=intent.getStringExtra("fileDescription");
        signal = intent.getIntExtra("signal", 0);
        filepath = intent.getStringExtra("filePath");
        Toolbar toolbar = (Toolbar)findViewById(R.id.edit_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        typeSpinner = findViewById(R.id.inputType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        typeSpinner.setAdapter(adapter);//将适配器和下拉框绑定
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //这个方法里可以对点击事件进行处理
                //i指的是点击的位置,通过i可以取到相应的数据源
                fileType = types[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if(!fileType.equals("")){
            for(int i=0;i<types.length;i++) {
                if(types[i].equals(fileType)) {
                    typeSpinner.setSelection(i);
                }
            }
        }
        final EditText editName = (EditText)findViewById(R.id.inputName);
        editName.setText(fileName);
        simpleSwitch.setChecked(fileStatus);
        final EditText editDescription = (EditText)findViewById(R.id.inputDescription);
        editDescription.setText(fileDescription);

        tv_startdate = findViewById(R.id.tv_startDate);
        tv_startdate.setOnClickListener(this);
        if(!startDate.equals("")){
            tv_startdate.setText(startDate);
        }else{
            //获取系统当前日期
            Calendar calendar = Calendar.getInstance();
            //年
            int year = calendar.get(Calendar.YEAR);
            //月
            int month = calendar.get(Calendar.MONTH)+1;
            //日
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            tv_startdate.setText(month+"/"+day+"/"+year);
            startDate = month+"/"+day+"/"+year;
        }
        tv_expirationdate = findViewById(R.id.tv_expirationDate);
        tv_expirationdate.setOnClickListener(this);
        tv_expirationdate.setText(expirationDate);

        Button uploadFileButton = findViewById(R.id.uploadFileBtn);
        uploadFileButton.setOnClickListener(this);
        tv_uploadFile=findViewById(R.id.tv_uploadFile);
        if(filepath.equals("")) {
            tv_uploadFile.setVisibility(View.GONE);
        }else{
            tv_uploadFile.setText(filepath);
            tv_uploadFile.setVisibility(View.VISIBLE);
            tv_uploadFile.setOnClickListener(this);
        }
    }

    //菜单项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save_button:{
                EditText editName = (EditText)findViewById(R.id.inputName);
                String fileName = editName.getText().toString();
                Boolean fileStatus = simpleSwitch.isChecked();
                EditText editDescription = (EditText)findViewById(R.id.inputDescription);
                String fileDescription = editDescription.getText().toString();
                document mDocument = new document(fileType, fileName, fileStatus, startDate,expirationDate, fileDescription,filepath);
                //储存已经输入的内容
                if(signal==0){
                    mDocument.save();
                    //防止重复新建对象
                    signal = 3;
                }
                //更新，编辑
                else {
                    //防止连续储存一样的内容
                    signal = 3;
//                    ContentValues values = new ContentValues();
//                    values.put("fileType", mDocument.getFileType().toString());
                    //update fileName later
//                    values.put("fileStatus", mDocument.getFileStatus());
//                    values.put("fileTime", mDocument.getFileTime().toString());
//                    values.put("fileDescription", mDocument.getFileDescription().toString());
                  //  DataSupport.updateAll(document.class, values, "fileName=?", fileName );
//                    values.put("fileName", mDocument.getFileName().toString());
                 //   DataSupport.updateAll(document.class, values, "fileName=?", fileName );
                   document updateDocument = new document();
                   updateDocument.setFileType(mDocument.getFileType());
                   updateDocument.setStartDate(mDocument.getStartDate());
                   updateDocument.setExpirationDate(mDocument.getExpirationDate());
                   updateDocument.setFileDescription(mDocument.getFileDescription());
                   updateDocument.setFileName(mDocument.getFileName());
                   updateDocument.update(Integer.parseInt(getIntent().getStringExtra("id")));
                }
                break;
            }
            //点击返回
            case android.R.id.home:{
                if(signal==3){
                    finish();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("");
                    builder.setMessage("Do you want to save the changes?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editName = (EditText)findViewById(R.id.inputName);
                            String fileName = editName.getText().toString();
                            Switch editStatus = (Switch)findViewById(R.id.switchStatus);
                            boolean fileStatus = editStatus.isChecked();
                            EditText editDescription = (EditText)findViewById(R.id.inputDescription);
                            String fileDescription = editDescription.getText().toString();
                            document mDocument = new document(fileType, fileName, fileStatus, startDate,expirationDate, fileDescription,filepath);
                            if(signal==0){
                                mDocument.save();
                                //防止重复新建对象
                                signal = 3;
                            }
                            //更新，编辑
                            else {
                                //防止连续储存一样的内容
                                signal = 3;
                                document docToUpdate = new document();
                                docToUpdate.setFileType(fileType);
                                docToUpdate.setFileName(fileName);
                                docToUpdate.setFileStatus(fileStatus);
                                docToUpdate.setStartDate(startDate);
                                docToUpdate.setExpirationDate(expirationDate);
                                docToUpdate.setFileDescription(fileDescription);
                                docToUpdate.updateAll("fileName=?",fileName);
                            }
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.create().show();
                }
                break;
            }
            default:

        }
        return true;

    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_startDate:
                //显示日历
                Calendar cal = Calendar.getInstance();
                DatePickerDialog date = new DatePickerDialog(EditDocActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {

                        startDate=(month+1)+"/"+day+"/"+year;
                        //将选择日期赋值给textview
                        tv_startdate.setText(startDate);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1);
                date.show();
                break;
            case R.id.tv_expirationDate:
                Calendar cal1 = Calendar.getInstance();
                DatePickerDialog date1 = new DatePickerDialog(EditDocActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        expirationDate=(month+1)+"/"+day+"/"+year;
                        tv_expirationdate.setText(expirationDate);
                        //获取当前日期
                        //年
                        int curyear = cal1.get(Calendar.YEAR);
                        //月
                        int curmonth = cal1.get(Calendar.MONTH)+1;
                        //日
                        int curday = cal1.get(Calendar.DAY_OF_MONTH);
                        String s1=year+"-"+month+"-"+day;
                        String s2=curyear+"-"+curmonth+"-"+curday;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date d1 = sdf.parse(s1);
                            Date d2 = sdf.parse(s2);
                            if (d1.compareTo(d2) > 0) {
                                //当前日期大于所选日期
                                //将effectivestatus改成Invalid
                                simpleSwitch.setChecked(false);
                                //status不可编辑
                                simpleSwitch.setEnabled(false);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH)+1);
                date1.show();
                break;
            case R.id.uploadFileBtn:
                requestCameraPermission();
                break;
            case R.id.tv_uploadFile:
                //查看文件
                Intent intentFile = new Intent(EditDocActivity.this,ViewFileActivity.class);
                intentFile.putExtra("filepath",filepath);
                startActivity(intentFile);
                break;
        }
    }

    /**
     * 当用户点击打开摄像头拍照时，请求获得使用摄像头权限和读写SD卡权限
     */
    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }else{
            showPopueWindow();
        }
    }

    /**
     * 当用户选择是否授予权限后回调此方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPopueWindow();
            } else {
                Toast.makeText(EditDocActivity.this, "Please grant permission to use the camera and read and write external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPopueWindow(){
        View popView = View.inflate(this,R.layout.popupwindow_camera_need,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_file = (Button) popView.findViewById(R.id.btn_pop_file);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST_CODE);
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

                fileDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "photo");
                if(!fileDir.exists()) {
                    fileDir.mkdir();
                }

                long currentTimeMillis = System.currentTimeMillis();
                Date today = new Date(currentTimeMillis);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String title = dateFormat.format(today);
                filepath = fileDir + File.separator + title+".jpg";

                File cameraSavePath = new File(filepath);

                //如果版本大于安卓7.0
                Uri imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(EditDocActivity.this, "com.example.covid_onetool.fileprovider", cameraSavePath);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    imageUri = Uri.fromFile(cameraSavePath);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                popupWindow.dismiss();
            }
        });

        bt_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开系统的文件选择器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(intent, "Please select file"), 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(EditDocActivity.this, "Please install the file manager", Toast.LENGTH_SHORT);
                }
            }
        });

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);
    }


    // 获取文件的真实路径
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fileDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "photo");
        if(!fileDir.exists()) {
            fileDir.mkdir();
        }
        //如果从图库界面返回
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //如果用户选择了相片
            if(data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                //查询我们需要的数据
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                filepath = picturePath;
                setFile();
            }
        }

        //如果从摄像头界面返回
        if(requestCode == CAMERA_REQUEST_CODE) {
            //如果用户拍摄了相片
            setFile();
        }

        //从文件选择器返回
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            filepath = ContentUriUtil.getPath(this,data.getData());
            if(!isVaildFileType(new File(filepath))){
                Toast.makeText(this, "Please select file or picture", Toast.LENGTH_LONG);
            }

            setFile();
        }
    }

    private void setFile(){
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);

        File f=new File(filepath);
        String fileName=f.getName();
        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);

        //如果选择的是图片，将图片转为pdf
        if(prefix.endsWith("png")||prefix.endsWith("jpg")||prefix.endsWith("jpeg")){
            String pdfpath=fileDir+ File.separator + title +".pdf";
            try {
                Document document = new Document(PageSize.A4, 38, 38, 50, 38);
                com.itextpdf.text.Rectangle documentRect = document.getPageSize();
                PdfWriter.getInstance(document, new FileOutputStream(pdfpath));
                document.open();
                Bitmap bmp = MediaStore.Images.Media.getBitmap(
                       getContentResolver(), Uri.fromFile(new File(filepath)));
                com.itextpdf.text.Image image =   com.itextpdf.text.Image.getInstance(filepath);
                if (bmp.getWidth() > documentRect.getWidth()
                        || bmp.getHeight() > documentRect.getHeight()) {
                    //bitmap is larger than page,so set bitmap's size similar to the whole page
                    image.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
                } else {
                    image.scaleAbsolute(bmp.getWidth(), bmp.getHeight());
                }
                document.newPage();
                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);
                image.setBorder(com.itextpdf.text.Image.BOX);
                image.setBorderWidth(15);
                document.add(image);
                document.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            filepath=pdfpath;
        }

        tv_uploadFile.setText(filepath);
        tv_uploadFile.setVisibility(View.VISIBLE);
    }

    private boolean isVaildFileType(File f) {

        for (String type : documentTypeArr) {
            if (f.getName().contains(type))
                return true;
        }
        return false;
    }
}
