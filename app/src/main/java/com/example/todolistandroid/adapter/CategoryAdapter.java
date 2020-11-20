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

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<String> stringList = new ArrayList<>();
    private int[] idImage = new int[]{R.drawable.ic_baseline_sports_volleyball_24,
                                        R.drawable.ic_kategori_pekerjaan,
                                        R.drawable.ic_kaategori_acara,
                                        R.drawable.ic_kategori_makan,
                                        R.drawable.ic_kategori_meeting,
                                        R.drawable.ic_kategori_rekreasi};

    public CategoryAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;
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
        holder.txt_item_sum.setText("0 items");
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
