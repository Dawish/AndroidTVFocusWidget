package org.bangbang.support.v4.widget;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.View;

public class ReflectUtil {
	public static boolean view_setFrame(Object o, Class viewClazz, int left, int top, int right, int bottom){
		boolean changed = false;
		try {
			Method[] methods = viewClazz.getDeclaredMethods();
			Method method = null;
			for (Method m : methods) {
				if ("setFrame".equals(m.getName())) {
					method = m;
					break;
				}
			}
//			method = viewClazz.getDeclaredMethod("setFrame", new Class[]{Integer.class, Integer.class, Integer.class, Integer.class});
			if (null != method) {
				changed = (Boolean) method.invoke(o, new Object[]{new Integer(left), new Integer(top), new Integer(right), new Integer(bottom)});
			}
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return changed;
	}
	
	public static boolean view_isOpaque(Object viewChild){
		boolean opaque = true;
		try {
			Method[] methods = viewChild.getClass().getDeclaredMethods();
			Method method = null;
			for (Method m : methods) {
				if ("isOpaque".equals(m.getName())) {
					method = m;
					method.setAccessible(true);
					break;
				}
			}
			if (null != method) {
				opaque = (Boolean) method.invoke(new Object[]{(Object)null});
			}
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return opaque;
	}
	
	public static boolean view_isRootNamespace(Object o){
		boolean root = false;
		try {
			Method[] methods = View.class.getDeclaredMethods();
			Method method = null;
			for (Method m : methods) {
				if ("isRootNamespace".equals(m.getName())) {
					method = m;
					method.setAccessible(true);
					break;
				}
			}
			if (null != method) {
				root = (Boolean) method.invoke(o, new Object[]{(Object)null});
			}
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return root;
	}
	
	/**
	 * XXX how to get super's field and change it.
	 * 
	 * @param o
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static int getIntField(Object o, Class clazz, String fieldName) {
		int v = 0;
		Field field;
		try {

//			field = o.getClass().getDeclaredField(fieldName);
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			v = field.getInt(clazz);
		} catch (NoSuchFieldException e) {
//			 TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		return v;
	}
	
	public static void setIntField(Class clazz, String fieldName, int value) {
		Field field;
		try {
			field = clazz.getField(fieldName);
			field.setInt(clazz, value);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean getBooleanField(Class clazz, String fieldName) {
		boolean v = false;
		Field field;
		try {
			field = clazz.getField(fieldName);
			v = field.getBoolean(clazz);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return v;
	}
	
	public static void setBooleanField(Class clazz, String fieldName, boolean value) {
		Field field;
		try {
			field = clazz.getField(fieldName);
			field.setBoolean(clazz, value);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
