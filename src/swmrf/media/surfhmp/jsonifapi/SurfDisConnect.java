package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class SurfDisConnect {
	
	private int reqId;
	private int errorCode;
	private String reqType;
	private String cmdType;
	private String reason;
	
	public void req_id(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void error_code(int errorCode)
	{
		this.errorCode = errorCode;
	}
	
	public void req_type(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void reason(String reason)
	{
		this.reason = reason;
	}
	
	public void cmd_type(String cmdType)
	{
		this.cmdType = cmdType;
	}
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		
		dataHash.put(SurfConstants.CMD_TYPE, this.cmdType);
		
		JSONObject data = new JSONObject(dataHash);
    		return data;
    }
	
	public String clear_all_tools()
    {
    		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
    		HashMap<String,Object> sysReqHash = new HashMap<String,Object>();
		
    		sysReqHash.put(SurfConstants.REQ_ID, new Integer(this.reqId));
    		sysReqHash.put(SurfConstants.REQ_TYPE, this.reqType);
    		sysReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject sys_req = new JSONObject(sysReqHash);
		
		sendDataHash.put(SurfConstants.SYS_REQ, sys_req);
		JSONObject senddata = new JSONObject(sendDataHash);
		
    		return senddata.toString();
    }
    
    public String SurfDisConnectMsg()
    {
    		HashMap<String,Object> disConnectHash = new HashMap<String,Object>();
    		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
    		
    		disConnectHash.put(SurfConstants.ERROR_CODE, new Integer(this.errorCode));
    		disConnectHash.put(SurfConstants.REASON, this.reason);
		JSONObject disConnect = new JSONObject(disConnectHash);
		
		sendDataHash.put(SurfConstants.DISCONNECT, disConnect);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    }
    
}
