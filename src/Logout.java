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
	private DBHandler dbh = null;

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
		boolean success = true;
		String errorMessage = null;
		String errorType = null;
		
		try {
			//TODO add this to all servlets
			if (dbh == null)
				throw new CouchDbException("No DB connection");
			
			String token = request.getParameter("token");
			String email = request.getParameter("email");

			boolean isValidToken = token != null && !token.equals("");
			
			if (!UserData.isValidEmail(email) || !isValidToken)
				throw new SecurityException("Invalid session please login again.");

			UserData ud = dbh.getUser(email);
			boolean isValidAccount = ud != null && ud.isConfirmed();
			
			if(!isValidAccount)
				throw new SecurityException("Invalid account please login again.");
			
			if (!token.equals(ud.getToken()))
				throw new SecurityException("Invalid token please login again.");				
			
			success = dbh.removeToken(email, token);
			
			//this should never happen but just 
			if(!success)
				throw new Exception("Internal error.");
		
		} catch (IllegalArgumentException iae) {
			errorMessage = iae.getMessage();
			errorType = iae.getClass().getSimpleName();
		} catch (SecurityException se) {
			errorMessage = se.getMessage();
			errorType = se.getClass().getSimpleName();
		} catch (CouchDbException cdbe) {
			errorMessage = cdbe.getMessage();
			errorType = cdbe.getClass().getSimpleName();
		} catch (Exception e) {
			// This should never happen
			errorMessage = e.getMessage();
			errorType = e.getClass().getSimpleName();
			try {
				dbh.writeError(errorType, e);
			} catch(Exception e1){
				//TODO write the error in a log file
			}
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

}
