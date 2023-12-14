package net.abigailthompson.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class GroceryDataSource {
    SQLiteDatabase database;
    GroceryDBHelper dbHelper;
    public static final String TAG = "GroceryDataSource";

    public GroceryDataSource(Context context){
        dbHelper = new GroceryDBHelper(context, GroceryDBHelper.DATABASE_NAME, null, GroceryDBHelper.DATABASE_VERSION);
    }

    public void open() throws SQLException{
        open(false);
    }

    public void open(boolean refresh) throws SQLException{
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "open: " + database.isOpen());
        if(refresh) refreshData();
    }

    public void close()
    {
        dbHelper.close();
    }

    public void refreshData() {
        Log.d(TAG, "refreshData: ");
        ArrayList<Grocery> grocerys = new ArrayList<Grocery>();

        int results = 0;
        for(Grocery grocery: grocerys)
        {
            results += insert(grocery);
        }
        Log.d(TAG, "refreshData: Inserted " + results + " rows...");
    }

    public Grocery get(int id)
    {
        Log.d(TAG, "get: " + id);

        ArrayList<Grocery> grocerys = new ArrayList<Grocery>();
        Grocery grocery = null;

        try{
            String query = "Select * from tblGrocery where _id = " + id;
            Cursor cursor = database.rawQuery(query, null);

            //Cursor cursor = database.query("tblGrocery",null, null, null, null, null, null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                grocery = new Grocery();
                grocery.setId(cursor.getInt(0));
                grocery.setName(cursor.getString(1));

                Boolean shoppingList = (cursor.getInt(2) == 1);
                grocery.setIsOnShoppingList(shoppingList);

                Boolean cart = (cursor.getInt(3) == 1);
                grocery.setIsInCart(cart);

                Log.d(TAG, "get: " + grocery.toString());

                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return grocery;
    }
    public ArrayList<Grocery> get()
    {
        ArrayList<Grocery> grocerys = new ArrayList<Grocery>();
        Log.d(TAG, "get: Start");

        try{
            String query = "Select * from tblGrocery";
            Cursor cursor = database.rawQuery(query, null);

            Grocery grocery;
            cursor.moveToFirst();

            while(!cursor.isAfterLast())
            {
                grocery = new Grocery();
                grocery.setId(cursor.getInt(0));
                grocery.setName(cursor.getString(1));

                Boolean shoppingList = (cursor.getInt(2) == 1);
                grocery.setIsOnShoppingList(shoppingList);

                Boolean cart = (cursor.getInt(3) == 1);
                grocery.setIsInCart(cart);

                grocerys.add(grocery);
                Log.d(TAG, "get: " + grocery.toString());

                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return grocerys;
    }

    public int deleteAll()
    {
        try{
            return database.delete("tblGrocery", null, null);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(Grocery grocery)
    {
        try{
            int id = grocery.getId();
            if(id < 1)
                return 0;
            return delete(id);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(int id)
    {
        try{
            return database.delete("tblGrocery", "_id = " + id, null);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getNewId()
    {
        int lastId;
        try{
            String query = "SELECT max(_id) from tblGrocery";
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            lastId = cursor.getInt(0) + 1;
            cursor.close();
        }
        catch(Exception e)
        {
            lastId = -1;
        }
        return lastId;

    }

    public int update(Grocery grocery)
    {
        Log.d(TAG, "update: Start" + grocery.toString());
        int rowsaffected = 0;

        if(grocery.getId() < 1)
            return insert(grocery);

        try{
            ContentValues values = new ContentValues();
            values.put("name", grocery.getName());
            values.put("isOnShoppingList", grocery.getIsOnShoppingList());
            values.put("isInCart", grocery.getIsInCart());

            String where = "_id = " + grocery.getId();

            rowsaffected = (int)database.update("tblGrocery", values, where, null);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsaffected;
    }
    public int insert(Grocery grocery)
    {
        Log.d(TAG, "insert: Start");
        int rowsaffected = 0;

        try{
            ContentValues values = new ContentValues();
            values.put("name", grocery.getName());
            values.put("isOnShoppingList", grocery.getIsOnShoppingList());
            values.put("isInCart", grocery.getIsInCart());

            rowsaffected = (int)database.insert("tblGrocery", null, values);
        }
        catch(Exception e)
        {
            Log.d(TAG, "get: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsaffected;
    }
}
