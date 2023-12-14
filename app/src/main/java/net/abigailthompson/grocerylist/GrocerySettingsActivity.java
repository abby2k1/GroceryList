package net.abigailthompson.grocerylist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class GrocerySettingsActivity extends AppCompatActivity {
    public static final String TAG = "GrocerySettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_settings);

        // init buttons
        Navbar.initListButton(this);
        Navbar.initMapButton(this);
        Navbar.initSettingsButton(this);

        initSortByClick();
        initSortOrderClick();
        initSettings();

        this.setTitle(getString(R.string.grocery_settings));
    }

    private void initSortByClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);

        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbName = findViewById(R.id.radioName);
                RadioButton rbCity = findViewById(R.id.radioCity);
                RadioButton rbIsFavorite = findViewById(R.id.radioIsFavorite);

                String sortBy;

                if(rbName.isChecked())
                {
                    sortBy = "name";
                }
                else if(rbCity.isChecked())
                {
                    sortBy = "city";
                }
                else
                {
                    sortBy = "isfavorite";
                }

                getSharedPreferences("groceryspreferences",
                        Context.MODE_PRIVATE)
                        .edit()
                        .putString("sortfield", sortBy)
                        .apply();
                Log.d(TAG, "onCheckedChanged: " + sortBy);
            }
        });

    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortOrder);

        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbAscending = findViewById(R.id.radioAscending);
                RadioButton rbDescending = findViewById(R.id.radioDescending);

                String sortOrder;

                if(rbAscending.isChecked())
                {
                    sortOrder = "ASC";
                }
                else
                {
                    sortOrder = "DESC";
                }

                getSharedPreferences("groceryspreferences",
                        Context.MODE_PRIVATE)
                        .edit()
                        .putString("sortorder", sortOrder)
                        .apply();
                Log.d(TAG, "onCheckedChanged: " + sortOrder);
            }
        });
    }
    private void initSettings() {
        String sortBy = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("sortfield", "name");
        String sortOrder = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("sortorder", "ASC");

        RadioButton rbName = findViewById(R.id.radioName);
        RadioButton rbCity = findViewById(R.id.radioCity);
        RadioButton rbIsFavorite = findViewById(R.id.radioIsFavorite);
        RadioButton rbAscending = findViewById(R.id.radioAscending);
        RadioButton rbDescending = findViewById(R.id.radioDescending);

        rbName.setChecked(true);
        rbCity.setChecked(sortBy.equalsIgnoreCase("city"));
        rbIsFavorite.setChecked(sortBy.equalsIgnoreCase("isfavorite"));

        rbAscending.setChecked(true);
        rbDescending.setChecked(sortOrder.equalsIgnoreCase("DESC"));

    }
}