package ceo.app_knit.com.ceo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProfileActivity extends AppCompatActivity {

    LoginSessoion session;
    String id;

    String ProfileApi = Utility.CEO_API + "connect_reference.php";
    int status;
    TextView name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_read);

        session = new LoginSessoion(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        id = user.get(LoginSessoion.KEY_NAME);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        Connect();

    }

    private void Connect() {
        //Getting values from edit texts


        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("user_id", id);


        Log.e("postparams", postParam.toString());


        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ProfileApi, new JSONObject(postParam),
                new Response.Listener<JSONObject>()


                {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());


                        status = response.optInt("status");

                        if (status == 1) {


                            String firstname = response.optString("first_name");
                            String lastname = response.optString("last_name");
                            String email = response.optString("email");

                            name.setText(firstname + "" + lastname);
                            email = response.optString(email);


                        }


                        pDialog.hide();

                    }
                }, new Response.ErrorListener()


        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", error.toString());
                pDialog.hide();
                VolleyLog.d("response", "Error: " + error.getMessage());
            }
        })

        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
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
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        return new VolleyError(e.getMessage());

                    }
                    return new VolleyError(json);
                }


                return volleyError;
            }

        };


        // Adding request to request queue
        AppController.getInstance().

                addToRequestQueue(jsonObjReq);
    }

}
