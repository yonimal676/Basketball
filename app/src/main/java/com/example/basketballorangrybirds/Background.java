package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background
{
    int x = 0, y = 0;
    Bitmap backgroundBitmap;

    public Background(Resources res, int ScreenWidth, int ScreenHeight)
    {
        backgroundBitmap = BitmapFactory.decodeResource(res, R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, ScreenWidth, ScreenHeight, false);
    }
}
