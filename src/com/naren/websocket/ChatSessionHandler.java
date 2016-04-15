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
import javax.websocket.Session;

import org.json.simple.JSONObject;

import com.naren.model.Chat;
import com.naren.model.Type;

/**
 * @author nstanwar
 *
 */
@ApplicationScoped
public class ChatSessionHandler {

	private final Set<Session> sessions = new HashSet<>();
	private final Set<Chat> chats = new HashSet<>();
	private final Set<User> users = new HashSet<>();
	/**
	 * 
	 */
	public ChatSessionHandler() {
		// TODO Auto-generated constructor stub
	}

	public void addSession(Session session) {
		sessions.add(session);
        for (Chat device : chats) {
            JSONObject addMessage = createAddMessage(device);
            sendToSession(session, addMessage);
        }
	}

	public void removeSession(Session session) {
		sessions.remove(session);
	}
	public List getDevices() {
		return new ArrayList<>(chats);
	}

	public void addDevice(Chat chat) {
//		UUID Id = UUID.randomUUID();
//		chat.setId(Id.toString());
		chats.add(chat);
        JSONObject addMessage = createAddMessage(chat);
        sendToAllConnectedSessions(addMessage);
	}
	public void addUser(User user) {
//		UUID Id = UUID.randomUUID();
//		chat.setId(Id.toString());
		users.add(user);
        JSONObject addMessage = createAddMessage(user);
        sendToAllConnectedSessions(addMessage);
	}
	public void removeDevice(String id) {
		Chat chat = getChatById(id);
        if (chat != null) {
            chats.remove(chat);
            JSONObject removeMessage = new JSONObject();      
            removeMessage.put("action", "remove");
            removeMessage.put("id", id);
            sendToAllConnectedSessions(removeMessage);
        }
	}

	private Chat getChatById(String id) {
        for (Chat chat : chats) {
            if (chat.getId() == id) {
                return chat;
            }
        }
        return null;
    }

    private JSONObject createAddMessage(Type type) {
    	JSONObject addMessage = new JSONObject();  
    	if(type instanceof Chat){
    		Chat chat  = (Chat) type;
    		addMessage.put("action", "add");
            addMessage.put("id", chat.getId());
            addMessage.put("userName", chat.getUserName());
            addMessage.put("message", chat.getMessage());
            addMessage.put("acion", chat.getAction());
    	}else if (type instanceof User){
    		User user  = (User) type;
    		addMessage.put("action", "add");
            addMessage.put("id", user.getId());
            addMessage.put("userName", user.getUserName());
            addMessage.put("action", user.getAction());
    	}
            
        
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

	public void getUser(String id) {
		// TODO Auto-generated method stub
		
	}

	public void getAllUsers() {
		// TODO Auto-generated method stub
		
	}

}
