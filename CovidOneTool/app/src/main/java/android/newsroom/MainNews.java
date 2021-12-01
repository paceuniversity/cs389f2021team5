package android.newsroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_onetool.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MainNews extends AppCompatActivity {

    private List<News> newsList;
    private NewsAdapter adapter;
    private Handler handler;
    private ListView lv;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.news_lv);
        getNews();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    adapter = new  NewsAdapter(MainNews.this,newsList);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            News news = newsList.get(position);
                            Intent intent = new Intent(MainNews.this,  NewsDisplayActivity.class);
                            intent.putExtra("news_url",news.getNewsUrl());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

    }



    private void getNews(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    for(int i = 1;i<=20;i++) {
                        //https://coronavirus.health.ny.gov/latest-news
                        //https://www.bilibili.com/
                        Document doc = Jsoup.connect("https://coronavirus.health.ny.gov/latest-news").get();

                        //div.info-box
                        Elements titleLinks = doc.select("div.webny-teaser-title");    //解析来获取每条新闻的标题与链接地址
                        //
                        //div.info
                        Elements descLinks = doc.select("div.webny-teaser-title");//解析来获取每条新闻的简介
                        //
                        //div.otherInfo
                        Elements timeLinks = doc.select("div.description");   //解析来获取每条新闻的时间与来源

                        for(int j = 0;j < titleLinks.size();j++){
                            String title = titleLinks.get(j).select("a").text();
                            String uri = titleLinks.get(j).select("a").attr("href");
                            String desc = descLinks.get(j).select("span").text();
                            String time = timeLinks.get(j).select("p").text();

                            News news = new  News(title,uri,desc,time);
                            newsList.add(news);
                        }
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
