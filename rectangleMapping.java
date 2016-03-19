import java.net.*;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.PriorityQueue;
import java.io.File;
import java.util.Scanner;

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
	Vector productList;
	public userCart(rect a)
	{
		miss=0;
		curr=new rect(a);
		productList=new Vector();
	}
	public userCart(userCart a)
	{
		miss=a.miss;
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
	int size;
	public person()
	{
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
			System.out.println(leftRack.elementAt(i)+" "+leftRackHeight.elementAt(i)+" "+leftRackProduct.elementAt(i));
		}
		rightCount= Integer.parseInt(br.readLine());
		for (i = 0; i < rightCount; ++i)
		{
			String ip=br.readLine();
			String [] elements = ip.split(" ");
			rightRack.addElement(Integer.parseInt(elements[0]));
			rightRackHeight.addElement(Integer.parseInt(elements[1]));
			rightRackProduct.addElement(elements[2]);
			System.out.println(rightRack.elementAt(i)+" "+rightRackHeight.elementAt(i)+" "+rightRackProduct.elementAt(i));
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
            return (int) (c1.distance - c2.distance);
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
	void map(rects a) throws Exception
	{
		Queue <maputilclass> mappingUtil= new PriorityQueue<maputilclass>(1,idComparator);
		boolean [] personsCheck =new boolean [ persons.size()];
		boolean [] rectsCheck = new boolean [a.rectCount];
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
					//System.out.println("Person removed");
					i--;
					this.size--;
				}
			}
		for(i=0;i<a.rectCount;i++)
			if(rectsCheck[i]==false)
			{
				//System.out.println("New person added");
				this.persons.addElement(new userCart(a.rectangles[i]));
				this.size++;
			}
		//System.out.println("Persons : "+this.persons.size());
		//int leftRack[] = new int[100];
		//int leftRackHeight[] = new int[100];
		//int rightRack[] = new int[100];
		//int rightRackHeight = new int [100];
		
		for (i = 0; i < a.rectCount; ++i)			
		{
			int lowestLevelOfFrame = 360 - a.rectangles[i].ylb - 30; // net y = 360
			int horizontalAxis = 300 - a.rectangles[i].xlb;	//	net x = 300

			int scale = 50 / lowestLevelOfFrame;
			int distance;
			if(scale >= 1)
				distance = lowestLevelOfFrame * scale;
			else
				distance = lowestLevelOfFrame; 
			if (distance < 300)
			{
				//System.out.println("Persons : "+this.persons.size());
				//System.out.println("Distance in Metres: " + (float)distance / 100);
				if (horizontalAxis <= 150)
				{
					System.out.println("Left Rack");
					int rack = distance / 100;
					int row;
					int x=(a.rectangles[i].xlt+a.rectangles[i].xrb)/2;
					int y=(a.rectangles[i].ylt+a.rectangles[i].yrb)/2;
					row = y/90;
					System.out.println(findLeft(rack,row));  
				}
				else
				{
					System.out.println("Right Rack");
					int rack = distance / 100;
					int row;
					int x=(a.rectangles[i].xlt+a.rectangles[i].xrb)/2;
					int y=(a.rectangles[i].ylt+a.rectangles[i].yrb)/2;
					row = y/90;
					System.out.println(findRight(rack,row));
				}
			}

			//System.out.println(lowestLevelOfFrame);
			//System.out.println((a.rectangles[i].xlb + a.rectangles[i].xrb) / 2);
		}
	}
}

public class rectangleMapping
{
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

	public static void main(String[] args) throws Exception
	{
		Map < Integer, rects > retrivedFrames = new LinkedHashMap<Integer, rects>();
		person p = new person();

		BufferedReader br = new BufferedReader(new FileReader("dump.txt"));
		String everything = "";
		String lines = "";
		int lineNum = 1;
		try {
	    	StringBuilder sb = new StringBuilder();
	    	String line;
	    	line = br.readLine();
	    	while (line != null){
	    		
	    		//lines += line;
	    		//System.out.println(line);
	    		retrivedFrames.put(lineNum, parseFrameString(line));
	    		line = br.readLine();
	    		lineNum += 1;
	    	}
	    	// while (line != null) {
	    	//     sb.append(line);
	     //   		sb.append(System.lineSeparator());
	     //    	line = br.readLine();
	    	// }
	    	// everything = sb.toString();
		} 
		finally {
	    	br.close();
		}

		for (Map.Entry<Integer, rects> entry : retrivedFrames.entrySet())
				{
					p.map(entry.getValue());
				}
	}
}