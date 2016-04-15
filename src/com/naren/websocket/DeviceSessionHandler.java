/**
 * 
 */
package com.naren.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import org.json.simple.JSONObject;

import com.naren.model.Device;

/**
 * @author nstanwar
 *
 */
@ApplicationScoped
public class DeviceSessionHandler {

	private int deviceId = 0;
	private final Set<Session> sessions = new HashSet<>();
	private final Set<Device> devices = new HashSet<>();
	/**
	 * 
	 */
	public DeviceSessionHandler() {
		// TODO Auto-generated constructor stub
	}

	public void addSession(Session session) {
		sessions.add(session);
        for (Device device : devices) {
            JSONObject addMessage = createAddMessage(device);
            sendToSession(session, addMessage);
        }
	}

	public void removeSession(Session session) {
		sessions.remove(session);
	}
	public List getDevices() {
		return new ArrayList<>(devices);
	}

	public void addDevice(Device device) {
		device.setId(deviceId);
        devices.add(device);
        deviceId++;
        JSONObject addMessage = createAddMessage(device);
        sendToAllConnectedSessions(addMessage);
	}

	public void removeDevice(int id) {
		Device device = getDeviceById(id);
        if (device != null) {
            devices.remove(device);
            JSONObject removeMessage = new JSONObject();      
            removeMessage.put("action", "remove");
            removeMessage.put("id", id);
            sendToAllConnectedSessions(removeMessage);
        }
	}

	public void toggleDevice(int id) {
		JsonProvider provider = JsonProvider.provider();
        Device device = getDeviceById(id);
        if (device != null) {
            if ("On".equals(device.getStatus())) {
                device.setStatus("Off");
            } else {
                device.setStatus("On");
            }
            JSONObject updateDevMessage = new JSONObject();      
            updateDevMessage.put("action", "toggle");
            updateDevMessage.put("id", device.getId());
            updateDevMessage.put("status", device.getStatus());
            sendToAllConnectedSessions(updateDevMessage);
        }
	}

	private Device getDeviceById(int id) {
        for (Device device : devices) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    private JSONObject createAddMessage(Device device) {
        JSONObject addMessage = new JSONObject();      
        addMessage.put("action", "add");
        addMessage.put("id", device.getId());
        addMessage.put("name", device.getName());
        addMessage.put("type", device.getType());
        addMessage.put("status", device.getStatus());
        addMessage.put("description", device.getDescription());
        return addMessage;
    }

    private void sendToAllConnectedSessions(JSONObject addMessage) {
        for (Session session : sessions) {
            sendToSession(session, addMessage);
        }
    }

    private void sendToSession(Session session, JSONObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
