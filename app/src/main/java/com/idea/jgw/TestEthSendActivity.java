package com.idea.jgw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.idea.jgw.service.GetSendStatusService;

public class TestEthSendActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_eth_send);


        EditText et_send = findViewById(R.id.et_send);
        EditText et_from = findViewById(R.id.et_from);
        Button btnSend = findViewById(R.id.btn_send);
        Button btnQuery = findViewById(R.id.btn_query);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tranId = "0x912635a770ff0fdd302499f4d3ec7036d7b1cf3a3ddc343fd4f8365b38eddc22";
                Intent intent = new Intent(TestEthSendActivity.this, GetSendStatusService.class);
                intent.putExtra(GetSendStatusService.EXTRA_TRANSACTION_ID, tranId);
                TestEthSendActivity.this.startService(intent);
            }
        });
    }
}
