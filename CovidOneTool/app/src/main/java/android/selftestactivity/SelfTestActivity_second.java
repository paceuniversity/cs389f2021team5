package android.selftestactivity;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.infoactivity.InfoActivity;
import android.libraryactivity.LibraryActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;

public class SelfTestActivity_second extends AppCompatActivity {

    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_test_second);


        Intent intent = getIntent();
        boolean s1 = intent.getBooleanExtra("switch1", true);
        boolean s2 = intent.getBooleanExtra("switch2", true);
        boolean s3 = intent.getBooleanExtra("switch3", true);
        boolean s4 = intent.getBooleanExtra("switch4", true);
        boolean s5 = intent.getBooleanExtra("switch5", true);

        TextView textView = (TextView) findViewById(R.id.switch1);

    }

    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(SelfTestActivity_second.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(SelfTestActivity_second.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(SelfTestActivity_second.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(SelfTestActivity_second.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }



}