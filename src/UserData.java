public class UserData {

	public String _id;
	public String _rev;
	private String email;
	private String token;
	private String password="";
	private long confirmationTimestamp = 0;
	private boolean confirmed = false;
	private long reminderTimestamp = 0;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	public String toString(){
		return _id + email;
	}

}
