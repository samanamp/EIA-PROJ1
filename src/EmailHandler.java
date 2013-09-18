import javax.mail.MessagingException;


public class EmailHandler extends Thread {
	private final String serverEmailAddress = "samana@student.unimelb.edu.au";
	private String recipient, subject, message;
	public EmailHandler(String recipient, String subject, String message){
		this.recipient = recipient;
		this.subject = subject;
		this.message = message;
	}
	
	public void run(){
		int tryCount = 0;
		Email email = new Email();
		while(tryCount < 10){
			try{
			email.sendEmail(recipient, serverEmailAddress, subject, message);
			tryCount = 20;
			}catch(MessagingException e){
				tryCount++;
			}
			
		}
	}

}
