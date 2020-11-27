package com.example.todolistandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todolistandroid.adapter.CategoryAdapter;
import com.example.todolistandroid.adapter.TaskAdapter;
import com.example.todolistandroid.databinding.ActivityHomepageBinding;
import com.example.todolistandroid.model.Task;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    private final String KEY_PHOTO_URL = "photo";

    private ActivityHomepageBinding mBinding;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManagerTask;
    private CategoryAdapter categoryAdapter;
    private TaskAdapter taskAdapter;
    private List<String> stringList;
    private List<Task> tasks;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        myIntent = getIntent();
        Glide.with(this).load(myIntent.getStringExtra(KEY_PHOTO_URL)).into(mBinding.imgProfile);
        addItemToList();
        addTaskToList();
        mLinearLayoutManager = new LinearLayoutManager(HomepageActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        mLinearLayoutManagerTask = new LinearLayoutManager(HomepageActivity.this);
        mBinding.rcCatergory.setLayoutManager(mLinearLayoutManager);
        mBinding.rcTask.setLayoutManager(mLinearLayoutManagerTask);
        categoryAdapter = new CategoryAdapter(this, stringList);
        taskAdapter = new TaskAdapter(this, tasks);
        mBinding.rcCatergory.setAdapter(categoryAdapter);
        mBinding.rcTask.setAdapter(taskAdapter);
        mBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addTask();
                Toast.makeText(getApplicationContext(), "Tombol berhasil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTask(){
        Intent taskintent = new Intent(this, AddTaskActivity.class);
        startActivity(taskintent);
    }

    private void addTaskToList() {
        tasks = new ArrayList<>();
        Task task1 = new Task();
        Task task2 = new Task();
        task1.setTanggal("30");
        task1.setNamaTask("Mandi");
        task1.setWaktu("13:30");
        task2.setTanggal("31");
        task2.setNamaTask("Makan");
        task2.setWaktu("09:30");
        tasks.add(task1);
        tasks.add(task2);
    }

    private void addItemToList() {
        stringList = new ArrayList<>();
        stringList.add("Olahraga");
        stringList.add("Pekerjaan");
        stringList.add("Acara");
        stringList.add("Makan");
        stringList.add("Meeting");
        stringList.add("Rekreasi");
    }




}