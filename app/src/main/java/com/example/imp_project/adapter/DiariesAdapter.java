package com.example.imp_project.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imp_project.AddDiaryActivity;
import com.example.imp_project.DiaryDetailsActivity;
import com.example.imp_project.R;
import com.example.imp_project.States;
import com.example.imp_project.db.DatabaseHelper;
import com.example.imp_project.model.Diary;
import com.example.imp_project.model.UpdatedModel;

import java.util.List;

public class DiariesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Diary> diaryList;
    private Context context;
    private ActivityResultLauncher arl;
    private ActivityResultLauncher contactResultLauncher;

    public DiariesAdapter(Context context , List<Diary> diaryList,ActivityResultLauncher arl,ActivityResultLauncher contactResultLauncher) {
        this.layoutInflater=LayoutInflater.from(context);
        this.diaryList = diaryList;
        this.context = context;
        this.arl = arl;
        this.contactResultLauncher = contactResultLauncher;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.diary_card_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView cardTitle,cardDescription;
        ImageView cardImageView;
        ImageButton updateButton,deleteButton,sendButton,setPasswordButton;
        Diary diary = diaryList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("MYPREFS",0);
                String settedPassword = sharedPref.getString("password","");
                if(!diary.getPassword().isEmpty() || !settedPassword.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Şifreyi Giriniz");

// Set up the input
                    final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            String checkedPassword = null;
                            if(!settedPassword.isEmpty()) {
                                checkedPassword =settedPassword;
                            }else if(diary.getPassword() != null || !diary.getPassword().isEmpty()) {
                                checkedPassword = diary.getPassword();
                            }

                            if(checkedPassword != null && checkedPassword.equals(password)){
                                Intent intent = new Intent(context, DiaryDetailsActivity.class);
                                intent.putExtra("diaryDetail",diary);
                                context.startActivity(intent);
                            }else {
                                Toast.makeText(context, "Şifre Hatalı",Toast.LENGTH_LONG).show();
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
                }else {
                    Intent intent = new Intent(context, DiaryDetailsActivity.class);
                    intent.putExtra("diaryDetail",diary);
                    context.startActivity(intent);
                }

            }
        });

        View itemView = holder.itemView;
        cardTitle = itemView.findViewById(R.id.cardTitle);
        cardDescription = itemView.findViewById(R.id.cardDescription);
        cardImageView = itemView.findViewById(R.id.cardimageView);
        updateButton = itemView.findViewById(R.id.updateDiaryButton);
        deleteButton = itemView.findViewById(R.id.deleteDiaryButton);
        setPasswordButton = itemView.findViewById(R.id.setPasswordDiaryButton);
        sendButton = itemView.findViewById(R.id.sendDiaryButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = context.getSharedPreferences("MYPREFS",0);
                String settedPassword = sharedPref.getString("password","");
                if(!diary.getPassword().isEmpty() || !settedPassword.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Şifreyi Giriniz");

// Set up the input
                    final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            String checkedPassword = null;
                            if(!settedPassword.isEmpty()) {
                                checkedPassword =settedPassword;
                            }else if(diary.getPassword() != null || !diary.getPassword().isEmpty()) {
                                checkedPassword = diary.getPassword();
                            }

                            if(checkedPassword != null && checkedPassword.equals(password)){
                                Intent intent = new Intent(view.getContext(), AddDiaryActivity.class);
                                intent.putExtra("updateDiary",diary);
                                arl.launch(intent);
                                notifyDataSetChanged();
                            }else {
                                Toast.makeText(context, "Şifre Hatalı",Toast.LENGTH_LONG).show();
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
                }else {
                    Intent intent = new Intent(view.getContext(), AddDiaryActivity.class);
                    intent.putExtra("updateDiary",diary);
                    arl.launch(intent);
                    notifyDataSetChanged();

                }

            }
        });

        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("MYPREFS",0);
                String settedPassword = sharedPref.getString("password","");
                if(!diary.getPassword().isEmpty() || !settedPassword.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Şifreyi Giriniz");

// Set up the input
                    final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            String checkedPassword = null;
                            if(!settedPassword.isEmpty()) {
                                checkedPassword =settedPassword;
                            }else if(diary.getPassword() != null || !diary.getPassword().isEmpty()) {
                                checkedPassword = diary.getPassword();
                            }

                            if(checkedPassword != null && checkedPassword.equals(password)){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Yeni Şifre Giriniz");

// Set up the input
                                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                builder.setView(input);

// Set up the buttons
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String password = input.getText().toString();
                                        DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());
                                        db.setPassword(diary,password);
                                        diaryList = db.getNoteList();
                                        db.close();
                                        notifyItemChanged(holder.getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }else {
                                Toast.makeText(context, "Şifre Hatalı",Toast.LENGTH_LONG).show();
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
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Şifre Giriniz");

// Set up the input
                    final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());
                            db.setPassword(diary,password);
                            diaryList = db.getNoteList();
                            db.close();
                            notifyItemChanged(holder.getAdapterPosition());
                            notifyDataSetChanged();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                }

        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = context.getSharedPreferences("MYPREFS",0);
                String settedPassword = sharedPref.getString("password","");
                if(!diary.getPassword().isEmpty() || !settedPassword.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Şifreyi Giriniz");

// Set up the input
                    final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            String checkedPassword = null;
                            if(!settedPassword.isEmpty()) {
                                checkedPassword =settedPassword;
                            }else if(diary.getPassword() != null || !diary.getPassword().isEmpty()) {
                                checkedPassword = diary.getPassword();
                            }

                            if(checkedPassword != null && checkedPassword.equals(password)){
                                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                                States.setSendDiary(diary);
                                contactResultLauncher.launch(i);
                            }else {
                                Toast.makeText(context, "Şifre Hatalı",Toast.LENGTH_LONG).show();
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
                }else {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    States.setSendDiary(diary);
                    contactResultLauncher.launch(i);
                }


            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences("MYPREFS",0);
                String settedPassword = sharedPref.getString("password","");
                if(!diary.getPassword().isEmpty() || !settedPassword.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Şifreyi Giriniz");

// Set up the input
                    final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = input.getText().toString();
                            String checkedPassword = null;
                            if(!settedPassword.isEmpty()) {
                                checkedPassword =settedPassword;
                            }else if(diary.getPassword() != null || !diary.getPassword().isEmpty()) {
                                checkedPassword = diary.getPassword();
                            }

                            if(checkedPassword != null && checkedPassword.equals(password)){
                                DatabaseHelper db = new DatabaseHelper(context);
                                db.deleteNote(diary.getId());
                                diaryList.remove(diary);
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), diaryList.size());
                            }else {
                                Toast.makeText(context, "Şifre Hatalı",Toast.LENGTH_LONG).show();
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
                }else {
                    DatabaseHelper db = new DatabaseHelper(context);
                    db.deleteNote(diary.getId());
                    diaryList.remove(diary);
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), diaryList.size());
                }

            }
        });

        cardTitle.setText(diary.getTitle());
        cardDescription.setText(diary.getDescription());
        if(!diary.getUri().isEmpty()) {

            cardImageView.setImageBitmap(BitmapFactory.decodeFile(diary.getUri()));
        }

    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView cardTitle,cardDescription;
        ImageView cardImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDescription = itemView.findViewById(R.id.cardDescription);
            cardImageView = itemView.findViewById(R.id.cardimageView);
        }
    }


}
