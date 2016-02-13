import java.net.*;
import java.io.*;

class global
{
	public static boolean logging_enabled = true;
	public static String dns_ip = "127.0.0.1";
	public static int dns_port = 10000;	
	public static String continuousTrackingServiceName = "continuousTracking";
	public static int continuousTrackingServicePort = 10006;

	public static void log(String in)
	{
		if (global.logging_enabled)
			System.out.println(in);
	}

	public static void print(String in)
	{
		System.out.println(in);
	}
}

class ServerThread extends Thread
{
	static Socket clientSocket;
	static BufferedReader clientReader;
	static PrintWriter clientWriter;
	public ServerThread(Socket client)
	{
		try
		{
			clientSocket = client;		
			clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			clientWriter = new PrintWriter(client.getOutputStream(),true);
			global.log("Client connected: " + client.getRemoteSocketAddress());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public void run()
	{
		try
		{
			while (true)
				global.print(clientReader.readLine());		
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
	}
}


class Server extends Thread
{
	ServerSocket serverSocket;
	static int serverPort;

	public Server(int port)
	{
		serverPort = port;
	}

	public void run()
	{
		//	start server on the given port
		try
		{
			serverSocket = new ServerSocket(serverPort);
			global.log("Server started on port: " + Integer.toString(serverPort));
		}
		catch(Exception e)
		{
			global.log("Unable to start server on port: " + Integer.toString(serverPort));
		}

		while(true)
		{
			try
			{
				Socket client = serverSocket.accept();
				ServerThread server_thread = new ServerThread(client);
				server_thread.start();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		
	}
}

class dnsclient
{
	public static String resolve_ip(String dns_ip, int dns_port, String service)
	{
		String resolved = null;
		PrintWriter clientWriter;
		BufferedReader clientReader;
		try
		{
			Socket client = new Socket(dns_ip, dns_port);
			clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			clientWriter = new PrintWriter(client.getOutputStream(),true);
			clientWriter.println("0 " + service + " ");
			resolved = clientReader.readLine();
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
		return resolved;
	}

	public static String set_ip(String dns_ip, int dns_port, String service, String ip, int port)
	{
		String resolved = null;
		PrintWriter clientWriter;
		BufferedReader clientReader;
		try
		{
			Socket client = new Socket(dns_ip, dns_port);
			clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			clientWriter = new PrintWriter(client.getOutputStream(),true);
			clientWriter.println("1 " + service + " " + ip + " " + Integer.toString(port));
			resolved = clientReader.readLine();
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
		return resolved;
	}
}

public class continuousTracking
{

	public static void set_arguments(String[] args)
	{
		try
		{
			//	arguments
			if (args[0].equals("-d"))
				global.dns_ip = args[1];
			if (args[2].equals("-p"))
				global.dns_port = Integer.parseInt(args[3]);
		}
		catch (Exception e)
		{
			global.log("No parameters provided");
		}
	}

	public static void main(String[] args)
	{
		set_arguments(args);


		//	create entry in DNS
		try
		{
			InetAddress IP = InetAddress.getLocalHost();
			global.log("AA");
			String res = dnsclient.set_ip(global.dns_ip, global.dns_port, global.continuousTrackingServiceName, IP.getHostAddress().toString(), global.continuousTrackingServicePort);
			global.log("BB");
			global.log(res);
			global.log("DNS entry created");
		}
		catch(Exception e)
		{
			global.log("DNS entry not created.");
			return ;
		}	
		Server server = new Server(global.continuousTrackingServicePort);
		server.start();
		while(true);	
	}
}