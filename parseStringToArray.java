import java.net.*;
import java.io.*;
import java.util.Collections;
public class parseStringToArray
{
	public static void main(String[] args)
	{
		String in = "[  9  84 131 248]]\n[[147 210 225 389]";


	 	String[] rectanglesS = in.replaceAll("\\[", "").replaceAll("\\]", "").split("\n");
	 	int i = 0;
	 	for (i = 0; i < rectanglesS.length; ++i)
	 	{
	 		String[] valuesS = rectanglesS[i].split(" ");
	 		int[] values = new int[valuesS.length];
	 		for (int j = 0; j < valuesS.length; ++j)
	 		{
	 			if (valuesS[j].equals(""))
	 				continue;

	 			System.out.println(valuesS[j]);
	 			values[j] = Integer.parseInt(valuesS[j]);
	 		}
	 		//System.out.println(rects[i]);
	 	}

	}
}