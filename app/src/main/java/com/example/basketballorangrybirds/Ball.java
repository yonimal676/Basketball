package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ball
{
    boolean actionDown = false;     // down => touchDown (on the screen).
    float x, y;
    float initialX, initialY;  // acts
    // as the (0,0) point.
    short width, height;  //short is like int
    Bitmap ballBitmap;

    Bitmap centerBitmap;

    byte quarter;




    public final int WEIGHT = 620; // grams
    public final float GRAVITY = 9.8f;
    public float VELOCITY;


    public Ball (Resources res, float ratioX, float ratioY, float screenX, float screenY)
    {
        final float ratioToScale = Math.max(screenY/1920, screenX/1080); // find the biggest difference in ratio to scale so that the ball is equally sized


        x = (int) (screenX / 2);             // 1/8 to the right
        y = (int) (screenY - screenY / 2); // 1 - 1/3.5 up


        width = (short) (90 * ratioToScale);
        height = (short) (90 * ratioToScale);

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
        this.y = y - height /2f;
    }

    public float angle(float Tx, float Ty) // T - Touch point || * returns a radian
    {return (float) (Math.atan2(initialY - (Ty + height/2f), initialX - (Tx + width/2f)));} // discussion: "degrees vs radians"


    double calcDistance (float x, float y)
    {return Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));} // this is the distance function




    public int Formula (int x, int y)
    {

        return 0;
    }



}


