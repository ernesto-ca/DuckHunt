package com.proyecto.duckhunt.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.proyecto.duckhunt.R;
import com.proyecto.duckhunt.common.Constantes;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvCounterDucks, tvNickname, tvTimer;
    private ImageView ivDuck;
    int Counter = 0;
    int millsGameplay = 750;
    int anchoPantalla = 0, altoPantalla = 0;
    Random aleatorio;
    public boolean gameOver = false;
    String idUser= "", nick="", lastDucks = "";
    FirebaseFirestore dataBase;
    MediaPlayer mediaPlayer;
    int velocidad = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dataBase = FirebaseFirestore.getInstance();

        // Iniciamos los elementos View
        initViewComponents();

        // Metodo de los eventos
        events();

        //Metodo para obtener el ancho y alto del dispositivo
        initPantalla();


        //Metodo cuenta atrás
        initCuentaAtras();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(!gameOver) {
            //mover continuamente
            moveDuckHandler();
        }
    }

    private void initCuentaAtras() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                long segRes = millisUntilFinished/1000;
                tvTimer.setText(segRes+"s");
            }

            public void onFinish() {
                tvTimer.setText("0s");
                gameOver = true;
                mostrarGameOver();

            }
        }.start();

    }

    private void saveResultFirestore() {
        if(Integer.parseInt(lastDucks)<=Counter){
            dataBase.collection(Constantes.USERS_FIRESTORE)
                    .document(idUser)
                    .update(Constantes.DOC_DUCKS,Counter);
        }

    }

    private void mostrarGameOver() {

        mediaPlayer = MediaPlayer.create(GameActivity.this,R.raw.over);
        mediaPlayer.seekTo(3000);
        mediaPlayer.start();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Game Over")
               .setMessage("Has conseguido cazar: "+Counter+" patos. \n"+"Tu puntuación anterior fue: "+lastDucks+ " patos.")
               .setIcon(R.drawable.duck_clicked)
               .setCancelable(false);

        builder.setPositiveButton("Jugar de Nuevo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveResultFirestore();
                Counter = 0;
                tvCounterDucks.setText("0");
                gameOver = false;
                moveDuck();
                initCuentaAtras();

            }
        });
        builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveResultFirestore();
                dialog.dismiss();
                //destruir la actividad GameActivity
                finish();
            }
        });


        builder.create().show();


    }

    private void initPantalla() {
        // 1. Obtener el tamaño de la pantalla del dispositivo donde se está ejecutando la App.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        anchoPantalla = size.x;
        altoPantalla = size.y;

        //Inicializamos el objeto para generar números aleatorios.
        aleatorio = new Random();
        moveDuck();
    }

    private void events() {

        ivDuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = MediaPlayer.create(GameActivity.this,R.raw.shot);
                mediaPlayer.start();
                if(!gameOver) {
                Counter++;
                tvCounterDucks.setText(String.valueOf(Counter));
                ivDuck.setImageResource(R.drawable.duck_clicked);
                if(Counter>15){
                    if(Counter==20){
                        Toast.makeText(GameActivity.this, "MÁS RAPIDO!!!", Toast.LENGTH_SHORT).show();
                        velocidad = 5;
                    }
                    millsGameplay = millsGameplay - velocidad;}
                //Ejecutar código  tiempo después (milisegundos)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivDuck.setImageResource(R.drawable.duck);

                    }
                }, 75);
             }
            }
        });
    }

    private void moveDuck() {
        int min = 0;
        int maximoX = anchoPantalla - ivDuck.getWidth();
        int maximoY = altoPantalla - ivDuck.getWidth();

        //Generamos dos numero aleatorios uno para la coordenada X y otra para Y
        int randomX = aleatorio.nextInt(((maximoX - min) + 1) + min);
        int randomY = aleatorio.nextInt(((maximoY - min) + 1) + min);

        //Utilizamos los numeros aleatorios para mover el pato a esa posición
        ivDuck.setX(randomX);
        ivDuck.setY(randomY);

        moveDuckHandler();
    }



    private void moveDuckHandler() {
        if(!gameOver) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveDuck();
                }
            }, millsGameplay);
        }
    }

    private void initViewComponents() {
        tvCounterDucks = findViewById(R.id.tvCounter);
        tvTimer = findViewById(R.id.tvTimer);
        tvNickname = findViewById(R.id.tvNick);
        ivDuck = findViewById(R.id.ivDuck);

        //Cambiar tipo de fuente
        Typeface typeface =  Typeface.createFromAsset(getAssets(),"pixel.ttf");
        tvCounterDucks.setTypeface(typeface);
        tvTimer.setTypeface(typeface);
        tvNickname.setTypeface(typeface);

        //Obtenemos de extras el nick y mostramos en el Textview
        Bundle b = getIntent().getExtras();
        nick = b.getString(Constantes.EXTRA_NICK);
        idUser = b.getString(Constantes.EXTRA_ID);
        lastDucks = b.getString(Constantes.DOC_DUCKS);
        tvNickname.setText(nick);

    }


}