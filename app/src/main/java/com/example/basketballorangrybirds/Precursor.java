package com.example.basketballorangrybirds;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class Precursor extends Activity
{
    DrawLine drawLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawLine = new DrawLine(this);
        drawLine.setBackgroundColor(Color.WHITE);
        setContentView(drawLine);
    }

    static class DrawLine extends View
    {
        Paint paint = new Paint();

        public DrawLine(Context context) {
            super(context);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(10, 20, 30, 40, paint);
        }
    }
}
