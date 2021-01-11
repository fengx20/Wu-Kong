package com.example.xie.moneymanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Checkaccounting extends Activity implements View.OnClickListener {
    TextView tv_choosedate1; //选择日期
    String datestr; //日历中获取到的日期（在数据库查询）
    Calendar cal;  //日历
    int year;
    int month;
    int day;
    int month_r;  //month从0开始，month_r=month+1

    private ListView mList;
    ArrayList<String> al_consumdate = new ArrayList<>();
    List<Map<String, Object>> datalistForcurrentdata = new ArrayList<Map<String, Object>>();
    private LayoutInflater mInflater;
    ImageButton img_back;
    TextView it;
    SQLiteDatabase db;
    Button btn_addaccounting1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkaccounting);

        mList= (ListView) findViewById(R.id.lv);
        initial();
        getDate();
        MyBaseAdapter mAdapter = new MyBaseAdapter();
        mList.setAdapter(mAdapter);

        img_back.setOnClickListener(this);
        btn_addaccounting1.setOnClickListener(this);


        tv_choosedate1.setOnClickListener(new View.OnClickListener() {   //选择日期，显示
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        month_r = month+1;
                        datetype(year, month_r, dayOfMonth);
                        tv_choosedate1.setText("<"+datestr+">");

                       /* MyBaseAdapter mAdapter = new MyBaseAdapter();*/

                        /*System.out.println(mAdapter.a_consumdate.size());

                        mAdapter.a_consumdate.clear();

                        System.out.println(mAdapter.a_consumdate.size());*/

                       /* mAdapter.notifyDataSetChanged();
                        mList.setAdapter(mAdapter);*/
                        /*mAdapter.a_consumdate.clear();*/
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(Checkaccounting.this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
            }
        });
    }

    void initial()
    {
        img_back = (ImageButton)findViewById(R.id.img_back);
        tv_choosedate1 = (TextView)findViewById(R.id.tv_choosedate1);
        btn_addaccounting1 = (Button) findViewById(R.id.btn_addaccounting1);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getDate() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
        month_r = month+1;
        if (month_r < 10)   //月份个位数
        {
            if (day < 10)   //天数个位数
            {
                datestr=year+"-0"+month_r+"-0"+day;
            }
            else
            {
                datestr=year+"-0"+month_r+"-"+day;
            }
        }
        else
        {
            if (day < 10)
            {
                datestr=year+"-0"+month_r+"-0"+day;
            }
            else
            {
                datestr=year+"-0"+month_r+"-"+day;
            }
        }
        tv_choosedate1.setText("<"+year+"-0"+month_r+"-"+day+">");
    }

    void datetype(int year, int month_r, int dayOfMonth) {
        if (month_r < 10)      //月份一位数
        {
            if (dayOfMonth < 10)
            {
                datestr=year+"-0"+month_r+"-0"+dayOfMonth;
            }
            else
            {
                datestr=year+"-0"+month_r+"-"+dayOfMonth;
            }
        }
        else                     //两位数
        {
            if (dayOfMonth < 10)
            {
                datestr=year+"-"+month_r+"-0"+dayOfMonth;
            }
            else
            {
                datestr=year+"-"+month_r+"-"+dayOfMonth;
            }
        }
    }


    class MyBaseAdapter extends BaseAdapter {

       /* String datestr123  = tv_choosedate1.getText().toString().replaceAll("<","");
        String datestr1234 = datestr123.replaceAll(">","");
        String datestr9    = datestr1234;*/
        ArrayList<String>   a_consumdate = getDatedbData();

        //得到item的总数
        @Override
        public int getCount() {

            //System.out.println(a_consumdate.toString());

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
            ViewHodler holder = null;
            if (convertView == null) {
                holder = new ViewHodler();
                convertView = mInflater.from(getApplicationContext()).
                        inflate(R.layout.list_item,parent,false);
                holder.mTextView= (TextView) convertView
                        .findViewById(R.id.item_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHodler) convertView.getTag();
            }
            holder.mTextView.setText(a_consumdate.get(position));

           // System.out.println(a_consumdate.size());

            return convertView;
        }

        private class ViewHodler {
            TextView mTextView;
        }
    }


    ArrayList<String> getDatedbData(){

       /* System.out.println(datestr);
       * String datestr
       * */

        db = openOrCreateDatabase("record.db", MODE_PRIVATE, null);
        String sqlCreate = "create table if not exists accountingrecord" +
                "(_id integer primary key autoincrement,phonenum text,yzm text,consumdate text," +
                "consumtype text, money double, inOrout integer)";
        //手机号，验证码，日期，收支内容，金额，支出0或收入1
        db.execSQL(sqlCreate);

        /*where consumdate = '"+datestr+"'*/

        String sqlInsert = "select consumdate,consumtype,money,inOrout  from accountingrecord where consumdate = '"+datestr+"' ";
        Cursor cout = db.rawQuery(sqlInsert,
                null);
        if (cout != null)
        {
            if (cout.moveToFirst())
            {
                do
                {
                    Map<String, Object>map = new HashMap<String, Object>();
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
            String l_money      =(String) datalistForcurrentdata.get(k).get("money");
            System.out.println(l_money);
            String all = "";
            if(l_inOrout.equals("0")) {
                all = l_consumdate + "\n" + "支出事项：" + l_consumtype +"\n"+"金额：-" + l_money + "元";
            }else{
                all = l_consumdate + "\n" + "收入事项：" + l_consumtype +"\n"+"金额：+" + l_money + "元";

            }
            al_consumdate.add(all);
        }
        System.out.println(al_consumdate.toString());
        return al_consumdate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_back:finish();break;
            case R.id.btn_addaccounting1:
                String datestr123  = tv_choosedate1.getText().toString().replaceAll("<","");
                String datestr1234 = datestr123.replaceAll(">","");
                String datestr9    = datestr1234;
                Intent intent = new Intent(Checkaccounting.this, ShowDateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("date1",datestr9);
                System.out.println(datestr9);
                intent.putExtras(bundle);
                startActivity(intent);
               break;
        }
    }
}




