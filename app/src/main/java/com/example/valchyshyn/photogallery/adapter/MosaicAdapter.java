package com.example.valchyshyn.photogallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.valchyshyn.photogallery.R;
import com.example.valchyshyn.photogallery.viewHolder.GalleryViewHolder;

import java.util.List;

/**
 * Created by valchyshyn on 14.02.17.
 */

public class MosaicAdapter extends ArrayAdapter<Uri> {
  private List<Uri> imageList;
  private Context context;

  public MosaicAdapter(Context context, List<Uri> imageList) {
    super(context, R.layout.gallary_item, imageList);
    this.context = context;
    this.imageList = imageList;
  }

  public void setImageList(List<Uri> imageList) {
    this.imageList.clear();
    this.imageList.addAll(imageList);
    notifyDataSetChanged();
  }

  public void log() {
    Log.d("Test", "clear");
  }

  @Override
  public int getCount() {
    return imageList.isEmpty() ? 0 : imageList.size();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    GalleryViewHolder viewHolder = null;
    View vi = convertView;
    if (vi == null) {
      vi = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallary_item, parent, false);
      viewHolder = new GalleryViewHolder(vi, context);
      vi.setTag(viewHolder);
    } else {
      viewHolder = (GalleryViewHolder) vi.getTag();
    }

    if (!imageList.isEmpty()) {
      viewHolder.setImage(imageList.get(position));
    }
    return vi;
  }
}
