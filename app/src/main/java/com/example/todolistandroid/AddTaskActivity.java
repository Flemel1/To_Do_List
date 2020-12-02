package com.example.todolistandroid;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import com.example.todolistandroid.databinding.ActivityAddtaskBinding;
import com.example.todolistandroid.utils.Reminder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.util.Random;

public class AddTaskActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener {

    private ActivityAddtaskBinding mBinding;
    private static final String TAG = "AddTaskActivity";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private Button btnDate;
    private Intent myIntent;
    private String currentDateString;
    private String currentCategory;
    private int year, month, day, rand;
    private long waktuInMilis;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mBinding = ActivityAddtaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btnDate.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });
        mBinding.txtAddWaktu.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        });
        mBinding.btnCancel.setOnClickListener(view -> cancel());
        mBinding.btnSave.setOnClickListener(view ->save());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinnerCategory.setAdapter(adapter);
        mBinding.spinnerCategory.setOnItemSelectedListener(this);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        this.year = year;
        c.set(Calendar.MONTH, month);
        this.month = month;
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.day = dayOfMonth;
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        mBinding.tvDate.setText(currentDateString);
    }

    private void cancel(){
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    private void save(){
//        Save ke firebase
        String judul = mBinding.txtAddTitle.getText().toString();
        String waktu = mBinding.txtAddWaktu.getText().toString();
        String reminder = mBinding.txtAddReminder.getText().toString();
        String desc = mBinding.txtAddTask.getText().toString();
        String uid = currentUser.getUid();
        Random random = new Random();
        rand = random.nextInt(10000);
        Map<String, Object> task = new HashMap<>();
        task.put("uid", uid);
        task.put("judul",judul);
        task.put("tanggal",currentDateString);
        task.put("waktu",waktu);
        task.put("reminder",reminder);
        task.put("desc",desc);
        task.put("kategori", currentCategory);
        task.put("notifID", String.valueOf(rand));
        db.collection("Tasks").add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        scheduleNotification(getNotification(judul, currentDateString+" "+waktu), rand, waktuInMilis);
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

//        Move to Homepage Activity
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
        finish();
    }

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
        String textTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        mBinding.txtAddWaktu.setText(textTime);
        waktuInMilis = calendar.getTimeInMillis();
    }

    //untuk menjadwalkan notifikasi
    private void scheduleNotification(Notification notification, int id, long waktu) {
        Intent notificationIntent = new Intent( this, Reminder.class );
        //listNotifID.add(rand);
        //listId.setText(listNotifID.toString());
        Log.d(TAG, "NOTIFICATION_ID: "+rand);
        notificationIntent.putExtra(Reminder.NOTIFICATION_ID,  id);
        notificationIntent.putExtra(Reminder.NOTIFICATION, notification) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast (this, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;
        Log.d("Activity", "Waktu: "+waktu);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, waktu , pendingIntent) ;
    }

    //membuat notifikasi
    private Notification getNotification (String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id) ;
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel(true) ;
        builder.setChannelId(NOTIFICATION_CHANNEL_ID) ;
        return builder.build() ;
    }

    //untuk cancel notifikasi yang telah ada
    private void cancelNotification(int id){
        //if (listNotifID.contains(id)){
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(getApplicationContext(), Reminder.class);
            myIntent.putExtra(Reminder.NOTIFICATION_ID, id);
            //listNotifID.remove(id);
            //listId.setText(listNotifID.toString());
            myIntent.putExtra(Reminder.NOTIFICATION_CANCEL, true) ;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        //}
    }
}
