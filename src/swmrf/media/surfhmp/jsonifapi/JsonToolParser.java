package swmrf.media.surfhmp.jsonifapi;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonToolParser {
	
	public static final String TOOL_ANS = "tool_ans";
	public static final String SYS_ANS = "sys_ans";
	public static final String TOOL_INF = "tool_inf";
	public static final String SYS_INF = "sys_inf";
	public static final String CONNECT = "connect";
	public static final String INF_TYPE = "inf_type";
	public static final String DATA = "data";
	public static final String CPU = "CPU";
	
	public void RecvParser(String Data) throws ParseException
	{
		JSONParser jsonParser = new JSONParser();
		
		 JSONObject jsonObject = (JSONObject) jsonParser.parse(Data);
		 
		 if(jsonObject.get(TOOL_ANS) != null)
			 SurfCommonLog.Log("tool_ans : "+ jsonObject.get(TOOL_ANS));
		 else if(jsonObject.get(SYS_ANS) != null)
			 SurfCommonLog.Log("sys_ans : "+ jsonObject.get(SYS_ANS));
		 else if(jsonObject.get(TOOL_INF) != null)
		 {
			 SurfCommonLog.Log("tool_inf : "+ jsonObject.get(TOOL_INF));
			 this.ToolInf(jsonObject.get(TOOL_INF).toString());
		 }
		 else if(jsonObject.get(SYS_INF) != null)
		 {
			 SurfCommonLog.Log("sys_inf : "+ jsonObject.get(SYS_INF));
			 this.SysInf(jsonObject.get(SYS_INF).toString());
		 }
		 else if(jsonObject.get(CONNECT) != null)
			 SurfCommonLog.Log("connect : "+ jsonObject.get(CONNECT));
		 else
			 SurfCommonLog.Log("else :" + Data);
	}
	
	public void ToolInf(String Data) throws ParseException
	{
//		System.out.println("inf_type : "+ this.JsonToSting(Data, INF_TYPE));
//		System.out.println("data : "+ this.JsonToSting(Data, DATA));
//		System.out.println("sent_bytes : "+ this.JsonToSting(this.JsonToSting(Data,DATA), "sent_bytes"));
//		System.out.println("frames_encoded : "+ this.JsonToSting(this.JsonToSting(Data,DATA), "frames_encoded"));
//		System.out.println("SSRC_sent : "+ this.JsonToSting(this.JsonToSting(Data,DATA), "SSRC_sent"));
	}
	
	public void SysInf(String Data) throws ParseException
	{
//		System.out.println("inf_type : "+ this.JsonToSting(Data, INF_TYPE));
//		System.out.println("data : "+ this.JsonToSting(Data, DATA));
		
//		System.out.println("CPU : "+ this.JsonToSting(this.JsonToSting(Data,DATA), CPU));
	}
	
	public String JsonToSting(String Data,String TagName) throws ParseException
	{
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(Data);
		if(jsonObject.get(TagName) == null)
			return null;
		else
			return jsonObject.get(TagName).toString();
	}
	

}
