package online.classes.dailyupdates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import online.classes.R;
import online.classes.adapter.PhotoAdapter;
import online.classes.details.PhotoViewActivity;

public class AddPhotosActivity extends AppCompatActivity {

    ImageView add_photo;
    Button add;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    GridView gridView;

    private ArrayList<String> mImageUrlList;
    private PhotoAdapter mAdapter;
    String randomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        add_photo=findViewById(R.id.add_photo);
        add=findViewById(R.id.add);
        gridView=findViewById(R.id.gridView);

        mImageUrlList = new ArrayList<>();
        mAdapter = new PhotoAdapter(this, mImageUrlList);
        gridView.setAdapter(mAdapter);

        db = FirebaseFirestore.getInstance();
        fetchPhotosFromFirestore();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageUrl = mImageUrlList.get(position);
                Intent intent=new Intent(getApplicationContext(), UpdatePhoto.class);
                intent.putExtra("Photo Url",imageUrl);
                startActivity(intent);
            }
        });

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    // Generate a random UUID for the image file
                     randomKey = UUID.randomUUID().toString();

                    // Create a reference to 'images/<randomKey>.jpg'
                    final StorageReference imageRef = storageRef.child("images/" + randomKey + ".jpg");

                    // Upload file to Firestore Storage
                    imageRef.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Get the download URL for the uploaded image
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Image URL
                                            String imageUrl = uri.toString();

                                            // Create a new document with a generated ID
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("imageUrl", imageUrl);

                                            // Add a new document with a generated ID
                                            db.collection("DailyUpdates")
                                                    .document("Photos")
                                                    .collection("PhotoList")
                                                    .document(randomKey)
                                                    .set(data)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
                                                            fetchPhotosFromFirestore();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Photo not uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void fetchPhotosFromFirestore() {
        db.collection("DailyUpdates").document("Photos").collection("PhotoList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageUrl = document.getString("imageUrl");
                                mImageUrlList.add(imageUrl);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error fetching photos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            add_photo.setImageURI(imageUri);
        }
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

