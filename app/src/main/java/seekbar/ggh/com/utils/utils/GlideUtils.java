package seekbar.ggh.com.utils.utils;//package com.blankj.subutil.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import seekbar.ggh.com.myapplication.R;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/05/16
 *     desc  :
 * </pre>
 */
public final class GlideUtils {



    public static void setCircleImage(Context context, String url, ImageView view) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_apk)
                .error(R.drawable.ic_apk)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop().dontAnimate();
        Glide.with(context).load(url).apply(requestOptions).into(view);
    }

    public static void setImage(Context context, String url, ImageView view) {
        if (url.endsWith(".svg") || url.endsWith(".SVG")) {
            setSvgImage(context, url, view);
            return;
        }

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_unknow)
                .error(R.drawable.ic_unknow).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).dontAnimate();
        Glide.with(context).load(url).apply(requestOptions).into(view);
    }

    private static void setSvgImage(Context context, String url, ImageView view) {
//        Glide.with(context)
//                .as(PictureDrawable.class)
//                .error(R.drawable.ic_add).load(url).into(view);
    }
}
