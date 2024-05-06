package online.classes.details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import online.classes.R;
import online.classes.details.DetailView;
import online.classes.model.ClassDetails;
import online.classes.model.UserDetails;

public class TeacherDetails extends AppCompatActivity {

    ListView detailList;

    SharedPreferences sp;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference classCollection,teacherCollection,studentCollection,myClassCollection;
    ClassDetails classDetails;
    UserDetails userDetails;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailList=findViewById(R.id.detailList);
        title=findViewById(R.id.title);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserEmail", null);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        classDetails=new ClassDetails();

        classCollection = firebaseFirestore.collection("ClassDetails");
        teacherCollection = firebaseFirestore.collection("TeacherDetails");
        studentCollection = firebaseFirestore.collection("StudentDetails");
        myClassCollection=firebaseFirestore.collection("My Classes");


        String Detail=getIntent().getStringExtra("Detail");

        List<String> teacherList = new ArrayList<>();
        if(Detail.equals("Teacher")){
            title.setText("Teacher Details");
            teacherCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Get teacher details from each document
                            String teacherName = document.getString("userName");
                            String teacherEmail = document.getString("userEmail");
//                            String joiningDate = document.getString("joiningDate");
                            String teacherMobile = document.getString("userMobile");
                            // Construct teacher details string
                            String teacherDetails = "Name: " + teacherName + "\nEmail: " + teacherEmail+"\nMobile: " + teacherMobile +"\n";
                            teacherList.add(teacherDetails);
                        }
                        // Display the teacher list in a ListView or any other UI component
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, teacherList);
                        detailList.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve teacher details
                        Toast.makeText(getApplicationContext(), "Failed to retrieve teacher details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected teacher's details from the teacherList
                    String selectedTeacherDetails = teacherList.get(position);
                    // Split the teacher details string to extract individual data fields
                    String[] dataFields = selectedTeacherDetails.split("\n");
                    // Extract data from the selected teacher details
                    String teacherName = dataFields[0].substring(dataFields[0].indexOf("Name: ") + 6);
                    String teacherEmail = dataFields[1].substring(dataFields[1].indexOf("Email: ") + 7);
                    String teacherMobile = dataFields[2].substring(dataFields[2].indexOf("Mobile: ") + 8);
//                    String joiningDate = dataFields[3].substring(dataFields[3].indexOf("Joining Date: ") + 14);

                    // Create an intent to launch the DetailView activity
                    Intent intent = new Intent(getApplicationContext(), DetailView.class);
                    intent.putExtra("Detail","Teacher");
                    intent.putExtra("TeacherName", teacherName);
                    intent.putExtra("TeacherEmail", teacherEmail);
                    intent.putExtra("TeacherMobile", teacherMobile);
//                    intent.putExtra("JoiningDate", joiningDate);
                    startActivity(intent);
                }
            });


        }
        else{
            title.setText("Student Details");
            studentCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Get teacher details from each document
                            String teacherName = document.getString("userName");
                            String teacherEmail = document.getString("userEmail");
//                            String joiningDate = document.getString("joiningDate");
                            String teacherMobile = document.getString("userMobile");
                            // Construct teacher details string
                            String teacherDetails = "Name: " + teacherName + "\nEmail: " + teacherEmail+"\nMobile: " + teacherMobile +"\n";
                            teacherList.add(teacherDetails);
                        }
                        // Display the teacher list in a ListView or any other UI component
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, teacherList);
                        detailList.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve teacher details
                        Toast.makeText(getApplicationContext(), "Failed to retrieve student details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected teacher's details from the teacherList
                    String selectedTeacherDetails = teacherList.get(position);
                    // Split the teacher details string to extract individual data fields
                    String[] dataFields = selectedTeacherDetails.split("\n");
                    // Extract data from the selected teacher details
                    String teacherName = dataFields[0].substring(dataFields[0].indexOf("Name: ") + 6);
                    String teacherEmail = dataFields[1].substring(dataFields[1].indexOf("Email: ") + 7);
                    String teacherMobile = dataFields[2].substring(dataFields[2].indexOf("Mobile: ") + 8);
//                    String joiningDate = dataFields[3].substring(dataFields[3].indexOf("Joining Date: ") + 15);

                    // Create an intent to launch the DetailView activity
                    Intent intent = new Intent(getApplicationContext(), DetailView.class);
                    intent.putExtra("Detail","Student");
                    intent.putExtra("TeacherName", teacherName);
                    intent.putExtra("TeacherEmail", teacherEmail);
                    intent.putExtra("TeacherMobile", teacherMobile);
//                    intent.putExtra("JoiningDate", joiningDate);
                    startActivity(intent);
                }
            });
        }
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