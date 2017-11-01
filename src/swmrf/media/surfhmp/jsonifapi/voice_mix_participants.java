package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class voice_mix_participants {
	private String reqType;
	private String dataType;
	private int reqId;
	private int toolId;
	private int dataToolId;
	private int id;
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void DataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void DataToolId(int dataToolId)
	{
		this.dataToolId = dataToolId;
	}
	
	public void ID(int id)
	{
		this.id = id;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		HashMap<String,Object> participantsHash = new HashMap<String,Object>();
		JSONArray participantsArr = new JSONArray();
		participantsHash.put(SurfConstants.TYPE, this.dataType);
		participantsHash.put(SurfConstants.ID, new Integer(this.id));
		participantsHash.put(SurfConstants.DATA_TOOL_ID, new Integer(this.dataToolId));
		JSONObject participants = new JSONObject(participantsHash);
		participantsArr.add(participants);
		dataHash.put(SurfConstants.PARTICIPANT, participantsArr);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String voice_mix_participants_msg()
    {
		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
		HashMap<String,Object> toolReqHash = new HashMap<String,Object>();
		
		toolReqHash.put(SurfConstants.TOOL_ID, new Integer(this.toolId));
		toolReqHash.put(SurfConstants.REQ_ID, new Integer(this.reqId));
		toolReqHash.put(SurfConstants.REQ_TYPE, this.reqType);
		toolReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject toolReq = new JSONObject(toolReqHash);
		
		sendDataHash.put(SurfConstants.TOOL_REQ, toolReq);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    }
}
