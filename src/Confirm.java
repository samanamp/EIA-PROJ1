import java.io.IOException;
import java.io.PrintWriter;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		DBHandler dbHandler = new DBHandler();
		try {
			
			UserData userData = dbHandler.findByToken(token);
			if(userData == null)
				throw new NoDocumentException("No document found with specified token");
			if(userData.isConfirmed())
				throw new Exception("You have confirmed it before!");
			userData.setConfirmed(true);
			
			SendPassword.sendNewPasswordForUser(userData);
			
			dbHandler.updateObject(userData);
			out.println("Sent password Email");

		} catch (NoDocumentException e) {
			out.println("No Document was found! with token: "+token);
		} catch (IllegalArgumentException e) {
			out.println("you should provide the token string!");
		} catch (AddressException e) {
			out.println("Error in email Address");
		} catch (MessagingException e) {
			out.println("Internal messaging error");
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
