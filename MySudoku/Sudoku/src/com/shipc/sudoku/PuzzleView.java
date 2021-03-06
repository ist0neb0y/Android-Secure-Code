package com.shipc.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View{
	private static final String TAG = "Sudoku";
	private final Game game;
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();
	
	
	public PuzzleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.game= (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	//计算屏幕上每个单元格的大小，其宽度和高度分别等于整个视图宽度和高度的1/9。宽度和高度值都是浮点数。
	protected void onSizeChanged(int w,int h,int oldw,int oldh)
	{
		width = w/9f;
		height = h/9f;
		getRect(selX,selY,selRect);
		Log.d(TAG,"onSizeChanged:width "+width+", height "+height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	//每次需要更新视图的任何部分，android就会调用视图的onDraw()方法。简单起见，onDraw方法表面上就是从头开始重绘整个屏幕。实际上真正需要重绘的可能只是视图的一小部分。
	//具体区域由画布的裁剪矩形定义，android负责替你完成裁剪工作。
	@Override
	protected void onDraw(Canvas canvas)
	{
		Log.i(TAG,"PuzzleView:onDraw");
		//在画布上绘制了游戏盘面的背景，指定了背景颜色，绘制了一个矩形
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0,getWidth(),getHeight(),background);
		//draw the board...
		//Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		//Draw the minor grid lines
		for(int i=0;i<9;i++)
		{
			canvas.drawLine(0, i*height, getWidth(), i*height, light);
			canvas.drawLine(0,i*height+1,getWidth(),i*height+1,hilite);
			canvas.drawLine(i*width, 0, i*width, getHeight(), dark);
			canvas.drawLine(i*width+1,0,i*width+1,getHeight(),hilite);
		}
		//Draw the major grid lines
		for (int i=0;i<9;i++)
		{
			if(i%3!=0)
				continue;
			canvas.drawLine(0, i*height, getWidth(), i*height, dark);
			canvas.drawLine(0,i*height+1,getWidth(),i*height+1,hilite);
			canvas.drawLine(i*width, 0, i*width, getHeight(), dark);
			canvas.drawLine(i*width+1,0,i*width+1,getHeight(),hilite);
			
		}
		//draw th number...
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height*0.75f);
		foreground.setTextScaleX(width/height);
		foreground.setTextAlign(Paint.Align.CENTER);
		//
		FontMetrics fm = foreground.getFontMetrics();
		float x = width/2;
		float y = height/2-(fm.ascent+fm.descent)/2;
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				canvas.drawText(this.game.getTileString(i,j), i*width+x, j*height+y, foreground);
			}
		}
		//draw the hints...
		Paint hint = new Paint();
		int c[]={getResources().getColor(R.color.puzzle_hint_0),
				getResources().getColor(R.color.puzzle_hint_1),
				getResources().getColor(R.color.puzzle_hint_2),};
		Rect r = new Rect();
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				int movesleft = 9-game.getUsedTiles(i,j).length;
				if(movesleft <c.length)
				{
					getRect(i,j,r);
					hint.setColor(c[movesleft]);
					canvas.drawRect(r,hint);
				}
			}
		}
		//draw the selection...
		Log.d(TAG,"selRect= "+selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect,selected);
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		Log.d(TAG,"onKeyDown: keyCode="+keyCode+", event="+event);
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX,selY-1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX,selY+1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX-1,selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX+1,selY);
			break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:
			setSelectedTile(0);
			break;
		case KeyEvent.KEYCODE_1:
			setSelectedTile(1);
			break;
		case KeyEvent.KEYCODE_2:
			setSelectedTile(2);
			break;
		case KeyEvent.KEYCODE_3:
			setSelectedTile(3);
			break;
		case KeyEvent.KEYCODE_4:
			setSelectedTile(4);
			break;
		case KeyEvent.KEYCODE_5:
			setSelectedTile(5);
			break;
		case KeyEvent.KEYCODE_6:
			setSelectedTile(6);
			break;
		case KeyEvent.KEYCODE_7:
			setSelectedTile(7);
			break;
		case KeyEvent.KEYCODE_8:
			setSelectedTile(8);
			break;
		case KeyEvent.KEYCODE_9:
			setSelectedTile(9);
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			game.showKeypadOrError(selX,selY);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	private void select(int x,int y){
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX,selY,selRect);
		invalidate(selRect);
	}
	private void getRect(int x,int y,Rect rect)
	{
		rect.set((int)(x*width),(int)(y*height),(int)(x*width+width),(int)(y*height+height));
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction()!=MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		select((int)(event.getX()/width),(int)(event.getY()/height));
		game.showKeypadOrError(selX,selY);
		Log.d(TAG,"onTouchEvent: x " +selX+", y"+selY);
		return true;
	}
	
	//
	public void setSelectedTile(int tile)
	{
		if(game.setTileIfValid(selX,selY,tile))
		{
			invalidate();
		}
		else
		{
			Log.d(TAG,"setSelectedTile: invalid: "+tile);
			startAnimation(AnimationUtils.loadAnimation(game,R.anim.shake));
		}
	}
	
}
