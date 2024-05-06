package online.classes.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Objects;

import online.classes.R;
import online.classes.model.ClassDetails;

public class DeleteMyCourse extends AppCompatActivity {

    SharedPreferences sp;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    ClassDetails classDetails;

    TextView subject,State,District,Address,date_started,date_end,TeacherName,whatsappLink;
    Button delete_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_my_course);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserEmail", null);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        classDetails = new ClassDetails();

        subject=findViewById(R.id.subject);
        State=findViewById(R.id.state);
        District=findViewById(R.id.district);
        Address=findViewById(R.id.address);
        date_started=findViewById(R.id.date_started);
        date_end=findViewById(R.id.date_end);
        TeacherName=findViewById(R.id.teacherName);
        whatsappLink=findViewById(R.id.whatsappLink);
        delete_course=findViewById(R.id.delete_course);

        String SubjectName = getIntent().getStringExtra("subject");
        String state= getIntent().getStringExtra("state");
        String district= getIntent().getStringExtra("district");
        String startedDate= getIntent().getStringExtra("startedDate");
        String endDate= getIntent().getStringExtra("endDate");
        String address= getIntent().getStringExtra("address");
        String teacherName=getIntent().getStringExtra("teacherName");
        String whatsapp_link=getIntent().getStringExtra("whatsappLink");

        subject.setText(SubjectName);
        State.setText(state);
        District.setText(district);
        Address.setText(address);
        date_started.setText(startedDate);
        date_end.setText(endDate);
        TeacherName.setText(teacherName);
        whatsappLink.setText(whatsapp_link);

        delete_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the reference to the document to be deleted
                DocumentReference docRef = FirebaseFirestore.getInstance()
                        .collection("My Classes").document(userId).collection("Courses").document(SubjectName);

                // Perform the delete operation
                docRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully deleted
                                Toast.makeText(getApplicationContext(), "Document deleted successfully", Toast.LENGTH_SHORT).show();
                                // Finish the activity or perform any other necessary action
                                finish();
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