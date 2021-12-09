package android.libraryeditdocactivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_onetool.R;
import com.joanzapata.pdfview.PDFView;

import java.io.File;

public class ViewFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        //接收文件路径
        String filePath = getIntent().getStringExtra("filepath");
        //打开阅读器
        PDFView pdfView = findViewById(R.id.pdf);
        File file = new File(filePath);
        //文件加载，默认从第一页开始
        pdfView.fromFile(file).defaultPage(1).showMinimap(false).enableSwipe(true).load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}