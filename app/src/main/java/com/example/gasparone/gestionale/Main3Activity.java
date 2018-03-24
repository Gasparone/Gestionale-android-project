package com.example.gasparone.gestionale;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main3Activity extends AppCompatActivity {

    JSONArray jsonArray;
    JSONObject jsonObj;
    TextView textNome;
    TextView textDispositivo;
    TextView textDescPro;
    TextView textDataAcc;
    TextView textDataComp;
    TextView textCorso;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        textNome = findViewById(R.id.textCliente);
        textDispositivo = findViewById(R.id.textDispositivo);
        textDescPro = findViewById(R.id.textDesc_pro);
        textDataAcc = findViewById(R.id.textData_acc);
        textDataComp = findViewById(R.id.textData_Comp);
        textCorso = findViewById(R.id.textCorso);

        String json = getIntent().getStringExtra("json");

        setUp(json);
    }

    protected void setUp(String json) {
        try {
            jsonArray = new JSONArray(json);
            jsonObj = jsonArray.getJSONObject(0);

            textDispositivo.setText(jsonObj.get("modello").toString());
            textDispositivo.setTextColor(Color.BLACK);
            textNome.setText(jsonObj.get("nome").toString() + " " + jsonObj.get("cognome").toString());
            textNome.setTextColor(Color.BLACK);
            textDescPro.setText(jsonObj.get("descrizione_cliente").toString());
            textDescPro.setTextColor(Color.BLACK);
            textDataAcc.setText(jsonObj.get("confermato").toString());
            textDataAcc.setTextColor(Color.BLACK);

            if (jsonObj.get("in_corso").equals("1")) {
                textCorso.setText("In lavorazione");
                textCorso.setTextColor(Color.BLACK);
                textCorso.setBackgroundColor(Color.rgb(224, 42, 29));
                textDataComp.setText("Non disponibile");
                textDataComp.setTextColor(Color.BLACK);
            } else {
                textCorso.setText("Completato");
                textCorso.setBackgroundColor(Color.GREEN);
                textDataComp.setText(jsonObj.get("completato").toString());
                textDataComp.setTextColor(Color.BLACK);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
