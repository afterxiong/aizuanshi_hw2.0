package com.hw.aizuanshi;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hw.aizuanshi.utils.Config;
import com.hw.aizuanshi.utils.Parameters;
import com.hw.aizuanshi.R;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class ShareMeActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = null;
	private String summary = "哈哈，这个APP太牛了,我每天都能够领取几千个钻石。。。";
	private String title = "天天领钻石";
	private TextView qq, wechats, qzones, wechatmom, gameTitle, before_number;
	private int id;
	private String name;
	private int number;
	private Tencent tencent;
	private IWXAPI api;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		Intent intent = getIntent();
		id = intent.getIntExtra("ids", 0);
		name = intent.getStringExtra("names");
		initView();
		configPlatforms();

	}

	private void configPlatforms() {
		tencent = Tencent.createInstance(Parameters.QQ_APP_ID, this.getApplicationContext());
		api = WXAPIFactory.createWXAPI(this, Parameters.WEIXIN_APP_ID);
		api.registerApp(Parameters.WEIXIN_APP_ID);
	}

	public void initView() {
		number = config.getQq() + config.getQzones() + config.getWechats() + config.getWechatmom();
		qq = (TextView) findViewById(R.id.tencen_qq);
		qq.setOnClickListener(this);
		wechats = (TextView) findViewById(R.id.wechats);
		wechats.setOnClickListener(this);
		qzones = (TextView) findViewById(R.id.qzones);
		qzones.setOnClickListener(this);
		wechatmom = (TextView) findViewById(R.id.wechatmom);
		wechatmom.setOnClickListener(this);

		before_number = (TextView) findViewById(R.id.before_number);
		String before = getResources().getString(R.string.before);
		before = String.format(before, number);
		before_number.setText(before);

		gameTitle = (TextView) findViewById(R.id.game_title);
		Drawable drawable = getResources().getDrawable(Parameters.iconId[id]);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		gameTitle.setCompoundDrawables(drawable, null, null, null);
		gameTitle.setText(name);

		String numberStr = getResources().getString(R.string.share_number);
		qq.setText(String.format(numberStr, new Object[] { config.getQq(), Config.QQ_MAX }));
		qzones.setText(String.format(numberStr, new Object[] { config.getQzones(), Config.QZONES_MAX }));
		wechats.setText(String.format(numberStr, new Object[] { config.getWechats(), Config.WECHATS_MAX }));
		wechatmom.setText(String.format(numberStr, new Object[] { config.getWechatmom(), Config.WECHATMOM_MAX }));

		findViewById(R.id.follow).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contacts();
			}
		});

	}

	public void raise(View view) {
		number = config.getQq() + config.getQzones() + config.getWechats() + config.getWechatmom();
		if (number >= 10) {
			config.setRank(2);
			startActivity(new Intent(ShareMeActivity.this, DeslActivity.class));
			this.finish();
		} else {
			Toast.makeText(this, "你今天的任务还没有完成...", Toast.LENGTH_LONG).show();
		}
	}

	public void finishActivity(View view) {
		this.finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tencen_qq:
			shareQQ();
			break;
		case R.id.wechats:
			shareWechats();
			break;
		case R.id.qzones:
			shareQzones();
			break;
		case R.id.wechatmom:
			shareWechatmom();
			break;

		default:
			break;
		}
		Toast.makeText(this, "正在分享，请稍等....", Toast.LENGTH_LONG).show();
	}

	public void shareQQ() {
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Parameters.TARGET_URL);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Parameters.IMAGE_URL);
		tencent.shareToQQ(this, params, new BaseUiListener());
		Parameters.SHARETYPE = 1;
	}

	public void shareQzones() {
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Parameters.TARGET_URL);
		ArrayList<String> list = new ArrayList<String>();
		list.add(Parameters.IMAGE_URL);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
		tencent.shareToQzone(this, params, new BaseUiListener());
		Parameters.SHARETYPE = 2;
	}

	public void shareWechats() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Parameters.TARGET_URL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = summary;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		msg.thumbData = thumb.getNinePatchChunk();
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		Parameters.SHARETYPE = 3;
		this.finish();
	}

	public void shareWechatmom() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Parameters.TARGET_URL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = summary;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		msg.thumbData = thumb.getNinePatchChunk();
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
		Parameters.SHARETYPE = 4;
		this.finish();
	}

	class BaseUiListener implements IUiListener {

		public void onCancel() {
			Toast.makeText(ShareMeActivity.this, "取消分享...", Toast.LENGTH_LONG).show();
		}

		public void onComplete(Object obj) {
			Toast.makeText(ShareMeActivity.this, "分享成功...", Toast.LENGTH_LONG).show();
			if (Parameters.SHARETYPE == 1) {
				config.setQq();
			} else if (Parameters.SHARETYPE == 2) {
				config.setQzones();
			}
			initView();
		}

		public void onError(UiError error) {
			Toast.makeText(ShareMeActivity.this, "分享拒绝...", Toast.LENGTH_LONG).show();
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		tencent.onActivityResult(requestCode, resultCode, data);
		initView();
	}

}
