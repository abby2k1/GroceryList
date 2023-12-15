package net.abigailthompson.grocerylist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d(TAG, "onOptionsItemSelected: " + (String)item.getTitle());

        if (id == R.id.masterlist) {
            Log.i(TAG, "onOptionsItemSelected: " + R.id.masterlist);
            getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .edit()
                    .putString("listselected", "masterlist")
                    .apply();
            Context fromActivity = MainActivity.this;
            Class<?> destinationActivityClass = GroceryListActivity.class;
            Intent intent = new Intent(fromActivity, destinationActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fromActivity.startActivity(intent);
            return true;
        } else if (id == R.id.shoppinglist) {
            Log.i(TAG, "onOptionsItemSelected: " + R.id.shoppinglist);
            getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .edit()
                    .putString("listselected", "shoppinglist")
                    .apply();
            Context fromActivity = MainActivity.this;
            Class<?> destinationActivityClass = GroceryListActivity.class;
            Intent intent = new Intent(fromActivity, destinationActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fromActivity.startActivity(intent);
            return true;
        } else if (id == R.id.clearall) {
            Log.i(TAG, "onOptionsItemSelected: " + R.id.clearall);
            getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .edit()
                    .putString("menuaction", "clearall")
                    .apply();
            Context fromActivity = MainActivity.this;
            Class<?> destinationActivityClass = GroceryListActivity.class;
            Intent intent = new Intent(fromActivity, destinationActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fromActivity.startActivity(intent);
            return true;
        } else {
            Log.i(TAG, "onOptionsItemSelected: " + R.id.deletechecked);
            getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .edit()
                    .putString("menuaction", "deletechecked")
                    .apply();
            Context fromActivity = MainActivity.this;
            Class<?> destinationActivityClass = GroceryListActivity.class;
            Intent intent = new Intent(fromActivity, destinationActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fromActivity.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }*/
}