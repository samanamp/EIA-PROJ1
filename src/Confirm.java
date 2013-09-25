import java.io.IOException;
import java.io.PrintWriter;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.HttpHostConnectException;
import org.lightcouch.NoDocumentException;

/**
 * Servlet implementation class Confirm
 */
@WebServlet("/Confirm")
public class Confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Confirm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try{
			appLogic(request, response);
		}catch(Exception e){
			e.printStackTrace(response.getWriter());
		}
	}
	
	private synchronized void appLogic(HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		String token = request.getParameter("token");
		String email = request.getParameter("email");
		
		try {
			DBHandler dbHandler = new DBHandler();
			
			if(!dbHandler.ifUserExists(email))
				throw new NoDocumentException("No document found with specified email");
			
			UserData userData = dbHandler.getUser(email);
			
			if(!userData.getToken().equalsIgnoreCase(token))
				throw new Exception("Token and Email doesn't match!");
			if(userData.isConfirmed())
				throw new Exception("You have confirmed it before!");
			
			userData.setConfirmed(true);
			out.println("User is confirmed, Checkout your email for password!");
			SendPassword.sendNewPasswordForUser(userData);
			dbHandler.updateObject(userData);

		} catch (NoDocumentException e) {
			out.println("You haven't registered yet with email: "+email);
		} catch (IllegalArgumentException e) {
			out.println("you should provide the token string!");
		} catch (AddressException e) {
			out.println("Error in email Address");
		} catch (MessagingException e) {
			out.println("Internal messaging error");
		} catch (HttpHostConnectException e){
			out.println("Can not connect to DB, please try again later!");
		} catch (Exception e){
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
