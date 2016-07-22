package com.slogup.catalog.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.slogup.catalog.R;
import com.slogup.catalog.manager.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

public class APIRequester {

    private static final String LOG_TAG = APIRequester.class.getSimpleName();

    private Context mContext;

    public APIRequester(Context context) {
        mContext = context;
    }

    public void requestGET(String subUrl, JSONObject jsonRequestParams, final APICallbackListener callbackListener) {

        callbackListener.onBefore();

        String rootUrl;
        if (AppManager.isDebug) {

            rootUrl = APIConstants.ROOT_URL_DEVELOPMENT;
        } else {

            rootUrl = APIConstants.ROOT_URL_PRODUCTION;
        }
        Log.i(LOG_TAG, subUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, rootUrl + subUrl, jsonRequestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {

                    Log.i(LOG_TAG, response.toString());
                    callbackListener.onSuccess(response);

                } else {

                    callbackListener.onSuccess(null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("hello", error.toString());
                String msg = "";
                if (error instanceof TimeoutError)
                    msg = mContext.getString(R.string.err_timeout);
                else if (error instanceof NoConnectionError)
                    msg = mContext.getString(R.string.err_no_connection);
                else if (error instanceof ServerError)
                    msg = mContext.getString(R.string.err_server);
                else if (error instanceof NetworkError)
                    msg = mContext.getString(R.string.err_network);
                else
                    msg = mContext.getString(R.string.err_unexpected);

                Error err = new Error(msg);
                callbackListener.onError(err);
                Log.i(LOG_TAG, "error" + error.getLocalizedMessage());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(12000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueueManager.getInstance(mContext).getRequestQueue().add(jsonObjectRequest);
    }

    public JSONObject makeRequestParams(String fid, String id, JSONObject fields, JSONObject recordsSet) {

        JSONObject params = new JSONObject();
        JSONObject transactions = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONObject dataSet = new JSONObject();


        try {
            params.put(APIConstants.COMMON_PARAM_ROOT_KEY_DATASETS, dataSet);
            params.put(APIConstants.COMMON_PARAM_ROOT_KEY_TRASACTION, transactions);
            params.put(APIConstants.COMMON_PARAM_ROOT_KEY_ATTRIBUTES, attributes);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "makeRequestParams: Error making root objects");
        }

        Log.i(LOG_TAG, params.toString());

        return params;
    }

    public interface APICallbackListener {

        public void onBefore();

        public void onSuccess(JSONObject object);

        public void onError(Error error);
    }
}
