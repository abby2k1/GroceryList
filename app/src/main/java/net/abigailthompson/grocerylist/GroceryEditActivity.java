package net.abigailthompson.grocerylist;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

public class GroceryEditActivity extends AppCompatActivity implements RaterDialog.SaveRatingListener, OnMapReadyCallback {
    Grocery grocery;
    public static final String TAG = GroceryEditActivity.class.getName();
    boolean loading = true;
    public static final int PERMISSION_REQUEST_PHONE = 102;
    public static final int PERMISSION_REQUEST_CAMERA = 103;
    public static final int CAMERA_REQUEST = 1888;
    FusedLocationProviderClient fusedLocationClient;
    GoogleMap gMap1;

    int groceryId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        groceryId = extras.getInt("groceryid");
        this.setTitle("Grocery: " + groceryId);

        // init buttons
        Navbar.initListButton(this);
        Navbar.initMapButton(this);
        Navbar.initSettingsButton(this);

        initRatingButton();
        initSaveButton();
        initTextChanged(R.id.etName);
        initTextChanged(R.id.etCity);
        initTextChanged(R.id.editCell);
        initCallFunction();
        initImageButton();
        initMapTypeButtons();
        initGeoButton();

        //grocerys = new ArrayList<Grocery>();
        //grocerys = GroceryListActivity.readFromTextFile(this);
        //Log.d(TAG, "onCreate: Grocerys: " + grocerys.size());

