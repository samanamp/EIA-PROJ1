import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class DBHandler {

	CouchDbClient dbClient;
	public DBHandler(){
		dbClient = new CouchDbClient("newproj1db", true, "http", "127.0.0.1", 5984, "saman", "123");
	}
	
	public void addNewUser(UserData newUser){
		Gson gson = new Gson();
		String jsonString = gson.toJson(newUser);
		JsonObject jsonobj = dbClient.getGson().fromJson(jsonString, JsonObject.class);
		jsonobj.addProperty("_id", newUser.getEmail());
		dbClient.save(jsonobj);
	}
	
	public boolean ifUserExists(String email){
		return dbClient.contains(email);
	}
	
	public UserData findByEmail(String email){
		return dbClient.find(UserData.class, email);
	}
	
	public UserData findByToken(String token) throws NoDocumentException, IllegalArgumentException{
		UserData ud = null;
		List<UserData> list = dbClient.view("_all_docs").includeDocs(true).query(UserData.class);
		int listSize = list.size();
		for(int i = 0;i<listSize;i++){
			if(list.get(i).getToken().equalsIgnoreCase(token))
				return list.get(i);
		}
		return ud;
	}
	
	public void updateObject(UserData userObject){
		Gson gson = new Gson();
		String jsonString = gson.toJson(userObject);
		JsonObject jsonobj = dbClient.getGson().fromJson(jsonString, JsonObject.class);
		dbClient.update(jsonobj);
	}
	public void closeConnection(){
		dbClient.shutdown();
	}
}
