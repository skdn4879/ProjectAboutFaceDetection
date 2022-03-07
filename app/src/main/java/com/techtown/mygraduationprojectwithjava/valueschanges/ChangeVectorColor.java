package com.techtown.mygraduationprojectwithjava.valueschanges;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class ChangeVectorColor {
    public void changeColor(ImageView img, int color, Context context){
        Drawable drawable = DrawableCompat.wrap(img.getDrawable());

        DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, color));
    }
}
