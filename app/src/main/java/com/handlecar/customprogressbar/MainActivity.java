package com.handlecar.customprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.handlecar.customprogressbar.View.CustomProgressBar;

public class MainActivity extends AppCompatActivity {

	private CustomProgressBar my_cpb;
	private Handler mHandler;
	private Handler mHandler3;
	private CustomProgressBar my_cpb3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		my_cpb = (CustomProgressBar) findViewById(R.id.my_cpb);
		my_cpb3 = (CustomProgressBar) findViewById(R.id.my_cpb3);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				my_cpb.setProgress(msg.arg1 + 1);
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message message = mHandler.obtainMessage(1, i, 0);
					Message message3 = mHandler.obtainMessage(1, i, 0);
					mHandler.sendMessage(message);
					mHandler3.sendMessage(message3);
				}
			}
		}).start();

		mHandler3 = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				my_cpb3.setProgress(msg.arg1 + 1);
			}
		};
	}
}
