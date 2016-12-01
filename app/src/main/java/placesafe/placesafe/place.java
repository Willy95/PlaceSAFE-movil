package placesafe.placesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gerson on 29/11/16.
 */
public class place extends Activity {

    ImageButton btnBack;
    Button btnComment;
    Context app = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_lugar);

        fillOpinions();
        fillReactions();
        fillImagePlace();
        TextView title = (TextView) findViewById(R.id.titlePlace);
        title.setText(getIntent().getExtras().getString("titlePlace"));
        btnBack = (ImageButton) findViewById(R.id.back);
        btnComment = (Button) findViewById(R.id.send_comment);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOpinion();
            }
        });

        RequestVolley request = RequestVolley.getInstance(this);
        NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.imageRequest);
        request.requestImage("https://lh3.googleusercontent.com/fPZtta7U9eWIQoi-6cxgz3toIHEWopWGnaIrHyJ--fzidvDV3lPDX2g2Aldi-5YreL8=w300", networkImageView);
        createTabs();

    }

    public void  fillOpinions(){
        RequestVolley req = RequestVolley.getInstance(getApplicationContext());
        HashMap<String,String> data = new HashMap<>();
        Intent intentRecived = getIntent();
        Bundle bundleRecived = intentRecived.getExtras();
        data.put("lat",bundleRecived.getString("lat"));
        data.put("lng", bundleRecived.getString("lng"));
        req.requestString("POST", "/opinions", new Response.Listener<String>() {
            @Override
            public void onResponse(String responser) {

                try {
                    List<Opinion> opinions = new ArrayList<>();
                    RecyclerView rv = (RecyclerView) findViewById(R.id.opinionesList);
                    LinearLayoutManager lym = new LinearLayoutManager(app);
                    rv.setLayoutManager(lym);
                    JSONArray response = new JSONArray(responser);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jresponse = response.getJSONObject(i);
                        String nickname = jresponse.getString("nickname");
                        String opinionText = jresponse.getString("opinionText");
                        opinions.add(new Opinion(nickname, opinionText));
                    }

                    AdapterOpinions adapterOpinions = new AdapterOpinions(opinions);
                    rv.setAdapter(adapterOpinions);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, data);
    }

    public void fillImagePlace(){
        RequestVolley req = RequestVolley.getInstance(getApplicationContext());
        HashMap<String,String> data = new HashMap<>();
        Intent intentRecived = getIntent();
        Bundle bundleRecived = intentRecived.getExtras();
        data.put("lat",bundleRecived.getString("lat"));
        data.put("lng", bundleRecived.getString("lng"));
        req.requestString("GET", "/getImagePlace", new Response.Listener<String>() {
            @Override
            public void onResponse(String responser) {
                    NetworkImageView imageRequest = (NetworkImageView)findViewById(R.id.imagePlace);

                    RequestVolley request = RequestVolley.getInstance(getApplicationContext());
                    request.requestImage("http://placesafe.curiosity.com.mx/images/places/"+responser, imageRequest);


            }
        },data);
    }

    public void  fillReactions(){
        RequestVolley req = RequestVolley.getInstance(getApplicationContext());
        req.requestString("GET", "/getReactions", new Response.Listener<String>() {
            @Override
            public void onResponse(String responser) {

                try {
                    List<Reaction> reactions = new ArrayList<>();
                    RecyclerView rv = (RecyclerView) findViewById(R.id.iconReactions);
                    LinearLayoutManager lym = (new LinearLayoutManager(app, LinearLayoutManager.HORIZONTAL, false));
                    rv.setLayoutManager(lym);

                    String uri = "http://placesafe.curiosity.com.mx/images/reactions/";
                    JSONArray response = new JSONArray(responser);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jresponse = response.getJSONObject(i);
                        String name = jresponse.getString("name");
                        String image = jresponse.getString("image");
                        reactions.add(new Reaction(name, uri+image,getIntent().getExtras()));
                    }

                    AdapterReaction adapterReaction = new AdapterReaction(reactions);
                    rv.setAdapter(adapterReaction);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveOpinion(){
        HashMap<String,String> data = new HashMap<>();
        Intent intentRecived = getIntent();
        Bundle bundleRecived = intentRecived.getExtras();
        TextView opinion = (TextView) findViewById(R.id.set_comment);
        data.put("opinionText", String.valueOf(opinion.getText()));
        data.put("lat",bundleRecived.getString("lat"));
        data.put("lng", bundleRecived.getString("lng"));
        data.put("nickname",usuarioSqlLiteHelper.getInstance(getApplicationContext(),"DBUsuarios",null,1).getUserLog(this));
        OpinionController.saveOpinion(getApplication(), data);
        opinion.setText("");
    }

    public void createTabs(){
        Resources res = getResources();

        TabHost tabs = (TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("comentarios");
        spec.setContent(R.id.comentarios);
        spec.setIndicator("Opiniones", res.getDrawable(android.R.drawable.ic_dialog_dialer));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("comentar");
        spec.setContent(R.id.comentar);
        spec.setIndicator("Opinar", res.getDrawable(android.R.drawable.ic_dialog_dialer));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("reaccionar");
        spec.setContent(R.id.reaccionar);
        spec.setIndicator("Reaccionar", res.getDrawable(android.R.drawable.ic_menu_edit));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    protected void back(){
        final Intent intentMap = new Intent(getApplicationContext(), Map.class);
        startActivity(intentMap);
    }


}