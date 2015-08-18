package com.estagio.buyadot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.estagio.buyadot.util.IabHelper;
import com.estagio.buyadot.util.IabResult;
import com.estagio.buyadot.util.Inventory;
import com.estagio.buyadot.util.Purchase;


public class Main extends Activity {


    private TextView dots;
    private IabHelper mHelper;
    private static final String ITEM_SKU_DOT = "com.estagio.buyadot.dot";
    private static final String ITEM_SKU_SPACE = "com.estagio.buyadot.space";
    private final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoNhrXWyQ/03JtGRk+JX7sA9YG0RqhE9k42IqEw0CT03NEGapkRIUPTriFmveedvAlgwYqsRxwVgF1c2bf5lZnRuF4PJac6WcKyi4SMWzUaLNUOXmj53acQnWRlDEa2KCApeXiRx/wM0419qQ9W28dfWohhIdY0FsaC6g0tDzAp8GsY6sQTXG2CmfPov70PX8qUtTIQHoyu14rAtSvASwZ7pLcm/EB1rjP6YKR3PIs0hRVxNdh1sh2vTSvJcpebQVbsoXGpJZOEdY4kFg+nPRPA7pLPyEkzZHb5zvVFvqI4l7KuXiBeZ1rnewD/vFuaNlB6A9S+6Edcmqmffkzt9HdQIDAQAB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dots = (TextView) findViewById(R.id.dots);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    System.out.println("In-app Billing setup failed: " +
                            result);
                } else {
                    System.out.println("In-app Billing is set up OK");
                }
            }
        });
    }

    public void plusDot(View v) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU_DOT, 10001, mPurchaseFinishedListener, "mypurchasetoken");
    }

    public void plusSpace(View v) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU_SPACE, 10001, mPurchaseFinishedListener, "mypurchasetoken");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU_DOT)) {
                consumeItem();
            }

        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                System.out.println("Falhou no mReceivedInventoryListener");
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU_DOT),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {
                    if (result.isSuccess() && purchase.getSku().equals(ITEM_SKU_DOT)) {
                        dots.setText(dots.getText().toString() + "■");
                    } else if (result.isSuccess() && purchase.getSku().equals(ITEM_SKU_SPACE)) {
                        dots.setText(dots.getText().toString() + "  ");
                    } else {
                        System.out.println("Falhou no mConsumeFinishedListener");
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
