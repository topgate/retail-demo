package jp.co.topgate.gretail;

import java.util.Date;

/**
 * Created by nagai on 2017/05/09.
 */
public class CheckTest {

    public class MyClass {
        private Date date;
        public void setDate(Date date) {
            this.date = date;
        }
        public Date getDate() {
            return this.date;
        }
    }

    public static void main(String[] args) {
        /*
        System.out.println("Hello");
        //Gson gson = new Gson();
        //Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

        String date = "\"2013-02-10T13:45:30+0900\"";
        //Date test = gson.fromJson(date, Long.class);
        System.out.println("date:" + test);

        //Receipt a = gson.fromJson('{"salesDate": "2017-01-01T00:00:00",\n "storeCode": "a",\n "registerID": "b"\n "receiptNumber": 1,\n "receiptBranchNumber": 1,\n "productCode": "a",\n "quantity": 1,\n "price": 1}', Receipt.class);
        System.out.println("ij: " + gson.toJson(test));
        */
    }
}
