package swmrf.media.surfhmp.jsonifapi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SurfCommonLog {
	
	public static void Log(String x)
	{
		BufferedWriter bw = null;
		
		long time = System.currentTimeMillis(); 

		SimpleDateFormat dayTime = new SimpleDateFormat("HH:mm:ss.SSS");
		SimpleDateFormat LogDate = new SimpleDateFormat("yyyy-MM-dd");

		String str = dayTime.format(new Date(time)) + " * " + x;
		String Date  = LogDate.format(new Date(time));
		
		System.out.println(str);
//		String FileName = "/Users/jongchullim/Documents/Work/" + Date+".txt";
//		FileWriter out;
//		try {
//			out = new FileWriter(FileName,true);
//			bw = new BufferedWriter(out);
//			bw.write(str);bw.newLine();
//			bw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}

}
