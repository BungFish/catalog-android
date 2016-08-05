package com.himart.showroom.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Metadata {
    JSONObject std;

    public Metadata() {}

    public Metadata(JSONObject meta) {

        try {
            std = meta.getJSONObject("std");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getCdn() {
        try {
            return std.getJSONObject("cdn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getRootUrl() {
        try {
            return getCdn().getString("rootUrl") + "/";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject getStd() {
        return std;
    }

    public void setStd(JSONObject std) {
        this.std = std;
    }
}
