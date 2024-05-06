package online.classes.dailyupdates;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import online.classes.R;
import online.classes.adapter.VideoAdapter;
import online.classes.details.VideoViewActivity;

public class VideosActivity extends AppCompatActivity {

    private GridView gridView;
    private FirebaseFirestore db;
    private List<String> videoUrls;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.gridView);
        db = FirebaseFirestore.getInstance();
        videoUrls = new ArrayList<>();
        adapter = new VideoAdapter(this, R.layout.grid_item_video, videoUrls);
        gridView.setAdapter(adapter);

        retrieveVideos();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageUrl = videoUrls.get(position);
                Intent intent=new Intent(getApplicationContext(), VideoViewActivity.class);
                intent.putExtra("Video Url",imageUrl);
                startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
