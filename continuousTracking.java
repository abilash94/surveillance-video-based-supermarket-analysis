import java.net.*;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


//	structure for rectangle co-ordinates
class rect
{
	public int xlt, ylt, xrt, yrt, xlb, ylb, xrb, yrb;
}

//	accomodate many rectangles within a frame
class rects
{
	public int rectCount = 0;
	public rect[] rectangles = null;
}

//	global variables
class global
{
	public static boolean logging_enabled = true;
	public static String dns_ip = "127.0.0.1";
	public static int dns_port = 10000;	
	public static String continuousTrackingServiceName = "continuousTracking";
	public static int continuousTrackingServicePort = 10006;
	//	interval for switching databanks (in milliseconds)
	public static int dataBankSwitchInterval = 1000;
	//	min no of frames to be accumulated in a databank before switching
	public static int dataBankSwitchCount = 30;

	public static void log(String in)
	{
		if (global.logging_enabled)
			System.out.println(in);
	}

	public static void print(String in)
	{
		System.out.println(in);
	}

	//	parse frame data string into rectangle(s)
	public static rects parseFrameString(String frameData)
	{
		
		//	counts
		int rectsCount = 0;
		int valuesCount = 0;

		//	parse frame data into int arrays
		String[] valuesS = frameData.split(" ");
	 	int[] values = new int[valuesS.length];
	 	for (int i = 0; i < valuesS.length; ++i)
	 	{
	 		if (valuesS[i].equals(""))
	 			continue;
	 		values[i] = Integer.parseInt(valuesS[i]);
	 		++valuesCount;
	 	}

	 	//	number of rectangles
	 	rectsCount = valuesCount / 4;

	 	// create rects and assign each rectangle it's co-ords
	 	rects rcs = new rects();
	 	rcs.rectCount = rectsCount;
	 	rcs.rectangles = new rect[rectsCount];

	 	for (int j = 0; j < rectsCount; ++j)
	 	{
	 		rcs.rectangles[j] = new rect();
	 		int xa = values[j];
	 		int ya = values[j + 1];
	 		int xb = values[j + 2];
	 		int yb = values[j + 3];
	 		
	 		//	point A - left top
	 		//	point B - right bottom
	 		/*
	 		rcs.rectangles[j].xlt = xa;
	 		rcs.rectangles[j].ylt = ya;
	 		rcs.rectangles[j].xlb = xa;
	 		rcs.rectangles[j].ylb = yb;
	 		rcs.rectangles[j].xrt = xb;
	 		rcs.rectangles[j].yrt = ya;
	 		rcs.rectangles[j].xrb = xb;
	 		rcs.rectangles[j].yrb = yb;
	 		*/

	 		//	point A - left bottom
	 		//	point B - right top
	 		
	 		rcs.rectangles[j].xlt = xa;
	 		rcs.rectangles[j].ylt = yb;
	 		rcs.rectangles[j].xlb = xa;
	 		rcs.rectangles[j].ylb = ya;
	 		rcs.rectangles[j].xrt = xb;
	 		rcs.rectangles[j].yrt = yb;
	 		rcs.rectangles[j].xrb = xb;
	 		rcs.rectangles[j].yrb = ya;
			
	 	}
	 	return rcs;
	 	
		
	}
}






//	thread for connecting with a single client
class ServerThread extends Thread
{
	Socket clientSocket;
	BufferedReader clientReader;
	PrintWriter clientWriter;
	dataManager dm;
	public ServerThread(Socket client, dataManager d)
	{
		clientSocket = client;
		dm = d;
		try
		{
			
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
			{
				//	be safe for null values
				String metainfo = clientReader.readLine();
				if (metainfo.equals(null))
				{
					Thread.sleep(1);
					continue;
				}
				//	read metainfo (frameID)
				int frameID = Integer.parseInt(metainfo);

				//	read frame data
				rects r = global.parseFrameString(clientReader.readLine());
				
				global.log("size writebank " + dm.currentBankWriting + " : " + Integer.toString(dm.appendRects(frameID, r)));
				//global.print(clientReader.readLine());	
			}
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
	}
}

//	base server thread
class Server extends Thread
{
	ServerSocket serverSocket;
	static int serverPort;
	dataManager dm;

	public Server(int port, dataManager d)
	{
		serverPort = port;
		dm = d;
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
				ServerThread server_thread = new ServerThread(client, dm);
				server_thread.start();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		
	}
}

//	client to connect with DNS
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


class dataBankManager extends Thread
{
	dataManager dm;
	public dataBankManager(dataManager d)
	{
		dm = d;
	}

