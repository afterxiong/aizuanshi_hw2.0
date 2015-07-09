package com.hw.aizuanshi.receiver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class InstallBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Query query = new DownloadManager.Query();
		long ids = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		query.setFilterById(ids);
		Cursor c = downloadManager.query(query);
		if (c != null && c.moveToFirst()) {
			try {
				String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				File f = new File(URLDecoder.decode(uri.replace("file://", ""), "utf-8"));
				Intent installIntent = new Intent();
				installIntent.setAction("android.intent.action.VIEW");
				installIntent.addCategory("android.intent.category.DEFAULT");
				installIntent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(installIntent);
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

}
