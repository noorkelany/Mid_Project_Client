// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;
import java.sql.Date;

import common.ChatIF;
import data.UpdateOrderDetails;
import javafx.application.Platform;

/**
 * This class constructs the UI for a chat client. It implements the chat
 * interface in order to activate the display() method. Warning: Some of the
 * code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF {
	// Class variables *************************************************

	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Instance variables **********************************************

	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	ChatClient client;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ClientConsole(String host, int port) {
		try {
			client = new ChatClient(host, port, this);
			client.openConnection();
			// new client has connected to the server
			accept("connect");
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating client.");
			exception.printStackTrace();
			System.exit(1);
		}
	}

	// Instance methods ************************************************

	/**
	 * This method waits for input from the console. Once it is received, it sends
	 * it to the client's message handler.
	 */
	public void accept(Object messageToServer) {
		try {
			/*
			 * BufferedReader fromConsole = new BufferedReader(new
			 * InputStreamReader(System.in)); String message;
			 */

			/*
			 * while (true) { System.out.println("Enter a command:"); message =
			 * fromConsole.readLine();
			 * 
			 * if (message.equals("update")) { System.out.println("Enter order number:");
			 * int orderNumber = Integer.parseInt(fromConsole.readLine());
			 * 
			 * System.out.println("Enter new parking space:"); int parkingSpace =
			 * Integer.parseInt(fromConsole.readLine());
			 * 
			 * System.out.println("Enter new order date (yyyy-mm-dd):"); String dateStr =
			 * fromConsole.readLine(); Date orderDate = Date.valueOf(dateStr);
			 * 
			 * UpdateOrderDetails update = new UpdateOrderDetails(orderNumber, parkingSpace,
			 * orderDate);
			 * 
			 * client.handleMessageFromClientUI(update); // send the object } else {
			 * client.handleMessageFromClientUI(message); // send normal messages like
			 * "show" } }
			 */

			client.handleMessageFromClientUI(messageToServer);
		} catch (Exception ex) {

			System.out.println("Unexpected error while reading from console!");
			ex.printStackTrace();
		}
	}

	/**
	 * This method overrides the method in the ChatIF interface. It displays a
	 * message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */

	@Override
	public void display(Object message) {
	    Platform.runLater(() -> {
	        if (message instanceof String str) {
	            System.out.println("Message from server: " + str); //check

	            if (str.startsWith("Registration")) {
	                RegisterController controller = RegisterController.getInstance();
	                if (controller != null) {
	                    controller.handleServerResponse(str); 
	                }
	            }
	        }
	    });
	}



	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the Client UI.
	 *
	 * @param args[0] The host to connect to.
	 */
	public static void main(String[] args) {
		String host = "";
		int port = 0; // The port number

		try {
			host = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			host = "localhost";
		}
		ClientConsole chat = new ClientConsole(host, DEFAULT_PORT);
		// chat.accept(""); // Wait for console data
		// chat.updateOrderDetails(153, 80, "2025-05-30");
	}

	/****** client - related GUI methods ****/
	/**
	 * method to display connection info
	 */
	public void showConnectedClientInfo() {
		try {
			accept("clientDetails");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// display all orders
	/**
	 * method to display all existing order
	 */
	public void diplayAllOrders() {
		try {
			accept("showAllOrders");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// update specific order
	/**
	 * method to update specific order
	 * 
	 * @param orderNumber  number of order to update it's values
	 * @param parkingSpace new value of parkingSpace
	 * @param dateStr      new value of orderDate
	 */
	public void updateOrderDetails(int orderNumber, int parkingSpace, String dateStr) {
		try {
			Date orderDate = Date.valueOf(dateStr);
			UpdateOrderDetails update = new UpdateOrderDetails(orderNumber, parkingSpace, orderDate);
			accept(update);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//End of ConsoleChat class
