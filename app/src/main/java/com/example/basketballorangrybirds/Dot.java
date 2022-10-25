package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Dot
{
    float x, y;
    short width, height;
    Bitmap dotBitmap;

    public Dot (float x, float y, Resources res)
    {
        width = 10;
        height = 10;

        dotBitmap = BitmapFactory.decodeResource(res, R.drawable.white);
        dotBitmap = Bitmap.createScaledBitmap(dotBitmap, width, height, false);
    }
}
