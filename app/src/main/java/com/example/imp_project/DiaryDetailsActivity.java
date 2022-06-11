package com.example.imp_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imp_project.model.Diary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryDetailsActivity extends AppCompatActivity {

    private Diary diary;

    private TextView titleTextView,emojiTextView,descriptionTextView;
    private ImageView imageView;

    int pageHeight = 1120;
    int pagewidth = 792;

    // creating a bitmap variable
    // for storing our images
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.openMapDetail:
                Intent intent = new Intent(this,MapsActivity.class);
                List<String> strings = new ArrayList<>();
                strings.add(diary.getLocation());
                intent.putExtra("locations", (Serializable) strings);
                startActivity(intent);
                break;
            case R.id.exportPdf:
                    generatePDF();
                break;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_details);

        Intent intent = getIntent();
        Diary diary = (Diary) intent.getSerializableExtra("diaryDetail");
        this.diary = diary;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        titleTextView = findViewById(R.id.diartDetailTitle);
        emojiTextView = findViewById(R.id.diaryDetailEmoji);
        descriptionTextView = findViewById(R.id.diaryDetailDescription);
        imageView = findViewById(R.id.diaryDetailImageView);

        titleTextView.setText(diary.getTitle());
        emojiTextView.setText(diary.getModeEmoji());
        descriptionTextView.setText(diary.getDescription());
        imageView.setImageBitmap(BitmapFactory.decodeFile(diary.getUri()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    private void generatePDF() {
        // creating an object variable
        // for our PDF document.
        try {
            PdfDocument pdfDocument = new PdfDocument();

            // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint


        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText(diary.getTitle(), 209, 100, title);
        canvas.drawText(diary.getModeEmoji(), 209, 80, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(diary.getDescription(), 396, 560, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        File file = new File(Environment.getExternalStorageDirectory()+"/Documents", "diary"+ LocalDateTime.now().toString().replaceAll(":", ".")+".pdf");


            // after creating a file name we will
            // write our PDF file to that location.
            FileOutputStream fos =    new FileOutputStream(file);
            pdfDocument.writeTo(fos);

            // below line is to print toast message
            // on completion of PDF generation.
            pdfDocument.close();

            Toast.makeText(this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            Toast.makeText(this, "PDF file generated failed.", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2016);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2016);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2016) {
                if (grantResults.length > 0) {

                    // after requesting permissions we are showing
                    // users a toast message of permission granted.
                    boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorage && readStorage) {
                        Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

        }

}

