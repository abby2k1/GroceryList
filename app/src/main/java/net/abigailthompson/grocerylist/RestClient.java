package net.abigailthompson.grocerylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RestClient {
    public static final String TAG = "RestClient";

    public static void execGetRequest(String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetRequest: Start: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<Grocery> grocerys = new ArrayList<>();

        try{
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                        Log.d(TAG, "execGetRequest: response: " + response);
                        try {
                            JSONArray items = new JSONArray(response);
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                Grocery grocery = new Grocery();
                                grocery.setId(item.getInt("id"));
                                grocery.setName(item.getString("description"));
                                grocery.setIsInCart(item.getBoolean("isInCart"));

                                String jsonPhoto = item.getString("photo");

                                if(jsonPhoto != null)
                                {
                                    byte[] bytePhoto = null;
                                    bytePhoto = Base64.decode(jsonPhoto, Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                    grocery.setPhoto(bmp);
                                }

                                grocerys.add(grocery);
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, "execGetRequest: Exception: " + e.getMessage());
                        }

                        volleyCallback.onSuccess(grocerys);
                    },
                    error -> {
                        Log.e(TAG, "execGetRequest: Error: " + error.getMessage());
                        //volleyCallback.onSuccess(grocerys);
                    });
                    requestQueue.add(stringRequest);
        } catch (Exception e) {
            throw e;
            //Log.e(TAG, "execGetRequest: Exception: " + e.getMessage());
        }
    }

    public static void execDeleteRequest(Grocery grocery,
                                         String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        try {
            executeRequest(grocery, url, context, volleyCallback, Request.Method.DELETE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void execPutRequest(Grocery grocery,
                                      String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        try {
            executeRequest(grocery, url, context, volleyCallback, Request.Method.PUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void execPostRequest(Grocery grocery,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback)
    {
        try {
            executeRequest(grocery, url, context, volleyCallback, Request.Method.POST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void executeRequest(Grocery grocery,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback,
                                       int method)
    {
        Log.d(TAG, "executeRequest: " + method + ":" + url);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject object = new JSONObject();

            object.put("id", grocery.getId());
            object.put("name", grocery.getName());
            object.put("isOnShoppingList", grocery.getIsOnShoppingList());
            object.put("isInCart", grocery.getIsInCart());

            if(grocery.getPhoto() != null)
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createScaledBitmap(grocery.getPhoto(), 144, 144, false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String jsonPhoto = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                object.put("photo", jsonPhoto);
            }
            else
            {
                object.put("photo", null);
            }

            final String requestBody = object.toString();
            Log.d(TAG, "executeRequest: " + requestBody);

            JsonObjectRequest request = new JsonObjectRequest(method, url, object,
                    response -> {
                        Log.d(TAG, "onResponse: " + response);
                        // ALSO SUPER IMPORTANT
                        volleyCallback.onSuccess(new ArrayList<Grocery>());
                    }, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()))
            {
                @Override
                public byte[] getBody(){
                    Log.i(TAG, "getBody: " + object.toString());
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void execGetOneRequest(String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetOneRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<Grocery> grocerys = new ArrayList<Grocery>();
        Log.d(TAG, "execGetOneRequest: " + url);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    response -> {
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            JSONObject object = new JSONObject(response);
                            Grocery grocery = new Grocery();
                            grocery.setId(object.getInt("id"));
                            grocery.setName(object.getString("name"));
                            grocery.setIsOnShoppingList(object.getBoolean("isOnShoppingList"));
                            grocery.setIsInCart(object.getBoolean("isInCart"));

                            String jsonPhoto = object.getString("photo");

                            if(jsonPhoto != null)
                            {
                                byte[] bytePhoto = null;
                                bytePhoto = Base64.decode(jsonPhoto, Base64.DEFAULT);
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
                                grocery.setPhoto(bmp);
                            }

                            grocerys.add(grocery);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        volleyCallback.onSuccess(grocerys);
                    },
                    error -> {
                        Log.d(TAG, "onErrorResponse: ");
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    });

            // Important!!!
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            Log.d(TAG, "execGetOneRequest: Error" + e.getMessage());
        }
    }
}
