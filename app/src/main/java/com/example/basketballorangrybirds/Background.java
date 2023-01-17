package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background
{
    Bitmap backgroundBitmap, devBackgroundBitmap;

    public Background(Resources res, int screenX, int screenY)
    {
        backgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.alt_background);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenX + 100, screenY, false);

        devBackgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        devBackgroundBitmap = Bitmap.createScaledBitmap(devBackgroundBitmap, screenX, screenY, false);


    }
}
