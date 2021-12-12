package android.selftestactivity;

import android.content.Intent;
import android.infoactivity.InfoActivity;
import android.libraryactivity.LibraryActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;

public class SelfTestActivity extends AppCompatActivity {
    SwitchCompat switch1,switch2,switch3,switch4,switch5;
    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selftest);

        Button button = (Button)findViewById(R.id.button);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent (SelfTestActivity.this, SelfTestActivity_second.class);

                intent.putExtra("switch1", switch1.isChecked());
                intent.putExtra("switch2", switch2.isChecked());
                intent.putExtra("switch3", switch3.isChecked());
                intent.putExtra("switch4", switch4.isChecked());
                intent.putExtra("switch5", switch5.isChecked());

                startActivity(intent);
            }
        });

    }


    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(SelfTestActivity.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(SelfTestActivity.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(SelfTestActivity.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(SelfTestActivity.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }

}
