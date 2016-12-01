package placesafe.placesafe;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gerson on 30/11/16.
 */
public class OpinionController {
    public static void saveOpinion(final Application app,View v){
        RequestVolley req = RequestVolley.getInstance(app.getApplicationContext());
        HashMap<String, String> data = new HashMap<>();
        TextView opinion = (TextView) v.findViewById(R.id.set_comment);
        data.put("opinionText", String.valueOf(opinion.getText()));
        req.requestString("POST", "/saveOpinion", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("OK")) {
                    Toast.makeText(app.getApplicationContext(), "Gracias por tu opinion", Toast.LENGTH_SHORT).show();

                }
            }
        }, data);

        opinion.setText("");
    }
    public static void  fillOpinions(Application app,final View v,final Context context){

        RequestVolley req = RequestVolley.getInstance(app.getApplicationContext());

        req.requestString("POST", "/opinions", new Response.Listener<String>() {
            @Override
            public void onResponse(String responser) {

                try {
                    List<Opinion> opinions = new ArrayList<>();
                    RecyclerView rv = (RecyclerView) v.findViewById(R.id.opinionesList);
                    LinearLayoutManager lym = new LinearLayoutManager(context);
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
        });


    }
}
