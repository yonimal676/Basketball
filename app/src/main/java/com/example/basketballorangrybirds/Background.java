package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background
{
    Bitmap backgroundBitmap;

    public Background(Resources res, int screenX, int screenY)
    {
        backgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenX, screenY, false);


    }
}
