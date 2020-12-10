package com.example.todolistandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistandroid.R;
import com.example.todolistandroid.model.Task;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<String> stringList = new ArrayList<>();
    private Task myTask;
    private int[] idImage = new int[]{R.drawable.ic_basketball,
                                        R.drawable.ic_folder,
                                        R.drawable.ic_festival,
                                        R.drawable.ic_healthy_eating,
                                        R.drawable.ic_videoconference,
                                        R.drawable.ic_van};

    public CategoryAdapter(Context context, List<String> stringList, Task myTask) {
        this.context = context;
        this.stringList = stringList;
        this.myTask = myTask;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.image.setImageResource(idImage[position]);
        holder.txt_category_name.setText(stringList.get(position));
        // mengisi jumlah item pada setiap kategori
        if (position == 0) {
            holder.txt_item_sum.setText(myTask.getTotalKatergoriOlahraga() + " Item");
        }
        else if (position == 1) {
            holder.txt_item_sum.setText(myTask.getTotalKatergoriPekerjaan() + " Item");
        }
        else if (position == 2) {
            holder.txt_item_sum.setText(myTask.getTotalKatergoriAcara() + " Item");
        }
        else if (position == 3) {
            holder.txt_item_sum.setText(myTask.getTotalKatergoriMakan() + " Item");
        }
        else if (position == 4) {
            holder.txt_item_sum.setText(myTask.getTotalKatergoriMeeting() + " Item");
        }
        else if (position == 5) {
            holder.txt_item_sum.setText(myTask.getTotalKatergoriPekerjaan() + " Item");
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView txt_category_name;
        TextView txt_item_sum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_of_category);
            txt_category_name = itemView.findViewById(R.id.txt_category_name);
            txt_item_sum = itemView.findViewById(R.id.txt_item_sum);
        }
    }
}
