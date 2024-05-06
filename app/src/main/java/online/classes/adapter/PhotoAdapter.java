package online.classes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import online.classes.R;

public class PhotoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mImageUrlList;

    public PhotoAdapter(Context context, ArrayList<String> imageUrlList) {
        mContext = context;
        mImageUrlList = imageUrlList;
    }

    @Override
    public int getCount() {
        return mImageUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.grid_item_photo, null);
        } else {
            gridView = convertView;
        }

        ImageView imageView = gridView.findViewById(R.id.grid_item_image);
        Picasso.get().load(mImageUrlList.get(position)).into(imageView);

        return gridView;
    }
}
