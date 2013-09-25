
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;

/**
 * Servlet implementation class Reset
 */
@WebServlet("/Delete")
public class Delete extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private DBHandler dbh;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Delete() {
        super();
        dbh = new DBHandler();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// create a JSON object (using json-simple)
		JSONObject res = executeSynchronizedLogic(request);
		
		// respond to the web browser
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(res);
		out.close();
	}
	
	public synchronized JSONObject executeSynchronizedLogic(HttpServletRequest request) {
		
		JSONObject res = new JSONObject();
		
		try {
			//Verify the session
			String email = (String) request.getSession().getAttribute("email");
			String token = "";
			if (email != null) {
				//Validate the user session
				token = UserData.generateSessionToken(email);
				if (request.getSession().getAttribute(token) == null) {
					res.put("success", false);
					res.put("error", "No session found.");
					return res;
				}
			} else {
				res.put("success", false);
				res.put("error", "No session found.");
				return res;	
			}
			
			
			//TODO verify with professor
			//String email = request.getParameter("email");
			
			if (email == null) {
				res.put("success", false);
				res.put("error", "No e-mail was defined.");
				return res;
			} else {
				// Validate if the e-mail is valid.
				boolean result = true;
				try {
					InternetAddress emailAddr = new InternetAddress(email);
					emailAddr.validate();
				} catch (javax.mail.internet.AddressException ex) {
					res.put("success", false);
					res.put("error", "Invalid e-mail address.");
					return res;
				}
			}
						
			//Delete the user account.
			try {
				dbh.deleteUser(email);
			} catch (NoDocumentException nde) {
				res.put("success", false);
				res.put("error", nde.getMessage());
				dbh.writeError("NoDocumentException", nde);
				return res;
			} catch (DocumentConflictException dce) {
				res.put("success", false);
				res.put("error", dce.getMessage());
				dbh.writeError("DocumentConflictException", dce);
				return res;
			}
			
			//Log-out the session
			request.getSession().removeAttribute(token);
			request.getSession().removeAttribute("email");
			request.getSession().invalidate();
			
			res.put("success",  true);
		} catch (Exception e) {
			res.put("success", false);
			res.put("error", "Unknown error at the Server: " + getStackTrace(e));
			try {
				dbh.writeError("Exception", e);
			} catch (Exception e2) {
				System.err.println("Error writing exception to the database");
			}
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}