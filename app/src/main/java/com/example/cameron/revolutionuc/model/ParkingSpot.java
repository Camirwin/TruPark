package com.example.cameron.revolutionuc.model;

import android.location.Location;

/**
 * Created by Cameron on 11/15/2014.
 */
public class ParkingSpot {

    private String DeviceId;
    private String Address;
    private Double Lat;
    private Double Lng;

    public ParkingSpot(String address, Double lat, Double lng) {
        Address = address;
        Lat = lat;
        Lng = lng;
    }

    public ParkingSpot(String deviceId, String address, Double lat, Double lng) {
        DeviceId = deviceId;
        Address = address;
        Lat = lat;
        Lng = lng;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }

    public Double getDistance() {
        Location location = new Location("");
        location.setLatitude(39.1320);
        location.setLongitude(-84.5155);
        return Double.valueOf(location.distanceTo(getLocation())) / 1609.34;
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(getLat());
        location.setLongitude(getLng());
        return location;
    }

}
