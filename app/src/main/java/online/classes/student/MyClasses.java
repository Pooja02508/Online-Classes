package online.classes.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import online.classes.R;
import online.classes.model.ClassDetails;
import online.classes.teacher.DeleteMyCourse;

public class MyClasses extends AppCompatActivity {

    SharedPreferences sp;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    CollectionReference classCollection;
    ClassDetails classDetails;
    String userId;
    ListView courseList;
    String subject,state,district,startedDateString,endDate,address,teacherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        userId = sp.getString("UserEmail", null);

        courseList=findViewById(R.id.courseList);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        classDetails = new ClassDetails();

        List<String> classList = new ArrayList<>();

        CollectionReference classCollection = firestore.collection("My Classes").document(userId).collection("Courses");

        // Query all documents (classes) in the collection
        classCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        subject = document.getString("subject");
                        state = document.getString("state");
                        district = document.getString("district");
                        address = document.getString("address");
                        startedDateString = document.getString("startedDate");
                        endDate = document.getString("endDate");
                        teacherName = document.getString("teacherName");

                        String classDetails = "Subject: " + subject + "\nState: " + state + "\nDistrict: " + district +"\nAddress: "+ address + "\nStarted Date: " + startedDateString+ "\nEnd Date: " + endDate + "\nTeacher Name: " + teacherName+"\n";
                        classList.add(classDetails);
                    }

                    // Populate the ListView with class details
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, classList);
                    courseList.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve classes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });



        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = classList.get(position);
                String[] dataFields = selectedItem.split("\n");

                // Extract data from the selected item
                String subject1 = dataFields[0].substring(dataFields[0].indexOf("Subject: ") + 9);
                String state1 = dataFields[1].substring(dataFields[1].indexOf("State: ") + 7);
                String district1 = dataFields[2].substring(dataFields[2].indexOf("District: ") + 10);
                String address1 = dataFields[3].substring(dataFields[3].indexOf("Address: ") + 9);
                String startedDate1 = dataFields[4].substring(dataFields[4].indexOf("Started Date: ") + 14);
                String endDate1 = dataFields[5].substring(dataFields[5].indexOf("End Date: ") + 10);
                String teacherName1 = dataFields[6].substring(dataFields[6].indexOf("Teacher Name: ") + 14);

                // Create an intent and pass the extracted data to the next activity
                Intent intent = new Intent(getApplicationContext(), DeleteMyCourse.class);
                intent.putExtra("subject", subject1);
                intent.putExtra("state", state1);
                intent.putExtra("district", district1);
                intent.putExtra("address", address1);
                intent.putExtra("startedDate", startedDate1);
                intent.putExtra("endDate", endDate1);
                intent.putExtra("teacherName", teacherName1);
                startActivity(intent);
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