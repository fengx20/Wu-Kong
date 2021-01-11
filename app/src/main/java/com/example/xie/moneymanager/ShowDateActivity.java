package com.example.xie.moneymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowDateActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView mlist;
    ImageButton img_back;
    SQLiteDatabase db;
    List<Map<String, Object>> datalistForcurrentdata = new ArrayList<Map<String, Object>>();
    ArrayList<String> al_consumdate = new ArrayList<>();
    private LayoutInflater mInflater;
    String datestr;

    double allout_money = 0;
    double allin_money = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_date);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        datestr = bundle.getString("date1");

        System.out.println(datestr);
        mlist= (ListView) findViewById(R.id.lv_123);
        initial();
        MyBaseAdapter mAdapter = new MyBaseAdapter();
        mlist.setAdapter(mAdapter);

        img_back.setOnClickListener(this);

    }

    void initial()
    {
        img_back = (ImageButton)findViewById(R.id.img_back);
    }

    class MyBaseAdapter extends BaseAdapter {

        ArrayList<String> a_consumdate = getDatedbData();

        //得到item的总数
        @Override
        public int getCount() {
            //返回ListView Item条目的总数
            if (a_consumdate == null) {
                return 0;
            }
            return a_consumdate.size();
        }

        //得到Item代表的对象
        @Override
        public Object getItem(int position) {
            if (a_consumdate == null) {
                return null;
            }
            return a_consumdate.get(position);
        }

        //得到Item的id
        @Override
        public long getItemId(int position) {
            //返回ListView Item的id
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyBaseAdapter.ViewHodler holder = null;
            if (convertView == null) {
                holder = new MyBaseAdapter.ViewHodler();
                convertView = mInflater.from(getApplicationContext()).
                        inflate(R.layout.list_item2, parent, false);
                holder.mTextView = (TextView) convertView
                        .findViewById(R.id.item_tv2);
                convertView.setTag(holder);
            } else {
                holder = (MyBaseAdapter.ViewHodler) convertView.getTag();
            }
            holder.mTextView.setText(a_consumdate.get(position));

            return convertView;
        }

        private class ViewHodler {
            TextView mTextView;
        }
    }

    ArrayList<String> getDatedbData(){

        db = openOrCreateDatabase("record.db", MODE_PRIVATE, null);
        String sqlCreate = "create table if not exists accountingrecord" +
                "(_id integer primary key autoincrement,phonenum text,yzm text,consumdate text," +
                "consumtype text, money double, inOrout integer)";
        //手机号，验证码，日期，收支内容，金额，支出0或收入1
        db.execSQL(sqlCreate);
        System.out.println(datestr);
        String sqlInsert = "select consumdate,consumtype,money,inOrout  from accountingrecord where consumdate = '"+datestr+"' ";
        Cursor cout = db.rawQuery(sqlInsert,
                null);
        if (cout != null)
        {
            if (cout.moveToFirst())
            {
                do
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("inOrout",cout.getString(cout.getColumnIndex("inOrout")));
                    map.put("consumdate", cout.getString(cout.getColumnIndex("consumdate")));
                    map.put("consumtype", cout.getString(cout.getColumnIndex("consumtype")));
                    map.put("money", cout.getString(cout.getColumnIndex("money")));
                    datalistForcurrentdata.add(map);
                }while (cout.moveToNext());
            }
        }
        db.close();
        cout.close();
        for (int k = 0; k < datalistForcurrentdata.size(); k++)
        {
            String l_inOrout   =(String) datalistForcurrentdata.get(k).get("inOrout");
            System.out.println(l_inOrout);
            String l_consumdate =(String) datalistForcurrentdata.get(k).get("consumdate");
            System.out.println(l_consumdate);
            String l_consumtype =(String) datalistForcurrentdata.get(k).get("consumtype");
            System.out.println(l_consumtype);
            String l_money    =(String) datalistForcurrentdata.get(k).get("money");
            System.out.println(l_money);

            int money = Integer.parseInt(datalistForcurrentdata.get(k).get("money").toString());
            System.out.println(money);
            String all = "";
            if(l_inOrout.equals("0")) {
                allout_money = allout_money + money;
                String strout_money  = String.valueOf(allout_money);
                all = l_consumdate + "\n" + "支出事项：" + l_consumtype +"\n"+"金额：-" + l_money + "元"+"\n"+"               今日已支出："+strout_money+"元";
            }else{
                allin_money = allin_money + money;
                String strin_money = String.valueOf(allin_money);
                all = l_consumdate + "\n" + "收入事项：" + l_consumtype +"\n"+"金额：+" + l_money + "元"+"\n"+"               今日已收入："+strin_money+"元";
            }
            al_consumdate.add(all);
        }
        return al_consumdate;
    }

    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_back:

                mlist.setAdapter(null);
                finish();
                break;
        }
    }

}
