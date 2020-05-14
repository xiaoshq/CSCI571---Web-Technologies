package com.shuqixiao.newsapp.Fragment.Home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shuqixiao.newsapp.R;

import android.Manifest;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FragmentHome extends Fragment {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locationManager;
    private String locationProvider;
    private String cityName;
    private String stateName;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean state = checkLocationPermission();
        if (state) {
            try {
                getLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView cityTV = (TextView) view.findViewById(R.id.weather_city);
        TextView stateTV = (TextView) view.findViewById(R.id.weather_state);
        cityTV.setText(cityName);
        stateTV.setText(stateName);
        getWeather(cityName);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_home);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NewsFragment newsFragment = (NewsFragment) getChildFragmentManager().findFragmentById(R.id.news_fragment);
                newsFragment.getNewsListData();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        return view;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("About Weather Card")
                        .setMessage("Sorry, we don't have the permission to get your location. The weather card will be blank!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void getLocation() throws IOException {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (location == null || l.getAccuracy() < location.getAccuracy()) {
                location = l;
                locationProvider = provider;
            }
        }

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // get the city name and state name
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                cityName = addresses.get(0).getLocality();
                stateName = addresses.get(0).getAdminArea();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //This is what you need:
            Toast.makeText(getContext(), "Location is null!", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onLocationChanged(Location location) {
            location.getAccuracy();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }
    };

    private void getWeather(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + getString(R.string.openWeatherKEY);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject weather = (JSONObject) jsonObject.getJSONArray("weather").get(0);
                    String summary = weather.getString("main");
                    JSONObject main = (JSONObject) jsonObject.get("main");
                    double temp = Double.parseDouble(main.getString("temp"));
                    String temperature = Math.round(temp) + getString(R.string.scaleOfTemperature);

                    TextView temperatureTV = (TextView) getView().findViewById(R.id.temperature);
                    temperatureTV.setText(temperature);
                    TextView summaryTV = (TextView) getView().findViewById(R.id.weatherSummary);
                    summaryTV.setText(summary);
                    ImageView background = (ImageView) getView().findViewById(R.id.weather_background);
                    switch (summary) {
                        case "Clouds":
                            background.setImageResource(R.drawable.cloudy_weather);
                            break;
                        case "Clear":
                            background.setImageResource(R.drawable.clear_weather);
                            break;
                        case "Snow":
                            background.setImageResource(R.drawable.snowy_weather);
                            break;
                        case "Rain":
                        case "Drizzle":
                            background.setImageResource(R.drawable.rainy_weather);
                            break;
                        case "Thunderstorm":
                            background.setImageResource(R.drawable.thunder_weather);
                            break;
                        default:
                            background.setImageResource(R.drawable.sunny_weather);
                    }

                } catch (JSONException e) {
                    // TODO: handle error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // TODO: handle error
                volleyError.printStackTrace();
            }
        });

        // 将创建的请求添加到请求队列中
        requestQueue.add(jsonObjectRequest);
    }
}
