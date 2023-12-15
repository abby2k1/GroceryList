package net.abigailthompson.grocerylist;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroceryMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "GroceryMapActivity";
    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    ArrayList<Grocery> Grocerys = new ArrayList<>();
    Grocery currentGrocery = null;
    int groceryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_map);

        try {
            Bundle extras = getIntent().getExtras();
            groceryId = extras.getInt("groceryid");
            this.setTitle("Grocery: " + groceryId);

            initMapTypeButtons();
            initGetLocationButton();
            initSaveButton();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

        if(groceryId != -1)
        {
            initGrocery(groceryId);
        }
        else {
            // Making a new grocery.
            currentGrocery = new Grocery();
        }

        Navbar.initListButton(this);
        Navbar.initMapButton(this);
        Navbar.initSettingsButton(this);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //createLocationRequest();
        createLocationCallback();

    }

    //    locationRequest = LocationRequest.create();
    //    locationRequest.setInterval(10000);
    //    locationRequest.setFastestInterval(5000);
    //    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                                    " Long: " + location.getLongitude() +
                                    " Accuracy:  " + location.getAccuracy(),
                            Toast.LENGTH_LONG).show();
                }
            };
        };
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null );
        gMap.setMyLocationEnabled(true);
    }

    private void stopLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void initGetLocationButton(){
        Button buttonGetLocation = findViewById(R.id.buttonGetLocation);

        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editAddress = findViewById(R.id.editAddress);
                EditText editCity = findViewById(R.id.editCity);
                EditText editState = findViewById(R.id.editState);
                EditText editZipcode = findViewById(R.id.editZipcode);

                String address = editAddress.getText().toString() + ", " +
                        editCity.getText().toString() + ", " +
                        editState.getText().toString() + ", " +
                        editZipcode.getText().toString();
                List<Address> addresses = null;

                Geocoder geo = new Geocoder(GroceryMapActivity.this);
                try{
                    addresses = geo.getFromLocationName(address, 1);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                TextView txtLatitude = findViewById((R.id.textLatitude));
                TextView txtLongitude = findViewById((R.id.textLongitude));

                txtLatitude.setText(String.valueOf(addresses.get(0).getLatitude()));
                txtLongitude.setText(String.valueOf(addresses.get(0).getLongitude()));

                if(currentGrocery != null)
                {
                    currentGrocery.setLatitude(addresses.get(0).getLatitude());
                    currentGrocery.setLongitude(addresses.get(0).getLongitude());
                }


            }
        });
    }

    private void initGrocery(int groceryid) {
        try{
            Log.d(TAG, "initGrocery: " + groceryid);

            RestClient.execGetOneRequest(GroceryListActivity.TEAMSAPI + groceryid,
                    this,
                    new VolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<Grocery> result) {
                            currentGrocery = result.get(0);
                            Log.d(TAG, "onSuccess: " + currentGrocery.getName());
                            GroceryMapActivity.this.setTitle(currentGrocery.getName());
                        }
                    });

            Log.d(TAG, "initGrocery: " + currentGrocery.toString());
        }
        catch(Exception e)
        {
            Log.d(TAG, "initGrocery: " + e.getMessage());
        }

    }

    private void initSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groceryId == -1)
                {
                    Log.d(TAG, "Inserting: " +currentGrocery.toString());
                    RestClient.execPostRequest(currentGrocery,
                            GroceryListActivity.TEAMSAPI,
                            GroceryMapActivity.this,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<Grocery> result) {
                                    currentGrocery.setId(result.get(0).getId());
                                    Log.d(TAG, "onSuccess: Post" + currentGrocery.getId());
                                }
                            });
                }
                else {
                    Log.d(TAG, "Updating: " + currentGrocery.toString());
                    RestClient.execPutRequest(currentGrocery,
                            GroceryListActivity.TEAMSAPI + groceryId,
                            GroceryMapActivity.this,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(ArrayList<Grocery> result) {
                                    Log.d(TAG, "onSuccess: Post" + currentGrocery.getId());
                                }
                            });
                }
            }
        });

    }

    private void initMapTypeButtons() {
        RadioGroup rgMapType = findViewById(R.id.radioGroupMapType);
        rgMapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbNormal = findViewById(R.id.radioButtonNormal);
                if (rbNormal.isChecked()) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                else  {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        RadioButton rbNormal = findViewById(R.id.radioButtonNormal);
        rbNormal.setChecked(true);

        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;

        if (Grocerys.size()>0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i=0; i<Grocerys.size(); i++) {
                currentGrocery = Grocerys.get(i);

                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                //String address = currentGrocery.getStreetAddress() + ", " +
                //        currentGrocery.getCity() + ", " +
                //        currentGrocery.getState() + " " +
                //        currentGrocery.getZipCode();

                //try {
                //addresses = geo.getFromLocationName(address, 1);
                //}

                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                builder.include(point);

                //gMap.addMarker(new MarkerOptions().position(point).title(currentGrocery.getGroceryName()).snippet(address));
            }
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), measuredWidth, measuredHeight, 450));
        }
        else {
            if (currentGrocery != null) {
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                /*String address = currentGrocery.getStreetAddress() + ", " +
                        currentGrocery.getCity() + ", " +
                        currentGrocery.getState() + " " +
                        currentGrocery.getZipCode();*/

                try {
                    // addresses = geo.getFromLocationName(address, 1);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                LatLng point = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());

                //gMap.addMarker(new MarkerOptions().position(point).title(currentGrocery.getGroceryName()).snippet(address));
                gMap.animateCamera(CameraUpdateFactory. newLatLngZoom(point, 16));
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(GroceryMapActivity.this).create();
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    } });
                alertDialog.show();
            }
        }


        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(GroceryMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(GroceryMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                        Snackbar.make(findViewById(R.id.activity_grocery_map), "MyGroceryList requires this permission to locate " + "your Grocerys", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        ActivityCompat.requestPermissions(GroceryMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LOCATION);
                                    }
                                })
                                .show();

                    } else {
                        ActivityCompat.requestPermissions(GroceryMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LOCATION);
                    }
                } else {
                    startLocationUpdates();
                }
            }else {
                startLocationUpdates();
            }
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}