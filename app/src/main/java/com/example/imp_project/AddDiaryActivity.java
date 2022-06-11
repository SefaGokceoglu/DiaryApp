package com.example.imp_project;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imp_project.db.DatabaseHelper;
import com.example.imp_project.model.Diary;

import java.time.LocalDateTime;
import java.util.List;

public class AddDiaryActivity extends AppCompatActivity implements LocationListener {

    EditText iconInput, titleInput, descriptionInput;
    ImageView selectedImageView;
    Uri selectedImage;
    Button saveButton;
    protected LocationManager locationManager;
    String location;

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {

                @Override
                public void onActivityResult(Uri uri) {
                    selectedImage = uri;
                    selectedImageView.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        Intent intent = getIntent();
        Diary updatedDiary =(Diary) intent.getSerializableExtra("updateDiary");
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            Location myLocation = getLastKnownLocation();

            Double currentLatitude = myLocation.getLatitude();
            Double currentLongitude = myLocation.getLongitude();
            this.location = currentLatitude+","+currentLongitude;
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);


        iconInput = findViewById(R.id.emojiTextView);
        titleInput = findViewById(R.id.txtTitle);
        descriptionInput = findViewById(R.id.txtContent);
        selectedImageView = findViewById(R.id.pickImageView);
        saveButton = findViewById(R.id.btnSave);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(updatedDiary != null) {
            iconInput.setText(updatedDiary.getModeEmoji());
            titleInput.setText(updatedDiary.getTitle());
            descriptionInput.setText(updatedDiary.getDescription());
        }
        selectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                }else{
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mGetContent.launch("image/*");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(selectedImage != null && !titleInput.getText().toString().isEmpty() && !iconInput.getText().toString().isEmpty() && !descriptionInput.getText().toString().isEmpty() && location != null) {
                        Diary newDiary;
                        if(updatedDiary != null) {

                            newDiary = new Diary(updatedDiary.getId(), titleInput.getText().toString(), iconInput.getText().toString(),selectedImage.toString() , descriptionInput.getText().toString(), updatedDiary.getPassword(), updatedDiary.getDate(),updatedDiary.getLocation());
                            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                            db.update(newDiary);
                            db.close();
                        }else {
                            newDiary = new Diary(null, titleInput.getText().toString(), iconInput.getText().toString(), selectedImage.toString(), descriptionInput.getText().toString(), "", LocalDateTime.now(),location);
                            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                            db.addNote(newDiary);
                            db.close();
                        }
                        titleInput.setText("");
                        iconInput.setText("");
                        descriptionInput.setText("");
                        selectedImageView.setImageURI(null);
                        Intent _result = new Intent();
                        _result.putExtra("diary", newDiary);
                        setResult(242, _result);
                        Intent intent = getIntent();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Alanları giriniz!",Toast.LENGTH_SHORT).show();

                    }




            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Double lat =  location.getLatitude();
        Double lng = location.getLongitude();

        this.location = lat + ","+lng;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}