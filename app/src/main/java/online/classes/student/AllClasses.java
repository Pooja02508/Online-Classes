package online.classes.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import online.classes.R;
import online.classes.details.SubjectDetails;
import online.classes.model.ClassDetails;

public class AllClasses extends AppCompatActivity {

    ListView classesList;
    SharedPreferences sp;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference classCollection;
    ClassDetails classDetails;
    String subject,state,district,startedDateString,endDate,address,teacherName,language,whatsappLink;
    Button searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_classes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        classesList=findViewById(R.id.classesList);
        searchView=findViewById(R.id.search);



        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        classDetails=new ClassDetails();

        classCollection = firebaseFirestore.collection("ClassDetails");

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchCourse.class));
            }
        });


        // Declare classList as a field of your class
        List<String> classList = new ArrayList<>();

        firebaseFirestore.collectionGroup("Subject").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get the class details from each document
                        subject = document.getString("subject");
                        state = document.getString("state");
                        district = document.getString("district");
                        address = document.getString("address");
                        startedDateString = document.getString("startedDate");
                        endDate = document.getString("endDate");
                        teacherName = document.getString("teacherName");
                        language=document.getString("language");
                        whatsappLink=document.getString("whatsappLink");
                        // Construct the class details string with the number before each item
                        String classDetails = "Subject: " + subject + "\nState: " + state + "\nDistrict: " + district +"\nAddress: "+ address + "\nStarted Date: " + startedDateString+ "\nEnd Date: " + endDate +"\nLanguage: " + language+ "\nTeacher Name: " + teacherName+"\nWhatsapp Link: "+ whatsappLink+"\n";
                        classList.add(classDetails);
                    }

                    // Populate the ListView with class details
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, classList);
                    classesList.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve classes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Firebase Error",e.getMessage());
                });

        classesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the classList based on its position
                String selectedItem = classList.get(position);

                // Split the selected item string to extract individual data fields
                String[] dataFields = selectedItem.split("\n");

                // Extract data from the selected item
                String subject1 = dataFields[0].substring(dataFields[0].indexOf("Subject: ") + 9);
                String state1 = dataFields[1].substring(dataFields[1].indexOf("State: ") + 7);
                String district1 = dataFields[2].substring(dataFields[2].indexOf("District: ") + 10);
                String address1 = dataFields[3].substring(dataFields[3].indexOf("Address: ") + 9);
                String startedDate1 = dataFields[4].substring(dataFields[4].indexOf("Started Date: ") + 14);
                String endDate1 = dataFields[5].substring(dataFields[5].indexOf("End Date: ") + 10);
                String lang = dataFields[6].substring(dataFields[6].indexOf("Language: ") + 10);
                String teacher1 = dataFields[7].substring(dataFields[7].indexOf("Teacher Name: ") + 14);
                String whatsapp= dataFields[8].substring(dataFields[8].indexOf("Whatsapp Link: ") + 14);

                // Create an intent and pass the extracted data to the next activity
                Intent intent = new Intent(getApplicationContext(), SubjectDetails.class);
                intent.putExtra("subject", subject1);
                intent.putExtra("state", state1);
                intent.putExtra("district", district1);
                intent.putExtra("address", address1);
                intent.putExtra("startedDate", startedDate1);
                intent.putExtra("endDate", endDate1);
                intent.putExtra("language",lang);
                intent.putExtra("teacherName", teacher1);
                intent.putExtra("whatsappLink",whatsapp);
                startActivity(intent);
            }
        });


//        classesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItem = classList.get(position); // Get the selected item from the classList
//                // Extract the subject name from the selected item
//                String subjectName = selectedItem.substring(selectedItem.indexOf("Subject: ") + 9, selectedItem.indexOf("\n"));
//                Intent intent = new Intent(getApplicationContext(), SubjectDetails.class);
//                intent.putExtra("SubjectName", subjectName);
//                startActivity(intent);
//            }
//        });




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