
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("Hello World");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try{
		appLogic(request,response);
		}catch(Exception e){
			out.println(e.getMessage());
		}
	}
	
	private synchronized void appLogic(HttpServletRequest request, HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		String email = request.getParameter("email");
		
		if (UserData.isValidEmail(email)) {
			DBHandler dbHandler = new DBHandler();
			UserData newUser = new UserData();
			newUser.setEmail(email);
			if(dbHandler.ifUserExists(email))
				out.println("This email has registered before");
			else{
			/******** Send Confirmation Link ***************/
			String token = SecureGen.generateSecureString(32);
			newUser.setToken(token);
			
			String confirmMessage = "Please confirm your registration by clicking on following link: \n"
					+ "<a href=\"http://"
					+ request.getLocalAddr()
					+ ":8080/proj1/Confirm?token=" + token + "\">Click me!</a>";
			EmailHandler emailHandler = new EmailHandler(email,
					"Account Confirmation", confirmMessage);
			emailHandler.start();

			newUser.setConfirmationTimestamp(System.currentTimeMillis());
			/******* Save to DB **************************/
			
			dbHandler.addNewUser(newUser);
			dbHandler.closeConnection();

			out.println("Registration Successful:" + email);
			}
		} else
			out.println("Wrong Mail Address");
	}

}
