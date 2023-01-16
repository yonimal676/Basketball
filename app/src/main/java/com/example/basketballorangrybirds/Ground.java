package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Ground
{
    short x,y;
    short width, height;

    float ratioPXtoM ; // discussion: Pixels to centimeters #19 || x pixels to meters.

    Bitmap groundBitmap;


    public Ground(Resources res, int screenX, int screenY)
    {

        ratioPXtoM = screenX / 14f;

        width = (short) screenX;

        height = (short) (0.4 * ratioPXtoM); // discussion: screen ratios #21

        x = 0;
        y = (short) (screenY - height);


        groundBitmap = BitmapFactory.decodeResource(res, R.drawable.ground);
        groundBitmap = Bitmap.createScaledBitmap(groundBitmap, width, height, false);


    }//(⌐■_■)✧

}
