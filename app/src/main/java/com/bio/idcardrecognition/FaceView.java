package com.bio.idcardrecognition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class FaceView extends View {

    private Context context;
    private Paint realPaint;
    private Paint spoofPaint;

    private Size frameSize;

    private Rect position;

    private String documentName;

    public FaceView(Context context) {
        this(context, null);

        this.context = context;
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    public void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        realPaint = new Paint();
        realPaint.setStyle(Paint.Style.STROKE);
        realPaint.setStrokeWidth(3);
        realPaint.setColor(Color.GREEN);
        realPaint.setAntiAlias(true);
        realPaint.setTextSize(50);

        spoofPaint = new Paint();
        spoofPaint.setStyle(Paint.Style.STROKE);
        spoofPaint.setStrokeWidth(3);
        spoofPaint.setColor(Color.RED);
        spoofPaint.setAntiAlias(true);
        spoofPaint.setTextSize(50);
    }

    public void setFrameSize(Size frameSize)
    {
        this.frameSize = frameSize;
    }

    public void setDocumentInfos(Rect position, String documentName)
    {
        this.position = position;
        this.documentName = documentName;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(position != null && !position.isEmpty()) {
            float x_scale = this.frameSize.getWidth() / (float)canvas.getWidth();
            float y_scale = this.frameSize.getHeight() / (float)canvas.getHeight();

            realPaint.setStrokeWidth(3);
            realPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawText(documentName, (position.left / x_scale) + 10, (position.top / y_scale) - 30, realPaint);

            realPaint.setStyle(Paint.Style.STROKE);
            realPaint.setStrokeWidth(5);

            canvas.drawRect(new Rect((int)(position.left / x_scale), (int)(position.top / y_scale),
                    (int)(position.right / x_scale), (int)(position.bottom / y_scale)), realPaint);
        }
    }
}
