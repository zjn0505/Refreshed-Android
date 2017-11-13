package xyz.jienan.pushpull.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import xyz.jienan.pushpull.R;

/**
 * Created by Jienan on 2017/11/7.
 */

public class PushConfigDialog extends DialogFragment {

    private RadioGroup rgExpired;
    private RadioButton rbMin;
    private RadioButton rbHr;
    private RadioButton rbDay;
    private RadioButton rbInfi;

    private TextView tvExpire;
    private SeekBar sbPeriod;
    private EditText edtAllowance;


    public String expiredTime;
    public int expiredAllowance;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_expire, null);

        rgExpired = view.findViewById(R.id.rg_expire);
        rbMin = view.findViewById(R.id.rb_min);
        rbHr = view.findViewById(R.id.rb_hr);
        rbDay = view.findViewById(R.id.rb_day);
        rbInfi = view.findViewById(R.id.rb_infi);
        sbPeriod = view.findViewById(R.id.sb_period);
        tvExpire = view.findViewById(R.id.tv_expire);
        edtAllowance = view.findViewById(R.id.edt_allowance);

        rgExpired.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_min:
                        sbPeriod.setMax(60);
                        tvExpire.setEnabled(true);
                        sbPeriod.setEnabled(true);
                        tvExpire.setTextColor(Color.BLACK);
                        editor.putInt("EXPIRED_TYPE", 0);
                        break;
                    case R.id.rb_hr:
                        sbPeriod.setMax(48);
                        tvExpire.setEnabled(true);
                        sbPeriod.setEnabled(true);
                        tvExpire.setTextColor(Color.BLACK);
                        editor.putInt("EXPIRED_TYPE", 1);
                        break;
                    case R.id.rb_day:
                        sbPeriod.setMax(30);
                        tvExpire.setEnabled(true);
                        sbPeriod.setEnabled(true);
                        tvExpire.setTextColor(Color.BLACK);
                        editor.putInt("EXPIRED_TYPE", 2);
                        break;
                    case R.id.rb_infi:
                        tvExpire.setEnabled(false);
                        sbPeriod.setEnabled(false);
                        tvExpire.setTextColor(Color.GRAY);
                        editor.putInt("EXPIRED_TYPE", 3);
                        break;
                }
                editor.commit();
            }
        });

        sbPeriod.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvExpire.setText(String.format("%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt("EXPIRED_TIME", seekBar.getProgress());
                editor.commit();
            }
        });

        builder.setTitle(R.string.action_push_config)
                .setView(view)
                .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int allowance = Integer.valueOf(edtAllowance.getText().toString());
                        editor.putInt("ACCESS_COUNT", allowance);
                    }
                });
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sharedPreferences != null) {
            int time = sharedPreferences.getInt("EXPIRED_TIME", 0);
            int type = sharedPreferences.getInt("EXPIRED_TYPE", 2);
            int count = sharedPreferences.getInt("ACCESS_COUNT", 0);

            switch (type) {
                case 0:
                    rgExpired.check(R.id.rb_min);
                    break;
                case 1:
                    rgExpired.check(R.id.rb_hr);
                    break;
                case 2:
                    rgExpired.check(R.id.rb_day);
                    break;
                case 3:
                    rgExpired.check(R.id.rb_infi);
                    break;
            }
            sbPeriod.setProgress(time);
            edtAllowance.setText(count);
        }
    }

    public void setContext(FragmentActivity context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences("MEMO_CONFIG", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}
