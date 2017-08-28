package com.example.dilleswararao.map_plot;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //public class MainActivity extends AppCompatActivity

    private static final String TAG = "MainActivity";
    TextView yourTextView;
    String lat[]= new String[6],lon[]= new String[6];
    String name[]= new String[6];
    int i;

    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result = "";
            String contact_name = "";
            String contact_phoneno = "";
            String contact_addrss = "";
            String contact_email = "";

            try {
                URL url = new URL("http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt");
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if (code == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";
                        i=0;

                        while ((line = bufferedReader.readLine()) != null) {
                            String[] parts = line.split(" ");
                            contact_name = parts[0];
                            contact_email = parts[1];
                            contact_phoneno = parts[2];
                            contact_addrss = parts[3];
                            lat[i]=contact_phoneno;
                            lon[i] = contact_addrss;
                            name[i] = contact_name ;
                            Log.d(TAG, "doInBackground: "+lat[i]);
                            i++;
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                            int rawContactInsertIndex = ops.size();
                            ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                    .build());
                            {
                                ops.add(ContentProviderOperation.newInsert(
                                        ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(
                                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact_name).build());
                            }

                            {
                                ops.add(ContentProviderOperation.
                                        newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact_phoneno)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                        .build());
                            }
                            {
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact_email)
                                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                        .build());
                            }
                            {
                                ops.add(ContentProviderOperation.
                                        newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact_addrss)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                        .build());
                            }

                            try {
                                ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            } catch (RemoteException e) {
                                // error
                            } catch (OperationApplicationException e) {
                                // error
                            }
                            //   result += line;
                            Log.d(TAG, "doInBackground: " + line);


                        }
                        Log.d(TAG, "doInBackground: " + line);
                    }
                    in.close();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            //yourTextView.setText(result);
            // super.onPostExecute(s);
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        //yourTextView = (TextView)findViewById(R.id.textView);
//        GetData getdata = new GetData();
//        getdata.execute();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button locate = (Button) findViewById(R.id.locater);
        locate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        GetData getdata = new GetData();
        getdata.execute();

    }


    /**
     * Manipulates the map once available.

     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, theg user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        i=0;
        while (i<5){
            Log.d(TAG, "onMapReady: "+lat[i]);
            LatLng pos = new LatLng(Integer.parseInt(lat[i])/1000000,Integer.parseInt(lon[i])/1000000);
            mMap.addMarker(new MarkerOptions().position(pos).title(name[i]));
            // mMap.addMarker(new MarkerOptions().position(new LatLng(Integer.parseInt(lat[i])/ 10000000, Integer.parseInt(lon[i]) / 100000000)).title(name[i]));
        i++ ;
        }

       //mMap.addMarker(new MarkerOptions().position(new LatLng(lat[1]/10000000, lon[1]/100000000)).title(name[1]));
       // mMap.addMarker(new MarkerOptions().position(new LatLng(lat[2]/10000000, lon[2]/100000000)).title(name[2]));
       // mMap.addMarker(new MarkerOptions().position(new LatLng(lat[3]/10000000, lon[3]/100000000)).title(name[3]));
       // mMap.addMarker(new MarkerOptions().position(new LatLng(lat[4]/10000000, lon[4]/100000000)).title(name[4]));


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
