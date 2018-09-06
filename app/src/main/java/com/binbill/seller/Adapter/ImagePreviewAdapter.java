package com.binbill.seller.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.binbill.seller.Constants;
import com.binbill.seller.Model.JobCopy;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shruti.vig on 8/23/18.
 */

public class ImagePreviewAdapter extends PagerAdapter {

    Context context;
    ArrayList<Uri> images;
    ArrayList<JobCopy> jobCopies;
    LayoutInflater layoutInflater;
    int mType;

    public ImagePreviewAdapter(Context context, ArrayList<?> images, int type) {
        this.context = context;

        if (type == Constants.TYPE_URI)
            this.images = (ArrayList<Uri>) images;
        else if (type == Constants.TYPE_URL)
            this.jobCopies = (ArrayList<JobCopy>) images;
        this.mType = type;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        if (mType == Constants.TYPE_URI) {
            if (images == null)
                return 0;
            else
                return images.size();
        } else {
            if (jobCopies == null)
                return 0;
            else
                return jobCopies.size();
        }
    }

    @Override
    public boolean isViewFromObject(View container, Object object) {
        return container == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View itemView = layoutInflater.inflate(R.layout.photo_view, container, false);

        final PhotoView imageView = (PhotoView) itemView.findViewById(R.id.imageView);

        if (mType == Constants.TYPE_URI) {

            String uriString = Utility.getPath(context, images.get(position));
            Picasso.get()
                    .load("file://" + uriString)
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(imageView);
        } else if (mType == Constants.TYPE_URL) {
            final String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            return response.request().newBuilder()
                                    .header("Authorization", authToken)
                                    .build();
                        }
                    }).build();

            Picasso picasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
            picasso.load(Constants.BASE_URL + jobCopies.get(position).getCopyUrl())
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .into(imageView);
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
