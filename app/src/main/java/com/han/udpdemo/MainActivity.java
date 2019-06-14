package com.han.udpdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	Button startUdpButton, endUdpButton, sendUdpButtton;
	TextView udpDataTextView;
	EditText dataEdidtext;

	UdpSocket socket;
	public static final String REFRESH_ACTION = "refreshData";
	MessageBroadcast messageBroadcast;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}


	public void refreshData(String content) {
		content = (udpDataTextView.getText() + " \n " + content);
		final String finalContent = content;

		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.d("Runnable", "Runnable");
				udpDataTextView.setText(finalContent);
				udpDataTextView.invalidate();

			}
		});

	}

	private void initView() {
		startUdpButton = findViewById(R.id.start_udp_connect_btn);
		endUdpButton = findViewById(R.id.end_udp_disconnect_btn);
		udpDataTextView = findViewById(R.id.udp_data_tv);
		sendUdpButtton = findViewById(R.id.send_udp_data_btn);
		dataEdidtext = findViewById(R.id.udp_send_data_et);
		startUdpButton.setOnClickListener(this);
		endUdpButton.setOnClickListener(this);
		sendUdpButtton.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.start_udp_connect_btn:
				startUdp();
				return;
			case R.id.end_udp_disconnect_btn:
				endUdp();
				return;
			case R.id.send_udp_data_btn:
				sendMessage();
				return;


		}
	}

	private void sendMessage() {
		if (socket != null) {
			if (dataEdidtext.getText().length() == 0) {
				return;
			}
			socket.sendMessage(dataEdidtext.getText().toString().getBytes());
		}

	}

	private void startUdp() {
		if (socket == null) {
			socket = new UdpSocket(MainActivity.this);
			socket.startUDPSocket();
		}
	}

	private void endUdp() {
		if (socket != null) {
			socket.stopUDPSocket();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (messageBroadcast == null) {
			messageBroadcast = new MessageBroadcast();
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(REFRESH_ACTION);// 只有持有相同的action的接受者才能接收此广播
		registerReceiver(messageBroadcast, intentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(messageBroadcast);
	}

	public class MessageBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("MessageBroadcast", "onReceive");
			String msg = intent.getStringExtra("Message");
			refreshData(msg);
		}
	}
}
