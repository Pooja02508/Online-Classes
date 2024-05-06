package online.classes.dailyupdates;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import online.classes.R;

public class UpdateVideo extends AppCompatActivity {

    VideoView videoView;
    Button deleteVideo;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_video);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        videoView=findViewById(R.id.videoView);
        deleteVideo=findViewById(R.id.deleteVideo);

        String videoUrl=getIntent().getStringExtra("Video Url");
        videoView.setVideoPath(videoUrl);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Start playback
        // videoView.start();
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start playback
        videoView.start();

        // Hide system UI elements for full screen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVideoFromFirestore(videoUrl);
            }
        });
    }

    private void deleteVideoFromFirestore(final String photoUrl) {
        // Query Firestore to find the document containing the given imageUrl
        db.collection("DailyUpdates").document("Videos").collection("VideoList")
                .whereEqualTo("videoUrl", photoUrl)
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
                                                    Toast.makeText(UpdateVideo.this, "Video deleted successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(UpdateVideo.this, "Failed to delete video", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(UpdateVideo.this, "Error finding photo document", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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