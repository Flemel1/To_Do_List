package com.example.todolistandroid;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.todolistandroid.databinding.ActivityDetailtaskBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class DetailTaskActivity extends AppCompatActivity{

    private ActivityDetailtaskBinding mBinding;
    private static final String TAG = "DetailTaskActivity";
    private Intent myIntent;
    private String docId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mBinding = ActivityDetailtaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btnBack.setOnClickListener(view -> back());
        myIntent = getIntent();
        docId = myIntent.getStringExtra("docId");
        db.collection("Tasks").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mBinding.tvTanggal.setText(document.getString("tanggal"));
                        mBinding.tvJudul.setText(document.getString("judul"));
                        mBinding.tvWaktu.setText(document.getString("waktu"));
                        mBinding.tvKategori.setText(document.getString("kategori"));
                        mBinding.tvDeskripsi.setText(document.getString("desc"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void back(){
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

}
