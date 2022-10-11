package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Ball
{
    //short min value is -32,768 and max value is 32,767 (inclusive).
    //byte min value is -128 and max value is 127 (inclusive)
    float x, y;
    float initialX, initialY;  // acts as the (0,0) point relative to the ball.
    short width, height;  //short is like int

    float lineStopX, lineStopY;
    byte quarter;
    float radius; // for better readability.

    boolean actionDown = false;     // true => touchDown (on the screen).

    Bitmap ballBitmap;

    Bitmap centerBitmap;





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

        radius = width /2f;


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


    float findAngleWhenOutside(float Tx, float Ty) // T - Touch point || * returns a radian
    {return (float) (Math.atan2(initialY - Ty, initialX - Tx ));} // discussion: "degrees vs radians"


    float ballAngle() // the previous method isn't sufficient bc it updates only when touch is outside max dist.
    {return (float) (Math.atan2(initialY - (y + height/2f), initialX - (x + width/2f)));}



    double calcDistanceFromI(float x, float y)
    {return Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));} // this is the distance function



    float m () // slope of initial point to ball point.
    {return (initialY - (y+height/2f)) / (initialX - (x+width/2f));}


}


