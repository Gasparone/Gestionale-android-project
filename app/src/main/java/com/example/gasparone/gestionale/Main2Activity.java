package com.example.gasparone.gestionale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {

    ListView lv;
    JSONArray jsonArray;
    HashMap<String, String> result = new HashMap<>();
    HashMap<String, String> result2 = new HashMap<>();
    ArrayList<HashMap<String, String>> resultList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        lv = findViewById(R.id.listView);
        resultList = new ArrayList<>();
        String json = getIntent().getStringExtra("json");
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String cod = jsonArray.getJSONObject(i).get("cod_intervento").toString();
                String cf = jsonArray.getJSONObject(i).get("confermato").toString();
                String m = jsonArray.getJSONObject(i).get("modello").toString();

                result = new HashMap<>();
                result.put("dispositivo", "Dispositivo: " + m.toUpperCase());
                result.put("confermato", "Consegnato in negozio il: " + cf.toUpperCase());
                result.put("codice", "Codice intervento: " + cod.toUpperCase());

                resultList.add(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        diplayList();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                result2 = (HashMap) lv.getAdapter().getItem(i);
                String codice = result2.get("codice");
                int temp = 0;
                for (int j = 0; j < codice.length(); j++) {
                    if (codice.charAt(j) == ':') {
                        temp = j;
                    }

                }
                String num = codice.substring(temp + 2, codice.length());
                GetInfo g = new GetInfo();
                g.execute(num);

            }
        });
    }

    protected void diplayList() {
        ListAdapter adapter = new SimpleAdapter(this, resultList,
                R.layout.list_layout, new String[]{"dispositivo", "confermato", "codice"},
                new int[]{R.id.dispositivo, R.id.confermato, R.id.id});

        lv.setAdapter(adapter);
    }

    public class GetInfo extends AsyncTask<String, Void, Void> {

        String jsonStr = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Scarico dati...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(String... Strings) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://gaspasites.altervista.org/dati.php?id=" + Strings[0].toString();
            jsonStr = sh.makeServiceCall(url);
            try {
                jsonStr.length();
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (jsonStr.equals(null)) {
                Toast.makeText(getApplicationContext(), "Nessun risultato, qualcosa non è andato :(", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
                intent.putExtra("json", "" + jsonStr);
                startActivity(intent);
            }
        }
    }

}
