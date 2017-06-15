package com.gxypnh.nhnw;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mUsername;
    private EditText mCode;
    private EditText mPassword1;
    private EditText mPassword2;
    private EditText mRealname;
    private Button btnCode;
    private Button btnReg;
    private TextView textView;
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_acitivty);
        code = null;
        initView();
        setListener();
    }

    private void initView(){
        mUsername = (EditText) findViewById(R.id.et_reg_username);
        mCode = (EditText) findViewById(R.id.et_reg_code);
        mPassword1 = (EditText) findViewById(R.id.et_reg_password1);
        mPassword2 = (EditText) findViewById(R.id.et_reg_password2);
        btnCode = (Button) findViewById(R.id.btn_reg_getCode);
        btnReg = (Button) findViewById(R.id.btn_reg_reg);
        textView = (TextView) findViewById(R.id.textView);
        mRealname = (EditText) findViewById(R.id.et_realname);
    }
    private void setListener(){
        btnCode.setOnClickListener(this);
        btnReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reg_getCode:
                getCode();
                break;
            case R.id.btn_reg_reg:
                reg();
                break;
            default:
                break;
        }
    }



    private void getCode(){
        String username = mUsername.getText().toString().trim();
        new getCode().execute("http://sjwym.cn:8080/NHNW/RegServlet?username="+username+"&type=getCode");
    }

    private class getCode extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
                String url = params[0];
                return MyOKHttp.get(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            displayCode(s);
        }

        private void displayCode(String s){
            JSONObject jsonObject;
            String result;
            try {
                jsonObject = new JSONObject(s);
                result = jsonObject.getString("result");
                switch (result){
                    case "OK":
                        code = jsonObject.getString("code");
                        textView.setText("已发送验证码！");
                        break;
                    case "error":
                        textView.setText("username already exists");
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void reg(){
        String username = mUsername.getText().toString().trim();
        String password = mPassword1.getText().toString().trim();
        String realname = mRealname.getText().toString().trim();
        String userCode = mCode.getText().toString().trim();
        new reg().execute("http://sjwym.cn:8080/NHNW/RegServlet?username="+username
                +"&password="+password
                +"$realname="+realname+"&type=reg",userCode);
    }

    private class reg extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String userCode = params[1];
            if(userCode.equals(code)){
                String url = params[0];
                return MyOKHttp.get(url);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                JSONObject jsonObject;
                String result;
                try {
                    jsonObject = new JSONObject(s);
                    result = jsonObject.getString("result");
                    switch (result){
                        case "true":
                            textView.setText("注册成功！");
                            break;
                        case "false":
                            textView.setText("注册失败！");
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                textView.setText("验证码错误！");
            }

        }
    }
}
