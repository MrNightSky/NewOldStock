package ru.group.sky.night.newoldstock;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import org.jsoup.Jsoup; // Библиотека для парсинга сайта
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // Массив, в который мы будем заносить найденные ссылки на картинки
    public ArrayList<String> titleList = new ArrayList<String>();
    // Создаем RecyclerView и адаптер для него
    private RecyclerView RView;
    private ImAdapter adapter;
    public Integer page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Возвращаемся к предыдущей теме, чтобы картинка загрузки не осталавалась на заднем плане
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.go_in, R.anim.go_out); // Устанавливаем свою анимацию перехода
        setContentView(R.layout.main);


        Intent intent = getIntent();
        if (intent.getStringExtra("PAGE") != null){
            page = Integer.valueOf(intent.getStringExtra("PAGE"));
        }

        //Задаем параметры RecyclerView, чтобы картинки не скакали
        RView = findViewById(R.id.RecView);
        RView.setHasFixedSize(true);
        RView.setItemViewCacheSize(20);
        RView.setDrawingCacheEnabled(true);
        RView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        new NewThread().execute();
        // Передаем адаптеру массив с ссылками на картинки
        adapter = new ImAdapter(this, titleList, page);
        // Создаем манеджер для адаптера, чтобы была фиксированная ширина и разная высота картинки
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        RView.setLayoutManager(staggeredGridLayoutManager);
    }
    // Функция проверки наличия интернета
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @SuppressLint("StaticFieldLeak")
    public class NewThread extends AsyncTask<String, Void, String> {
        @Override
        protected  String doInBackground(String... arg){
            // Документ, в который мы запишем, всю страницу
            Document doc;
            // Проверяем соединение
            if (isOnline()){
                try {
                    // Загружаем страницу в документ
                    doc = Jsoup.connect("https://nos.twnsnd.co/page/" + Integer.toString(page)).get();
                    // Ищем в странице ссылки на картинки
                    Elements content = doc.select("img[src$=.jpg]");
                    // Очищаем список, в который мы будем загружать ссылки, и загружаем их в цикле
                    titleList.clear();
                    if (page > 1){
                        titleList.add("Previous");
                    }
                    for (Element src : content){
                        if (src.tagName().equals("img"))
                            titleList.add(src.attr("abs:src"));
                    }
                    if (page < 127){
                        titleList.add("Next");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            } else {
                // Если соединения нет, то выводим сообщене с ошибкой
                setContentView(R.layout.internet_error);
            }
            return null;
        }

        @Override
        protected void  onPostExecute(String result){
            // устанавливаем адаптер через экземпляр класса ImAdapter
            RView.setAdapter(adapter);
        }

    }
}
