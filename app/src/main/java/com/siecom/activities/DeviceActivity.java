package com.siecom.activities;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;
import com.siecom.framework.appinterface.Api;
import com.siecom.framework.bean.BankCardInfoBean;
import com.siecom.framework.bean.IdentityInfoBean;
import com.siecom.framework.bean.TradeLogBean;
import com.siecom.framework.constconfig.Config;
import com.siecom.framework.device.SiecomDevice;
import com.siecom.framework.device.SiecomTask;
import com.siecom.framework.listen.DeviceConStatusListen;
import com.siecom.framework.module.BankCardModule;
import com.siecom.framework.module.EmvOptions;
import com.siecom.framework.module.KeyBroadModule;
import com.siecom.tools.Base64;
import com.siecom.tools.ByteTool;


import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Administrator on 2015/12/3.
 * 所有的SDK操作都是以SiecomDevice为入口
 * 使用 SiecomTask.TaskCallback 作为回调的方法都能直接在回调方法内操作界面元素，已经封装了handler在界面执行，除非方法中含有Sync字样，否则均是异步方法，不会堵塞主线程
 * 失败回调固定有错误码和描述，默认内部如IO异常、数组越界等常规错误直接用e.hashcode作为错误码，e.getMessage为信息，其余错误则为本sdk自定义错误。如下
 * int code = bundle.getInt("code");
 * String msg = bundle.getString("message");
 * 成功回调的信息则在bundle内，具体类型和数据请参考本类中的使用
 * SiecomDevice.deviceConnStatusChange是监听连接状态的，不能直接操作界面元素，需要用handler接受，由于会重试所以有可能会发送两次onDisconnect
 */
public class DeviceActivity extends AppCompatActivity {
    //UI setting
    public TextView deviceName;
    public TextView deviceMac;
    public TextView deviceVersion;
    public ImageView deviceIcon;
    public TextView power;
    private SmoothProgressBar spb = null;
    //TextSpeaker speaker;//语音朗读
    //身份证UI
    private TextView tv_name, tv_sex, tv_nation, tv_birth, tv_address, tv_id,
            tv_police, tv_validate;
    private ImageView ivPhotoes;
    //指纹扫描动画
    private ImageView scan_line;
    private Animation upAnimation;
    private Animation downAnimation;


    private BluetoothDevice device;
    private LinearLayout mLinearLayout;
    private MaterialDialog pDialog = null;
    public final static int dis_connect = 0;
    public final static int arpc_ret = 1; //接受同步ARPC接口
    public final static int get_log_ret = 2; //接受同步明细接口
    public final static int write_code_name = 3; //接受同步写入编号接口
    public final static int read_code_name = 4; //接受同步读取编号接口
    public final static int read_uniq_name = 5; //接受同步读取唯一号接口
    public final static int test_psam = 6; //接受测试PSAM卡
    public TextView cardInfoText;//银行卡信息
    public TextView codenameStr;//编号的textView
    public TextView uniqstr;//唯一号的textView
    long now = 0;

    private static final String[] cardTypeStr = {"IC卡", "非接卡", "磁条卡", "自动"};

    // 主密钥
    static byte[] master_key = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

    // 工作密钥
    static byte[] work_key = {0x12, 0x34, 0x56, 0x78, (byte) 0x90,
            (byte) 0xab, (byte) 0xcd, (byte) 0xef, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08};

    final String bankCardNo = "6230710101010708018";// 卡号
    final String amount = "123456.00";// 金额显示,只做显示用，有些固件版本并无此功能


