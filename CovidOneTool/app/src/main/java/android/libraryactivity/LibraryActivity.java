package android.libraryactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.libraryeditdocactivity.EditDocActivity;
import android.infoactivity.InfoActivity;
import android.libraryeditdocactivity.ViewFileActivity;
import android.newsroom.NewsroomActivity;
import android.widget.Toast;
import org.litepal.crud.DataSupport;
import android.os.Bundle;
import android.selftestactivity.SelfTestActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements documentAdapter.ItemInnerClickListener,documentAdapter.OnLongClickListener {
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
        adapter.setOnItemClickListener(this);
        adapter.setOnLongClickListener(this);
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
                goEditDocActivity.putExtra("startDate", "");
                goEditDocActivity.putExtra("expirationDate","");
                goEditDocActivity.putExtra("fileDescription", "");
                goEditDocActivity.putExtra("signal",0);//0 for New document
                goEditDocActivity.putExtra("filePath","");
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
            adapter.notifyDataSetChanged();
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
                        documentList.remove(i);
                    }
                    adapter.setMultiSelect(false);
                    adapter.notifyDataSetChanged();
                    isDeleteState = false;
                    deleStr.clear();
                    Toast.makeText(LibraryActivity.this, "@.@: Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Noooo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
            case R.id.menu_Newsroom:{
                Intent goNewsroom = new Intent(LibraryActivity.this, NewsroomActivity.class);
                startActivity(goNewsroom);
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

    @Override
    public void onItemInnerClick(View view,int position){
        switch (view.getId()){
            case R.id.tvDetail:
                document mDocument = documentList.get(position);
                String fileType = mDocument.getFileType().toString();
                String fileName = mDocument.getFileName().toString();
                boolean fileStatus = mDocument.getFileStatus();
                String startDate = mDocument.getStartDate();
                String expirationDate = mDocument.getExpirationDate();
                String fileDescription = mDocument.getFileDescription().toString();
                Intent intent = new Intent(LibraryActivity.this, EditDocActivity.class);
                intent.putExtra("id",mDocument.getId()+"");
                intent.putExtra("fileType", fileType);
                intent.putExtra("fileName", fileName);
                intent.putExtra("fileStatus", fileStatus);
                intent.putExtra("startDate",startDate);
                intent.putExtra("expirationDate",expirationDate);
                intent.putExtra("fileDescription", fileDescription);
                intent.putExtra("signal", 1);
                intent.putExtra("filePath",mDocument.getFilePath());
                startActivity(intent);
                break;
            case R.id.viewFile:
                //查看文件
                document mDocument1 = documentList.get(position);
                Intent intentFile = new Intent(LibraryActivity.this, ViewFileActivity.class);
                intentFile.putExtra("filepath",mDocument1.getFilePath());
                startActivity(intentFile);
                break;
            case R.id.checkBtn:
                if( documentAdapter.isSelected.containsKey(position)) {
                    documentAdapter.isSelected.remove(position);
                    deleStr.remove(position);
                }else{
                    documentAdapter.isSelected.put(position,true);
                    deleStr.put(position, documentAdapter.mDocumentList.get(position).getFileName());
                }
                adapter.notifyDataSetChanged();

                if(deleStr.size()==0){
                    isDeleteState = false;
                    deleStr.clear();
                    adapter.setMultiSelect(false);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        if(!isDeleteState && documentList.size()>0){
            isDeleteState = true;
            deleStr.clear();
            documentAdapter.isSelected.put(position, true);
            adapter.setMultiSelect(true);
            adapter.notifyDataSetChanged();
            deleStr.put(position, documentAdapter.mDocumentList.get(position).getFileName());
        }
    }
}





