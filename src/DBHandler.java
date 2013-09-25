import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DBHandler {

	private CouchDbClient dbClient;
	private Gson gson;

	public DBHandler() {
		dbClient = new CouchDbClient("proj1", true, "http", "localhost", 5984,
				null, null);
		gson = new Gson();
	}

	/**
	 * Sets the password of a given user to a new randomly generated password.
	 * 
	 * @param The
	 *            e-mail the user is registered with
	 * @throws NoDocumentException
	 *             If the user defined by the e-mail does not exist.
	 * @throws DocumentConflictException
	 *             If there was a conflict while updating the object.
	 */
	public void resetPassword(String userEmail)
			throws DocumentConflictException, NoDocumentException {

		UserData user = this.getUser(userEmail);

		String newPassword = (new BigInteger(130,
				new java.security.SecureRandom())).toString(12);
		user.setPassword(newPassword);

		String jsonString = gson.toJson(user);
		JsonObject jsonobj = dbClient.getGson().fromJson(jsonString,
				JsonObject.class);
		dbClient.update(jsonobj);
	}

	/**
	 * Deletes the user data related to the given e-mail.
	 * 
	 * @param The
	 *            e-mail the user is registered with
	 * @throws NoDocumentException
	 *             If the user defined by the e-mail does not exist.
	 * @throws DocumentConflictException
	 *             If there was a conflict while deleting the object.
	 */
	public void deleteUser(String userEmail) throws DocumentConflictException,
			NoDocumentException {

		UserData user = this.getUser(userEmail);

		String jsonString = gson.toJson(user);
		JsonObject jsonobj = dbClient.getGson().fromJson(jsonString,
				JsonObject.class);
		dbClient.remove(jsonobj);
	}

	/**
	 * Returns the UserData Object given the user e-mail if it exists.
	 * 
	 * @param userEmail
	 * @return The UserData Object. If not found then null is returned.
	 * @throws NoDocumentException
	 *             In case there is no user with the specified e-mail.
	 */
//	public UserData getUser(String userEmail) throws NoDocumentException {
//
//		List<UserData> users = dbClient.view("reset/user_list")
//				.includeDocs(true).query(UserData.class);
//		UserData user = null;
//		for (int i = 0; i < users.size(); i++) {
//			if (users.get(i).getEmail().toLowerCase()
//					.equals(userEmail.toLowerCase())) {
//				user = users.get(i);
//				break;
//			}
//		}
//
//		if (user == null)
//			throw new NoDocumentException("The user " + userEmail
//					+ " is not defined");
//		return user;
//	}

	/**
	 * Writes errors into the database
	 * 
	 * @param type
	 *            The type of the error, which is the type of the Exception
	 * @param t
	 *            The exception itself.
	 * @throws DocumentConflictException
	 *             If there was a conflict while updating the object.
	 */
	public void writeError(String type, Throwable t)
			throws DocumentConflictException {
		Error error = new Error();
		error.setType(type);
		error.setMessage(t.getMessage());
		error.setDetail(getStackTrace(t));

		String jsonString = gson.toJson(error);
		JsonObject jsonobj = dbClient.getGson().fromJson(jsonString,
				JsonObject.class);
		dbClient.save(jsonobj);
	}

	/**
	 * Get user by e-mail
	 * 
	 * @param email
	 * @return
	 */
//	public UserData getByEmail(String email) {
//		List<UserData> list = dbClient.view("login/email").key(email).limit(1)
//				.includeDocs(true).query(UserData.class);
//		try {
//			return list.get(0);
//		} catch (IndexOutOfBoundsException iob) {
//			return null;
//		}
//
//	}
	public UserData getUser(String email) {
		UserData ud = dbClient.find(UserData.class, email);
		
		return ud;
	}
	
	// TODO added function (confirm we need this)
	public boolean saveToken(String email, String token) {
		UserData ud = dbClient.find(UserData.class, email);
		ud.setToken(token);
		dbClient.update(ud);
		
		ud = dbClient.find(UserData.class, email);
		if(ud.getToken().equals(token)) {
			return true;
		} else {
			return false;
		}
	}

	// TODO confirm we need this
	public boolean removeToken(String email, String token) {
		UserData ud = dbClient.find(UserData.class, email);
		if (ud.getToken().equals(token)) {
			ud.setToken(null);
			dbClient.update(ud);
		}
		
		ud = dbClient.find(UserData.class, email);
		
		if(ud.getToken() == null) {
			return true;
		} else {
			return false;
		}
	}

	/* To transform the Stack Trace into a String */
	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

}