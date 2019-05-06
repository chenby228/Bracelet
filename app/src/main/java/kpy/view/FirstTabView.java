package kpy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import kpy.bracelet.R;

/**
 * Created by KPY on 2017/4/17.
 */

public class FirstTabView extends View {

    private Paint paint;
    private int mBackHeight,mBackBottomHeight;
    private Path mBottompath;

    public FirstTabView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        int backColor = ContextCompat.getColor(context,R.color.lightslategray);
        paint = new Paint();
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
        mBottompath =new Path();

        mBackHeight = getResources().getDimensionPixelSize(R.dimen.tab_sport_top_height);
        mBackBottomHeight = getResources().getDimensionPixelSize(R.dimen.tab_sport_oval_half_height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getMeasuredWidth(), mBackHeight, paint);
        mBottompath.moveTo(0, mBackHeight);
        mBottompath.lineTo(getMeasuredWidth()/2, mBackHeight + mBackBottomHeight);
        mBottompath.lineTo(getMeasuredWidth(), mBackHeight);
        mBottompath.close();
        canvas.drawPath(mBottompath,paint);
    }
}
