package com.siecom.framework.plugin.printer;

import java.util.Iterator;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.printer.bluetooth.android.BluetoothPrinter;
import com.printer.bluetooth.android.PrinterType;


public class Printer {
	
	private BluetoothPrinter mPrinter;
	private BluetoothAdapter mBluetoothAdapter;
	private int connectCount = 0;


	public boolean stopdevice() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mPrinter.closeConnection();
		return true;
	}

	public boolean openBlue() {
		String BluetoothPrinterDevice = "T7 BT Printer";

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (Iterator<BluetoothDevice> iterator = pairedDevices.iterator(); iterator
					.hasNext();) {
				BluetoothDevice device = (BluetoothDevice) iterator.next();
				;
				if (BluetoothPrinterDevice.equals(device.getName())) {
					mBluetoothAdapter.enable();
					mPrinter = new BluetoothPrinter(device);
					mPrinter.openConnection();
					if (!mPrinter.isPrinterNull()) {
						while (!mPrinter.isConnected() && connectCount < 5) {
							connectCount++;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						return false;
					}
				}
			}
		}
		return mPrinter.isConnected();
	}


	public int check() {
		if (!mPrinter.isPrinterNull()) {
			if (mPrinter.isConnected()) {
				return 0;
			}
		}
		return -1;
	}

	public int bluePrint(String content) {
		mPrinter.setCurrentPrintType(PrinterType.T9);
		mPrinter.setPrinter(BluetoothPrinter.COMM_ALIGN,
				BluetoothPrinter.COMM_ALIGN_CENTER);
		mPrinter.setCharacterMultiple(1, 1);
		mPrinter.setPrinter(BluetoothPrinter.COMM_ALIGN,
				BluetoothPrinter.COMM_ALIGN_LEFT);
		mPrinter.setCharacterMultiple(0, 0);
		mPrinter.setPrinter(BluetoothPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE,
				1);
		mPrinter.printText(content + "\n\n\n\n");
		return 0;
	}


}
