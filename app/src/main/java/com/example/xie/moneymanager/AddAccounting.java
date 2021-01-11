package com.example.xie.moneymanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAccounting extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    SimpleAdapter sim_adapter;     //数据适配器
    List<Map<String, Object>> datalist;
    ListView listView_addaccounting;  //账单列表
    Button btn_addaccounting;
    SQLiteDatabase db;
    ImageButton img_back;
    ImageButton imgbtn_refresh;
    TextView tv_todaydetail;  //今日情况
    int currentposition = 0;
    double todaytotal_in = 0;  //收入
    double todaytotal_out = 0; //支出

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accounting);

        initial();
        int[] item_id = {R.id.tv_mainlistviewitem_out, R.id.tv_mainlistviewitem_in, R.id.img_center};
        String[] item_name = {"out", "in", "img", "line_up", "line_down"};
        sim_adapter = new SimpleAdapter(this, getData(), R.layout.addaccountinglistviewitem, item_name, item_id);
        listView_addaccounting.setAdapter(sim_adapter);     //数据配置
        listView_addaccounting.setOnItemClickListener(this);     //长按菜单
        btn_addaccounting.setOnClickListener(this);     //添加账目
        img_back.setOnClickListener(this);
        imgbtn_refresh.setOnClickListener(this);

        //长按弹出菜单
        listView_addaccounting.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
        {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
            {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                currentposition = info.position;
                menu.add(0, 0, 0, "删 除");
            }
        });

    }

    public void initial() {     //初始化
        listView_addaccounting = (ListView) findViewById(R.id.lv_addaccounting);
        datalist = new ArrayList<Map<String, Object>>();     //账目列表
        btn_addaccounting = (Button) findViewById(R.id.btn_addaccounting);
        tv_todaydetail = (TextView)findViewById(R.id.tv_todaydetail);
        img_back = (ImageButton)findViewById(R.id.img_back);
        imgbtn_refresh = (ImageButton)findViewById(R.id.imgbtn_refresh);
    }

    public boolean onContextItemSelected(MenuItem item) {   //删除
        deleteItem();
        return true;
    }

    private List<Map<String, Object>> getData() {
        todaytotal_in = 0;
        todaytotal_out = 0;
        datalist = new ArrayList<Map<String, Object>>();

        db = openOrCreateDatabase("record.db", MODE_PRIVATE, null);
        String sqlCreate = "create table if not exists accountingrecord" +
                "(_id integer primary key autoincrement,phonenum text,yzm text,consumdate text," +
                "consumtype text, money double, inOrout integer)";
        //手机号，验证码，日期，收支内容，金额，支出0或收入1
        db.execSQL(sqlCreate);

        String sqlInsert = "select * from accountingrecord where consumdate = date('now')";
        Cursor c = db.rawQuery(sqlInsert, null); //提取所有今天支出项
        if (c != null)
        {
            c.moveToFirst();
        }
        if (c.moveToFirst())
        {
            do
            {
                Map<String, Object> map = new HashMap<String, Object>();
                int inOrout = c.getInt(c.getColumnIndex("inOrout"));
                int id = c.getInt(c.getColumnIndex("_id"));
                String consumdate = c.getString(c.getColumnIndex("consumdate"));
                String consumtype = c.getString(c.getColumnIndex("consumtype"));
                double money = c.getDouble(c.getColumnIndex("money"));

                map.put("id", id);
                map.put("consumdate", consumdate);
                map.put("consumtype", consumtype);
                map.put("money", money);
                if (inOrout == 0) //支出
                {
                    map.put("out", consumtype+"  -"+money+"￥");  //支出：内容  -金额
                    map.put("in", "");
                    todaytotal_out = todaytotal_out+money;  //今日已支出+新支出金额
                    map.put("img", R.drawable.round);  //分隔
                }
                else if (inOrout == 1) //收入
                {
                    map.put("in", consumtype+"   +"+money+"￥");   //收入：内容  +金额
                    map.put("out", "");
                    todaytotal_in = todaytotal_in+money;
                    map.put("img", R.drawable.round1);
                }
                map.put("line_up", R.id.img_line_up);
                map.put("line_down", R.id.img_line_down);

                tv_todaydetail.setText("今日：支出"+todaytotal_out+"元  收入"+todaytotal_in+"元");
                datalist.add(map);
            }while (c.moveToNext());
        }
        return datalist;
    }

    //listView.invalidateViews(); 刷新listview
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //列表项点击事件
        currentposition = position;
    }

    private void deleteItem() {
        int id = (int) datalist.get(currentposition).get("id");
        String sqlDelete = "delete from accountingrecord where _id="+id;
        db = openOrCreateDatabase("record.db", MODE_PRIVATE, null);
        db.execSQL(sqlDelete);
        datalist.remove(currentposition);
        sim_adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {     //跳转
        switch (v.getId())
        {
            case R.id.btn_addaccounting:
            {
                Intent intent = new Intent(AddAccounting.this, AddNewOne.class);
                startActivity(intent);
                break;
            }
            case R.id.img_back:finish();break;
            case R.id.imgbtn_refresh:
            {
                int[] item_id = {R.id.tv_mainlistviewitem_out, R.id.tv_mainlistviewitem_in, R.id.img_center};
                String[] item_name = {"out", "in", "img", "line_up", "line_down"};
                sim_adapter = new SimpleAdapter(this, getData(), R.layout.addaccountinglistviewitem, item_name, item_id);
                listView_addaccounting.setAdapter(sim_adapter);
                Toast.makeText(AddAccounting.this, "账目已更新", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}