package online.classes.details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import online.classes.R;
import online.classes.model.ClassDetails;
import online.classes.student.MyClasses;

public class SubjectDetails extends AppCompatActivity {

    SharedPreferences sp;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    ClassDetails classDetails;

    TextView subject,State,District,Address,date_started,date_end,TeacherName,language,whatsappLink;
//    Button add_class;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


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
        language=findViewById(R.id.language);
        whatsappLink=findViewById(R.id.whatsappLink);

        String SubjectName = getIntent().getStringExtra("subject");
        String state= getIntent().getStringExtra("state");
        String district= getIntent().getStringExtra("district");
        String startedDate= getIntent().getStringExtra("startedDate");
        String endDate= getIntent().getStringExtra("endDate");
        String address= getIntent().getStringExtra("address");
        String teacherName=getIntent().getStringExtra("teacherName");
        String lang=getIntent().getStringExtra("language");
        String whatsapp=getIntent().getStringExtra("whatsappLink");

        subject.setText(SubjectName);
        State.setText(state);
        District.setText(district);
        Address.setText(address);
        date_started.setText(startedDate);
        date_end.setText(endDate);
        TeacherName.setText(teacherName);
        language.setText(lang);
        whatsappLink.setText(whatsapp);

        whatsappLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(whatsapp);
            }
        });

    }
    private void openWhatsApp(String whatsappLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(whatsappLink));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}