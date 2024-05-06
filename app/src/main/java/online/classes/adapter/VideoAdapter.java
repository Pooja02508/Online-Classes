package online.classes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import online.classes.R;

public class VideoAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> videos;

    public VideoAdapter(@NonNull Context context, int resource, @NonNull List<String> videos) {
        super(context, resource, videos);
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(context).inflate(R.layout.grid_item_video, parent, false);
        }

        String videoUrl = videos.get(position);

        ImageView videoImageView = gridItemView.findViewById(R.id.videoImageView);
        // Load video thumbnail using Glide or any other image loading library
        Glide.with(context).load(videoUrl).into(videoImageView);

        return gridItemView;
    }
}

