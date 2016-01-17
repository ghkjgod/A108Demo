package com.siecom.framework.appinterface;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.siecom.framework.channel.ChannelInstance;
import com.siecom.framework.emvkernel.EMVCAPK;
import com.siecom.framework.emvkernel.EMV_APPLIST;
import com.siecom.framework.emvkernel.EMV_PARAM;
import com.siecom.tools.ByteTool;
import com.siecom.tools.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.siecom.nativelibs.NativeFunction;

public class Api {

	public final int ERCD_COM_TIMEOUT = -2500;
	public final int ERCD_COM_WLEN = -2501;
	public final int ERCD_COM_COMM = -2502;
	public final int ERCD_COM_DATA = -2503;
	public final int ERCD_COM_WCMD = -2504;
	public final int ERCD_COM_EDC = -2505;
	public final int ERCD_COM_OTHERS = -2506;
	public final int ERCD_COM_SEND = -2507;
	public final int ERCD_COM_RECV = -2508;
	public final int RECV_PACKET_ERROR = -2509;
	public final int SEND_PACKET_ERROR = -2510;
	public final int PACKET_LEN_ERROR = -2511;
	public final int RECV_TIMEOUT = -2512;
	public final int PACKET_LEN_TOO_LONG = -2513;
	public final int BLUETOOTH_HANDSHACK_FAIL = -1000;
	public final int BLUETOOTH_SEND_ERR = -1;
	public final int BLUETOOTH_RECV_TIMEOUT = -2;
	public final int BLUETOOTH_RECV_LENERR = -3;
	public final int BLUETOOTH_RECV_FUNNO_ERR = -4;
	public final int RECV_PACKET_MAX_LEN = 2176;
	public final byte BLTH_MODULE_SYSTEM = 0;
	public final byte BLTH_MODULE_PED = 1;
	public final byte BLTH_MODULE_ICC = -73;
	public final byte BLTH_MODULE_CODEREAD = -69;
	public final byte BLTH_MODULE_MAGIC = -63;
	public final byte BLTH_MODULE_PRINT = 6;
	public final byte BLTH_MODULE_PICC = -60;
	public final byte BLTH_HANDSHACK = 1;
	public final byte BLTH_READ_VER_INFO = 2;
	public final byte BLTH_SYSTEM_TEST_RESPONSE = 3;
	public final byte BLTH_WRITE_BT_NAME = 4;
	public final byte BLTH_PED_WRITE_MKEY = 1;
	public final byte BLTH_PED_WRITE_PINKEY = 2;
	public final byte BLTH_PED_WRITE_MACKEY = 3;
	public final byte BLTH_PED_WRITE_DESKEY = 4;
	public final byte BLTH_PED_DERIVE_PINKEY = 5;
	public final byte BLTH_PED_DERIVE_MACKEY = 6;
	public final byte BLTH_PED_DERIVE_DESKEY = 7;
	public final byte BLTH_PED_GET_PIN = 8;
	public final byte BLTH_PED_GET_MAC = 9;
	public final byte BLTH_PED_DES = 10;
	public final byte BLTH_MCR_OPEN = 1;
	public final byte BLTH_MCR_CLOSE = 3;
	public final byte BLTH_MCR_RESET = 9;
	public final byte BLTH_MCR_CHECK = 5;
	public final byte BLTH_MCR_READ = 7;
	public final byte BLTH_ICC_DETECT = 7;
	public final byte BLTH_ICC_RESET = 1;
	public final byte BLTH_ICC_CLOSE = 3;
	public final byte BLTH_ICC_COMMAND = 5;
	public final byte BLTH_PICC_OPEN = 1;
	public final byte BLTH_PICC_CLOSE = 3;
	public final byte BLTH_PICC_CHECK = 5;
	public final byte BLTH_PICC_COMMAND = 7;
	public final byte BLTH_PICC_REMOVE = 9;
	public final byte BLTH_PICC_HALT = 11;
	public final byte BLTH_PICC_RESET = 13;
	public final byte BLTH_PICC_M1_AUTHORITY = 17;
	public final byte BLTH_PICC_M1_READ_BLOCK = 19;
	public final byte BLTH_PICC_M1_WRITE_BLOCK = 21;
	public final byte BLTH_PICC_M1_OPERATE = 23;
	public final int SUCCESS = 0;
	public final int ICC_VCCMODEERR = -2500;
	public final int ICC_INPUTSLOTERR = -2501;
	public final int ICC_VCCOPENERR = -2502;
	public final int ICC_ICCMESERR = -2503;
	public final int ICC_T0_TIMEOUT = -2200;
	public final int ICC_T0_MORESENDERR = -2201;
	public final int ICC_T0_MORERECEERR = -2202;
	public final int ICC_T0_PARERR = -2203;
	public final int ICC_T0_INVALIDSW = -2204;
	public final int ICC_DATA_LENTHERR = -2400;
	public final int ICC_PARERR = -2401;
	public final int ICC_PARAMETERERR = -2402;
	public final int ICC_SLOTERR = -2403;
	public final int ICC_PROTOCALERR = -2404;
	public final int ICC_CARD_OUT = -2405;
	public final int ICC_NO_INITERR = -2406;
	public final int ICC_ICCMESSOVERTIME = -2407;
	public final int ICC_ATR_TSERR = -2100;
	public final int ICC_ATR_TCKERR = -2101;
	public final int ICC_ATR_TIMEOUT = -2102;
	public final int ICC_TS_TIMEOUT = -2115;
	public final int ICC_ATR_TA1ERR = -2103;
	public final int ICC_ATR_TA2ERR = -2104;
	public final int ICC_ATR_TA3ERR = -2105;
	public final int ICC_ATR_TB1ERR = -2106;
	public final int ICC_ATR_TB2ERR = -2107;
	public final int ICC_ATR_TB3ERR = -2108;
	public final int ICC_ATR_TC1ERR = -2109;
	public final int ICC_ATR_TC2ERR = -2110;
	public final int ICC_ATR_TC3ERR = -2111;
	public final int ICC_ATR_TD1ERR = -2112;
	public final int ICC_ATR_TD2ERR = -2113;
	public final int ICC_ATR_LENGTHERR = -2114;

	public final int ICC_T1_BWTERR = -2300;
	public final int ICC_T1_CWTERR = -2301;
	public final int ICC_T1_ABORTERR = -2302;
	public final int ICC_T1_EDCERR = -2303;
	public final int ICC_T1_SYNCHERR = -2304;
	public final int ICC_T1_EGTERR = -2305;
	public final int ICC_T1_BGTERR = -2306;
	public final int ICC_T1_NADERR = -2307;
	public final int ICC_T1_PCBERR = -2308;
	public final int ICC_T1_LENGTHERR = -2309;
	public final int ICC_T1_IFSCERR = -2310;
	public final int ICC_T1_IFSDERR = -2311;
	public final int ICC_T1_MOREERR = -2312;
	public final int ICC_T1_PARITYERR = -2313;
	public final int ICC_T1_INVALIDBLOCK = -2314;

	public final int ICC_ER_DAIN = -2600;
	public final int ICC_ER_DNIN = -2601;
	public final int ICC_ER_NOCD = -2602;
	public final int ICC_ER_SYSF = -2603;
	public final int ICC_ER_TMOT = -2604;
	public final int ICC_ER_AFTM = -2605;
	public final int ICC_ER_INVA = -2606;
	public final int ICC_ER_PAER = -2607;
	public final int ICC_ER_FRAM = -2608;
	public final int ICC_ER_EDCO = -2609;
	public final int ICC_ER_INFR = -2610;
	public final int ICC_ER_INFN = -2611;
	public final int ICC_ER_INDN = -2612;
	public final int ICC_ER_INPA = -2613;
	public final int ICC_ER_TOPS = -2614;
	public final int ICC_ER_INPS = -2615;
	public final int ICC_ER_DOVR = -2616;

	public final int ICC_ER_NSFN = -2617;
	public final int ICC_ER_NSDN = -2618;
	public final int ICC_ER_NSPR = -2619;
	public final int ICC_ER_MEMF = -2620;

	public final int PICC_OK = 0;
	public final int PICC_ChipIDErr = -3500;
	public final int PICC_OpenErr = -3501;
	public final int PICC_NotOpen = -3502;
	public final int PICC_ParameterErr = -3503;
	public final int PICC_TxTimerOut = -3504;
	public final int PICC_RxTimerOut = -3505;
	public final int PICC_RxDataOver = -3506;
	public final int PICC_TypeAColl = -3507;
	public final int PICC_FifoOver = -3508;
	public final int PICC_CRCErr = -3509;
	public final int PICC_SOFErr = -3510;
	public final int PICC_ParityErr = -3511;
	public final int PICC_KeyFormatErr = -3512;

	public final int PICC_RequestErr = -3513;
	public final int PICC_AntiCollErr = -3514;
	public final int PICC_UidCRCErr = -3515;
	public final int PICC_SelectErr = -3516;
	public final int PICC_RatsErr = -3517;
	public final int PICC_AttribErr = -3518;
	public final int PICC_HaltErr = -3519;
	public final int PICC_OperateErr = -3520;
	public final int PICC_WriteBlockErr = -3521;
	public final int PICC_ReadBlockErr = -3522;
	public final int PICC_AuthErr = -3523;
	public final int PICC_ApduErr = -3524;
	public final int PICC_HaveCard = -3525;
	public final int PICC_Collision = -3526;
	public final int PICC_CardTyepErr = -3527;
	public final int PICC_CardStateErr = -3528;

	public final int PICC_RxTimerOut2 = -3529;
	public final int PICC_RxErr = -3530;
	public final int PICC_RxOverFlow = -3531;

	public final int PICC_ProtocolErr = -3532;

	public final int PICC_FastOut = -3533;

	public final int PICC_Fsderror = -3533;
	public final int PICC_CRCErr2 = -3534;
	public final int PICC_Continue = -3535;
	public final int PICC_RxBlockErr = -3536;

	public final int PICC_ApduErr1 = -3540;
	public final int PICC_ApduErr2 = -3541;
	public final int PICC_ApduErr3 = -3542;
	public final int PICC_ApduErr4 = -3543;
	public final int PICC_ApduErr5 = -3544;
	public final int PICC_ApduErr6 = -3545;
	public final int PICC_ApduErr7 = -3546;
	public final int PICC_ApduErr8 = -3547;
	public final int PICC_ApduErr9 = -3548;
	public final int PICC_ApduErr10 = -3549;
	public final int PICC_ApduErr11 = -3550;
	public final int PICC_ApduErr12 = -3551;
	public final int PICC_ApduErr13 = -3552;
	public final int PICC_ApduErr14 = -3553;
	public final int PICC_ApduErr15 = -3554;
	public final int PICC_ApduErr16 = -3555;
	public final int PICC_ApduErr17 = -3556;
	public final int PICC_ApduErr18 = -3557;
	public final int PICC_ApduErr19 = -3558;
	public final int PICC_ApduErr20 = -3559;
	public final int PICC_ApduErr21 = -3560;
	public final int PICC_ApduErr22 = -3561;
	public final int PICC_ApduErr23 = -3562;
	public final int PICC_ApduErr24 = -3563;

