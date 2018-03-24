package com.example.gasparone.gestionale;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText textCognome;
    EditText textNome;
    Button button;
    GifImageView img;
    String jsonStr = null;
    JSONArray jsonArray;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textCognome = findViewById(R.id.textCognome);
        textNome = findViewById(R.id.textNome);
        img = findViewById(R.id.img);


        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected == true) {
            new GifArray().execute("https://media.giphy.com/media/2tO3rdzQEHM5ICU8Lh/giphy.gif");
            img.startAnimation();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flag = false;
                    if (textNome.getText().toString().equals("") || textCognome.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "Compila entrambi i campi!", Toast.LENGTH_SHORT).show();
                    } else {
                        GetContacts g = new GetContacts();
                        g.execute();
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Attenzione");
            builder.setMessage("Connessione assente! Assicurati di essere connesso ad internet");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closeContextMenu();
                }
            }).show();
        }
    }

    public class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://gaspasites.altervista.org/ricerca.php?nome=" +
                    textNome.getText().toString() + "&cognome=" +
                    textCognome.getText().toString();
            jsonStr = sh.makeServiceCall(url);
            try {
                jsonArray = new JSONArray(jsonStr);
                jsonArray.length();
            } catch (JSONException e) {
                flag = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (flag == true) {
                Toast.makeText(getApplicationContext(), "Nessun risultato, inserisci Nome e Cognome corretti", Toast.LENGTH_LONG).show();
                textCognome.setText("");
                textNome.setText("");
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                textNome.startAnimation(shake);
                textCognome.startAnimation(shake);
            } else {
                textCognome.setText("");
                textNome.setText("");
                Toast.makeText(MainActivity.this, "Connessione...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("json", "" + jsonStr);
                startActivity(intent);
            }
        }
    }

    public class GifArray extends AsyncTask<String, Void, byte[]> {

        @Override
        protected byte[] doInBackground(String... strings) {


            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[10240];
                    while ((nRead = in.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    return buffer.toByteArray();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            img.setBytes(bytes);
        }
    }
}
