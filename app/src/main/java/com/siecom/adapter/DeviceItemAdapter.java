package com.siecom.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.siecom.activities.DeviceActivity;
import com.siecom.activities.R;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DeviceItemAdapter extends RecyclerView.Adapter<DeviceItemAdapter.ViewHolder> {
    private  static  final String A108prefix = "P3520";
    private List<BluetoothDevice> mDataset;
    private Activity activity;

    // empty list, then use append method to add list elements
    public DeviceItemAdapter() {

        mDataset = new ArrayList<>();
    }

    public DeviceItemAdapter(Activity activity, List<BluetoothDevice> dataset) {
        super();
        this.activity = activity;
        mDataset = dataset; // reference
    }

    public void RefreshDataset(List<BluetoothDevice> dataset) {

        mDataset = dataset; // reference
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(viewGroup.getContext(), R.layout.view_device_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        // set text
        if (viewHolder.tvDeviceName != null) {
            if(mDataset.get(i).getBondState()== BluetoothDevice.BOND_BONDED){

                viewHolder.tvDeviceName.setTextColor(ContextCompat.getColor(activity, R.color.forest_green));
            }
            viewHolder.tvDeviceName.setText(mDataset.get(i).getName());

        }
        if(mDataset.get(i)!=null) {
            try {
                if (!mDataset.get(i).getName().contains(A108prefix)) {

                    viewHolder.ivDeviceIcon.setImageResource(R.mipmap.bt);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            if (viewHolder.tvDeviceMac != null)
                viewHolder.tvDeviceMac.setText(mDataset.get(i).getAddress());

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    /**
     * View Holder:
     * Called by RecyclerView to display the data at the specified position.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public int position;
        public boolean isLoading = false;

        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceMac;
        View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView =itemView;
            itemView.findViewById(R.id.item_card).setOnClickListener(this);
            itemView.findViewById(R.id.item_card).setOnLongClickListener(this);

            // get all views


            ivDeviceIcon = (ImageView) itemView.findViewById(R.id.device_icon);

            tvDeviceName = (TextView) itemView.findViewById(R.id.device_name);
            tvDeviceMac = (TextView) itemView.findViewById(R.id.device_mac);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_card:
                    Log.e("click", mDataset.get(getAdapterPosition()).getName());
                    BluetoothDevice btDev = mDataset.get(getAdapterPosition());

                    if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
                        Log.e("BlueToothTest", "start pair");
                        Boolean isBonded = false;
                        try {
                            isBonded = createBond(btDev);
                            if (isBonded) {
                                Log.e("Log", "Paired");
                                tvDeviceName.setTextColor(ContextCompat.getColor(activity, R.color.forest_green));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {

                        Jump();


                    }
                    break;

            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.item_card:
                    Log.e("onLongClick", mDataset.get(getAdapterPosition()).getName());
                    new MaterialDialog.Builder(activity)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);

                                    unpairDevice(mDataset.get(getAdapterPosition()));
                                    tvDeviceName.setTextColor(ContextCompat.getColor(activity, R.color.device_item_title));
                                }
                            })
                            .theme(Theme.LIGHT)
                            .backgroundColorRes(R.color.dlgBackgroundColor)
                            .contentColorRes(R.color.dlgContentColor)
                            .positiveColorRes(R.color.dlgPositiveButtonColor)
                            .negativeColorRes(R.color.dlgNegativeButtonColor)
                            .content(R.string.unpair)
                            .contentGravity(GravityEnum.CENTER)
                            .positiveText(R.string.sure)
                            .negativeText(R.string.no)
                            .show();
                    break;

            }
            return true;
        }

        public void Jump(){

            Intent intent = new Intent(activity, DeviceActivity.class);
            intent.putExtra("Device", mDataset.get(getAdapterPosition()));


            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    Pair.create(itemView.findViewById(R.id.device_icon), "device_icon"),
                    Pair.create(itemView.findViewById(R.id.device_name), "device_name"));
            ActivityCompat.startActivity(activity, intent, options.toBundle());

        }

        public boolean createBond(BluetoothDevice btDevice)
                throws Exception {
            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
            return returnValue.booleanValue();
        }

        private void unpairDevice(BluetoothDevice device) {
            try {
                Method m = device.getClass()
                        .getMethod("removeBond", (Class[]) null);
                m.invoke(device, (Object[]) null);
            } catch (Exception e) {
                Log.e("unpairDevice", e.getMessage());
            }
        }


    }


}