package com.getcinderella.app.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentsHelper   {
    private static String MID="dMcswz34091511518632";
    private Context mContext;
    private DataHelper dataHelper;
    private String TXN_AMOUNT;
    private String CUST_ID;
    private String ORDER_ID;
    private int pixies_bought;
    private Boolean isPremiumPurchase;
    public PaymentsHelper(Context context) {
        mContext=context;
        dataHelper=new DataHelper(mContext);
        isPremiumPurchase=false;
    }
    public void startPayment(String TXN_AMOUNT,int pixies_bought){
        this.TXN_AMOUNT=TXN_AMOUNT;
        this.CUST_ID=dataHelper.getUID();
        this.ORDER_ID=dataHelper.getUID()+"_"+System.currentTimeMillis();
        this.pixies_bought=pixies_bought;
        try{
            sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
            dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }catch(Exception e) {
            e.printStackTrace();
        }

    }
    public void startPremiumPayment(String TXN_AMOUNT){
        this.TXN_AMOUNT=TXN_AMOUNT;
        this.CUST_ID=dataHelper.getUID();
        this.ORDER_ID=dataHelper.getUID()+"_"+System.currentTimeMillis();
        isPremiumPurchase=true;
        try{
            sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
            dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(mContext);
        //private String orderId , mid, custid, amt;
        String url ="https://cinderellaapp.000webhostapp.com/paytm/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(mContext);
            String param=
                    "MID="+MID+
                            "&ORDER_ID=" + ORDER_ID+
                            "&CUST_ID="+CUST_ID+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+TXN_AMOUNT+
                            "&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
        }
    }
}
