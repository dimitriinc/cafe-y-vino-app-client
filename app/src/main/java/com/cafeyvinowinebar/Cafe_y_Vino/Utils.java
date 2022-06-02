package com.cafeyvinowinebar.Cafe_y_Vino;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Utils {

    // constants for the messaging action keys
    public static final String ACTION_PUERTA = "puerta";
    public static final String ACTION_RESERVA = "reserva";
    public static final String ACTION_PEDIDO = "pedido";
    public static final String ACTION_CUENTA = "cuenta";
    public static final String ACTION_REGALO = "regalo";
    public static final String ACTION_PUERTA_ADMIN = "puerta_admin";
    public static final String ACTION_RESERVA_ACK = "reserva_ack";
    public static final String ACTION_RESERVA_NACK = "reserva_nack";
    public static final String ACTION_PEDIDO_ADMIN = "pedido_admin";
    public static final String ACTION_CUENTA_ACK = "cuenta_ack";
    public static final String ACTION_CUENTA_ADMIN = "cuenta_admin";
    public static final String ACTION_REGALO_ADMIN = "regalo_admin";
    public static final String ACTION_MSG = "msg";

    // keys for data transferring
    public static final String KEY_TOKEN = "token";
    public static final String KEY_FECHA = "fecha";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_MESA = "mesa";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_REGALO = "regalo";
    public static final String KEY_ACTION = "action";
    public static final String KEY_MODO = "modo";
    public static final String KEY_HORA = "hora";
    public static final String KEY_PAX = "pax";
    public static final String KEY_COMENTARIO = "comentario";
    public static final String KEY_PARTE = "parte";
    public static final String KEY_PART = "part";
    public static final String KEY_CATEGORIA = "categoria";
    public static final String TAG = "tag";
    public static final String KEY_DATE = "date";
    public static final String KEY_TYPE = "type";
    public static final String KEY_NAME = "name";
    public static final String KEY_BONO = "bono";
    public static final String KEY_FECHA_DE_NACIMIENTO = "fecha de nacimiento";
    public static final String KEY_ICON = "icon";
    public static final String KEY_LLEGADO = "llegado";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_CONFIRMADO = "confirmado";
    public static final String KEY_META_ID = "metaDocId";
    public static final String KEY_ADMIN_TOKEN = "adminToken";

    // values
    public static final String TO_ADMIN_NEW = "toAdminNew";
    public static final String BARRA = "barra";
    public static final String IS_PRESENT = "isPresent";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String GMT = "GMT-5";
    public static final String DIA = "dia";
    public static final String NOCHE = "noche";
    public static final String CASH = "efectivo";
    public static final String POS = "POS";
    public static final String YAPE = "Yape";
    public static final String CRIPTO = "Cripto";

    // firestore keys
    public static final String TOTAL = "total";
    public static final String SERVIDO = "servido";
    public static final String SERVIDO_BARRA = "servidoBarra";
    public static final String SERVIDO_COCINA = "servidoCocina";
    public static final String KEY_IS_EXPANDED = "isExpanded";
    public static final String KEY_BONOS = "bonos";
    public static final String TELEFONO = "telefono";
    public static final String TELEFONO_ACCENT = "teléfono";
    public static final String CAT_PATH = "catPath";
    public static final String PRECIO = "precio";
    public static final String USER = "user";
    public static final String EMAIL = "email";
    public static final String KEY_USER = "user";


    public static final String CAN_SEND_PEDIDOS = "canSendPedidos";

    public static boolean getCanSendPedidos(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(CAN_SEND_PEDIDOS, false);
    }

    public static void setCanSendPedidos(Context context, boolean canSendPedidos) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(CAN_SEND_PEDIDOS, canSendPedidos)
                .apply();
    }

    public static void setIsUserPresent(Context context, boolean isPresent) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_PRESENT, isPresent)
                .apply();
    }

    public static String getMessageId() {
        return "m-" + new Random().nextLong();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(GMT));
        return sdf.format(new Date());
    }


}
