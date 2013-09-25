import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class Reminder
 */
@WebServlet("/Reminder")
public class Reminder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final long timeBetweenTries = 300000;

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
		PrintWriter out = response.getWriter();
		JSONObject res = appLogic(request);

		// respond to the web browser
		response.setContentType("application/json");
		out.print(res);
		out.close();

	}

	private synchronized JSONObject appLogic(HttpServletRequest request) throws IOException {
		// if email address is invalid?
		JSONObject res = new JSONObject();
		try {
			String email = request.getParameter("email");
			if (UserData.isValidEmail(email)) {
				DBHandler dbHandler = new DBHandler();
				UserData userData;
				if (!dbHandler.ifUserExists(email))
					throw new Exception(
							"Email Doesn't exist, you may probably want to Register");
				
				userData = dbHandler.getUser(email);
				if (!userData.isConfirmed())
					throw new Exception("The email should be confirmed at first");
				
				SendPassword.sendReminder(userData, timeBetweenTries);
				
				userData.setReminderTimestamp(System.currentTimeMillis());
				
				dbHandler.updateObject(userData);
				res.put("success", true);
			} else{
				res.put("success", false);
				res.put("error", "Wrong Email Address");
			}
		} catch (Exception e) {
			res.put("success", false);
			res.put("error", e.getMessage());
		}
		return res;
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
