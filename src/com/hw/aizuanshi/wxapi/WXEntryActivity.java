package com.hw.aizuanshi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hw.aizuanshi.ShareMeActivity;
import com.hw.aizuanshi.utils.Config;
import com.hw.aizuanshi.utils.Parameters;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Parameters.WEIXIN_APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	public void onReq(BaseReq req) {

	}

	public void onResp(BaseResp resp) {
		Config config = Config.getSingle(this);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Toast.makeText(this, "分享成功...", Toast.LENGTH_LONG).show();
			System.out.println("Parameters.SHARETYPE : " + Parameters.SHARETYPE);
			if (Parameters.SHARETYPE == 3) {
				config.setWechats();
			} else if (Parameters.SHARETYPE == 4) {
				config.setWechatmom();
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(this, "取消分享...", Toast.LENGTH_LONG).show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(this, "分享拒绝...", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		startActivityForResult(new Intent(this, ShareMeActivity.class), 0);
		this.finish();
	}

}