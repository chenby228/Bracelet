package kpy.chat.netty.star.client.factory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MessageFactory {

    public static String createRegisterMessage(String userId){
        JSONObject register = new JSONObject();
        JSONObject head = new JSONObject();
        try {
            head.put("msgType", "register")
                    .put("msgId", new Date().getTime())
                    .put("userId", userId);
            register.put("head", head);
            return register.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createSendMessage(String userId, String toUserId, String sendMsgValue){
        JSONObject send = new JSONObject();
        JSONObject head = new JSONObject();
        JSONObject body = new JSONObject();
        if (userId.equals("admin")){
            toUserId = "admin2";
        }
        if (userId.equals("admin2")){
            toUserId = "admin";
        }
        try {
            head.put("msgType", "sendTo")
                    .put("forUser", userId)
                    .put("toUser", toUserId)
                    .put("msgId", new Date().getTime());
            body.put("value", sendMsgValue)
                    .put("time",new Date().toString());
            send.put("head", head)
                    .put("body", body);
            return send.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