        if(groceryId != -1)
        {
            // Editing an existing grocery
            //grocery = grocerys.get(groceryid-1);

            initGrocery(groceryId);
        }
        else {
            // Making a new grocery.
            grocery = new Grocery();
        }
        RebindGrocery();
        loading = false;
        setForEditing(false);
        initToggleButton();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        Log.d(TAG, "onCreate: End");
    }

    private void initGeoButton() {
        Button btnGeo = findViewById(R.id.btnGeo);
        btnGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroceryEditActivity.this, GroceryMapActivity.class);
                intent.putExtra("groceryid", grocery.getId());
                startActivity(intent);
            }
        });
    }

    private void initImageButton() {
        ImageButton imageGrocery = findViewById(R.id.imageGrocery);

        imageGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23)
                {
                    // Check for the manifest permission
                    if(ContextCompat.checkSelfPermission(GroceryEditActivity.this, Manifest.permission.CAMERA) != PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(GroceryEditActivity.this, Manifest.permission.CAMERA)){
                            Snackbar.make(findViewById(R.id.activity_main), "Grocerys requires this permission to take a photo.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "onClick: snackBar");
                                    ActivityCompat.requestPermissions(GroceryEditActivity.this,
                                            new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                                }
                            }).show();
                        }
                        else {
                            Log.d(TAG, "onClick: ");
                            ActivityCompat.requestPermissions(GroceryEditActivity.this,
                                    new String[] {Manifest.permission.CAMERA},PERMISSION_REQUEST_PHONE);
                            takePhoto();
                        }
                    }
                    else{
                        Log.d(TAG, "onClick: ");
                        takePhoto();
                    }
                }
                else {
                    // Only rely on the previous permissions
                    takePhoto();
                }
            }
        });

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);;
    }

    private void initMapTypeButtons() {
        Log.d(TAG, "initMapTypeButtons: Start");
        RadioGroup rgMapType = findViewById(R.id.radioGroupMapType1);
        rgMapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                Log.d(TAG, "onCheckedChanged: onCheckedChanged");
                RadioButton rbNormal = findViewById(R.id.radioButtonNormal1);
                if (rbNormal.isChecked()) {
                    gMap1.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                else  {
                    gMap1.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST)
        {
            if(resultCode == RESULT_OK)
            {
                Bitmap photo= (Bitmap)data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageButton = findViewById(R.id.imageGrocery);
                imageButton.setImageBitmap(scaledPhoto);
                grocery.setPhoto(scaledPhoto);
            }
        }
    }

    private void initCallFunction() {
        EditText etPhone = findViewById(R.id.editCell);
        etPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(grocery.getCellphone());
                return false;
            }
        });
    }

    private void initGrocery(int groceryid) {
        try{
            Log.d(TAG, "initGrocery: " + groceryid);
            RestClient.execGetOneRequest(GroceryListActivity.TEAMSAPI + groceryid,
                    this,
                    VolleyCallback -> {
                        grocery = VolleyCallback.get(0);
                        Log.d(TAG, "onSuccess: " + grocery.getName());
                        GroceryEditActivity.this.setTitle(grocery.getName());
                        RebindGrocery();
                    });
        }
        catch(Exception e)
        {
            Log.d(TAG, "initGrocery: " + e.getMessage());
        }

    }
    private void setForEditing(boolean enabled)
    {
        EditText editName = findViewById(R.id.etName);
        EditText editCity = findViewById(R.id.etCity);
        EditText editCellPhone = findViewById(R.id.editCell);
        TextView txtRating = findViewById(R.id.txtRating);
        Button btnRating = findViewById(R.id.btnRating);

        editName.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editCellPhone.setEnabled(enabled);
        btnRating.setEnabled(enabled);

        if(enabled)
            // Set focus to this control
            editName.requestFocus();
        else
        {
            // Scroll to the top of the scrollview
            ScrollView scrollView = findViewById(R.id.scrollView);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    private void checkPhonePermission(String cellphone) {
        // Check the API version
        if(Build.VERSION.SDK_INT >= 23)
        {
            // Check for the manifest permission
            if(ContextCompat.checkSelfPermission(GroceryEditActivity.this, Manifest.permission.CALL_PHONE) != PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(GroceryEditActivity.this, Manifest.permission.CALL_PHONE)){
                    Snackbar.make(findViewById(R.id.activity_main), "Grocerys requires this permission to place a call form the app.",
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: snackBar");

                            ActivityCompat.requestPermissions(GroceryEditActivity.this,
                                    new String[] {Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_PHONE);
                        }
                    }).show();
                }
                else {
                    Log.d(TAG, "checkPhonePermission: 1");
                    ActivityCompat.requestPermissions(GroceryEditActivity.this,
                            new String[] {Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_PHONE);
                    callGrocery(cellphone);
                }
            }
            else{
                Log.d(TAG, "checkPhonePermission: 2");
                callGrocery(cellphone);
            }
        }
        else {
            // Only rely on the previous permissions
            callGrocery(cellphone);
        }
    }

    private void callGrocery(String cellphone) {
        Log.d(TAG, "callGrocery: " + cellphone);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + cellphone));
        startActivity(intent);

    }

    private void initToggleButton() {

        ToggleButton toggleButton = findViewById(R.id.toggleButtonEdit);
        toggleButton.setChecked(false);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setForEditing(toggleButton.isChecked());
            }
        });

    }

    private void initTextChanged(int controlId) {
        EditText editText = findViewById(controlId);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!loading) {
                    Log.d(TAG, "afterTextChanged: "+ editable.toString());
                    grocery.setControlText(controlId, editable.toString());
                }
            }
        });
    }

    private void RebindGrocery()
    {
        EditText editName = findViewById(R.id.etName);
        EditText editCity = findViewById(R.id.etCity);
        EditText editCellPhone = findViewById(R.id.editCell);
        TextView txtRating = findViewById(R.id.txtRating);

        if(grocery == null)
            return;

        editName.setText(grocery.getName());
        editCity.setText(grocery.getCity());
        editCellPhone.setText(grocery.getCellphone());
        txtRating.setText(String.valueOf(grocery.getRating()));

        ImageButton imageGrocery = findViewById(R.id.imageGrocery);
        if(grocery.getPhoto() != null)
            imageGrocery.setImageBitmap(grocery.getPhoto());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }
    private void initSaveButton() {
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groceryId == -1)
                {
                    Log.d(TAG, "Inserting: " +grocery.toString());

                    RestClient.execPostRequest(grocery,
                            GroceryListActivity.TEAMSAPI,
                            GroceryEditActivity.this,
                            VolleyCallback -> {
                                grocery.setId(VolleyCallback.get(0).getId());
                                Log.d(TAG, "onSuccess: Post" + grocery.getId());
                            });
                }
                else {
                    Log.d(TAG, "Updating: " + grocery.toString());
                    RestClient.execPutRequest(grocery,
                            GroceryListActivity.TEAMSAPI + groceryId,
                            GroceryEditActivity.this,
                            VolleyCallback -> {
                                Log.d(TAG, "onSuccess: Post" + grocery.getId());
                            });
                }
                //FileIO.writeFile(GroceryListActivity.FILENAME,
                //           GroceryEditActivity.this,
                //                 GroceryListActivity.createGroceryArray(grocerys));
            }
        });

    }

    private void initRatingButton() {
        Button btnRating = findViewById(R.id.btnRating);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                RaterDialog raterDialog = new RaterDialog(grocery.getRating());
                raterDialog.show(fragmentManager, "Rate Grocery");
            }
        });

    }

    @Override
    public void didFinishGroceryRaterDialog(float rating) {
        Log.d(TAG, "didFinishGroceryRaterDialog: ");
        TextView txtRating = findViewById(R.id.txtRating);
        txtRating.setText(String.valueOf(rating));
        grocery.setRating(rating);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {

            Log.d(TAG, "onMapReady: ");
            gMap1 = googleMap;

            Point point = new Point();
            WindowManager windowManager = getWindowManager();
            windowManager.getDefaultDisplay().getSize(point);

            if (grocery != null) {
                LatLngBounds.Builder  builder = new LatLngBounds.Builder();
                LatLng marker = new LatLng(grocery.getLatitude(), grocery.getLongitude());
                builder.include(marker);

                gMap1.addMarker(new MarkerOptions()
                        .position(marker)
                        .title(grocery.getName())
                        .snippet(grocery.getMarkerText()));

                gMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 13f));

            } else {
                Log.d(TAG, "onMapReady: No grocery");
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "onMapReady: " + e.getMessage());
        }
    }
}