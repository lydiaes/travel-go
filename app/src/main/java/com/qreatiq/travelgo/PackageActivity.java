package com.qreatiq.travelgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qreatiq.travelgo.adapters.FindTourAdapter;
import com.qreatiq.travelgo.adapters.PackageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PackageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PackageAdapter adapter;
    ArrayList<JSONObject> array=new ArrayList<>();

    RequestQueue queue;

    SharedPreferences user;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        adapter=new PackageAdapter(array);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        user = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        userID = user.getString("user_id", "Not Found");

        queue = Volley.newRequestQueue(this);

        getData();
    }

    private void getData(){
        String url = "https://3gomedia.com/travel-go/api/getPackageUser.php?id="+userID;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("packageTour", response.toString());
                    for(int x=0;x<response.getJSONArray("package").length();x++) {
                        JSONObject package1=response.getJSONArray("package").getJSONObject(x);
                        JSONObject json=new JSONObject();
                        json.put("location",package1.getString("location"));
                        json.put("date",package1.getString("start_date")+" - "+package1.getString("end_date"));
                        array.add(json);
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
