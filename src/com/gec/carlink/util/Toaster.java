package com.gec.carlink.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 消息弹框类
 * 
 * @author sig
 * @version 1.0
 */
public class Toaster {

	/**
	 * 消息弹框3.5s
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            文本字符串
	 */
	public static void longToastShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * 消息弹框3.5s
	 * 
	 * @param context
	 *            上下文
	 * @param resId
	 *            资源索引
	 */
	public static void longToastShow(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	/**
	 * 消息弹框2s
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            文本字符串
	 */
	public static void shortToastShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 消息弹框2s
	 * 
	 * @param context
	 *            上下文
	 * @param resId
	 *            资源索引
	 */
	public static void shortToastShow(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
}
