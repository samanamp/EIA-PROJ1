import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SendPassword {
	public static int sendNewPasswordForID(String id, String serverEmailAddress,
			CouchDbClient dbClient) throws AddressException, MessagingException, NoDocumentException, IllegalArgumentException {

			// set new password
			UserData udata = dbClient.find(UserData.class, id);
			String email = udata.getEmail();
			String password = SecureGen.generateSecureString(12);
			udata.setPassword(password);
			// update the db
			Gson gson = new Gson();
			String jsonString = gson.toJson(udata);
			JsonObject jsonobj = dbClient.getGson().fromJson(jsonString,
					JsonObject.class);
			dbClient.update(jsonobj);
			// Send email address
			String emailMessage = "The password for your proj1 account is: "
					+ password + "\n Keep it secret, keep it safe.";
			EmailHandler emailHandler = new EmailHandler(email, "Account Password",
					emailMessage);
			emailHandler.start();

		
		return 0;
	}
}
