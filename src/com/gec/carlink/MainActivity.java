package com.gec.carlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;

import com.gec.carlink.util.Toaster;
import com.gec.carlink.widget.CircleSteerView;
import com.gec.carlink.widget.CircleSteerView.OnDirectionChangedListener;
import com.gec.carlink.widget.Direction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

/**
 * 主控制类
 * 
 * @author sig
 * @version 1.0
 */
public class MainActivity extends Activity {
	// 调试用
	private static final String TAG = MainActivity.class.getCanonicalName();
	// 服务器IP地址
	private static final String IP = "192.168.4.1";

	// 开启WiFi请求码
	private static final int REQUEST_WIFI = 0x01;
	// 服务器端口
	private static final int PORT = 333;

	// 协议常量
	private static byte[] data = new byte[] { 0x01 };

	// 提示内容
	private TextView mTextView = null;
	// 方向盘
	private CircleSteerView mCircleSteerView = null;

	private Socket mSocket = null;
	// 输出流
	private OutputStream mOutS = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) this.findViewById(R.id.text_view);
		mCircleSteerView = (CircleSteerView) this.findViewById(R.id.circle_steer_view);

		mCircleSteerView.setOnDirectionChangedListener(new OnDirectionChangedListener() {

			@Override
			public void onDirectionChanged(Direction direction) {
				switch (direction) {

				case CENTER:
					Log.d(TAG, "[1] --> 中央");
					mTextView.setText("原地待命...");
					data[0] = 0x09;
					writeStream(data);
					break;

				case UP_DIR:
					Log.d(TAG, "[2] --> 前");
					mTextView.setText("向前突进...");
					data[0] = 0x01;
					writeStream(data);
					break;

				case DOWN_DIR:
					Log.d(TAG, "[3] --> 后");
					mTextView.setText("向后撤退...");
					data[0] = 0x02;
					writeStream(data);
					break;

				case LEFT_DIR:
					Log.d(TAG, "[4] --> 左");
					mTextView.setText("向左拐...");
					data[0] = 0x03;
					writeStream(data);
					break;

				case RIGHT_DIR:
					Log.d(TAG, "[5] --> 右");
					mTextView.setText("向右拐...");
					data[0] = 0x04;
					writeStream(data);
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		checkNetworkInfo();
	}

	@Override
	protected void onStop() {
		close();

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_WIFI) {
			// Handler延时3s后再执行UDP广播线程，防止程序崩溃退出
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					new TcpThread().start();
				}
			}, 3 * 1000);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		close();

		super.onBackPressed();
	}

	/**
	 * 检测网络状态
	 */
	@SuppressWarnings("deprecation")
	private void checkNetworkInfo() {
		// 获取系统服务
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取WiFi信号的状态
		State wifiState = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		Log.d(TAG, "[1] --> WiFi state:" + wifiState.toString());

		if (wifiState == State.CONNECTED || wifiState == State.CONNECTING) { // 判断连接状态
			// 执行UDP广播线程
			new TcpThread().start();
			return;
		}

		Toaster.shortToastShow(this, "都什么年代了，还塞网络o(╯□╰)o");
		// 启动WiFi设置页面并回调onActivityResult()方法
		startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_WIFI);
	}

	/**
	 * TCP连接线程
	 */
	private class TcpThread extends Thread {

		@Override
		public void run() {
			InputStream inS = null;

			try {
				mSocket = new Socket(IP, PORT);
				mOutS = mSocket.getOutputStream();
				inS = mSocket.getInputStream();

				byte[] data = new byte[512];
				int len;
				while ((len = inS.read(data)) > 0) {
					String tcpResponse = byteArray2HexStr(data, 0, len);
					Log.d(TAG, "[3] -->" + tcpResponse);
				}
			} catch (IOException e) {
				// 屏蔽Log错误信息
				// throw new RuntimeException("输入输出异常", e);
			}
		}
	}

	/**
	 * 字节数组转化为十六进制字符串
	 * 
	 * @param data
	 *            字节数组
	 * @param offset
	 *            起点
	 * @param byteCount
	 *            字节数组长度
	 * @return
	 */
	private String byteArray2HexStr(byte[] data, int offset, int byteCount) {
		String ret = "";

		for (int i = offset; i < byteCount; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			String newHex = String.format(Locale.getDefault(), "%02s", hex);
			// if (hex.length() == 1) {
			// hex = '0' + hex;
			// }
			ret += newHex.toUpperCase(Locale.getDefault());
		}
		return ret;
	}

	/**
	 * 关闭Socket
	 */
	private void close() {
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				throw new RuntimeException("输入输出异常", e);
			}
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param data
	 */
	private void writeStream(byte[] data) {
		try {
			if (mOutS != null) {
				// 写数据到输出流
				mOutS.write(data);
				// 调用write()之后数据依然留在缓存中，必须调用flush()，才能将数据真正发送出去
				mOutS.flush();
			}
		} catch (IOException e) {
			// 涉及UI操作的必须在主线程中运行，runOnUiThread()的原理即为Handler消息处理机制
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toaster.shortToastShow(MainActivity.this, "连接超时，被服务器君抛弃了::>_<::");
					// 结束程序
					MainActivity.this.finish();
				}
			});
		}
	}
}
