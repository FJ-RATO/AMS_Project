package com.example.cityparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    FirebaseAuth autentication;
    Button signup, back;
    EditText email, password, name, nif, confpassword;

    List<EditText> fields;

    private static final String usersCol = "users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        autentication = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confpassword = findViewById(R.id.password_confirm);
        nif = findViewById(R.id.nif);
        name = findViewById(R.id.name);
        signup = findViewById(R.id.signup);
        back = findViewById(R.id.back);
        fields = new LinkedList<>();
        fields.add(name);fields.add(email);fields.add(nif);fields.add(password);fields.add(confpassword);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!password.getText().toString().equals(confpassword.getText().toString())){
                    confpassword.setError("Passwords dont match");
                    confpassword.requestFocus();
                    return;
                }
                for(EditText field : fields){
                    if(field.getText().toString().isEmpty()){
                        field.setError("Mandatory Field");
                        field.requestFocus();
                        return;
                    }
                }
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();
                autentication.createUserWithEmailAndPassword(strEmail,strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        addUsertoDB(name.getText().toString(), email.getText().toString(), authResult.getUser().getUid(),nif.getText().toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e.getMessage().toLowerCase().contains("email")){
                            email.setError(e.getMessage());
                            email.requestFocus();
                        }else{
                            confpassword.setError(e.getMessage());
                            confpassword.requestFocus();
                        }

                        Log.d("ERROR", "Error creating acount " + e);
                    }
                });

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addUsertoDB(String name, String email, String id, String nif){
        DocumentReference userDoc = FirebaseFirestore.getInstance().document(usersCol + id);
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("nif", nif);
        userDoc.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
