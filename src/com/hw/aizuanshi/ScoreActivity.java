package com.hw.aizuanshi;

import net.slidingmenu.tools.os.OffersManager;
import net.slidingmenu.tools.os.PointsManager;
import net.slidingmenu.tools.video.VideoAdManager;
import net.slidingmenu.tools.video.listener.VideoAdListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.aizuanshi.utils.Parameters;
import com.newqm.sdkoffer.QuMiConnect;
import com.hw.aizuanshi.R;

public class ScoreActivity extends BaseActivity {
	private TextView gameTitle, gold;
	private int id;
	private String name;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		Intent intent = getIntent();
		id = intent.getIntExtra("ids", 0);
		name = intent.getStringExtra("names");
		initView();
		initAd();
	}

	private void initAd() {
		OffersManager.getInstance(this).onAppLaunch();
		VideoAdManager.getInstance(this).requestVideoAd();
	}

	public void finishActivity(View view) {
		this.finish();
	}

	public void pass2(View view) {
		OffersManager.getInstance(this).showOffersWall();
	}

	public void pass3(View view) {
		QuMiConnect.getQumiConnectInstance(this).initOfferAd(this);
		QuMiConnect.getQumiConnectInstance().showOffers(this);
	}

	public void pass4(View view) {
		Toast.makeText(this, "建议在WIFI情况下使用该通道...", Toast.LENGTH_LONG).show();
		VideoAdManager.getInstance(this).showVideo(this, new VideoAdListener() {

			public void onVideoPlayFail() {
			}

			public void onVideoPlayComplete() {
				PointsManager.getInstance(ScoreActivity.this).awardPoints(5);
				Toast.makeText(ScoreActivity.this, "您获得了5个金币的奖励", Toast.LENGTH_SHORT).show();
				initView();
			}

			public void onVideoCallback(boolean callback) {
			}

			public void onVideoPlayInterrupt() {
			}

			public void onDownloadComplete(String id) {
			}

			public void onNewApkDownloadStart() {
			}

			public void onVideoLoadComplete() {

			}
		});
	}

	public void promote(View view) {
		if (amount() > 50) {
			boolean isSuccess = PointsManager.getInstance(this).spendPoints(50);
			if (!isSuccess) {
				QuMiConnect.getQumiConnectInstance().spendPoints(this, 50);
			}
			config.setRank(config.getrRank() + 1);
			initView();
			Toast.makeText(this, "升级成功...", Toast.LENGTH_SHORT).show();
		} else {
			final AlertDialog.Builder dialog = new Builder(this);
			dialog.setTitle("温馨提示");
			dialog.setMessage("你升级的金币数量不够，无法升级。请通过金币免费获取通道来获取金币");
			dialog.setNegativeButton("选择其他的通道", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	public void feedback(View view) {
		dialogFollow();
	}

	public void initView() {

		gold = (TextView) findViewById(R.id.gold);
		String goldNumber = getResources().getString(R.string.gold_tip);
		goldNumber = String.format(goldNumber, amount());
		gold.setText(goldNumber);

		gameTitle = (TextView) findViewById(R.id.game_title);
		gameTitle.setText(name);
		Drawable drawable = getResources().getDrawable(Parameters.iconId[id]);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		gameTitle.setCompoundDrawables(drawable, null, null, null);

		findViewById(R.id.follow).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialogFollow();
			}
		});

	}

	public int amount() {
		int pointsBalance = PointsManager.getInstance(this).queryPoints();
		int qumiNun = getQuMiNumber();
		return pointsBalance + qumiNun;
	}

	protected void onDestroy() {
		super.onDestroy();
		OffersManager.getInstance(this).onAppExit();
	}

}
