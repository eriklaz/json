package com.lazaro.json;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends ListActivity{

    private ProgressDialog pDialog;
    // URL to get contacts JSON
    private static String url ="http://eulisesrdz.260mb.net/lazaro/aacc.json";

    // JSON Node names
    private final String KEY_ESCUELA= "alumnos";
    private final String KEY_MATRICULA = "matricula";
    private final String KEY_NOMBRE = "nombre";
    private final String KEY_APELLIDOS    = "apellidos";
    private final String KEY_CURP = "curp";
    private final String KEY_EMAIL = "email";
    private final String KEY_CARRERA = "carrera";
    private final String KEY_TURNO = "turno";
    // contacts JSONArray
    JSONArray alumnos = null;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> escuetList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        escuetList = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();
        // Listview on item click listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                String matricula = ((TextView) view.findViewById(R.id.txtmatr)).getText().toString();
                String nombre = ((TextView) view.findViewById(R.id.txtnomb)).getText().toString();
                String apelli = ((TextView) view.findViewById(R.id.txtapell)).getText().toString();
                String curp = ((TextView) view.findViewById(R.id.txtcurp)).getText().toString();
                String email = ((TextView) view.findViewById(R.id.txtemail)).getText().toString();
                String carrera = ((TextView) view.findViewById(R.id.txtcarr)).getText().toString();
                String turno = ((TextView) view.findViewById(R.id.txttur)).getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),vista_alumnos.class);
                in.putExtra(KEY_MATRICULA, matricula);
                in.putExtra(KEY_NOMBRE, nombre);
                in.putExtra(KEY_APELLIDOS, apelli);
                in.putExtra(KEY_CURP, curp);
                in.putExtra(KEY_EMAIL, email);
                in.putExtra(KEY_CARRERA, carrera);
                in.putExtra(KEY_TURNO, turno);

                startActivity(in);
            }
        });
        // Calling async task to get json
        new GetAlumnos().execute();
    }
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetAlumnos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


    }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    alumnos    = jsonObj.getJSONArray(KEY_ESCUELA);

                    // looping through All Contacts
                    for (int i = 0; i < alumnos .length(); i++) {
                        JSONObject c = alumnos.getJSONObject(i);
                        String matricula = c.getString(KEY_ESCUELA);
                        String nombre = c.getString(KEY_NOMBRE);
                        String apelli = c.getString(KEY_APELLIDOS);
                        String curp = c.getString(KEY_CURP);
                        String email = c.getString(KEY_EMAIL);
                        String carrera = c.getString(KEY_CARRERA);
                        String turno = c.getString(KEY_TURNO);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(KEY_MATRICULA, matricula);
                        contact.put(KEY_NOMBRE, nombre);
                        contact.put(KEY_APELLIDOS, apelli);
                        contact.put(KEY_CURP, curp);
                        contact.put(KEY_EMAIL, email);
                        contact.put(KEY_CARRERA, carrera);
                        contact.put(KEY_TURNO, turno);

                        // adding contact to contact list
                        escuetList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, escuetList,
                    R.layout.lista_alumnos, new String[] {KEY_MATRICULA, KEY_NOMBRE, KEY_APELLIDOS, KEY_CURP, KEY_EMAIL, KEY_CARRERA, KEY_TURNO},
                    new int[] { R.id.txtmatr,R.id.txtnomb,R.id.txtapell,R.id.txtcurp, R.id.txtemail, R.id.txtcarr, R.id.txttur });
            setListAdapter(adapter);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
