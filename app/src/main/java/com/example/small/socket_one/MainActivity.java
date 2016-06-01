package com.example.small.socket_one;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public EditText editText;
    //public SocketClient client;
    public RecyclerView mRecyclerView;
    public final RunnableThread runnable = new RunnableThread();
    private static final int COMPLETED = 0;
    public Handler handler = new Handler();

    public List<Bean> myDataset = new ArrayList<Bean>();
    public RecycleAdapter mAdapter = new RecycleAdapter(myDataset);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        //mRecyclerView.setHasFixedSize(true);


        //创建Adapter
        mRecyclerView.setAdapter(mAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText = (EditText) findViewById(R.id.editText);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "正在发送", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();
                        */
                if (!isempty()) {
                    Log.d("qiang", "启动中");
                    new Thread(runnable).start();
                    editText.setText("");

                } else {
                    Snackbar.make(view, "不能发送空消息", Snackbar.LENGTH_SHORT)
                            .setAction("", null).show();
                }
            }
        });
    }

    public boolean isempty() {
        editText = (EditText) findViewById(R.id.editText);
        assert editText != null;
        if(editText.getText().length() == 0||editText.getText().toString() == null)
        {
            return true;

        }
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class RunnableThread implements Runnable {
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == COMPLETED) {
                    myDataset.add(new Bean(Bean.X_TYPE, msg.obj.toString()));
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    myDataset.add(new Bean(Bean.X_TYPE, "data error!"));
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        };

        @Override
        public void run() {
            Socket socket = null;
            editText = (EditText) findViewById(R.id.editText);
            assert editText != null;
            String message = editText.getText().toString();
            Log.d("qiang", ""+message);
            try {
                //创建客户端socket,注意:不能用localhost或127.0.0.1，Android模拟器把自己作为localhost
                socket = new Socket("123.206.52.70", 8001);

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter
                        (socket.getOutputStream())), true);
                //发送数据
                out.println(message);

                //接收数据
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = in.readLine();

                //处理完成后给handler发送消息
                Message msgs = new Message();
                msgs.what = COMPLETED;
                msgs.obj = msg;
                handler.sendMessage(msgs);

                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != socket) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
