package com.example.basketballorangrybirds;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable
{

    private Paint paint;            // The paint is the thing that "draws"// the image/Bitmap.
    private final int maxDistBallToInitial;   // is the radius of the circle which determines max dist. between b - ball and i - initial points
    private float perpOpp, perpAdj;
    private float angle_of_touch;
    boolean isOnEdge;
    // General



    private Background background;
    private Ball ball;
    private Precursor precursor;
    private Player player;
    private Basket basket;
    // Objects

    private final int screenX , screenY;    // notice that
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

        background = new Background(getResources(), screenX, screenY);
        ball = new Ball(getResources(), ratioX, ratioY, screenX, screenY);

        isPlaying = true;

        maxDistBallToInitial = (int) (300 * ratioX * ratioY);

        precursor = new Precursor();

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{30, 30}, 0)); // array of ON and OFF distances,
        paint.setStrokeWidth(6f);

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


            screenCanvas.drawBitmap(background.backgroundBitmap, 0, 0, paint);
            screenCanvas.drawBitmap(ball.ballBitmap, ball.x,ball.y , paint);
            screenCanvas.drawBitmap(ball.centerBitmap, ball.initialX,ball.initialY , paint);


            screenCanvas.drawLine(0,ball.initialY,screenX,ball.initialY, paint);









            switch (ball.quarter)
            {
                case 1:
                    screenCanvas.drawLine(ball.x + fixLineToBall(),ball.y + ball.height - fixLineToBall(),
                            ball.initialX - perpAdj - fixX(),ball.initialY + perpOpp + fixY(), paint);
                    break;

                case 2:
                    screenCanvas.drawLine(ball.x + ball.width - fixLineToBall(),ball.y + ball.height - fixLineToBall(),
                            ball.initialX + perpAdj + fixX(),ball.initialY + perpOpp + fixY(), paint);
                    break;

                case 3:
                    screenCanvas.drawLine(ball.x + ball.width - fixLineToBall(),ball.y + fixLineToBall(),
                            ball.initialX + perpAdj + fixX(),ball.initialY - perpOpp - fixY(), paint);
                    break;

                case 4:
                    screenCanvas.drawLine(ball.x + fixLineToBall(), ball.y + fixLineToBall(),
                            ball.initialX - perpAdj,ball.initialY - perpOpp, paint);
                    break;
            }
            // TODO: 05/10/2022
            // make the line connect to the ball at the RIGHT angle from i



            getHolder().unlockCanvasAndPost(screenCanvas);
        }
    }


    private void sleep() {
        try { Thread.sleep(SLEEP_MILLIS); }// = 16
        catch (InterruptedException e) {e.printStackTrace();}
    }


    public void setBallQuarter(byte qtr)
    {ball.quarter = qtr;}


    public float fixLineToBall() // issue: correcting the line with the ball #13
    {return (float) (Math.tan(22.5) * ball.width/2 / Math.sqrt(2));}

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



            case MotionEvent.ACTION_MOVE:
            {

                if (ball.getActionDown()) // if touched the ball
                {
                    angle_of_touch = ball.angle(event.getX(), event.getY());

                    if (Math.abs(180 / Math.PI * angle_of_touch) >= 90)

                        if (180 / Math.PI * angle_of_touch >= 0)
                            setBallQuarter((byte) 1); // top right corner

                        else
                            setBallQuarter((byte) 4); // bottom right corner

                    else

                        if (180 / Math.PI * angle_of_touch >= 0)
                            setBallQuarter((byte) 2); // top left corner

                        else
                            setBallQuarter((byte) 3); // bottom left corner




                    if (ball.calcDistance(event.getX(), event.getY()) < maxDistBallToInitial) // in drag-able circle
                    {
                        ball.setPosition(event.getX(), event.getY());
                        isOnEdge = false;
                    }




                    else // issue NO.4   ||   finger drag outside of radius
                    {   //  perp- perpendicular (ניצב), adj- adjacent (ליד), opp- opposite (מול)

                        perpOpp = (float) Math.abs(Math.sin(angle_of_touch) * maxDistBallToInitial);
                        perpAdj = (float) Math.abs(Math.cos(angle_of_touch) * maxDistBallToInitial);

                        Log.d("KEY1121 perp", perpOpp + " <-opp ||| adj-> " + perpAdj );


                        switch (ball.quarter)
                        {
                            case 1: ball.setPosition(ball.initialX + perpAdj, ball.initialY - perpOpp); break;

                            case 2: ball.setPosition(ball.initialX - perpAdj, ball.initialY - perpOpp); break;

                            case 3: ball.setPosition(ball.initialX - perpAdj, ball.initialY + perpOpp); break;

                            case 4: ball.setPosition(ball.initialX + perpAdj, ball.initialY + perpOpp); break;
                        }


                        isOnEdge = true;


                    }// outside drag-able circle
                }// if touched the ball
            }//ACTION_MOVE

            break;


            case MotionEvent.ACTION_UP: // end of touch
                ball.setActionDown(false);



                /*long start = System.nanoTime();
                if (start >= 1000)
                {
                    long elapsedTime = System.nanoTime() - start;
                    Log.d("key1121 time", "elapsedTime: " + elapsedTime);
                }
*/
                // start timer for calculating the speed of the ball.
                break;
        }
        return true;
    }







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
}

