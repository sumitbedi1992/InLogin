package ceo.app_knit.com.ceo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    LoginButton Login;

    String LoginApi = Utility.CEO_API + "facebook_login.php";
    //For facebook


    //For facebook
    private CallbackManager callbackManager;

    LoginButton loginButton;
    URL Profilepic;
    String id;
    String firstnaem;
    String lastname;
    String email;
    String profile;
    String gender;
    int status;
    LoginSessoion usersession;
    public static final String PACKAGE = "ceo.app_knit.com.ceo";
    private static final String TAG = MainActivity.class.getSimpleName();
    boolean fb = false;
    Boolean linkdin = false;
    Button LdLoginButtton;
    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:(email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        usersession = new LoginSessoion(getApplicationContext());
        if (usersession.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
            startActivity(intent);

        } else {

        }


        loginButton = findViewById(R.id.Login);


        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Bundle fbdata = getFacebookdata(object);

                        Log.v("saradata", fbdata.toString());

                        firstnaem = fbdata.getString("first_name");
                        lastname = fbdata.getString("last_name");
                        email = fbdata.getString("email");
                        profile = fbdata.getString("profile_pic");
                        gender = fbdata.getString("gender");
                        fb = true;

                        Login();


                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private Bundle getFacebookdata(JSONObject object) {


        Bundle bundle = new Bundle();
        id = object.optString("id");
        try {
            Profilepic = new URL("https://graph.facebook.com/" + id + "/picture?width=100&height=100");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        bundle.putString("idFacebook", id);

        if (object.has("first_name")) {
            try {
                bundle.putString("first_name", object.getString("first_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (object.has("last_name")) {
            try {
                bundle.putString("last_name", object.getString("last_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (object.has("email")) {
            try {
                bundle.putString("email", object.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (object.has("gender")) {
            try {
                bundle.putString("gender", object.getString("gender"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return bundle;
    }


    private void Login() {
        //Getting values from edit texts


        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("oauth_provider", "facebook");
        postParam.put("oauth_uid", id);
        postParam.put("first_name", firstnaem);
        postParam.put("last_name", lastname);
        postParam.put("email", email);
        postParam.put("gender", gender);
        postParam.put("profile", Profilepic.toString());

        Log.e("postparams", postParam.toString());


        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                LoginApi, new JSONObject(postParam),
                new Response.Listener<JSONObject>()


                {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());


                        pDialog.hide();

                        status = response.optInt("status");

                        int profile = response.optInt("profile");

                        usersession.createLoginSession(String.valueOf(profile));
                        if (status == 1) {
                            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
                            intent.putExtra("first_name", firstnaem);
                            intent.putExtra("last_name", lastname);
                            intent.putExtra("image", profile);
                            intent.putExtra("email", email);
                            intent.putExtra("gender", gender);
                            startActivity(intent);
                            finish();
                        }


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        callbackManager.onActivityResult(requestCode, resultCode, data);


    }


    // After complete authentication start new HomePage Activity


}


