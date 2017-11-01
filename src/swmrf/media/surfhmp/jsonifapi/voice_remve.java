package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class voice_remve {
	private int reqId;
	private int toolId;
	private String reqType;
	
	public void req_id(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void tool_id(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void req_type(String reqType)
	{
		this.reqType = reqType;
	}
	
    public String SurfRemoveMsg()
    {
    		HashMap<String,Object> removeHash = new HashMap<String,Object>();
    		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
    		
    		removeHash.put(SurfConstants.TOOL_ID, new Integer(this.toolId));
    		removeHash.put(SurfConstants.REQ_ID, this.reqId);
    		removeHash.put(SurfConstants.REQ_TYPE, this.reqType);
		JSONObject remove = new JSONObject(removeHash);
		
		sendDataHash.put(SurfConstants.TOOL_REQ, remove);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    }
}
