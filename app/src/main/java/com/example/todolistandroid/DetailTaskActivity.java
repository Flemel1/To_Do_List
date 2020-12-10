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


public class DetailTaskActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener {

    private ActivityDetailtaskBinding mBinding;
    private static final String TAG = "DetailTaskActivity";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private Button btnDate;
    private Intent myIntent;
    private String currentDateString;
    private String currentCategory;
    private int year = -1, month = -1, day = -1, hour = -1, minute = -1,rand;
    private String docId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private String judul;
    private String waktu;
    private String reminder;
    private String desc;
    private String uid;
    private String tanggal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mBinding = ActivityDetailtaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.tvDate.setText(calendar.get(Calendar.DAY_OF_MONTH)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR));
        mBinding.txtAddWaktu.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+(calendar.get(Calendar.MINUTE)));
        mBinding.txtAddWaktu.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        });
        mBinding.btnBack.setOnClickListener(view -> back());

        myIntent = getIntent();
        docId = myIntent.getStringExtra("docId");
        db.collection("Tasks").document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        rand = Integer.parseInt(document.getString("notifID"));
                        Log.d(TAG, "rand: "+rand);
                        currentDateString = document.getString("tanggal");
                        tanggal = document.getString("tanggal");
                        mBinding.tvDate.setText(document.getString("tanggal"));
                        mBinding.txtAddWaktu.setText(document.getString("waktu"));
                        getWaktuFromString(document.getString("tanggal"),  document.getString("waktu"));
                        List<String> catList = Arrays.asList(getResources().getStringArray(R.array.category_array));
                        mBinding.txtAddTitle.setText(document.getString("judul"));
                        for(int i=0;i<catList.size();i++){
                            if(catList.get(i).equals(document.getString("kategori"))){
                                mBinding.txtCategory.setText(document.getString("kategori"));
                            }
                        }
                        mBinding.txtAddTask.setText("Deskripsi: "+document.getString("desc"));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        //currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        String date = day+"-"+(month+1)+"-"+year;
        Log.d(TAG, date);
        mBinding.tvDate.setText(date);
    }

    private void back(){
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

//    private void save(){
////        Save ke firebase
//        judul = mBinding.txtAddTitle.getText().toString();
//        tanggal = mBinding.tvDate.getText().toString();
//        waktu = mBinding.txtAddWaktu.getText().toString();
//        reminder = mBinding.txtAddReminder.getText().toString();
//        desc = mBinding.txtAddTask.getText().toString();
//        getWaktuFromString(tanggal, waktu);
//        uid = currentUser.getUid();
//        if(modeInt==0){
//            Random random = new Random();
//            rand = random.nextInt(10000);
//        }
//        Map<String, Object> task = new HashMap<>();
//        task.put("uid", uid);
//        task.put("judul",judul);
//        task.put("tanggal",tanggal);
//        task.put("waktu",waktu);
//        task.put("reminder",reminder);
//        task.put("desc",desc);
//        task.put("kategori", currentCategory);
//        task.put("notifID", String.valueOf(rand));
//        if(modeInt==0){
//            db.collection("Tasks").add(task)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            scheduleNotification(getNotification(judul, tanggal+" "+waktu), rand, timeInMillis());
//                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error adding document", e);
//                        }
//                    });
//        }else{
//            db.collection("Tasks").document(docId).update(task).addOnSuccessListener(new OnSuccessListener(){
//                @Override
//                public void onSuccess(Object o) {
//                    cancelNotification(rand);
//                    scheduleNotification(getNotification(judul, currentDateString+" "+waktu), rand, timeInMillis());
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.w(TAG, "Error adding document", e);
//                }
//            });
//        }
////        Move to Homepage Activity
//        Intent intent = new Intent(this, HomepageActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("TAG", "onItemSelected() called with: adapterView = [" + adapterView + "], view = [" + view + "], i = [" + i + "], l = [" + l + "]"+" item selected: "+adapterView.getItemAtPosition(i));
        currentCategory = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.YEAR, this.year);
        calendar.set(Calendar.MONTH, this.month);
        calendar.set(Calendar.DAY_OF_MONTH, this.day);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        this.hour = hourOfDay;
        this.minute = minute;
        String textTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        mBinding.txtAddWaktu.setText(textTime);
    }

    public void getWaktuFromString(String tanggal, String waktu){
        day = Integer.parseInt(tanggal.split("-")[0]);
        month = Integer.parseInt(tanggal.split("-")[1])-1;
        year = Integer.parseInt(tanggal.split("-")[2]);
        hour = Integer.parseInt(waktu.split(":")[0]);
        minute = Integer.parseInt(waktu.split(":")[1]);
        Log.d(TAG, "DATETIME: "+day+month+year+hour+minute);
    }

//    public long timeInMillis(){
//        Calendar calendar = Calendar.getInstance();
//        if(this.year == -1){
//            this.year = calendar.get(Calendar.YEAR);
//        }
//        if(this.month == -1){
//            this.month = calendar.get(Calendar.MONTH);
//        }
//        if(this.day == -1){
//            this.day = calendar.get(Calendar.DAY_OF_MONTH);
//        }
//        if(this.hour == -1){
//            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
//        }
//        if(this.minute == -1){
//            this.minute = calendar.get(Calendar.MINUTE);
//        }
//        calendar.set(Calendar.YEAR, this.year);
//        calendar.set(Calendar.MONTH, this.month);
//        calendar.set(Calendar.DAY_OF_MONTH, this.day);
//        calendar.set(Calendar.HOUR_OF_DAY, this.hour);
//        calendar.set(Calendar.MINUTE, this.minute);
//        calendar.set(Calendar.SECOND, 0);
//        waktuInMilis = calendar.getTimeInMillis();
//        return waktuInMilis;
//    }

    //untuk menjadwalkan notifikasi
//    private void scheduleNotification(Notification notification, int id, long waktu) {
//        Intent notificationIntent = new Intent( this, Reminder.class );
//        Log.d(TAG, "NOTIFICATION_ID: "+rand);
//        notificationIntent.putExtra(Reminder.NOTIFICATION_ID,  id);
//        notificationIntent.putExtra(Reminder.NOTIFICATION, notification) ;
//        PendingIntent pendingIntent = PendingIntent.getBroadcast (this, id , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;
//        Log.d("Activity", "Waktu: "+waktu);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE ) ;
//        assert alarmManager != null;
//        alarmManager.set(AlarmManager.RTC_WAKEUP, waktu , pendingIntent) ;
//    }

    //membuat notifikasi
//    private Notification getNotification (String title, String content) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id) ;
//        builder.setContentTitle(title);
//        builder.setContentText(content);
//        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
//        builder.setAutoCancel(true) ;
//        builder.setChannelId(NOTIFICATION_CHANNEL_ID) ;
//        return builder.build() ;
//    }

    //untuk cancel notifikasi yang telah ada
//    private void cancelNotification(int id){
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent myIntent = new Intent(getApplicationContext(), Reminder.class);
//        myIntent.putExtra(Reminder.NOTIFICATION_ID, id);
//        myIntent.putExtra(Reminder.NOTIFICATION_CANCEL, true) ;
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.cancel(pendingIntent);
//    }
}