	public void run()
	{
		while (true)
		{
			try
			{
				//	if sufficient number of frames have accumulated and the read bank has been read, switch, else wait for an interval and check again
				while (dm.sizeWritingBank() < global.dataBankSwitchCount || !dm.dataReadCompleted)
					Thread.sleep(global.dataBankSwitchInterval);

				global.log("switching dataBank, size: " + dm.sizeWritingBank());
				dm.switchDataBanks();
			}
			catch(Exception e)
			{

			}
		}
	}

}

//	manage rectangle data
class dataManager
{
	public static boolean ASC = true;
    public static boolean DESC = false;

	public Map < Integer, rects > dataBank_1 = new HashMap < Integer, rects >();
	public Map < Integer, rects > dataBank_2 = new HashMap < Integer, rects >();
	public int currentBankWriting = 1;
	public int currentBankReading = 2;
	public boolean dataReadCompleted = false;

	//	insert rectangle data into data bank
	public int appendRects(int frameID, rects r)
	{
		if (currentBankWriting == 1)
			dataBank_1.put(frameID, r);

		else
			dataBank_2.put(frameID, r);
		return sizeWritingBank();
	}

	public int sizeWritingBank()
	{
		return (currentBankWriting == 1) ? dataBank_1.size() : dataBank_2.size();
	}

	public int sizeReadingBank()
	{
		return (currentBankReading == 1) ? dataBank_1.size() : dataBank_2.size();
	}

	public void switchDataBanks()
	{
		int t = currentBankReading;
		currentBankReading = currentBankWriting;
		currentBankWriting = t;

		if (currentBankWriting == 1)
			dataBank_1.clear();
		else
			dataBank_2.clear();

		//	data read bank is now ready for reading
		dataReadCompleted = false;
	}

	public Map < Integer, rects > getFrames()
	{
		//	get the source data bank from which frames have to be retrieved
		Map < Integer, rects > sourceDataBank = (currentBankReading == 1) ? dataBank_1 : dataBank_2;
		//	sort the bank and store it
		Map < Integer, rects > sortedMap = sortByComparator(sourceDataBank, ASC);
		//	clear the original data bank to prevent retrieving the processed frames again
		sourceDataBank.clear();

		//	set flag that the read bank has been read
		dataReadCompleted = true;

		return sortedMap;
	}


	//	sorting helper function to sort the databank based on frameID
	private static Map < Integer, rects > sortByComparator(Map < Integer, rects > unsortMap, final boolean order)
    {

        List<Entry<Integer, rects>> list = new LinkedList<Entry<Integer, rects>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, rects>>()
        {
            public int compare(Entry<Integer, rects> o1,
                    Entry<Integer, rects> o2)
            {
                if (order)
                {
                    return o1.getKey().compareTo(o2.getKey());
                }
                else
                {
                    return o2.getKey().compareTo(o1.getKey());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<Integer, rects> sortedMap = new LinkedHashMap<Integer, rects>();
        for (Entry<Integer, rects> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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

		//	create class which manages the data
		dataManager dm = new dataManager();

		//	create a bank manager thread which can periodically switch the data bank
		dataBankManager dbm = new dataBankManager(dm);

		dbm.start();

		//	create entry in DNS
		try
		{
			InetAddress IP = InetAddress.getLocalHost();
			String res = dnsclient.set_ip(global.dns_ip, global.dns_port, global.continuousTrackingServiceName, IP.getHostAddress().toString(), global.continuousTrackingServicePort);
			global.log(res);
			global.log("DNS entry created");
		}
		catch(Exception e)
		{
			global.log("DNS entry not created.");
			return ;
		}
		try
		{	
			Server server = new Server(global.continuousTrackingServicePort, dm);
			server.start();
		}
		catch(Exception e)
		{
			global.log("parent ain't waiting ");
		}

		while (true)
		{
			//	retrieve frames
			Map < Integer, rects > retrivedFrames = dm.getFrames();

			//	log number of frames received
			int framesCount = retrivedFrames.size();
			if (framesCount != 0)
			{
				global.log("got frames bank " + dm.currentBankWriting + " : " + Integer.toString(framesCount));
			
				//	process frames
				for (Map.Entry<Integer, rects> entry : retrivedFrames.entrySet())
				{
				    global.log(entry.getKey() + "/" + entry.getValue().rectangles.length);
				}
			}	
			else
			{
				//	to avoid high cpu usage		
				try
				{
					Thread.sleep(global.dataBankSwitchInterval);
				}
				catch(Exception e){}
			}
		}
	}
}