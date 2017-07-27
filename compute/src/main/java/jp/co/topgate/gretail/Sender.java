package jp.co.topgate.gretail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ning.http.client.AsyncHttpClient;
import jp.co.topgate.gretail.model.Order;

/**
 * Created by nagai on 2017/05/15.
 */
public class Sender {

    private final AsyncHttpClient client;
    private final String endpoint;
    private final Gson gson = new GsonBuilder().create();;

    public Sender(String endpoint) {
        this.client = new AsyncHttpClient();
        this.endpoint = endpoint;
    }

    public void send(final Order order) {
        final String body = gson.toJson(order);
        client.preparePost(this.endpoint)
                .setHeader("Content-Type","application/json")
                .setBody(body).execute();//
    }

}
