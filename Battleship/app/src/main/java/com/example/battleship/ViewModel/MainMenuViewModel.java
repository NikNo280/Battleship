package com.example.battleship.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.battleship.Activity.MainMenu;
import com.example.battleship.Model.FirebaseAuthenticationModel;
import com.example.battleship.Model.FirebaseDatabaseModel;
import com.example.battleship.Model.FirebaseStorageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainMenuViewModel extends AndroidViewModel {

    private final MutableLiveData<HashMap<String, Object>> moveToRoom = new MutableLiveData<>();


    FirebaseAuthenticationModel firebaseAuth;
    FirebaseDatabaseModel firebaseDatabase;

    public MainMenuViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new FirebaseAuthenticationModel();
        firebaseDatabase = new FirebaseDatabaseModel();
    }

    public LiveData<HashMap<String, Object>> getMoveToRoom() {
        return moveToRoom;
    }

    public void createRoom(String roomName) {
        if (roomName.isEmpty()) {
            Toast.makeText(getApplication(), "Поле пустое", Toast.LENGTH_SHORT).show();
            return;
        }
        String key = firebaseDatabase.push("rooms");

        Map<String, Object> values = new HashMap<>();
        values.put("name", roomName);
        firebaseDatabase.updateChild("/rooms/" + key, values);

        values = new HashMap<>();
        values.put("user", firebaseAuth.getUserUID());
        values.put("ship", MainMenu.MAX_SHIP_COUNT);
        firebaseDatabase.updateChild("/rooms/" + key + "/p1", values);

        HashMap<String, Object> value = new HashMap<>();
        value.put("role", MainMenu.REQUEST_HOST);
        value.put("name", key);
        moveToRoom.setValue(value);
    }

    public void connectToRoom(String key) {
        if (key.isEmpty()) {
            Toast.makeText(getApplication(), "Комнаты не существует", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseDatabase.getReference("rooms/" + key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseDatabase.setValue("rooms/" + key + "/p2/user", firebaseAuth.getUserUID());
                    firebaseDatabase.setValue("rooms/" + key + "/p2/ship", MainMenu.MAX_SHIP_COUNT);
                    HashMap<String, Object> value = new HashMap<>();
                    value.put("role", MainMenu.REQUEST_GUEST);
                    value.put("name", key);
                    moveToRoom.setValue(value);
                } else {
                    Toast.makeText(getApplication(), "Комнаты не существует", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void removeRoom() {
        firebaseDatabase.remove("rooms/" + moveToRoom.getValue().get("name").toString());
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}