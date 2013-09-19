import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.lightcouch.NoDocumentException;

public class SendPassword {
	public static void sendNewPasswordForUser(UserData udata)
			throws AddressException, MessagingException, NoDocumentException,
			IllegalArgumentException {

		// set new password
		String email = udata.getEmail();
		String password = SecureGen.generateSecureString(12);
		udata.setPassword(password);

		// Send email address
		String emailMessage = "The password for your proj1 account is: "
				+ password + "\n Keep it secret, keep it safe.";
		EmailHandler emailHandler = new EmailHandler(email, "Account Password",
				emailMessage);
		emailHandler.start();

	}
}
