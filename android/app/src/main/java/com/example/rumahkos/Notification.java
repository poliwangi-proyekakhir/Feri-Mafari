package com.example.rumahkos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Notification extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("msg")) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Notifikasi")
                .setContentText(getIntent().getExtras().getString("msg"))
                .setConfirmText("OK")
                .setConfirmClickListener(sDialog -> {

                    Intent intent = new Intent(Notification.this, MainActivity.class);
                    intent.putExtra("redirect", "goto-notifikasi");
                    startActivity(intent);

                    sDialog.dismissWithAnimation();
                    finish();
                })
                .show();
        }

    }
}
