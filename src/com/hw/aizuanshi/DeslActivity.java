package com.hw.aizuanshi;

import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hw.aizuanshi.utils.Config;
import com.hw.aizuanshi.utils.Parameters;
import com.hw.aizuanshi.R;

public class DeslActivity extends BaseActivity {
	private TextView gameTitle, rank, tip;
	private int id;
	private String name;
	private int number;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_des);
		Intent intent = getIntent();
		id = intent.getIntExtra("ids", 0);
		name = intent.getStringExtra("names");
		initView();
	}

	// private void initAd() {
	// AdView adView = new AdView(this, AdSize.FIT_SCREEN);
	// LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
	// adLayout.addView(adView);
	// }

	public void goShare(View view) {
		startShareActivity();
	}

	public void goGain(View view) {
		tipDialog();
	}

	public void tipDialog() {
		final AlertDialog.Builder dialog = new Builder(this);
		if (config.getrRank() <= 1) {
			dialog.setTitle("温馨提示");
			dialog.setMessage(getResources().getString(R.string.tip_text1));
			dialog.setNegativeButton("免费赚积分", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					startShareActivity();
				}
			});
		} else {
			dialog.setTitle("温馨提示");
			String rankTextTip = Parameters.rankId[config.getrRank()];
			dialog.setMessage("当前" + rankTextTip + "数量超现，请通过升级界面免费升级成Vip2或者更高级别");
			dialog.setNegativeButton("免费去升级", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(DeslActivity.this, ScoreActivity.class);
					intent.putExtra("ids", id);
					intent.putExtra("names", name);
					startActivityForResult(intent, 1);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			});
		}
		dialog.show();
	}

	private void initView() {
		number = config.getQq() + config.getQzones() + config.getWechats() + config.getWechatmom();
		if (number >= Config.SHARE_NUMBER_MAX) {
			if (config.getrRank() == 0) {
				config.setRank(1);
			}
		}
		gameTitle = (TextView) findViewById(R.id.game_title);
		tip = (TextView) findViewById(R.id.tip);
		rank = (TextView) findViewById(R.id.rank);

		String rankStr = getResources().getString(R.string.rank);
		rankStr = String.format(rankStr, Parameters.rankId[config.getrRank()]);
		rank.setText(rankStr);

		Drawable drawable = getResources().getDrawable(Parameters.iconId[id]);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		gameTitle.setCompoundDrawables(drawable, null, null, null);
		gameTitle.setText(name);

		String str = "";
		int[] num = { 1000, 2000, 3000 };
		for (int i = 0; i < 10; i++) {
			int q1 = new Random().nextInt(999);
			int q2 = new Random().nextInt(9999);
			int n = new Random().nextInt(2);
			String s = "恭喜" + q1 + "***" + q2 + "领取了" + num[n] + "颗钻石" + "   ";
			str = str + s;
		}

		tip.setText(str);

		findViewById(R.id.follow).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contacts();
			}
		});

	}

	public void finishActivity(View view) {
		this.finish();
	}

	public void startShareActivity() {
		Intent intent = new Intent(DeslActivity.this, ShareMeActivity.class);
		intent.putExtra("ids", id);
		intent.putExtra("names", name);
		startActivityForResult(intent, 0);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		initView();
	}

}
