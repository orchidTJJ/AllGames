package com.orchid.counter.allgames;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private GridView                 gridView;
    private GridViewAdapter          adapter;
    private String[]                 urls;
    private GameBeanDao              gameBeanDao;
    private Dialog                   dialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    loadApps(1);
                    dialog.dismiss();
                    break;

            }


            super.handleMessage(msg);
        }
    };
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv);
        title = (TextView) findViewById(R.id.tv);

        urls = new String[]{"http://www.wandoujia.com/category/6001/"
                , "http://www.wandoujia.com/category/6003/"
                , "http://www.wandoujia.com/category/6008/"
                , "http://www.wandoujia.com/category/6004/"
                , "http://www.wandoujia.com/category/6002/"
                , "http://www.wandoujia.com/category/6007/"
                , "http://www.wandoujia.com/category/6009/"
                , "http://www.wandoujia.com/category/6005/"
                , "http://www.wandoujia.com/category/5015/"

        };


        //创建数据库
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "game.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        gameBeanDao = daoSession.getGameBeanDao();

        List<GameBean> gameBeanList = gameBeanDao.queryBuilder().build().list();
        LogUtils.d("数据库大小: " + gameBeanList.size());
        if (gameBeanList.size() > 0) {
            //获取所有app信息
            loadApps(1);
        } else {
            //爬取豌豆荚数据
            wadoujiaData();
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //LogUtils.d("短按" + adapter.getItem(position).getPackageName());
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(adapter.getItem(position)
                        .getPackageName());
                startActivity(LaunchIntent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除该条目");
                builder.setMessage("确认要删除该条目吗?");
                builder.setPositiveButton("删除",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String packageName = adapter.getItem(pos).getPackageName();

                                Uri uri = Uri.fromParts("package", packageName, null);
                                Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                return true;
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(MainActivity.this, view);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.main, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(MainActivity.this);
                //显示(这一行代码不要忘记了)
                popup.show();
            }
        });


    }

    private void wadoujiaData() {
        dialog = ProgressDialogHelper.createLoadingDialog(MainActivity.this, "数据加载中....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        List<GameBean> list = gameBeanDao.queryBuilder().build().list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                gameBeanDao.delete(list.get(i));
            }
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                Document doc = null;
                for (int i = 0; i < urls.length; i++) {
                    int index = 0;
                    for (int j = 0; j < 10; j++) {
                        try {
                            doc = Jsoup.connect(urls[i] + j).get();
                            Elements els = doc.select("a.install-btn");
                            for (int k = 0; k < els.size(); k++) {
                                Element el = els.get(k);
                                String href = el.attr("data-app-pname");
                                index++;
                                Log.e(i + "-" + index, href);
                                GameBean bean = new GameBean();
                                bean.setPackageName(href);
                                gameBeanDao.insert(bean);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void loadApps(int type) {
        List<AppUtils.AppInfo> appsInfo = AppUtils.getAppsInfo();

        ArrayList<AppUtils.AppInfo> list = new ArrayList();
        for (int i = 0; i < appsInfo.size(); i++) {
            if (!appsInfo.get(i).isSystem()) {
                if (type == 0) {
                    list.add(appsInfo.get(i));
                } else {
                    List<GameBean> gameBeanList = gameBeanDao
                            .queryBuilder()
                            .where(GameBeanDao.Properties.PackageName.eq(appsInfo.get(i).getPackageName()))
                            .build()
                            .list();
                    if (gameBeanList.size() > 0) {
                        LogUtils.d(appsInfo.get(i).getPackageName() + "---大小: " + gameBeanList.size());
                        list.add(appsInfo.get(i));
                    }
                }
            }
        }

        LogUtils.d("之前数量 : " + appsInfo.size() + "\n之后数量 : " + list.size());


        adapter = new GridViewAdapter(list, MainActivity.this);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.all_app:
                title.setText("全部应用▼");
                loadApps(0);
                break;

            case R.id.game_app:
                title.setText("游戏应用▼");
                loadApps(1);
                break;
        }
        return false;
    }
}
