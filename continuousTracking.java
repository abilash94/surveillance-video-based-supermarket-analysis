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
import java.util.Vector;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Random;
import java.net.URL;

//	structure for rectangle co-ordinates
class rect
{
	public int xlt, ylt, xrt, yrt, xlb, ylb, xrb, yrb;
	public rect()
	{
		//dummy
	}
	public rect(rect a)
	{
		xlt=a.xlt;
		ylt=a.ylt;
		xrt=a.xrt;
		yrt=a.yrt;
		xlb=a.xlb;
		ylb=a.ylb;
		xrb=a.xrb;
		yrb=a.yrb;
	}
}

class rects
{
	public int rectCount = 0;
	public rect[] rectangles = null;
}
class userCart
{
	rect curr;
	int miss;
	int lastColumn;
	int columnFrameCount;
	boolean billed;
	Vector <String> productList;
	public userCart(rect a)
	{
		billed=false;
		miss=0;
		lastColumn=columnFrameCount=0;
		curr=new rect(a);
		productList=new Vector<String>();
	}
	public userCart(userCart a)
	{
		billed=false;
		miss=a.miss;
		lastColumn=columnFrameCount=0;
		curr=new rect(a.curr);
		productList=(Vector)a.productList.clone();
	}
}
class maputilclass
{
	int distance;
	int i;
	int j;
}
class person
{
	Vector <userCart> persons;
	Vector <Integer> leftRack; 
	Vector <Integer> rightRack;
	Vector <Integer> leftRackHeight;
	Vector <Integer> rightRackHeight;
	Vector <String> leftRackProduct;
	Vector <String> rightRackProduct;
	
	BufferedWriter writer; // line added by lakshmi
	int framify; // line added by lakshmi
	
