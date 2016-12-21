package ir.dorsa.dorsaworld.other;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mehdi on 1/2/16 AD.
 */
public class FetchInternet {

    
    public static String getStringFromInternet(String Address, ArrayList<String> postName, ArrayList<String> postVal) {

        try {
            String url = Address;
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);


            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

            for (int i = 0; i < postName.size(); i++) {
                urlParameters.add(new BasicNameValuePair(postName.get(i), postVal.get(i)));
            }


            post.setEntity(new UrlEncodedFormEntity(urlParameters, HTTP.UTF_8));


            HttpResponse response = client.execute(post);

//       post.setRequestProperty("connection", "close");

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            post.abort();
            client.getConnectionManager().closeExpiredConnections();
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    // Fast Implementation
    private static StringBuilder inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return full string
        return total;
    }
}
