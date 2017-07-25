package com.linkloving.dyh08.utils.sportUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Daniel.Xu on 2016/12/30.
 */

public class LocationUtils  {
    public static  Location getLocation(Context context){
        //获取地理位置管理器
        LocationManager    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
       String locationProvider = null ;
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            return null ;
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        return location ;
    }
}
