package com.jaguarlandrover.hvacdemo;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RVIDlinkReceivePacket.java
 * Project: HVACDemo
 *
 * Created by Lilli Szafranski on 6/15/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class RVIDlinkReceivePacket extends RVIDlinkPacket
{
    private final static String TAG = "HVACDemo:RVIDlinkReceivePacket";

    /**
     * The mod parameter.
     * This client is only using 'proto_json_rpc' at the moment.
     */
    @SerializedName("mod")
    private String mMod;

    /**
     * The RVIService used to create the request params
     */
    private transient RVIService mService;

    @SerializedName("data")
    private String mData;

    public RVIDlinkReceivePacket() {
    }

    /**
     * Helper method to get a receive dlink json object
     *
     * @param service The service that is getting invoked
     */
    public RVIDlinkReceivePacket(RVIService service) {
        super(Command.RECEIVE);

        mMod = "proto_json_rpc";
        mService = service; // TODO: With this paradigm, if one of the parameters of mService changes, mData string will still be the same.
        mData = Base64.encodeToString(mService.jsonString().getBytes(), Base64.DEFAULT);
    }

    public RVIDlinkReceivePacket(HashMap jsonHash) {
        super(Command.RECEIVE, jsonHash);

        mMod = (String) jsonHash.get("mod");

        mService = new RVIService(new String(Base64.decode((String)jsonHash.get("data"), Base64.DEFAULT)));
    }

    public RVIService getService() {
        if (mService == null && mData != null)
            mService = new RVIService(new String(Base64.decode(mData, Base64.DEFAULT)));

        return mService;
    }

    public void setService(RVIService service) {
        mService = service;
    }
}
