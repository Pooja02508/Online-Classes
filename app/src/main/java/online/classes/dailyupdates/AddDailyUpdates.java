package online.classes.dailyupdates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Objects;

import online.classes.R;

public class AddDailyUpdates extends AppCompatActivity {

    RelativeLayout videos,messages,photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_updates);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        videos=findViewById(R.id.videos);
        photos=findViewById(R.id.photos);
        messages=findViewById(R.id.messages);

        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddVideosActivity.class));
            }
        });
        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddPhotosActivity.class));
            }
        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddMessagesActivity.class));
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