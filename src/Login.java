import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;

/**
 * Servlet implementation class Chat
 */
@WebServlet("/Login")
public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DBHandler dbh = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		String errorMessage = null;
		String errorType = null;
		try{
			dbh = new DBHandler();
		}catch(CouchDbException cdbe){
			//TODO save error in a log file 
			errorMessage = cdbe.getMessage();
			errorType = cdbe.getClass().getSimpleName();
		} catch(Exception e){
			errorMessage = e.getMessage();
			errorType = e.getClass().getSimpleName();
		}	
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// create a JSON object (using json-simple)
		JSONObject res = executeSynchronizedLogic(request);

		// respond to the web browser
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(res);
		out.close();
	}

	@SuppressWarnings("unchecked")
	public synchronized JSONObject executeSynchronizedLogic(
			HttpServletRequest request) {
		
		JSONObject res = new JSONObject();
		String errorMessage = null;
		String errorType = null;
		boolean success = true;
		UserData ud = null;
		
		try {
			//TODO add this to all servlets
			if (dbh == null)
				throw new CouchDbException("No DB connection");
			
			// Get session and create a new one if one doesn't exist
//			HttpSession session = request.getSession(true);
			String token = null;
			String email = request.getParameter("email");
			String password = request.getParameter("password");

			boolean isPasswordValid = password != null && !password.equals("");

			if (!UserData.isValidEmail(email) || !isPasswordValid)
				throw new IllegalArgumentException(
						"Please provide a valid email and password.");

			try{
				ud = dbh.getUser(email);
			} catch(CouchDbException e){
				throw new IllegalArgumentException(
						"The email you entered does not belong to any account."
						+ "To create an account click " 
						+ "<span id=\"lnkRegisterHere\"><a href=\"javascript:void(0)\">here</a></span>");
			}
//			if (ud == null)
//				throw new IllegalArgumentException(
//						"The email you entered does not belong to any account. You can create an account here.");

			if (!ud.isConfirmed())
				throw new SecurityException(
						"This account has not been activated.<br />"
								+ "Please check your email inbox for the confirmation email and click the confirmation link.<br />"
								+ "If you have not received the confirmation email click "
								+ "<span id=\"lnkResendConfirmation\"><a href=\"javascript:void(0)\">here</a></span>"
								+ " to resend it.");

			if (!password.equals(ud.getPassword()))
				throw new IllegalArgumentException(
						"Incorrect Email/Password Combination.");

			// TODO the specs say that we have to save the session_token in the
			// DB is that really necessary?
			token = UserData.generateSessionToken(email);
			dbh.saveToken(ud.getEmail(), token);
			res.put("token", token);
		
		} catch (IllegalArgumentException iae) {
			errorMessage = iae.getMessage();
		} catch (SecurityException se) {
			errorMessage = se.getMessage();
		} catch (Exception e) {
			// This should never happen
			errorMessage = e.getMessage();
			errorType = e.getClass().getSimpleName();
			try {
				dbh.writeError(errorType, e);
			} catch(Exception e1){
				//TODO write the error in a log file
			}
		} finally {
			if (errorMessage != null) {
				success = false;
				res.put("error", errorMessage);
			}

			res.put("success", success);
		}

		return res;
	}

	public String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
