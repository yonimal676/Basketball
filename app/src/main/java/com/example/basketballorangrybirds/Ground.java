package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ground
{
    int x,y;
    int width, height;

    Bitmap groundBitmap;


    public Ground(Resources res, int screenX, int screenY)
    {
        width = screenX;
        height = (int) ((screenY /4f) / 3.5f); // discussion: screen ratios #21

        x = 0;
        y = screenY;


        groundBitmap = BitmapFactory.decodeResource(res, R.drawable.ground);
        groundBitmap = Bitmap.createScaledBitmap(groundBitmap, width, height, false);


    }


    Rect getRektLol () {return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height));}
    //(⌐■_■)✧

}
