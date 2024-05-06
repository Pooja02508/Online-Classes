package online.classes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import online.classes.R;

public class LoginActivity extends AppCompatActivity {

    String user;

    TextView signup_here;
    SharedPreferences sp;
    private FirebaseAuth mAuth;
    TextInputEditText pass,emailid;

    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        signup_here=findViewById(R.id.signup_here);
        pass=findViewById(R.id.password);
        emailid=findViewById(R.id.username);

        login=findViewById(R.id.login);

        String USER=getIntent().getStringExtra("USER");

        if(USER.equals("Student")){
            user="Student";
        }
        if(USER.equals("Admin")){
            user="Admin";
        }
        if(USER.equals("Teacher")) {
            user = "Teacher";
        }

        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login",MODE_PRIVATE);


        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }


        signup_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SignupActivity.class);
                intent.putExtra("USER",user);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();
            }
        });


    }

    private void loginUserAccount() {

        String email, password;
        email = emailid.getText().toString();
        password = pass.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_SHORT).show();
                                    sp.edit().putBoolean("logged",true).apply();
                                    sp.edit().putString("USER", user).apply();
                                    sp.edit().putString("UserEmail",email).apply();
                                    goToMainActivity();
                                    finish();
                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
    }

    public void goToMainActivity(){

        Intent i = new Intent(LoginActivity.this, NavigationDrawer.class);
        i.putExtra("UserEmail",emailid.getText().toString());
        startActivity(i);
        finish();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }
}