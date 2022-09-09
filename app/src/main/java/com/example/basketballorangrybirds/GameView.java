package com.example.basketballorangrybirds;

import android.content.Context;
import android.view.SurfaceView;

public class GameView extends SurfaceView
{

    private Background background;
    private Ball ball;
    private Player player;
    private Basket basket;

    private Thread thread;
    private Runnable runnable; // can delete this
    



    public GameView(Context context)
    {
        super(context);

    }
}
