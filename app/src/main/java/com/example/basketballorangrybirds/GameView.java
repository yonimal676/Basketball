package com.example.basketballorangrybirds;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.Timer;

public class GameView
        extends SurfaceView implements Runnable
{


    private final Paint paint;            // The paint is the thing that "draws"// the image/Bitmap.
    private final int maxBallPull; // radius of the circle which determines max dist. ball from initial point
    private float perpOpp, perpAdj;
    boolean isOnEdge;
    // General


    private Background background;
    private Ball ball;
    private Player player;
    private Basket basket;
    // Objects

    private final int screenX , screenY;
    private final float ratioX , ratioY;
    private final byte SLEEP_MILLIS = 16; // byte is like int
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    // Technical stuff




    public GameView(GameActivity activity, int screenX,  int screenY)
    {
        super(activity);

        this.activity = activity;
        gameActivityContext = this.activity;

        this.screenX = screenX;
        this.screenY = screenY;

        ratioX = 1080f / screenX; // side to side
        ratioY = 1920f / screenY; // top to bottom


        isPlaying = true;


        background = new Background(getResources(), screenX, screenY);

        ball = new Ball(getResources(), ratioX, ratioY, screenX, screenY);


        maxBallPull = (int) (200 * ratioX * ratioY); // the radius of max dist of the ball from the initial position



        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{30, 30}, 0)); // array of ON and OFF distances,
        paint.setStrokeWidth(6f);
        // ↑  This segment is for the precursor of the throw.
    }




    @Override
    public void run()
    {
        while (isPlaying)
        { // run only if we play.

            update();//The screen
            draw();//The components
            sleep();//To render
        }
    }



    public void update ()
    {
        //ball.Formula(x,y,velocity)
    }




    public void draw () {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas


            screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint);


            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint);
            screenCanvas.drawBitmap(ball.centerBitmap, ball.initialX,ball.initialY , paint);

//          SHOW AXIS:
            screenCanvas.drawLine(0,ball.initialY,screenX,ball.initialY, paint);
            screenCanvas.drawLine(ball.initialX,0,ball.initialX,screenY, paint);



            //discussion: X and Y of stop screenCanvas.DrawLine #15

            if (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) > ball.radius) {
                ball.lineStopX = (float) Math.abs((Math.cos(ball.ballAngle()) * ball.radius));
                ball.lineStopY = (float) Math.abs((Math.sin(ball.ballAngle()) * ball.radius));


            switch (ball.quarter) // draw a line to the opposite corner
            {
                case 1:
                    screenCanvas.drawLine(
                            ball.x + fixX() - ball.lineStopX,
                            ball.y + fixY() + ball.lineStopY,
                            ball.initialX - (ball.x - ball.initialX),
                            ball.initialY + (ball.initialY-ball.y),
                            paint);
                    break;

                case 2:
                    screenCanvas.drawLine(
                            ball.x + fixX() + ball.lineStopX,
                            ball.y + fixY() + ball.lineStopY,
                            ball.initialX + (ball.initialX - ball.x),
                            ball.initialY + (ball.initialY - ball.y),
                            paint);
                    break;

                case 3:
                    screenCanvas.drawLine(
                            ball.x + fixX() + ball.lineStopX,
                            ball.y + fixY() - ball.lineStopY,
                            ball.initialX + (ball.initialX - ball.x),
                            ball.initialY - (ball.y - ball.initialY),
                            paint);
                    break;

                case 4:
                    screenCanvas.drawLine(
                            ball.x + fixX() - ball.lineStopX,
                            ball.y + fixY() - ball.lineStopY,
                            ball.initialX - (ball.x - ball.initialX),
                            ball.initialY - (ball.y - ball.initialY),
                            paint);
                    break;
            }

            }


            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    }





    private void sleep() {
        try { Thread.sleep(SLEEP_MILLIS); }// = 16
        catch (InterruptedException e) {e.printStackTrace();}
    }




    public void setBallQuarter(byte qtr)
    {ball.quarter = qtr;}


    public float fixX()
    {return ball.width/2f;}


    public float fixY()
    {return ball.height/2f;}





    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {
        switch (event.getAction()) // down/move/up
        {

            case MotionEvent.ACTION_DOWN:// started touch

                if (ball.isTouching(event.getX(),event.getY()))
                    ball.setActionDown(true);                   // 'ball' has a boolean method that indicates whether the object is touched.

                break;



            case MotionEvent.ACTION_MOVE: // pressed and moving
            {

                if (ball.getActionDown()) // if touched the ball
                {

                    float angle_of_touch = ball.findAngleWhenOutside(event.getX(), event.getY()); // also sets ball.angle


                    if (Math.abs(180 / Math.PI * angle_of_touch) >= 90)
                    {
                        if (180 / Math.PI * angle_of_touch >= 0)
                            setBallQuarter((byte) 1); // top right corner
                        else
                            setBallQuarter((byte) 4); // bottom right corner
                    }
                    else
                    {
                        if (180 / Math.PI * angle_of_touch >= 0)
                            setBallQuarter((byte) 2); // top left corner

                        else
                            setBallQuarter((byte) 3); // bottom left corner
                    }




                    if (ball.calcDistanceFromI(event.getX(), event.getY()) < maxBallPull) // in drag-able circle
                    {
                        ball.setPosition(event.getX(), event.getY());
//                        pullin =
                    }




                    else // issue: finger drag outside of radius #4
                    {
//                        pulling = 75.6 kmh


                        perpOpp = (float) Math.abs(Math.sin(angle_of_touch) * maxBallPull); // for y
                        perpAdj = (float) Math.abs(Math.cos(angle_of_touch) * maxBallPull); // for x
                        //  perp- perpendicular (ניצב), adj- adjacent (ליד), opp- opposite (מול)



                        switch (ball.quarter)
                        {
                            case 1: ball.setPosition(ball.initialX + perpAdj, ball.initialY - perpOpp); break;

                            case 2: ball.setPosition(ball.initialX - perpAdj, ball.initialY - perpOpp); break;

                            case 3: ball.setPosition(ball.initialX - perpAdj, ball.initialY + perpOpp); break;

                            case 4: ball.setPosition(ball.initialX + perpAdj, ball.initialY + perpOpp); break;
                        }



                        isOnEdge = true;


                    }// outside drag-able circle
                    Log.d("quarter", "" + ball.quarter);

                }// if touched the ball

            }//ACTION_MOVE

            break;



            case MotionEvent.ACTION_UP: // ended touch

                ball.setActionDown(false);

                Timer timer = new Timer(); // or something like this.


                // start timer for calculating the speed of the ball.
                break;

        }


        return true;
    }















    /////////////////////////////////////////////////////////////////////////
    // general functions



    public void resume() {// discussion: "activity lifecycle"

        isPlaying = true;
        thread = new Thread(this); // -> "this" is the run() method above.
        thread.start();
    } // resume the game


    public void pause()
    {
        try {
            isPlaying = false;
            thread.join(); // join = stop
            Thread.sleep(100);

//            activity.PauseMenu();
        }
        catch (InterruptedException e) {e.printStackTrace();}
    }




    public Context getGameActivityContext() {
        return gameActivityContext;
    }
}

