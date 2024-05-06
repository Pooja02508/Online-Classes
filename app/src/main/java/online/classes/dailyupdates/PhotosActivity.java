package online.classes.dailyupdates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import online.classes.R;
import online.classes.adapter.PhotoAdapter;
import online.classes.details.PhotoViewActivity;

public class PhotosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<String> mImageUrlList;
    private PhotoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView gridView = findViewById(R.id.grid_view);
        mImageUrlList = new ArrayList<>();
        mAdapter = new PhotoAdapter(this, mImageUrlList);
        gridView.setAdapter(mAdapter);

        db = FirebaseFirestore.getInstance();
        fetchPhotosFromFirestore();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageUrl = mImageUrlList.get(position);
                Intent intent=new Intent(getApplicationContext(), PhotoViewActivity.class);
                intent.putExtra("Photo Url",imageUrl);
                startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}