package com.example.basketballorangrybirds;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable
{

    private Paint paint;            // The paint is the thing that "draws"// the image/Bitmap.
    private float angle_of_touch;
    private final int max_i_to_b;   // serves as the radius of the circle witch determines max dist. between ball and inital
    // General

    private Background background;
    private Ball ball;
    private Precursor precursor;
    private Player player;
    private Basket basket;
    // Objects

    private final int screenX , screenY;    // notice that
    private final float ratioX , ratioY;
    private final int SLEEP_MILLIS = 16;
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
        ratioY = 1920f / screenY ; // top to bottom

        background = new Background(getResources(), screenX, screenY);
        ball = new Ball(getResources(), ratioX, ratioY, screenX, screenY);

        isPlaying = true;

        max_i_to_b = (int) (500 * ratioX * ratioY);
    }



    @Override
    public void run() {
        while (isPlaying) { // because we don't want to run the app if we don't play.
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

            screenCanvas.drawBitmap(background.backgroundBitmap, background.x, background.y, paint);
            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint);
            screenCanvas.drawBitmap(ball.centerBitmap, ball.initialX,ball.initialY , paint);

            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    }


    private void sleep() {
        try { Thread.sleep(SLEEP_MILLIS); }// = 16
        catch (InterruptedException e) {e.printStackTrace();}
    }






    @Override
    public boolean onTouchEvent (MotionEvent event) // this is a method that helps me detect touch.
    {

        switch (event.getAction()) // down/move/up
        {

            case MotionEvent.ACTION_DOWN:
                // started touch

                if (ball.isTouching(event.getX(),event.getY()))
                    ball.setActionDown(true);
                // 'ball' has a boolean method that indicates whether the object is touched.

                Log.d("key1121", event.getX() + " ||| " + event.getY() );


                break;





            case MotionEvent.ACTION_MOVE:  // still touching and moving

                if (ball.getActionDown()) /* try to explain this to a 6 y/o */
                {   // if touched the ball

                    if (ball.calcDistance(event.getX(), event.getY()) < max_i_to_b)
                      // checks dist. between finger and initial point.
                        ball.setPosition((int) (event.getX()), (int) (event.getY()));




                    // this took me a full day to understand.. no kidding

                    else // issue NO.4

                    {
                        // finger is far away from the i (initial spot)


                        angle_of_touch = ball.angle(event.getX(),event.getY());
                        // hypo- hypotenuse (יתר) , perp- perpendicular (ניצב), adj- adjacent (ליד), opp- opposite (מול)

                        float perpOpp = (float) Math.abs( Math.sin(angle_of_touch) * max_i_to_b)
                                , perpAdj = (float) Math.abs(Math.cos(angle_of_touch)  * max_i_to_b);




        /*qtr.4*/      if (Math.abs(180/Math.PI*angle_of_touch) >= 90 && 180/Math.PI*angle_of_touch >= 0)
                            ball.setPosition(ball.initialX + perpAdj, ball.initialY - perpOpp);

        /*qtr.1*/      else if (Math.abs(180/Math.PI*angle_of_touch) < 90 && 180/Math.PI*angle_of_touch >= 0)
                            ball.setPosition(ball.initialX - perpAdj, ball.initialY - perpOpp);

        /*qtr.3*/      else if (Math.abs(180/Math.PI*angle_of_touch) >= 90 && 180/Math.PI*angle_of_touch < 0)
                            ball.setPosition(ball.initialX + perpAdj, ball.initialY + perpOpp);

        /*qtr.2*/      else // <90 <0 ---> -x -y
                            ball.setPosition(ball.initialX - perpAdj, ball.initialY + perpOpp);
                        // issue NO. 6


                        Log.d("key1121","--------------------- ||| ---------------------" );

                        Log.d("key1121 PERP", perpOpp + " OPP ||| ADJ " + perpAdj);
                        Log.d("key1121 BALL", ball.initialX + " I-X ||| I-Y " + ball.initialY);
                        Log.d("key1121 BALL", ball.x + " B-X ||| B-Y " + ball.y);






                    }//calcDistance<170


                    Log.d("key1121 ANGLE", 180/Math.PI * ball.angle(event.getX(),event.getY()) + "");

                }//ball.getActionDown()


                break;

            case MotionEvent.ACTION_UP: // end of touch
                ball.setActionDown(false);


                ball.x = ball.initialX - ball.width/2f; /* TEMPORARY! */
                ball.y = ball.initialY - ball.height/2f; /* TEMPORARY! */



                // start timer for calculating the speed of the ball.
                break;
        }
        return true;
    }





    public void resume() {// issue NO.1

        isPlaying = true;
        thread = new Thread(this); // -> "this" is the run() method above.
        thread.start();
    } // resume the game


}