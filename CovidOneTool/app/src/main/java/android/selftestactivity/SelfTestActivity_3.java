package android.selftestactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_onetool.R;

/**
 * create by liubit on 2021/12/9
 */
public class SelfTestActivity_3 extends AppCompatActivity {
    TextView mTvContent;
    boolean result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selftest_3);

        mTvContent = findViewById(R.id.mTvContent);
        result = getIntent().getBooleanExtra("result",true);
        if(!result){
            mTvContent.setText("Based on your answers, you're health now");
            mTvContent.setTextColor(getResources().getColor(R.color.teal_200));
        }else {
            mTvContent.setText("Based on your answers, we suggest you to do the Covid-test");
            mTvContent.setTextColor(getResources().getColor(R.color.red));
        }

    }
}