	int size;
	public person() throws Exception
	{
		writer = new BufferedWriter(new FileWriter("file.txt", true)); // line added by lakshmi
		framify = 0;
		
		size=0;
		persons=new Vector<userCart>();
		leftRack = new Vector<Integer>();
		 rightRack = new Vector<Integer>();
		 leftRackHeight = new Vector<Integer>();
		 rightRackHeight = new Vector<Integer>();
		 leftRackProduct = new Vector<String>();
		 rightRackProduct = new Vector<String>();
		try
		{
		BufferedReader br = new BufferedReader(new FileReader("map.txt"));
		int i;
		int leftCount = Integer.parseInt(br.readLine());
		int rightCount;
		for (i = 0; i < leftCount; ++i)
		{
			String ip=br.readLine();
			String [] elements = ip.split(" ");
			leftRack.addElement(Integer.parseInt(elements[0]));
			leftRackHeight.addElement(Integer.parseInt(elements[1]));
			leftRackProduct.addElement(elements[2]);
			//System.out.println(leftRack.elementAt(i)+" "+leftRackHeight.elementAt(i)+" "+leftRackProduct.elementAt(i));
		}
		rightCount= Integer.parseInt(br.readLine());
		for (i = 0; i < rightCount; ++i)
		{
			String ip=br.readLine();
			String [] elements = ip.split(" ");
			rightRack.addElement(Integer.parseInt(elements[0]));
			rightRackHeight.addElement(Integer.parseInt(elements[1]));
			rightRackProduct.addElement(elements[2]);
			//System.out.println(rightRack.elementAt(i)+" "+rightRackHeight.elementAt(i)+" "+rightRackProduct.elementAt(i));
		}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public int rectDistance(rect a,rect b)   //Distance between midpoints of two rectangles
	{
		int x,y,x1,y1;
		x=(a.xlt+a.xrb)/2;
		y=(a.ylt+a.yrb)/2;
		x1=(b.xlt+b.xrb)/2;
		y1=(b.ylt+b.yrb)/2;
		return (x-x1)*(x-x1)+(y-y1)*(y-y1);
	}
	public static Comparator<maputilclass> idComparator = new Comparator<maputilclass>(){
         
        @Override
        public int compare(maputilclass c1, maputilclass c2) {
            return (c1.distance - c2.distance);
        }
    };
    public String findLeft(int rack, int row)
    {
    	int i;
    	for(i=0;i<leftRack.size();i++)
    		if(leftRack.elementAt(i)==rack&&leftRackHeight.elementAt(i)==row)
    			return leftRackProduct.elementAt(i);
 		return null;
    }
    public String findRight(int rack, int row)
    {
    	int i;
    	for(i=0;i<rightRack.size();i++)
    		if(rightRack.elementAt(i)==rack&&rightRackHeight.elementAt(i)==row)
	    		return rightRackProduct.elementAt(i);
	    return null;
    }
	public int detectHandMovement(int a)
	{
		return (a % 2 == 0) ? 1 : 4;
	}
	void map(rects a) throws Exception
	{
		Queue <maputilclass> mappingUtil= new PriorityQueue<maputilclass>(1,idComparator);
		//writer = new BufferedWriter(new FileWriter("file.txt", true)); // line added by lakshmi
		boolean [] personsCheck =new boolean [ persons.size()];
		boolean [] rectsCheck = new boolean [a.rectCount];
		framify++; // line added by lakshmi
		//System.out.println("Frame: " + framify);
		int i,j;
		//System.out.println("Vector size : "+ this.size);
		for(i=0;i<this.size;i++)
		{
			for(j=0;j<a.rectCount;j++)
			{
				//System.out.println(i);
				maputilclass temp=new maputilclass();
				temp.distance=rectDistance(this.persons.elementAt(i).curr,a.rectangles[j]);
				temp.i=i;
				temp.j=j;
				mappingUtil.add(temp);
			}
		}
		while(mappingUtil.size()>0)
		{
			maputilclass temp=mappingUtil.poll();
			if(personsCheck[temp.i]==false&&rectsCheck[temp.j]==false)
			{
				personsCheck[temp.i]=true;
				rectsCheck[temp.j]=true;
				this.persons.elementAt(temp.i).curr=new rect(a.rectangles[temp.j]);
				this.persons.elementAt(temp.i).miss=0;
			} 
		}
		for(i=0;i<this.size;i++)
			if(personsCheck[i]==false)
			{
				this.persons.elementAt(i).miss++;
				if(this.persons.elementAt(i).miss>60)
				{
					persons.removeElementAt(i);
					System.out.println("Person removed"); //Comment by lakshmi
					i--;
					this.size--;
				}
			}
		for(i=0;i<a.rectCount;i++)
			if(rectsCheck[i]==false)
			{
				System.out.println("New person added");//Comment by lakshmi
				this.persons.addElement(new userCart(a.rectangles[i]));
				this.size++;
			}
		//System.out.println("Persons : "+this.persons.size());
		//int leftRack[] = new int[100];
		//int leftRackHeight[] = new int[100];
		//int rightRack[] = new int[100];
		//int rightRackHeight = new int [100];
		
		// Calculation part starts here
		
		//writer.write("Frame number is " + this.framify); // line added by lakshmi
		//writer.write("\n");// line added by lakshmi
		for (i = 0; i < this.persons.size(); ++i)			
		{
			/*
			writer.write("Rectangle number " + (i + 1)); // line added by lakshmi
			writer.write("\n");// line added by lakshmi
			writer.write("Top left: (" + this.persons.elementAt(i).curr.xlt + ", " + this.persons.elementAt(i).curr.ylt + ")");// line added by lakshmi
			writer.write("\n");// line added by lakshmi
			writer.write("Bottom right: (" + this.persons.elementAt(i).curr.xrb + ", " + this.persons.elementAt(i).curr.yrb + ")");// line added by lakshmi
			writer.write("\n");// line added by lakshmi
			*/
			
			// Each frame is 320 x 240
			// (x1, y1) = top left && x2, y2) = bottom right
			int cur_x1 = this.persons.elementAt(i).curr.xlt;
			int cur_y1 = this.persons.elementAt(i).curr.ylt;
			int cur_x2 = this.persons.elementAt(i).curr.xrb;
			int cur_y2 = this.persons.elementAt(i).curr.yrb;
			
			// Check if the customer reaches the billing area
			if(cur_y1 <= 220 && cur_y1 >= 50&&this.persons.elementAt(i).billed==false&&this.persons.elementAt(i).productList.size()>0&&!this.persons.elementAt(i).productList.contains("null"))
			{
				System.out.println("Inside sendify");
				this.persons.elementAt(i).billed=true;
				// Send the cart to a URL
				String sendify = "";
				for(int k = 0; k < this.persons.elementAt(i).productList.size(); k++)
				{
					System.out.println(k+ " "+this.persons.elementAt(i).productList.elementAt(k));
					sendify += this.persons.elementAt(i).productList.elementAt(k);
					if(k!=this.persons.elementAt(i).productList.size()-1)
						sendify += "%20";
				}
				System.out.println("Sendify string is " + sendify);
				
				String recvd = dnsclient.resolve_ip(global.dns_ip, global.dns_port, "DBbackend");
				System.out.println("recvd is " + recvd);
				String[] recvify = recvd.split(" ");
				String urlsend = "http://" + recvify[0] + ":" + recvify[1] + "/billing_customer_carts/cart_arrival?billing_customer_cart[cart]=" + sendify;
				System.out.println("url is " + urlsend);
				System.out.println(urlsend);
				
				URL url = new URL(urlsend);
				String strTemp = "";
				//url.openConnection();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				while (null != (strTemp = br.readLine()));
			}
			
			// Determine left rack or right rack
			// 720 width => 360 left + 360 right
			boolean direction = true; // true -> left, false -> right
			if(cur_x2 > 170)
				direction = false;
			
			// Find the column
			int[] dist_array = {10, 30, 70, 150};
			int column_num = 0;
			for(j = 0; j < 4; j++)
			{
				if(cur_y2 - 80 < dist_array[j]) // 100 is for eliminating top portion
				{
					column_num = j + 1;
					break;
				}
			}

			// Find the row
			int row_num = (cur_y1 + cur_y2) / 2;
			
			row_num = detectHandMovement(row_num);
			
			if(this.persons.elementAt(i).lastColumn==column_num)
				this.persons.elementAt(i).columnFrameCount++;
			else
			{
				if(this.persons.elementAt(i).columnFrameCount>=30)
				{
								// Find the corresponding product
						String product;
						if(direction)
							product = findLeft(row_num, this.persons.elementAt(i).lastColumn);
						else	
							product = findRight(row_num, this.persons.elementAt(i).lastColumn);
						
						// Assign the product onto the person's list
						if(direction)
						{
							if(!this.persons.elementAt(i).productList.contains(product))
							{
								this.persons.elementAt(i).productList.addElement(product);
								System.out.println("Person "+i+" at left rack near product "+product);
							}
						}
						else
						{
							if(!this.persons.elementAt(i).productList.contains(product))
							{
								this.persons.elementAt(i).productList.addElement(product);
								System.out.println("Person "+i+" at right rack near product "+product);
							}
						}	
						for(int k=0;k<this.persons.elementAt(i).productList.size();k++)
						{
							System.out.print(this.persons.elementAt(i).productList.elementAt(k)+" ");
						}
						System.out.println(' ');
					}
					this.persons.elementAt(i).lastColumn=column_num;
					this.persons.elementAt(i).columnFrameCount=0;		
			}
			
			
		}
		//writer.close(); // line added by lakshmi
		
		// Calculation part ends here
	}
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
		{
			//System.out.println(in); // Comment by lakshmi
		}
	}

	public static void print(String in)
	{
		//System.out.println(in);// Comment by lakshmi
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
	 		
			if(xa <= xb)
			{
				if(ya <= yb)
				{
					rcs.rectangles[j].xlt = xa;
					rcs.rectangles[j].ylt = ya;
					rcs.rectangles[j].xrb = xb;
					rcs.rectangles[j].yrb = yb;
					rcs.rectangles[j].xlb = xa;
					rcs.rectangles[j].ylb = yb;
					rcs.rectangles[j].xrt = xb;
					rcs.rectangles[j].yrt = ya;
				}
				else
				{
					rcs.rectangles[j].xlb = xa;
					rcs.rectangles[j].ylb = ya;
					rcs.rectangles[j].xrt = xb;
					rcs.rectangles[j].yrt = yb;
					rcs.rectangles[j].xrb = xb;
					rcs.rectangles[j].yrb = ya;
					rcs.rectangles[j].xlt = xa;
					rcs.rectangles[j].ylt = yb;
				}
			}
			else
			{
				if(yb <= ya)
				{
					rcs.rectangles[j].xlt = xb;
					rcs.rectangles[j].ylt = yb;
					rcs.rectangles[j].xrb = xa;
					rcs.rectangles[j].yrb = ya;
					rcs.rectangles[j].xlb = xb;
					rcs.rectangles[j].ylb = ya;
					rcs.rectangles[j].xrt = xa;
					rcs.rectangles[j].yrt = yb;
				}
				else
				{
					rcs.rectangles[j].xlb = xb;
					rcs.rectangles[j].ylb = yb;
					rcs.rectangles[j].xrt = xa;
					rcs.rectangles[j].yrt = ya;
					rcs.rectangles[j].xrb = xa;
					rcs.rectangles[j].yrb = yb;
					rcs.rectangles[j].xlt = xb;
					rcs.rectangles[j].ylt = ya;
				}
			}
			
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
				dm.appendRects(frameID, r);
				//global.log("size writebank " + dm.currentBankWriting + " : " + Integer.toString(dm.appendRects(frameID, r)));
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

	public static void main(String[] args) throws Exception
	{
		
		person p = new person();
		
		
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
				//global.log("got frames bank " + dm.currentBankWriting + " : " + Integer.toString(framesCount));
				//System.out.println("Call from main frame count " + framesCount);
				//	process frames
				for (Map.Entry<Integer, rects> entry : retrivedFrames.entrySet())
				{
				   // global.log(entry.getKey() + "/" + entry.getValue().rectangles.length);
				   //System.out.println(entry.getKey() + "/" + entry.getValue().rectangles.length); // lakshmi
					p.map(entry.getValue());
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