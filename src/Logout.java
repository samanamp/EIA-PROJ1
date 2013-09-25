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
@WebServlet("/Logout")
public class Logout extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DBHandler dbh;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Logout() {
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
			HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject res = new JSONObject();
		HttpSession session = request.getSession(false);
		boolean success = true;
		String errorMessage = null;
		String errorType = null;
		String token = null;
		String email = null;
		
		try {
			
			if(session == null)
				throw new SecurityException("Invalid session please login again.");
			
			email = (String) session.getAttribute("email");
			
			if(email == null)
				throw new SecurityException("Invalid session please login again.");
			
			if (!UserData.isValidEmail(email))
				throw new IllegalArgumentException("Invalid account.");

			UserData ud = dbh.getByEmail(email);
			boolean isValidAccount = ud != null && ud.isConfirmed();
			
			if(!isValidAccount)
				throw new SecurityException("Invalid account please login again.");
			
			token = (String) session.getAttribute(UserData.generateSessionToken(ud.getEmail()));
			
			if (token == null)
				throw new SecurityException("Invalid token please login again.");				
			
			session.removeAttribute(token);
			session.removeAttribute("email");
			session.invalidate();
			
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
		}  finally {
			if (errorMessage != null) {
				// TODO save error in the DB
				success = false;
				res.put("error", errorMessage);
			}

			res.put("success", success);
		}

		return res;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	// TODO
	public String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

}
