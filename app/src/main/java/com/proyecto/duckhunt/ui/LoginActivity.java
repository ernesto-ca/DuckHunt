package com.proyecto.duckhunt.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.duckhunt.R;
import com.proyecto.duckhunt.common.Constantes;
import com.proyecto.duckhunt.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etNick;
    private Button btnStart, btnRanking;
    FirebaseFirestore dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNick = findViewById(R.id.etNickLogin);
        btnStart = findViewById(R.id.btnStart);
        btnRanking = findViewById(R.id.btnGotoRanking);

        // Instanciamos la conexión a firestore
        dataBase = FirebaseFirestore.getInstance();

        //Cambiar tipo de fuente
        Typeface typeface =  Typeface.createFromAsset(getAssets(),"pixel.ttf");
        etNick.setTypeface(typeface);
        btnStart.setTypeface(typeface);
        btnRanking.setTypeface(typeface);

        //Onclick
        btnStart.setOnClickListener(this);
        btnRanking.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
    if(v.getId() == btnStart.getId()) {
        if (etNick.getText().toString().length() > 3) {
            addNickAndStart();
        } else if (etNick.getText().toString().isEmpty()) {
            etNick.setError("El nombre de usuario es obligatorio");
        } else {
            etNick.setError("El nombre de usuario debe tener más de 3 carácteres");
        }
    }else {
        Intent i = new Intent(LoginActivity.this,UserRankingActivity.class);
        startActivity(i);
    }
    }

    private void addNickAndStart() {
        dataBase.collection(Constantes.USERS_FIRESTORE).whereEqualTo("nick", etNick.getText().toString().trim())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()>0){
                            Intent i = new Intent(LoginActivity.this,GameActivity.class);
                            i.putExtra(Constantes.EXTRA_NICK,etNick.getText().toString().trim());
                            i.putExtra(Constantes.EXTRA_ID, queryDocumentSnapshots.getDocuments().get(0).getId());
                            i.putExtra(Constantes.DOC_DUCKS,""+queryDocumentSnapshots.getDocuments().get(0).get(Constantes.DOC_DUCKS));
                            etNick.setText("");
                            startActivity(i);
                        }else{

                            addNickToFiretore();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"JUGANDO SIN GUARDAR",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this,GameActivity.class);
                        i.putExtra(Constantes.EXTRA_NICK,etNick.getText().toString().trim());
                        i.putExtra(Constantes.EXTRA_ID, "-");
                        i.putExtra(Constantes.DOC_DUCKS,"0");
                        etNick.setText("");
                        startActivity(i);
                    }
                });

    }


    private void addNickToFiretore() {
        dataBase.collection(Constantes.USERS_FIRESTORE)
                .add(new User(etNick.getText().toString().trim(),0))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent i = new Intent(LoginActivity.this,GameActivity.class);
                        i.putExtra(Constantes.EXTRA_NICK,etNick.getText().toString().trim());
                        i.putExtra(Constantes.EXTRA_ID, documentReference.getId());
                        i.putExtra(Constantes.DOC_DUCKS,""+0);
                        etNick.setText("");
                        startActivity(i);
                    }
                });

    }
}