package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class file_play {
	public String cmdType;
	public String reqType;
	public int toolId;
	public int reqId;
	
	public JSONObject Data()
    {
		HashMap<String,Object> dataDash = new HashMap<String,Object>();
		dataDash.put(SurfConstants.CMD_TYPE, cmdType);
		JSONObject data = new JSONObject(dataDash);
		
    		return data;
    }
	
    public String file_play_msg()
    {
		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
		HashMap<String,Object> toolReqHash = new HashMap<String,Object>();
		
		toolReqHash.put(SurfConstants.TOOL_ID, new Integer(1));
		toolReqHash.put(SurfConstants.REQ_ID, new Integer(1002));
		toolReqHash.put(SurfConstants.REQ_TYPE, "command");
		toolReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject toolRreq = new JSONObject(toolReqHash);
		
		sendDataHash.put(SurfConstants.TOOL_REQ, toolRreq);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    }
}
