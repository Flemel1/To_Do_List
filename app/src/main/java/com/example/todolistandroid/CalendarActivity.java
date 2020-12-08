package com.example.todolistandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.todolistandroid.adapter.CategoryAdapter;
import com.example.todolistandroid.adapter.TaskAdapter;
import com.example.todolistandroid.databinding.ActivityCalendarBinding;
import com.example.todolistandroid.databinding.ActivityHomepageBinding;
import com.example.todolistandroid.model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";

    public ActivityCalendarBinding mBinding;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManagerTask;
    private CategoryAdapter categoryAdapter;
    private TaskAdapter taskAdapter;
    private List<String> stringList;
    List<Task> tasks;
    List<Task> calendarTasks;
    private Intent myIntent;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mBinding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        myIntent = getIntent();
        Calendar c = Calendar.getInstance();
        String currentDateString =c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
        Log.d(TAG, currentDateString);
        addTaskToList(currentDateString);
        mLinearLayoutManager = new LinearLayoutManager(CalendarActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        mLinearLayoutManagerTask = new LinearLayoutManager(CalendarActivity.this,LinearLayoutManager.VERTICAL, false);
        //mBinding.rcCatergory.setLayoutManager(mLinearLayoutManager);
        mBinding.rcTaskCalendar.setLayoutManager(mLinearLayoutManagerTask);
        mBinding.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, i);
                c.set(Calendar.MONTH, i1);
                c.set(Calendar.DAY_OF_MONTH, i2);
                String currentDate = c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
                addTaskToList(currentDate);
                //addTaskToList();
            }
        });

        mBinding.btnBack.setOnClickListener(v -> backToHome());
    }

    private void backToHome(){
        Intent homeIntent = new Intent(this, HomepageActivity.class);
        startActivity(homeIntent);
    }

    private void addTaskToList(String date) {
        tasks = new ArrayList<>();
        db.collection("Tasks")
                .whereEqualTo("uid", currentUser.getUid())
                .whereEqualTo("tanggal", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        Task totalKategoriKegiatanTiapUser = new Task();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // mengambil data dari firebase berdasarkan akun yang login
                                Task myTask = new Task();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                myTask.setUid(currentUser.getUid());
                                myTask.setNamaTask(document.get("judul").toString());
                                myTask.setTanggal(document.get("tanggal").toString());
                                myTask.setWaktu(document.get("waktu").toString());
                                myTask.setDeskripsi(document.get("desc").toString());
                                myTask.setKategori(document.get("kategori").toString());
                                myTask.setNotifID(Integer.parseInt(document.get("notifID").toString()));
                                myTask.setDocumentID(document.getId());
                                // menghitung total item pada setiap kategori dan dimasukan kedalam object totalKategoriKegiatanTiapUser
//                                if (document.get("kategori").toString().equalsIgnoreCase("olahraga")) {
//                                    totalKategoriKegiatanTiapUser.setTotalKatergoriOlahraga(1);
//                                }
//                                else if(document.get("kategori").toString().equalsIgnoreCase("pekerjaan")) {
//                                    totalKategoriKegiatanTiapUser.setTotalKatergoriPekerjaan(1);
//                                }
//                                else if(document.get("kategori").toString().equalsIgnoreCase("acara")) {
//                                    totalKategoriKegiatanTiapUser.setTotalKatergoriAcara(1);
//                                }
//                                else if(document.get("kategori").toString().equalsIgnoreCase("makan")) {
//                                    totalKategoriKegiatanTiapUser.setTotalKatergoriMakan(1);
//                                }
//                                else if(document.get("kategori").toString().equalsIgnoreCase("meeting")) {
//                                    totalKategoriKegiatanTiapUser.setTotalKatergoriMeeting(1);
//                                }
//                                else if(document.get("kategori").toString().equalsIgnoreCase("rekreasi")) {
//                                    totalKategoriKegiatanTiapUser.setTotalKatergoriRekreasi(1);
//                                }
                                tasks.add(myTask);
                            }
                            taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
//                            categoryAdapter = new CategoryAdapter(getApplicationContext(), stringList, totalKategoriKegiatanTiapUser);
                            mBinding.rcTaskCalendar.setAdapter(taskAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
