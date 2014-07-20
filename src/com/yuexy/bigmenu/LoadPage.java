package com.yuexy.bigmenu;

import java.util.ArrayList;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.bigmenu.R;

public class LoadPage extends ListView
{
	private int VELOCITY = 6;
	
	private final int LOAD_UP = 0;
	private final int LOAD_DOWN = 1;
	private final int SUB_UP = 2;
	private final int SUB_DOWN = 3;
	
	private View loadPage = null;
	private int loadPage_height = 0;
	private final int LOADPAGE_DOWN = 0;
	private final int LOADPAGE_UP = 1;
	private int LOADPAGE_STATE = LOADPAGE_DOWN;
	
	private ArrayList<View> mSubPages;
	private int[] mSubPages_height;
	private int[] SUBPAGE_STATE;
	private int mSubNum = 0;
	
	private Context mContext;
	
	/**
	 * @param v 速度大于0小于50
	 * @return 是否设置成功
	 */
	public boolean setVelocity(int v)
	{
		if(v <= 0 || v > 50)
		{
			return false;
		}
		else
		{
			this.VELOCITY = v;
			return true;
		}
	}
	
	/**
	 * @param 引导页
	 * @return 是否添加成功，必须首先添加引导页
	 */
	public boolean setLoadPage(View loadpage)
	{
		if(getHeaderViewsCount() == 0)
		{
			loadPage = loadpage;
			addHeaderView(loadPage);
			measureView(loadPage);
			loadPage_height = loadPage.getMeasuredHeight();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 引导页向上收起
	 * @return 是否收起成功
	 */
	public boolean loadPagePackUp()
	{
		if(loadPage == null) {return false;}
		if(LOADPAGE_STATE == LOADPAGE_UP) {return false;}
		LOADPAGE_STATE = LOADPAGE_UP;
		new Thread()
		{
			public void run()
			{
				Message msg;
				while(loadPage.getPaddingTop() > (-loadPage_height))
				{
					msg = mHandler.obtainMessage();
					msg.what = LOAD_UP;
					mHandler.sendMessage(msg);
					try 
    				{
    					sleep(5);
    				} 
    				catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
				}
			}
		}.start();
		return true;
	}
	
	/**
	 * 引导页向下放下
	 * @return 是否放下成功
	 */
	public boolean loadPagePackDown()
	{
		if(loadPage == null) {return false;}
		if(LOADPAGE_STATE == LOADPAGE_DOWN) {return false;}
		LOADPAGE_STATE = LOADPAGE_DOWN;
		new Thread()
		{
			public void run()
			{
				Message msg;
				while(loadPage.getPaddingTop() < 0)
				{
					msg = mHandler.obtainMessage();
					msg.what = LOAD_DOWN;
					mHandler.sendMessage(msg);
					try 
    				{
    					sleep(5);
    				} 
    				catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
				}
			}
		}.start();
		return true;
	}
	
	/**
	 * 增加子页
	 * @param L View的ArrayList
	 * @return 是否添加成功
	 */
	public boolean addSubPage(ArrayList<View> L)
	{
		if(getHeaderViewsCount() == 0) {return false;}
		if(L == null) {return false;}
		if(L.size() >= 50) {return false;}
		mSubPages = L;
		
		for(int i = 0; i < mSubPages.size(); i++)
		{
			addHeaderView(mSubPages.get(i));
			measureView(mSubPages.get(i));
			mSubPages_height[i] = mSubPages.get(i).getMeasuredHeight();
			mSubPages.get(i).setPadding(mSubPages.get(i).getPaddingLeft(), 
					mSubPages.get(i).getPaddingTop(), 
					mSubPages.get(i).getPaddingRight(), 
					-mSubPages_height[i]);
			SUBPAGE_STATE[i] = LOADPAGE_UP;
		}
		return true;
	}

	
	public boolean subPagePackDown(int num)
	{
		if(mSubPages.get(num) == null) {return false;}
		if(SUBPAGE_STATE[num] == LOADPAGE_DOWN) {return false;}
		SUBPAGE_STATE[num] = LOADPAGE_DOWN;
		mSubNum = num;
		new Thread()
		{
			public void run()
			{
				Message msg;
				while(mSubPages.get(mSubNum).getPaddingBottom() < 0)
				{
					msg = mHandler.obtainMessage();
					msg.what = SUB_DOWN;
					mHandler.sendMessage(msg);
					try 
    				{
    					sleep(5);
    				} 
    				catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
				}
			}
		}.start();
		return true;
	}
	
	public boolean subPagePackUp(int num)
	{
		if(mSubPages.get(num) == null) {return false;}
		if(SUBPAGE_STATE[num] == LOADPAGE_UP) {return false;}
		SUBPAGE_STATE[num] = LOADPAGE_UP;
		mSubNum = num;
		new Thread()
		{
			public void run()
			{
				Message msg;
				while(mSubPages.get(mSubNum).getPaddingBottom() > (-mSubPages_height[mSubNum]))
				{
					msg = mHandler.obtainMessage();
					msg.what = SUB_UP;
					mHandler.sendMessage(msg);
					try 
    				{
    					sleep(5);
    				} 
    				catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
				}
			}
		}.start();
		return true;
	}
	
	/**
	 * 初始化视图，最后必须调用
	 */
	public void Invalidate()
	{
		ArrayList<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems, R.layout.list, new String[] {"hander" ,"name" ,"gender"} , new int[]{R.id.hander, R.id.name ,R.id.info});
		setAdapter(simpleAdapter);
	}
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case LOAD_UP:
				loadPage.setPadding(loadPage.getPaddingLeft(), 
						loadPage.getPaddingTop() - VELOCITY, 
						loadPage.getPaddingRight(), 
						loadPage.getPaddingBottom());
				break;
				
			case LOAD_DOWN:
				loadPage.setPadding(loadPage.getPaddingLeft(), 
						loadPage.getPaddingTop() + VELOCITY, 
						loadPage.getPaddingRight(), 
						loadPage.getPaddingBottom());
				break;
				
			case SUB_UP:
				mSubPages.get(mSubNum).setPadding(mSubPages.get(mSubNum).getPaddingLeft(), 
						mSubPages.get(mSubNum).getPaddingTop(), 
						mSubPages.get(mSubNum).getPaddingRight(), 
						mSubPages.get(mSubNum).getPaddingBottom() - VELOCITY);
				break;
				
			case SUB_DOWN:
				mSubPages.get(mSubNum).setPadding(mSubPages.get(mSubNum).getPaddingLeft(), 
						mSubPages.get(mSubNum).getPaddingTop(), 
						mSubPages.get(mSubNum).getPaddingRight(), 
						mSubPages.get(mSubNum).getPaddingBottom() + VELOCITY);
				break;
				
			default:
				break;
			}
		}
	};
	
	public LoadPage(Context context)
	{
		this(context,null);
	}
	public LoadPage(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		mSubPages_height = new int[50];
		SUBPAGE_STATE = new int[50];
		init(context);
	}
	public LoadPage(Context context, AttributeSet attrs, int defStyle) 
	{
		this(context,null);
	}

	private void init(Context context)
	{
		
	}
	
	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if(p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if(lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		}
		else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
}
