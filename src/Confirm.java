
import java.io.IOException;
import java.io.PrintWriter;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Confirm
 */
@WebServlet("/Confirm")
public class Confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String serverEmailAddress = "samana@student.unimelb.edu.au";
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
		PrintWriter out = response.getWriter();
		String token = request.getParameter("token");

		/************* Database connection **************/
		CouchDbClient dbClient = new CouchDbClient("newproj1db", true, "http",
				"127.0.0.1", 5984, "saman", "123");
		try {
			//Set confirmed flag as true
			UserData udata = dbClient.find(UserData.class, token);
			udata.setConfirmed(true);
			out.println("Found : "+udata.getEmail());

			//updating the database
			Gson gson = new Gson();
			String jsonString = gson.toJson(udata);
			
			JsonObject jsonobj = dbClient.getGson().fromJson(jsonString,
					JsonObject.class);

			dbClient.update(jsonobj);
			out.println("Updated Confirmation Status : "+udata.getEmail());
			//Send password to user
			SendPassword.sendNewPasswordForID(token, serverEmailAddress, dbClient);
			out.println("Sent password Email");

		} catch (NoDocumentException e) {
			out.println("No Document was found!");
			out.println(token);
		} catch (IllegalArgumentException e) {
			out.println("you should provide the token string!");
			out.println(token);
			e.printStackTrace(out);
		} catch (AddressException e) {
			out.println("Error in email Address");
		} catch (MessagingException e){
			out.println("Internal messaging error");
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
