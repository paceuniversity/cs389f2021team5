package android.libraryeditdocactivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.libraryactivity.document;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;


import com.example.covid_onetool.R;

import java.io.File;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditDocActivity extends AppCompatActivity implements View.OnClickListener {

    //接收上个Activity传入的内容
    private String fileType;
    private String fileName;
    //private String fileTime;
    private String fileStartDate;
    private String fileExpirationDate;
    private boolean fileStatus;
    private String fileDescription;

    private TextView tv_fileStartDate;
    private TextView tv_fileExpirationDate;
    private TextView tv_showFile;

    //接收上个Activity传入的标志
    private int signal = 0;

    //声明spinner
    private Spinner typeSpinner;
    String [] types = new String[]{"Pass", "Acid Proof", "Vaccination", "Others"};
    private Switch simpleSwitch;
    private static final String[] documentTypeArr = { ".pdf", ".png", ".jpg,", ".jpeg" };//允许上传的文件类型//allowed file types
    private String filePath = "";
    private File fileDir;

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
        //fileTime=intent.getStringExtra("fileTime");
        fileStartDate=intent.getStringExtra("fileStartDate");
        fileExpirationDate=intent.getStringExtra("fileExpirationDate");
        fileDescription=intent.getStringExtra("fileDescription");
        signal = intent.getIntExtra("signal", 0);
        filePath = intent.getStringExtra("filePath");
        Toolbar toolbar = (Toolbar)findViewById(R.id.edit_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        //final EditText editType = (EditText)findViewById(R.id.inputType);
        //editType.setText(fileType);

        typeSpinner = findViewById(R.id.inputType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);//创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //设置下拉列表框的下拉选项样式
        typeSpinner.setAdapter(adapter);//绑定下拉框和适配器
        //spinner监听器
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                fileType = types[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if(!fileType.equals("")){
            for(int i=0; i<types.length; i++){
                if(types[i].equals(fileType)){
                    typeSpinner.setSelection(i);
                }
            }
        }

        final EditText editName = (EditText)findViewById(R.id.inputName);
        editName.setText(fileName);
        //final Switch editStatus = (Switch) findViewById(R.id.switchStatus);
        //editStatus.setChecked(fileStatus);
        simpleSwitch.setChecked(fileStatus);
        //final EditText editTime = (EditText)findViewById(R.id.inputTime);
        //editTime.setText(fileTime);
        final EditText editDescription = (EditText)findViewById(R.id.inputDescription);
        editDescription.setText(fileDescription);

        tv_fileStartDate = findViewById(R.id.tv_fileStartDate);
        tv_fileStartDate.setOnClickListener(this);
        if(!fileStartDate.equals("")){
            tv_fileStartDate.setText(fileStartDate);
        }else{
            //获取系统日期
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            tv_fileStartDate.setText(month +"/"+ day +"/"+ year);
            fileStartDate = month +"/"+ day +"/"+ year;
        }
        tv_fileExpirationDate = findViewById(R.id.tv_fileExpirationDate);
        tv_fileExpirationDate.setOnClickListener(this);
        tv_fileExpirationDate.setText(fileExpirationDate);

        Button uploadFileButton = findViewById(R.id.uploadFileBtn);
        uploadFileButton.setOnClickListener(this);
        tv_showFile = findViewById(R.id.tv_showFile);
        //根据有无文件设置按钮是否可见，此处按钮为text view形式
        if(filePath.equals("")){
            tv_showFile.setVisibility(View.GONE);
        }else{
            tv_showFile.setText(filePath);
            tv_showFile.setVisibility(View.VISIBLE);
            tv_showFile.setOnClickListener(this);
        }
    }

    //菜单项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save_button:{
                //EditText editType = (EditText)findViewById(R.id.inputType);
                //String fileType = editType.getText().toString();
                EditText editName = (EditText)findViewById(R.id.inputName);
                String fileName = editName.getText().toString();
                //Switch editStatus = (Switch) findViewById(R.id.switchStatus);
                Boolean fileStatus = simpleSwitch.isChecked();
                //EditText editTime = (EditText)findViewById(R.id.inputTime);
                //String fileTime = editTime.getText().toString();//未来改格式
                EditText editDescription = (EditText)findViewById(R.id.inputDescription);
                String fileDescription = editDescription.getText().toString();
                document mDocument = new document(fileType, fileName, fileStatus, fileStartDate, fileExpirationDate, fileDescription, filePath);
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
                    /* 这段代码会导致重名文件数据合并
                    ContentValues values = new ContentValues();
                    values.put("fileType", mDocument.getFileType().toString());
                    //update fileName later
                    values.put("fileStatus", mDocument.getFileStatus());
                    values.put("fileTime", mDocument.getFileTime().toString());
                    values.put("fileDescription", mDocument.getFileDescription().toString());
                    DataSupport.updateAll(document.class, values, "fileName=?", fileName );
                    values.put("fileName", mDocument.getFileName().toString());
                    DataSupport.updateAll(document.class, values, "fileName=?", fileName );
                    */
                    document updateDocument = new document();
                    updateDocument.setFileType(mDocument.getFileType());
                    updateDocument.setFileName(mDocument.getFileName());
                    updateDocument.setFileStartDate(mDocument.getFileStartDate());
                    updateDocument.setFileExpirationDate(mDocument.getFileExpirationDate());
                    updateDocument.setFileDescription(mDocument.getFileDescription());
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
                            //EditText editType = (EditText)findViewById(R.id.inputType);
                            //String fileType = editType.getText().toString();
                            EditText editName = (EditText)findViewById(R.id.inputName);
                            String fileName = editName.getText().toString();
                            Switch editStatus = (Switch)findViewById(R.id.switchStatus);
                            boolean fileStatus = editStatus.isChecked();
                            //EditText editTime = (EditText)findViewById(R.id.inputTime);
                            //String fileTime = editTime.getText().toString();//未来改格式
                            EditText editDescription = (EditText)findViewById(R.id.inputDescription);
                            String fileDescription = editDescription.getText().toString();
                            document mDocument = new document(fileType, fileName, fileStatus, fileStartDate, fileExpirationDate, fileDescription, filePath);
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
                                //docToUpdate.setFileTime(fileTime);
                                docToUpdate.setFileStartDate(fileStartDate);
                                docToUpdate.setFileExpirationDate(fileExpirationDate);
                                docToUpdate.setFileDescription(fileDescription);
                                //docToUpdate.updateAll("fileName=?",fileName);
                                docToUpdate.updateAll("fileName=?", fileName);
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
        switch (v.getId()){
            case R.id.tv_fileStartDate:
                //显示日历
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog date = new DatePickerDialog(EditDocActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fileStartDate = (month+1) +"/"+ dayOfMonth +"/"+ year;
                        tv_fileStartDate.setText(fileStartDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+1);
                date.show();
                break;

            case R.id.tv_fileExpirationDate:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog date1 = new DatePickerDialog(EditDocActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fileExpirationDate = (month+1) +"/"+ dayOfMonth +"/"+ year;
                        tv_fileExpirationDate.setText(fileExpirationDate);
                        int curYear = calendar1.get(Calendar.YEAR);
                        int curMonth = calendar1.get(Calendar.MONTH)+1;
                        int curDay = calendar1.get(Calendar.DAY_OF_MONTH);
                        String str1 = year +"-"+ month +"-"+ dayOfMonth;
                        String str2 = curYear +"-"+ curMonth +"-"+ curDay;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try{
                            Date d1 = sdf.parse(str1);
                            Date d2 = sdf.parse(str2);
                            //根据expirationDate自动更改file status，同时该选项对用户不可编辑
                            if(d1.compareTo(d2) > 0){
                                simpleSwitch.setChecked(false);
                                simpleSwitch.setEnabled(false);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH)+1);
                date1.show();
                break;

            case R.id.uploadFileBtn:
                //选择pdf文件或相片
                //打开系统的文件选择器
                Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                try{
                    startActivityForResult(Intent.createChooser(intent, "Please select a file or picture :-)"), 1);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(this, "Please install the file manager", Toast.LENGTH_SHORT);
                }
                break;

            case R.id.tv_showFile:
                //查看文件
                Intent intentFile = new Intent(EditDocActivity.this, ViewFileActivity.class);
                intentFile.putExtra("filePath", filePath);
                startActivity(intentFile);
                break;
        }
    }
    //获取文件路径//get file path
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            filePath = ContentUriUtil.getPath(this, data.getData());
            System.out.println("Single file path ---- " + filePath);
            if(!isValidfFileType(new File(filePath))){
                Toast.makeText(this, "Please select a file or picture", Toast.LENGTH_LONG);
            }
            fileDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "photo");
            if(!fileDir.exists()){
                fileDir.mkdir();
            }

            long currentTimeMills = System.currentTimeMillis();
            Date today = new Date(currentTimeMills);
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String title = dataFormat.format(today);

            File file1 = new File(filePath);
            String fileName = file1.getName();
            String prefix = fileName.substring(fileName.lastIndexOf(".")+1);//获取文件后缀名
            //将图片文件自动转为pdf文件
            if(prefix.endsWith("png") || prefix.endsWith("jpg") || prefix.endsWith("jpeg")){
                imageToPdf.imageToPDF(filePath, fileDir + File.separator + title + ".pdf");
                filePath = fileDir + File.separator + title + ".pdf";
                tv_showFile.setText(filePath);
                tv_showFile.setVisibility(View.VISIBLE);
            }
        }
    }


    private boolean isValidfFileType(File file){
        for(String type : documentTypeArr){
            if(file.getName().contains(type)){
                return false;
            }
        }
        return false;
    }
}