package com.example.todolistandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.todolistandroid.adapter.CategoryAdapter;
import com.example.todolistandroid.adapter.TaskAdapter;
import com.example.todolistandroid.databinding.ActivityHomepageBinding;
import com.example.todolistandroid.model.Task;
import com.example.todolistandroid.utils.Reminder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "HomepageActivity";
    private final String KEY_PHOTO_URL = "photo";

    private ActivityHomepageBinding mBinding;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManagerTask;
    private CategoryAdapter categoryAdapter;
    private TaskAdapter taskAdapter;
    private List<String> stringList;
    List<Task> tasks;
    List<Task> deactiveTask;
    List<Task> searchTasks;
    String taskMode = "active";
    private Intent myIntent;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentFilter;
    //variabel untuk mengambil tanggal saat ini
    private Calendar currentCalendar = Calendar.getInstance();

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
        mLinearLayoutManagerTask = new LinearLayoutManager(HomepageActivity.this,LinearLayoutManager.VERTICAL, false);
        mBinding.rcCatergory.setLayoutManager(mLinearLayoutManager);
        mBinding.rcTask.setLayoutManager(mLinearLayoutManagerTask);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.filter_array, android.R.layout.simple_spinner_item);
        //adapter.add("All");
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinnerFilter.setAdapter(adapter);
        mBinding.spinnerFilter.setOnItemSelectedListener(this);
