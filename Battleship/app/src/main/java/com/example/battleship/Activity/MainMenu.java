package com.example.battleship.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.battleship.R;
import com.example.battleship.ViewModel.MainMenuViewModel;

public class MainMenu extends AppCompatActivity {
    public static final int REQUEST_HOST = 103;
    public static final int REQUEST_GUEST = 104;
    public static final int MAX_SHIP_COUNT = 10;

    public static final String PARAM_MENU_USER_PAGE = "User Page";
    public static final String PARAM_MENU_USER_STATISTIC = "User Statistic";
    public static final String PARAM_INTENT_NAME_OF_ROOM = "roomName";
    public static final String PARAM_INTENT_TYPE_VIEWMODEL = "ViewModel";
    public static final String TYPE_VIEWMODEL_PLACEMENT = "Placement";
    public static final String TYPE_VIEWMODEL_BATTLE = "BattleView";

    private EditText textView;
    private MainMenuViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        dashboardViewModel = ViewModelProviders.of(this).get(MainMenuViewModel.class);

        dashboardViewModel.getMoveToRoom().observe(this, text -> moveToRoom(Integer.parseInt(text.get("role").toString()), text.get("name").toString()));
        textView = findViewById(R.id.room);

        findViewById(R.id.create).setOnClickListener(v -> dashboardViewModel.createRoom(textView.getText().toString()));
        findViewById(R.id.connect).setOnClickListener(v -> dashboardViewModel.connectToRoom(textView.getText().toString()));
    }

    public void btnGoToProfile(View view) {
        Intent intent  = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void moveToRoom(int index, String key) {
        Intent intent = new Intent(MainMenu.this, PreparationRoom.class);
        intent.putExtra(PARAM_INTENT_NAME_OF_ROOM, key);
        intent.putExtra(PARAM_INTENT_TYPE_VIEWMODEL, TYPE_VIEWMODEL_PLACEMENT);
        startActivityForResult(intent, index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_HOST) {
            if (resultCode == 0) {
                Toast.makeText(MainMenu.this, "Вы проиграли", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_GUEST) {
            if (resultCode == 0) {
                dashboardViewModel.removeRoom();
                Toast.makeText(MainMenu.this, "Вы победили", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}