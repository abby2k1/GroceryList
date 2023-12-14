package net.abigailthompson.grocerylist;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

public class Grocery implements Serializable {
    private int id;
    private String name;
    private String city;
    private String cellphone;
    private float rating;
    private boolean isfavorite;
    //private int imgId;

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    private Bitmap photo;

    public Grocery() {
        this.name = "";
        this.city = "";
        this.cellphone = "";
        this.rating = 0;
        this.isfavorite = false;
        //this.imgId = R.drawable.photoicon;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public String getMarkerText() {
        return name + ", " + city + " : " + rating;
    }

    @Override
    public String toString() {
       /* return "Grocery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", rating=" + rating +
                ", isfavorite=" + isfavorite +
                ", imgId=" + imgId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';*/
        return String.valueOf(id) + '|' +
                name + '|' +
                city + '|' +
                cellphone + '|' +
                rating  + '|' +
                isfavorite  + '|' +
                //imgId  + '|' +
                latitude  + '|' +
                longitude;
    }

    private Double latitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean getIsfavorite() {
        return isfavorite;
    }

    public void setIsfavorite(boolean isfavorite) {
        this.isfavorite = isfavorite;
    }

    /*public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }*/

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    private Double longitude;

    public Grocery(int id,
                   String name,
                   String city,
                   String cellphone,
                   float rating,
                   boolean isfavorite,
                   int imgId,
                   Double latitude,
                   Double longitude) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.cellphone = cellphone;
        this.rating = rating;
        this.isfavorite = isfavorite;
        //this.imgId = imgId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setControlText(int controlId, String value) {
        Log.d("Grocery", "setControlText: " + value);
        if (controlId == R.id.etName) {
            this.setName(value);
        } else if (controlId == R.id.etCity) {
            this.setCity(value);
        } else if (controlId == R.id.editCell) {
            this.setCellphone(value);
        }
        /*
        switch (controlId)
        {
            case R.id.etName:
                Log.d("Grocery", "setControlText: " + value );
                this.setName(value);
                break;
            case R.id.etCity:
                this.setCity(value);
                break;
            case R.id.editCell:
                this.setCellphone(value);
                break;
        }
        */
    }
}
