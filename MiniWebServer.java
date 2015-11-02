import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;
import java.io.FileReader;
import java.io.File;

public class MiniWebServer{
	public static void main(String[] args) throws Exception{
		Scanner input = new Scanner(System.in);
		System.out.println("Enter port: ");
		int port = Integer.parseInt(input.next());

		ServerSocket server = new ServerSocket(port);
		System.out.println("Listening for connection on port "+port+"...");

		while(true){
			try(Socket socket = server.accept()){
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader reader = new BufferedReader(isr);

				String output = "<!DOCTYPE html><html><body><table border='1' width='100%'>";
				String line = reader.readLine();
				String path = "backup/UDPClient.java";	//make this dynamnic
				boolean first = true;

				while(line!=null && !line.isEmpty()){
					if(!first){	//handles the header lines
						String[] keyValue = line.split(":");
						output += 	"<tr><td>"+keyValue[0]+"</td><td>"+keyValue[1]+"</td></tr>";

					}else{	//handles the initial request line
						//path = "";
						output += "<tr><td>Request line:</td><td>"+line+"</td><tr>";
						first = false;
					}
					line = reader.readLine();
				}

				//checks if the file exists
				File fileCheck = new File(path);
				String fileContents = "";
				if(fileCheck.exists() && !fileCheck.isDirectory()){
					fileContents = readFile(path);	//appends the file content to the string response
				}else{
					fileContents = "File Not Found";
				}
				
				output += "</table></body></html>";

				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + output+fileContents;
                socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			}
		}
	}

	private static String readFile(String file) throws IOException{
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while(( line = reader.readLine()) != null){
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		return "<pre style ='word wrap; break-word; white-space; pre-wrap;'>"+stringBuilder.toString()+"</pre>";
	}
}

/*to do list
	handle the file path from the GET method
*/