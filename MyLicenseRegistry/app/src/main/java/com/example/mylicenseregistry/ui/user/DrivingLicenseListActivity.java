package com.example.mylicenseregistry.ui.user;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylicenseregistry.R;
import com.example.mylicenseregistry.adapter.DrivingLicenseListAdapter;
import com.example.mylicenseregistry.datadto.BluelinkModel;
import com.example.mylicenseregistry.datadto.BluelinkSettings;
import com.example.mylicenseregistry.datadto.DrivingLicenseImage;
import com.example.mylicenseregistry.ui.BaseActivity_CommonGNB;
import com.example.mylicenseregistry.widget.ContentButton;

import java.util.ArrayList;


public class DrivingLicenseListActivity extends BaseActivity_CommonGNB {

    private static final String TAG = "DrivingLicenseListActivity";
    DrivingLicenseListAdapter mAdapter;
    RelativeLayout rel_DrivingLicenseListActivity_EmptyList;
    ContentButton btn_DrivingLicenseListActivity_ListAdd;
    RecyclerView rcl_DrivingLicenseListActivity;

    private ArrayList<DrivingLicenseImage> mDataList = new ArrayList<DrivingLicenseImage>();
    public static final int REQUEST_CODE_IMAGE = 30001;
    public static final int REQUEST_CODE_IMAGE_DETAIL = 30002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_license_list);
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDrivingLicenseList();
    }

    @Override
    public void getInitializeParameter() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE){
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("Position",0);
                setDrivingLicenseList();
                mAdapter.notifyItemChanged(position);
            }
        }
        if(requestCode == REQUEST_CODE_IMAGE_DETAIL){
            if (resultCode == RESULT_OK){
                int position = data.getIntExtra("Position",0);
                deleteDrivingLicenseImage(position);
                mDataList.remove(position);
                mAdapter.notifyItemRemoved(position);
                setDrivingLicenseList();
                Toast.makeText(mContext, R.string.Toast_DeleteComplete, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void initLayout() {
        //        mAdapter = new DrivingLicenseListAdapter(mContext, mUserInfo)
        setTitleText(getString(R.string.DrivingLicenseCertificate_Text));
        btn_DrivingLicenseListActivity_ListAdd = findViewById(R.id.btn_DrivingLicenseListActivity_ListAdd);
        rel_DrivingLicenseListActivity_EmptyList = findViewById(R.id.rel_DrivingLicenseListActivity_EmptyList);
        rcl_DrivingLicenseListActivity = findViewById(R.id.rcl_DrivingLicenseListActivity);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(DrivingLicenseRegisterActivity.class, REQUEST_CODE_IMAGE);
            }
        };
        btn_DrivingLicenseListActivity_ListAdd.setOnClickListener(clickListener);
        rel_DrivingLicenseListActivity_EmptyList.setOnClickListener(clickListener);
    }

    @Override
    public void initProcess() {

    }

    private void setDrivingLicenseList() {

        getDrivingLicenseList();

        mAdapter = new DrivingLicenseListAdapter(mContext, mDataList);
        rcl_DrivingLicenseListActivity.setLayoutManager(new LinearLayoutManager(mContext));
        rcl_DrivingLicenseListActivity.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DrivingLicenseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(mContext, DrivingLicenseDetailActivity.class);
                intent.putExtra("Position", position + 1);
                startActivityForResult(intent,REQUEST_CODE_IMAGE_DETAIL);
            }
        });
        if (mDataList == null || mDataList.isEmpty()) {
            rel_DrivingLicenseListActivity_EmptyList.setVisibility(View.VISIBLE);
            rcl_DrivingLicenseListActivity.setVisibility(View.GONE);
            btn_DrivingLicenseListActivity_ListAdd.setVisibility(View.GONE);
        } else {
            rel_DrivingLicenseListActivity_EmptyList.setVisibility(View.GONE);
            rcl_DrivingLicenseListActivity.setVisibility(View.VISIBLE);
            btn_DrivingLicenseListActivity_ListAdd.setVisibility(View.VISIBLE);
            if(mAdapter.getItemCount() >= 10){
                btn_DrivingLicenseListActivity_ListAdd.setVisibility(View.GONE);
            }
        }
    }

    private void getDrivingLicenseList() {
        mDataList.clear();

        ArrayList<Bundle> dbArrayList = null;
        dbArrayList = BluelinkModel.getInst(mContext).selectDrivingLicenseImageTable();

        if (dbArrayList != null) {
            for (Bundle item : dbArrayList) {
                mDataList.add(new DrivingLicenseImage(item.getInt(BluelinkSettings.DrivingLicenseImage.INDEX),
                        item.getString(BluelinkSettings.DrivingLicenseImage.DRIVING_LICENSE_IMAGE_FRONT),
                        item.getString(BluelinkSettings.DrivingLicenseImage.DRIVING_LICENSE_IMAGE_BACK)));
            }
        }
    }

    private void deleteDrivingLicenseImage(int position)
    {
        DrivingLicenseImage deleteImage = mDataList.get(position);
        int index = deleteImage.getIndex();
        BluelinkModel.getInst(mContext).deleteDrivingLicenseImage(index);
    }
}