package com.example.basketballorangrybirds;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable
{
    private Background background;
    private Ball ball;
    private Player player;
    private Basket basket;
    private Paint paint;                  // The paint is the thing that "draws"// the image/Bitmap.

    private final int screenX, screenY;
    private final float ratioX, ratioY;

    private int sleepMillis = 17;
    private boolean isPlaying;
    private Thread thread;
    private final GameActivity activity;
    private final Context gameViewContext;


    public GameView(GameActivity activity, int screenX, int screenY)
    {
        super(activity);

        this.activity = activity;
        gameViewContext = this.activity;

        this.screenX = screenX;
        this.screenY = - screenY;

        ratioX = 1080f / screenX; // side to side
        ratioY = 1920f / screenY ; // top to bottom

        background = new Background(getResources(), screenX, screenY);
        ball = new Ball(getResources(), ratioX, ratioY, screenX, screenY);

        isPlaying = true;

        paint.setColor(Color.BLACK);

    }

    @Override
    public void run() {
        while (isPlaying)
        {
            update();
            draw();
            sleep();
        }
    }

    public void update ()
    {
   //ball.Formula(x,y,velocity)
    }

    public void draw ()
    {
        if (getHolder().getSurface().isValid()) // is the surface valid?
        {
            Canvas screenCanvas = getHolder().lockCanvas(); // create the canvas

            screenCanvas.drawBitmap(background.backgroundBitmap, background.x, background.y, paint);
            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint);


//            screenCanvas.drawLine(ball.x,ball.y,ball.initialX+ball.x,ball.initialY+ball.y,paint);




            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    }


    private void sleep() {
        try { Thread.sleep(sleepMillis); }// = 17
        catch (InterruptedException e) {e.printStackTrace();}
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this); // -> "this" is the run() method above.
        thread.start();
    } // resume the game


    @Override
    public boolean onTouchEvent (MotionEvent event) // is a function that helps me detect touch.
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: // started touch
                if (ball.touched(event.getX(),event.getY()))
                    ball.setActionDown(true);
                break;
            case MotionEvent.ACTION_MOVE: // still touching

                if (ball.getActionDown() && (ball.touched(event.getX(),event.getY())))
                {// if touched the ball
                    if (calcDistance(ball.initialX, (int) event.getX(), ball.initialY, (int) event.getY()) < 170)
                    {// to make sure that the player doesn't take the ball away

                        ball.setPosition((int) (event.getX() - ball.width / 2), (int) (event.getY() - ball.height / 2));

                        Log.d("fuck", ball.calcThrowAngle() + "");


                    }
                }
                break;

            case MotionEvent.ACTION_UP: // end of touch
                ball.setActionDown(false);

                // start timer for calculating the speed of the ball.
                break;
        }
        return true;
    }

    double calcDistance (int x1, int x2, int y1, int y2)
    {return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 -y2) * (y1-y2));} // this is the distance function
}