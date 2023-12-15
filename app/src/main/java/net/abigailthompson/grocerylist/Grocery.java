package net.abigailthompson.grocerylist;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

public class Grocery implements Serializable {
    private int id;
    private String name;
    private boolean isonshoppinglist;
    private boolean isincart;
    private double latitude;
    private double longitude;
    private Bitmap photo;
    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Grocery() {
        this.name = "";
        this.isonshoppinglist = false;
        this.isincart = false;
    }

    public String getMarkerText() {
        return name;
    }

    @Override
    public String toString() {
       /* return "Grocery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", rating=" + rating +
                ", isInCart=" + isInCart +
                ", imgId=" + imgId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';*/
        return String.valueOf(id) + '|' +
                name + '|' +
                isonshoppinglist  + '|' +
                isincart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsOnShoppingList() {
        return isonshoppinglist;
    }

    public void setIsOnShoppingList(boolean isonshoppinglist) {
        this.isonshoppinglist = isonshoppinglist;
    }

    public boolean getIsInCart() {
        return isincart;
    }

    public void setIsInCart(boolean isincart) {
        this.isincart = isincart;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Grocery(int id,
                   String name,
                   boolean isonshoppinglist,
                   boolean isincart,
                   float latitude,
                   float longitude) {
        this.id = id;
        this.name = name;
        this.isonshoppinglist = isonshoppinglist;
        this.isincart = isincart;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public void setControlText(int controlId, String value) {
        Log.d("Grocery", "setControlText: " + value);
        if (controlId == R.id.etName) {
            this.setName(value);
        }
    }
}
