package android.selftestactivity;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_onetool.R;

public class testActivity extends AppCompatActivity {


    //接收上个Activity传入的标志
    private int signal = 0;


    //加载菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.editdoc_toolbar, menu);
        return true;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doc);



    }
    /*
    //菜单栏功能：页面跳转
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_Statistics:{
                Intent goStatistics = new Intent(testActivity.this, MainActivity.class);
                startActivity(goStatistics);
                break;
            }
            case R.id.menu_MyDocument:{
                Intent goMyDocument = new Intent(testActivity.this, LibraryActivity.class);
                startActivity(goMyDocument);
                break;
            }
            case R.id.menu_SelfTest:{
                Intent goSelfTest = new Intent(testActivity.this, SelfTestActivity.class);
                startActivity(goSelfTest);
                break;
            }
            case R.id.menu_MoreInfo:{
                Intent goMoreInfo = new Intent(testActivity.this, InfoActivity.class);
                startActivity(goMoreInfo);
                break;
            }

        }
        return true;
    }

     */

    /*
    //菜单项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save_button:{
                break;
            }
            //点击返回
            case android.R.id.home:{
                signal = 1;
                break;
            }
            default:

        }
        return true;

    }

     */

}