	public final int PRN_BUSY = -4001;
	public final int PRN_NOPAPER = -4002;
	public final int PRN_DATAERR = -4003;
	public final int PRN_FAULT = -4004;
	public final int PRN_TOOHEAT = -4005;
	public final int PRN_UNFINISHED = -4006;
	public final int PRN_NOFONTLIB = -4007;
	public final int PRN_BUFFOVERFLOW = -4008;
	public final int PRN_SETFONTERR = -4009;
	public final int PRN_GETFONTERR = -4010;

	byte[] g_abySendBuff = new byte[2176];
	byte[] g_abyRecvBuff = new byte[2176];
	byte[] abyBuff = null;
	byte[] crcOut = new byte[2];
	int[] g_wRecvLen = new int[1];
	int g_PrnStartFlag = 0;
	byte[] macOut = new byte[8];
	byte[] PTK = null;
	byte[] keyTemp = new byte[8];
	byte[] outTemp = new byte[8];
	
	public int Lib_LcdDrawImage(byte index, byte mode) {
		this.g_abySendBuff[0] = index;
		this.g_abySendBuff[1] = mode;
		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xb9,
				(byte) 97);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-71 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;
	}

	public int Lib_LcdCls() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xb9,
				(byte) 22);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (8 != this.g_wRecvLen[0])
			return -3;
		if (-71 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lib_Lcdprintf(String str) {
		byte[] buf = (byte[]) null;
		try {
			buf = str.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int length = buf.length;

		System.arraycopy(buf, 0, this.g_abySendBuff, 0, length);
		int iRet = SendPacket(this.g_abySendBuff, length, (byte) 0xb9,
				(byte) 50);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lib_LcdPrintxy(byte col, byte row, byte mode, String str) {
		byte[] buf = (byte[]) null;
		try {
			buf = str.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int length = buf.length;

		this.g_abySendBuff[0] = col;
		this.g_abySendBuff[1] = row;
		this.g_abySendBuff[2] = mode;
		System.arraycopy(buf, 0, this.g_abySendBuff, 3, length);
		int iRet = SendPacket(this.g_abySendBuff, 3 + length,
				(byte) 0xb9, (byte) 24);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lib_LcdGotoxy(byte x, byte y) {
		this.g_abySendBuff[0] = x;
		this.g_abySendBuff[1] = y;

		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xb9,
				(byte) 48);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lcd_ClrLine(byte startline, byte endline) {
		this.g_abySendBuff[0] = startline;
		this.g_abySendBuff[1] = endline;

		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xb9,
				(byte) 64);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lcd_SetFont(byte mode, byte index) {
		this.g_abySendBuff[0] = mode;
		this.g_abySendBuff[1] = index;

		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xb9,
				(byte) 54);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lcd_Flush() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xb9,
				(byte) 68);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Lib_FpGetImg(byte index, byte[] finishFlag, byte[] dataOut,
			int[] lenOut) {
		this.g_abySendBuff[0] = 0;
		this.g_abySendBuff[1] = index;
		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) -32,
				(byte) 13);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 20000);
		if (iRet != 0)
			return -2;
		if (14 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			finishFlag[0] = this.g_abyRecvBuff[7];
			lenOut[0] = ((this.g_abyRecvBuff[3] & 0xFF) * 256
					+ (this.g_abyRecvBuff[4] & 0xFF) - 3);
			Log.e("BluetoothCom", "dataLen = " + lenOut[0]);
			System.arraycopy(this.g_abyRecvBuff, 8, dataOut, 0, lenOut[0]);
		}
		return iRet;
	}

	public Bitmap CreateBMP(byte[] byBuffer, int iSize, String path,
			String bmpName) {
		int bmpWidth = 152;
		int bmpHeigh = 200;
		Bitmap bmpFingerprint = Bitmap.createBitmap(bmpWidth, bmpHeigh,
				Bitmap.Config.ARGB_8888);

		int iPos = 0;
		for (int x = 0; x < bmpWidth; x++) {
			for (int y = 0; y < bmpHeigh; y++) {
				iPos = y * bmpWidth + x;
				if (iSize <= iPos) {
					break;
				}

				int red = byBuffer[iPos];
				int green = byBuffer[iPos];
				int blue = byBuffer[iPos];

				int iColor = Color.rgb(red, green, blue);
				bmpFingerprint.setPixel(x, bmpHeigh - y - 1, iColor);
			}

		}
		Log.e("saveBitmap", "保存图片");
		File f = new File(path, bmpName);
		if (f.exists())
			f.delete();
		try {
			FileOutputStream out = new FileOutputStream(f);
			bmpFingerprint.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Log.i("saveBitmap", "已经保存");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bmpFingerprint;
	}

	/**
	 * 接收返回
	 *
	 * @return
	 */
	public int GetPinStarNum() {
		byte[] pbyRecvData = new byte[2];

		int ret = GetPinNum(pbyRecvData, 500);
		Log.e("GetPinStarNum:", "GetPinNum = " + ret);
		if (ret == 0) {
			byte key = pbyRecvData[0];
			Log.e("GetPinStarNum", "getPinNum key = " + key);
			return key;
		}

		return -1;
	}

	public int GetPinNum(byte[] pbyRecvData, int dwTimeoutMs) {
		int iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen,
				dwTimeoutMs);
		if (iRet != 0)
			return -2;
		if (10 != this.g_wRecvLen[0])
			return -3;
		if (-74 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		pbyRecvData[0] = (byte) (this.g_abyRecvBuff[7] & 0xFF);
		return 0;
	}


	public static byte GetEDC(byte[] byData, int pos, int iDataSize) {
		int iIndex = 0;
		byte edc = 0;
		for (iIndex = pos; iIndex < iDataSize + pos; iIndex++) {
			edc = (byte) (edc ^ byData[iIndex]);
		}
		return edc;
	}

	public void Sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int GetPin_quit() {
		int iRet = SendPacket(this.g_abySendBuff,0,
				(byte) 0xb6, (byte) 0x05);
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 500);
		if (iRet != 0) {
			return iRet;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;

//		byte[] Buff = new byte[10];
//		Buff[0] = -86;
//		ChannelInstance.sendBytes(Buff, 1);
//		return 0;

	}

	public int RecvBytes(byte[] rspMsg, int index, int wantedLen, int timeOutMs) {
		byte[] tempBuf = new byte[wantedLen];
		int ret = ChannelInstance.receiveBytes(tempBuf, wantedLen, timeOutMs);

        if(ret<0){
        	return ret;
        }
		System.arraycopy(tempBuf, 0, rspMsg, index, ret);
		return ret;
	}
	public int SendPacket(byte[] pbySendData, int wDataLen,
			byte byModule, byte byFunction) {
		this.abyBuff = new byte[2176];

		if ((wDataLen > 2048) || (wDataLen < 0)) {
			return ERCD_COM_WLEN;
		}
		this.abyBuff[0] = 2;
		this.abyBuff[1] = byModule;
		this.abyBuff[2] = byFunction;
		this.abyBuff[3] = (byte) (wDataLen / 256);
		this.abyBuff[4] = (byte) (wDataLen % 256);

		System.arraycopy(pbySendData, 0, this.abyBuff, 5, wDataLen);

		this.crcOut = new byte[2];

		/*  测试*/
		int len =  wDataLen + 4;
		byte[] buff = new byte[len];
		System.arraycopy(this.abyBuff,1,buff,0,len);
	//	Log.e("input:", ByteTool.byte2hex(buff));
		NativeFunction.Crc16CCITT2(this.abyBuff, wDataLen + 4, 1, this.crcOut);
	//	Log.e("crcOut:", ByteTool.byte2hex(crcOut));
		this.abyBuff[(wDataLen + 5)] = this.crcOut[0];
		this.abyBuff[(wDataLen + 6)] = this.crcOut[1];

		int byRet = ChannelInstance.sendBytes(this.abyBuff, wDataLen + 7);
		Log.i("SendPacketEX", "SendPacket: "+ ByteTool.bytearrayToHexString(this.abyBuff,
								wDataLen + 7));
		if (byRet != 0) {
			Log.e("BluetoothCom", "Send err! byRet = " + byRet);
			return -2507;
		}
		return 0;
	}

	public int SendPacket_key(byte[] pbySendData, int wDataLen,
			byte byModule, byte byFunction) {
		this.abyBuff = new byte[2176];

		byte[] Recv_clear_Buff = new byte[2048];
		int[] Recv_clear_Len = new int[1];
		RecvPacket(Recv_clear_Buff, Recv_clear_Len, 1);
		RecvPacket(Recv_clear_Buff, Recv_clear_Len, 1);

		if ((wDataLen > 2048) || (wDataLen < 0)) {
			return -2501;
		}
		this.abyBuff[0] = 2;
		this.abyBuff[1] = byModule;
		this.abyBuff[2] = byFunction;
		this.abyBuff[3] = (byte) (wDataLen / 256);
		this.abyBuff[4] = (byte) (wDataLen % 256);

		System.arraycopy(pbySendData, 0, this.abyBuff, 5, wDataLen);

		this.crcOut = new byte[2];
		NativeFunction.Crc16CCITT2(this.abyBuff, wDataLen + 4, 1, this.crcOut);
		this.abyBuff[(wDataLen + 5)] = this.crcOut[0];
		this.abyBuff[(wDataLen + 6)] = this.crcOut[1];

		int byRet = ChannelInstance.sendBytes(this.abyBuff, wDataLen + 7);
		Log.i("SendPacket",
				"SendPacket: "
						+ ByteTool.bytearrayToHexString(this.abyBuff,
								wDataLen + 7));
		if (byRet != 0) {
			Log.e("BluetoothCom", "Send err! byRet = " + byRet);
			return -2507;
		}
		return 0;
	}

	public int RecvPacket(byte[] pbyRecvData, int[] pwPacketetLen,
			int dwTimeoutMs) {
		int wDataLen = 0;
		int byRet;
		if (this.g_PrnStartFlag == 1) {
			byRet = RecvBytes(pbyRecvData, 0, 1, 20000);
		} else
			byRet = RecvBytes(pbyRecvData, 0, 1, dwTimeoutMs);
		if ((1 != byRet) || (2 != pbyRecvData[0])) {
			return -1;
		}
		byRet = RecvBytes(pbyRecvData, 1, 4, 200);
		if (4 != byRet) {
			return RECV_PACKET_ERROR;
		}
		wDataLen = (pbyRecvData[3] & 0xFF) * 256 + (pbyRecvData[4] & 0xFF);

		pwPacketetLen[0] = (wDataLen + 7);
		if (wDataLen > 2176) {
			return -2511;
		}

		byRet = RecvBytes(pbyRecvData, 5, wDataLen + 2, 500);
		if (wDataLen + 2 != byRet) {
			return RECV_PACKET_ERROR;
		}

		this.crcOut = new byte[2];
		NativeFunction.Crc16CCITT2(pbyRecvData, wDataLen + 4, 1, this.crcOut);
		if ((pbyRecvData[(5 + wDataLen)] != this.crcOut[0])
				|| (pbyRecvData[(6 + wDataLen)] != this.crcOut[1])) {
			return -2505;
		}
		Log.i("RecvPacket",
				"RecvPacket: "
						+ ByteTool.bytearrayToHexString(pbyRecvData,
						wDataLen + 7));

		return 0;
	}

	public int RecvPacket_test(byte[] pbyRecvData,
			int[] pwPacketetLen, int dwTimeoutMs) {
		int wDataLen = 0;
		int byRet;
		if (this.g_PrnStartFlag == 1)
			byRet = RecvBytes(pbyRecvData, 0, 1, 20000);
		else
			byRet = RecvBytes(pbyRecvData, 0, 1, dwTimeoutMs);
		if ((1 != byRet) || (2 != pbyRecvData[0])) {
			return -1;
		}

		byRet = RecvBytes(pbyRecvData, 1, 4, 200);
		if (4 != byRet) {
			return RECV_PACKET_ERROR;
		}

		wDataLen = (pbyRecvData[3] & 0xFF) * 256 + (pbyRecvData[4] & 0xFF);

		pwPacketetLen[0] = (wDataLen + 5);
		if (wDataLen > 2176) {
			return -2511;
		}

		byRet = RecvBytes(pbyRecvData, 5, wDataLen, 500);
		if (wDataLen != byRet) {
			return RECV_PACKET_ERROR;
		}

		Log.i("RecvPacket",
				"RecvPacket: "
						+ ByteTool.bytearrayToHexString(pbyRecvData,
								wDataLen + 5));

		return 0;
	}

	public int ReadVerInfo(byte[] VerInfo) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0x00,
				(byte) 0x02);
		if (iRet != 0) {
			return -1;
		}

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0)
			return iRet;
		if (15 != this.g_wRecvLen[0])
			return -3;
		if ((this.g_abyRecvBuff[1] != 0) || (2 != this.g_abyRecvBuff[2])) {
			return -4;
		}
		if (this.g_abyRecvBuff[5] == 0)
			System.arraycopy(this.g_abyRecvBuff, 6, VerInfo, 0, 8);
		return this.g_abyRecvBuff[5];
	}

	public int BT_Recv_Test(APDU_RESP ApduResp) {
		int iRet = RecvPacket_test(this.g_abyRecvBuff,
				this.g_wRecvLen, 2000);
		Log.d("", "BT -----recv = " + this.g_wRecvLen[0]);
		if (iRet == 0) {
			ApduResp.LenOut = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256 + (this.g_abyRecvBuff[4] & 0xFF));
			System.arraycopy(this.g_abyRecvBuff, 5, ApduResp.DataOut, 0,
					ApduResp.LenOut);
		}
		return iRet;
	}

	public int Lib_Beep() {
		this.g_abySendBuff[0] = 1;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xb8,
				(byte) 24);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}
		return this.g_abyRecvBuff[5];
	}

	public int Lib_Beep(int times) {
		this.g_abySendBuff[0] = (byte) times;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xb8,
				(byte) 24);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		return this.g_abyRecvBuff[5];
	}

	public int Sys_writeSN(byte[] SN) {

		System.arraycopy(SN, 0, this.g_abySendBuff, 0, 16);

		int iRet = SendPacket(this.g_abySendBuff, 16, (byte) 0xd1,
				(byte) 0x0c);

		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);

		return iRet;

	}

	public int Sys_GetSN(byte[] SN) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xd1,
				(byte) 0x0e);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;

		System.arraycopy(this.g_abyRecvBuff, 7, SN, 0, 16);

		return iRet;
	}

	public int IDCard_Open() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf0,
				(byte) 1);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0)
			return -2;
		if (-16 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;
	}

	public int IDCard_Close() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf0,
				(byte) 3);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 1000);
		if (iRet != 0)
			return -2;
		if (-16 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;
	}

	public int IDCard_Test(APDU_RESP ApduResp) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf0,
				(byte) 5);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 6000);
		Log.d("API", "ID -----recv = " + this.g_wRecvLen[0]);
		if (iRet != 0)
			return iRet;
		if (-16 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			ApduResp.LenOut = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
					+ (this.g_abyRecvBuff[4] & 0xFF) - 2);
			System.arraycopy(this.g_abyRecvBuff, 7, ApduResp.DataOut, 0,
					ApduResp.LenOut);
		}
		return iRet;
	}

	public int IDCard_Test_string(APDU_RESP ApduResp) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf0,
				(byte) 7);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 6000);
		Log.d("", "ID -----recv = " + this.g_wRecvLen[0]);
		if (iRet != 0)
			return iRet;
		if (-16 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			ApduResp.LenOut = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
					+ (this.g_abyRecvBuff[4] & 0xFF) - 2);
			System.arraycopy(this.g_abyRecvBuff, 7, ApduResp.DataOut, 0,
					ApduResp.LenOut);
		}
		return iRet;
	}

	public int TCD_Open() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xe0,
				(byte) 1);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 15000);
		if (iRet != 0)
			return -2;
		if (2 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;
	}

	public int TCD_Close() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xe0,
				(byte) 3);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (4 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;
	}

	public int Eroll_Test(int[] slot) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xe0,
				(byte) 5);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		if (iRet != 0) {
			return iRet;
		}
		if (6 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			slot[0] = (this.g_abyRecvBuff[7] & 0xFF);
		}
		return iRet;
	}

	public int Match_Test(int[] slot) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xe0,
				(byte) 7);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		if (iRet != 0)
			return iRet;
		if (8 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			slot[0] = (this.g_abyRecvBuff[7] & 0xFF);
		}
		return iRet;
	}
	public int TCD_Read_Test(APDU_RESP ApduResp) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xe0,
				(byte) 9);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		if (iRet != 0)
			return iRet;
		if (10 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			ApduResp.LenOut = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
					+ (this.g_abyRecvBuff[4] & 0xFF) - 2);
			System.arraycopy(this.g_abyRecvBuff, 7, ApduResp.DataOut, 0,
					ApduResp.LenOut);
		}
		return iRet;
	}

	public int Delete_All_Test() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xe0,
				(byte) 11);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 20000);
		if (iRet != 0)
			return -2;
		if (12 != this.g_abyRecvBuff[2])
			return -3;
		if (-32 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		return iRet;
	}

	public int getpan(byte mode, byte[] pan, short[] len, byte[] type) {
		this.g_abySendBuff[0] = mode;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xf2,
				(byte) 1);
		Log.e("result!:", "-----" + iRet + "!!!");
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		if (iRet != 0) {
			return iRet;
		}

		if (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6] == 0) {
			if (this.g_abyRecvBuff[7] < 0x10) {
				len[0] = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
						+ (this.g_abyRecvBuff[4] & 0xFF) - 3);
				System.arraycopy(this.g_abyRecvBuff, 8, pan, 0, len[0]);
				System.arraycopy(this.g_abyRecvBuff, 7, type, 0, 1);
			} else {
				len[0] = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
						+ (this.g_abyRecvBuff[4] & 0xFF) - 2);
				System.arraycopy(this.g_abyRecvBuff, 7, pan, 0, len[0]);

			}
			return 0;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Kb_Flush() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xb8,
				(byte) 1);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (8 != this.g_wRecvLen[0])
			return -3;
		if (-72 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Kb_Check() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xb8,
				(byte) 3);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (8 != this.g_wRecvLen[0])
			return -3;
		if (-72 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Kb_Value() {
		int iRet = SendPacket_key(this.g_abySendBuff, 0, (byte) 0xb8,
				(byte) 5);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 1000);
		if (iRet != 0)
			return -2;
		if (8 != this.g_wRecvLen[0])
			return -3;
		if (-72 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		return iRet;
	}

	public int Mcr_Open() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xc1,
				(byte) 1);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			Log.e("BluetoothCom", "RecvPacket iRet = " + iRet);
			return -2;
		}
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-63 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Mcr_Close() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xc1,
				(byte) 3);

		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-63 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Mcr_Reset() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xc1,
				(byte) 9);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return -2;
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-63 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Mcr_Check() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xc1,
				(byte) 5);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		if (iRet != 0)
			return -2;
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-63 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Mcr_Read(byte keyNo, byte mode, byte[] track1, byte[] track2,
			byte[] track3) {
		this.g_abySendBuff[0] = keyNo;
		this.g_abySendBuff[1] = mode;

		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xc1,
				(byte) 7);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0)
			return -2;
		if (-63 != this.g_abyRecvBuff[1]) {
			return -4;
		}

		int iOffset = 0;
		if (this.g_abyRecvBuff[8] != 0) {
			System.arraycopy(this.g_abyRecvBuff, 11, track1, 0,
					this.g_abyRecvBuff[8]);
			track1[this.g_abyRecvBuff[8]] = 0;
			iOffset += this.g_abyRecvBuff[8];
		} else {
			track1[0] = 0;
		}

		if (this.g_abyRecvBuff[9] != 0) {
			System.arraycopy(this.g_abyRecvBuff, 11 + iOffset, track2, 0,
					this.g_abyRecvBuff[9]);
			iOffset += this.g_abyRecvBuff[9];
			track2[this.g_abyRecvBuff[9]] = 0;
		} else {
			track2[0] = 0;
		}

		if (this.g_abyRecvBuff[10] != 0) {
			System.arraycopy(this.g_abyRecvBuff, 11 + iOffset, track3, 0,
					this.g_abyRecvBuff[10]);
			iOffset += this.g_abyRecvBuff[10];
			track3[this.g_abyRecvBuff[10]] = 0;
		} else {
			track3[0] = 0;
		}
		return this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6];
	}

	public int Msr_get_pan(byte[] track2, byte[] pan, byte[] len) {
		int i = 0;

		while ((track2[i] != 0) && (i < 40)) {
			if (track2[i] == 61) {
				len[0] = (byte) i;
				System.arraycopy(track2, 0, pan, 0, len[0]);
				pan[i] = 0;
				return 0;
			}
			i++;
		}
		pan[0] = 0;
		return -1;
	}

	public int Icc_Detect(byte slot) {
		this.g_abySendBuff[0] = slot;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xb7,
				(byte) 7);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0)
			return iRet;
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-73 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Icc_Close(byte slot) {
		this.g_abySendBuff[0] = slot;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xb7,
				(byte) 3);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return iRet;
		if (9 != this.g_wRecvLen[0])
			return -3;
		if (-73 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Icc_Open(byte slot, byte VCC_Mode, byte[] ATR) {
		this.g_abySendBuff[0] = slot;
		this.g_abySendBuff[1] = VCC_Mode;
		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xb7,
				(byte) 1);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return iRet;
		if (-73 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			System.arraycopy(this.g_abyRecvBuff, 7, ATR, 0, 40);
		}
		return iRet;
	}

	public int Icc_Command(byte slot, APDU_SEND ApduSend, APDU_RESP ApduResp) {
		this.g_abySendBuff[0] = slot;
		System.arraycopy(ApduSend.Command, 0, this.g_abySendBuff, 1, 4);
		this.g_abySendBuff[5] = (byte) (ApduSend.Lc / 256);
		this.g_abySendBuff[6] = (byte) (ApduSend.Lc % 256);
		System.arraycopy(ApduSend.DataIn, 0, this.g_abySendBuff, 7, ApduSend.Lc);
		this.g_abySendBuff[(7 + ApduSend.Lc)] = (byte) (ApduSend.Le / 256);
		this.g_abySendBuff[(8 + ApduSend.Lc)] = (byte) (ApduSend.Le % 256);
		int iRet = SendPacket(this.g_abySendBuff, ApduSend.Lc + 9,
				(byte) 0xb7, (byte) 5);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);

		if (iRet != 0)
			return iRet;
		if (-73 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = (this.g_abyRecvBuff[5] & 0xFF) * 256
				+ (this.g_abyRecvBuff[6] & 0xFF);
		if (iRet == 0) {
			ApduResp.LenOut = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
					+ (this.g_abyRecvBuff[4] & 0xFF) - 4);
			System.arraycopy(this.g_abyRecvBuff, 7, ApduResp.DataOut, 0,
					ApduResp.LenOut);
			ApduResp.SWA = this.g_abyRecvBuff[(ApduResp.LenOut + 7)];
			ApduResp.SWB = this.g_abyRecvBuff[(ApduResp.LenOut + 8)];
		}
		return iRet;
	}

	public int Icc_ReaderManage(int ReaderHandle, byte[] command, int cmdLen,
			byte[] response, int[] resLen) {
		this.g_abySendBuff[0] = 1;
		this.g_abySendBuff[1] = (byte) (cmdLen / 256);
		this.g_abySendBuff[2] = (byte) (cmdLen % 256);
		System.arraycopy(command, 0, this.g_abySendBuff, 3, cmdLen);

		int iRet = SendPacket(this.g_abySendBuff, cmdLen + 3,
				(byte) 0xb7, (byte) 9);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);

		if (iRet != 0)
			return iRet;
		if (-73 != this.g_abyRecvBuff[1]) {
			return -4;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			Log.i("Icc_ReaderManage",
					"g_abyRecvBuff = "
							+ ByteTool.bytearrayToHexString(this.g_abyRecvBuff,
									this.g_wRecvLen[0]));
		}
		return iRet;
	}

	public int Handshake() {
		int i = 0;
		byte[] T_RAND = new byte[8];
		byte[] T_RAND_E = new byte[8];
		byte[] D_RAND = new byte[8];
		byte[] D_RAND_E = new byte[8];
		byte[] DataOut = new byte[8];
		byte[] HSK = StringUtil
				.hexStringToBytes("22551111111111112222227722aa00223333333333334499");
		Random r = new Random();
		for (i = 0; i < 8; i++) {
			T_RAND[i] = (byte) ((byte) r.nextInt(10000) % 256);
			this.g_abySendBuff[i] = T_RAND[i];
		}

		int iRet = SendPacket(this.g_abySendBuff, 8, (byte) 0xb1,
				(byte) 0);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return iRet;
		if (39 != this.g_wRecvLen[0]) {
			return -3;
		}
		System.arraycopy(this.g_abyRecvBuff, 5, T_RAND_E, 0, 8);
		System.arraycopy(this.g_abyRecvBuff, 13, D_RAND, 0, 8);
		Log.e("T_RAND_E:", ByteTool.byte2hex(T_RAND_E));
		NativeFunction.Lib_Des24(T_RAND_E, DataOut, HSK, (byte) 0);
		Log.e("DataOut:", ByteTool.byte2hex(DataOut));
		if (ByteTool.bytearrayToHexString(T_RAND, 8).equals(
				ByteTool.bytearrayToHexString(DataOut, 8))) {
			this.g_abySendBuff[0] = 0;
			NativeFunction.Lib_Des24(D_RAND, D_RAND_E, HSK, (byte) 1);

			System.arraycopy(D_RAND_E, 0, this.g_abySendBuff, 1, 8);
		} else {
			this.g_abySendBuff[0] = 1;
			D_RAND = new byte[8];
			System.arraycopy(D_RAND, 0, this.g_abySendBuff, 1, 8);
		}
		iRet = SendPacket(this.g_abySendBuff, 9, (byte) 0xb1, (byte) 2);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return iRet;
		if (8 != this.g_wRecvLen[0]) {
			return -3;
		}
		return this.g_abyRecvBuff[5];
	}

	public int Pci_Authority(byte authkey_type, byte authkey_no,
			byte authkey_len, byte[] authkey_data) {
		byte[] DevRand = new byte[8];
		byte[] DevRand_E = new byte[8];

		this.g_abySendBuff[0] = authkey_type;
		this.g_abySendBuff[1] = authkey_no;
		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xb5,
				(byte) 0);
		if (iRet != 0) {
			return -1;
		}

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return iRet;
		if (15 != this.g_wRecvLen[0]) {
			return -3;
		}
		System.arraycopy(this.g_abyRecvBuff, 5, DevRand, 0, 8);

		NativeFunction.Lib_Des24(DevRand, DevRand_E, authkey_data, (byte) 1);
		System.arraycopy(DevRand_E, 0, this.g_abySendBuff, 0, 8);

		iRet = SendPacket(this.g_abySendBuff, 8, (byte) 0xb5, (byte) 2);
		if (iRet != 0) {
			return -1;
		}

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0)
			return iRet;
		if (8 != this.g_wRecvLen[0]) {
			return -3;
		}
		return this.g_abyRecvBuff[5];
	}

	private void pt_Mac(byte[] inbuf, byte[] outbuf, int len) {
		byte[] buf = new byte[8];
		byte[] bpkflag = ByteTool
				.stringToBytes("333333333333333333333333333333333333333333333333");
		// byte[] g_bySKMACK = { 41, 35, -66, -124, -31, 108, -42, -82, 82,
		// -112,
		// 73, -15, -15, -69, -23, -21, 10, -17, 33, 125, -116, 25, -75,
		// 68 };

		for (int i = 0; i < len; i++) {
			int tmp171_170 = (i % 8);
			byte[] tmp171_164 = buf;
			tmp171_164[tmp171_170] = (byte) (tmp171_164[tmp171_170] ^ inbuf[i]);
		}

		NativeFunction.Lib_Des24(buf, outbuf, bpkflag, (byte) 1);
	}

	public int Pci_WritePinMKey(byte key_no, byte key_len, byte[] key_data) {
		this.PTK = new byte[24];

		this.g_abySendBuff[0] = 0x0a;//说明是主密钥
		this.g_abySendBuff[1] = 0;
		this.g_abySendBuff[2] = key_no;
		this.g_abySendBuff[3] = key_len;
		for (int i = 0; i < key_len; i += 8) {
			System.arraycopy(key_data, i, this.keyTemp, 0, 8);
			NativeFunction.Lib_Des24(this.keyTemp, this.outTemp, this.PTK,
					(byte) 0);
			System.arraycopy(this.outTemp, 0, this.g_abySendBuff, 4 + i, 8);
		}

		for (int i = 0; i < 32; i++) {
			this.g_abySendBuff[(4 + key_len + i)] = 0;
		}
		pt_Mac(this.g_abySendBuff, this.macOut, 36 + key_len);
		System.arraycopy(this.macOut, 0, this.g_abySendBuff, key_len + 36, 4);

		int iRet = SendPacket(this.g_abySendBuff, 40 + key_len,
				(byte)0xb3, (byte) 0x22);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_WriteMacMKey(byte key_no, byte key_len, byte[] key_data) {
		this.PTK = new byte[24];

		this.g_abySendBuff[0] = 11;
		this.g_abySendBuff[1] = 0;
		this.g_abySendBuff[2] = key_no;
		this.g_abySendBuff[3] = key_len;
		for (int i = 0; i < key_len; i += 8) {
			System.arraycopy(key_data, i, this.keyTemp, 0, 8);
			NativeFunction.Lib_Des24(this.keyTemp, this.outTemp, this.PTK,
					(byte) 0);
			System.arraycopy(this.outTemp, 0, this.g_abySendBuff, 4 + i, 8);
		}

		for (int i = 0; i < 32; i++) {
			this.g_abySendBuff[(4 + key_len + i)] = 0;
		}
		pt_Mac(this.g_abySendBuff, this.macOut, 36 + key_len);
		System.arraycopy(this.macOut, 0, this.g_abySendBuff, key_len + 36, 4);

		int iRet = SendPacket(this.g_abySendBuff, 40 + key_len,
				(byte) -77, (byte) 34);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_WriteDesMKey(byte key_no, byte key_len, byte[] key_data) {
		this.PTK = new byte[24];

		this.g_abySendBuff[0] = 12;
		this.g_abySendBuff[1] = 0;
		this.g_abySendBuff[2] = key_no;
		this.g_abySendBuff[3] = key_len;
		for (int i = 0; i < key_len; i += 8) {
			System.arraycopy(key_data, i, this.keyTemp, 0, 8);
			NativeFunction.Lib_Des24(this.keyTemp, this.outTemp, this.PTK,
					(byte) 0);
			System.arraycopy(this.outTemp, 0, this.g_abySendBuff, 4 + i, 8);
		}

		for (int i = 0; i < 32; i++) {
			this.g_abySendBuff[(4 + key_len + i)] = 0;
		}
		pt_Mac(this.g_abySendBuff, this.macOut, 36 + key_len);
		System.arraycopy(this.macOut, 0, this.g_abySendBuff, key_len + 36, 4);

		int iRet = SendPacket(this.g_abySendBuff, 40 + key_len,
				(byte) -77, (byte) 34);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_WritePinKey(byte key_no, byte key_len, byte[] key_data,
			byte mode, byte mkey_no,byte[] checkData,int[] len) {
		this.g_abySendBuff[0] = 0x0d;
		this.g_abySendBuff[1] = mkey_no;
		this.g_abySendBuff[2] = key_no;
		this.g_abySendBuff[3] = key_len;
		this.g_abySendBuff[4] = mode;
		System.arraycopy(key_data, 0, this.g_abySendBuff, 5, key_len);

		for (int i = 0; i < 31; i++) {
			this.g_abySendBuff[(5 + key_len + i)] = 0;
		}
		NativeFunction.sk_Mac(this.g_abySendBuff, this.macOut, 36 + key_len);
		System.arraycopy(this.macOut, 0, this.g_abySendBuff, key_len + 36, 4);
		int iRet = SendPacket(this.g_abySendBuff, 40 + key_len,
				(byte) 0xb3, (byte) 0x24);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if(iRet==0){
			len[0] = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256 + (this.g_abyRecvBuff[4] & 0xFF) - 2);
			len[0] = len[0] < 0 ? 256 + len[0] : len[0];
			System.arraycopy(this.g_abyRecvBuff, 7, checkData, 0, len[0]);
		}
		return iRet;
	}

	public int Pci_WriteMacKey(byte key_no, byte key_len, byte[] key_data,
			byte mode, byte mkey_no,byte[] checkData) {
		this.g_abySendBuff[0] = 14;
		this.g_abySendBuff[1] = mkey_no;
		this.g_abySendBuff[2] = key_no;
		this.g_abySendBuff[3] = key_len;
		this.g_abySendBuff[4] = mode;
		System.arraycopy(key_data, 0, this.g_abySendBuff, 5, key_len);

		for (int i = 0; i < 31; i++) {
			this.g_abySendBuff[(5 + key_len + i)] = 0;
		}
		NativeFunction.sk_Mac(this.g_abySendBuff, this.macOut, 36 + key_len);
		System.arraycopy(this.macOut, 0, this.g_abySendBuff, key_len + 36, 4);
		int iRet = SendPacket(this.g_abySendBuff, 40 + key_len,
				(byte) -77, (byte) 36);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_WriteDesKey(byte key_no, byte key_len, byte[] key_data,
			byte mode, byte mkey_no) {
		this.g_abySendBuff[0] = 15;
		this.g_abySendBuff[1] = mkey_no;
		this.g_abySendBuff[2] = key_no;
		this.g_abySendBuff[3] = key_len;
		this.g_abySendBuff[4] = mode;
		System.arraycopy(key_data, 0, this.g_abySendBuff, 5, key_len);

		for (int i = 0; i < 31; i++) {
			this.g_abySendBuff[(5 + key_len + i)] = 0;
		}
		NativeFunction.sk_Mac(this.g_abySendBuff, this.macOut, 36 + key_len);
		System.arraycopy(this.macOut, 0, this.g_abySendBuff, key_len + 36, 4);
		int iRet = SendPacket(this.g_abySendBuff, 40 + key_len,
				(byte) -77, (byte) 36);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_GetPin_Single(byte minLen, byte maxLen, byte waitTimeSec,
			byte[] key_data, byte[] pinBlock1) {
		this.g_abySendBuff[0] = minLen;
		this.g_abySendBuff[1] = maxLen;
		this.g_abySendBuff[2] = waitTimeSec;
		System.arraycopy(key_data, 0, this.g_abySendBuff, 3, 24);
		int iRet = SendPacket(this.g_abySendBuff, 27, (byte) 0xb6,
				(byte) 12);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 120000);
		if (iRet != 0) {
			return iRet;
		}

		if (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6] == 0) {
			System.arraycopy(this.g_abyRecvBuff, 7, pinBlock1, 0, 8);
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_GetPin(byte keyNo, byte minLen, byte maxLen, byte mode,
			byte waitTimeSec, byte[] cardNo, byte mark, byte[] iAmount) {
		int card_no_len = cardNo.length;

		this.g_abySendBuff[0] = keyNo;
		this.g_abySendBuff[1] = minLen;
		this.g_abySendBuff[2] = maxLen;
		this.g_abySendBuff[3] = waitTimeSec;
		this.g_abySendBuff[4] = 0;
		this.g_abySendBuff[5] = mode;
		System.arraycopy(cardNo, 0, this.g_abySendBuff, 6, card_no_len);

		this.g_abySendBuff[(card_no_len + 6)] = 0;
		this.g_abySendBuff[(card_no_len + 7)] = mark;
		System.arraycopy(iAmount, 0, this.g_abySendBuff, card_no_len + 6 + 2,
				14);

		int iRet = SendPacket(this.g_abySendBuff, card_no_len + 22,
				(byte) 0xb6, (byte) 1);
		if (iRet != 0) {
			return iRet;
		}

		return iRet;
	}

	public int readPIN(byte[] pinBlock) {

		int iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen,
				1500);
		if (iRet != 0) {
			return iRet;
		}
		iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
		if (iRet == 0) {
			System.arraycopy(this.g_abyRecvBuff, 7, pinBlock, 0, 8);
		}
		return iRet;
	}

	public int Pci_GetMac(byte keyNo, byte mode, byte[] inData, byte[] macOut) {
		this.g_abySendBuff[0] = keyNo;
		this.g_abySendBuff[1] = mode;

		System.arraycopy(inData, 0, this.g_abySendBuff, 2, inData.length);

		int iRet = SendPacket(this.g_abySendBuff, inData.length + 2,
				(byte) 0xb6, (byte) 3);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		if (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6] == 0) {
			System.arraycopy(this.g_abyRecvBuff, 7, macOut, 0, 8);
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Pci_GetDes(byte keyNo, byte mode, byte[] inData, byte[] outData) {
		this.g_abySendBuff[0] = keyNo;
		this.g_abySendBuff[1] = mode;

		System.arraycopy(inData, 0, this.g_abySendBuff, 2, inData.length);

		int iRet = SendPacket(this.g_abySendBuff, inData.length + 2,
				(byte) 0xb6, (byte) 6);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return iRet;
		}

		if (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6] == 0) {
			System.arraycopy(this.g_abyRecvBuff, 7, outData, 0, 8);
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Lib_PciGetRnd(byte[] rnd) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xb5,
				(byte) 0);
		if (iRet != 0) {
			return iRet;
		}

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return iRet;
		}
		System.arraycopy(this.g_abyRecvBuff, 5, rnd, 0, 8);
		return 0;
	}

	public int Picc_Open() {
		byte[] Recv_clear_Buff = new byte[2176];
		int[] Recv_clear_Len = new int[1];
		int iRet = RecvPacket(Recv_clear_Buff, Recv_clear_Len, 200);

		iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xba, (byte) 1);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {

			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}
		return iRet;

	}

	public int Picc_Close() {

		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xba, (byte) 3);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 1000);
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {

			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}
		return iRet;
	}
	public int Picc_Check(byte mode, byte[] CardType, byte[] SerialNo) {
		int iRet;
		g_abySendBuff[0] = mode;
		iRet = SendPacket(g_abySendBuff, 1, (byte) 0xba,
				(byte)0x05);
		if (0 != iRet) {
			return iRet;
		}
		iRet = RecvPacket(g_abyRecvBuff, g_wRecvLen, 3000);
		if (0 != iRet) {
			return iRet;
		} else {
			if (g_abyRecvBuff[5] * 256 + g_abyRecvBuff[6] == 0) {
				CardType[0] = g_abyRecvBuff[7];
				CardType[1] = g_abyRecvBuff[8];
				// memcpy(SerialNo, &g_abyRecvBuff[8], 11);
				System.arraycopy(g_abyRecvBuff, 9, SerialNo, 0, 10);
				return 0;
			} else {
				return (0 - (g_abyRecvBuff[5] * 256 + g_abyRecvBuff[6]));
			}
		}
	}
	public int Picc_Check(byte mode) {
		this.g_abySendBuff[0] = mode;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xba,
				(byte) 5);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return iRet;
		}
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {

			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}
		return iRet;
	}

	public int Picc_Command(APDU_SEND ApduSend, APDU_RESP ApduResp) {
		System.arraycopy(ApduSend.Command, 0, this.g_abySendBuff, 0, 4);
		this.g_abySendBuff[4] = (byte) (ApduSend.Lc / 256);
		this.g_abySendBuff[5] = (byte) (ApduSend.Lc % 256);

		System.arraycopy(ApduSend.DataIn, 0, this.g_abySendBuff, 6, ApduSend.Lc);
		this.g_abySendBuff[(ApduSend.Lc + 6)] = (byte) (ApduSend.Le / 256);
		this.g_abySendBuff[(ApduSend.Lc + 7)] = (byte) (ApduSend.Le % 256);
		int iRet = SendPacket(this.g_abySendBuff, ApduSend.Lc + 8,
				(byte) 0xba, (byte) 7);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);

		if (iRet != 0) {
			return iRet;
		}
		if (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6] == 0) {
			ApduResp.LenOut = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256
					+ (this.g_abyRecvBuff[4] & 0xFF) - 4);

			System.arraycopy(this.g_abyRecvBuff, 7, ApduResp.DataOut, 0,
					ApduResp.LenOut);
			ApduResp.SWA = this.g_abyRecvBuff[(ApduResp.LenOut + 8)];
			ApduResp.SWB = this.g_abyRecvBuff[(ApduResp.LenOut + 7)];
			return 0;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Picc_Remove() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xba,
				(byte) 9);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Picc_Halt() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xba,
				(byte) 11);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Picc_Reset() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xba,
				(byte) 13);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		return this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6];
	}

	public int Picc_M1Authority(byte Type, byte BlkNo, byte[] Pwd,
			byte[] SerialNo) {
		this.g_abySendBuff[0] = Type;
		this.g_abySendBuff[1] = BlkNo;

		System.arraycopy(Pwd, 0, this.g_abySendBuff, 2, 6);

		System.arraycopy(SerialNo, 0, this.g_abySendBuff, 8, 4);

		int iRet = SendPacket(this.g_abySendBuff, 12, (byte) 0xba,
				(byte) 17);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Picc_M1ReadBlock(byte BlkNo, byte[] BlkValue) {
		this.g_abySendBuff[0] = BlkNo;

		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xba,
				(byte) 19);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		if (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6] == 0) {
			System.arraycopy(this.g_abyRecvBuff, 6, BlkValue, 0, 16);
			return 0;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Picc_M1WriteBlock(byte BlkNo, byte[] BlkValue) {
		this.g_abySendBuff[0] = BlkNo;

		System.arraycopy(BlkValue, 0, this.g_abySendBuff, 1, 16);

		int iRet = SendPacket(this.g_abySendBuff, 17, (byte) 0xba,
				(byte) 21);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int Picc_M1Operate(byte Type, byte BlkNo, byte[] Value,
			byte UpdateBlkNo) {
		this.g_abySendBuff[0] = Type;
		this.g_abySendBuff[1] = BlkNo;

		System.arraycopy(Value, 0, this.g_abySendBuff, 2, 4);
		this.g_abySendBuff[6] = UpdateBlkNo;

		int iRet = SendPacket(this.g_abySendBuff, 7, (byte) 0xba,
				(byte) 23);
		if (iRet != 0) {
			return iRet;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return iRet;
		}
		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}




	public int EmvLib_Init() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 1);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;

		return iRet;
	}

	public int EmvLib_GetVer() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 3);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_ClearTransLog() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 5);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_AddBlackList(byte[] cardNum, byte cardSerial) {
		System.arraycopy(cardNum, 0, g_abySendBuff, 0, 10);
		g_abySendBuff[10] = cardSerial;
		int iRet = SendPacket(this.g_abySendBuff, 11, (byte) 0xf3,
				(byte) 0x27);
		if (iRet != 0) {
			return -1001;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return -1002;
		}
		iRet = this.g_abyRecvBuff[5]& 0xFF;
		return iRet;
	}

	public int EmvLib_DelBlackList(byte[] cardNum, byte cardSerial) {
		System.arraycopy(cardNum, 0, g_abySendBuff, 0, 10);
		g_abySendBuff[10] = cardSerial;
		int iRet = SendPacket(this.g_abySendBuff, 11, (byte) 0xf3,
				(byte) 0x29);
		if (iRet != 0) {
			return -1001;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return -1002;
		}
		iRet = this.g_abyRecvBuff[5]& 0xFF;
		return iRet;
	}
	public int EmvLib_CardAuth() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 7);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_GetPath() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 9);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_ProcCLTrans() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 11);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_qPBOCPreProcess() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 0x07);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}
		return iRet;
	}

	public int EmvLib_TransInit() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 17);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_SaveAllApp() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 19);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_SaveAllCapk() {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 21);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_AddCapk(byte[] data) {
		// this.g_abySendBuff[0] = (byte) (Tag / 256);
		// this.g_abySendBuff[1] = (byte) (Tag % 256);
		this.g_abySendBuff = data;
		int iRet = SendPacket(this.g_abySendBuff,
				this.g_abySendBuff.length, (byte) 0xf3, (byte) 0x23);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] & 0xFF;
		// if (iRet == 0) {
		// OutLen[0] = ((this.g_abyRecvBuff[9] & 0xFF) * 256 +
		// (this.g_abyRecvBuff[10] & 0xFF));
		// System.arraycopy(this.g_abyRecvBuff, 11, DataOut, 0, OutLen[0]);
		// }
		Log.e("capk:", "写入公钥返回值:" + iRet);
		return iRet;
	}

	public int EmvLib_SetTLV(short Tag, byte[] DataIn, int DataLen) {
		this.g_abySendBuff[0] = (byte) (Tag / 256);
		this.g_abySendBuff[1] = (byte) (Tag % 256);
		this.g_abySendBuff[2] = (byte) (DataLen / 256);
		this.g_abySendBuff[3] = (byte) (DataLen % 256);
		System.arraycopy(DataIn, 0, this.g_abySendBuff, 4, DataLen);

		int iRet = SendPacket(this.g_abySendBuff, DataLen + 4,
				(byte) 0xf3, (byte) 25);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5] * 65536 * 256
				+ (this.g_abyRecvBuff[6] & 0xFF) * 65536
				+ (this.g_abyRecvBuff[7] & 0xFF) * 256
				+ (this.g_abyRecvBuff[8] & 0xFF);

		return iRet;
	}

	public int EmvLib_GetScriptResult(byte[] Result, int[] RetLen) {
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
				(byte) 0x13);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		if (iRet == 0) {
			RetLen[0] = ((this.g_abyRecvBuff[3] & 0xFF) * 256 + (this.g_abyRecvBuff[4] & 0xFF));
			System.arraycopy(this.g_abyRecvBuff, 5, Result, 0, RetLen[0]);
		}
		return iRet;
	}

	public int EmvLib_ProcTransComplete(byte ucResult, byte[] RspCode,
			byte[] AuthCode, int AuthCodeLen, byte[] IAuthData,
			int IAuthDataLen, byte[] script, int ScriptLen) {

		this.g_abySendBuff[0] = ucResult;
		this.g_abySendBuff[1] = RspCode[0];
		this.g_abySendBuff[2] = RspCode[1];

		this.g_abySendBuff[3] = (byte) (AuthCodeLen / 256);
		this.g_abySendBuff[4] = (byte) (AuthCodeLen % 256);

		this.g_abySendBuff[5] = (byte) (IAuthDataLen / 256);
		this.g_abySendBuff[6] = (byte) (IAuthDataLen % 256);

		this.g_abySendBuff[7] = (byte) (ScriptLen / 256);
		this.g_abySendBuff[8] = (byte) (ScriptLen % 256);

		System.arraycopy(AuthCode, 0, this.g_abySendBuff, 9, AuthCodeLen);
		System.arraycopy(IAuthData, 0, this.g_abySendBuff, 9 + AuthCodeLen,
				IAuthDataLen);
		System.arraycopy(script, 0, this.g_abySendBuff, 9 + AuthCodeLen
				+ IAuthDataLen, ScriptLen);
		int iRet = SendPacket(this.g_abySendBuff, 9 + AuthCodeLen
				+ IAuthDataLen + ScriptLen, (byte) 0xf3, (byte) 0x05);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		if (iRet != 0) {
			return -2;
		}
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			iRet = (this.g_abyRecvBuff[5] & 0xFF);
		}
		return iRet;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int EmvLib_GetTags(String[] tags, byte[] dataOut, int[] len) {
		int length = tags.length;
		String tmp = "";
		List lbyte = new ArrayList();
		for (int i = 0; i < length; i++) {
			tmp = tags[i];
			byte[] b = ByteTool.hexStr2Bytes(tmp);
			for (byte tmpb : b) {
				lbyte.add(tmpb);
			}
		}
		byte[] tb = new byte[lbyte.size()];
		for (int i = 0; i < tb.length; i++) {

			tb[i] = (Byte) lbyte.get(i);
		}
		int tagslen = lbyte.size();

		System.arraycopy(tb, 0, this.g_abySendBuff, 0, tagslen);

		int iRet = SendPacket(this.g_abySendBuff, tagslen,
				(byte) 0xf3, (byte) 0x15);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			iRet = this.g_abyRecvBuff[5] & 0xFF;
			byte[] len1 = new byte[2];
			len1[0] = this.g_abyRecvBuff[3];
			len1[1] = this.g_abyRecvBuff[4];
			len[0] = ByteTool.byteArray2int(len1) - 1;
			System.arraycopy(this.g_abyRecvBuff, 6, dataOut, 0, len[0]);
		}
		return iRet;
	}

	public int EmvLib_ProcTransBeforeOnline(byte solt, int TransNo,
			byte[] ifGoOnline, byte[] ifNeedPin) {
		this.g_abySendBuff[0] = solt;
		byte[] transNo = ByteTool.intTo4bytes(TransNo);

		Log.e("transNo:", ByteTool.byte2hex(transNo));
		System.arraycopy(transNo, 0, this.g_abySendBuff, 1, 4);
		int iRet = SendPacket(this.g_abySendBuff, 5, (byte) 0xf3,
				(byte) 0x03);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);

		iRet = this.g_abyRecvBuff[5] & 0xFF;
		ifGoOnline[0] = this.g_abyRecvBuff[6];
		ifNeedPin[0] = this.g_abyRecvBuff[7];
		return iRet;

	}

	public int EmvLib_CoreGetAmt(int AuthAmount, int BackAmt) {

		byte[] auth_amount = tranfarm(AuthAmount);
		byte[] back_amount = tranfarm(BackAmt);

		System.arraycopy(auth_amount, 0, this.g_abySendBuff, 0,
				auth_amount.length);
		System.arraycopy(back_amount, 0, this.g_abySendBuff, 8,
				back_amount.length);

		int iRet = SendPacket(this.g_abySendBuff, auth_amount.length
				+ back_amount.length, (byte) 0xf3, (byte) 0x2b);
		if (iRet != 0) {
			return -1;
		}

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}
		return iRet;

	}

	private byte[] tranfarm(int i) {

		byte[] b = String.valueOf(i).getBytes();
		if (b.length < 8) {

			b = (String.valueOf(i) + "\0").getBytes();
		}
		return b;
	}

	public int EmvLib_CreatAppList(byte Slot, byte ReadLogFlag,
			byte[] AidLists, int[] Num) {
		this.g_abySendBuff[0] = Slot;
		this.g_abySendBuff[1] = ReadLogFlag;

		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xf3,
				(byte) 0x30);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);

		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			iRet = this.g_abyRecvBuff[5] & 0xFF;

			System.arraycopy(this.g_abyRecvBuff, 7, AidLists, 0,
					18 * this.g_abyRecvBuff[6]);

			Num[0] = (int) this.g_abyRecvBuff[6] & 0xFF;
		}
		return iRet;

	}

	public int EmvLib_SetSelectApp(int index) {
		this.g_abySendBuff[0] = (byte) index;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xf3,
				(byte) 0x2d);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		return iRet;
	}

	public int EmvLib_SetTime(byte[] time) {
		System.arraycopy(time, 0, this.g_abySendBuff, 0, 6);
		int iRet = SendPacket(this.g_abySendBuff, 6, (byte) 0xd1,
				(byte) 1);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
	}

	public int EmvLib_SetParam(byte[] params) {

		System.arraycopy(params, 0, this.g_abySendBuff, 0, 142);
		int iRet = SendPacket(this.g_abySendBuff, 142, (byte) 0xf3,
				(byte) 0x21);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}

		return 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);

	}

	public int EmvLib_GetParam(byte[] params, int[] len) {
		this.g_abySendBuff[0] = 0x00;
		this.g_abySendBuff[1] = 0x00;
		int iRet = SendPacket(this.g_abySendBuff, 2, (byte) 0xf3,
				(byte) 0x19);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			len[0] = 256 + (this.g_abyRecvBuff[3] * 256 + this.g_abyRecvBuff[4]);

			System.arraycopy(this.g_abyRecvBuff, 5, params, 0, len[0]);

		}

		return iRet;
	}

	public int EmvLib_addApp(byte[] applist) {

		System.arraycopy(applist, 0, this.g_abySendBuff, 0, 252);

		int iRet = SendPacket(this.g_abySendBuff, 252, (byte) 0xf3,
				(byte) 0x1d);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {

			return 0;

		}
		return iRet;
	}

	public int EmvLib_GetApp(int Index, byte[] App) {
		this.g_abySendBuff[0] = (byte) Index;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xf3,
				(byte) 0x17);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 3000);
		if (iRet != 0) {
			return -2;
		}
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
            iRet = this.g_abyRecvBuff[5]&0xff;
			int len = 256 + (this.g_abyRecvBuff[3] * 256 + this.g_abyRecvBuff[4]);

			System.arraycopy(this.g_abyRecvBuff, 6, App, 0, len);

		}

		return iRet;

	}

	public int EmvLib_GetLog(int slot, byte[] tlog, int[] length) {
		this.g_abySendBuff[0] = (byte) slot;
		int iRet = SendPacket(this.g_abySendBuff, 1, (byte) 0xf3,
				(byte) 0x32);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0)
			return -2;
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			length[0] = this.g_abyRecvBuff[3] * 256 + this.g_abyRecvBuff[4];
			length[0] = (((int)length[0] < 0) ? ((int)length[0] + 256) : (int) length[0]);
			iRet = (int) this.g_abyRecvBuff[5];
			System.arraycopy(this.g_abyRecvBuff, 6, tlog, 0, length[0]);

		}
		return iRet;
	}

	public int IDCardFinger(byte[] finger, int[] len) {
		// TODO Auto-generated method stub
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf0,
				(byte) 0x13);
		if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (iRet != 0)
			return -2;
		// int length = this.g_abyRecvBuff[3] * 256 + this.g_abyRecvBuff[4];
		iRet = this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6];
		if (iRet == 0) {
			byte[] lent = new byte[2];
			lent[0] = this.g_abyRecvBuff[7];
			lent[1] = this.g_abyRecvBuff[8];
			len[0] = ByteTool.byteArray2int(lent);
			System.arraycopy(this.g_abyRecvBuff, 8, finger, 0, len[0]);
		}

		return iRet;

	}

	public int EmvLib_ProcCLTransBeforeOnline(byte Slot, int TransNo,
			byte[] bIfGoOnline, byte[] bIfNeedPin) {
		this.g_abySendBuff[0] = Slot;
		byte[] transNo = ByteTool.intTo4bytes(TransNo);

		Log.e("transNo:", ByteTool.byte2hex(transNo));
		System.arraycopy(transNo, 0, this.g_abySendBuff, 1, 4);
		int iRet = SendPacket(this.g_abySendBuff, 5, (byte) 0xf3,
				(byte) 0x09);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {
			iRet = this.g_abyRecvBuff[5] & 0xFF;
			bIfGoOnline[0] = this.g_abyRecvBuff[6];
			bIfNeedPin[0] = this.g_abyRecvBuff[7];
		}
		return iRet;

	}
   public int EmvLib_ProcCLTransComplete(byte Result, byte[] RspCode, byte[] AuthCode, int AuthCodeLen){

	   	this.g_abySendBuff[0] = Result;
		this.g_abySendBuff[1] = RspCode[0];
		this.g_abySendBuff[2] = RspCode[1];

		this.g_abySendBuff[3] = (byte) (AuthCodeLen / 256);
		this.g_abySendBuff[4] = (byte) (AuthCodeLen % 256);

		System.arraycopy(AuthCode, 0, this.g_abySendBuff, 5, AuthCodeLen);
		int iRet = SendPacket(this.g_abySendBuff, 5 + AuthCodeLen, (byte) 0xf3, (byte) 0x0b);
	   if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {

			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}

		return iRet;

   }
   public int EmvLib_GetStatus(){
	   int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xf3,
			   (byte) 0x11);

	   iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xf3) {

			iRet = this.g_abyRecvBuff[5] & 0xFF;
		}

		return iRet;

   }
	public int SysUniqId(byte[] SN){
		int iRet = SendPacket(this.g_abySendBuff, 0, (byte) 0xd1,
				(byte) 0x13);

		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 5000);
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xd1) {

			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			if(iRet==0) {
				int len= (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256 + (this.g_abyRecvBuff[4] & 0xFF) - 2);
				len = len < 0 ? 256 + len : len;
				System.arraycopy(this.g_abyRecvBuff, 7, SN, 0, len);
			}
		}
		return iRet;


	}
   public int chipGetDesKey(byte[] deskey,byte slot,byte[] pk){
	   this.g_abySendBuff[0] = slot;
	   this.g_abySendBuff[1] = 0x01;
	   this.g_abySendBuff[2] = (byte) ((pk.length)/256);
	   this.g_abySendBuff[3] = (byte) ((pk.length)%256);
	   this.g_abySendBuff[4] = 'Y';
	   for(int i=0;i<pk.length;i++){

		   this.g_abySendBuff[5+i]=pk[i];
	   }
	   int iRet = SendPacket(this.g_abySendBuff, 5 + pk.length, (byte) 0xc7,
			   (byte) 0x01);

	   iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 10000);
		Log.e("llll",this.g_wRecvLen[0]+"!!!");
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xc7) {


			iRet = this.g_abyRecvBuff[5] & 0xFF;
			System.arraycopy(this.g_abyRecvBuff, 7, deskey, 0, this.g_wRecvLen[0]-7);
		}
		return iRet;
   }
   public int EmvLib_AddApp(EMV_APPLIST App){
		int index = 0;
		int length;

		length = 33;
		System.arraycopy(App.getAppName(), 0, g_abySendBuff, index, length);
		index += length;

		length = 17;
		System.arraycopy(App.getAID(), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = App.getAidLen();
		g_abySendBuff[index++] = App.getSelFlag();
		g_abySendBuff[index++] = App.getPriority();
		g_abySendBuff[index++] = App.getTargetPer();
		g_abySendBuff[index++] = App.getMaxTargetPer();
		g_abySendBuff[index++] = App.getFloorLimitCheck();
		g_abySendBuff[index++] = App.getRandTransSel();
		g_abySendBuff[index++] = App.getVelocityCheck();

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)App.getFloorLimit()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)App.getThreshold()), 0, g_abySendBuff, index, length);
		index += length;

		length = 6;
		System.arraycopy(App.getTACDenial(), 0, g_abySendBuff, index, length);
		index += length;

		length = 6;
		System.arraycopy(App.getTACOnline(), 0, g_abySendBuff, index, length);
		index += length;

		length = 6;
		System.arraycopy(App.getTACDefault(), 0, g_abySendBuff, index, length);
		index += length;

		length = 7;
		System.arraycopy(App.getAcquierId(), 0, g_abySendBuff, index, length);
		index += length;

		length = 64;
		System.arraycopy(App.getdDOL(), 0, g_abySendBuff, index, length);
		index += length;

		length = 64;
		System.arraycopy(App.gettDOL(), 0, g_abySendBuff, index, length);
		index += length;

		length = 3;
		System.arraycopy(App.getVersion(), 0, g_abySendBuff, index, length);
		index += length;

		length = 10;
		System.arraycopy(App.getRiskManData(), 0, g_abySendBuff, index, length);
		index += length;



		g_abySendBuff[index++] = App.getEC_bTermLimitCheck();

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)App.getEC_TermLimit()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int) App.getCL_bStatusCheck()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)App.getCL_FloorLimit()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)App.getCL_TransLimit()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)App.getCL_CVMLimit()), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = App.getTermQuali_byte2();

		int iRet = SendPacket(this.g_abySendBuff, index, (byte) 0xf3,
				(byte) 0x1d);
	   if (iRet != 0) {
			return -1;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -2;
		}
		iRet = this.g_abyRecvBuff[5]& 0xFF;
		return iRet;
	}
   public int EmvLib_SetParam(EMV_PARAM tParam){
		int index = 0;
		int length;

		length = 64;
		System.arraycopy(tParam.getMerchName(), 0, g_abySendBuff, index, length);
		index += length;

		length = 2;
		System.arraycopy(tParam.getMerchCateCode(), 0, g_abySendBuff, index, length);
		index += length;

		length = 15;
		System.arraycopy(tParam.getMerchId(), 0, g_abySendBuff, index, length);
		index += length;

		length = 8;
		System.arraycopy(tParam.getTermId(), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = tParam.getTerminalType();

		length = 3;
		System.arraycopy(tParam.getCapability(), 0, g_abySendBuff, index, length);
		index += length;

		length = 5;
		System.arraycopy(tParam.getExCapability(), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = tParam.getTransCurrExp();
		g_abySendBuff[index++] = tParam.getReferCurrExp();

		length = 2;
		System.arraycopy(tParam.getReferCurrCode(), 0, g_abySendBuff, index, length);
		index += length;

		length = 2;
		System.arraycopy(tParam.getCountryCode(), 0, g_abySendBuff, index, length);
		index += length;

		length = 2;
		System.arraycopy(tParam.getTransCurrCode(), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)tParam.getReferCurrCon()), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = tParam.getTransType();
		g_abySendBuff[index++] = tParam.getForceOnline();
		g_abySendBuff[index++] = tParam.getbBatchCapture();
		g_abySendBuff[index++] = tParam.getbSupportAdvices();
		g_abySendBuff[index++] = tParam.getPOSEntryMode();
		g_abySendBuff[index++] = tParam.getPosKernelType();
		g_abySendBuff[index++] = tParam.getGetDataPIN();
		g_abySendBuff[index++] = tParam.getSurportPSESel();

		length = 4;
		System.arraycopy(tParam.getTermTransQuali(), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = tParam.getECTSI();
		g_abySendBuff[index++] = tParam.getEC_bTermLimitCheck();

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)tParam.getEC_TermLimit()), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = tParam.getCL_bStatusCheck();

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)tParam.getCL_FloorLimit()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)tParam.getTransLimit()), 0, g_abySendBuff, index, length);
		index += length;

		length = 4;
		System.arraycopy(ByteTool.intTo4bytes((int)tParam.getCL_CVMLimit()), 0, g_abySendBuff, index, length);
		index += length;

		int iRet = SendPacket(this.g_abySendBuff, index, (byte) 0xf3,
				(byte) 0x21);
	   if (iRet != 0) {
			return -1001;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -1002;
		}
		iRet = this.g_abyRecvBuff[5]& 0xFF;
		return iRet;
	}

	public int EmvLib_AddCapk(EMVCAPK capk){
		int index = 0;
		int length;

		length = 5;
		System.arraycopy(capk.getRID(), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = capk.getKeyID();
		g_abySendBuff[index++] = capk.getHashInd();
		g_abySendBuff[index++] = capk.getArithInd();
		g_abySendBuff[index++] = capk.getModulLen();

		length = 248;
		System.arraycopy(capk.getModul(), 0, g_abySendBuff, index, length);
		index += length;

		g_abySendBuff[index++] = capk.getExponentLen();

		length = 3;
		System.arraycopy(capk.getExponent(), 0, g_abySendBuff, index, length);
		index += length;

		length = 3;
		System.arraycopy(capk.getExpDate(), 0, g_abySendBuff, index, length);
		index += length;

		length = 20;
		System.arraycopy(capk.getCheckSum(), 0, g_abySendBuff, index, length);
		index += length;

		int iRet = SendPacket(this.g_abySendBuff, index, (byte) 0xf3,
				(byte) 0x23);
		if (iRet != 0) {
			return -1001;
		}
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
		if (iRet != 0) {
			return -1002;
		}
		iRet = this.g_abyRecvBuff[5]& 0xFF;
		return iRet;
	}
	
	public int inputRSApk(){
		
		return 0;
	}
	
	   /********************************************国钥加密_GJ_2015-11-02 ******************************************************/
	
	
	
		/**
		 * 
		 * @param Channel  卡通道号        01H：SAM1卡座          02H：SAM2卡座
		 * @param Voltage  指定的电压  1—5V     2—3V      3—1.8V
		 * @param KeyType  密钥类型 ： 00 RSA密钥          01 SM2密钥            02 SM4密钥             03 DES密钥
		 * @return
		 */
		
		public int NewKeyContainer(byte Channel, byte Voltage , byte KeyType){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = Channel;
			this.g_abySendBuff[1] = Voltage;
			this.g_abySendBuff[2] = KeyType;
			int iRet = SendPacket(this.g_abySendBuff, 3, (byte) 0xc8,
					(byte) 0x01);
			if (iRet != 0) {
				return -1;
			}
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
//			if (iRet != 0)
//				return -2;
//			if (9 != this.g_wRecvLen[0])
//				return -3;
//			if (-71 != this.g_abyRecvBuff[1]) {
//				return -4;
//			}
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			return iRet;
		}
		
		
		
		/**
		 * 
		 * @param Channel  卡通道号        01H：SAM1卡座          02H：SAM2卡座
		 * @param Voltage  指定的电压  1—5V     2—3V      3—1.8V
		 * @param KeyNumber  密钥编号：0x00-0x09
		 * @return
		 */
		public int NewSM2KeyPair(int Channel, int Voltage , int KeyNumber){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = (byte) Channel;
			this.g_abySendBuff[1] = (byte) Voltage;
			this.g_abySendBuff[2] = (byte) KeyNumber;
			int iRet = SendPacket(this.g_abySendBuff, 3, (byte) 0xc8,
					(byte) 0x03);
			if (iRet != 0) {
				return -1;
			}
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
//			if (iRet != 0)
//				return -2;
//			if (9 != this.g_wRecvLen[0])
//				return -3;
//			if (-71 != this.g_abyRecvBuff[1]) {
//				return -4;
//			}
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			return iRet;
		}
		
		/**
		 * 
		 * @param Channel  卡通道号        01H：SAM1卡座          02H：SAM2卡座
		 * @param Voltage  指定的电压  1—5V     2—3V      3—1.8V
		 * @param KeyNumber  密钥编号：0x00-0x09
		 * @param modo   模式         0: 获取公钥           1：加载私钥              2：加载公钥
		 * @param data    注入的 公钥  ...   mode=0时，Len为0，即无数据；          mode=1时，Len为32，即32字节私钥数据；           mode=2时，Len为64，即64字节公钥数据，其中前32字节为公钥X，后32字节为公钥Y
		 * @param Obtain   获取的密钥  ...   mode=0时，Len为64，前32字节为公钥X，后32字节为公钥Y；        mode=1时，Len为0 ；        mode=2时，Len为0
		 * @return
		 */
		public int UpdateSM2Key(byte Channel, byte Voltage , byte KeyNumber ,int modo , byte[] data ,byte[] Obtain){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = Channel;
			this.g_abySendBuff[1] = Voltage;
			this.g_abySendBuff[2] = KeyNumber;
			this.g_abySendBuff[3] = (byte) modo;
			if(modo == 1){
				System.arraycopy(data, 0, this.g_abySendBuff, 4, 32);
			}else if(modo == 2){
				System.arraycopy(data, 0, this.g_abySendBuff, 4, 64);
			}
			int iRet = SendPacket(this.g_abySendBuff, 4+data.length, (byte) 0xc8,
					(byte) 0x05);
			if (iRet != 0) {
				return -1;
			}
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
			
			
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			
			if(iRet == 0){
				if(modo == 0){
					System.arraycopy(this.g_abyRecvBuff, 7, Obtain, 0, 64);
				}
				
			}
			return iRet;
		}
		
		public int SM2EncryptDecode(byte Channel, byte Voltage , byte KeyNumber ,int modo , byte[] data , List<byte[]> Obtain){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = Channel;
			this.g_abySendBuff[1] = Voltage;
			this.g_abySendBuff[2] = KeyNumber;
			this.g_abySendBuff[3] = (byte) modo;
			System.arraycopy(data, 0, this.g_abySendBuff, 4, data.length);
			
			
			int iRet = SendPacket(this.g_abySendBuff, 4+data.length, (byte) 0xc8,
					(byte) 0x07);
			if (iRet != 0) {
				return -1;
			}
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
			
			
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			
			if(iRet == 0){
				byte[] mbyte = new byte[(this.g_abyRecvBuff[3] * 256 + this.g_abyRecvBuff[4]) -2];
				System.arraycopy(this.g_abyRecvBuff, 7, mbyte, 0, mbyte.length);
				
				Obtain.clear();
				Obtain.add(mbyte);
				
			}
			return iRet;
		}
		
		
		public int SM2UserId(byte Channel, byte Voltage , byte KeyNumber ,int modo , byte[] data){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = Channel;
			this.g_abySendBuff[1] = Voltage;
			this.g_abySendBuff[2] = KeyNumber;
			this.g_abySendBuff[3] = (byte) modo;
			if(modo == 0){
				System.arraycopy(data, 0, this.g_abySendBuff, 5, 32);
			}else if(modo == 1){
				System.arraycopy(data, 0, this.g_abySendBuff, 5, 64);
			}
			int iRet = SendPacket(this.g_abySendBuff, 3, (byte) 0xc8,
					(byte) 0x05);
			if (iRet != 0) {
				return -1;
			}
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
			
			
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			
			if(iRet == 0){
				if(modo == 0){
					byte[] mbyte = new byte[64];
					System.arraycopy(this.g_abyRecvBuff, mbyte.length + 2, mbyte, 0, 64);
				}
				
			}
			return iRet;
		}
		
		/**
		 * 
		 * @param Channel  卡通道号        01H：SAM1卡座          02H：SAM2卡座
		 * @param Voltage  指定的电压  1—5V     2—3V      3—1.8V
		 * @param KeyNumber  密钥编号：0x00-0x09
		 * @param data    SM4密钥
		 * @return
		 */
		public int NewSM4KeyPair(int Channel, int Voltage , int KeyNumber , byte[] data ){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = (byte) Channel;
			this.g_abySendBuff[1] = (byte) Voltage;
			this.g_abySendBuff[2] = (byte) KeyNumber;
			System.arraycopy(data, 0, this.g_abySendBuff, 3, data.length);
			
			int iRet = SendPacket(this.g_abySendBuff, data.length+3, (byte) 0xc8,
					(byte) 0x11);
			
			if (iRet != 0) {
				return -1;
			}
			
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
			
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			
			return iRet;
		}
		
		/**
		 * 
		 * @param Channel  卡通道号        01H：SAM1卡座          02H：SAM2卡座
		 * @param Voltage  指定的电压  1—5V     2—3V      3—1.8V
		 * @param KeyNumber  密钥编号：0x00-0x09
		 * @param type   0:ECB加密或解密      1:CBC加密或解密
		 * @param modo   模式         0: 解密     1:加密
		 * @param data    加密的数据
		 * @param Obtain   获取到的密文
		 * @return
		 */
		public int SM4EncryptDecode(int Channel, int Voltage , int KeyNumber ,int type,int modo , byte[] data , List<byte[]> Obtain){
			this.g_abySendBuff.clone();
			this.g_abySendBuff[0] = (byte) Channel;
			this.g_abySendBuff[1] = (byte) Voltage;
			this.g_abySendBuff[2] = (byte) KeyNumber;
			this.g_abySendBuff[3] = (byte) type;
			this.g_abySendBuff[4] = (byte) modo;
			System.arraycopy(data, 0, this.g_abySendBuff, 5, data.length);
			
			
			int iRet = SendPacket(this.g_abySendBuff, 5+data.length, (byte) 0xc8,
					(byte) 0x13);
			if (iRet != 0) {
				return -1;
			}
			iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, 2000);
			
			
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			
			if(iRet == 0){
				byte[] mbyte = new byte[(this.g_abyRecvBuff[3] * 256 + this.g_abyRecvBuff[4]) -2];
				System.arraycopy(this.g_abyRecvBuff, 7, mbyte, 0, mbyte.length);
				Obtain.clear();
				Obtain.add(mbyte);
				
			}else{
				Obtain.add(null);
			}
			return iRet;
		}

	/**
	 * 获取RSA公钥
	 * @param slot
	 * @param vlot
	 * @param pubKeyIndex
	 * @param puklen
	 * @param pubKeyData
	 * @param len
	 * @return
	 */
	public  int getPubRsaPuk(byte slot,byte vlot,byte pubKeyIndex,short puklen,byte[] pubKeyData,int[] len,int timeOut){
		this.g_abySendBuff[0] =  slot;
		this.g_abySendBuff[1] =  vlot;
		this.g_abySendBuff[2] =  pubKeyIndex;
		byte[] blen = new byte[2];
		blen  = ByteTool.short2byte(puklen);
		this.g_abySendBuff[3] =  blen[0];
		this.g_abySendBuff[4] =  blen[1];
		int iRet = SendPacket(this.g_abySendBuff, 5, (byte) 0xb6,
				(byte) 0x0e);
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, timeOut);
		if (iRet != 0) {
			return -2;
		}
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xb6) {
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			if(iRet==0) {
				len[0] = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256 + (this.g_abyRecvBuff[4] & 0xFF) - 2);
				len[0] = len[0] < 0 ? 256 + len[0] : len[0];
				System.arraycopy(this.g_abyRecvBuff, 7, pubKeyData, 0, len[0]);
			}
		}
		return iRet;
	}

	/**
	 * RSA解密
	 * @param slot
	 * @param vlot
	 * @param pubIndex
	 * @param mainKeyIndex
	 * @param KeyData
	 * @param checkData
	 * @param len
	 * @return
	 */
	public  int RsaDecipher(byte slot,byte vlot,byte pubIndex,byte mainKeyIndex,byte[] KeyData,byte[] checkData,int[] len,int timeOut){
		this.g_abySendBuff[0] =  slot;
		this.g_abySendBuff[1] =  vlot;
		this.g_abySendBuff[2] =  pubIndex;
		this.g_abySendBuff[3] =  mainKeyIndex;
		System.arraycopy(KeyData, 0, this.g_abySendBuff,4, KeyData.length);
		int iRet = SendPacket(this.g_abySendBuff, 4+KeyData.length, (byte) 0xb6,
				(byte) 0x10);
		iRet = RecvPacket(this.g_abyRecvBuff, this.g_wRecvLen, timeOut);
		if (iRet != 0) {
			return -2;
		}
		iRet = -1;
		if (this.g_abyRecvBuff[0] == (byte) 0x02
				&& this.g_abyRecvBuff[1] == (byte) 0xb6) {
			iRet = 0 - (this.g_abyRecvBuff[5] * 256 + this.g_abyRecvBuff[6]);
			if(iRet==0) {
				len[0] = (short) ((this.g_abyRecvBuff[3] & 0xFF) * 256 + (this.g_abyRecvBuff[4] & 0xFF) - 2);
				len[0] = len[0] < 0 ? 256 + len[0] : len[0];
				System.arraycopy(this.g_abyRecvBuff, 7, checkData, 0, len[0]);
			}
		}
		return iRet;
	}



   
}