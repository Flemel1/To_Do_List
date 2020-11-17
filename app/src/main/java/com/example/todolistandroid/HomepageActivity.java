package com.example.todolistandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.todolistandroid.adapter.CategoryAdapter;
import com.example.todolistandroid.databinding.ActivityHomepageBinding;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {
    private ActivityHomepageBinding mBinding;
    private LinearLayoutManager mLinearLayoutManager;
    private CategoryAdapter categoryAdapter;
    private List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        addItemToList();
        mLinearLayoutManager = new LinearLayoutManager(HomepageActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        mBinding.rcCatergory.setLayoutManager(mLinearLayoutManager);
        categoryAdapter = new CategoryAdapter(this, stringList);
        mBinding.rcCatergory.setAdapter(categoryAdapter);
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