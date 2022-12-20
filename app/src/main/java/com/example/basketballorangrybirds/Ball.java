package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;
//short min value is -32,768 and max value is 32,767 (inclusive).
//byte min value is -128 and max value is 127 (inclusive)

public class Ball
{
    float x, y;
    float colX, colY; // collision x, y coordinates.
    short width, height;  //short is like int
    boolean isTouch = false;     // true => touchDown (on the screen).
    Bitmap ballBitmap;


    float initialX, initialY;  // acts as the (0,0) point relative to the ball.
    float screenX, screenY;
    byte quarter;
    float angle;


    float velocity, velocityX, velocityY, initialVelocityY; //VELOCITY
    float time;
    float range; // of projectile.
    float HEIGHT;
    boolean thrown;

    final float MAX_VELOCITY; //meters per second.
    float GRAVITY;
    final float ratioPXtoM ; // discussion: Pixels to centimeters #19 || x pixels to meters.

    byte collision; // 0 = no collision, 1 = right wall, 2 = left wall, 3 = ground.
    // discussion: Changing Direction #34 -> number 1.

    ArrayList<Float> dotArrayListX;
    ArrayList<Float> dotArrayListY;



    public Ball (Resources res, float screenX, float screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;

        // Basketball court: 28m long  ->  screenX = half a basketball court ( 14m )
        ratioPXtoM = screenX / 14;

        /* This isn't coordinated with real court size */
        x = (int) ( screenX - 2 * ratioPXtoM ); // two meters to the left of the edge.
        y = (int) ( screenY - 2 * ratioPXtoM ); // two meters up from the bottom.

        // basketball diameter 24.1 cm or 0.241 meters * 2 for better looks
        width = (short) (0.241 * 2 * ratioPXtoM);
        height = (short) (0.241 * 2 * ratioPXtoM);


        initialX = x + width /2f;
        initialY = y + height /2f; // This is needed because the object:Ball is only initialized once.


        // Draw the ball:
        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);



        // Physics-related stuff:
        GRAVITY = 9.8f * ratioPXtoM;
        MAX_VELOCITY = 14 * ratioPXtoM; // also max pull | meters per second.
        time = 0;

        collision = 0; // = no collision.

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

    float ballAngle()
    {return angle = (float) (Math.atan2(initialY - (y + height/2f), initialX - (x + width/2f)));}


    float findAngleWhenOutside(float Tx, float Ty) // Find Angle When Finger Is Touching Outside, T - Touch point || * returns a radian
    {return (float) (Math.atan2(initialY - Ty, initialX - Tx ));} // discussion: "degrees vs radians"


    float calcDistanceFromI(float x, float y) // To know whether or not the ball is at max distance from i.
    {return (float) Math.sqrt((initialX - x) * (initialX - x) + (initialY - y) * (initialY -y));}


    public void reset () // Experimental
    {
        thrown = false; // also resets time in: GameView.java -> sleep() -> if (ball.thrown) !|-> else {ball.time = 0;}
        collision = 0; // = no collision.
//        haveBeenTrue = false;

        x = initialX - width / 2f;
        y = initialY - height / 2f;

        dotArrayListX.clear();
        dotArrayListY.clear(); // otherwise the dots would stay permanently.
    }




    public byte didCollide(int groundHeight)
    {
        /* expect only one hit to work cuz only one colX/Y are recorded. */

        if (x + width >= screenX)  // ball touches the right of the screen.
        {
            colX = x;   colY = y;
            return collision = 1;
        }

        if (y + height >= screenY - groundHeight) { // ball touches ground.
            colX = x;   colY = y;
            return collision = 3;
        }

        if (x <= 0) { // ball touches the left of the screen.
            colX = x;   colY = y;
            return collision = 2;
        }

        return collision = 0; // no collision.
    }



}


