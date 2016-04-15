/**
 * 
 */
package com.naren.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.naren.model.Device;

/**
 * @author nstanwar
 *
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

	private static final Logger LOG = Logger.getLogger(DeviceWebSocketServer.class);
	//@Inject
	private static final DeviceSessionHandler sessionHandler = new DeviceSessionHandler();
	/**
	 * 
	 */
	public DeviceWebSocketServer() {
		// TODO Auto-generated constructor stub
	}

	@OnOpen
	public void open(Session session) {
		sessionHandler.addSession(session);
	}

	@OnClose
	public void close(Session session) {
	}

	@OnError
	public void onError(Throwable error) {
		LOG.debug("ERROR: "+error);;
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws ParseException {
		LOG.debug("Message : "+message);
		JSONObject json = (JSONObject)new JSONParser().parse(message);
		Gson gson = new Gson(); 
		final Device device = gson.fromJson(message, Device.class);           
		if ("add".equals(json.get(("action")))) ;
		device.setStatus("Off");
		sessionHandler.addDevice(device);

		if ("remove".equals(json.get(("action")))) {
			int id = (int) json.get("id");
			sessionHandler.removeDevice(id);
		}

		if ("toggle".equals(json.get(("action")))) {
			int id = (int) json.get("id");
			sessionHandler.toggleDevice(id);
		}

	}
}
