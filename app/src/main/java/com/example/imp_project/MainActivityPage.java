package com.example.imp_project;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imp_project.adapter.DiariesAdapter;
import com.example.imp_project.db.DatabaseHelper;
import com.example.imp_project.model.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivityPage extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    DiariesAdapter diariesAdapter;
    List<Diary> diaryList = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {

            case R.id.setGlobalPassword:
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("MYPREFS",0);
                SharedPreferences.Editor editor = sharedPref.edit();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Şifreyi Giriniz");

// Set up the input
                final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if(!password.isEmpty()){
                            editor.putString("password",password);
                            editor.commit();
                            Toast.makeText(getBaseContext(), "Şifre Kaydedildi",Toast.LENGTH_LONG).show();
                            loadData();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                break;
            case R.id.openMap:
                Intent intent = new Intent(this,MapsActivity.class);
                intent.putExtra("locations", (Serializable) this.diaryList.stream().map(diary -> diary.getLocation()).collect(Collectors.toList()));
                startActivity(intent);
                break;
        }
        return true;
    }

     public  ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 242) {
                        // There are no request codes
                        loadData();
                    }
                }
            });



    ActivityResultLauncher<Intent> contactResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri contactUri = result.getData().getData();
                    Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                    if(cursor != null && cursor.moveToFirst()) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);

                        int column = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        Log.d("phone number", cursor.getString(column));
                        String phone = cursor.getString(column);
                        cursor.close();
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phone, null, States.getSendDiary().getDescription(), null, null);
                            Toast.makeText(getApplicationContext(), "Message Sent",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyler_view);

        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        diariesAdapter = new DiariesAdapter(this,this.diaryList,someActivityResultLauncher,contactResultLauncher);
        recyclerView.setAdapter(diariesAdapter);
        loadData();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AddDiaryActivity.class);
                someActivityResultLauncher.launch(intent);
            }
        });
    }

    void loadData() {
        DatabaseHelper db = new DatabaseHelper(this);
        diaryList = db.getNoteList();
        recyclerView.setAdapter(new DiariesAdapter(this,diaryList,someActivityResultLauncher,contactResultLauncher));
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

}