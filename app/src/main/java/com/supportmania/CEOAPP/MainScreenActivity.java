package com.supportmania.CEOAPP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.linkedin.platform.LISessionManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainScreenActivity extends AppCompatActivity {

    CircleImageView Read, Watch, conect;
    Button Logout;
    LoginSessoion loginSessoion;
    String VideoAPi = Utility.CEO_API + "view_video.php";
    int Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        loginSessoion = new LoginSessoion(getApplicationContext());
        Read = (CircleImageView) findViewById(R.id.Read);
        Logout = (Button) findViewById(R.id.Logout);
        Watch = (CircleImageView) findViewById(R.id.Watch);
        conect = (CircleImageView) findViewById(R.id.conect);


        Read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreenActivity.this, ReadActivity.class);
                startActivity(intent);
            }
        });
        Watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VideoApi();

            }
        });
        conect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreenActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logOut();
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                loginSessoion.logoutUser();
                Intent intent = new Intent(MainScreenActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }


    private void VideoApi() {
        //Getting values from edit texts

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonObjectRequest jsonReq = new JsonObjectRequest
                (Request.Method.POST, VideoAPi, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responsedetails", response.toString());


                        String videotitle = response.optString("video_name");

                        String details = response.optString("read_detail");

                        String example = response.optString("video_link");

                        String Videoid = getYouTubeId(example);

                        Status = response.optInt("status");

                        if (Status == 1) {
                            Intent intent = new Intent(MainScreenActivity.this, CustomPlayerControlActivity.class);
                            intent.putExtra("Videoid", Videoid);
                            intent.putExtra("Videoname", videotitle);
                            startActivity(intent);
                        }


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

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }


}
