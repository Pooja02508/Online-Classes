package online.classes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import online.classes.R;
import online.classes.dailyupdates.DailyUpdates;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    Button teacher_login,student_login,admin_login,daily_updates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        student_login=findViewById(R.id.student_login);
        teacher_login=findViewById(R.id.teacher_login);
        admin_login=findViewById(R.id.admin_login);
        daily_updates=findViewById(R.id.daily_updates);

        student_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),NavigationDrawer.class);
                intent.putExtra("USER","Student");
                startActivity(intent);
            }
        });
        teacher_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                intent.putExtra("USER","Teacher");
                startActivity(intent);
            }
        });

        admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                intent.putExtra("USER","Admin");
                startActivity(intent);
            }
        });
        daily_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DailyUpdates.class));
            }
        });

        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (sp.getBoolean("logged", false)) {
            Intent intent = new Intent(MainActivity.this, NavigationDrawer.class);
            startActivity(intent);
            finish();
        }

    }
}