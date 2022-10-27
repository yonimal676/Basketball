package com.example.basketballorangrybirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView
        extends SurfaceView implements Runnable
{

    private final Paint paint1;            // precursor of throw trajectory.
    private final Paint paint2;            // axis in respect to initial point of the ball.
    private final Paint paint3;            // ball hit-box.
    private final Paint paint4;            // axis in respect to the ball.

    private final int maxBallPull;         // radius of the circle which determines max dist. ball from initial point
    private float perpOpp, perpAdj;
    // General

    private Background background;
    private Ball ball;
    private ArrayList<Dot> dotArrayList;
    private Player player;
    private Basket basket;
    private Ground ground;
    // Objects


    private final int screenX , screenY;
    private final float ratioX , ratioY;
    private final byte SLEEP_MILLIS = 16; // byte is like int, refresh rate is (1000 / SLEEP_MILLIS)
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    byte quarterOfLaunch;
    // Technical stuff


    private Bitmap showAxis;   // screen axis in comparison to initial ball place #12
    private byte showAxisBool; // 0 -> false, 1 -> true



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

        ball = new Ball(getResources(), screenX, screenY);

        ground = new Ground(getResources(), screenX, screenY);


        maxBallPull = (int) (ball.width / 2f * 6); // the radius of max dist of the ball from the initial position



        paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setPathEffect(new DashPathEffect(new float[]{30, 30}, 0)); // array of ON and OFF distances,
        paint1.setStrokeWidth(3f);


        paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(6f);


        paint3 = new Paint();
        paint3.setColor(Color.RED);
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        paint3.setPathEffect(new DashPathEffect(new float[]{1, 4}, 0)); // array of ON and OFF distances,
        paint3.setStrokeWidth(3f);


        paint4 = new Paint();
        paint4.setColor(Color.WHITE);
        paint4.setStyle(Paint.Style.FILL);
        paint4.setStrokeWidth(3f);
        //ball hit-box


        showAxis = BitmapFactory.decodeResource(getResources(), R.drawable.play_btn);
        showAxis = Bitmap.createScaledBitmap(showAxis, ball.width * 3, ball.height * 3, false);


        showAxisBool = 1;
        ball.thrown = false;

        dotArrayList = new ArrayList<>();


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
        if ( ! ball.thrown && ball.getActionDown())
            quarterOfLaunch = ball.quarter;

        if (ball.thrown)
        {
            ball.HEIGHT = Math.abs(screenY - (ball.height / 2f + ball.y)) * ball.ratioPXtoM; // ✓


            ball.range = ball.velocityX * ball.time;
            ball.velocityY = ball.initialVelocityY - (ball.GRAVITY * ball.time);

            if (ball.velocityY == 0 || ball.x >= ball.range * ball.ratioPXtoM)
                ball.velocityY *= -1;

            ball.max_height = (float) (ball.HEIGHT + ball.velocity * ball.velocity * Math.sin(ball.ballAngle() * Math.sin(ball.ballAngle())
                    / (2 * ball.GRAVITY)));

            // Log.d("key19033 time elapsed","time elapsed: "+ ball.time);


            switch (ball.quarter) // discussion: From where has the ball been thrown? #24
            {
                case 1:
                    ball.GRAVITY = -1 * Math.abs(ball.GRAVITY);
                    ball.x = ball.initialX - ball.velocityX * ball.time - fixX(); // to the left
                    ball.y = (float) (ball.initialY + 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time) - fixY();
                    break;

                case 2:
                    ball.GRAVITY = -1 * Math.abs(ball.GRAVITY);
                    ball.x = ball.initialX + ball.velocityX * ball.time - fixX(); // to the left
                    ball.y = (float) (ball.initialY + 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time) - fixY();
                    break;

                case 3:
                    ball.GRAVITY = Math.abs(ball.GRAVITY);
                    ball.x = ball.initialX + ball.velocityX * ball.time - fixX(); // to the left
                    ball.y = (float) (ball.initialY - 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time) - fixY();
                    break;

                case 4:
                    ball.GRAVITY = Math.abs(ball.GRAVITY);
                    ball.x = ball.initialX - ball.velocityX * ball.time - fixX(); // to the left
                    ball.y = (float) (ball.initialY - 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time) - fixY();
            }


//                Log.d("key19033293", "yes, this is the first run.");



                /*
            else
                ball.y = (float) (ball.initialY + 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time) - fixY();
*/

//            dotArrayList.add(new Dot(ball.x + fixX(), ball.y + fixY(), getResources()));



/*
        if (ball.thrown) // discussion: physics #17
        {
            //discussion: physics #17 (scroll down)
            // TODO: 26/10/2022  :  ball.thrown should be false at some point here. ↓
            if (ball.collision(ground.height).equals("bottom")) {
                ball.velocityY *= -1;
            }




            // TODO: 26/10/2022 until here.





        */



        }// if (thrown)
    }




    public void draw () {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas


            screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint1);//background

            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint1);//ball

            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint1);//ground






            if (showAxisBool == 1)
            {
//          SHOW AXIS:
                screenCanvas.drawLine(0, ball.initialY, screenX, ball.initialY, paint2);
                screenCanvas.drawLine(ball.initialX, 0, ball.initialX, screenY, paint2);

//          SHOW BALL AXIS:
                screenCanvas.drawLine(0, ball.y + fixY(), screenX, ball.y + fixY(), paint4);
                screenCanvas.drawLine(ball.x + fixX(), 0, ball.x + fixX(), screenY, paint4);

//              SHOW BALL HITBOX
                screenCanvas.drawLine(ball.x, ball.y, ball.x + ball.width, ball.y, paint3);
                screenCanvas.drawLine(ball.x, ball.y, ball.x, ball.y + ball.height, paint3);
                screenCanvas.drawLine(ball.x + ball.width, ball.y, ball.x + ball.width, ball.y + ball.height, paint3);
                screenCanvas.drawLine(ball.x, ball.y + ball.height, ball.x + ball.width, ball.y + ball.height, paint3);




                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                screenCanvas.drawText(Float.toString((float) (- 180/Math.PI * ball.ballAngle())),100,100, paint2);

            }

            screenCanvas.drawBitmap(showAxis, screenX / 2f - ball.width * 3, 0, paint1);//button to show initial axis




            if ( ! ball.thrown) // draw this line only before the ball is thrown.
                {

                //discussion: X and Y of stop screenCanvas.DrawLine #15
                if (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) > ball.width / 2f) {
                    // line should only be drawn when the ball is farther from the center
                    // than the radius of the ball, otherwise it will be drawn inside the ball itself.

                    float lineStopX = (float) Math.abs((Math.cos(ball.ballAngle()) * ball.width / 2f)); // similar to perpAdj
                    float lineStopY = (float) Math.abs((Math.sin(ball.ballAngle()) * ball.width / 2f)); // similar to perpOpp
                    // issue: correcting the line with the ball #13


                    switch (ball.quarter) // draw a line to the opposite corner
                    {
                        case 1:
                            screenCanvas.drawLine(
                                    ball.x + fixX() - lineStopX,
                                    ball.y + fixY() + lineStopY,
                                    ball.initialX - (ball.x - ball.initialX) - fixX(),
                                    ball.initialY + (ball.initialY - ball.y) - fixY(),
                                    paint1);
                            break;

                        case 2:
                            screenCanvas.drawLine(
                                    ball.x + fixX() + lineStopX,
                                    ball.y + fixY() + lineStopY,
                                    ball.initialX + (ball.initialX - ball.x) - fixX(),
                                    ball.initialY + (ball.initialY - ball.y) - fixY(),
                                    paint1);
                            break;

                        case 3:
                            screenCanvas.drawLine(
                                    ball.x + fixX() + lineStopX,
                                    ball.y + fixY() - lineStopY,
                                    ball.initialX + (ball.initialX - ball.x) - fixX(),
                                    ball.initialY - (ball.y - ball.initialY) - fixY(),
                                    paint1);
                            break;

                        case 4:
                            screenCanvas.drawLine(
                                    ball.x + fixX() - lineStopX,
                                    ball.y + fixY() - lineStopY,
                                    ball.initialX - (ball.x - ball.initialX) - fixX(),
                                    ball.initialY - (ball.y - ball.initialY) - fixY(),
                                    paint1);
                            break;
                    }

                }
            }

            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    }





    private void sleep() {
        try { Thread.sleep(SLEEP_MILLIS); }// = 16
        catch (InterruptedException e) {e.printStackTrace();}

        //count time from throw:
        if (ball.thrown)
            ball.time += 0.032f; // milliseconds don't need to be very precise because the screen sleeps 16 MS anyway.
        else
            ball.time = 0;

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

                if (ball.isTouching(event.getX(),event.getY()) && ! ball.thrown)
                    ball.setActionDown(true);                   // 'ball' has a boolean method that indicates whether the object is touched.



                if (ball.calcDistanceFromI(ball.x, ball.y) > maxBallPull)
                    if (ball.calcDistanceFromI(event.getX(), event.getY()) <= maxBallPull)
                        ball.reset();

                    if (event.getRawX() >= screenX /2f - ball.width * 3 && event.getRawX() <= screenX /2f + ball.width * 3
                    && event.getRawY() >= 0 && event.getRawY() <= ball.height * 3)
                {
                    if (showAxisBool == 0)
                        showAxisBool = 1;
                    else
                        showAxisBool = 0;
                }

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
                        ball.setPosition(event.getX(), event.getY());



                    else // issue: finger drag outside of radius #4
                    {

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
                    }// outside drag-able circle
//                    Log.d("quarter", "" + ball.quarter);

                }// if touched the ball


            }//ACTION_MOVE

            break;



            case MotionEvent.ACTION_UP: // ended touch


                if (ball.getActionDown())
                {
                    ball.velocity = (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY() ) / maxBallPull) * ball.MAX_VELOCITY;
                    // Percent of pull * max velocity

                    ball.velocityX = (float) Math.abs(Math.cos(ball.ballAngle()) * ball.velocity); // ✓
                    ball.initialVelocityY = (float) Math.abs(Math.sin(ball.ballAngle()) * ball.velocity); // ✓
                    // Both of these values never change after the ball is thrown.



                    ball.thrown = true;





                    Log.d("key19033 i", ball.initialX +" :x <- initial -> y: "+ ball.initialY);
                    Log.d("key19033 angle", "angle: " + (float) (-1 * Math.toDegrees(ball.ballAngle())));

                    Log.d("key19033 VELOCITY axis",ball.velocityX + "  :x <- VELOCITY -> y:  "+ ball.velocityY);
                    Log.d("key19033 VELOCITY", "VELOCITY: "+ ball.velocity);

                    Log.d("key99999999999999 time","time: "+ ball.time);
                    Log.d("key19033 max time","ball.max_height: " + ball.max_height);




                    Log.d("key19033 HEIGHT112","height of ball: "+ball.HEIGHT);
                    Log.d("key19033 max height","max height: "+ ball.max_height);
                    Log.d("key19033 range","range: "+ ball.range);

                    Log.d("key19033","_____________________________________________________________________________");

                }

                ball.setActionDown(false);



                break;

        }


        return true;
    }















    /////////////////////////////////////////////////////////////////////////
    // general functions





    public void resume() // discussion: "activity lifecycle"
    {
        isPlaying = true;
        thread = new Thread(this); // -> "this" is the run() method above.
        thread.start();
    } // resume the game


    public void pause()  // discussion: "activity lifecycle"
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