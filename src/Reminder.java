import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;

/**
 * Servlet implementation class Reminder
 */
@WebServlet("/Reminder")
public class Reminder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int timeBetweenTries = 5 * 60 * 1000;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Reminder() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// if email address is invalid?
		PrintWriter out = response.getWriter();
		String email = request.getParameter("email");
		try {
			if (validEmailAddress(email)) {
				UserData userData;
				if ((userData = ifEmailExists(out,email)) == null)
					throw new Exception(
							"Email Doesn't exist, you may probably want to Register");
				// if less than 5 minutes passes from last confirmation?
				if ((System.currentTimeMillis() - userData
						.getConfirmationTimestamp()) < timeBetweenTries)
					throw new Exception(
							"You have requested it less than 5 minutes ago! Try Again later");
				// send password
				EmailHandler emailHandler = new EmailHandler(email,
						"Email Reminder",
						"You have requested a password reminder, your password is:\n"
								+ userData.getPassword());
				emailHandler.start();

			} else
				out.println("Wrong Mail Address");
		} catch (Exception e) {
			e.printStackTrace(out);
		}

	}

	private UserData ifEmailExists(PrintWriter out,String email) {
		
		CouchDbClient dbClient = new CouchDbClient("newproj1db", true, "http",
				"127.0.0.1", 5984, "saman", "123");
		List<UserData> list = dbClient.view("_all_docs").includeDocs(true).query(UserData.class);
		int listSize = list.size();
		for(int i = 0;i<listSize;i++){
			if(list.get(i).getEmail().equalsIgnoreCase(email))
				return list.get(i);
		}		
			
		return null;
	}

	public boolean validEmailAddress(String emailAddress) {

		Pattern regexPattern = Pattern
				.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
		Matcher regMatcher = regexPattern.matcher(emailAddress);
		if (regMatcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
