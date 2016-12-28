package com.handlecar.customprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.handlecar.customprogressbar.View.CustomProgressBar;

public class MainActivity extends AppCompatActivity {

	private CustomProgressBar my_cpb;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		my_cpb = (CustomProgressBar) findViewById(R.id.my_cpb);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				my_cpb.setProgress(msg.arg1);
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message message = mHandler.obtainMessage(1, i, 0);
					mHandler.sendMessage(message);
				}
			}
		}).start();
	}
}
