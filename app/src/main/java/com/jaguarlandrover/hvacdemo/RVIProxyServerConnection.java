package com.jaguarlandrover.hvacdemo;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RVIProxyServerConnection.java
 * Project: HVACDemo
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RVIProxyServerConnection implements RVIRemoteConnection
{
    private final static String TAG = "HVACDemo:RVIProxyServerConnection";

    private String mProxyServerUrl;

    @Override
    public void sendRviRequest(RPCRequest request) {
        if (!isConfigured())
            return;

        new AsyncRVIRequest().execute(request.jsonString());
    }

    private class AsyncRVIRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strs) {

            String urlParameters = strs[0];
            Log.d(TAG, "Sending url parameters: " + urlParameters);

            HttpURLConnection connection = null;
            URL url;

            try
            {
                url = new URL(mProxyServerUrl);
                //url = new URL("http://rvi1.nginfotpdx.net:8801");//mProxyServerUrl);
                //url = new URL("http://192.168.6.86:8811");//http://rvi1.nginfotpdx.net:8801");//mProxyServerUrl);
                //url = new URL("http://posttestserver.com/post.php");//mProxyServerUrl);

                //Create connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json-rpc");
                connection.setRequestProperty("User-Agent", "objc-JSONRpc/1.0");

                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                Map<String, List<String>> responseHeaders = connection.getHeaderFields();
                String responseString = connection.getResponseMessage();

                Log.d(TAG, "Response code: " + Integer.toString(connection.getResponseCode()));

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }

                rd.close();

                Log.d(TAG, "Got response: " + response.toString());

                return response.toString();

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
            finally
            {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

           @Override
           protected void onPostExecute(String result) {
               //Log.d(TAG, result);
           }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isConfigured() {
        return !(mProxyServerUrl == null || mProxyServerUrl.isEmpty());
    }

    public String getProxyServerUrl() {
        return mProxyServerUrl;
    }

    public void setProxyServerUrl(String mProxyServerUrl) {
        this.mProxyServerUrl = mProxyServerUrl;
    }
}
