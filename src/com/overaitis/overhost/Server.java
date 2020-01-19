package com.overaitis.overhost;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	static ServerSocket serverConnect;
    static String WEB_ROOT = "./";
    private static Socket s;
    private static InputStream is;
    private static OutputStream os;
    private static DataOutputStream dos;
    private static HTTPRequestParser httpRequestParser;
	public Server(int port) throws IOException {
		serverConnect = new ServerSocket(port);
		System.out.println("Server started on port " + port);
		
		while (true) {
            
            // Wait for someone to connect.
            try {
                s = serverConnect.accept();
            } catch (IOException e) {
                System.err.println("Unable to accept connection: " + e.getMessage());
                continue;
            }

            System.out.println("Connection accepted.");

            try {
                is = s.getInputStream();
                os = s.getOutputStream();
                dos = new DataOutputStream(os);
                handleRequests();

                s.close();
                System.out.println("Connection closed\n");
            } catch (IOException e) {
                System.err.println("Unable to read/write: "  + e.getMessage());
            }
        }
    }
	
	private static void handleRequests() throws IOException {
        httpRequestParser = new HTTPRequestParser(is);
        System.out.println(httpRequestParser.getFileName());
        String[] args = httpRequestParser.getFileName().split("/");
        if (args[1].equalsIgnoreCase("stop")) {
        	stop();
        }
        if (args[1].equalsIgnoreCase("learn")) {
        	Runtime rt = Runtime.getRuntime();
        	Process process = rt.exec("sudo java -jar /home/overhome/overdew.jar learn+" + args[2]);
        	
        	String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "now trying to learn a new device named" + args[2];
        	s.getOutputStream().write(httpResponse.getBytes("UTF-8"));
        }
        if (args[1].equalsIgnoreCase("send")) {
        	Runtime rt = Runtime.getRuntime();
        	Process process = rt.exec("sudo java -jar /home/overhome/overdew.jar send+" + args[2] + "+" + args[3]);
        	
        	String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "sending packet to " + args[2] + "to turn " + args[3];
        	s.getOutputStream().write(httpResponse.getBytes("UTF-8"));
        }
	}


	public static void stop() throws IOException {
		serverConnect.close();
		System.out.println("SERVER STOPPED");
	}
}
