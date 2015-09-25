package com.example.changsiyuan.mycaculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button[] btnCmd = new Button[8];  //command按钮
    private Button[] btnNum = new Button[11];  //number按钮
    private EditText editText = null;  //获取编辑框
    private TextView textView = null;  //获取显示信息框

    private String lastCommand = null;  //最近输入的运算符
    private boolean cmdBoolean = true;  //是否是第一个运算符
    private float lastResult = 0;  //已经计算的结果(第一个操作数)
    private float thisNum = 0;   //第二个操作数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取所有command
        btnCmd[0] = (Button) findViewById(R.id.plus);
        btnCmd[1] = (Button) findViewById(R.id.minus);
        btnCmd[2] = (Button) findViewById(R.id.mul);
        btnCmd[3] = (Button) findViewById(R.id.divide);
        btnCmd[4] = (Button) findViewById(R.id.equal);
        btnCmd[5] = (Button) findViewById(R.id.reverse);
        btnCmd[6] = (Button) findViewById(R.id.clear);
        btnCmd[7] = (Button) findViewById(R.id.back);
        //获取所有number
        btnNum[0] = (Button) findViewById(R.id.num0);
        btnNum[1] = (Button) findViewById(R.id.num1);
        btnNum[2] = (Button) findViewById(R.id.num2);
        btnNum[3] = (Button) findViewById(R.id.num3);
        btnNum[4] = (Button) findViewById(R.id.num4);
        btnNum[5] = (Button) findViewById(R.id.num5);
        btnNum[6] = (Button) findViewById(R.id.num6);
        btnNum[7] = (Button) findViewById(R.id.num7);
        btnNum[8] = (Button) findViewById(R.id.num8);
        btnNum[9] = (Button) findViewById(R.id.num9);
        btnNum[10] = (Button) findViewById(R.id.point);
        //获取编辑框和显示信息框
        editText = (EditText) findViewById(R.id.edit);
        textView = (TextView) findViewById(R.id.resultText);


        //实例化两个监听器
        CommandAction ca = new CommandAction();
        NumberAction na = new NumberAction();
        //command按钮植入监听器
        for (Button btnC : btnCmd) {
            btnC.setOnClickListener(ca);
        }
        //number按钮植入监听器
        for (Button btnN : btnNum) {
            btnN.setOnClickListener(na);
        }
    }

    //number按键监听器
    private class NumberAction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            String currentText = editText.getText().toString();  //editText中现有数据
            String input = btn.getText().toString();  //本次点击的数据

            if (currentText.length() == 0 && input.equals(".")) {
                editText.setText("0.");  //处理小数
            } else if ((currentText.equals("+") || currentText.equals("-") || currentText.equals("*") || currentText.equals("/"))
                    && input.equals(".")) {
                editText.setText("0.");  //处理小数
            } else if (currentText.equals("+") || currentText.equals("-") || currentText.equals("*") || currentText.equals("/")) {
                editText.setText(input);
            } else {
                if (currentText.length() <= 6) {
                    editText.setText(editText.getText().toString() + input);
                } else {
                    Toast.makeText(MainActivity.this, "输入长度已满", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //command按键监听器
    private class CommandAction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            String input = btn.getText().toString();
            String currentText = editText.getText().toString();

            if (input.equals("←")) {
                int currentLength = editText.getText().toString().length();
                if (currentLength == 0) {
                    Toast.makeText(MainActivity.this, "数据长度已经为零", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setText(editText.getText().toString().substring(0, currentLength - 1));
                }
            } else if (input.equals("C")) {
                //重置所有变量
                clear();
            } else if (input.equals("±")) {
//                if(currentText.length()==0){
//                    return;
//                }else if(currentText.contains("+") || currentText.contains("-") || currentText.contains("*") || currentText.contains("/")){
//                    return;
//                }else{
//                    editText.setText("-"+currentText);
//                }
                return;
            } else if (input.equals("=")) {
                if (currentText.length() == 0 && lastCommand == null) {
                    Toast.makeText(MainActivity.this, "尚未输入运算符", Toast.LENGTH_SHORT).show();
                    clear();
                } else if (currentText.length() != 0 && lastCommand == null) {
                    textView.setText(editText.getText().toString());
                    editText.setText("");
                    clearNum();
                } else if (currentText.contains("+") || currentText.contains("-") || currentText.contains("*") || currentText.contains("/")) {
                    textView.setText(String.valueOf(lastResult));
                    editText.setText("");
                    clearNum();
                } else {
                    thisNum = seeEditText();
                    textView.setText(String.valueOf(calculate(lastResult, lastCommand, thisNum)));
                }
                editText.setText("");
                clearNum();
            } else {
                if (cmdBoolean) {
                    lastResult = seeEditText();
                    lastCommand = input;
                    cmdBoolean = false;
                    textView.setText(String.valueOf(lastResult));
                    editText.setText(input);
                } else {
                    if (currentText.contains("+") || currentText.contains("-") || currentText.contains("*") || currentText.contains("/")) {
                        Toast.makeText(MainActivity.this, "不能连续输入运算符", Toast.LENGTH_SHORT).show();
                        clear();
                    } else {
                        thisNum = seeEditText();
                        lastResult = calculate(lastResult, lastCommand, thisNum);
                        textView.setText(String.valueOf(lastResult));
                        lastCommand = input;
                        editText.setText(input);
                    }
                }
            }
        }


        //读取EditText中数值（考虑小数）
        private float seeEditText() {
            String editTextContent = editText.getText().toString();
            int k = 0;  //整数部分
            int l = 0;  //小数部分
            float f = 0;
            if (editTextContent.length() == 0 && textView.getText().toString().length() == 0) {
                Toast.makeText(MainActivity.this, "默认第一个操作数为0", Toast.LENGTH_SHORT).show();
                f = 0;
            } else if (editTextContent.length() == 0 && textView.getText().toString().length() != 0) {
                f = Float.parseFloat(textView.getText().toString());
            } else if (editTextContent.contains("+") || editTextContent.contains("-") || editTextContent.contains("*") || editTextContent.contains("/")) {
                Toast.makeText(MainActivity.this, "不能连续输入运算符", Toast.LENGTH_SHORT).show();
                clear();
            } else if (editTextContent.contains(".")) {
                k = Integer.parseInt(editTextContent.split("\\.")[0]);
                l = Integer.parseInt(editTextContent.split("\\.")[1]);
                double decimalLength = Math.pow(10, editTextContent.split("\\.")[1].length());
                f = k + l / (float) decimalLength;
            } else {
                f = (float) Integer.parseInt(editTextContent);
            }
            return f;
        }

        //计算过程
        private float calculate(float first, String cmd, float second) {
            if (cmd.equals("+")) {
                return first + second;
            } else if (cmd.equals("-")) {
                return first - second;
            } else if (cmd.equals("*")) {
                return first * second;
            } else if (cmd.equals("/")) {
                if (second == 0.0) {
                    Toast.makeText(MainActivity.this, "除数不能为零", Toast.LENGTH_SHORT).show();
                    clear();
                } else {
                    return first / second;
                }
            } else {
                Toast.makeText(MainActivity.this, "系统出错", Toast.LENGTH_SHORT).show();
                clear();
            }
            return 0;
        }

        //清空设置
        private void clear() {
            lastCommand = null;
            cmdBoolean = true;
            lastResult = 0;
            thisNum = 0;
            editText.setText("");
            textView.setText("");
        }

        //清空变量
        private void clearNum() {
            lastCommand = null;
            cmdBoolean = true;
            lastResult = 0;
            thisNum = 0;
        }
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
}
