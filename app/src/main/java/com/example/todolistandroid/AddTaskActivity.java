package com.example.todolistandroid;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.todolistandroid.databinding.ActivityAddtaskBinding;

import java.text.DateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private ActivityAddtaskBinding mBinding;
    private Button btnDate;
    private Intent myIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAddtaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btnDate.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });
        mBinding.btnCancel.setOnClickListener(view -> cancel());
        mBinding.btnSave.setOnClickListener(view ->save());
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        mBinding.tvDate.setText(currentDateString);
    }

    private void cancel(){
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    private void save(){
//        Save ke firebase
        
//        Move to Homepage Activity
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }
}
