package com.gxypnh.nhnw;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mUsername;
    private EditText mPsw;
    private Button mLogin;
    private TextView mResult;
    private TextView mReg;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void initView(){
        mUsername = (EditText) findViewById(R.id.et_name);
        mPsw = (EditText) findViewById(R.id.et_pwd);
        mLogin = (Button) findViewById(R.id.btn_login);
        mResult = (TextView) findViewById(R.id.tv_result);
        mReg = (TextView) findViewById(R.id.tv_reg);
    }

    private void setListener(){
        mLogin.setOnClickListener(this);
        mReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                Login();
                break;
            case R.id.tv_reg:
                Reg();
                break;
            default:
                break;
        }
    }

    private void Login() {
        username = mUsername.getText().toString().trim();
        password = mPsw.getText().toString().trim();
        new getResult().execute("http://sjwym.cn:8080/NHNW/LoginServlet?username="+username+"&password="+password);
    }

    private class getResult extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            OkHttpClient client = new OkHttpClient();
            client.newBuilder().connectTimeout(10000, TimeUnit.MILLISECONDS);
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    String result =response.body().string();
                    return result;
                    //Toast.makeText(getApplicationContext(),response.body().toString(),Toast.LENGTH_LONG).show();
                }else{
                    throw new IOException("error : "+response);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mResult.setText(s);
            JSONObject jsonObject;
            String result;
            try {
                jsonObject = new JSONObject(s);
                result = jsonObject.getString("result");
                switch (result){
                    case "success":
                        Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
                        break;
                    case "wrong_password":
                        Toast.makeText(getApplicationContext(),"wrong_password",Toast.LENGTH_LONG).show();
                        break;
                    case "error":
                        Toast.makeText(getApplicationContext(),"username_not_exists",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void Reg(){
        Intent intent = new Intent(MainActivity.this,RegActivity.class);
        startActivity(intent);
    }
}
