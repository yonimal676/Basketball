package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

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





    final float WEIGHT; // kilo

    final float GRAVITY;

    float velocity, velocityX, velocityY; //VELOCITY

    float MAX_VELOCITY; //kmh

    float time;

    float max_height;

    float range;

    float HEIGHT;
    // acceleration = 0.

    float PIXEL_TO_METER_RATIO; // discussion: Pixels to meters #19




    public Ball (Resources res, float ratioX, float ratioY, float screenX, float screenY) {
        final float ratioToScale = Math.max(screenY / 1920, screenX / 1080); // find the biggest difference in ratio to scale so that the ball is equally sized


        x = (int) (screenX / 8);             // 1/8 to the right
        y = (int) (screenY - screenY / 3.5); // 1 - 1/3.5 up


        width = (short) (50 * ratioToScale);
        height = (short) (50 * ratioToScale);

        radius = width / 2f;


        initialX = x + width / 2f;
        initialY = y + height / 2f;


        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);

        centerBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        centerBitmap = Bitmap.createScaledBitmap(centerBitmap, 10, 10, false);


        PIXEL_TO_METER_RATIO = 139.34425f;
        GRAVITY = 9.8f;
        WEIGHT = 0.62f;
        MAX_VELOCITY = 75.6f;
        HEIGHT = (screenY - (y + height/2f)) / PIXEL_TO_METER_RATIO;

//        Log.d("key1903311" , "" + (screenY - initialY) / 1.83f);

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



    float calcDistanceFromI(float x, float y)
    {return (float) Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));} // this is the distance function



    float m () // slope of initial point to ball point.
    {return (initialY - (y+height/2f)) / (initialX - (x+width/2f));}


    void formulas () // velocity had already been calculated in GameView.pullToVelocity()
    {
        velocityX = (float) Math.abs(Math.cos(-1 * Math.toDegrees(ballAngle())) * velocity);
        velocityY = (float) Math.abs(Math.sin(-1 * Math.toDegrees(ballAngle())) * velocity);


        time = (float) ((velocityY + Math.sqrt(velocityY*velocityY + 2*GRAVITY* HEIGHT)) / GRAVITY);


        max_height = HEIGHT + ( velocityY * velocityY / (2 * GRAVITY)) / PIXEL_TO_METER_RATIO;


        range = (float) ((velocityX * (velocityY + Math.sqrt(velocityY*velocityY + 2*GRAVITY* HEIGHT)) / GRAVITY)) / PIXEL_TO_METER_RATIO;







        Log.d("key19033 VELOCITY axis",velocityX + " <- x || y -> "+ velocityY + " ||  VELOCITY: " + velocity);

    /*    Log.d("key19033 time",time + "");

        Log.d("key19033 HEIGHT112",HEIGHT + "");

        Log.d("key19033 max height",max_height + "");

        Log.d("key19033 range",range + "");*/

    }

    // WITH ELEVATION OF 1.83m

    //Horizontal velocity component: Vx = V * cos(α)
    //Vertical velocity component: Vy = V * sin(α)

    //Time of flight: t = [Vy + √(Vy² + 2 * g * h)] / g

    //Maximum height: max_height = h + Vy² / (2 * g)

    //Range of the projectile: R = Vx * [Vy + √(Vy² + 2 * g * h)] / g
}


