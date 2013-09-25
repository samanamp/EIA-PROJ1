import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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
@WebServlet("/verifySession")
public class verifySession extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DBHandler dbh;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public verifySession() {
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
		HttpSession session = request.getSession(false);
		String token = null;
		String email = null;
		
		try {
			
			String current = (String) request.getParameter("current");
			
			if(session == null)
				throw new SecurityException("Invalid session please login again.");
			
			email = (String) session.getAttribute("email");
			
			if(email == null)
				throw new SecurityException("Invalid session please login again.");
					
			UserData ud = dbh.getByEmail(email);
			token = (String) session.getAttribute(UserData.generateSessionToken(ud.getEmail()));

			if(token != null && session.getAttribute(token) != null){
				if(current.equals("login.html"))
					res.put("redirect", "home.html");
				else
					res.put("redirect", current);
			} else {
				res.put("redirect", "login.html");
			}
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			res.put("redirect", "login.html");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			res.put("redirect", "login.html");
		} catch(Exception e){
			res.put("redirect", "login.html");
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
