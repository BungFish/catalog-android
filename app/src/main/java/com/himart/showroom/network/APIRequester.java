package com.himart.showroom.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.himart.showroom.R;
import com.himart.showroom.manager.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

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
                Log.i(LOG_TAG, error.toString());
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
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> responseHeaders = response.headers;

                AppManager.appVersion = responseHeaders.get("X-SG-App-Version");

                try {
                    String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(
                            new JSONObject(json), HttpHeaderParser.parseCacheHeaders(response)
                    );
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    return Response.error(new ParseError(e));
                }

            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(12000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueueManager.getInstance(mContext).getRequestQueue().add(jsonObjectRequest);
    }

    public interface APICallbackListener {

        public void onBefore();

        public void onSuccess(JSONObject object);

        public void onError(Error error);
    }
}
