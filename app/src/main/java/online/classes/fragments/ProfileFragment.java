package online.classes.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import online.classes.R;
import online.classes.model.UserDetails;

public class ProfileFragment extends Fragment {


    CircleImageView profile_image;
    ImageView edit_profile1;
    SharedPreferences sp;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    TextView user_name, user_email, user_mobile;
    UserDetails userDetails;
    String USER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_image = root.findViewById(R.id.profile_image);

        sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserEmail", null);
        String user = sp.getString("USER", null);


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userDetails = new UserDetails();

        user_name = root.findViewById(R.id.user_name);
        user_email = root.findViewById(R.id.user_email);
        user_mobile = root.findViewById(R.id.user_mobile);
        edit_profile1 = root.findViewById(R.id.edit_profile2);


        if (userId == null) {

        } else {
            if(user.equals("Student")){
                USER="StudentDetails";
            }
            else{
                USER="TeacherDetails";
            }
            firestore.collection(USER).document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String getUserFirstName = document.getString("userName");
                                    String getUserMobile = document.getString("userMobile");
                                    String getUserEmail = document.getString("userEmail");

                                    user_name.setText(getUserFirstName);
                                    user_mobile.setText(getUserMobile);
                                    user_email.setText(getUserEmail);


                                } else {
                                    Toast.makeText(getActivity(), "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error getting user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return root;
    }
}