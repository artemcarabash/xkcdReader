package utils;

import android.widget.ImageView;

import com.xkcd.haufe.xkcdviewer.R;
import com.squareup.picasso.Picasso;

public class PicassoImageLoadingService {

    public void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url).placeholder(R.mipmap.ic_launcher).into(imageView);
    }

}
