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
	private DBHandler dbh;

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
			//TODO save error in DB not 
			errorMessage = cdbe.getMessage();
			errorType = cdbe.getClass().getSimpleName();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// create a JSON object (using json-simple)
		JSONObject res = executeSynchronizedLogic(request, response);

		// respond to the web browser
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(res);
		out.close();
	}

	@SuppressWarnings("unchecked")
	public synchronized JSONObject executeSynchronizedLogic(
			HttpServletRequest request, HttpServletResponse response) {

		JSONObject res = new JSONObject();
		String errorMessage = null;
		String errorType = null;
		boolean success = true;
		// Get session and create a new one if one doesn't exist
		HttpSession session = request.getSession(true);
		String token = null;
		
		try {
			
			String email = request.getParameter("email");
			String password = request.getParameter("password");

			boolean isPasswordValid = password != null && !password.equals("");

			if (!UserData.isValidEmail(email) || !isPasswordValid)
				throw new IllegalArgumentException(
						"Please provide a valid email and password.");

			UserData ud = dbh.getByEmail(email);

			if (ud == null)
				throw new IllegalArgumentException(
						"The email you entered does not belong to any account. You can create an account here.");

			if (!ud.isConfirmed())
				throw new SecurityException(
						"This account has not been activated.<br />"
								+ "Please check your email inbox for the confirmation email and click the confirmation link.<br />"
								+ "If you have not received the confirmation email click here to resend it.");

			if (!password.equals(ud.getPassword()))
				throw new IllegalArgumentException(
						"Incorrect Email/Password Combination.");

			// TODO the specs say that we have to save the session_token in the
			// DB is that really necessary?
			token = UserData.generateSessionToken(email);
			if (session.getAttribute(token) == null) {
				session.setAttribute(token, token);
				session.setAttribute("email", email);
				// dbh.saveToken(ud.getId(), token);
			}
						
		} catch (IllegalArgumentException iae) {
			errorMessage = iae.getMessage();
			errorType = iae.getClass().getSimpleName();
		} catch (SecurityException se) {
			errorMessage = se.getMessage();
			errorType = se.getClass().getSimpleName();
		} catch (DocumentConflictException dce) {
			errorMessage = dce.getMessage();
			errorType = dce.getClass().getSimpleName();
		} catch (Exception e) {
			// This should never happen
			errorMessage = e.getMessage();
			errorType = e.getClass().getSimpleName();
		} finally {
			if (errorMessage != null) {
				// TODO save error in the DB
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
