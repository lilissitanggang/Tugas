package com.example.lilis;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Lilis extends Activity {

	private String TAG = Lilis.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private Button btambah;
 
    // URL to get contacts JSON
    private static String url = "http://apilearning.totopeto.com/contacts";
 
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lilis);
        
        contactList = new ArrayList<HashMap<String, String>>();
        
        lv = (ListView) findViewById(R.id.list);
        btambah = (Button) findViewById(R.id.btambahkontak);
 
        new GetContacts().execute();
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> hm = contactList.get(position);
				Intent intent = new Intent(Lilis.this, ContactDetail.class);
				intent.putExtra("id", hm.get("id"));
				startActivity(intent);
			}
		});
        
        btambah.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Lilis.this, TambahContact.class);
				startActivity(intent);
			}
		});
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
    	 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Lilis.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
 
            // Making a request to URL and getting response
            String jsonStr = sh.makeServiceCall(url);
 
            Log.e(TAG, "Response from url: " + jsonStr);
 
            // Read JSON
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
 
                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");
 
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String address = c.getString("address");
                        String email = c.getString("email");
                        String phone = c.getString("phone");
                        String dob = c.getString("dob");
                        
                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("address", address);
                        contact.put("email", email);
                        contact.put("phone", phone);
                        contact.put("dob", dob);
                        
                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
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
            ListAdapter adapter = new SimpleAdapter(
                    Lilis.this, contactList,
                    R.layout.listitem, new String[]{"name", "email",
                    "phone"}, new int[]{R.id.name,
                    R.id.email, R.id.phone});
 
            lv.setAdapter(adapter);
        }
    }
}

