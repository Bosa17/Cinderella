package in.cinderella.testapp.Utils;

import android.content.Context;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;

import java.util.HashMap;
import java.util.Map;

public class PaymentsHelper {
    private Context mContext;
    public PaymentsHelper(Context context) {
        mContext=context;
        DataHelper dataHelper=new DataHelper(mContext);
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put( "MID" , "dMcswz34091511518632");
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , dataHelper.getUID()+System.currentTimeMillis());
        paramMap.put( "CUST_ID" , dataHelper.getUID());
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT" , "100.12");
        paramMap.put( "WEBSITE" , "WEBSTAGING");
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
        String checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MercahntKey, paramMap);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+paramMap.get("ORDER_ID"));
        paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=");
        PaytmOrder Order = new PaytmOrder(paramMap);
    }
}
