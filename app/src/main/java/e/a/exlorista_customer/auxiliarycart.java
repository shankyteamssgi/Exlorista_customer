package e.a.exlorista_customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by a on 5/4/2020.
 */

class auxiliarycart {
    private static final String SPFILE_CART="spfile_cart";
    private static final String SPKEY_CARTDATA="spkey_cartdata";
    private static final String SPKEY_STOREID="spkey_storeid";

    // internal HashMap key (keys used to store individual attributes of prod_attr_val argument)
    static final String IHMKEY_PRODBRANDNAME="prodbrand_name";
    static final String IHMKEY_PRODDETNAME="proddet_name";
    static final String IHMKEY_PRODSIZEUNITCONTAINER="prodsizeunitcontainer";
    static final String IHMKEY_PRODMRP="prod_mrp";
    static final String IHMKEY_PRODSTOREPRICE="storeprodlist_storeprice";
    static final String IHMKEY_PRODIMAGESTRING="prod_image_String";

    // internal HashMap key (key used to store product count)
    static final String IHMKEY_PRODCOUNT="prod_count";

    static void clearCart(Context context){
        // Clears cart
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        cartItemSP.edit()
                .putString(auxiliarycart.SPKEY_STOREID,null)
                .putString(auxiliarycart.SPKEY_CARTDATA,null)
                .apply();
    }

