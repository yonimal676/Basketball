package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ball
{
    boolean actionDown = false;     // down => touchDown (on the screen).
    float screenY;
    float x, y;
    float initialX, initialY;  // acts as the (0,0) point.
    int width, height;
    Bitmap ballBitmap;

    Bitmap centerBitmap;




    public final int WEIGHT = 620; // grams
    public final float GRAVITY = 9.8f;
    public float VELOCITY;


    public Ball (Resources res, float ratioX, float ratioY, float screenX, float screenY)
    {
        this.screenY = screenY;
        final float ratioToScale = ratioY * ratioX; /********/

        x = (int) (screenX / 2); // 1/8 to the right
        y = (int) (screenY - screenY / 2); // 1 - 1/3.5 up


        width = (int) (300 * ratioToScale);
        height = (int) (300 * ratioToScale);

        initialX = x + width/2f;
        initialY = y + height/2f;



        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);

        centerBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        centerBitmap = Bitmap.createScaledBitmap(centerBitmap,10,10,false);

    }
    public void setActionDown (boolean ActionDown) {this.actionDown = ActionDown;}

    public boolean getActionDown() {return actionDown;}


    public Rect getRektLol () {return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height ));}
    //(⌐■_■)✧


    public boolean isTouching (float x, float y) // Did touch in bounds?
    {return ((x >= this.x) && (x <= this.x + width) && y >= this.y) && (y <= this.y + height);}

    public void setPosition (float x, float y)
    {
        this.x = x - width /2f;
        this.y = /*screenY -*/ y - height /2f;
    }

    public float angle(float Tx, float Ty) // T - Touch point || * returns a radian
    {return (float) (Math.atan2(initialY - Ty, initialX - Tx));} // issue NO.2


    double calcDistance (float x, float y)
    {return Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));} // this is the distance function




    public int Formula (int x, int y)
    {

        return 0;
    }






}


