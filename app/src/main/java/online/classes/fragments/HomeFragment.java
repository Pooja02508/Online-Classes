package online.classes.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import online.classes.details.CourseDetails;
import online.classes.details.TeacherDetails;
import online.classes.model.ClassDetails;
import online.classes.teacher.AddClassActivity;
import online.classes.dailyupdates.AddDailyUpdates;
import online.classes.admin.AddTeachers;
import online.classes.student.AllClasses;
import online.classes.student.LatestClasses;


public class HomeFragment extends Fragment {

    SharedPreferences sp;
    View root;

    Button add_class,all_classes,my_classes,latest_classes;
    Button teacher_details,add_teachers,daily_updates;
    ListView subjectList;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    ClassDetails classDetails;
    String subject,state,district,startedDate,endDate,teacherName;
    String userId;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String user = sp.getString("USER", null);
        userId = sp.getString("UserEmail", null);
//        Toast.makeText(getActivity(),user,Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        classDetails = new ClassDetails();
        if(user==null){
            String Stud=getActivity().getIntent().getStringExtra("USER");
            if(Stud.equals("Student")){
                root= inflater.inflate(R.layout.fragment_student, container, false);
                all_classes=root.findViewById(R.id.all_classes);
//                my_classes=root.findViewById(R.id.my_classes);
                latest_classes=root.findViewById(R.id.latest_classes);
                all_classes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AllClasses.class));
                    }
                });
//                my_classes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(new Intent(getActivity(), MyClasses.class));
//                    }
//                });
                latest_classes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), LatestClasses.class));
                    }
                });
            }
        }
        else {
            if (user.equals("Admin")) {
                root = inflater.inflate(R.layout.fragment_admin, container, false);

                teacher_details = root.findViewById(R.id.teacher_details);
                daily_updates=root.findViewById(R.id.daily_updates);
                add_teachers = root.findViewById(R.id.add_teachers);

                add_teachers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddTeachers.class);
                        startActivity(intent);
                    }
                });

                teacher_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TeacherDetails.class);
                        intent.putExtra("Detail", "Teacher");
                        startActivity(intent);
                    }
                });
                daily_updates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AddDailyUpdates.class));
                    }
                });
//            student_details.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent(getActivity(), TeacherDetails.class);
//                    intent.putExtra("Detail","Student");
//                    startActivity(intent);
//                }
//            });
            }


            if (user.equals("Teacher")) {
                root = inflater.inflate(R.layout.fragment_teacher, container, false);
                add_class = root.findViewById(R.id.add_class);
                subjectList = root.findViewById(R.id.subjectList);
                add_class.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), AddClassActivity.class));
                    }
                });


                CollectionReference classCollection = firestore.collection("ClassDetails").document(userId).collection("Subject");

                // Query all documents (classes) in the collection
                classCollection.get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<String> classList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                // Get the class details from each document
                                subject = document.getString("subject");
                                state = document.getString("state");
                                district = document.getString("district");
                                startedDate = document.getString("startedDate");
                                endDate = document.getString("endDate");
                                teacherName = document.getString("teacherName");

                                String classDetails = "Subject: " + subject + "\nState: " + state + "\nDistrict: " + district + "\nStarted Date: " + startedDate + "\nEnd Date: " + endDate + "\nTeacherName: " + teacherName + "\n";
                                classList.add(classDetails);
                            }

                            // Populate the ListView with class details
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, classList);
                            subjectList.setAdapter(adapter);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to retrieve classes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
//            subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedItem = (String) parent.getItemAtPosition(position);
//                    //  Toast.makeText(getActivity(),"Selected Subject: "+selectedItem,Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(getActivity(), CourseDetails.class);
//                    intent.putExtra("SubjectName",subject);
//                    intent.putExtra("TeacherId",userId);
//                    startActivity(intent);
//                }
//            });

                subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        String subjectName = selectedItem.substring(selectedItem.indexOf("Subject: ") + 9, selectedItem.indexOf("\n", selectedItem.indexOf("Subject: ")));
                        // Toast.makeText(getActivity(),"Selected Subject: "+subjectName,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), CourseDetails.class);
                        intent.putExtra("SubjectName", subjectName);
                        intent.putExtra("TeacherId", userId);
                        startActivity(intent);
                    }
                });
            }

        }

        return root;
    }


}