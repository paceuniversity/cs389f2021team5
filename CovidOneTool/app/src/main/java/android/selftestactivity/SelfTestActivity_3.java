package android.selftestactivity;

import android.content.Intent;
import android.infoactivity.InfoActivity;
import android.libraryactivity.LibraryActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;

/**
 * create by liubit on 2021/12/9
 */
public class SelfTestActivity_3 extends AppCompatActivity {
    TextView mTvContent;
    boolean result;

    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selftest_3);

        mTvContent = findViewById(R.id.mTvContent);
        result = getIntent().getBooleanExtra("result",true);
        if(!result){
            mTvContent.setText("Based on your answers, you don't seem to need to worry too much.");
            mTvContent.setTextColor(getResources().getColor(R.color.teal_200));
        }else {
            mTvContent.setText("Based on your answers, we suggest you to do a Covid-test.");
            mTvContent.setTextColor(getResources().getColor(R.color.red));
        }

    }

    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(SelfTestActivity_3.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(SelfTestActivity_3.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(SelfTestActivity_3.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(SelfTestActivity_3.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }
}
