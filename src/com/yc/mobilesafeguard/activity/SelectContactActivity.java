package com.yc.mobilesafeguard.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.yc.mobilesafeguard.R;

public class SelectContactActivity extends Activity {
	private ListView lv_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		final List<Map<String,String>> data = getContacts(this);
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setAdapter(new SimpleAdapter(this, data,
				R.layout.select_contact_item, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));
		lv_contact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = data.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();
			}
		});
	}

	public List<Map<String,String>> getContacts(Context context) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("contact_id"));
			if (id != null) {
				Cursor dataCursor = resolver.query(dataUri, new String[] {
						"data1", "mimetype" }, "raw_contact_id=?",
						new String[] { id }, null);
				Map<String,String> map = new HashMap<String, String>();
				while (dataCursor.moveToNext()) {
					String data = dataCursor.getString(0);
					String type = dataCursor.getString(1);
					if ("vnd.android.cursor.item/name".equals(type)) {
						map.put("name", data);
					} else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
						map.put("phone", data);
					}
				}
				list.add(map);
			}
		}
		return list;
	}
}
