package com.example.basketballorangrybirds;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
//short min value is -32,768 and max value is 32,767 (inclusive).
//byte min value is -128 and max value is 127 (inclusive)

public class Ball
{


    float orgIX, orgIY; // experimental.
    float percentOfPull;
    short maxBallPull;         // radius of the circle which determines max dist. ball from initial point

    float x, y;
    short width, height;  //short is like int
    boolean isTouched = false;     // true => touchDown (on the screen).
    Bitmap ballBitmap;

    float prevX, prevY;
    float colX, colY; // collision x, y coordinates.


    float initialX, initialY;  // acts as the (0,0) point relative to the ball.
    float screenX, screenY;
    byte quarter;
    float angle;


    float removeBall_time;
    float v, vx, vy, v0y; // velocity, velocityX, velocityY, initialVelocityY
    float time;
    float range; // of projectile.
    float HEIGHT;
    boolean thrown;

    final float MAX_VELOCITY; //meters per second.
    float GRAVITY;
    final float ratioMtoPX; // discussion: Pixels to centimeters #19 || x pixels to meters.

    byte collision; // 0 = no collision, 1 = right wall, 2 = left wall, 3 = ground.
    byte howManyCols;
    byte floorHitCount;
    // discussion: Changing Direction #34 -> number 1.

    ArrayList<Float> dotArrayListX;
    ArrayList<Float> dotArrayListY;



    public Ball (Resources res, float screenX, float screenY)
    {
        this.screenX = screenX;
        this.screenY = screenY;

        // Basketball court: 28m long  ->  screenX = half a basketball court ( 14m )
        ratioMtoPX = screenX / 14; // TODO: remember that if you scale this up, the ball will NOT move in the same ratio!!

        x = (int) ( screenX - 2 * ratioMtoPX); // two meters to the left of the edge.
        y = (int) ( screenY - 2 * ratioMtoPX); // two meters up from the bottom.

/*        x = (int) ( screenX /2 );
        y = (int) ( screenY /2);*/


        prevX = x;
        prevY = y;

        // basketball diameter 24.1 cm or 0.241 meters * 2 for better looks
        width = (short) (0.241 * 2 * ratioMtoPX);
        height = (short) (0.241 * 2 * ratioMtoPX);


        initialX = x + width /2f;
        initialY = y + height /2f; // This is needed because the object:Ball is only initialized once.

        orgIX = initialX;
        orgIY = initialY;

        // Draw the ball:
        ballBitmap = BitmapFactory.decodeResource(res, R.drawable.basketball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, width, height, false);



        // Physics-related stuff: todo -> NOT NEGATIVE GRAVITY,  also:   ball.vy = ball.v0y + ball.GRAVITY * ball.time;
        GRAVITY =  9.8f * 6.3f * ratioMtoPX; // should be negative due to the earth's gravity pulling it downwards.
        MAX_VELOCITY = 21 * 1.4f * ratioMtoPX; // also max pull | meters per second.
        time = 0;

        howManyCols = 0;
        floorHitCount = 0;
        removeBall_time = 0;

        collision = 0; // = no collision.
        percentOfPull = 0;

        dotArrayListX = new ArrayList<>();
        dotArrayListY = new ArrayList<>();

    }


    boolean isTouching (float x, float y) // Did touch in bounds?
    {return ((x >= this.x) && (x <= this.x + width) && y >= this.y) && (y <= this.y + height);}



    void setPosition (float x, float y) {
        this.x = x - width /2f; // '- width /2f' is for going from current position to touch position smoothly
        this.y = y - height /2f;
    }


    float ballAngle() // from initial position.
    {return angle = (float) (Math.atan2(initialY - (y + height / 2f), initialX - (x + width / 2f)));}
/*
    The atan() and atan2() functions calculate the arc-tangent of x and y/x, respectively.

    The atan() function returns a value in the range -π/2 to π/2 radians.
    The atan2() function returns a value in the range -π to π radians.
 */

    float findAngleWhenOutside(float Tx, float Ty) // Find Angle When Finger Is Touching Outside, T - Touch point || * returns a radian
    {return (float) (Math.atan2(initialY - Ty, initialX - Tx ));} // discussion: "degrees vs radians"


    float calcDistanceFromI(float x, float y) // To know whether or not the ball is at max distance from i.
    {return (float) Math.sqrt((orgIX - x) * (orgIX - x) + (orgIY - y) * (orgIY - y));}



    public void reset () // for dev (me haha... ಥ_ಥ )
    {
        thrown = false; // also resets time in: GameView.java -> sleep() -> if (ball.thrown) !|-> else {ball.time = 0;}
        collision = 0; // = no collision.
        colX = 0;
        colY = 0;


        howManyCols = 0;
        floorHitCount = 0;

        initialX = orgIX;
        initialY = orgIY;

        x = initialX - width / 2f;
        y = initialY - height / 2f;

        prevX = x;
        prevY = y;

        angle = 0;
        removeBall_time = 0;

        dotArrayListX.clear();
        dotArrayListY.clear(); // erase dots.
    }




    public byte didCollide(short groundHeight)
    {
        if (x + (x - prevX) >= screenX /*&& ! (y + height + (y - prevY) >= screenY - groundHeight)*/)  // ball touches the right of the screen.
        {
            if (collision != 1)
            {
                howManyCols++;

                colX = x + (x - prevX);
                colY = y + height / 2f;

                /*vx *= percentOfPull;
                vy *= percentOfPull;*/

                vx = -1 * Math.abs(vx);

                return collision = 1;
            }
        }

        else if (x + width - (prevX - x) <= 0 /*&& ! (y + height + (y - prevY) >= screenY - groundHeight)*/)  // ball touches the left of the screen.
        {
            if (collision != 2)
            {
                howManyCols++;

                colX = x - (prevX - x);
                colY = y + height / 2f;

                /*vx *= percentOfPull;
                vy *= percentOfPull;*/

                vx = Math.abs(vx);

                return collision = 2;

            }
        }



        else if (y + height + (y - prevY) >= screenY - groundHeight)  // ball touches ground.
        {

            if (collision / 10 != 3)
            {
                howManyCols++;
                floorHitCount++;

                colX = x + width /2f;
                colY = y + height;


/*                vx *= percentOfPull;
                vy *= percentOfPull;*/


                for(int i = 1; i <= floorHitCount; i++)
                    vy = -1 * percentOfPull * Math.abs(vy); //<- percentOfPull should be here (in negative)

                vx *= percentOfPull;

                return collision = (byte) (3 * 10 + floorHitCount);

            }
        }//TODO : do col axis update after collision?


        else if (howManyCols == 0)
            return collision = 0; // no collision.

        return collision;
    }



}


