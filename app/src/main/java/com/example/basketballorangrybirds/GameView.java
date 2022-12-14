package com.example.basketballorangrybirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable
{

    private final Paint paint1;            // precursor of throw trajectory.
    private final Paint paint2;            // axis in respect to initial point of the ball.
    private final Paint paint3;            // ball hit-box.
    private final Paint paint4;            // axis in respect to the ball.
    private final Paint paint5;            // trajectory of the ball.
    private final Paint paint6;            // hitting point.

    private Background background;
    private Ball ball;
    private Player player;
    private Basket basket;
    private final Ground ground;
    // Objects


    private final int screenX, screenY;
    private final byte SLEEP_MILLIS = 16; // byte is like int | refresh rate is (1000 / SLEEP_MILLIS = 62.5 fps)
    private float game_time;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    private byte quarterOfLaunch;


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


        isPlaying = true;


        background = new Background(getResources(), screenX, screenY);
        ball = new Ball(getResources(), screenX, screenY);
        ground = new Ground(getResources(), screenX, screenY);


        ball.maxBallPull = (int) (screenY - ground.height - ball.initialY - ball.height); // the radius of max dist of the ball from the initial position

        game_time = 0;


        paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setPathEffect(new DashPathEffect(new float[]{30, 30}, 0)); // array of ON and OFF distances,
        paint1.setStrokeWidth(3f);


        paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(5f);


        paint3 = new Paint();
        paint3.setColor(Color.RED);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setStrokeWidth(2f);


        paint4 = new Paint();
        paint4.setColor(Color.WHITE);
        paint4.setStyle(Paint.Style.FILL);
        paint4.setStrokeWidth(3f);

        paint5 = new Paint();
        paint5.setColor(Color.BLUE);
        paint5.setStyle(Paint.Style.FILL);
        paint5.setStrokeWidth(4f);

        paint6 = new Paint();
        paint6.setColor(Color.RED);
        paint6.setStyle(Paint.Style.FILL);
        paint6.setStrokeWidth(6f);


        showAxis = BitmapFactory.decodeResource(getResources(), R.drawable.play_btn);
        showAxis = Bitmap.createScaledBitmap(showAxis, ball.width * 3, ball.height * 3, false);

        showAxisBool = 1;
        quarterOfLaunch = 0;
        ball.thrown = false;
    }



    @Override
    public void run()
    {
        while (isPlaying) // run only if playing.
        {
            update();//The components.
            draw();//Components on screen.
            sleep();//To render motion (FPS).
        }
    }



    public void update () // issue: physics #25
    {//discussion (bug fix): physics don't work when angle is -90 or 90 #29


        if ( ! ball.thrown && ball.isTouched) // get quarter right before the ball is thrown.
            quarterOfLaunch = ball.quarter; // discussion: From where has the ball been thrown? #24




        if (ball.thrown)
        {

            ball.prevX = ball.x;
            ball.prevY = ball.y; // for collision physics.
            // discussion: Changing Direction #34 || to prevent a bug (of dotArrayListX/Y) -> discussion: The Dots look disgusting #28


/*            // determine the current quarter of the ball after launch. todo: I don't think this is necessary...
            if (ball.x >= ball.orgIX) // right side:
                if (ball.y < ball.orgIY) ball.quarter = 1;// -top.
                else ball.quarter = 4;                    // -bottom.
            else // left side:
                if (ball.y < ball.orgIY) ball.quarter = 2;// -top.
                else ball.quarter = 3;                    // -bottom.

            if (ball.y > screenY) ball.HEIGHT =  -1 * Math.abs(screenY - (ball.height / 2f + ball.y)) / ball.ratioPXtoM; // ???
            else ball.HEIGHT = Math.abs(screenY - (ball.height / 2f + ball.y)) / ball.ratioPXtoM; // ???
            // in case I want the height in meters; make the height logical and not technical.  todo: I don't think this is necessary...
*/


            if (quarterOfLaunch == 1 || quarterOfLaunch == 2)
                ball.GRAVITY = -1 * Math.abs(ball.GRAVITY);// throwing the ball downwards.
            else ball.GRAVITY = Math.abs(ball.GRAVITY);

            ball.didCollide(ground.height);
            ball.velocityY = (short) (ball.initialVelocityY - (ball.GRAVITY * ball.time));

            //todo: remember that nextX/Y is a possibility!!!!!!!!!!!!!!!!!

            if (ball.collision == 0)
                physicsUpdateNoCol();
            else
                physicsUpdate(ball.collision, ball.detectDirection());

        }


        /* After ball axis are set, and we have the previous ball axis, we can now begin with
         what I said in discussion: Changing Direction #34 -> 3. 2nd attempt: */


        ball.dotArrayListX.add(ball.prevX + fixX()); // ??? ???
        ball.dotArrayListY.add(ball.prevY + fixY()); // show path of the ball

    }/* UPDATE */


    public void physicsUpdateNoCol() // origin of throw (for collision) | col -> collision number (type)
    {

        ball.velocityY = (short) (ball.initialVelocityY - (ball.GRAVITY * ball.time));



        switch (quarterOfLaunch) // discussion: From where has the ball been thrown? #24 || physics #17
        {
            case 1:
                ball.x = ball.initialX - ball.velocityX * ball.time;
                ball.y = (float) (ball.initialY + 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time);
                break;

            case 2:
                ball.x = ball.initialX + ball.velocityX * ball.time;
                ball.y = (float) (ball.initialY + 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time);
                break;

            case 3:
                ball.x = ball.initialX + ball.velocityX * ball.time;
                ball.y = (float) (ball.initialY - 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time);
                break;

            case 4:
                ball.x = ball.initialX - ball.velocityX * ball.time; // to the left
                ball.y = (float) (ball.initialY - 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time);
                break;
        }
    }





    public void physicsUpdate (byte col, byte direction) // col -> collision number (type) | direction of the ball
    {

        switch (col)
        {
            case 1:
                switch (quarterOfLaunch) // discussion: From where has the ball been thrown? #24 || physics #17
                {
                }
                break;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            case 2:

                ball.colAngle = (float) (Math.atan2(ball.prevX, ball.prevY));



                ball.x = ball.colX + ball.velocityX * ball.time;
                ball.y = (float) (ball.colY - 0.5 * (ball.initialVelocityY + ball.velocityY) * ball.time);


                break;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            case 3: break;

        }

    }


    public void draw ()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas

            // KEEP IN MIND THAT THE ORDER MATTERS. ???

            screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint1);//background
            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint1);//ball
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint1);//ground

            showStats(screenCanvas);



            if (ball.time > 0.032)
                for (int i = 0; i < ball.dotArrayListX.size() - 1; i++)
                    try{screenCanvas.drawPoint(ball.dotArrayListX.get(i), ball.dotArrayListY.get(i), paint1);}
                    catch (Exception ignored) {}

            // discussion: The Dots look disgusting #28
            //TODO: the try-catch block is temporary, remove after game is complete.







            if ( ! ball.thrown) // draw this line only before the ball is thrown.
            {
                ball.velocity = 0;
                ball.velocityX = 0;
                ball.velocityY = 0;


                //discussion: X and Y of stop screenCanvas.DrawLine #15
                if (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) > ball.width / 2f)
                {
                    // line is only drawn when the ball is farther from the center
                    // than the radius of the ball, otherwise it will be drawn inside the ball itself.

                    float lineStopX = (float) Math.abs((Math.cos(ball.ballAngle()) * ball.width / 2f)); // similar to perpAdj
                    float lineStopY = (float) Math.abs((Math.sin(ball.ballAngle()) * ball.width / 2f)); // similar to perpOpp
                    // issue: correcting the line with the ball #13


                    switch (ball.quarter) // draw a line to the opposite corner
                    {
                        case 1:
                            screenCanvas.drawLine(ball.x + fixX() - lineStopX, ball.y + fixY() + lineStopY,
                                    ball.initialX - (ball.x - ball.initialX) - fixX(),
                                    ball.initialY + (ball.initialY - ball.y) - fixY(), paint1);
                            break;
                        case 2:
                            screenCanvas.drawLine(ball.x + fixX() + lineStopX, ball.y + fixY() + lineStopY,
                                    ball.initialX + (ball.initialX - ball.x) - fixX(),
                                    ball.initialY + (ball.initialY - ball.y) - fixY(), paint1);
                            break;
                        case 3:
                            screenCanvas.drawLine(ball.x + fixX() + lineStopX, ball.y + fixY() - lineStopY,
                                    ball.initialX + (ball.initialX - ball.x) - fixX(),
                                    ball.initialY - (ball.y - ball.initialY) - fixY(), paint1);
                            break;
                        case 4:
                            screenCanvas.drawLine(ball.x + fixX() - lineStopX, ball.y + fixY() - lineStopY,
                                    ball.initialX - (ball.x - ball.initialX) - fixX(),
                                    ball.initialY - (ball.y - ball.initialY) - fixY(), paint1);
                            break;
                    }
                }
            }
            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    } /* DRAW */




    private void sleep() // discussion: Time updating #33
    {
        try { Thread.sleep(SLEEP_MILLIS); }// = 16
        catch (InterruptedException e) {e.printStackTrace();}

        //count time from throw:
        if (ball.thrown)
            ball.time += (60 / 1000f);
        else
            ball.time = 0;

        game_time += (60 / 1000f);


        if (ball.collision == 3 && ball.removeBall_time == 0)
            ball.removeBall_time = ball.time;

        if (ball.removeBall_time != 0 && ball.time - ball.removeBall_time > 2)
            ball.reset();


    } /* SLEEP */



    public float fixX() {return ball.width/2f;}// for comfortable coding.
    public float fixY() {return ball.height/2f;}// for comfortable coding.



    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {
        switch (event.getAction()) // down/move/up
        {

            case MotionEvent.ACTION_DOWN:// started touch

                if (ball.isTouching(event.getX(),event.getY()) && ! ball.thrown)
                    ball.isTouched= true; // 'ball' has a boolean method that indicates whether the object is touched.


                if (ball.thrown && ball.calcDistanceFromI(event.getX(), event.getY()) <= ball.maxBallPull)
                    ball.reset(); // reset when touch origin.


                if (event.getRawX() >= screenX /2f - ball.width * 3 && event.getRawX() <= screenX /2f + ball.width * 3
                        && event.getRawY() >= 0 && event.getRawY() <= ball.height * 3)
                {
                    if (showAxisBool == 0)
                        showAxisBool = 1;
                    else
                        showAxisBool = 0;
                }// turn on or off the dev mode.

                break;




            case MotionEvent.ACTION_MOVE: // pressed and moving
            {
                if (ball.isTouched) // if touched the ball
                {

                    float angle_of_touch = ball.findAngleWhenOutside(event.getX(), event.getY()); // also sets ball.angle


                    if (Math.abs(180 / Math.PI * angle_of_touch) > 90) // right side
                        if (180 / Math.PI * angle_of_touch >= 0)
                            ball.quarter = 1; // top right corner
                        else
                            ball.quarter = 4; // bottom right corner

                    else // left side
                        if (180 / Math.PI * angle_of_touch >= 0)
                            ball.quarter = 2; // top left corner
                        else
                            ball.quarter = 3; // bottom left corner





                    if (ball.calcDistanceFromI(event.getX(), event.getY()) < ball.maxBallPull) // if in drag-able circle
                        ball.setPosition(event.getX(), event.getY());                     // than drag normally.



                    else // issue: finger drag outside of radius #4
                    {
                        float perpOpp = (float) Math.abs(Math.sin(angle_of_touch) * ball.maxBallPull); // for y of maxBallPull
                        float perpAdj = (float) Math.abs(Math.cos(angle_of_touch) * ball.maxBallPull); // for x
                        //  perp- perpendicular (????????), adj- adjacent (??????), opp- opposite (??????)


                        switch (ball.quarter)
                        {
                            case 1: ball.setPosition(ball.initialX + perpAdj, ball.initialY - perpOpp); break;
                            case 2: ball.setPosition(ball.initialX - perpAdj, ball.initialY - perpOpp); break;
                            case 3: ball.setPosition(ball.initialX - perpAdj, ball.initialY + perpOpp); break;
                            case 4: ball.setPosition(ball.initialX + perpAdj, ball.initialY + perpOpp); break;
                        } // discussion: screen axis in comparison to initial ball place #12

                    }// outside drag-able circle
                }// if touched the ball
            }//ACTION_MOVE

            break;



            case MotionEvent.ACTION_UP: // ended touch || discussion: Throw from stretched point #40

                if (ball.isTouched) // if touched the ball in the first place.
                {
                    if (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) < ball.width)
                    {
                        ball.x = ball.orgIX - fixX();
                        ball.y = ball.orgIY - fixY();
                    } // discussion: Disable ball movement when only touched briefly #31


                    else { // shot

                        ball.thrown = true;


                        ball.percentOfPull = ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) / ball.maxBallPull;

                        ball.velocity = (short) (ball.percentOfPull * ball.MAX_VELOCITY);
                        // Percent of pull * max velocity = percent of max velocity

                        ball.velocityX = (short) Math.abs(Math.cos(ball.angle) * ball.velocity); // ???
                        ball.initialVelocityY = (short) Math.abs(Math.sin(ball.angle) * ball.velocity); // ???
                        // Both of these values never change after the ball is thrown.


                        ball.orgIX = ball.initialX;
                        ball.orgIY = ball.initialY;

                        ball.initialX = ball.x;
                        ball.initialY = ball.y;


                        ball.range = (float) ((ball.velocity * ball.velocity * Math.sin(2 * ball.angle) * Math.sin(2 * ball.angle))
                                / ball.GRAVITY / ball.ratioPXtoM);


                    }



                }

                ball.isTouched = false;

                break;

        }

        return true;
    }




    /////////////////////////////////////////////////////////////////////////
    // general functions



    public void showStats (Canvas screenCanvas)
    {

        screenCanvas.drawLine(ball.prevX, ball.prevY + fixY(), ball.x, ball.y + fixY(), paint5);

        screenCanvas.drawLine(screenX / 4f, 0, screenX / 4f, screenY, paint5);

        if (ball.collision != 0) {
            screenCanvas.drawPoint(ball.colX, ball.colY, paint6);
            screenCanvas.drawLine(ball.colX, ball.colY, ball.x + fixX(), ball.y + fixY(), paint5);
        }

        if (showAxisBool == 1) {
//              SHOW AXIS:
            screenCanvas.drawLine(0, ball.orgIY, screenX, ball.orgIY, paint2);
            screenCanvas.drawLine(ball.orgIX, 0, ball.orgIX, screenY, paint2);

//              SHOW BALL AXIS:
            screenCanvas.drawLine(0, ball.y + fixY(), screenX, ball.y + fixY(), paint4);
            screenCanvas.drawLine(ball.x + fixX(), 0, ball.x + fixX(), screenY, paint4);

//              SHOW BALL HITBOX
            screenCanvas.drawLine(ball.x, ball.y, ball.x + ball.width, ball.y, paint3);
            screenCanvas.drawLine(ball.x, ball.y, ball.x, ball.y + ball.height, paint3);
            screenCanvas.drawLine(ball.x + ball.width, ball.y, ball.x + ball.width, ball.y + ball.height, paint3);
            screenCanvas.drawLine(ball.x, ball.y + ball.height, ball.x + ball.width, ball.y + ball.height, paint3);


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            screenCanvas.drawText("X: " + ball.x + fixX(), 75, 50, paint2);
            screenCanvas.drawText("Y: " + ball.y + fixY(), 75, 75, paint2);
            screenCanvas.drawText("Angle: " + (float) (180 / Math.PI * ball.angle), 75, 125, paint2);
            screenCanvas.drawText("velocity: " + ball.velocity, 75, 175, paint2);
            screenCanvas.drawText("velocityX: " + ball.velocityX, 75, 200, paint2);
            screenCanvas.drawText("velocityY: " + ball.velocityY, 75, 225, paint2);
            screenCanvas.drawText("HEIGHT: " + ball.HEIGHT, 75, 250, paint2);
            screenCanvas.drawText("quarter: " + ball.quarter, 75, 274, paint2);
            screenCanvas.drawText("Time: " + (int) game_time, screenX - 175, 125, paint2);
            screenCanvas.drawText("range: " + ball.range, 75, 300, paint2);


            screenCanvas.drawText("collided: " + ball.collision, screenX / 2f - ball.width * 3, ball.height * 4, paint2);
            screenCanvas.drawText("colX: " + ball.colX, screenX / 2f - ball.width * 3, ball.height * 4.5f, paint2);
            screenCanvas.drawText("colY: " + ball.colY, screenX / 2f - ball.width * 3, ball.height * 5, paint2);

            screenCanvas.drawText("colAngle!!!!: " + 180/Math.PI * ball.colAngle, screenX / 2f - ball.width * 3, ball.height * 6, paint2);

        }

        screenCanvas.drawBitmap(showAxis, screenX / 2f - ball.width * 3, 0, paint1);//button to show initial axis

    } // TODO: THIS BLOCK IS TEMP




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