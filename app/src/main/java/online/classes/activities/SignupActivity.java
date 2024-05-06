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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

import online.classes.R;
import online.classes.model.UserDetails;

public class SignupActivity extends AppCompatActivity {

    SharedPreferences sp;
    TextView login_here;
    EditText  username, emailId, mobile, pass;
    Button signUp;
    boolean isAllFieldsChecked = false;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference userCollection;
    UserDetails userDetails;
    CountryCodePicker codePicker;
    String currentDate;



    String phone,user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login_here = findViewById(R.id.login_here);
        signUp = findViewById(R.id.signUp);
        emailId = findViewById(R.id.emailId);
        username = findViewById(R.id.username);
        mobile = findViewById(R.id.mobileNumber);
        pass = findViewById(R.id.password);
        codePicker = findViewById(R.id.country_code);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String USER=getIntent().getStringExtra("USER");
        if(USER.equals("Student")){
            userCollection = firebaseFirestore.collection("StudentDetails");
            user="Student";
        }
        if(USER.equals("Admin")){
            userCollection = firebaseFirestore.collection("AdminDetails");
            user="Admin";
        }
        if(USER.equals("Teacher")) {
            userCollection = firebaseFirestore.collection("TeacherDetails");
            user="Teacher";
        }


        userDetails = new UserDetails();

        sp = getSharedPreferences("login", MODE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(new Date());

        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (sp.getBoolean("logged", false)) {
            goToMainActivity();
        }

        login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    registerNewUser();
                }
            }
        });

    }

    private void registerNewUser() {
        String email, password;
        email = emailId.getText().toString();
        password = pass.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter email and password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String country_code = codePicker.getSelectedCountryCode();
                            phone = "+" + country_code + mobile.getText().toString();
                            userDetails.setUserName(username.getText().toString());
                            userDetails.setUserEmail(emailId.getText().toString());
                            userDetails.setUserMobile(phone);
                            userDetails.setUserPassword(pass.getText().toString());
                            userDetails.setJoiningDate(currentDate);

                            userCollection.document(emailId.getText().toString()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Toast.makeText(getApplicationContext(), "User already exists.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                userCollection.document(emailId.getText().toString()).set(userDetails)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

                                                                String USER=getIntent().getStringExtra("USER");

                                                                sp.edit().putBoolean("logged", true).apply();
                                                                sp.edit().putString("USER", user).apply();
                                                                sp.edit().putString("UserEmail", emailId.getText().toString()).apply();
                                                                sp.edit().putString("UserName", username.getText().toString()).apply();
                                                                goToMainActivity();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Fail to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed!! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean CheckAllFields() {
        if (username.getText().toString().length() == 0) {
            username.setError("Username is required");
            return false;
        }

        if (emailId.getText().toString().length() == 0) {
            emailId.setError("Email is required");
            return false;
        }

        if (mobile.getText().toString().length() == 0) {
            mobile.setError("Mobile is required");
            return false;
        } else if (mobile.getText().toString().length() < 10) {
            mobile.setError("Enter valid mobile number");
            return false;
        }

        if (pass.getText().toString().length() == 0) {
            pass.setError("Password is required");
            return false;
        } else if (pass.getText().toString().length() < 8) {
            pass.setError("Password must be minimum 8 characters");
            return false;
        }

        return true;
    }

    public void goToMainActivity() {
        Intent i = new Intent(SignupActivity.this, NavigationDrawer.class);
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