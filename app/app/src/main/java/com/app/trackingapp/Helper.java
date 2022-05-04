package com.app.trackingapp;

import com.google.android.gms.maps.model.LatLng;

public class Helper {

    // Haversine Formula
    public double CalculationByDistance(LatLng aa, LatLng bb){
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(bb.latitude-aa.latitude);
        double dLon = toRadians(bb.longitude-aa.longitude);
        double srcLat = toRadians(aa.latitude);
        double destLat = toRadians(bb.latitude);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(srcLat) * Math.cos(destLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private double toRadians(double deg) {
        return deg * (Math.PI/180);
    }
}
