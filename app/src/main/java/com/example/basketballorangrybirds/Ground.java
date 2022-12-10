package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ground
{
    int x,y;
    int width, height;

    float ratioPXtoM ; // discussion: Pixels to centimeters #19 || x pixels to meters.

    Bitmap groundBitmap;


    public Ground(Resources res, int screenX, int screenY)
    {

        ratioPXtoM = screenX / 14f;

        width = screenX;

        height = (int) (0.4 * ratioPXtoM); // discussion: screen ratios #21

        x = 0;
        y = screenY - height;


        groundBitmap = BitmapFactory.decodeResource(res, R.drawable.ground);
        groundBitmap = Bitmap.createScaledBitmap(groundBitmap, width, height, false);


    }


    Rect getRektLol () {return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height));}
    //(⌐■_■)✧

}
