
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.HttpHostConnectException;
import org.json.simple.JSONObject;

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
		
		
		JSONObject res = appLogic(request);

		// respond to the web browser
		response.setContentType("application/json");
		out.print(res);
		out.close();
		
		}catch(HttpHostConnectException e){
			JSONObject res = new JSONObject();
			res.put("success", false);
			res.put("error", "Couldn't connect to database, please try again later!");
			response.setContentType("application/json");
			out.print(res);
			out.close();
		}catch(Exception e){
			JSONObject res = new JSONObject();
			DBHandler dbHandler = new DBHandler();
			dbHandler.addNewError(new Error("Register servlet", "General Error", e.getMessage()));
			res.put("success", false);
			res.put("error", "General error occured please contact administrator!");
			response.setContentType("application/json");
			out.print(res);
			out.close();
		}
	}
	
	private synchronized JSONObject appLogic(HttpServletRequest request) throws HttpHostConnectException, Exception{
		JSONObject res = new JSONObject();
		String email = request.getParameter("email");
		
		if (UserData.isValidEmail(email)) {
			
			DBHandler dbHandler = new DBHandler();
			UserData newUser = new UserData();
			newUser.setEmail(email);
			if(dbHandler.ifUserExists(email)){
				res.put("success", false);
				res.put("error", "User has registered before with this email address");
			}
			else{
			/******** Send Confirmation Link ***************/
			String token = SecureGen.generateSecureString(32);
			newUser.setToken(token);
			
			String confirmMessage = "Please confirm your registration by clicking on following link: \n"
					+ "<a href=\"http://"
					+ request.getLocalAddr()
					+ ":8080/proj1/Confirm?token=" + token + "&email="+email+"\">Click me!</a>";
			EmailHandler emailHandler = new EmailHandler(email,
					"Account Confirmation", confirmMessage);
			emailHandler.start();

			newUser.setConfirmationTimestamp(System.currentTimeMillis());
			/******* Save to DB **************************/
			
			dbHandler.addNewUser(newUser);
			dbHandler.closeConnection();

			res.put("success", true);
			}
		} else{
			res.put("success", false);
		res.put("error", "Wrong Email Address");
		}
		return res;
	}

}
