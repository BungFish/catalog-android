package com.himart.showroom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.himart.showroom.manager.AppManager;
import com.himart.showroom.models.Product;
import com.himart.showroom.network.APIConstants;

import java.text.NumberFormat;

public class CommonHelper {
    public static void showDialog(Context context, String msg, String actionTitle, final View.OnClickListener positiveCallback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setNegativeButton(context.getString(com.himart.showroom.R.string.cancel), new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (actionTitle != null && positiveCallback != null) {

            builder.setPositiveButton(actionTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveCallback.onClick(null);
                    dialog.dismiss();
                }
            });
        }

        builder.show();
    }

    public static void showDialogWithNegative(Context context, String msg, String actionTitle, final View.OnClickListener positiveCallback, final View.OnClickListener negativeCallback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(msg);

        if (actionTitle != null && positiveCallback != null) {

            builder.setPositiveButton(actionTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positiveCallback.onClick(null);
                    dialog.dismiss();
                }
            });
        }

        if (actionTitle != null && negativeCallback != null) {
            builder.setNegativeButton(context.getString(com.himart.showroom.R.string.finish), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    negativeCallback.onClick(null);
                    dialog.dismiss();
                }
            });
        }

        builder.setCancelable(false);
        builder.show();
    }

    public static String moneyFormatter(int number) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(Integer.valueOf(number)) + "원";
    }

//    public static String imageUrlFormatter(Product product, int tempPosition) {
//        return APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + product.getProductImageArrayList().get(tempPosition).getImageUrl();
//    }

    public static String imageUrlFormatter(Product product, int tempPosition) {
        if (product.getProductImageArrayList().size() > 0) {
            if (AppManager.isDebug) {
                return APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + product.getProductImageArrayList().get(tempPosition).getImageUrl();
            } else {
                return APIConstants.ROOT_URL_PRODUCTION + AppManager.meta.getRootUrl() + product.getProductImageArrayList().get(tempPosition).getImageUrl();
            }
        } else {
            return APIConstants.ROOT_URL_DEVELOPMENT;
        }
    }

    public static String thumbnailUrlFormatter(Product product, int tempPosition) {

        if (product.getProductImageArrayList().size() > 0) {
            if (AppManager.isDebug) {
                return APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + product.getProductImageArrayList().get(tempPosition).getSmallImageUrlName();
            } else {
                return APIConstants.ROOT_URL_PRODUCTION + AppManager.meta.getRootUrl() + product.getProductImageArrayList().get(tempPosition).getSmallImageUrlName();
            }
        } else {
            return APIConstants.ROOT_URL_DEVELOPMENT;
        }

    }

}
