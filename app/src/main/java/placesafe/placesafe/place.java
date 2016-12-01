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
import com.android.volley.toolbox.JsonArrayRequest;
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

        createTabs();

    }

    public void  fillOpinions(){
        OpinionController.fillOpinions(getApplication(), this.getCurrentFocus(), app);
    }

    public void saveOpinion(){
        OpinionController.saveOpinion(getApplication(), this.getCurrentFocus());
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