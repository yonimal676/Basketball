package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;

public class Ground
{
    int x,y;
    int width, height;

    Bitmap groundBitmap;


    public Ground(Resources res, int screenX, int screenY)
    {
        this.width = screenX;
        this.height = screenY / 8;

        //BitmapFactory...
        //
    }
}
