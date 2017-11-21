package ceo.app_knit.com.ceo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainScreenActivity extends AppCompatActivity {

    CircleImageView Read, Watch, conect;
    Button Logout;
    LoginSessoion loginSessoion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        loginSessoion = new LoginSessoion(getApplicationContext());
        Read = findViewById(R.id.Read);
        Logout = findViewById(R.id.Logout);
        Watch = (CircleImageView) findViewById(R.id.Watch);
        conect = findViewById(R.id.conect);


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
                Intent intent = new Intent(MainScreenActivity.this, CustomPlayerControlActivity.class);
                startActivity(intent);
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
                loginSessoion.logoutUser();
                Intent intent = new Intent(MainScreenActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }


}
