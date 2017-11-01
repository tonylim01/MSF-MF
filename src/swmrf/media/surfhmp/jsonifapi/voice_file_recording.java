package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class voice_file_recording {
	private String cmdType;
	private String reqType;
	private String fileName;
	private int toolId;
	private int reqId;
	private int maxSize;
	
	public void CmdType(String cmdType)
	{
		this.cmdType = cmdType;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	public void FileName(String fileName)
	{
		this.fileName = fileName;
	}
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	public void MaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		dataHash.put(SurfConstants.CMD_TYPE, this.cmdType);
		dataHash.put(SurfConstants.FILE_NAME, this.fileName);
		dataHash.put(SurfConstants.MAX_SIZE, this.maxSize);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String file_record_msg()
    {
		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
		HashMap<String,Object> toolReqHash = new HashMap<String,Object>();
		
		toolReqHash.put(SurfConstants.TOOL_ID, new Integer(this.toolId));
		toolReqHash.put(SurfConstants.REQ_ID, new Integer(this.reqId));
		toolReqHash.put(SurfConstants.REQ_TYPE, this.reqType);
		toolReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject tool_req = new JSONObject(toolReqHash);
		
		sendDataHash.put(SurfConstants.TOOL_REQ, tool_req);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    }
}
