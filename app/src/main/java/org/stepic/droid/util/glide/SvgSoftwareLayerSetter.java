package org.stepic.droid.util.glide;


import android.annotation.TargetApi;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

/**
 * UnitProgressListener which updates the {@link ImageView} to be software rendered, because
 * {@link com.caverock.androidsvg.SVG SVG}/{@link android.graphics.Picture Picture} can't render on
 * a hardware backed {@link android.graphics.Canvas Canvas}.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SvgSoftwareLayerSetter implements RequestListener<PictureDrawable> {

    @Override
    public boolean onLoadFailed(GlideException e, Object model, Target<PictureDrawable> target,
                                boolean isFirstResource) {
        if (target instanceof ImageViewTarget) {
            ImageView view = ((ImageViewTarget<?>) target).getView();
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null);
        }
        return false;
    }

    @Override
    public boolean onResourceReady(PictureDrawable resource, Object model,
                                   Target<PictureDrawable> target, DataSource dataSource, boolean isFirstResource) {
        if (target instanceof ImageViewTarget) {
            ImageView view = ((ImageViewTarget<?>) target).getView();
            view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        }
        return false;
    }
}