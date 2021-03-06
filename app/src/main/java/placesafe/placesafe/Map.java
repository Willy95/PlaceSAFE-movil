package placesafe.placesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Wihar on 26/11/2016.
 */

public class Map extends Activity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    GoogleMap mapp;
    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_mapacompleto);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            mapp = map;
            LatLng torreon = new LatLng(25.5400882, -103.4236984);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(torreon, 11));

            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(true);
            map.getUiSettings().setRotateGesturesEnabled(true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
            else{
//            Toast.makeText(this, "No supported", Toast.LENGTH_LONG).show();
            }

            RequestVolley req = RequestVolley.getInstance(getApplicationContext());
            req.requestString("GET", "/getPlaces", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray obj = new JSONArray(response);
                        for (int i = 0; i < obj.length(); i++){
                            String lat = obj.getJSONObject(i).getString("latitud");
                            String lng = obj.getJSONObject(i).getString("longitud");
                            String title = obj.getJSONObject(i).getString("address");
                            LatLng place = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                            makePoint(mapp,place,title,"Ver Detalles");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Map.this, "No fue posible mostrar los lugares recomendados.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }catch(Error e){
            Intent intent = new Intent(getApplicationContext(), Map.class);
            Toast.makeText(this, "Recargando!", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    public void makePoint(GoogleMap map, LatLng pos, String tit, String snip) {
        map.addMarker(new MarkerOptions()
                .position(pos)
                .title(tit)
                .snippet(snip));
        map.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getApplicationContext(), place.class);
        Bundle bundle = new Bundle();
        bundle.putString("lat",String.valueOf(marker.getPosition().latitude));
        bundle.putString("lng",String.valueOf(marker.getPosition().longitude));
        bundle.putString("titlePlace",marker.getTitle());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }
}
