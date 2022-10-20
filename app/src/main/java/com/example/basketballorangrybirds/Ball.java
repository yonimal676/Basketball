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



    float screenX, screenY;


    final float WEIGHT; // kilo

    float GRAVITY;

    float velocity, velocityX, velocityY; //VELOCITY

    float MAX_VELOCITY; //kmh

    float time;

    float max_height;

    float range;

    float HEIGHT;

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


        // screenX = half a basketball court which is 14 meters.
        ratioPXtoM = screenX / 14 ;
        /* TOO TINY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/


        //Basketball court: 28m long  ->  screenX = 14m
        /*x = (int) ( screenX / 5.2578f );
        y = (int) ( screenY - (1.83 * ratioPXtoM) );*/

        x = (int) ( screenX / 8 );
        y = (int) ( screenY - screenY / 3.5 );



        initialX = x + width / 2f;
        initialY = y + height / 2f;




        // basketball diameter 75 cm
        width = (short) (0.75 * ratioPXtoM);
        height = (short) (0.75 * ratioPXtoM);

        radius = width / 2f; // which is 24 cm in circumference. (2 * radius)




        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);

        centerBitmap = BitmapFactory.decodeResource(res, R.drawable.black);
        centerBitmap = Bitmap.createScaledBitmap(centerBitmap, 10, 10, false);











        GRAVITY = 9.8f;
        WEIGHT = 0.62f;
        MAX_VELOCITY = 75.6f;
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

        velocityX = (float) Math.abs(Math.cos(ballAngle()) * velocity); // ✓
        velocityY = (float) Math.abs(Math.sin(ballAngle()) * velocity); // ✓

        HEIGHT = Math.abs(screenY - (height / 2f + y)) / ratioPXtoM; // ✓




        // time of flight: Vy * t – g * t² / 2 = 0
        // ( -g * t² / 2) + velocityY * t = 0   / * 2
        // -g * t² + 2 * velocityY * t = 0
        // a -> -g | b -> 2 * velocityY | c -> 0


        float tempPLUS = (float) (((-2 * velocityY) + Math.sqrt( (2 * velocityY) * (2 * velocityY))) / 2 * -GRAVITY); // -4 * -g * 0 = 0
        float tempMINUS = (float) (((-2 * velocityY) - Math.sqrt( (2 * velocityY) * (2 * velocityY))) / 2 * -GRAVITY); // -4 * -g * 0 = 0


        if (tempPLUS >= 0)
            time = tempPLUS;
        else if (tempMINUS > 0)
            time = tempMINUS;



        /*
        max_height = (HEIGHT + velocityY * velocityY / (2 * GRAVITY * HEIGHT)) * ratioX;
        range = (float) (velocityX * (velocityY + Math.sqrt(velocityY * velocityY + 2 * GRAVITY * HEIGHT)) / GRAVITY) * ratioX;
        */








        Log.d("key19033 ", "Grav " + GRAVITY);
        Log.d("key19033 i", initialX +" :x <- initial -> y: "+ initialY);
        Log.d("key19033 VELOCITY axis",velocityX + " <- x || y -> "+ velocityY);
        Log.d("key19033 VELOCITY", "VELOCITY: "+ velocity);
        Log.d("key19033 angle", "angle: " + -1 * Math.toDegrees(ballAngle()));
        Log.d("key19033 time","time: "+ time);
        Log.d("key19033 HEIGHT112","HEIGHT: "+ HEIGHT);
        Log.d("key19033 max height","max_height: "+ max_height);
        Log.d("key19033 range","range: "+ range);

    }

    //Horizontal velocity component: Vx = V * cos(α)
    //Vertical velocity component: Vy = V * sin(α)
    //Vertical velocity is calculated as follows: Vy – g * t

    //Range of the projectile: R = Vx * [Vy + √(Vy² + 2 * g * h)] / g    ||    R = V² * sin(2α) / g   ||  Vx * time

    //Maximum height: max_height = h + Vy² / (2 * g)

    // time of flight: Vy * t – g * t² / 2 = 0

}


