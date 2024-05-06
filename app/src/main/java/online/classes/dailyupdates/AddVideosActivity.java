package online.classes.dailyupdates;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import online.classes.R;
import online.classes.adapter.VideoAdapter;
import online.classes.details.VideoViewActivity;

public class AddVideosActivity extends AppCompatActivity {


    Button add;
    VideoView add_video;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private Uri imageUri, videoUri;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    private GridView gridView;
    private List<String> videoUrls;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_videos);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        add_video = findViewById(R.id.add_video);
        add = findViewById(R.id.add);

        gridView = findViewById(R.id.gridView);
        videoUrls = new ArrayList<>();
        adapter = new VideoAdapter(this, R.layout.grid_item_video, videoUrls);
        gridView.setAdapter(adapter);

        retrieveVideos();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageUrl = videoUrls.get(position);
                Intent intent=new Intent(getApplicationContext(), UpdateVideo.class);
                intent.putExtra("Video Url",imageUrl);
                startActivity(intent);
            }
        });

        add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_REQUEST);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoUri != null) {

                    final String randomKey = UUID.randomUUID().toString();
                    // Create a reference to 'videos/<randomKey>.mp4'
                    StorageReference videoRef = storageRef.child("videos/" + randomKey + ".mp4");

                    // Upload file to Firestore Storage
                    videoRef.putFile(videoUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Video uploaded successfully
                                    // Get the download URL for the uploaded video
                                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Video URL
                                            String videoUrl = uri.toString();

                                            // Create a new document with a generated ID
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("videoUrl", videoUrl);

                                            // Add a new document with a generated ID
                                            db.collection("DailyUpdates")
                                                    .document("Videos")
                                                    .collection("VideoList")
                                                    .document(randomKey)
                                                    .set(data)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(),"Video Uploaded",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(),"Video not uploaded",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void retrieveVideos() {
        db.collection("DailyUpdates").document("Videos").collection("VideoList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String videoUrl = document.getString("videoUrl");
                                videoUrls.add(videoUrl);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // Handle failure
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
             if (requestCode == PICK_VIDEO_REQUEST && data != null && data.getData() != null) {
                videoUri = data.getData();
                imageUri = null;
                add_video.setVideoURI(videoUri);
                add_video.start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}