    static void clearCartOGC(Context context){
        // Clears cart
        context.getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE).edit()
                .putString(auxiliarycart.SPKEY_STOREID,null)
                .putString(auxiliarycart.SPKEY_CARTDATA,null)
                .apply();
    }

    static void addProdToCart(Context context, String prod_id, String store_id, String[] prod_attr_val, String prod_image_String){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String prev_store_id=cartItemSP.getString(auxiliarycart.SPKEY_STOREID,null);
        String prev_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(prev_store_id==null && prev_cart_data_String==null){
            HashMap<String,HashMap<String,String>> cart_data_HashMap=new HashMap<>();
            HashMap<String,String> prod_attr_val_internalHashMap=new HashMap<>();
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODBRANDNAME,prod_attr_val[0]);
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODDETNAME,prod_attr_val[1]);
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODSIZEUNITCONTAINER,prod_attr_val[2]);
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODMRP,prod_attr_val[3]);
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODSTOREPRICE,prod_attr_val[4]);
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODCOUNT,"1");
            prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODIMAGESTRING,prod_image_String);
            cart_data_HashMap.put(prod_id,prod_attr_val_internalHashMap);
            String cart_data_String=(new Gson()).toJson(cart_data_HashMap);
            cartItemSP.edit()
                    .putString(auxiliarycart.SPKEY_STOREID,store_id)
                    .putString(auxiliarycart.SPKEY_CARTDATA,cart_data_String)
                    .apply();
        } else if (prev_store_id!=null && prev_cart_data_String!=null){
            if(prev_store_id.equals(store_id)){
                Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
                HashMap<String,HashMap<String,String>> cart_data_HashMap=(new Gson()).fromJson(prev_cart_data_String,type);
                HashMap<String,String> prod_attr_val_internalHashMap=new HashMap<>();
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODBRANDNAME,prod_attr_val[0]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODDETNAME,prod_attr_val[1]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODSIZEUNITCONTAINER,prod_attr_val[2]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODMRP,prod_attr_val[3]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODSTOREPRICE,prod_attr_val[4]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODCOUNT,"1");
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODIMAGESTRING,prod_image_String);
                cart_data_HashMap.put(prod_id,prod_attr_val_internalHashMap);
                String cart_data_String=(new Gson()).toJson(cart_data_HashMap);
                cartItemSP.edit()
                        .putString(auxiliarycart.SPKEY_CARTDATA,cart_data_String)
                        .apply();
            } else{
                auxiliarycart.clearCart(context);
                HashMap<String,HashMap<String,String>> cart_data_HashMap=new HashMap<>();
                HashMap<String,String> prod_attr_val_internalHashMap=new HashMap<>();
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODBRANDNAME,prod_attr_val[0]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODDETNAME,prod_attr_val[1]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODSIZEUNITCONTAINER,prod_attr_val[2]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODMRP,prod_attr_val[3]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODSTOREPRICE,prod_attr_val[4]);
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODCOUNT,"1");
                prod_attr_val_internalHashMap.put(auxiliarycart.IHMKEY_PRODIMAGESTRING,prod_image_String);
                cart_data_HashMap.put(prod_id,prod_attr_val_internalHashMap);
                String cart_data_String=(new Gson()).toJson(cart_data_HashMap);
                cartItemSP.edit()
                        .putString(auxiliarycart.SPKEY_STOREID,store_id)
                        .putString(auxiliarycart.SPKEY_CARTDATA,cart_data_String)
                        .apply();
            }
        }
    }

    static String incrementProdCountByOne(Context context, String prod_id){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String prev_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
        HashMap<String,HashMap<String,String>> prev_cart_data_HashMap=(new Gson()).fromJson(prev_cart_data_String,type);
        String prod_count=prev_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT);
        String prod_count_incremented=Integer.toString(Integer.parseInt(prod_count)+1);
        prev_cart_data_HashMap.get(prod_id).put(auxiliarycart.IHMKEY_PRODCOUNT,prod_count_incremented);
        String cart_data_String=(new Gson()).toJson(prev_cart_data_HashMap);
        cartItemSP.edit()
                .putString(auxiliarycart.SPKEY_CARTDATA,cart_data_String)
                .apply();
        return prod_count_incremented;
    }

    static String incrementProdCountByOneOGC(Context context, String prod_id){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        HashMap<String,HashMap<String,String>> prev_cart_data_HashMap=(
                new Gson()).fromJson(cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null)
                ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
        String prod_count_incremented=Integer.toString(Integer
                .parseInt(prev_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT))+1);
        prev_cart_data_HashMap.get(prod_id).put(auxiliarycart.IHMKEY_PRODCOUNT,prod_count_incremented);
        cartItemSP.edit()
                .putString(auxiliarycart.SPKEY_CARTDATA,(new Gson()).toJson(prev_cart_data_HashMap))
                .apply();
        return prod_count_incremented;
    }

    static String decrementProdCountByOne(Context context, String prod_id){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String prev_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
        HashMap<String,HashMap<String,String>> prev_cart_data_HashMap=(new Gson()).fromJson(prev_cart_data_String,type);
        String prod_count=prev_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT);
        int prod_count_decremented_int=Integer.parseInt(prod_count)-1;
        String prod_count_decremented=Integer.toString(prod_count_decremented_int<0?0:prod_count_decremented_int);
        prev_cart_data_HashMap.get(prod_id).put(auxiliarycart.IHMKEY_PRODCOUNT,prod_count_decremented);
        String cart_data_String=(new Gson()).toJson(prev_cart_data_HashMap);
        cartItemSP.edit()
                .putString(auxiliarycart.SPKEY_CARTDATA,cart_data_String)
                .apply();
        return prod_count_decremented;
    }

    static String decrementProdCountByOneOGC(Context context, String prod_id){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        HashMap<String,HashMap<String,String>> prev_cart_data_HashMap=(
                new Gson()).fromJson(cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null)
                ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
        int prod_count_decremented_int=Integer
                .parseInt(prev_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT))-1;
        String prod_count_decremented=Integer.toString(prod_count_decremented_int<0?0:prod_count_decremented_int);
        prev_cart_data_HashMap.get(prod_id).put(auxiliarycart.IHMKEY_PRODCOUNT
                ,prod_count_decremented);
        cartItemSP.edit()
                .putString(auxiliarycart.SPKEY_CARTDATA,(new Gson()).toJson(prev_cart_data_HashMap))
                .apply();
        return prod_count_decremented;
    }

    static String thisProdCountInCart(Context context, String prod_id, String store_id, String[] prod_attr_val){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String SP_store_id=cartItemSP.getString(auxiliarycart.SPKEY_STOREID,null);
        String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        try{
            if(SP_store_id==null && SP_cart_data_String==null){
                return null;
            }
            else if(SP_store_id!=null && SP_cart_data_String!=null){
                if(SP_store_id.equals(store_id)){
                    Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
                    HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String,type);
                    // particularly the below line is causing NullPointerException
                    return SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT);
                } else{
                    return null;
                }
            }
        } catch (NullPointerException npe){
            //Log.i("CART","NPE occurred (prodCountInCart) for product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]);
            //npe.printStackTrace();
        } catch (Exception e){
            //Log.i("CART","Exception occurred (prodCountInCart) for product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]);
            //e.printStackTrace();
        }
        return null;
    }

    static String thisProdCountInCartOGC(Context context, String prod_id, String store_id){
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String SP_store_id=cartItemSP.getString(auxiliarycart.SPKEY_STOREID,null);
        String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        try{
            if(SP_store_id==null && SP_cart_data_String==null){
                return null;
            }
            else if(SP_store_id!=null && SP_cart_data_String!=null){
                if(SP_store_id.equals(store_id)){
                    HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String
                            ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
                    // particularly the below line is causing NullPointerException
                    return SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT);
                } else{
                    return null;
                }
            }
        } catch (NullPointerException npe){
            //Log.i("CART","NPE occurred (prodCountInCart) for product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]);
            //npe.printStackTrace();
        } catch (Exception e){
            //Log.i("CART","Exception occurred (prodCountInCart) for product name,SUC: "+prod_attr_val[1]+" "+prod_attr_val[2]);
            //e.printStackTrace();
        }
        return null;
    }

    static ArrayList<String> prodIdsInCart(Context context){
        // utility in CartProductsAdapter.java
        // returns prod_id of those products in cart whose count is greater than 0
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String,type);
            Set<String> prodIdSet=SP_cart_data_HashMap.keySet();
            ArrayList<String> prodIdArrayList=new ArrayList<>();
            for(String prodId: prodIdSet){
                String prodCountString=SP_cart_data_HashMap.get(prodId).get(auxiliarycart.IHMKEY_PRODCOUNT);
                if(Integer.parseInt(prodCountString)>0){
                    prodIdArrayList.add(prodId);
                }
            }
            return prodIdArrayList;
        } else{
            return null;
        }
    }

    static ArrayList<String> prodIdsInCartOGC(Context context){
        // utility in CartProductsAdapter.java
        // returns prod_id of those products in cart whose count is greater than 0
        String SP_cart_data_String=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE)
                .getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String
                    ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
            Set<String> prodIdSet=SP_cart_data_HashMap.keySet();
            ArrayList<String> prodIdArrayList=new ArrayList<>();
            for(String prodId: prodIdSet){
                String prodCountString=SP_cart_data_HashMap.get(prodId).get(auxiliarycart.IHMKEY_PRODCOUNT);
                if(Integer.parseInt(prodCountString)>0){
                    prodIdArrayList.add(prodId);
                }
            }
            return prodIdArrayList;
        } else{
            return null;
        }
    }

    static int diffProdCountInCart(Context context){
        // utility in CartProductsAdapter.java
        // returns prod_id of those products in cart whose count is greater than 0
        int diff_prodcount=0;
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String,type);
            Set<String> prodIdSet=SP_cart_data_HashMap.keySet();
            for(String prodId: prodIdSet){
                String prodCountString=SP_cart_data_HashMap.get(prodId).get(auxiliarycart.IHMKEY_PRODCOUNT);
                if(Integer.parseInt(prodCountString)>0){
                    diff_prodcount+=1;
                }
            }
        }
        return diff_prodcount;
    }

    static int diffProdCountInCartOGC(Context context){
        // utility in CartProductsAdapter.java
        // returns prod_id of those products in cart whose count is greater than 0
        int diff_prodcount=0;
        String SP_cart_data_String=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE)
                .getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String
                    ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
            Set<String> prodIdSet=SP_cart_data_HashMap.keySet();
            for(String prodId: prodIdSet){
                if(Integer.parseInt(SP_cart_data_HashMap.get(prodId).get(auxiliarycart.IHMKEY_PRODCOUNT))>0){
                    diff_prodcount+=1;
                }
            }
        }
        return diff_prodcount;
    }

    static String getStoreIdInCart(Context context){
        // utility in CartProductsAdapter.java
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        return cartItemSP.getString(auxiliarycart.SPKEY_STOREID,null);
    }

    static String getStoreIdInCartOGC(Context context){
        // utility in CartProductsAdapter.java
        return context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE)
                .getString(auxiliarycart.SPKEY_STOREID,null);
    }

    static String getCartProdInternalHashMapValue(Context context, String prod_id, String prod_IHMK){
        // utility in CartProductsAdapter.java
        // takes prod_id for a product in cart and returns value of given IHMK for that product
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String,type);
            return SP_cart_data_HashMap.get(prod_id).get(prod_IHMK);
        }
        return null;
    }

    static String getCartProdInternalHashMapValueOGC(Context context, String prod_id, String prod_IHMK){
        // utility in CartProductsAdapter.java
        // takes prod_id for a product in cart and returns value of given IHMK for that product
        String SP_cart_data_String=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE)
                .getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String
                    ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
            return SP_cart_data_HashMap.get(prod_id).get(prod_IHMK);
        }
        return null;
    }

    static String[] getCartProdInternalHashMapAllVals(Context context, String prod_id){
        // utility in CartProductsAdapter.java
        SharedPreferences cartItemSP=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
        String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String,type);
            String[] IHMK_vals=new String[7];
            IHMK_vals[0]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODIMAGESTRING);
            IHMK_vals[1]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODBRANDNAME);
            IHMK_vals[2]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODDETNAME);
            IHMK_vals[3]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODSIZEUNITCONTAINER);
            IHMK_vals[4]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODMRP);
            IHMK_vals[5]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODSTOREPRICE);
            IHMK_vals[6]=SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT);
            return IHMK_vals;
        }
        return null;
    }

    static String[] getCartProdInternalHashMapAllValsOGC(Context context, String prod_id){
        // utility in CartProductsAdapter.java
        String SP_cart_data_String=context
                .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE)
                .getString(auxiliarycart.SPKEY_CARTDATA,null);
        if(SP_cart_data_String!=null){
            HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String
                    ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
            return new String[]{
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODIMAGESTRING),
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODBRANDNAME),
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODDETNAME),
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODSIZEUNITCONTAINER),
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODMRP),
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODSTOREPRICE),
                    SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT)};
        }
        return null;
    }

    static int getCartTotalAmount(Context context, ArrayList<String> prodIdWithCountGrtThanZero){
        if(prodIdWithCountGrtThanZero.size()>0){
            SharedPreferences cartItemSP=context
                    .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE);
            String SP_cart_data_String=cartItemSP.getString(auxiliarycart.SPKEY_CARTDATA,null);
            if(SP_cart_data_String!=null){
                int totalAmount=0;
                Type type=new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
                HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String,type);
                for(String prod_id : prodIdWithCountGrtThanZero){
                    Log.i("AIOOB auxiliary cart",SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODSTOREPRICE));
                    String prod_storePrice_String=SP_cart_data_HashMap.get(prod_id)
                            .get(auxiliarycart.IHMKEY_PRODSTOREPRICE);
                    int prod_storePrice=prod_storePrice_String.matches("\\d+")
                            ?Integer.parseInt(prod_storePrice_String)
                            :Integer.parseInt(prod_storePrice_String.substring(3));
                    int prod_count=Integer.parseInt(SP_cart_data_HashMap.get(prod_id)
                            .get(auxiliarycart.IHMKEY_PRODCOUNT));
                    totalAmount+=prod_storePrice*prod_count;
                }
                return totalAmount;
            }
        }
        return 0;
    }

    static int getCartDeliveryCharges(int cartTotalAmount){
        int MINIMUM_ORDER_QUANTITY=700;
        int MARGIN=5;
        int REVENUE_PER_ORDER=MINIMUM_ORDER_QUANTITY*MARGIN/100;
        return cartTotalAmount>=MINIMUM_ORDER_QUANTITY?0:REVENUE_PER_ORDER-(cartTotalAmount*MARGIN/100);
    }

    static int getCartTotalAmountOGC(Context context, ArrayList<String> prodIdWithCountGrtThanZero){
        int totalAmount=0;
        if(prodIdWithCountGrtThanZero.size()>0){
            String SP_cart_data_String=context
                    .getSharedPreferences(auxiliarycart.SPFILE_CART,Context.MODE_PRIVATE)
                    .getString(auxiliarycart.SPKEY_CARTDATA,null);
            if(SP_cart_data_String!=null){
                HashMap<String,HashMap<String,String>> SP_cart_data_HashMap=(new Gson()).fromJson(SP_cart_data_String
                        ,new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType());
                for(String prod_id : prodIdWithCountGrtThanZero){
                    totalAmount+=Integer.parseInt(SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODSTOREPRICE).substring(3)) *
                            Integer.parseInt(SP_cart_data_HashMap.get(prod_id).get(auxiliarycart.IHMKEY_PRODCOUNT));
                }
            }
        }
        return totalAmount;
    }

}
