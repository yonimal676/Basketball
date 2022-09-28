package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ball
{
    boolean actionDown = false;     // down => touchDown (on the screen).

    int x,y, initialX, initialY;
    int width, height;
    Bitmap ballBitmap;

    Bitmap centerBitmap;

    public final int WEIGHT = 620; // grams

    float hypo , perpTop, perpLeft; // hypotenuse (יתר) , perpendicular (ניצב)

    public Ball (Resources res, float ratioX, float ratioY, int screenX, int screenY)
    {
        int num = screenX / screenY; // I'm clever tbh

        x = (int) (500 * ratioX);
        y = (int) ((screenY - 650) * ratioY);

        width = 50 * num;
        height = 50 * num;

        initialX = x + width/2;
        initialY = y + height/2;

        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);

        centerBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        centerBitmap = Bitmap.createScaledBitmap(centerBitmap,10,10,false);

    }
    public void setActionDown (boolean ActionDown)
    {
        this.actionDown = ActionDown;
    }

    public boolean getActionDown()
    {
        return actionDown;
    }

    Rect getRect () {
        return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height ));
    }

    public boolean touched (float x, float y) // Did touch in bounds?
    {
        boolean isXinside = (x >= this.x) && (x <= this.x +200);
        boolean isYinside = (y >= this.y) && (y <= this.y +200);

        return isXinside && isYinside;
    }

    public void setPosition (int x, int y)
    {
        this.x = x;
        this.y = y;
    }


    public float calcThrowAngle () /* try to explain this to a 6 y/o */
    {
        perpTop = Math.abs(initialX - x) + width/2;
        perpLeft = Math.abs(y - initialY) + height/2;

        return (float) ((180/Math.PI) * (Math.atan(perpLeft / perpTop))); // 180 / pi  = 1 radian = ∡∅
    }

    public int Formula (int x, int y)
    {

        return 0;
    }






}


