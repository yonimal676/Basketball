package com.example.basketballorangrybirds;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class Ball
{
    boolean actionDown = false;     // down => touchDown (on the screen).

    int x,y, initialX, initialY;
    int width, height;
    Bitmap ballBitmap;

    float hypo , perpTop, perpLeft; // hypotenuse (יתר) , perpendicular (ניצב)

    public Ball (Resources res, float ratioX, float ratioY, int screenX, int screenY)
    {
        int num = (int) screenX / screenY; // I'm clever tbh

        x = (int) (500 * ratioX);
        y = (int) ((screenY - 650) * ratioY);

        initialX = x;
        initialY = y;

        width = (int) (50 * num);
        height = (int) (50 * num);

        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);

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
    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
        // Life is good ヽ(✿ﾟ▽ﾟ)ノ (only when your code works)
    }


    public float calcThrowAngle () // omg this was so easy
    {
        perpTop = initialX - x;
        perpLeft = y - initialY;

        return (float) ((180/Math.PI) * (Math.atan(perpLeft / perpTop))); // 180 / pi  = 1 radian = ∡∅
    }

    public int[] Formula (int x, int y)
    {

        return new int[0];
    }






}


