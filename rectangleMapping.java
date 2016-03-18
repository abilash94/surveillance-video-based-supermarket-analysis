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
	int size;
	public person()
	{
		size=0;
		persons=new Vector<userCart>();
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
	void map(rects a)
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
		System.out.println("Persons : "+this.persons.size());
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