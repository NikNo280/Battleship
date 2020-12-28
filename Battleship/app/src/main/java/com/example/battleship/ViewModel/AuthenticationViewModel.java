package com.example.battleship.ViewModel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.battleship.Activity.MainActivity;
import com.example.battleship.Model.FirebaseAuthenticationModel;
import com.example.battleship.Model.FirebaseDatabaseModel;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationViewModel extends AndroidViewModel{
    private final MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>();

    FirebaseAuthenticationModel firebaseAuth;
    FirebaseDatabaseModel firebaseDatabase;

    public LiveData<Boolean> isSuccessful() {
        return isSuccessful;
    }

    public AuthenticationViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new FirebaseAuthenticationModel();
        firebaseDatabase = new FirebaseDatabaseModel();
    }

    public void Registration(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplication(), "Регистрация провалена", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidEmail(email)) {
            Toast.makeText(getApplication(), "Регистрация провалена", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> values = new HashMap<>();
                values.put("Gravatar", false);
                values.put("userName", firebaseAuth.getUserUID());
                values.put("Image", "https://firebasestorage.googleapis.com/v0/b/battleship-8cda2.appspot.com/o/avatar.jpg?alt=media&token=303f6098-cf6a-4c5f-9a69-80514ffcdf12");
                firebaseDatabase.updateChild("Users/" + firebaseAuth.getUserUID(), values);
                isSuccessful.setValue(true);
                Toast.makeText(getApplication(), "Регистрация успешна", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplication(), "Регистрация провалена", Toast.LENGTH_SHORT).show();
                isSuccessful.setValue(false);
            }
        });
    }

    public void Login(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplication(), "Aвторизация провалена", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> isSuccessful.setValue(task.isSuccessful()));
        return;
    }

    private Boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
