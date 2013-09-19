import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		appLogic(request, response);

	}

	private synchronized void appLogic(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// if email address is invalid?
		PrintWriter out = response.getWriter();
		try {
			String email = request.getParameter("email");
			if (UserData.ifValidEmailAddress(email)) {
				DBHandler dbHandler = new DBHandler();
				UserData userData;
				if (!dbHandler.ifUserExists(email))
					throw new Exception(
							"Email Doesn't exist, you may probably want to Register");
				
				userData = dbHandler.findByEmail(email);
				if (!userData.isConfirmed())
					throw new Exception("The email should be confirmed at first");
				
				SendPassword.sendReminder(userData, timeBetweenTries);
				
				userData.setReminderTimestamp(System.currentTimeMillis());
				
				out.println(System.currentTimeMillis());
				dbHandler.updateObject(userData);

			} else
				out.println("Wrong Mail Address");
		} catch (Exception e) {
			
			out.println(e.getMessage());
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
