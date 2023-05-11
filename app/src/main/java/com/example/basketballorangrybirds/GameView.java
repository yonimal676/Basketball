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
    private final Paint path_paint;            // hitting point.

    private Background background;
    private Ball ball;
    private Basket basket;
    private final Ground ground;
    // Objects


    private final short screenX, screenY;
    private float game_time;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameActivityContext;
    private byte quarterOfLaunch;


    // Technical stuff


    private Bitmap showAxis;   // screen axis in comparison to initial ball place #12
    private byte showAxisBool; // 0 -> false, 1 -> true



    public GameView(GameActivity activity, short screenX, short screenY)
    {
        super(activity);

        this.activity = activity;
        gameActivityContext = this.activity;

        this.screenX = screenX;
        this.screenY = screenY;


        isPlaying = true;


        background = new Background(getResources(), screenX, screenY);
        ball = new Ball(getResources(), screenX, screenY);
        ground = new Ground(getResources(), screenX + 100, screenY);


        ball.maxBallPull = (short) (screenY - ground.height - ball.initialY - ball.height); // the radius of max dist of the ball from the initial position

        game_time = 0;

        quarterOfLaunch = 0;
        ball.thrown = false;


        paint1 = new Paint();
        paint1.setColor(Color.rgb(224, 65, 11));
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // array of ON and OFF distances,
        paint1.setStrokeWidth(3f);


        paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(5f);


        paint3 = new Paint();
        paint3.setColor(Color.RED);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setStrokeWidth(1f);


        paint4 = new Paint();
        paint4.setColor(Color.WHITE);
        paint4.setStyle(Paint.Style.FILL);
        paint4.setStrokeWidth(3f);

        paint5 = new Paint();
        paint5.setColor(Color.BLUE);
        paint5.setStyle(Paint.Style.FILL);
        paint5.setStrokeWidth(4f);

        path_paint = new Paint();
        path_paint.setColor(Color.rgb(189, 146, 81));
        path_paint.setStyle(Paint.Style.FILL);
        path_paint.setStrokeWidth(4f);


        showAxis = BitmapFactory.decodeResource(getResources(), R.drawable.play_btn);
        showAxis = Bitmap.createScaledBitmap(showAxis, ball.width * 3, ball.height * 3, false);
        showAxisBool = 0;

    }



    @Override
    public void run()
    {
        while (isPlaying) // run only if playing.
        {
            sleep();//To render motion (FPS).
            update();//The components.
            draw();//Components on screen.
        }
    }



    public void update () // issue: physics #25
    {//discussion (bug fix): physics don't work when angle is -90 or 90 #29


        if (ball.thrown)
        {
            ball.prevX = ball.x;
            ball.prevY = ball.y; // for collision physics.
            // discussion: Changing Direction #34 || to prevent a bug (of dotArrayListX/Y) -> discussion: The Dots look disgusting #28

            physicsUpdateNoCol();  // -> if collided will call physicsUpdate()
        }

        else if (ball.isTouched) // get quarter right before the ball is thrown.
            quarterOfLaunch = ball.quarter; // discussion: From where has the ball been thrown? #24


        ball.dotArrayListX.add(ball.prevX + fixX()); // → ↓
        ball.dotArrayListY.add(ball.prevY + fixY()); // show path of the ball

    }/* UPDATE */


    public void physicsUpdateNoCol () // issue: physics #25
    {

        if (ball.didCollide(ground.height) == 0) // if did not collide
        {
            if (quarterOfLaunch == 2)
            {
                ball.vx = abs(ball.vx);
/*
                ball.vy = abs(ball.vy);
*/
            }
            else {
                ball.vy = quarterOfLaunch == 1  ?  abs(ball.vy) : -1 * abs(ball.vy);
                ball.vx = quarterOfLaunch == 3  ?  abs(ball.vx) : -1 * abs(ball.vx);
            } // =4 -> -1 * abs(ball.vy) && -1 * abs(ball.vx)

            ball.vy = ball.v0y - ball.GRAVITY * ball.time; // vx doesn't change ever


            ball.x = ball.initialX + ball.vx * ball.time; // Vx * t
            ball.y = ball.initialY + ball.vy * ball.time - ball.GRAVITY * ball.time * ball.time / 2; // h + Vy * t - g * t² / 2


            // Explanation: In previous attempts, I didn't change the velocities, but rather the way the ball moves,
            // for example: ball.x = ball.initialX - ball.vx * time;
            // Now this did work but I had to make 4 different cases for | -- | -+ | +- | ++ |
            // which is unreadable. Instead, I'll change the velocities according to where they should move towards.
            // Discussion: physics #25
        }


        else
            physicsUpdate(ball.collision); // discussion: Changing Direction #34 | issue: Collision Physics #26
    }



    public void physicsUpdate (byte col) // col -> collision number (type)
    {

        switch (col)
        {
            case 1: // right wall

                ball.x = ball.vx * ball.time + screenX + screenX - ball.initialX; // ✔️? but why ?
                ball.y = (ball.vy*ball.time-ball.GRAVITY*ball.time*ball.time/2)  + ball.initialY ; // ✔️
                break;
                // (function[y]) + new axis 🤩
                /*ALSO WORKS:  +(screenX - ball.initialX + ball.colX - abs(ball.colX - screenX))*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            case 2: // left wall
                ball.x = ball.vx * ball.time - screenX;
                ball.y = (ball.vy * ball.time - ball.GRAVITY * ball.time * ball.time / 2);

                break;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            case 3: // floor
                ball.x = ball.vx * ball.time + ( ball.colX - fixX());
                ball.y = ball.vy * ball.time - ball.GRAVITY * ball.time * ball.time / 2 + (ball.colY+ball.height+ground.height+abs(ball.initialY - ball.orgIY));
                break;

        }


    }


    public void draw ()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas

            // KEEP IN MIND THAT THE ORDER MATTERS. ↓

            if (showAxisBool == 0)
                screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint1);//background
            else
                screenCanvas.drawBitmap(background.devBackgroundBitmap, 0, 0, paint1);//background


            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint1);//ball
            screenCanvas.drawBitmap(ground.groundBitmap, ground.x, ground.y, paint1);//ground

            showStats(screenCanvas);



            if (ball.time > 0.032)
                for (short i = 0; i < ball.dotArrayListX.size() - 1; i++)
                    try{screenCanvas.drawCircle(ball.dotArrayListX.get(i), ball.dotArrayListY.get(i), ball.width/20f, paint3/*path_paint*/);}
                    catch (Exception ignored) {}
            // discussion: The Dots look disgusting #28




            if ( ! ball.thrown) // draw this line only before the ball is thrown.
            {
                ball.v = 0;
                ball.vx = 0;
                ball.vy = 0;


                //discussion: X and Y of stop screenCanvas.DrawLine #15 | issue: correcting the line with the ball #13
                if (ball.calcDistanceFromI(ball.x + fixX(), ball.y + fixY()) > ball.width / 2f) // don't draw inside the ball
                {

                    float lineStopX = abs((Math.cos(ball.ballAngle()) * fixX())); // similar to perpAdj
                    float lineStopY = abs((Math.sin(ball.ballAngle()) * fixX())); // similar to perpOpp | fixX() = radius of the ball so..


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




    private void sleep() // discussion: Time updating #33 | byte is like int | refresh rate is (1000 / SLEEP_MILLIS = 62.5 FPS)
    {
        float SLEEP_MILLIS = 1000/120f;//

        try { Thread.sleep((long) (SLEEP_MILLIS / 2)); }
        catch (InterruptedException e) {e.printStackTrace();}

        //count time from throw:
//        ball.time = ball.thrown ? ball.time + SLEEP_MILLIS/1000f : 0;
        if (ball.thrown)
            ball.time += SLEEP_MILLIS / 1000;  // = 0.022 -> cuz it looks good
        else
            ball.time = 0;


        game_time += SLEEP_MILLIS / 1000;  // = 0.01
    }







    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {
        switch (event.getAction()) // down/move/up
        {

            case MotionEvent.ACTION_DOWN:// started touch

                if (ball.isTouching(event.getX(),event.getY()) && ! ball.thrown)
                    ball.isTouched = true; // 'ball' has a boolean method that indicates whether the object is touched.


                if (ball.thrown && ball.calcDistanceFromI(event.getX(), event.getY()) <= ball.maxBallPull)
                    ball.reset(); // reset when touch origin.


                if (event.getRawX() >= screenX /2f - ball.width * 3 && event.getRawX() <= screenX /2f + ball.width * 3
                        && event.getRawY() >= 0 && event.getRawY() <= ball.height * 3)

                    showAxisBool = (byte) ((showAxisBool == 0) ? 1 : 0);


                // turn dev mode on or off.

                break;




            case MotionEvent.ACTION_MOVE: // pressed and moving
            {
                if (ball.isTouched) // if touched the ball
                {

                    float angle_of_touch = ball.findAngleWhenOutside(event.getX(), event.getY()); // also sets ball.angle


                    if (abs(180 / Math.PI * angle_of_touch) > 90) // right side
                        if (180 / Math.PI * angle_of_touch >= 0)
                            ball.quarter = 1; // top right corner
                        else
                            ball.quarter = 4; // bottom right corner

                    else // left side
                        if (180 / Math.PI * angle_of_touch >= 0)
                            ball.quarter = 2; // top left corner
                        else
                            ball.quarter = 3; // bottom left corner

                    // TODO: DON'T EVER CHANGE THIS !!!



                    if (ball.calcDistanceFromI(event.getX(), event.getY()) < ball.maxBallPull) // if in drag-able circle
                        ball.setPosition(event.getX(), event.getY());                         // than drag normally.



                    else // issue: finger drag outside of radius #4
                    {
                        float perpOpp = abs(Math.sin(angle_of_touch) * ball.maxBallPull); // for y of maxBallPull
                        float perpAdj = abs(Math.cos(angle_of_touch) * ball.maxBallPull); // for x
                        //  perp- perpendicular (ניצב), adj- adjacent (ליד), opp- opposite (מול)


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

                        ball.v = ball.percentOfPull * ball.MAX_VELOCITY;
                        // Percent of pull * max velocity = percent of max velocity

                        ball.vx = abs(Math.cos(ball.angle) * ball.v);

                        if (quarterOfLaunch == 1 || quarterOfLaunch == 2)
                            ball.v0y = abs(Math.sin(ball.angle) * ball.v);
                        else
                            ball.v0y = -1 * abs(Math.sin(ball.angle) * ball.v);
                        // Both of these values never change after the ball is thrown.

                        ball.orgIX = ball.initialX;
                        ball.orgIY = ball.initialY;

                        ball.initialX = ball.x;
                        ball.initialY = ball.y;
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

        screenCanvas.drawLine(0, 0, 0, screenY, paint5);
        screenCanvas.drawLine(screenX, 0, screenX, screenY, paint5);

        if (ball.collision != 0) {
            screenCanvas.drawPoint(ball.colX, ball.colY, paint5);
            screenCanvas.drawLine(ball.colX, ball.colY, ball.x + fixX(), ball.y + fixY(), paint5);
        }

        if (showAxisBool == 1)
        {

            if (ball.thrown) {
                screenCanvas.drawLine(ball.prevX, ball.prevY + fixY(), ball.x, ball.y + fixY(), paint5);
//              SHOW BALL AXIS:
                screenCanvas.drawLine(0, ball.y + fixY(), screenX, ball.y + fixY(), paint4);
                screenCanvas.drawLine(ball.x + fixX(), 0, ball.x + fixX(), screenY, paint4);

                // SHOW i AXIS:
                screenCanvas.drawLine(0, ball.initialY + fixY(), screenX, ball.initialY + fixX(), paint3);
                screenCanvas.drawLine(ball.initialX + fixX(), 0, ball.initialX + fixX(), screenY, paint3);

            }
//              SHOW org AXIS:
            screenCanvas.drawLine(0, ball.orgIY, screenX, ball.orgIY, paint2);
            screenCanvas.drawLine(ball.orgIX, 0, ball.orgIX, screenY, paint2);




//          SHOW BALL HITBOX
            screenCanvas.drawLine(ball.x, ball.y, ball.x + ball.width, ball.y, paint3);
            screenCanvas.drawLine(ball.x, ball.y, ball.x, ball.y + ball.height, paint3);
            screenCanvas.drawLine(ball.x + ball.width, ball.y, ball.x + ball.width, ball.y + ball.height, paint3);
            screenCanvas.drawLine(ball.x, ball.y + ball.height, ball.x + ball.width, ball.y + ball.height, paint3);


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            screenCanvas.drawText("X: " + ball.x + fixX(), 75, 50, paint2);
            screenCanvas.drawText("Y: " + ball.y + fixY(), 75, 75, paint2);

            screenCanvas.drawText("Angle: ∠ " + (float) (180 / Math.PI * ball.angle) + "°", 75, 110, paint2);
            screenCanvas.drawText("velocity (m/s): " + ball.v / ball.ratioMtoPX, 75, 130, paint2);
            screenCanvas.drawText("velocityX (m/s): " + ball.vx / ball.ratioMtoPX, 75, 150, paint2);
            screenCanvas.drawText("velocityY (m/s): " + ball.vy / ball.ratioMtoPX, 75, 170, paint2);
            screenCanvas.drawText("v0y: " + ball.v0y / ball.ratioMtoPX, 75, 210, paint2);

            screenCanvas.drawText("collided: " + ball.collision, screenX / 2f - ball.width * 3, ball.height * 3 + 50, paint2);
            screenCanvas.drawText("colX: " + ball.colX, screenX / 2f - ball.width * 3, ball.height * 3 + 70, paint2);
            screenCanvas.drawText("colY: " + ball.colY, screenX / 2f - ball.width * 3, ball.height * 3 + 90, paint2);

            screenCanvas.drawText("quarterOfLaunch: " + quarterOfLaunch, screenX / 2f - ball.width * 3, ball.height * 3 + 110, paint2);
            screenCanvas.drawText("range: " + ball.range, screenX / 2f - ball.width * 3, ball.height * 3 + 130, paint2);
            screenCanvas.drawText("HEIGHT: " + ball.HEIGHT, screenX / 2f - ball.width * 3, ball.height * 3 + 150, paint2);

            screenCanvas.drawText("Time: " + (int) game_time +"s", screenX / 2f + ball.width * 3, 50, paint2);




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


    public float fixX() {return ball.width/2f;}// for comfortable coding.
    public float fixY() {return ball.height/2f;}// for comfortable coding.
    public float abs(double num) {return (float) Math.abs(num);} // saves space -> '(float)' and 'Math.' are unnecessary.
}