package com.example.valchyshyn.photogallery.viewHolder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.valchyshyn.photogallery.R;

/**
 * Created by valchyshyn on 14.02.17.
 */

public class GalleryViewHolder extends RecyclerView.ViewHolder {

  private ImageView galleryImage;
  private Context context;

  public GalleryViewHolder(View itemView, Context context) {
    super(itemView);
    this.context = context;
    galleryImage = (ImageView) itemView.findViewById(R.id.gallery_image);
  }

  public void setImage(Uri uri) {
    Glide
        .with(context)
        .load(uri)
        .thumbnail(0.7f)
        .error(R.drawable.ic_error_black_24dp)
        .into(galleryImage);
  }
}
