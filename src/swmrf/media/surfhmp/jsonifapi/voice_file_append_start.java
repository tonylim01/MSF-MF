package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class voice_file_append_start {
	private int duration;
	private int toolId;
	private int reqId;
	private String reqType;
	private String cmdType;
	private String fileList[] = new String[2];
	
	public void Duration(int duration)
	{
		this.duration = duration;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void CmdType(String cmdType)
	{
		this.cmdType = cmdType;
	}
	
	public void FileList(int index,String fileList)
	{
		this.fileList[index] = fileList;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject Data()
    {
		HashMap<String,Object> filesTempHash = new HashMap<String,Object>();
		HashMap<String,Object> filesTemp2Hash = new HashMap<String,Object>();
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		JSONArray files = new JSONArray();
		
		filesTempHash.put(SurfConstants.NAME, this.fileList[0]);
		filesTempHash.put(SurfConstants.DURATION, new Integer(this.duration));
		JSONObject filesTemp = new JSONObject(filesTempHash);
		files.add(filesTemp);
		
		filesTemp2Hash.put(SurfConstants.NAME, this.fileList[1]);
		JSONObject fileTemp2 = new JSONObject(filesTemp2Hash);
		files.add(fileTemp2);
		
		dataHash.put(SurfConstants.CMD_TYPE, this.cmdType);
		dataHash.put(SurfConstants.FILES, files);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String file_append_start_msg()
    {
    		HashMap<String,Object> toolReqHash = new HashMap<String,Object>();
    		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
		
    		toolReqHash.put(SurfConstants.TOOL_ID, this.toolId);
    		toolReqHash.put(SurfConstants.REQ_ID, this.reqId);
    		toolReqHash.put(SurfConstants.REQ_TYPE, this.reqType);
    		toolReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject toolReq = new JSONObject(toolReqHash);
		
		sendDataHash.put(SurfConstants.TOOL_REQ, toolReq);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    		
    }
}
