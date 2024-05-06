package online.classes.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import online.classes.R;
import online.classes.model.ClassDetails;
import online.classes.teacher.UpdateClass;

public class CourseDetails extends AppCompatActivity {

    SharedPreferences sp;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    ClassDetails classDetails;

    TextView subject,state,district,address,date_started,date_end,teacherName,language,whatsappLink;
    Button updateCourse,deleteCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        classDetails = new ClassDetails();

        String subjectName=getIntent().getStringExtra("SubjectName");
        String TeacherId=getIntent().getStringExtra("TeacherId");

        subject=findViewById(R.id.subject);
        state=findViewById(R.id.state);
        district=findViewById(R.id.district);
        address=findViewById(R.id.address);
        date_started=findViewById(R.id.date_started);
        date_end=findViewById(R.id.date_end);
        teacherName=findViewById(R.id.teacherName);
        updateCourse=findViewById(R.id.updateCourse);
        deleteCourse=findViewById(R.id.deleteCourse);
        language=findViewById(R.id.language);
        whatsappLink=findViewById(R.id.whatsappLink);

        DocumentReference docRef = firestore.collection("ClassDetails").document(TeacherId).collection("Subject").document(subjectName);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Document exists, retrieve data and populate EditText fields
                String subject_name = documentSnapshot.getString("subject");
                String state_name = documentSnapshot.getString("state");
                String district_name = documentSnapshot.getString("district");
                String addr = documentSnapshot.getString("address");
                String startedDate = documentSnapshot.getString("startedDate");
                String endDate = documentSnapshot.getString("endDate");
                String teacher_name = documentSnapshot.getString("teacherName");
                String lang=documentSnapshot.getString("language");
                String whatsapp=documentSnapshot.getString("whatsappLink");

                // Populate EditText fields with retrieved data
                subject.setText(subject_name);
                state.setText(state_name);
                district.setText(district_name);
                address.setText(addr);
                date_started.setText(startedDate);
                date_end.setText(endDate);
                teacherName.setText(teacher_name);
                language.setText(lang);
                whatsappLink.setText(whatsapp);
            } else {
                // Document doesn't exist
                Toast.makeText(getApplicationContext(), "Document for subject " + subjectName + " does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            // Error handling
            Toast.makeText(getApplicationContext(), "Error getting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        updateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), UpdateClass.class);
                intent.putExtra("subjectName",subjectName);
                intent.putExtra("TeacherId",TeacherId);
                startActivity(intent);
            }
        });
        deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = firestore.collection("ClassDetails").document(TeacherId).collection("Subject").document(subjectName);

                // Perform the delete operation
                docRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully deleted
                                Toast.makeText(getApplicationContext(), "Course deleted successfully", Toast.LENGTH_SHORT).show();
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