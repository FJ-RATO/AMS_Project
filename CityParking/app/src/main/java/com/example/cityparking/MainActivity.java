package com.example.cityparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button back;
    FirebaseAuth autentication;
    TextView email, nif, name;

    private static final String usersCol = "users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        nif = findViewById(R.id.nif);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autentication = FirebaseAuth.getInstance();
                FirebaseUser user = autentication.getCurrentUser();
                if(user != null) {
                    autentication.signOut();
                }

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        autentication = FirebaseAuth.getInstance();
        FirebaseUser user = autentication.getCurrentUser();

        if(user != null){

            DocumentReference userDoc = FirebaseFirestore.getInstance().document(usersCol + user.getUid());

            userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try{
                        name.setText(name.getText() + String.class.cast(documentSnapshot.get("name")));
                        email.setText(email.getText() + String.class.cast(documentSnapshot.get("email")));
                        nif.setText(nif.getText() + String.class.cast(documentSnapshot.get("nif")));
                    }catch(ClassCastException e){
                        name.setText(name.getText() + "Casting error");
                        return;
                    }catch (NullPointerException e){
                        name.setText(name.getText() + "Element not in Data Base");
                        return;
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    name.setText(name.getText() + "Error reading Data Base");
                    email.setText(email.getText() + "Error reading Data Base");
                    nif.setText(nif.getText() + "Error reading Data Base");
                }
            });
        }else{
            name.setText(name.getText() + "Not loged in");
            email.setText(email.getText() + "Not loged in");
            nif.setText(nif.getText() + "Not loged in");
        }

    }


}
