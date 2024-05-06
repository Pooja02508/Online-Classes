package online.classes.details;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import online.classes.R;
import online.classes.model.ClassDetails;
import online.classes.admin.UpdateTeacher;

public class DetailView extends AppCompatActivity {

    TextView teacher_name,teacher_email,teacher_mobile,teacher_joiningDate;
    ListView teacherClassList;
    TextView title,updateTeacher,deleteTeacher;
    SharedPreferences sp,tsp;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference classCollection,teacherCollection,studentCollection,myClassCollection;
    ClassDetails classDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teacherClassList = findViewById(R.id.teacherClassList);
        title = findViewById(R.id.title);
        updateTeacher=findViewById(R.id.updateTeacher);
        deleteTeacher=findViewById(R.id.deleteTeacher);
        teacher_name = findViewById(R.id.teacher_name);
        teacher_email = findViewById(R.id.teacher_email);
        teacher_mobile = findViewById(R.id.teacher_mobile);
        teacher_joiningDate = findViewById(R.id.teacher_joiningDate);

        String teacherName = getIntent().getStringExtra("TeacherName");
        String teacherEmail = getIntent().getStringExtra("TeacherEmail");
        String teacherMobile = getIntent().getStringExtra("TeacherMobile");
       // String joiningDate = getIntent().getStringExtra("JoiningDate");
        String Detail = getIntent().getStringExtra("Detail");

        teacher_name.setText(teacherName);
        teacher_email.setText(teacherEmail);
        teacher_mobile.setText(teacherMobile);
      //  teacher_joiningDate.setText(joiningDate);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserEmail", null);

        tsp = getSharedPreferences("TeacherLogin", MODE_PRIVATE);
        String teacherUserId = tsp.getString("TeacherEmail", null);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        classDetails = new ClassDetails();

        classCollection = firebaseFirestore.collection("ClassDetails").document(teacherUserId).collection("Subject");
        teacherCollection = firebaseFirestore.collection("TeacherDetails");
        studentCollection = firebaseFirestore.collection("StudentDetails");
        myClassCollection = firebaseFirestore.collection("My Classes").document(teacherEmail).collection("Courses");



        updateTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), UpdateTeacher.class);
                intent.putExtra("TeacherId",teacherUserId);
                startActivity(intent);
            }
        });
        deleteTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("TeacherDetails").document(teacherUserId);

                // Perform the delete operation
                docRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully deleted
                                Toast.makeText(getApplicationContext(), "Teacher deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error handling
                                Toast.makeText(getApplicationContext(), "Error deleting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        List<String> teacherList = new ArrayList<>();

        if (Detail.equals("Teacher")) {
            title.setText("Teacher Details");
            updateTeacher.setText("Update Teacher");
            deleteTeacher.setText("Delete Teacher");

            classCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Get teacher details from each document
                            String subjectName = document.getString("subject");
                            String state = document.getString("state");
                            String district = document.getString("district");
                            String startedDate = document.getString("startedDate");
                            String endDate = document.getString("endDate");
                            String address = document.getString("address");
                            String teacher_Name = document.getString("teacherName");
                            String language=document.getString("language");
                            // Construct teacher details string
                            String teacherDetails = "Subject Name: " + subjectName + "\nState: " + state + "\nDistrict: " + district + "\nAddress: " + address + "\nStarted Date: " + startedDate + "\nEnd Date: " + endDate + "\nLanguage: " + language+"\n"+ "\nTeacher Name: " + teacher_Name+"\n";
                            teacherList.add(teacherDetails);
                        }
                        // Display the teacher list in a ListView or any other UI component
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, teacherList);
                        teacherClassList.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve teacher details
                        Toast.makeText(getApplicationContext(), "Failed to retrieve teacher details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            teacherClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the clicked item from the adapter
                    String clickedItem = (String) parent.getItemAtPosition(position);
                    String subjectName = clickedItem.substring(clickedItem.indexOf("Subject: ") + 15, clickedItem.indexOf("\n"));
                    Intent intent = new Intent(getApplicationContext(), CourseDetails.class);
                    intent.putExtra("SubjectName", subjectName);
                    intent.putExtra("TeacherId",teacherUserId);
                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), clickedItem, Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            title.setText("Student Details");

            updateTeacher.setText("Update Student");
            deleteTeacher.setText("Delete Student");

            myClassCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Get teacher details from each document
                            String subjectName = document.getString("subject");
                            String state = document.getString("state");
                            String district = document.getString("district");
                            String startedDate = document.getString("startedDate");
                            String endDate = document.getString("endDate");
                            String address = document.getString("address");
                            String teacher_Name = document.getString("teacherName");
                            // Construct teacher details string
                            String teacherDetails = "Subject Name: " + subjectName + "\nState: " + state + "\nDistrict: " + district + "\nAddress: " + address + "\nStarted Date: " + startedDate + "\nEnd Date: " + endDate + "\nTeacher Name: " + teacher_Name+"\n";
                            teacherList.add(teacherDetails);
                        }
                        // Display the teacher list in a ListView or any other UI component
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, teacherList);
                        teacherClassList.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to retrieve teacher details
                        Toast.makeText(getApplicationContext(), "Failed to retrieve teacher details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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