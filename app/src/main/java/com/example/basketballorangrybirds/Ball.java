package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;


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
    float max_height;
    float range; // of projectile.
    float HEIGHT;
    boolean thrown;


    final float MAX_VELOCITY; //meters per second.
    float GRAVITY;
    final float ratioPXtoM ; // discussion: Pixels to centimeters #19 || x pixels to meters.

    boolean didCollideWithFloor;
    boolean haveBeenTrue;

    ArrayList<Float> dotArrayListX;
    ArrayList<Float> dotArrayListY;







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
        x = (int) ( screenX - 2 * ratioPXtoM );
        y = (int) ( screenY - 2 * ratioPXtoM );

        // basketball diameter 24.1 cm or 0.241 meters
        width = (short) (0.241 * 2 * ratioPXtoM);
        height = (short) (0.241 * 2 * ratioPXtoM);


        initialX = x + width /2f;
        initialY = y + height /2f; // This works because the object:Ball is only initialized once.


        // Draw the ball:
        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);



        // Physics-related stuff:
        GRAVITY = 9.8f * ratioPXtoM;
        MAX_VELOCITY = 12 * ratioPXtoM; // also max pull | meters per second.
        time = 0;


        didCollideWithFloor = false;
        haveBeenTrue = false;

        dotArrayListX = new ArrayList<>();
        dotArrayListY = new ArrayList<>();

    }

    void setActionDown (boolean ActionDown) {this.isTouch = ActionDown;}
    boolean getActionDown() {return isTouch;}


    Rect getRect () {return new Rect((int) x,(int) y, (int) (x + width), (int) (y + height ));}


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


    float ballAngleCollision() // 'findAngleWhenOutside()' isn't enough because it only updates when touch is outside max dist.
    {
/*        if (didCollide())
            return 180 - ballAngle();*/
        return (float) (Math.atan2(initialY - (y + height/2f), initialX - (x + width/2f)));
    }


    float calcDistanceFromI(float x, float y) // To know whether or not the ball is at max distance from i.
    {return (float) Math.sqrt((initialX - x) * (initialX - x) + (initialY -y) * (initialY -y));}


    public void reset () // Experimental
    {
        thrown = false; // also resets time in: GameView.java -> sleep() -> if (ball.thrown) !|-> else {ball.time = 0;}
        didCollideWithFloor = false;
        haveBeenTrue = false;

        x = initialX - width / 2f;
        y = initialY - height / 2f;

        dotArrayListX.clear();
        dotArrayListY.clear(); // otherwise the dots would stay permanently.
    }



    public boolean didCollideWithFloor ()
    {
        if (y + height >= screenY - (ratioPXtoM * 0.4))
        {
            if ( ! haveBeenTrue)
                didCollideWithFloor = true;
            else
                haveBeenTrue = true;
        }

        return !haveBeenTrue && didCollideWithFloor;
    }
    // Makes sure that it flips once.
    // I need this because the ball will move up after hit - and because this method is called every run, I need to check like this.

}


