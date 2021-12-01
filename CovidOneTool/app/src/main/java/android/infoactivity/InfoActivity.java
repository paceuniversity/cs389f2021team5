package android.infoactivity;

import android.content.Intent;
import android.libraryactivity.LibraryActivity;
import android.newsroom.NewsDisplay;
import android.newsroom.NewsroomActivity;
import android.os.Bundle;
import android.selftestactivity.SelfTestActivity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_onetool.MainActivity;
import com.example.covid_onetool.R;

public class InfoActivity extends AppCompatActivity {



    //加载菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

    }


    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(InfoActivity.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(InfoActivity.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_Newsroom:{
                Intent goNewsroom = new Intent(InfoActivity.this, NewsroomActivity.class);
                startActivity(goNewsroom);
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(InfoActivity.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }

}
