package controllers;

import java.security.SecureRandom;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	public static HashMap<String,WebSocket.Out<String>> hm = new HashMap<>();

    public static Result index() {
    	SecureRandom random = new SecureRandom();
    	byte[] bytes = new byte[32];
    	random.nextBytes(bytes);
    	String sid = Base64.encodeBase64URLSafeString(bytes);
        return ok(index.render(sid));
    }
    
    public static WebSocket<String> application(final String sid) {
 
        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {    
            	hm.put(sid, out);
                in.onMessage(new Callback<String>() {
                    public void invoke(String event) {

                    }
                });

                in.onClose(new Callback0() {
                    public void invoke() {
                    	hm.remove(sid);
                    }
                });
            }

        };
    }
    
    public static Result controller(String sid){
    	return ok(controller.render(sid));
    }
    
    public static WebSocket<String> con() {
        return new WebSocket<String>() {
            public void onReady(final WebSocket.In<String> in, final WebSocket.Out<String> out) {              
                in.onMessage(new Callback<String>() {
                    public void invoke(String event) {
                    	JsonNode message = play.libs.Json.parse(event);
                    	WebSocket.Out<String> application = hm.get(message.get("sid").asText());
                    	application.write(message.get("command").asText());
                    }
                });

                in.onClose(new Callback0() {
                    public void invoke() {
                    }
                });
            }

        };
    }
    
    
}
