package online.classes.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

import online.classes.R;
import online.classes.model.ClassDetails;

public class UpdateTeacher extends AppCompatActivity {


 //   FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    ClassDetails classDetails;
    EditText update_firstname,update_mobile,update_location,update_password;
    Button updateTeacher;
    CountryCodePicker codePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
       // firebaseFirestore = FirebaseFirestore.getInstance();
        classDetails = new ClassDetails();

        update_firstname=findViewById(R.id.firstname);
        update_mobile=findViewById(R.id.mobileNumber);
        update_location=findViewById(R.id.location);
        updateTeacher=findViewById(R.id.updateTeacher);
        codePicker = findViewById(R.id.country_code);
        update_password=findViewById(R.id.password);


        String TeacherId=getIntent().getStringExtra("TeacherId");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("TeacherDetails").document(TeacherId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String firstname = documentSnapshot.getString("userName");
                String mobile = documentSnapshot.getString("userMobile");
                String location = documentSnapshot.getString("userLocation");
                String password = documentSnapshot.getString("userPassword");

                // Example of displaying data in Logcat for debugging:
                Log.d("TeacherDetails", "First Name: " + firstname);
                Log.d("TeacherDetails", "Mobile: " + mobile);
                Log.d("TeacherDetails", "Location: " + location);

                update_firstname.setText(firstname);
                update_mobile.setText(mobile);
                update_location.setText(location);
                update_password.setText(password);
            } else {
                // Document doesn't exist
                Log.d("TeacherDetails", "Details for Teacher " + TeacherId + " does not exist");
            }
        }).addOnFailureListener(e -> {
            // Error handling
            Log.e("TeacherDetails", "Error getting document: " + e.getMessage());
        });


        updateTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String country_code = codePicker.getSelectedCountryCode();
                String updatedMobile = "+" + country_code + update_mobile.getText().toString();
                String updatedFirstName = update_firstname.getText().toString();
                String updatedLocation = update_location.getText().toString();
                String updatedPassword=update_password.getText().toString();


                DocumentReference oldDocRef = firebaseFirestore.collection("TeacherDetails").document(TeacherId);


                oldDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        oldDocRef.delete().addOnSuccessListener(aVoid -> {
                            // Create a new document with the updated subject name and updated fields
                            firebaseFirestore.collection("TeacherDetails")
                                    .document(TeacherId)
                                    .set(data)  // Set the data from the original document
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Update the fields in the new document
                                        DocumentReference newDocRef = firebaseFirestore.collection("TeacherDetails")
                                                .document(TeacherId);

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("userName", updatedFirstName);
                                        updates.put("userMobile", updatedMobile);
                                        updates.put("userLocation", updatedLocation);
                                        updates.put("userPassword",updatedPassword);


                                        newDocRef.update(updates)
                                                .addOnSuccessListener(aVoid2 -> {
                                                    Toast.makeText(UpdateTeacher.this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                                                    // You may finish the activity or perform other actions upon successful update
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(UpdateTeacher.this, "Error updating document fields: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(UpdateTeacher.this, "Error creating new document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(UpdateTeacher.this, "Error deleting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(UpdateTeacher.this, "Document for Teacher does not exist", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(UpdateTeacher.this, "Error getting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });


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