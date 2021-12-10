package android.selftestactivity;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.infoactivity.InfoActivity;
import android.libraryactivity.LibraryActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;

public class SelfTestActivity_second extends AppCompatActivity {
    TextView sw1,sw2,sw3,sw4,sw5;
    boolean s1,s2,s3,s4,s5;

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
         s1 = intent.getBooleanExtra("switch1", true);
         s2 = intent.getBooleanExtra("switch2", true);
         s3 = intent.getBooleanExtra("switch3", true);
         s4 = intent.getBooleanExtra("switch4", true);
         s5 = intent.getBooleanExtra("switch5", true);

        TextView textView = (TextView) findViewById(R.id.switch1);

        sw1 = (TextView) findViewById(R.id.sw1);
        sw2 = (TextView) findViewById(R.id.sw2);
        sw3 = (TextView) findViewById(R.id.sw3);
        sw4 = (TextView) findViewById(R.id.sw4);
        sw5 = (TextView) findViewById(R.id.sw5);

        sw1.setText(getAnswer(s1));
        sw2.setText(getAnswer(s2));
        sw3.setText(getAnswer(s3));
        sw4.setText(getAnswer(s4));
        sw5.setText(getAnswer(s5));

        findViewById(R.id.button).setOnClickListener(view -> {
            Intent intent1 = new Intent(SelfTestActivity_second.this,SelfTestActivity_3.class);
            if(!s1&&!s2&&!s3&&!s4&&!s5){
                intent1.putExtra("result", false);
            }else {
                intent1.putExtra("result", true);
            }
            startActivity(intent1);
        });
    }

    private String getAnswer(boolean yes){
        return yes?"yes":"no";
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