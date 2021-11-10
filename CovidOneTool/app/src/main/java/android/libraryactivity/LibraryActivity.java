package android.libraryactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.libraryeditdocactivity.EditDocActivity;
import android.infoactivity.InfoActivity;
import android.widget.Toast;
import org.litepal.crud.DataSupport;
import android.os.Bundle;
import android.selftestactivity.SelfTestActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {
    //要删除的item的标记, String 为fileName
    HashMap<Integer,String> deleStr = new HashMap<>();
    //设置是否在onStart更新数据源的标记
    private int update=0;
    //储存document对象的数组
    private List<document> documentList=new ArrayList<>();
    //adapter
    private documentAdapter adapter;
    //是否处于待删除状态，区分点击和长按
    private boolean isDeleteState=false;
    //网格布局管理器
    //GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
    //线性布局管理器
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_library);

        //从数据库中获取document对象
        documentList = DataSupport.findAll(document.class);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_doc);
        recyclerView.setLayoutManager(linearLayoutManager);

        //适配器初始化，入参为数据源
        adapter = new documentAdapter(documentList, false);
        recyclerView.setAdapter(adapter);
        //初始化[add]和[delete]按钮
        Button add = (Button)findViewById(R.id.button_AddDoc);
        Button delete = (Button)findViewById(R.id.button_DeleteDoc);
        //点击[add]后，跳转至新建页
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goEditDocActivity = new Intent(LibraryActivity.this, EditDocActivity.class);
                goEditDocActivity.putExtra("fileType", "");
                goEditDocActivity.putExtra("fileName", "");
                goEditDocActivity.putExtra("fileStatus", true);
                goEditDocActivity.putExtra("fileTime", "");
                goEditDocActivity.putExtra("fileDescription", "");
                goEditDocActivity.putExtra("signal",0);//0 for New document
                startActivity(goEditDocActivity);
            }
        });

        //点击[delete]后，删除item
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelections();
            }
        });

        //为RecylerView添加点击时间响应和长按事件响应
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if(!isDeleteState && documentList.size()>0){
                    document mDocument = documentList.get(position);
                    String fileType = mDocument.getFileType().toString();
                    String fileName = mDocument.getFileName().toString();
                    boolean fileStatus = mDocument.getFileStatus();
                    String fileTime = mDocument.getFileTime().toString();
                    String fileDescription = mDocument.getFileDescription().toString();
                    Intent intent = new Intent(LibraryActivity.this, EditDocActivity.class);
                    intent.putExtra("fileType", fileType);
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("fileStatus", fileStatus);
                    intent.putExtra("fileTime", fileTime);
                    intent.putExtra("fileDescription", fileDescription);
                    intent.putExtra("signal", 1);
                    startActivity(intent);
                }else if(documentList.size()>0){
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                        deleStr.remove(position);
                    }else{
                        deleStr.put(position, documentAdapter.mDocumentList.get(position).getFileName());
                        checkBox.setChecked(true);
                    }
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

                if(!isDeleteState && documentList.size()>0){
                    isDeleteState = true;
                    deleStr.clear();
                    documentAdapter.isSelected.put(position, true);
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_doc);
                    adapter = new documentAdapter(documentList, true);
                    recyclerView.setAdapter(adapter);
                    deleStr.put(position, documentAdapter.mDocumentList.get(position).getFileName());

                }

            }
        }));

    }

    //用户返回该Activity
    @Override
    protected void onStart(){
        super.onStart();
        if(update==1){
            documentList.clear();
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_doc);
            List<document> docs = DataSupport.findAll(document.class);
            for(document mDocument: docs){
                documentList.add(mDocument);
            }
            adapter = new documentAdapter(documentList, false);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        update = 1;
    }

    //删除函数
    private void deleteSelections(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(deleStr.size()==0){
            builder.setTitle("oops").setMessage("Please long press to select the documents you want to delete").setPositiveButton("okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_doc);
                    adapter = new documentAdapter(documentList, false);
                    recyclerView.setAdapter(adapter);
                    isDeleteState = false;
                    deleStr.clear();
                }
            }).create().show();
        }else {
            builder.setTitle("@_@");
            builder.setMessage("Delete selected documents?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(int i:deleStr.keySet()){
                        DataSupport.deleteAll(document.class, "fileName=?", deleStr.get(i));
                    }
                    documentList.clear();
                    List<document> data = DataSupport.findAll(document.class);
                    for(document mDocument: data){
                        documentList.add(mDocument);
                    }
                    adapter.notifyDataSetChanged();
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_doc);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    adapter = new documentAdapter(documentList, false);
                    recyclerView.setAdapter(adapter);
                    isDeleteState = false;
                    deleStr.clear();
                    Toast.makeText(LibraryActivity.this, "@.@: Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Noooo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_doc);
                    adapter = new documentAdapter(documentList, false);
                    recyclerView.setAdapter(adapter);
                    isDeleteState = false;
                    deleStr.clear();
                }
            });
            builder.create().show();
        }
    }


    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(LibraryActivity.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(LibraryActivity.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(LibraryActivity.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(LibraryActivity.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }


    }





