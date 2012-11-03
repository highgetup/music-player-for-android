package com.xrlfy.musicplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class Triangle extends View{
	
	private final float radius = 28;
	
	private Path path;
	private Paint paint;
	private float width;
	private float height;
	private float sx;
	private float sy;
	private float px1;
	private float py1;
	private float px2;
	private float py2;

	public Triangle(Context context) {
		super(context);
		init();
	}

	public Triangle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Triangle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setAntiAlias(true);
		paint.setColor(0xFF000000);
		path=new Path();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		sx=(width-radius/2f)/2f;
		sy=(height-radius)/2f;
		px1=sx;
		py1=sy+radius;
		px2=sx+radius/2f;
		py2=sy+radius/2f;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		path.moveTo(sx, sy);
		path.lineTo(px1, py1);
		path.lineTo(px2, py2);
		path.lineTo(sx, sy);
		path.close();
		canvas.drawPath(path, paint);
	}
}
