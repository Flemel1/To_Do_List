package com.example.todolistandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistandroid.R;
import com.example.todolistandroid.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.txt_tanggal.setText(task.getTanggal());
        holder.txt_namaTask.setText(task.getNamaTask());
        holder.txt_waktu.setText(task.getWaktu());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_tanggal;
        TextView txt_namaTask;
        TextView txt_waktu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_namaTask = itemView.findViewById(R.id.txt_nama_task);
            txt_waktu = itemView.findViewById(R.id.txt_waktu);
        }
    }
}
