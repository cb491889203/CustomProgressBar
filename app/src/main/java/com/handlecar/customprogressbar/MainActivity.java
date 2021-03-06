package com.handlecar.customprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.handlecar.customprogressbar.View.CustomProgressBar;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private CustomProgressBar my_cpb;
	private CustomProgressBar my_cpb2;
	private CustomProgressBar my_cpb3;
	private CustomProgressBar my_cpb4;
	private Handler mHandler;
	private Handler mHandler2;
	private Handler mHandler3;
	private Handler mHandler4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		my_cpb = (CustomProgressBar) findViewById(R.id.my_cpb);
		my_cpb2 = (CustomProgressBar) findViewById(R.id.my_cpb2);
		my_cpb3 = (CustomProgressBar) findViewById(R.id.my_cpb3);
		my_cpb4 = (CustomProgressBar) findViewById(R.id.my_cpb4);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.i(TAG, "handleMessage前: msg.what = " + msg.what);
				if (msg.what <= my_cpb.getMax()) {
					int i = msg.what;
					my_cpb.setProgress(msg.what);
					mHandler.sendEmptyMessageDelayed(++i, 1000);
					Log.i(TAG, "handleMessage后: msg.what = " + msg.what);
				}
			}
		};
		mHandler.sendEmptyMessageDelayed(1, 1000);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int i = 0;
//				while (i < my_cpb.getMax()) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					mHandler.sendEmptyMessage(i);
//					i++;
//				}
//			}
//		}).start();

		mHandler2 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what <= my_cpb2.getMax()) {
					int i = msg.what;
					my_cpb2.setProgress(i);
					i++;
					mHandler2.sendEmptyMessageDelayed(i, 300);
				}
			}
		};
		mHandler2.sendEmptyMessageDelayed(0, 300);

		mHandler3 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what <= my_cpb3.getMax()) {
//					my_cpb3.setProgress(msg.what);
//					mHandler3.sendEmptyMessageDelayed(msg.what++, 500);
					int i = msg.what;
					my_cpb3.setProgress(i);
					i++;
					mHandler3.sendEmptyMessageDelayed(i, 50);
				}
			}
		};
		mHandler3.sendEmptyMessageDelayed(0, 50);

		mHandler4 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what <= my_cpb4.getMax()) {
//					my_cpb3.setProgress(msg.what);
//					mHandler3.sendEmptyMessageDelayed(msg.what++, 500);
					int i = msg.what;
					my_cpb4.setProgress(i);
					i++;
					mHandler4.sendEmptyMessageDelayed(i, 100);
				}
			}
		};
		mHandler4.sendEmptyMessageDelayed(0, 200);
	}
}