    public int cardType = 0;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case dis_connect:
                    //用于接收连接断开的信息
                    dismissDialog();
                    Snackbar.make(mLinearLayout, getResources().getString(R.string.maybe_disconnect), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                case arpc_ret:
                    Bundle bundle = msg.getData();
                    int ret = bundle.getInt("ret");
                    byte[] ScriptResult = bundle.getByteArray("ScriptResult");
                    byte[] TC = bundle.getByteArray("TC");
                    cardInfoText.setText("ARPC 返回:" + ret + "\r\n");
                    break;
                case get_log_ret:
                    Bundle bundle2 = msg.getData();
                    int ret1 = bundle2.getInt("ret");
                    int len = bundle2.getInt("length");
                    byte[] log = bundle2.getByteArray("log");
                    /**
                     * 可以用这个方法格式化交易记录
                     */
                    List<TradeLogBean> list = ByteTool.parseLog(log);
                    StringBuffer buf = new StringBuffer();
                    for (TradeLogBean bean : list) {

                        Log.e("logbean:", bean.getMerchName());
                        buf.append("交易记录—时间：" + bean.getDate() + bean.getTime() + "—金额:" + bean.getAmount() + "—商户：" + bean.getMerchName() + "\r\n");
                    }
                    cardInfoText.setText("交易明细：\r\n" + buf);
                    break;
                case write_code_name:
                    Bundle bundle3 = msg.getData();
                    int ret2 = bundle3.getInt("ret");
                    if(ret2==-6){
                        Snackbar.make(mLinearLayout, getResources().getString(R.string.string_len_err), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }else if(ret2==0){

                        Snackbar.make(mLinearLayout, getResources().getString(R.string.write_succ), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }else{
                        Snackbar.make(mLinearLayout, "err:" + ret2, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    break;

                case read_code_name:
                    Bundle bundle4 = msg.getData();
                    String codeName = bundle4.getString("codeName");
                    if(codeName!=null){
                        codenameStr.setText(codeName);
                    }
                    break;
                case read_uniq_name:
                    Bundle bundle5 = msg.getData();
                    String uniq = bundle5.getString("uniq");

                    if(uniqstr!=null){
                        uniqstr.setText(uniq);
                    }
                    break;
                case test_psam:
                    Bundle bundle6 = msg.getData();
                    int ret3 = bundle6.getInt("ret");
                    if(ret3==0){
                        String slot = bundle6.getString("slot");
                        Toast.makeText(DeviceActivity.this,"PSAM卡槽"+slot+"ok！！",Toast.LENGTH_LONG).show();
                    }else{
                        String slot = bundle6.getString("slot");
                        Toast.makeText(DeviceActivity.this,"PSAM卡槽"+slot+"fail！！",Toast.LENGTH_LONG).show();
                    }
                    break;

            }

        }
    };

    class SpinnerSelectedListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(Spinner parent, View view, int position, long id) {
            if (cardTypeStr[position].equals("IC卡")) {
                cardType = BankCardModule.IC_CARD;
            }

            if (cardTypeStr[position].equals("非接卡")) {

                cardType = BankCardModule.PIC_CARD;
            }
            if (cardTypeStr[position].equals("磁条卡")) {

                cardType = BankCardModule.MSR_CARD;
            }
            if (cardTypeStr[position].equals("自动")) {

                cardType = BankCardModule.AUTO_FIND;
            }

        }
    }

    public void dismissDialog() {

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void showOtherDialog() {
        final View view = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.other_layout, null);
        Button writeCodeBtn = (com.rey.material.widget.Button) view.findViewById(R.id.write_code_btn);
        Button readCodeBtn = (com.rey.material.widget.Button) view.findViewById(R.id.read_code_btn);
        Button readSnBtn = (com.rey.material.widget.Button) view.findViewById(R.id.read_sn_btn);
        final EditText codename = (com.rey.material.widget.EditText) view.findViewById(R.id.code_name_str);
        codenameStr= (TextView) view.findViewById(R.id.code_text);
        uniqstr    = (TextView) view.findViewById(R.id.sn_text);
        Button test_psam1_btn = (com.rey.material.widget.Button) view.findViewById(R.id.test_psam1_btn);
        Button test_psam2_btn = (com.rey.material.widget.Button) view.findViewById(R.id.test_psam2_btn);
        Button test_btn = (com.rey.material.widget.Button) view.findViewById(R.id.test_btn);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 圈存初始化
                 */
//                String cipherFlag = "01";
//                String amount     = "00000000";//8位
//                String terminalCode ="AFE133343536";
//                KSshimingka.getInstance().RFIintForLoad(cipherFlag, amount, terminalCode, new KSshimingka.KsListen() {
//                    @Override
//                    public void onSucced(byte[] back) {
//                        Log.e("back",ByteTool.byte2hex(back));
//                    }
//
//                    @Override
//                    public void onFail() {
//
//                    }
//                });
//                //昆山市民卡全圈存写卡
//                String date = "20151230";//日期
//                String time = "192144";//时间
//                String  mac  = "11111111";//mac;
//
//                KSshimingka.getInstance().RFCreditForLoad(date,time,mac);
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     Api api = new Api();
                     byte slot = 0x01;
                     byte vlot = 0x01;
                     byte pubKeyIndex = 0x00;
                     short puklen = 256;
                     byte[] pubKeyData = new byte[256];
                     int[] len = new int[1];
                     int ret = api.getPubRsaPuk(slot,vlot,pubKeyIndex,puklen,pubKeyData,len,60*1000);
                     Log.e(len[0]+"===ret:" + ret, ByteTool.byte2hex(pubKeyData));
                     Log.e("base64:", Base64.encodeBase64String(pubKeyData));
                 }
             }).start();




            }
        });
        test_psam1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int ret =  SiecomDevice.SyncTestPsam((byte) 0x01);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("ret", ret);
                        bundle.putString("slot", "1");
                        msg.setData(bundle);
                        msg.what = test_psam;
                        handler.sendMessage(msg);

                    }
                }).start();
            }
        });

        test_psam2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int ret =  SiecomDevice.SyncTestPsam((byte) 0x02);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("ret", ret);
                        bundle.putString("slot", "2");
                        msg.setData(bundle);
                        msg.what = test_psam;
                        handler.sendMessage(msg);

                    }
                }).start();
            }
        });

        writeCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String codenameStr = codename.getText().toString().trim();
                        int ret =  SiecomDevice.SyncWriteCodeName(codenameStr);
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("ret", ret);
                        msg.setData(bundle);
                        msg.what = write_code_name;
                        handler.sendMessage(msg);

                    }
                }).start();
            }

        });
        readCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String codeName =  SiecomDevice.SyncReadCodeName();
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("codeName",codeName);
                        msg.setData(bundle);
                        msg.what = read_code_name;
                        handler.sendMessage(msg);

                    }
                }).start();
            }
        });
        readSnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                String SerialNo =  SiecomDevice.SyncReadSerialNo();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("uniq",SerialNo);
                msg.setData(bundle);
                msg.what = read_uniq_name;
                handler.sendMessage(msg);
                    }
                }).start();
            }
        });

        pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                .theme(Theme.LIGHT)
                .title(R.string.other)
                .customView(view, true)
                .positiveText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                        dismissDialog();

                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {


                    }
                })
                .build();
        pDialog.show();

    }


    private void showCardDialog() {
        //默认就是第一个
        cardType = 0;

        dismissDialog();

        final View view = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.card_dialog_layout, null);
        Button read_card_btn = (com.rey.material.widget.Button) view.findViewById(R.id.read_card_btn);

        final Button arpcBtn = (com.rey.material.widget.Button) view.findViewById(R.id.arpc_btn);
        final Button getLogBtn = (com.rey.material.widget.Button) view.findViewById(R.id.get_log_btn);

        Spinner spinner = (Spinner) view.findViewById(R.id.card_list_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(DeviceActivity.this, android.R.layout.simple_spinner_item, cardTypeStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        cardInfoText = (TextView) view.findViewById(R.id.card_info_text);
        final TextView cardTypeText = (TextView) view.findViewById(R.id.card_type_text);
        final TextView cardNoText = (TextView) view.findViewById(R.id.card_no_text);

        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());

        pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                .theme(Theme.LIGHT)
                .customView(view, true)
                .title(R.string.readCard)
                .positiveText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        dismissDialog();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        /**
                         * 取消任务；参数是是否还连接，如果是在SiecomDevice.deviceConnStatusChange的onDisconnect回调中使用，则必须是false,否则会循环调用onDisconnect
                         */
                        SiecomDevice.cancelLastTask(true);


                    }
                })
                .build();
        pDialog.show();

        read_card_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e("cardType:", cardType + "----");

                SiecomTask.TaskCallback taskCallback = new SiecomTask.TaskCallback() {
                    @Override
                    public void onStart(Bundle bundle) {
                        cardTypeText.setText("");
                        cardNoText.setText("");
                        cardInfoText.setText("");
//                        speaker.speak("please swipe or insert your card");
                        Snackbar.make(view, getResources().getString(R.string.checkCard), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        now = System.currentTimeMillis();
                    }

                    @Override
                    public void onSucceed(Bundle bundle) {
                        final BankCardInfoBean bean = bundle.getParcelable("data");
                        long finish = System.currentTimeMillis();
                        Snackbar.make(view, getResources().getString(R.string.readComplete) + ":useTime:" + (finish - now) + "ms", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        if (bean.cardType == BankCardModule.IC_CARD) {
                            cardTypeText.setText(getResources().getString(R.string.ICC));
                            cardNoText.setText(bean.cardNo);
                            cardInfoText.setText(bean.ICChipData);

                        } else if (bean.cardType == BankCardModule.PIC_CARD) {

                            cardTypeText.setText(getResources().getString(R.string.PICC));
                            cardNoText.setText(bean.cardNo);
                            cardInfoText.setText(bean.ICChipData);

                        } else if (bean.cardType == BankCardModule.MSR_CARD) {
                            cardTypeText.setText(getResources().getString(R.string.MSR));
                            cardNoText.setText(bean.cardNo);
                            cardInfoText.setText(bean.oneMagneticTrack + "\r\n" + bean.twoMagneticTrack + "\r\n" + bean.threeMagneticTrack);

                        }
//                        speaker.speak("ok!");
                        /**
                         *
                         * 重要:如果不需要做ARPC等写卡操作，请在读取完成后关闭，不然会导致异常，如果需要做ARPC请在做完ARPC后再关闭，如果提前关闭会导致ARPC失败，调用方式如下
                         *SiecomDevice.shutDownCard();
                         *
                         *
                         */
                        //如果不是磁卡可以获取交易的明细和做arpc
                        if (bean.cardType != BankCardModule.MSR_CARD) {
                            arpcBtn.setVisibility(View.VISIBLE);
                            getLogBtn.setVisibility(View.VISIBLE);

                            arpcBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String arpc = "910A2501EEC5A3FDBF563030";//91开头，脚本是72开头
                                    final String script = "7251860F04DA9F790A000000000000A4B17D6D860F04DA9F770A0000001000008C09E6BF862D04DC010C2870229F61123433313232313139383830373136313231589F6201105F2006C5CBB0D8D3EEA168BB5A";

                                    cardInfoText.setText("TestARPC:" + arpc + "\r\n" + "TestScript:" + script);
                                    /**
                                     * 这里可以做ARPC校验，这里是数据是测试的数据，百分百失败的
                                     */
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            // TODO Auto-generated method stub
                                            byte[] ScriptResult = new byte[1024];
                                            byte[] TC = new byte[1024];

                                            /**
                                             * 这是同步接口
                                             */
                                            int ret = SiecomDevice.SyncARPCExecuteScript(bean.cardType, arpc, script, ScriptResult, TC);
                                            Log.e("ARPC", "ret:" + ret + ",ScriptResult==" + ByteTool.bytearrayToHexString(ScriptResult, 1024));
                                            Message msg = new Message();
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("ret", ret);
                                            bundle.putByteArray("ScriptResult", ScriptResult);
                                            bundle.putByteArray("TC", TC);
                                            msg.setData(bundle);
                                            msg.what = arpc_ret;
                                            handler.sendMessage(msg);

                                        }
                                    }).start();

                                }
                            });
                            getLogBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //获取交易明细
                                    cardInfoText.setText("正在获取交易明细");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int[] length = new int[1];
                                            byte[] tlog = new byte[1024];
                                            int ret = SiecomDevice.SyncGetLog(bean.cardType, tlog, length);
                                            Message msg = new Message();
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("ret", ret);
                                            bundle.putInt("length", length[0]);
                                            bundle.putByteArray("log", tlog);
                                            msg.setData(bundle);
                                            msg.what = get_log_ret;
                                            handler.sendMessage(msg);

                                        }
                                    }).start();
                                }

                            });
                        } else {

                            arpcBtn.setVisibility(View.INVISIBLE);
                            getLogBtn.setVisibility(View.INVISIBLE);
                        }


                    }

                    @Override
                    public void onError(Bundle bundle) {
                        int code = bundle.getInt("code");
                        String msg = bundle.getString("message");

                        //speaker.speak("error：" + code);

                        Snackbar.make(view, "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();


                    }
                };
                /**
                 * 可以自定义一些参数，参数具体含义请参考EmvOptions 内部说明
                 */
                EmvOptions options = new EmvOptions();
                String time = "151215231111";// 交易时间，年月日时分秒 2015为15 直接传入java生成的即可
                options.setTime(time);
                byte[] MerchCateCode = new byte[2];
                MerchCateCode[0] = 0x00;
                MerchCateCode[1] = 0x01;
                options.setMerchCateCode(MerchCateCode);
                String MerchId = "123456789012345";
                options.setMerchId(MerchId.getBytes());
                options.setMerchName("EMV LIBRARY".getBytes());
                options.setTermId("12345678".getBytes());
                options.setTerminalType((byte) 0x22);
                options.setSupportPSESel((byte) 0x01);
                byte[] Capability = {(byte) 0xE0, (byte) 0xC0, (byte) 0xC8};
                options.setCapability(Capability);
                byte[] ExCapability = {(byte) 0x60, (byte) 0x00, (byte) 0xF0,
                        (byte) 0x20, (byte) 0x01};
                options.setExCapability(ExCapability);
                byte[] CountryCode = {(byte) 0x01, (byte) 0x56};
                options.setCountryCode(CountryCode);
                options.setTransCurrCode(CountryCode);
                options.setTransCurrExp((byte) 0x02);
                options.setReferCurrCode(CountryCode);
                options.setCL_TransLimit(100000);
                options.setTransType((byte) 0x01);// 交易类型

                byte[] TermTransQuali = {0x24, 0x00, 0x00, (byte) 0x80};
                options.setTermTransQuali(TermTransQuali);
                options.setCL_bStatusCheck((byte) 0x00);
                options.setCL_CVMLimit(10000);
                options.setCL_FloorLimit(20000);
                options.setTransNo(1);// 交易号 可以不用设置，默认1；

                String[] tags = {"5A", "57", "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A03",
                        "9C", "9F02", "5F2A", "82", "9F1A", "9F03", "9F33", "9F34",
                        "9F35", "9F1E", "84", "9F09", "9F41", "9F63"};
                options.setTags(tags);// 设置要获取的标签类型 要读取卡号必须保证有5A和57的标签

                /**
                 * 可以使用默认设置，options可以null
                 * SiecomDevice.ReadBankCard(cardType,null,taskCallback);
                 */

                SiecomDevice.ReadBankCard(cardType, options, taskCallback, 60 * 1000);

                arpcBtn.setVisibility(View.INVISIBLE);
                getLogBtn.setVisibility(View.INVISIBLE);

            }
        });

    }


    /**
     * 密码键盘的界面
     */
    private void showKeyDialog() {

        final View view = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.keypad_dialog, null);
        TextView main_key = (TextView) view.findViewById(R.id.main_key_text);
        main_key.setText(getResources().getString(R.string.main_key) + ":" + ByteTool.byte2hex(master_key));
        TextView work_key_text = (TextView) view.findViewById(R.id.work_key_text);
        work_key_text.setText(getResources().getString(R.string.work_key) + ":" + ByteTool.byte2hex(work_key));
        com.rey.material.widget.Button input_mkey_btn = (com.rey.material.widget.Button) view.findViewById(R.id.input_mkey_btn);
        com.rey.material.widget.Button input_wkey_btn = (com.rey.material.widget.Button) view.findViewById(R.id.input_wkey_btn);
        final TextView cardNo = (TextView) view.findViewById(R.id.card_no_str);
        TextView amount_text = (TextView) view.findViewById(R.id.amount_str);
        cardNo.setText(getResources().getString(R.string.card_no) + ":" + bankCardNo);
        amount_text.setText(getResources().getString(R.string.amount) + ":" + amount);
        final AppCompatEditText click_input = (AppCompatEditText) view.findViewById(R.id.click_input);
        click_input.setInputType(InputType.TYPE_NULL);
        click_input.requestFocus();

        input_mkey_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 注入主密钥
                 */
                int keyindex = 0;//使用第0组存储密钥；
                SiecomTask.TaskCallback taskCallback = new SiecomTask.TaskCallback() {
                    @Override
                    public void onStart(Bundle bundle) {

                        Snackbar.make(view, getResources().getString(R.string.input_key_start), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }

                    @Override
                    public void onSucceed(Bundle bundle) {

                        Snackbar.make(view, getResources().getString(R.string.input_main_succ), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }

                    @Override
                    public void onError(Bundle bundle) {
                        int code = bundle.getInt("code");
                        String msg = bundle.getString("message");

                        Snackbar.make(view, "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();


                    }
                };
                SiecomDevice.inputMainKey(keyindex, master_key, taskCallback);
            }
        });

        input_wkey_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 注入工作密钥
                 */
                int keyindex = 0;//使用第0组存储密钥；
                int mainKeyIndex = 0;//对应的主密钥索引，参考之前注入的主密钥使用第0组
                SiecomTask.TaskCallback taskCallback = new SiecomTask.TaskCallback() {
                    @Override
                    public void onStart(Bundle bundle) {
                        Snackbar.make(view, getResources().getString(R.string.input_key_start), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    @Override
                    public void onSucceed(Bundle bundle) {

                        Snackbar.make(view, getResources().getString(R.string.input_work_succ), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }

                    @Override
                    public void onError(Bundle bundle) {
                        int code = bundle.getInt("code");
                        String msg = bundle.getString("message");
                        Snackbar.make(view, "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }
                };
                SiecomDevice.inputWorkKey(keyindex, mainKeyIndex, work_key, taskCallback);
            }
        });

        click_input.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                /**
                 * 输入密码
                 */
                Log.e("click", "-------!!");
                int keyindex = 0;//使用第0组密钥对；
                int timeOut = 30;//输入超时时间
                byte maxLen = 0x06;//输入最大长度
                final byte encryptType = 0x00;// 0x00 ANSI X9.8加密，0x03明文
                SiecomTask.TaskCallback callback = new SiecomTask.TaskCallback() {
                    @Override
                    public void onStart(Bundle bundle) {
                        Snackbar.make(view, getResources().getString(R.string.start_input), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        click_input.setText("");
                    }

                    @Override
                    public void onSucceed(Bundle bundle) {
                        // TODO Auto-generated method stub

                        int op = bundle.getInt("option");

                        switch (op) {
                            case KeyBroadModule.OP_CANCEL:
                                click_input.setText("");
                                Snackbar.make(view, getResources().getString(R.string.input_cancel), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                break;
                            case KeyBroadModule.OP_FINISH:
                                byte[] pin = bundle.getByteArray("password");
                                if (encryptType == 0x00) {
                                    //x9.8加密
                                    click_input.setText(ByteTool.byte2hex(pin));
                                } else if (encryptType == 0x03) {
                                    //明文
                                    click_input.setText(new String(pin).trim());
                                }
                                break;

                            case KeyBroadModule.OP_INPUTTING:
                                // 显示*号的个数
                                int num = bundle.getInt("keyNum");
                                StringBuffer buf = new StringBuffer();
                                for (int i = 0; i < num; i++)
                                    buf.append("*");

                                click_input.setText(buf.toString());
                                break;
                            default:
                                break;
                        }

                    }

                    @Override
                    public void onError(Bundle bundle) {
                        // TODO Auto-generated method stub
                        int code = bundle.getInt("code");
                        String msg = bundle.getString("message");
                        Log.e("code", code + "----" + msg);
                        if (code == -7009) {
                            Snackbar.make(view, getResources().getString(R.string.input_timeout), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else if (code == -880) {
                            Snackbar.make(view, getResources().getString(R.string.input_timeout) + "," + getResources().getString(R.string.maybe_disconnect), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            Snackbar.make(view, "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                        click_input.setText("");
                    }
                };
                SiecomDevice.getPin(keyindex, bankCardNo, amount, timeOut, maxLen, encryptType, callback);
            }
        });
        String str = getResources().getString(R.string.no);
        pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                .theme(Theme.LIGHT)
                .title(R.string.keyPin)
                .customView(view, true)
                .positiveText(str)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                        dismissDialog();

                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        /**
                         * 取消任务；
                         */
                        SiecomDevice.cancelLastTask(true);


                    }
                })
                .build();
        pDialog.show();


    }


    /**
     * 连接设备
     */
    private void initLink() {

        SiecomTask.TaskCallback taskCallback = new SiecomTask.TaskCallback() {
            @Override
            public void onStart(Bundle bundle) {

                Snackbar.make(mLinearLayout, getResources().getString(R.string.conn_start), Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

            @Override
            public void onSucceed(Bundle bundle) {

                afterLink();
                Snackbar.make(mLinearLayout, getResources().getString(R.string.conn_succ), Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

            @Override
            public void onError(Bundle bundle) {
                int code = bundle.getInt("code");
                String msg = bundle.getString("message");
                Snackbar.make(mLinearLayout, getResources().getString(R.string.conn_err) + "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        };

        SiecomDevice.connectToBtDevice(device, taskCallback);


        /**
         * 用于监听整个通讯过程中蓝牙连接变化
         */
        SiecomDevice.deviceConnStatusChange(new DeviceConStatusListen() {

            @Override
            public void onDisconnect() {
                Log.e("conn", "onDisconnect");
                /**
                 * 断开之后可以取消任务了 ，参数isConnected，因为断开所以已经false
                 */
                SiecomDevice.cancelLastTask(false);
                handler.sendEmptyMessage(dis_connect);

            }

            @Override
            public void connected() {
                Log.e("conn", "connected");
            }

            @Override
            public void connecting() {
                Log.e("conn", "connecting");
            }
        });


    }


    /**
     * 读取指纹特征码
     */
    public void readFingerPrint() {

        SiecomTask.TaskCallback taskCallback = new SiecomTask.TaskCallback() {
            @Override
            public void onStart(Bundle bundle) {

                dismissDialog();

                View view = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.layout_finger_scan, null);
                scan_line = (ImageView) view.findViewById(R.id.lockscreen_scan_line);
                upAnimation = AnimationUtils.loadAnimation(DeviceActivity.this, R.anim.push_up_in);
                upAnimation.setFillAfter(true);
                upAnimation.setAnimationListener(new UpAnimationOnListener());

                downAnimation = AnimationUtils.loadAnimation(DeviceActivity.this, R.anim.push_up_out);
                downAnimation.setFillAfter(true);
                downAnimation.setAnimationListener(new DownAnimationOnListener());
                scan_line.startAnimation(downAnimation);
                pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                        .theme(Theme.DARK)
                        .customView(view, true)
                        .title(R.string.finger_scan)
                        .positiveText(R.string.no)
                        .cancelable(true)
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                /**
                                 * 取消任务；
                                 */
                                SiecomDevice.cancelLastTask(true);


                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                dismissDialog();
                                Snackbar.make(mLinearLayout, "取消指纹后再启动最长可能需要5~7秒", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            }
                        })
                        .build();
                pDialog.show();


            }

            @Override
            public void onSucceed(Bundle bundle) {

                /**
                 * 获取到指纹特征码，默认使用Base64转一下
                 */
                String fingerCode = bundle.getString("data");

                Log.e("fingerPrint:", fingerCode);

                dismissDialog();

                pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                        .theme(Theme.DARK)
                        .title(R.string.finger_code)
                        .content(getResources().getString(R.string.your_finger) + ":" + fingerCode)
                        .positiveText(R.string.sure)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                dismissDialog();
                            }
                        })
                        .build();
                pDialog.show();

            }

            @Override
            public void onError(Bundle bundle) {
                int code = bundle.getInt("code");
                String msg = bundle.getString("message");
                Snackbar.make(mLinearLayout, "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                dismissDialog();

            }
        };
        SiecomDevice.ReadFinger(Config.WELL_FINGER, taskCallback, 60 * 1000);

    }

    /**
     * 读取身份证
     */
    public void readIDcard() {

        SiecomTask.TaskCallback taskCallback = new SiecomTask.TaskCallback() {
            @Override
            public void onStart(Bundle bundle) {
                now = System.currentTimeMillis();
                Snackbar.make(mLinearLayout, getResources().getString(R.string.read_id), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                        .title(R.string.read_id)
                        .content(R.string.please_wait)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .positiveText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                dismissDialog();
                            }
                        })
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                /**
                                 * 取消任务；
                                 */
                                SiecomDevice.cancelLastTask(true);


                            }
                        })
                        .build();
                pDialog.show();
            }

            @Override
            public void onSucceed(Bundle bundle) {
                 long fin = System.currentTimeMillis();
                Toast.makeText(DeviceActivity.this,"useTime:"+(fin-now)+"ms",Toast.LENGTH_SHORT).show();
                //获取bean
                IdentityInfoBean bean = bundle.getParcelable("data");

                dismissDialog();

                View view = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.draw_idcard, null);
                tv_name = (TextView) view.findViewById(R.id.name);
                tv_sex = (TextView) view.findViewById(R.id.sex);
                tv_nation = (TextView) view.findViewById(R.id.nation);
                tv_birth = (TextView) view.findViewById(R.id.birth);
                tv_address = (TextView) view.findViewById(R.id.address);
                tv_id = (TextView) view.findViewById(R.id.id);
                tv_police = (TextView) view.findViewById(R.id.police);
                tv_validate = (TextView) view.findViewById(R.id.validate);
                ivPhotoes = (ImageView) view.findViewById(R.id.touxiang);

                tv_name.setText(bean.fullName);
                tv_sex.setText(bean.sex);
                tv_nation.setText(bean.nation);
                String birthday = bean.birthday.substring(0, 4) + "        " + bean.birthday.substring(4, 6)
                        + "     " + bean.birthday.substring(6, 8);
                tv_birth.setText(birthday);
                tv_address.setText(bean.idAddr);
                tv_id.setText(bean.idNo);
                tv_police.setText(bean.idOrg);
                tv_validate.setText(bean.beginDate + "-" + bean.endDate);
                ivPhotoes.setImageBitmap(bean.icon);
                String str = getResources().getString(R.string.no_finger);
                if (bean.fingerByte != null) {
                    str = getResources().getString(R.string.has_finger);
                }
                pDialog = new MaterialDialog.Builder(DeviceActivity.this)
                        .theme(Theme.DARK)
                        .customView(view, true)
                        .positiveText(str)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                dismissDialog();
                            }
                        })
                        .build();
                pDialog.show();


            }

            @Override
            public void onError(Bundle bundle) {
                int code = bundle.getInt("code");
                String msg = bundle.getString("message");
                Log.e("------", msg + "!!!!!");
                Snackbar.make(mLinearLayout, "err:" + code + "--" + msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                dismissDialog();
            }
        };
        SiecomDevice.ReadIdentity(true, taskCallback, 60 * 1000);

    }

    /**
     * UI界面
     */
    private void addUI() {
        /*  身份证 */
        LinearLayout rl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_card, null);
        rl.findViewById(R.id.item_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                readIDcard();
            }
        });
        mLinearLayout.addView(rl);

        /*  指纹 */
        rl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_card, null);
        TextView tv = (TextView) rl.findViewById(R.id.read_id);
        tv.setText(getResources().getString(R.string.read_finger));
        ImageView iv = (ImageView) rl.findViewById(R.id.id_icon);
        iv.setImageResource(R.mipmap.fingerprint);

        rl.findViewById(R.id.item_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                readFingerPrint();
            }

        });

        mLinearLayout.addView(rl);


        /*密码键盘*/
        rl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_card, null);
        tv = (TextView) rl.findViewById(R.id.read_id);
        tv.setText(getResources().getString(R.string.keyPin));
        iv = (ImageView) rl.findViewById(R.id.id_icon);
        iv.setImageResource(R.mipmap.keyboard);

        rl.findViewById(R.id.item_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showKeyDialog();

            }
        });
        mLinearLayout.addView(rl);


          /*银行卡*/
        rl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_card, null);
        tv = (TextView) rl.findViewById(R.id.read_id);
        tv.setText(getResources().getString(R.string.bankCard));
        iv = (ImageView) rl.findViewById(R.id.id_icon);
        iv.setImageResource(R.mipmap.ic_credit_card);

        rl.findViewById(R.id.item_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCardDialog();

            }
        });
        mLinearLayout.addView(rl);

        rl = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_card, null);
        tv = (TextView) rl.findViewById(R.id.read_id);
        tv.setText(getResources().getString(R.string.other));
        iv = (ImageView) rl.findViewById(R.id.id_icon);
        iv.setImageResource(R.mipmap.settings_black);

        rl.findViewById(R.id.item_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showOtherDialog();
            }
        });
        mLinearLayout.addView(rl);


    }


    /**
     * 连接成功后
     */
    public void afterLink() {
        spb.progressiveStop();
        deviceName.setTextColor(ContextCompat.getColor(DeviceActivity.this, R.color.forest_green));
        TransitionDrawable mTransitionDrawable = new TransitionDrawable(new Drawable[]{
                deviceIcon.getDrawable(),
                ContextCompat.getDrawable(DeviceActivity.this, R.mipmap.connected_black)
        });
        mTransitionDrawable.setCrossFadeEnabled(true);
        mTransitionDrawable.startTransition(1000);
        deviceIcon.setImageDrawable(mTransitionDrawable);
        addUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_identify);
        device = getIntent().getParcelableExtra("Device");
        deviceName = (TextView) findViewById(R.id.device_name);
        deviceMac = (TextView) findViewById(R.id.device_mac);
        deviceVersion = (TextView) findViewById(R.id.device_version);
        deviceName.setText(device.getName());
        deviceMac.setText(device.getAddress());
        power = (TextView) findViewById(R.id.power);
        deviceIcon = (ImageView) findViewById(R.id.device_icon);
        spb = (SmoothProgressBar) findViewById(R.id.spb);
        spb.setVisibility(View.INVISIBLE); // wait for runnable
        mLinearLayout = (LinearLayout) findViewById(R.id.info_scroll);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spb.setVisibility(View.VISIBLE);
                initLink();
            }
        }, 200);
        /**
         * 语音播放
         */
        //speaker = new TextSpeaker(this);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /**
     * 指纹动画效果
     */
    private class DownAnimationOnListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            scan_line.startAnimation(upAnimation);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }

    }

    private class UpAnimationOnListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            scan_line.startAnimation(downAnimation);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }


}




