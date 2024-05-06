package online.classes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import online.classes.R;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        String user = sp.getString("USER", null);

        Toast.makeText(getApplicationContext(),"User is: "+user,Toast.LENGTH_SHORT).show();
    }
}