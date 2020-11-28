package com.example.todolistandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todolistandroid.adapter.CategoryAdapter;
import com.example.todolistandroid.adapter.TaskAdapter;
import com.example.todolistandroid.databinding.ActivityHomepageBinding;
import com.example.todolistandroid.model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {
    private static final String TAG = "HomepageActivity";
    private final String KEY_PHOTO_URL = "photo";

    private ActivityHomepageBinding mBinding;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManagerTask;
    private CategoryAdapter categoryAdapter;
    private TaskAdapter taskAdapter;
    private List<String> stringList;
    List<Task> tasks;
    private Intent myIntent;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mBinding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        myIntent = getIntent();
        Glide.with(this).load(currentUser.getPhotoUrl()).into(mBinding.imgProfile);
        addItemToList();
        addTaskToList();
        mLinearLayoutManager = new LinearLayoutManager(HomepageActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        mLinearLayoutManagerTask = new LinearLayoutManager(HomepageActivity.this);
        mBinding.rcCatergory.setLayoutManager(mLinearLayoutManager);
        mBinding.rcTask.setLayoutManager(mLinearLayoutManagerTask);
        mBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
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
        db.collection("Tasks")
                .whereEqualTo("uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        Task totalKategoriKegiatanTiapUser = new Task();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Task myTask = new Task();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                myTask.setUid(document.getId());
                                myTask.setNamaTask(document.get("judul").toString());
                                myTask.setTanggal(document.get("tanggal").toString());
                                myTask.setWaktu(document.get("waktu").toString());
                                myTask.setDeskripsi(document.get("desc").toString());
                                myTask.setKategori(document.get("kategori").toString());
                                if (document.get("kategori").toString().equalsIgnoreCase("olahraga")) {
                                    totalKategoriKegiatanTiapUser.setTotalKatergoriOlahraga(1);
                                }
                                else if(document.get("kategori").toString().equalsIgnoreCase("pekerjaan")) {
                                    totalKategoriKegiatanTiapUser.setTotalKatergoriPekerjaan(1);
                                }
                                else if(document.get("kategori").toString().equalsIgnoreCase("acara")) {
                                    totalKategoriKegiatanTiapUser.setTotalKatergoriAcara(1);
                                }
                                else if(document.get("kategori").toString().equalsIgnoreCase("makan")) {
                                    totalKategoriKegiatanTiapUser.setTotalKatergoriMakan(1);
                                }
                                else if(document.get("kategori").toString().equalsIgnoreCase("meeting")) {
                                    totalKategoriKegiatanTiapUser.setTotalKatergoriMeeting(1);
                                }
                                else if(document.get("kategori").toString().equalsIgnoreCase("rekreasi")) {
                                    totalKategoriKegiatanTiapUser.setTotalKatergoriRekreasi(1);
                                }
                                tasks.add(myTask);
                            }
                            taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
                            categoryAdapter = new CategoryAdapter(getApplicationContext(), stringList, totalKategoriKegiatanTiapUser);
                            mBinding.rcTask.setAdapter(taskAdapter);

                            mBinding.rcCatergory.setAdapter(categoryAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

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