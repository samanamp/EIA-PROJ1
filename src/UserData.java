import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserData {
	
	private String _id;
	private String _rev;
	private String token;
	private String password;
	private long confirmationTimestamp = 0;
	private boolean confirmed = false;
	private long reminderTimestamp = 0;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public UserData(){}
	public UserData(String email, String _rev, String token, String password,
			long confirmationTimestamp, boolean confirmed,
			long reminderTimestamp) {
		super();
		this._id = email;
		this._rev = _rev;
		this.token = token;
		this.password = password;
		this.confirmationTimestamp = confirmationTimestamp;
		this.confirmed = confirmed;
		this.reminderTimestamp = reminderTimestamp;
	}
//	public UserData(String _id, String _rev, String email, String token, String password,
//			long confirmationTimestamp, boolean confirmed,
//			long reminderTimestamp) {
//		super();
//		this._id = _id;
//		this._rev = _rev;
//		this.email = email;
//		this.token = token;
//		this.password = password;
//		this.confirmationTimestamp = confirmationTimestamp;
//		this.confirmed = confirmed;
//		this.reminderTimestamp = reminderTimestamp;
//	}
	
//	public String getId() {
//		return _id;
//	}
	
	public String getRev() {
		return _rev;
	}

	public String getEmail() {
		return _id;
	}

	public void setEmail(String email) {
		this._id = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getConfirmationTimestamp() {
		return confirmationTimestamp;
	}

	public void setConfirmationTimestamp(long confirmationTimestamp) {
		this.confirmationTimestamp = confirmationTimestamp;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public long getReminderTimestamp() {
		return reminderTimestamp;
	}

	public void setReminderTimestamp(long reminderTimestamp) {
		this.reminderTimestamp = reminderTimestamp;
	}
	
	//TODO new function
	public static boolean isValidEmail(String email) {
		if(email == null || email.equals(""))
			return false;
		
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
	
//	public static boolean ifValidEmailAddress(String emailAddress) {
//
//	    Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)\\.]+[(a-zA-z)]{2,3}$");
//	    Matcher regMatcher   = regexPattern.matcher(emailAddress);
//	    if(regMatcher.matches()){
//	        return true;
//	    } else {
//	    return false;
//	    }
//	}

}
