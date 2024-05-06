package online.classes.dailyupdates;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import online.classes.R;

public class UpdatePhoto extends AppCompatActivity {

    ImageView photoView;
    Button deletePhoto;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        photoView = findViewById(R.id.photoView);
        deletePhoto = findViewById(R.id.deletePhoto);

        String url = getIntent().getStringExtra("Photo Url");

        Glide.with(this).load(url).into(photoView);

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete photo from Firestore and storage
                deletePhotoFromFirestore(url);
            }
        });
    }

    private void deletePhotoFromFirestore(final String photoUrl) {
        // Query Firestore to find the document containing the given imageUrl
        db.collection("DailyUpdates").document("Photos").collection("PhotoList")
                .whereEqualTo("imageUrl", photoUrl)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Delete the document containing the imageUrl
                                document.getReference().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Document deleted successfully
                                                   // deletePhotoFromStorage(photoUrl);
                                                    Toast.makeText(UpdatePhoto.this, "Photo deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(UpdatePhoto.this, "Failed to delete photo", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(UpdatePhoto.this, "Error finding photo document", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


//    private void deletePhotoFromStorage(String photoUrl) {
//        // Get a reference to the photo file in storage
//        StorageReference photoRef = storage.getReferenceFromUrl(photoUrl);
//        final StorageReference imageRef = storageRef.child("images/" + photoUrl);
//
//        // Delete the photo file
//        imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(Task<Void> task) {
//                if (task.isSuccessful()) {
////                    Toast.makeText(UpdatePhoto.this, "Photo deleted successfully", Toast.LENGTH_SHORT).show();
//                    finish(); // Finish the activity after successful deletion
//                } else {
//                    Toast.makeText(UpdatePhoto.this, "Failed to delete photo", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
