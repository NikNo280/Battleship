package com.example.battleship.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.battleship.R;
import com.example.battleship.ViewModel.ProfileViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class Profile extends AppCompatActivity {

    private ProfileViewModel userPageViewModel;

    ImageView imageView;
    Button setNewName;
    EditText editText;

    public static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userPageViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        editText = findViewById(R.id.NameUser);
        setNewName = findViewById(R.id.ApplyName);
        imageView = findViewById(R.id.imageView);

        userPageViewModel.setInformation();

        userPageViewModel.getImagePath().observe(this, image -> {
            Picasso.with(getApplicationContext())
                    .load(image)
                    .into(imageView);
        });
        userPageViewModel.getImgUri().observe(this, uri -> {
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        userPageViewModel.isGravatar().observe(this, status -> {
            if (status) {
                ((RadioButton) findViewById(R.id.GravatarButton)).setChecked(true);
                findViewById(R.id.ChooseImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.INVISIBLE);
            } else {
                ((RadioButton) findViewById(R.id.FireBaseButton)).setChecked(true);
                findViewById(R.id.ChooseImage).setVisibility(View.VISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.VISIBLE);
            }
        });

        userPageViewModel.getUserName().observe(this, userName -> editText.setHint(userName));
        setNewName.setOnClickListener(v -> userPageViewModel.setName(editText.getText().toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            userPageViewModel.setImgUri(data.getData());
        }
    }

    public void btnOpenGallery_Click(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_image)), REQUEST_CODE);
    }

    public void btnUploadImage_Click(View v) {
        userPageViewModel.uploadImage();
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.GravatarButton:
                findViewById(R.id.ChooseImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.INVISIBLE);
                userPageViewModel.changeButton(true);
                break;
            case R.id.FireBaseButton:
                findViewById(R.id.ChooseImage).setVisibility(View.VISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.VISIBLE);
                userPageViewModel.changeButton(false);
                break;
        }
    }
}