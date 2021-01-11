package com.example.xie.moneymanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;


public class AddNewOne extends Activity implements View.OnClickListener  {
    TabHost th;  //TabHost容器
    TextView tv_choosedate; //选择日期
    Button btn_submit; //提交到数据库

    //支出
    EditText et_consummoney;
    EditText et_out;
    String consumtype = "";  //消费内容
    double d_money; //消费金额

    //收入
    EditText et_in;
    EditText et_in_earnmoney;;
    String in_consumtype = ""; //收入内容
    double in_d_money; //收入金额

    ImageButton imgbtn_back;

    View v_current;  //
    View v_last;  //

    int flag_isthefirstclick = 0; //如果为0表示从未点击过按钮，否则表示已经点击过
    String datestr; //日历中获取到的日期（保存到数据库）
    int year;
    int month;
    int day;
    int month_r;  //month从0开始，month_r=month+1
    int inOrout_tabfocus; //获取上一个页面指向收入还是支出 0代表收入，1代表支出
    Calendar cal;  //日历
    SQLiteDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_one);
        initial();  //初始化
        getDate();
        setlistener();

        tv_choosedate.setOnClickListener(new View.OnClickListener() {   //选择日期，显示
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        month_r = month+1;
                        datetype(year, month_r, dayOfMonth);
                        tv_choosedate.setText("<  "+datestr+"  >");
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(AddNewOne.this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {   //提交至数据库
            public void onClick(View v) {
                if (submitcheck() != 0)
                {
                    insertrecordintodb();
                    Toast.makeText(AddNewOne.this, "账目已提交！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void initial() {     //初始化
        th = (TabHost) findViewById(R.id.tabhost);
        tv_choosedate = (TextView)findViewById(R.id.tv_choosedate);
        btn_submit = (Button)findViewById(R.id.btn_addnewone_submit);

        ////////////////支出/////////////////
        et_out = (EditText)findViewById(R.id.et_out); //消费内容
        et_consummoney = (EditText)findViewById(R.id.et_consummoney);

        ///////////////收入//////////////////
        et_in = (EditText)findViewById(R.id.et_in);  //收入内容
        et_in_earnmoney = (EditText)findViewById(R.id.et_in_earnmoney);

        th.setup(); //初始化TabHost容器
        th.addTab(th.newTabSpec("tab1").setIndicator("支出", null).setContent(R.id.lv_tab1));
        th.addTab(th.newTabSpec("tab2").setIndicator("收入", null).setContent(R.id.lv_tab2));

        imgbtn_back = (ImageButton)findViewById(R.id.img_back);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getDate() {       //获取今日
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
        month_r = month+1;
        if (month_r < 10)
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
        else
        {
            if (day < 10)
            {
                datestr=year+"-"+month_r+"-0"+day;
            }
            else
            {
                datestr=year+"-"+month_r+"-"+day;
            }
        }
        tv_choosedate.setText("<"+year+"-"+month_r+"-"+day+">");
    }

    void setlistener() {     //设置监听
        et_out.setOnClickListener(this);
        et_in.setOnClickListener(this);
        imgbtn_back.setOnClickListener(this);
    }

    //提交逻辑
    int submitcheck() {
        inOrout_tabfocus = th.getCurrentTab();
        consumtype = et_out.getText().toString();
        in_consumtype = et_in.getText().toString();
        if (inOrout_tabfocus == 0)   //消费
        {
            String s = et_consummoney.getText().toString();
            if (!s.isEmpty())
            {
                d_money = Double.parseDouble(et_consummoney.getText().toString());
            }
            if (d_money == 0)
            {
                Toast.makeText(AddNewOne.this, "请填写消费金额！", Toast.LENGTH_SHORT).show();
                return 0;
            }
            if (consumtype.length() == 0)
            {
                Toast.makeText(AddNewOne.this, "请填写消费内容！", Toast.LENGTH_SHORT).show();
                return 0;
            }
            if (consumtype.length()>10)
            {
                Toast.makeText(AddNewOne.this, "不超过十个字", Toast.LENGTH_SHORT).show();
                return 0;
            }

        }
        else if (inOrout_tabfocus == 1)   //收入
        {
            String s = et_in_earnmoney.getText().toString();
            if (!s.isEmpty())
            {
                in_d_money = Double.parseDouble(et_in_earnmoney.getText().toString());
            }
            if (in_d_money == 0)
            {
                Toast.makeText(AddNewOne.this, "请填写收入金额！",Toast.LENGTH_SHORT).show();
                return 0;
            }
            if (in_consumtype.length() == 0)
            {
                Toast.makeText(AddNewOne.this, "请填写收入内容！", Toast.LENGTH_SHORT).show();
                return 0;
            }
            if (in_consumtype.length()>10)
            {
                Toast.makeText(AddNewOne.this, "不超过十个字", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        return 1;
    }

    void insertrecordintodb() {     //数据库操作
        db = openOrCreateDatabase("record.db", MODE_PRIVATE, null);
        String sqlCreate = "create table if not exists accountingrecord" +
                "(_id integer primary key autoincrement,phonenum text,yzm text,consumdate text," +
                "consumtype text, money double, inOrout integer)";
        //手机号，验证码，日期，收支内容，金额，支出0或收入1
        db.execSQL(sqlCreate);

        String sqlInsert = new String();
        if (inOrout_tabfocus == 0)     //支出
        {
            sqlInsert = "insert into accountingrecord (consumdate, consumtype, money, inOrout) values " +
                    "('"+datestr+"', "+" '"+consumtype+"', "+d_money+", "+inOrout_tabfocus+")";
        }
        else if (inOrout_tabfocus == 1)     //收入
        {
            sqlInsert = "insert into accountingrecord (consumdate, consumtype, money, inOrout) values " +
                    "('"+datestr+"', "+"'"+in_consumtype+"', "+in_d_money+", "+inOrout_tabfocus+")";
        }
        db.execSQL(sqlInsert);
        db.close();
    }

    void datetype(int year, int month_r, int dayOfMonth) {
        if (month_r < 10)
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
        else
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

    public void onClick(View v) {
        if (flag_isthefirstclick == 0)
        {
            v_current = v;
            v_last = v;
        }
        else
        {
            v_last = v_current;
            v_current = v;
        }
        setbuttonstyle();

        switch (v.getId())   //选择布局
        {
            //支出
            case R.id.et_out:consumtype=et_out.getText().toString();break;

            //收入
            case R.id.et_in:consumtype=et_in.getText().toString();break;

            case R.id.img_back:finish();
        }
    }

    void setbuttonstyle()
    {
        if(flag_isthefirstclick == 0)
        {
            v_current.setSelected(true);
            flag_isthefirstclick = 1;
        }
        else
        {
            v_last.setSelected(false);
            v_current.setSelected(true);
        }
    }

}
