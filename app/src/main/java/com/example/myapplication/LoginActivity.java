package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.User;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.Serializable;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText student_ID_edit,student_pwd_edit;
    Button forget_pwd_button,register_button,login_button;
    RequestQueue mQueue;
    TokenHelper tokenHelper;
    String token;
    User user;

    boolean userIsAdmin = false;

    public static LoginActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       instance=this;
        init();
        //test();
        checkLoginHistory();
        setButtonOnClickFun();
    }


    private void init() {
        student_ID_edit = findViewById(R.id.student_ID_edit);
        student_pwd_edit = findViewById(R.id.student_pwd_edit);
        forget_pwd_button = findViewById(R.id.forget_pwd_button);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);
        mQueue = Volley.newRequestQueue(LoginActivity.this);
        tokenHelper = new TokenHelper();
        user = new User();
    }

    // 查看用户之前有没有token，有则二次登陆
    private void checkLoginHistory() {
        if(tokenHelper.getToken()!=null){
            Intent intent = new Intent(LoginActivity.this, LoginWithTokenActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setButtonOnClickFun() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentID = student_ID_edit.getText().toString();
                String pwd = student_pwd_edit.getText().toString();
                if(checkRules(studentID,pwd)){
                    // 发送给后台核对
                    /*if(sendToServer(studentID,pwd)){
                        // 登陆成功，跳转到主页
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra("user",(Serializable) user);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                    }*/
                    Log.e("##", "信息发送开始");
                    sendToServer(studentID,pwd);
                }
            }
        });


        student_pwd_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                student_pwd_edit.setBackgroundResource(R.drawable.shapet);
                student_ID_edit.setBackgroundResource(R.drawable.shapet2);
                student_pwd_edit.setHint("");
                student_ID_edit.setHint("请输入账号");
            }
        });
        student_ID_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                student_ID_edit.setBackgroundResource(R.drawable.shapet);
                student_pwd_edit.setBackgroundResource(R.drawable.shapet2);
                student_ID_edit.setHint("");
                student_pwd_edit.setHint("请输入密码");
            }
        });

    }

    private Boolean checkRules(String studentID,String pwd) {
        // 查看是否为空
        if(studentID==null||pwd==null){
            Toast.makeText(getApplicationContext(), "账号密码均不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 查看账号格式是否为学号,xml已限制只能输入数字
        /*if(studentID.length()!=9){
            Toast.makeText(getApplicationContext(), "账号为9位学号", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }

    private void sendToServer(final String studentID, String pwd) {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
                    try {
                        jsonObject.put("username", studentID);
                        jsonObject.put("userpassword", pwd);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = "http://47.100.226.176:8080/shopapp/user/login/"+studentID+"/"+pwd;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

                        public void onResponse(org.json.JSONObject jsonObject) {
                            Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                            Log.e("##", jsonObject.toString());

                            // 操作成功
                            if(message.getCode() == 100){

                                if(message.getExtend().get("va_msg").toString().compareTo("此时登录成功为用户") == 0){
                                    userIsAdmin = false;

                                }else{
                                    userIsAdmin = true;
                                }

                                // 获取token，保存token到本地并进行跳转
                                fetchToken(studentID);


                            }else{
                                // 操作失败
                                Toast.makeText(getApplicationContext(), message.getExtend().get("va_msg").toString() , Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
                        }
                    });
                    mQueue.add(jsonObjectRequest);
        // return true;
    }

    private void test(){
        Log.e("##","test返回：");
    }

    public void fetchToken(final String username){


        String url = "http://47.100.226.176:8080/shopapp/user/returntoken/"+username;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());
                Log.e("##", String.valueOf(message.getCode()));
                Log.e("##", message.getExtend().get("token").toString());
                // 操作成功
                if(message.getCode() == 100){

                    // user.setUserid(message.getExtend().get("userId").toString());

                    token = message.getExtend().get("userId").toString();
                    //Toast.makeText(getApplicationContext(), message.getExtend().get("token").toString() , Toast.LENGTH_SHORT).show();
                    tokenHelper.deleteToken();
                    tokenHelper.saveToken(token);

                    // 跳转
                    if(!userIsAdmin){
                        // 用户登陆成功跳转
                        Toast.makeText(getApplicationContext(), "欢迎登陆"+username , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra("user",(Serializable) user);
                        startActivity(intent);
                        finish();

                    }else{
                        // 管理员登陆成功跳转
                        Intent intent = new Intent(LoginActivity.this, Admin_MainActivity.class);
                        //intent.putExtra("user",(Serializable) user);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    // 操作失败
                    token = "fail";
                    Toast.makeText(getApplicationContext(), message.getExtend().get("token").toString()+"请联系管理员" , Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);


    }
}