//        SwipeController swipeController = new SwipeController();
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(mBinding.rcTask);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mBinding.rcTask);

        mBinding.btnAdd.setOnClickListener(v -> addTask(0, "baru"));
        mBinding.btnCalendar.setOnClickListener(v ->calendarAct());
        //Searching yang dicari huruf awal atau huruf yang ada
        mBinding.editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "Searching:"+s);
                searchList(s.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "Search:"+s);
                searchList(s.toLowerCase());
                return false;
            }
        });
        mBinding.editText.setOnCloseListener(() -> {
            searchList("");
            return false;
        });
        //register context menu untuk logout dengan menekan lama gambar profile
        registerForContextMenu(mBinding.imgProfile);
    }

    ItemTouchHelper.SimpleCallback simpleCallback= new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch(direction){
                    case ItemTouchHelper.LEFT:
                        if(taskMode.equals("active")){
                            Log.d(TAG, "onSwiped() returned: " + tasks.get(position).getDocumentID());
                            delete(tasks.get(position).getDocumentID(), tasks.get(position).getNotifID());
                            tasks.remove(position);
                            taskAdapter.notifyItemRemoved(position);
                        }else if(taskMode.equals("deactive")){
                            Log.d(TAG, "onSwiped() returned: " + deactiveTask.get(position).getDocumentID());
                            delete(deactiveTask.get(position).getDocumentID(), deactiveTask.get(position).getNotifID());
                            deactiveTask.remove(position);
                            taskAdapter.notifyItemRemoved(position);
                        }

                        break;
                    case ItemTouchHelper.RIGHT:
                        if(taskMode.equals("active")){
                            addTask(1,tasks.get(position).getDocumentID());
                        }else if(taskMode.equals("deactive")){
                            addTask(1,deactiveTask.get(position).getDocumentID());
                        }else if(taskMode.equals("search")){
                            addTask(1,searchTasks.get(position).getDocumentID());
                        }
                        break;
                }
        }


    };

    private void delete(String documentID, int notifID){
        db.collection("Tasks").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cancelNotification(notifID);
                        Log.d(TAG, "DocumentSnapshot successfully deleted! DocID : "+documentID+"id: "+notifID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    //navigasi ke calendar
    private void calendarAct(){
        Intent calIntent = new Intent(this, CalendarActivity.class);
        startActivity(calIntent);
    }

    private void addTask(int mode, String docId){
        Intent taskIntent = new Intent(this, AddTaskActivity.class);
        if(mode==1){
            taskIntent.putExtra("mode",1);
            taskIntent.putExtra("docId", docId);
        }else{
            taskIntent.putExtra("mode",0);
        }
        startActivity(taskIntent);
    }

//    private void detailTask(String docId){
//        Intent detailTaskIntent = new Intent(this, AddTaskActivity.class);
//        detailTaskIntent.putExtra("mode", 2);
//        detailTaskIntent.putExtra("docId", docId);
//        startActivity(detailTaskIntent);
//    }

    private void addTaskToList() {
        tasks = new ArrayList<>();
        deactiveTask = new ArrayList<>();
        db.collection("Tasks")
                .whereEqualTo("uid", currentUser.getUid())
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
                                if(document.contains("notifID")){
                                    myTask.setNotifID(Integer.parseInt(document.get("notifID").toString()));
                                }
                                myTask.setDocumentID(document.getId());
                                // menghitung total item pada setiap kategori dan dimasukan kedalam object totalKategoriKegiatanTiapUser
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
                                try {
                                    Calendar taskCalendar = Calendar.getInstance();
                                    Date dateOfTask = new SimpleDateFormat("dd-MM-yyyy").parse(document.get("tanggal").toString());
                                    taskCalendar.setTime(dateOfTask);
                                    Log.d(TAG, "Date: "+dateOfTask);
                                    int today = currentCalendar.get(Calendar.DAY_OF_MONTH);
                                    int currentMonth = currentCalendar.get(Calendar.MONTH);
                                    int dayTask = taskCalendar.get(Calendar.DAY_OF_MONTH);
                                    int monthTask = taskCalendar.get(Calendar.MONTH);
                                    if (today <= dayTask && currentMonth <= monthTask)
                                        tasks.add(myTask);
                                    else
                                        deactiveTask.add(myTask);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
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
    //fungsi searching
    private void searchList(String search) {
        searchTasks = new ArrayList<>();
        if (search.equals("") && currentFilter.equals("Semua")) {
            if(taskMode.equals("active")){
                taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
            }else if(taskMode.equals("deactive")){
                taskAdapter = new TaskAdapter(getApplicationContext(), deactiveTask);
            }
            mBinding.rcTask.setAdapter(taskAdapter);
        }else {
            List<Task> temp = tasks;
            if(taskMode.equals("deactive")){
                temp = deactiveTask;
            }
            for (Task task : temp){
                String dataSearch = task.getNamaTask().toLowerCase();
                if(currentFilter.equals("Semua") || task.getKategori().equals(currentFilter)){
                    if(dataSearch.contains(search) || dataSearch.startsWith(search)) {
                        searchTasks.add(task);
                    }
                }
            }
            taskAdapter = new TaskAdapter(getApplicationContext(), searchTasks);
            mBinding.rcTask.setAdapter(taskAdapter);
        }
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

    private void cancelNotification(int id){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), Reminder.class);
        myIntent.putExtra(Reminder.NOTIFICATION_ID, id);
        myIntent.putExtra(Reminder.NOTIFICATION_CANCEL, true) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        logout();
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("TAG", "onItemSelected() called with: adapterView = [" + adapterView + "], view = [" + view + "], i = [" + i + "], l = [" + l + "]"+" item selected: "+adapterView.getItemAtPosition(i));
        currentFilter = adapterView.getItemAtPosition(i).toString();
        searchList("");
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        
    }

    // fungsi logout
    private void logout() {
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActiveTask(View view) {
        taskMode = "active";
        if(currentFilter.equals("Semua")){
            taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
            mBinding.rcTask.setAdapter(taskAdapter);
        }else{
            searchList("");
        }
    }

    public void onDeactiveTask(View view) {
        taskMode = "deactive";
        if(currentFilter.equals("Semua")){
            taskAdapter = new TaskAdapter(getApplicationContext(), deactiveTask);
            mBinding.rcTask.setAdapter(taskAdapter);
        }else{
            searchList("");
        }
    }
}