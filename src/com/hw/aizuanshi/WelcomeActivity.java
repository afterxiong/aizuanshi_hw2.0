package com.hw.aizuanshi;

import java.util.ArrayList;
import java.util.List;

import net.slidingmenu.tools.AdManager;
import net.slidingmenu.tools.st.SpotManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import com.hw.aizuanshi.db.DBHelper;
import com.hw.aizuanshi.utils.Game;
import com.hw.aizuanshi.utils.Parameters;
import com.newqm.sdkoffer.QuMiConnect;

public class WelcomeActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initAd();
		startActivity(new Intent(this, MainActivity.class));
		this.finish();
		checkApks();
	}

	private void initAd() {
		/** 有米 */
		AdManager.getInstance(this).init(Parameters.YOU_MI_ID, Parameters.YOU_MI_KEY);
		// 启动广告缓存
		SpotManager.getInstance(this).loadSpotAds();
		// 竖屏动画
		SpotManager.getInstance(this).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
		/** 趣米 */
		QuMiConnect.ConnectQuMi(this, Parameters.QU_MI_APP_ID, Parameters.QU_MI_KEY);
	}

	private void checkApks() {
		DBHelper helper = new DBHelper(this);
		List<Game> temp = initData();
		PackageManager pm = this.getPackageManager();
		for (int j = 0; j < temp.size(); j++) {
			Game game = temp.get(j);
			List<PackageInfo> info = pm.getInstalledPackages(0);
			for (PackageInfo p : info) {
				if (game.getPackages().equals(p.packageName)) {
					helper.updateSate(game.getId());
				}
			}
		}
	}
}
