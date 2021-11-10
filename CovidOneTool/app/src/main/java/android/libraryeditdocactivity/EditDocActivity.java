package android.libraryeditdocactivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.libraryactivity.document;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;


import com.example.covid_onetool.R;

public class EditDocActivity extends AppCompatActivity {

    //接收上个Activity传入的内容
    private String fileType;
    private String fileName;
    private String fileTime;
    private boolean fileStatus;
    private String fileDescription;

    //接收上个Activity传入的标志
    private int signal = 0;

    //加载菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.editdoc_toolbar, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doc);
        Switch simpleSwitch = (Switch) findViewById(R.id.switchStatus); // initiate Switch
        simpleSwitch.setTextOn("Valid"); // displayed text of the Switch whenever it is in checked or on state
        simpleSwitch.setTextOff("Invalid"); // displayed text of the Switch whenever it is in unchecked i.e. off state
        //接受由LibraryActivity传来的doc信息
        Intent intent=getIntent();
        fileType=intent.getStringExtra("fileType");
        fileName=intent.getStringExtra("fileName");
        fileStatus=intent.getBooleanExtra("fileStatus", true);
        fileTime=intent.getStringExtra("fileTime");
        fileDescription=intent.getStringExtra("fileDescription");
        signal = intent.getIntExtra("signal", 0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.edit_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        final EditText editType = (EditText)findViewById(R.id.inputType);
        editType.setText(fileType);
        final EditText editName = (EditText)findViewById(R.id.inputName);
        editName.setText(fileName);
        final Switch editStatus = (Switch) findViewById(R.id.switchStatus);
        editStatus.setChecked(fileStatus);
        final EditText editTime = (EditText)findViewById(R.id.inputTime);
        editTime.setText(fileTime);
        final EditText editDescription = (EditText)findViewById(R.id.inputDescription);
        editDescription.setText(fileDescription);


    }

    //菜单项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save_button:{
                EditText editType = (EditText)findViewById(R.id.inputType);
                String fileType = editType.getText().toString();
                EditText editName = (EditText)findViewById(R.id.inputName);
                String fileName = editName.getText().toString();
                Switch editStatus = (Switch) findViewById(R.id.switchStatus);
                Boolean fileStatus = editStatus.isChecked();
                EditText editTime = (EditText)findViewById(R.id.inputTime);
                String fileTime = editTime.getText().toString();//未来改格式
                EditText editDescription = (EditText)findViewById(R.id.inputDescription);
                String fileDescription = editDescription.getText().toString();
                document mDocument = new document(fileType, fileName, fileStatus, fileTime, fileDescription);
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
                    ContentValues values = new ContentValues();
                    values.put("fileType", mDocument.getFileType().toString());
                    //update fileName later
                    values.put("fileStatus", mDocument.getFileStatus());
                    values.put("fileTime", mDocument.getFileTime().toString());
                    values.put("fileDescription", mDocument.getFileDescription().toString());
                    DataSupport.updateAll(document.class, values, "fileName=?", fileName );
                    values.put("fileName", mDocument.getFileName().toString());
                    DataSupport.updateAll(document.class, values, "fileName=?", fileName );

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
                            EditText editType = (EditText)findViewById(R.id.inputType);
                            String fileType = editType.getText().toString();
                            EditText editName = (EditText)findViewById(R.id.inputName);
                            String fileName = editName.getText().toString();
                            Switch editStatus = (Switch)findViewById(R.id.switchStatus);
                            boolean fileStatus = editStatus.isChecked();
                            EditText editTime = (EditText)findViewById(R.id.inputTime);
                            String fileTime = editTime.getText().toString();//未来改格式
                            EditText editDescription = (EditText)findViewById(R.id.inputDescription);
                            String fileDescription = editDescription.getText().toString();
                            document mDocument = new document(fileType, fileName, fileStatus, fileTime, fileDescription);
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
                                docToUpdate.setFileTime(fileTime);
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



}
