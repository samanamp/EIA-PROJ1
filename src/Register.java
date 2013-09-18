

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String serverEmailAddress = "samana@student.unimelb.edu.au";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
        out.println("Hello World");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String email = request.getParameter("email");
		if(validEmailAddress(email)){
		
			UserData newUser = new UserData();
			newUser.setEmail(email);

			/********Send Confirmation Link***************/			
			String token = SecureGen.generateSecureString(32);
			newUser.setToken(token);
			// TODO Output to user's interface
			String confirmMessage = "Please confirm your registration by clicking on following link: \n" + "<a href=\"http://" + request.getLocalAddr() + ":8080/proj1/Confirm?token="+token+"\">Click me!</a>";
			EmailHandler emailHandler = new EmailHandler(email, "Account Confirmation", confirmMessage);
			emailHandler.start();
			
			newUser.setConfirmationTimestamp(System.currentTimeMillis());
			/*******Save to DB**************************/
			CouchDbClient dbClient = new CouchDbClient("newproj1db", true, "http", "127.0.0.1", 5984, "saman", "123");
			Gson gson = new Gson();
			String jsonString = gson.toJson(newUser);
			JsonObject jsonobj = dbClient.getGson().fromJson(jsonString, JsonObject.class);
			jsonobj.addProperty("_id", token);
			dbClient.save(jsonobj);
			
			out.println("Email:" + email);			
		}else
			out.println("Wrong Mail Address");
			
	}
	
	public boolean validEmailAddress(String emailAddress) {

	    Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
	    Matcher regMatcher   = regexPattern.matcher(emailAddress);
	    if(regMatcher.matches()){
	        return true;
	    } else {
	    return false;
	    }
	}

}
