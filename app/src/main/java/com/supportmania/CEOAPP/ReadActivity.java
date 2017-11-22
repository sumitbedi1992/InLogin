package com.supportmania.CEOAPP;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ReadActivity extends AppCompatActivity {

    String ReadApi = Utility.CEO_API + "view_read.php";
    TextView bookname, readdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        bookname = (TextView) findViewById(R.id.bookname);
        readdetails = (TextView) findViewById(R.id.readdetails);


        ReadApi();

    }

    private void ReadApi() {
        //Getting values from edit texts

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonObjectRequest jsonReq = new JsonObjectRequest
                (Request.Method.POST, ReadApi, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responsedetails", response.toString());


                        String read = response.optString("read_name");
                        bookname.setText(read);
                        String details = response.optString("read_detail");
                        readdetails.setText(details);


                        pDialog.hide();
                    }
                }, new Response.ErrorListener()

                {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("response", error.toString());
                        VolleyLog.d("response", "Error: " + error.getMessage());

                        pDialog.hide();
                    }
                })

        {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                String json;
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    try {
                        json = new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    } catch (UnsupportedEncodingException e) {
                        return new VolleyError(e.getMessage());
                    }
                    return new VolleyError(json);
                }
                return volleyError;
            }


        };

        // Adding request to request queue
        AppController.getInstance().

                addToRequestQueue(jsonReq);
    }

}
