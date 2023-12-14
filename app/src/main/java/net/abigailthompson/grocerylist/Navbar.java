package net.abigailthompson.grocerylist;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;

public class Navbar {
    public static final String TAG = "Navbar";

    public static void initListButton(Activity activity) {
        Log.d(TAG, "initListButton()");
        ImageButton ibList = (ImageButton)activity.findViewById(R.id.imageButtonList);
        setUpListenerEvent(ibList, activity, GroceryListActivity.class);
    }

    public static void initMapButton(Activity activity) {
        Log.d(TAG, "initMapButton()");
        ImageButton ibMap = (ImageButton)activity.findViewById(R.id.imageButtonMap);
        setUpListenerEvent(ibMap, activity, GroceryMapActivity.class);
    }

    public static void initSettingsButton(Activity activity) {
        Log.d(TAG, "initSettingsButton()");
        ImageButton ibSettings = (ImageButton)activity.findViewById(R.id.imageButtonSettings);
        setUpListenerEvent(ibSettings, activity, GrocerySettingsActivity.class);
    }

    private static void setUpListenerEvent(ImageButton imageButton, Activity fromActivity, Class<?> destinationActivityClass) {
        Log.d(TAG, "setUpListenerEvent()");
        imageButton.setEnabled(fromActivity.getClass() != destinationActivityClass);
        imageButton.setOnClickListener(view -> {
                Log.d(TAG, "onClick(): map");
                Intent intent = new Intent(fromActivity, destinationActivityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                fromActivity.startActivity(intent);
        });
    }
}
