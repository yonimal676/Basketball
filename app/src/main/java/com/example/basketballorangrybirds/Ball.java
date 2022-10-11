package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ball
{
    //short min value is -32,768 and max value is 32,767 (inclusive).
    //byte min value is -128 and max value is 127 (inclusive)

    boolean actionDown = false;     // down => touchDown (on the screen).
    float x, y;
    float initialX, initialY;  // acts
    // as the (0,0) point.
    short width, height;  //short is like int
    Bitmap ballBitmap;

    Bitmap centerBitmap;

    byte quarter;

    float angle;




    final float WEIGHT = 0.62f; // kilo
    final float GRAVITY = 9.807f;
    float V, Vx, Vy; // max:  21 meters per second || 75.6 kmh
    float t;

    // acceleration = 0.


    public Ball (Resources res, float ratioX, float ratioY, float screenX, float screenY)
    {
        final float ratioToScale = Math.max(screenY/1920, screenX/1080); // find the biggest difference in ratio to scale so that the ball is equally sized


        x = (int) (screenX / 8);             // 1/8 to the right
        y = (int) (screenY - screenY / 3.5); // 1 - 1/3.5 up


        width = (short) (50 * ratioToScale);
        height = (short) (50 * ratioToScale);


        initialX = x + width/2f;
        initialY = y + height/2f;


        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);

        centerBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        centerBitmap = Bitmap.createScaledBitmap(centerBitmap,10,10,false);

    }

    void setActionDown (boolean ActionDown) {this.actionDown = ActionDown;}
    boolean getActionDown() {return actionDown;}


    Rect getRektLol () {return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height ));}
    //(⌐■_■)✧


    boolean isTouching (float x, float y) // Did touch in bounds?
    {return ((x >= this.x) && (x <= this.x + width) && y >= this.y) && (y <= this.y + height);}



    void setPosition (float x, float y)
    {
        this.x = x - width /2f;
        this.y = y - height /2f;
    }


    float findAngle(float Tx, float Ty) // T - Touch point || * returns a radian
    {
        this.angle = (float) (Math.atan2(initialY - (Ty + height/2f), initialX - (Tx + width/2f)));
        return (float) (Math.atan2(initialY - (Ty + height/2f), initialX - (Tx + width/2f)));
    } // discussion: "degrees vs radians"


    double calcDistance (float x, float y)
    {return Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));} // this is the distance function



    float m () // slope of initial point to ball point.
    {return -1 * (initialY - (y+height/2f)) / (initialX - (x+width/2f));}




    String functionItoB ()
    {
        float num = m() * initialX - initialY;
        return "y="+m()+"x"+num;
    }



    public int Formula (int x, int y)
    {

        return 0;
    }



}


