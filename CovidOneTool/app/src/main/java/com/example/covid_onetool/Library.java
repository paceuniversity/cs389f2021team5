package com.example.covid_onetool;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Library extends AppCompatActivity {
    //设置是否在onStart更新数据源的标记
    private int update=0;
    //储存document对象的数组
    private List<document> documentList=new ArrayList<>();
    //适配器？我们需要吗？

    //是否处于多选删除状态；区分正常点击和多选删时的点击事件；长按状态下不再响应长按事件
    private boolean isDeleteStatus=false;


    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_library);
        //从数据库中获取document对象
        documentList = DataSupport.findAll(document.class);
        //recyclerView?
        if(documentList.size()>0){
            //无document

        }



    }




}
