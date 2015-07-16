package com.houston.HoustonAndroid.com.houston;

import com.houston.HoustonAndroid.com.houston.model.GeoCoordinate;
import com.houston.HoustonAndroid.com.houston.model.Item;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum ServerFacade {
    INSTANCE;
    private static final String URL = "http://82.193.123.105:8080/data";

    public JSONObject setKey(final String key, final String value) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair("key", key));
            nameValuePair.add(new BasicNameValuePair("value", value));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return parseJSON(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject getKey(String key, long time) {
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("key", key));
            params.add(new BasicNameValuePair("time", time + ""));
            String paramString = URLEncodedUtils.format(params, "utf-8");

            URI website = new URI(URL + "?" + paramString);
            request.setURI(website);

            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return parseJSON(response);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject parseJSON(HttpResponse response) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());

            return new JSONObject(tokener);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}
