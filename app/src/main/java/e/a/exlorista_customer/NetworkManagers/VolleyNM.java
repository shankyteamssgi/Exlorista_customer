package e.a.exlorista_customer.NetworkManagers;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import e.a.exlorista_customer.Singletons.AppSingleton;
import e.a.exlorista_customer.Singletons.VolleyRequestQueueSingleton;

public class VolleyNM {

    public enum HttpMethod{GET,POST};

    public static String request(HttpMethod httpMethod,
                                 String url,
                                 final HashMap<String,String> keyVal){
        /*
        Takes:
        1) http method : Either GET or POST (enum)
        2) url (String)
        3) key and value (HashMap)

        Returns:
        server response (String)
         */

        final StringBuilder _response=new StringBuilder();

        StringRequest stringRequest;

        RequestQueue requestQueue=VolleyRequestQueueSingleton.getInstance(AppSingleton.getContext()).getRequestQueue();
        requestQueue.start();

        if(keyVal!=null){
            if(keyVal.size()>0){
                stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                        , url
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            if(!response.isEmpty()){
                                _response.append(response);
                                Log.i("volley message", response);
                            }
                        }
                    }
                }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return keyVal;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String,String> params=new HashMap<String,String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }
                };
            } else{
                stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                        , url
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            if(!response.isEmpty()){
                                _response.append(response);
                                Log.i("volley message", response);
                            }
                        }
                    }
                }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            }
        } else{
            stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                    , url
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response!=null){
                        if(!response.isEmpty()){
                            _response.append(response);
                            Log.i("volley message", response);
                        }
                    }
                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }
        requestQueue.add(stringRequest);
        if(_response.toString().trim().isEmpty()){
            return null;
        }
        return _response.toString().trim();
    }



    public static String requestSync(HttpMethod httpMethod,
                                     String url,
                                     final HashMap<String,String> keyVal,
                                     int timeoutInSeconds){
        final StringBuilder _response=new StringBuilder();

        StringRequest stringRequest;

        RequestFuture<String> future=RequestFuture.newFuture();

        RequestQueue requestQueue=VolleyRequestQueueSingleton.getInstance(AppSingleton.getContext()).getRequestQueue();
        requestQueue.start();

        if(keyVal!=null){
            if(keyVal.size()>0){
                stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                        ,url
                        ,future
                        ,future){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return keyVal;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String,String> params=new HashMap<String,String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }
                };
            } else{
                stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                        ,url
                        ,future
                        ,future);
            }
        } else{
            stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                    ,url
                    ,future
                    ,future);
        }

        requestQueue.add(stringRequest);

        try {
            _response.append(future.get(timeoutInSeconds, TimeUnit.SECONDS));
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (TimeoutException te) {
            te.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        if(_response.toString().trim().isEmpty()){
            return null;
        }
        return _response.toString().trim();
    }



    public static void requestDynamic(HttpMethod httpMethod,
                                      String url,
                                      final HashMap<String,String> keyVal,
                                      final ServerCallback serverCallback){
        StringRequest stringRequest;

        RequestQueue requestQueue=VolleyRequestQueueSingleton.getInstance(AppSingleton.getContext()).getRequestQueue();
        requestQueue.start();

        if(keyVal!=null){
            if(keyVal.size()>0){
                stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                        , url
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            if(!response.isEmpty()){
                                serverCallback.onSuccess(response);
                            } else{
                                serverCallback.onEmptyResult();
                            }
                        } else{
                            serverCallback.onFailure();
                        }
                    }
                }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverCallback.onError(error);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return keyVal;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String,String> params=new HashMap<String,String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }
                };
            } else{
                stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                        , url
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            if(!response.isEmpty()){
                                serverCallback.onSuccess(response);
                            } else{
                                serverCallback.onEmptyResult();
                            }
                        } else{
                            serverCallback.onFailure();
                        }
                    }
                }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverCallback.onError(error);
                    }
                });
            }
        } else{
            stringRequest=new StringRequest(httpMethod == HttpMethod.GET ? Request.Method.GET : Request.Method.POST
                    , url
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response!=null){
                        if(!response.isEmpty()){
                            serverCallback.onSuccess(response);
                        } else{
                            serverCallback.onEmptyResult();
                        }
                    } else{
                        serverCallback.onFailure();
                    }
                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    serverCallback.onError(error);
                }
            });
        }

        requestQueue.add(stringRequest);
    }

    public static void fetchImageRequest(String url,
                                    final ImageFetchCallback imageFetchCallback){
        ImageRequest imageRequest=new ImageRequest(url
                , new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    imageFetchCallback.onSuccess(response);
                } else {
                    imageFetchCallback.onFailure();
                }
            }
        }
                , 0
                , 0
                , null
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageFetchCallback.onError(error);
            }
        });
    }



    public interface ServerCallback{
        void onSuccess(String response);
        void onEmptyResult();
        void onFailure();
        void onError(VolleyError ve);
    }

    public interface ImageFetchCallback{
        void onSuccess(Bitmap response);
        void onFailure();
        void onError(VolleyError ve);
    }

}