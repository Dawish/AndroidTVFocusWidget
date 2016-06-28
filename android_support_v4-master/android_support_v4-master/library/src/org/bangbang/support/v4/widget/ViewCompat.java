package org.bangbang.support.v4.widget;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

public class ViewCompat {
	
	private static IViewCompat sImpl;

	static {
		sImpl = new ViewCompatNull();
		if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
			sImpl = new ViewCompatHoneycomb();
		} else  {
//			String detailMessage = "not impled under 3.0";
//			throw new IllegalStateException(detailMessage);

			sImpl = new ViewCompatDonut();
		}
		
		// debug
//		sImpl = new ViewCompatDonut();
	}

	public static void offsetChildrenTopAndBottom(ViewGroup group, int offset) {
		sImpl.offsetChildrenTopAndBottom(group, offset);
	}
	

	public static void offsetChildrenLeftAndRight(ViewGroup group, int offset) {
       sImpl.offsetChildrenLeftAndRight(group, offset);
	}
	
	
	public static void setIntField(Object o, String fieldName, int value) {
		Field field;
		try {
			field = View.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.setInt(o, value);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	static class ViewCompatNull implements IViewCompat {

		@Override
		public void offsetChildrenTopAndBottom(ViewGroup group, int offset) {
			final int count = group.getChildCount();

	        for (int i = 0; i < count; i++) {
	            final View v = group.getChildAt(i);
	            v.offsetTopAndBottom(offset);
	        }
		}

		@Override
		public void offsetChildrenLeftAndRight(ViewGroup group, int offset) {
			final int count = group.getChildCount();

	        for (int i = 0; i < count; i++) {
	            final View v = group.getChildAt(i);
	            v.offsetLeftAndRight(offset);
	        }
		}
		
	}
	
	static class ViewCompatDonut implements IViewCompat {
	
			@Override
			public void offsetChildrenTopAndBottom(ViewGroup group, int offset) {
				final int count = group.getChildCount();

		        for (int i = 0; i < count; i++) {
		            final View v = group.getChildAt(i);
//		            v.mTop += offset;
//		            v.mBottom += offset;
//		            v.setTop(v.getTop() + offset);
//		            v.setBottom(v.getBottom() + offset);
		            setIntField(v, "mTop", v.getTop() + offset);
		            setIntField(v, "mBottom", v.getBottom() + offset);
		        }
			}
	
			@Override
			public void offsetChildrenLeftAndRight(ViewGroup group, int offset) {
		        final int count = group.getChildCount();
	
		        for (int i = 0; i < count; i++) {
		            final View v = group.getChildAt(i);
	//	            v.mTop += offset;
	//	            v.mBottom += offset;
	//	            v.setLeft(v.getLeft() + offset);
	//	            v.setRight(v.getRight() + offset);
		            setIntField(v, "mLeft", v.getLeft() + offset);
		            setIntField(v, "mRight", v.getRight() + offset);
		        }
				
			}
			
		}

	static class ViewCompatHoneycomb implements IViewCompat {
		@SuppressLint("NewApi")
		public void offsetChildrenTopAndBottom(ViewGroup group, int offset) {
	        final int count = group.getChildCount();

	        for (int i = 0; i < count; i++) {
	            final View v = group.getChildAt(i);
//	            v.mTop += offset;
//	            v.mBottom += offset;
	            v.setTop(v.getTop() + offset);
	            v.setBottom(v.getBottom() + offset);
	        }
		}
		
		@SuppressLint("NewApi")
		public void offsetChildrenLeftAndRight(ViewGroup group, int offset) {
	        final int count = group.getChildCount();

	        for (int i = 0; i < count; i++) {
	            final View v = group.getChildAt(i);
//	            v.mTop += offset;
//	            v.mBottom += offset;
	            v.setLeft(v.getLeft() + offset);
	            v.setRight(v.getRight() + offset);
	        }
		}
	}

}

interface IViewCompat {

	void offsetChildrenTopAndBottom(ViewGroup group, int offset);

	void offsetChildrenLeftAndRight(ViewGroup group, int offset);
	
}

