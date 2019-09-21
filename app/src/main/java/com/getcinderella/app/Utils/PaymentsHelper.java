package com.getcinderella.app.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentsHelper  implements PaytmPaymentTransactionCallback {
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", MID); //MID provided by paytm
            paramMap.put("ORDER_ID", ORDER_ID);
            paramMap.put("CUST_ID", CUST_ID);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", TXN_AMOUNT);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(mContext, true, true,
                    PaymentsHelper.this  );
        }
    }
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        if (bundle.get("RESPCODE").equals("01")){
            if (isPremiumPurchase)
                dataHelper.putIsPremium2Firebase(true);
            else
                dataHelper.addPixies(pixies_bought);
        }
        else
            Toast.makeText(mContext, "Faced Unexpected Problem! Try Again!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void networkNotAvailable() {
        Toast.makeText(mContext, "Network Problem! Try Again!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }
}
