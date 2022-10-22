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

public class GameView
        extends SurfaceView implements Runnable
{

    private final Paint paint1;            // precursor of throw trajectory.
    private final Paint paint2;            // axis in respect to initial point of the ball.
    private final Paint paint3;            // ball hit-box.
    private final Paint paint4;            // axis in respect to the ball.

    private final int maxBallPull; // radius of the circle which determines max dist. ball from initial point
    private float perpOpp, perpAdj;
    private boolean thrown;
    // General


    private Background background;
    private Ball ball;
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
        paint2.setStrokeWidth(3f);


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


        showAxisBool = 0;
        thrown = false;
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
        if (thrown)
        {
            ball.velocityX = (float) Math.abs(Math.cos(ball.ballAngle()) * ball.velocity); // ✓
            ball.initialVelocityY = (float) Math.abs(Math.sin(ball.ballAngle()) * ball.velocity); // ✓

            ball.HEIGHT = Math.abs(screenY - (ball.height / 2f + ball.y)) / ball.ratioPXtoM; // ✓



            ball.time = (float) (2 * ball.velocity * Math.sin(-180/Math.PI * ball.ballAngle()));

            ball.velocityY = ball.initialVelocityY - (ball.GRAVITY * ball.time);

            ball.x = ball.initialX + ball.velocityX * ball.time;

            ball.y = (float) (ball.initialY + 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time);




            //y=(tanθ0)x−[g2(v0cosθ0)2]x2.

            Log.d("key19033 i", ball.initialX +" :x <- initial -> y: "+ ball.initialY);
            Log.d("key19033 angle", "angle: " + -1 * Math.toDegrees(ball.ballAngle()));

            Log.d("key19033 VELOCITY axis",ball.velocityX + "  :x <- VELOCITY -> y:  "+ ball.velocityY);
            Log.d("key19033 VELOCITY", "VELOCITY: "+ ball.velocity);

            Log.d("key19033 time","time: "+ ball.time);
            Log.d("key19033 HEIGHT112","height of ball: "+ball.HEIGHT);
            Log.d("key19033 max height","max height: "+ ball.max_height);
            Log.d("key19033 range","range: "+ ball.range);

            Log.d("key19033 time","__________________________SWAG____________________________");


        }

    }




    public void draw () {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas


            screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint1);

            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint1);



            screenCanvas.drawLine(ball.initialX - ball.width/3f, ball.initialY - ball.height/3f,
                    ball.initialX + ball.width/3f, ball.initialY + ball.height/3f, paint2);
            screenCanvas.drawLine(ball.initialX - ball.width/3f, ball.initialY + ball.height/3f,
                    ball.initialX + ball.width/3f, ball.initialY - ball.height/3f, paint2);


            if (showAxisBool == 1)
            {
//          SHOW AXIS:
                screenCanvas.drawLine(0, ball.initialY, screenX, ball.initialY, paint2);
                screenCanvas.drawLine(ball.initialX, 0, ball.initialX, screenY, paint2);

//          SHOW BALL AXIS:
                screenCanvas.drawLine(0, ball.y + fixY(), screenX, ball.y + fixY(), paint4);
                screenCanvas.drawLine(ball.x + fixX(), 0, ball.x + fixX(), screenY, paint4);


                screenCanvas.drawLine(ball.x, ball.y, ball.x + ball.width, ball.y, paint3);
                screenCanvas.drawLine(ball.x, ball.y, ball.x, ball.y + ball.height, paint3);
                screenCanvas.drawLine(ball.x + ball.width, ball.y, ball.x + ball.width, ball.y + ball.height, paint3);
                screenCanvas.drawLine(ball.x, ball.y + ball.height, ball.x + ball.width, ball.y + ball.height, paint3);


            }

            screenCanvas.drawBitmap(showAxis, screenX / 2f - ball.width * 3, 0, paint1);

//            Log.d("key123123 MAX POINT",  ball.range /2f + " <- x ||| y -> " + ball.max_height);
//            screenCanvas.drawBitmap(ball.centerBitmap, ball.x + ball.range/2f / ball.ratioX, screenY - (ball.max_height / ball.ratioY), paint1);




            //discussion: X and Y of stop screenCanvas.DrawLine #15
            if (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) > ball.width/2f)
            {// line should only be drawn outside of the ball

                float lineStopX = (float) Math.abs((Math.cos(ball.ballAngle()) * ball.width/2f)); // similar to perpAdj
                float lineStopY = (float) Math.abs((Math.sin(ball.ballAngle()) * ball.width/2f)); // similar to perpOpp
                // issue: correcting the line with the ball #13


                switch (ball.quarter) // draw a line to the opposite corner
                {
                    case 1:
                        screenCanvas.drawLine(
                                ball.x + fixX() - lineStopX,
                                ball.y + fixY() + lineStopY,
                                ball.initialX - (ball.x - ball.initialX) - fixX(),
                                ball.initialY + (ball.initialY-ball.y) - fixY(),
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
                } // How do humans know which mushrooms are safe to eat? Trial and error, my friend.

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
                    pullToVelocity(ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY())); // ✓

                    thrown = true;
                }

                ball.setActionDown(false);

                break;

        }


        return true;
    }















    /////////////////////////////////////////////////////////////////////////
    // general functions


    public void pullToVelocity (float pullDistance) // percent of distance from max distance will result in the velocity.
    {ball.velocity = (pullDistance / maxBallPull) * ball.MAX_VELOCITY;}



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

