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
    short width, height;  //short is like int
    boolean isTouch = false;     // true => touchDown (on the screen).
    Bitmap ballBitmap;


    float initialX, initialY;  // acts as the (0,0) point relative to the ball.
    float screenX, screenY;
    byte quarter;


    float velocity, velocityX, velocityY, initialVelocityY; //VELOCITY
    float time;
//    float timeAtMax;
    float max_height;
    float range; // of projectile.
    float HEIGHT;
    boolean thrown;


    final float MAX_VELOCITY; //meters per second.
    float GRAVITY;
    final float ratioPXtoM ; // discussion: Pixels to centimeters #19 || x pixels to meters.









    /*
     the middle of the rim is 4.191 meters from free throw line
     the optimal launch angle for a 6 feet person is 50.8
     the speed at which there will be a score is 28.968 kmh
      */
    public Ball (Resources res, float screenX, float screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;

        // Basketball court: 28m long  ->  screenX = half a basketball court ( 14m )
        ratioPXtoM = screenX / 14;

        /* This isn't YET coordinated with the real court size */
        x = (int) ( screenX / 8 );             // 1/8 to the right
        y = (int) ( screenY - screenY / 3.5 ); // 1 - 1/3.5 up

        // basketball diameter 24.1 cm or 0.241 meters
        width = (short) (0.241 * 2 * ratioPXtoM);
        height = (short) (0.241 * 2 * ratioPXtoM);


        initialX = x + width /2f;
        initialY = y + height /2f; // This works because the object:Ball is only initialized once.


        // Draw the ball:
        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);



        // Physics-related stuff:
        GRAVITY = 25 * ratioPXtoM;
        MAX_VELOCITY = 20 * ratioPXtoM; // also max pull | meters per second.
        time = 0;

/*
        GRAVITY = 9.8f * ratioPXtoM;
        MAX_VELOCITY = 25 * ratioPXtoM; // also max pull | meters per second.*/
    }

    void setActionDown (boolean ActionDown) {this.isTouch = ActionDown;}
    boolean getActionDown() {return isTouch;}


    Rect getRektLol () {return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height ));}
    //(⌐■_■)✧


    boolean isTouching (float x, float y) // Did touch in bounds?
    {return ((x >= this.x) && (x <= this.x + width) && y >= this.y) && (y <= this.y + height);}



    void setPosition (float x, float y)
    {
        this.x = x - width /2f; // '- width /2f' is for going from current position to touch position smoothly
        this.y = y - height /2f;
    }


    float findAngleWhenOutside(float Tx, float Ty) // T - Touch point || * returns a radian
    {return (float) (Math.atan2(initialY - Ty, initialX - Tx ));} // discussion: "degrees vs radians"


    float ballAngle() // 'findAngleWhenOutside()' isn't enough because it only updates when touch is outside max dist.
    {return (float) (Math.atan2(initialY - (y + height/2f), initialX - (x + width/2f)));}


    float calcDistanceFromI(float x, float y) // To know whether or not the ball is at max distance from i.
    {return (float) Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));}


    public void reset () // Experimental
    {
        thrown = false; // also resets time in: GameView.java -> sleep() -> if (ball.thrown) !|-> else {ball.time = 0;}

        x = initialX - width / 2f;
        y = initialY - height / 2f;
    }

    public String collision (int groundHeight) // we need this for bottom collision
    {

        if (y + height >= screenY - groundHeight)
            return "bottom";

        if (x + width >=  screenX)
            return "right";

        if (x <= 0)
            return "left";



        return "none";
    }

}


