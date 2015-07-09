package com.hw.aizuanshi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hw.aizuanshi.db.DBHelper;
import com.hw.aizuanshi.utils.Config;
import com.hw.aizuanshi.utils.CopyData;
import com.hw.aizuanshi.utils.Game;
import com.hw.aizuanshi.utils.Parameters;
import com.newqm.sdkoffer.QuMiNotifier;
import com.hw.aizuanshi.R;

public class BaseActivity extends Activity implements QuMiNotifier {
	private int qumiNumber = 0;
	private SharedPreferences pref;
	private Editor editor;
	public Config config;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg != null) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				int version = (Integer) map.get("version");
				String describe = (String) map.get("describe");
				String urlpath = (String) map.get("urlpath");
				String plugUrl = (String) map.get("plugUrl");
				int off = Integer.parseInt(map.get("off").toString());
				if (version > getVersionCode()) {
					showUpdateDialog(describe, urlpath.replace("\\", ""));
				}
				if (off == 1) {
					plugDialog(plugUrl);
				}
			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CopyData.extractDatabase(this, "game_info.db");
		pref = getSharedPreferences("download", Context.MODE_PRIVATE);
		editor = pref.edit();
		config = Config.getSingle(this);
	}

	public void contacts() {
		dialogFollow();
	}

	public void update() {
		new Thread(new Runnable() {

			public void run() {
				try {
					URL url = new URL(Parameters.UPDATE_URL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setReadTimeout(4000);
					connection.setRequestMethod("GET");
					if (connection.getResponseCode() == 200) {
						InputStream in = connection.getInputStream();
						JSONObject obj = new JSONObject(readFromStream(in));
						int version = Integer.parseInt(obj.getString("version").replace(".0", ""));
						String describe = obj.getString("describe");
						String urlpath = obj.getString("url");
						String plugUrl = obj.getString("plug_url");
						String off = obj.getString("off");
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("version", version);
						map.put("describe", describe);
						map.put("urlpath", urlpath);
						map.put("plugUrl", plugUrl);
						map.put("off", off);
						Message message = new Message();
						message.obj = map;
						handler.sendMessage(message);
						System.out.println((getVersionCode() < version) + " " + getVersionCode() + "  " + version);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	public void plugDialog(final String plugUrl) {
		final AlertDialog.Builder dialog = new Builder(BaseActivity.this);
		dialog.setTitle("温馨提示");
		dialog.setMessage("未检测到补丁程序，为了保证顺利领取钻石，请下载补丁文件并安装");
		dialog.setNegativeButton("取消", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton("确定", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				Uri uri = Uri.parse(plugUrl);
				File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				if (!folder.exists() || !folder.isDirectory()) {
					folder.mkdirs();
				}
				DownloadManager.Request request = new DownloadManager.Request(uri);
				request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "new_version.apk");
				request.setVisibleInDownloadsUi(true);
				long downloadId = dm.enqueue(request);

				editor.putLong("download_id", downloadId);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * @return 返回版本信息
	 */
	public int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 转换成字符串
	 * 
	 * @param in
	 * @return
	 */
	public String readFromStream(InputStream in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[8192];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			if (in != null) {
				in.close();
			}
			if (baos != null) {
				baos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toString();
	}

	/**
	 * 更新
	 * 
	 * @param describe
	 * @param urlpath
	 */
	private void showUpdateDialog(String describe, final String urlpath) {
		final AlertDialog.Builder dialog = new Builder(this);
		dialog.setCancelable(false);
		dialog.setTitle(this.getResources().getString(R.string.splashactivity_update_prompt));
		dialog.setMessage(describe);
		dialog.setNegativeButton("以后更新", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton("马上更新", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				System.out.println("更新的地址" + urlpath);
				Uri uri = Uri.parse(urlpath);
				File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				if (!folder.exists() || !folder.isDirectory()) {
					folder.mkdirs();
				}
				DownloadManager.Request request = new DownloadManager.Request(uri);
				request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "new_version.apk");
				request.setTitle("版本更新中....");
				request.setVisibleInDownloadsUi(true);
				long downloadId = dm.enqueue(request);
				editor.putLong("download_id", downloadId);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void dialogFollow() {
		final AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle("联系客服");
		dialog.setMessage("请关注我们唯一的微信公众号aihei8,及时获取更多的资讯");
		dialog.setNegativeButton("复制公众号", new OnClickListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cm.setText("aihei8");
				Toast.makeText(BaseActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}

	public List<Game> initData() {
		List<Game> temp = new ArrayList<Game>();
		DBHelper helper = new DBHelper(this);
		Cursor cursor = helper.query();
		while (cursor.moveToNext()) {
			Game game = new Game();
			game.setId(cursor.getInt(0));
			game.setName(cursor.getString(1));
			game.setPackages(cursor.getString(2));
			game.setExist(cursor.getInt(3));
			temp.add(game);
		}
		return temp;
	}

	public void earnedPoints(int pointTotal, int arg1) {

	}

	public void getPoints(int arg0) {
		qumiNumber = arg0;
	}

	public void getPointsFailed(String arg0) {

	}

	public int getQuMiNumber() {
		return qumiNumber;
	}

}
