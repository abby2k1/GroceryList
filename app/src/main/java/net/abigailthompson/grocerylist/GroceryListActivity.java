package net.abigailthompson.grocerylist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;

public class GroceryListActivity extends AppCompatActivity {
    public static final String TAG = "GroceryListActivity";
    public static final String TEAMSAPI = "https://fvtcdp.azurewebsites.net/api/GroceryList/";
    public static final String FILENAME = "grocerys.txt";
    ArrayList<Grocery> grocerys;
    RecyclerView groceryList;
    GroceryAdapter groceryAdapter;
    GroceryAdapterMaster groceryAdapterMaster;

    // String Sorters
    Comparator<Grocery> nameComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());

    // Boolean Sorter
    Comparator<Grocery> isInCartComparator = (c1, c2) -> Boolean.compare(c2.getIsInCart(), c1.getIsInCart());

    private View.OnClickListener onClickListener = view -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
        int position = viewHolder.getAdapterPosition();
        int id = grocerys.get(position).getId();
        Grocery grocery = grocerys.get(position);
        Log.d(TAG, "onClick: " + grocery.getName());
        Intent intent = new Intent(GroceryListActivity.this, GroceryEditActivity.class);
        intent.putExtra("groceryid", grocery.getId());
        startActivity(intent);
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (compoundButton, b) -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) compoundButton.getTag();
        int position = viewHolder.getAdapterPosition();
        int id = grocerys.get(position).getId();
        Grocery grocery = grocerys.get(position);
        id = grocery.getId();

        String listMode = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("listselected", "shoppinglist");

        if (!listMode.equals("masterlist")) {
            grocery.setIsInCart(b);
            Log.d(TAG, "onCheckedChanged: " + grocery.getName());

            // grocery.setIsInCart(b);

            RestClient.execPutRequest(grocery, TEAMSAPI + id, this,
                    VolleyCallback -> {
                        Log.d(TAG, "onSuccess: " + VolleyCallback);
                        //grocerys.set(position, VolleyCallback);
                        groceryAdapter.notifyDataSetChanged();
                        RebindScreen();
                    });
        } else if (listMode.equals("masterlist")) {
            grocerys.get(position).setIsOnShoppingList(b);
            Log.d(TAG, "onCheckedChanged: " + grocerys.get(position).getName());

            // grocery.setIsInCart(b);

            RestClient.execPutRequest(grocerys.get(position), TEAMSAPI + id, this,
                    VolleyCallback -> {
                        Log.d(TAG, "onSuccess: " + VolleyCallback);
                        //grocerys.set(position, VolleyCallback);
                        groceryAdapterMaster.notifyDataSetChanged();
                        RebindScreen();
                    });
        }

    };

    @Override
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
            Context fromActivity = GroceryListActivity.this;
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
            Context fromActivity = GroceryListActivity.this;
            Class<?> destinationActivityClass = GroceryListActivity.class;
            Intent intent = new Intent(fromActivity, destinationActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fromActivity.startActivity(intent);
            return true;
        } else if (id == R.id.clearall) {
            Log.i(TAG, "onOptionsItemSelected: " + R.id.clearall);
            String listMode = getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .getString("listselected", "shoppinglist");
            for ( Grocery grocery : grocerys ) {
                if (listMode.equals("masterlist"))
                {
                    grocery.setIsOnShoppingList(false);
                }
                grocery.setIsInCart(false);
                RestClient.execPutRequest(grocery, TEAMSAPI + grocery.getId(), this,
                        VolleyCallback -> {
                            Log.d(TAG, "onSuccess: " + VolleyCallback);
                            //grocerys.set(position, VolleyCallback);
                            if (listMode.equals("masterlist"))
                            {
                                groceryAdapterMaster.notifyDataSetChanged();
                            }
                            else if (!listMode.equals("masterlist"))
                            {
                                groceryAdapter.notifyDataSetChanged();
                            }
                            RebindScreen();
                        });
            }
        } else {
            Log.i(TAG, "onOptionsItemSelected: " + R.id.deletechecked);
            String listMode = getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .getString("listselected", "shoppinglist");
            for ( Grocery grocery : grocerys ) {
                if (!listMode.equals("masterlist"))
                {
                    if (!grocery.getIsInCart()) continue;
                    grocery.setIsOnShoppingList(false);
                    grocery.setIsInCart(false);
                    RestClient.execPutRequest(grocery, TEAMSAPI + grocery.getId(), this,
                            VolleyCallback -> {
                                Log.d(TAG, "onSuccess: " + VolleyCallback);
                                //grocerys.set(position, VolleyCallback);
                                groceryAdapter.notifyDataSetChanged();
                                RebindScreen();
                            });
                }
                else if (listMode.equals("masterlist"))
                {
                    if (!grocery.getIsOnShoppingList()) continue;
                    RestClient.execDeleteRequest(grocery, TEAMSAPI + grocery.getId(), this,
                            VolleyCallback -> {
                                Log.d(TAG, "onSuccess: " + VolleyCallback);
                                //grocerys.set(position, VolleyCallback);
                                groceryAdapterMaster.notifyDataSetChanged();
                                RebindScreen();
                            });
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        grocerys = new ArrayList<Grocery>();

        /*
        grocerys = readFromTextFile(this);

        if(grocerys.size() == 0)
        {
            Log.d(TAG, "onCreate: ");
            grocerys.add(new Grocery(1, "Packers", "Green Bay", "9203551234", 4.5f, true, R.drawable.packers, 0.0,0.0));
            grocerys.add(new Grocery(2, "Vikings", "Minneapolis", "11111111111", 2.5f, false, R.drawable.vikings, 0.0,0.0));
            grocerys.add(new Grocery(3, "Lions", "Detroit", "2222222222", 1.3f, true, R.drawable.lions, 0.0,0.0));
            grocerys.add(new Grocery(4, "Bears", "Chicago", "3333333333", 2.0f, false, R.drawable.bears, 0.0,0.0));
            FileIO.writeFile(FILENAME,
                    this,
                    createGroceryArray(grocerys));
        }*/

        // init buttons
        Navbar.initListButton(this);
        Navbar.initMapButton(this);
        Navbar.initSettingsButton(this);

        this.setTitle(getString(R.string.grocery_list));

        initDeleteButton();
        initAddGroceryButton();
        //initDatabase();
        readFromAPI();

        // Get the battery life
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                double levelScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int batteryPercent = (int)Math.floor(batteryLevel / levelScale * 100);

                TextView txtBatteryLevel = findViewById(R.id.txtBatteryLevel);
                txtBatteryLevel.setText(batteryPercent + "%");
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, filter);

        Log.d(TAG, "onCreate: End");

    }

    private void readFromAPI()
    {
        String listMode = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("listselected", "shoppinglist");
        if (!listMode.equals("masterlist"))
        {
            this.setTitle(getString(R.string.shopping_list));
            try {
                Log.d(TAG, "readFromAPI: Start");

                RestClient.execGetShoppingRequest(TEAMSAPI + "abigailt", this,
                        VolleyCallback -> {
                            Log.d(TAG, "onSuccess: " + VolleyCallback.size());
                            grocerys = VolleyCallback;
                            for (Grocery t : grocerys) {
                                Log.d(TAG, "onSuccess: " + t.getName());
                            }

                            RebindScreen();
                            //groceryAdapter.notifyDataSetChanged();
                        });
                RebindScreen();
            }
            catch(Exception e)
            {
                Log.e(TAG, "readFromAPI: " + e.getMessage());
            }
        }
        else if (listMode.equals("masterlist"))
        {
            this.setTitle(getString(R.string.master_list));
            try {
                Log.d(TAG, "readFromAPI: Start");

                RestClient.execGetRequest(TEAMSAPI + "abigailt", this,
                        VolleyCallback -> {
                            Log.d(TAG, "onSuccess: " + VolleyCallback.size());
                            grocerys = VolleyCallback;
                            for (Grocery t : grocerys) {
                                Log.d(TAG, "onSuccess: " + t.getName());

                            }

                            RebindScreen();
                            //groceryAdapter.notifyDataSetChanged();
                        });
                RebindScreen();
            }
            catch(Exception e)
            {
                Log.e(TAG, "readFromAPI: " + e.getMessage());
            }
        }
    }

    private void initDatabase() {
        GroceryDataSource ds = new GroceryDataSource(this);
        ds.open();
        grocerys = ds.get();
        if(grocerys.size() == 0)
        {
            ds.refreshData();
            grocerys = ds.get();
        }
        ds.close();
        Log.d(TAG, "initDatabase: Grocerys: " + grocerys.size());
    }

    private void initDeleteButton() {
        Switch switchDelete = findViewById(R.id.switchDelete);

        switchDelete.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.d(TAG, "onCheckedChanged: " + b);

            String listMode = getSharedPreferences("groceryspreferences",
                    Context.MODE_PRIVATE)
                    .getString("listselected", "shoppinglist");
            if (!listMode.equals("masterlist"))
            {
                groceryAdapter.setDelete(b);
                groceryAdapter.notifyDataSetChanged();
            }
            else if (listMode.equals("masterlist"))
            {
                groceryAdapterMaster.setDelete(b);
                groceryAdapterMaster.notifyDataSetChanged();
            }
        });
    }

    private void initAddGroceryButton() {
        Button btnAddGrocery = findViewById(R.id.buttonAddGrocery);
        btnAddGrocery.setOnClickListener(view -> {
            Intent intent = new Intent(GroceryListActivity.this, GroceryEditActivity.class);
            intent.putExtra("groceryid", -1);
            startActivity(intent);
        });
        /* btnAddGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroceryListActivity.this, GroceryEditActivity.class);
                intent.putExtra("groceryid", -1);
                startActivity(intent);
            }
        }); */
    }

    public static String[] createGroceryArray(ArrayList<Grocery> grocerys) {
        String[] groceryData = new String[grocerys.size()];
        for(Integer count = 0; count < grocerys.size(); count++)
        {
            Log.d(TAG, "createGroceryArray: " + grocerys.get(count).toString());
            groceryData[count] = grocerys.get(count).toString();
        }
        return groceryData;
    }

    public static ArrayList<Grocery> readFromTextFile(AppCompatActivity activity) {
        Log.d(TAG, "readFromTextFile: Start");
        ArrayList<String> strData = FileIO.readFile(FILENAME, activity);
        Log.d(TAG, "readFromTextFile: After read");
        ArrayList<Grocery> grocerys = new ArrayList<Grocery>();

        /*for(String s : strData)
        {
            Log.d(TAG, "readFromTextFile: " + s);
            String[] data = s.split("\\|");
            grocerys.add(new Grocery(
                    Integer.parseInt(data[0]),
                    data[1],
                    data[2],
                    data[3],
                    Float.parseFloat(data[4]),
                    Boolean.parseBoolean(data[5]),
                    Integer.parseInt(data[6]),
                    Double.parseDouble(data[7]),
                    Double.parseDouble(data[8])
            ));
        }*/
        Log.d(TAG, "readFromTextFile: " + grocerys.size());
        return grocerys;
    }

    public void RebindScreen()
    {
        Log.d(TAG, "RebindScreen: Start: " + grocerys.size());
        sortGrocerys();
        groceryList = findViewById(R.id.rvGrocerys);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        groceryList.setLayoutManager(layoutManager);
        Log.d(TAG, "RebindScreen: " + "why the heck is no adapter attached outside if");

        String listMode = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("listselected", "shoppinglist");
        if (!listMode.equals("masterlist"))
        {
            groceryAdapter = new GroceryAdapter(grocerys, this);
            groceryAdapter.setOnItemClickListener(onClickListener);
            groceryAdapter.setOnItemCheckedChangeListener(onCheckedChangeListener);
            Log.d(TAG, "RebindScreen: " + "why the heck is no adapter attached");
            groceryList.setAdapter(groceryAdapter);

            groceryAdapter.notifyDataSetChanged();
        }
        else if (listMode.equals("masterlist"))
        {
            groceryAdapterMaster = new GroceryAdapterMaster(grocerys, this);
            groceryAdapterMaster.setOnItemClickListener(onClickListener);
            groceryAdapterMaster.setOnItemCheckedChangeListener(onCheckedChangeListener);
            groceryList.setAdapter(groceryAdapterMaster);

            groceryAdapterMaster.notifyDataSetChanged();
        }


        Log.d(TAG, "RebindScreen: End: "+ grocerys.size());
    }

    private void sortGrocerys()
    {
        String sortBy = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("sortfield", "name");
        String sortOrder = getSharedPreferences("groceryspreferences",
                Context.MODE_PRIVATE)
                .getString("sortorder", "ASC");

        Log.d(TAG, "sortGrocerys: " + sortBy + ":" + sortOrder);

        if (sortOrder.equals("ASC"))
        {
            if(sortBy.equals("name")) grocerys.sort(nameComparator);
            if(sortBy.equals("isInCart")) grocerys.sort(isInCartComparator);
        }
        else
        {
            if(sortBy.equals("name")) grocerys.sort(nameComparator.reversed());
            if(sortBy.equals("isInCart")) grocerys.sort(isInCartComparator.reversed());
        }
    }
}