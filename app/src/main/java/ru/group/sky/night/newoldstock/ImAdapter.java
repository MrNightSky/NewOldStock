package ru.group.sky.night.newoldstock;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

// Импортируем библиотеку Picasso для отображения картинок по ссылке
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Адаптер для заполнения RecyclerView
public class ImAdapter extends RecyclerView.Adapter<ImAdapter.ViewHolder> {
    private LayoutInflater inflater;
    // Задаем массив, в который мы перенесем ссылки
    private ArrayList<String> links;
    private Context context;
    private Integer page;

    ImAdapter(Context context, ArrayList<String> links,int page) {
        this.context = context;
        this.links = links;
        this.inflater = LayoutInflater.from(context);
        this.page = page;

    }


    @NonNull
    @Override
    public ImAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Загружаем в RecyclerView ImageView
        View imgsView = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(imgsView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImAdapter.ViewHolder holder, int position) {

        if ((position == 0) & (page > 1)) {
            // Первой выводим картинку Previous Page, кликая по которой мы будем возвращаться на предыдущую страницу, если мы не на первой странице
            Picasso.get().load(R.drawable.previous).placeholder(R.drawable.placeholder).error(R.drawable.error).into(holder.imgView);
        } else if ((position == links.size() - 1) & (page < 127)) {
            // Последней выводим картинку Next Page, кликая по которой мы будем переходить на следующую страницу, если мы не на последней страице сайта (127)
            Picasso.get().load(R.drawable.next).placeholder(R.drawable.placeholder).error(R.drawable.error).into(holder.imgView);
        } else {
            // Рисуем картинку в ImageView
            Picasso.get().load(links.get(position)).placeholder(R.drawable.placeholder).error(R.drawable.error).into(holder.imgView);
        }
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    // Создаем возможность кликать по картинке
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgView;
        ViewHolder(View itemView){
            super(itemView);
            imgView = itemView.findViewById(R.id.img1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // Переход на предыдущую страницу
            if ((position == 0) & (page > 1)){
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("PAGE", Integer.toString(page - 1));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            // Переход на следующую страницу
            } else if ((position == links.size() - 1) & (page < 127)) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("PAGE", Integer.toString(page + 1));
                context.startActivity(intent);
            // Увеличение картинки при нажатии на нее
            } else if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("PICTURE", links.get(position));
                context.startActivity(intent);
            }
        }
    }
}