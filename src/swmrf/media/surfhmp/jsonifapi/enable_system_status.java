package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class enable_system_status {
	
	private String statusType;
	private int statusPeriod;
	private String reqType;
	private int reqId;
	
	public void StatusType(String statusType)
	{
		this.statusType = statusType;
	}
	
	public void StatusPeriod(int statusPeriod)
	{
		this.statusPeriod = statusPeriod;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		HashMap<String,Object> statusTempHash = new HashMap<String,Object>();
		JSONArray statusArr = new JSONArray();
		
		statusTempHash.put(SurfConstants.TYPE, this.statusType);
		statusTempHash.put(SurfConstants.PERIOD, new Integer(this.statusPeriod));
		JSONObject statusTemp = new JSONObject(statusTempHash);
		
		statusArr.add(statusTemp);
		
		dataHash.put(SurfConstants.STATUS, statusArr);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String system_status()
    {
		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
		HashMap<String,Object> sysReqHash = new HashMap<String,Object>();
		
		sysReqHash.put(SurfConstants.REQ_ID, new Integer(this.reqId));
		sysReqHash.put(SurfConstants.REQ_TYPE, this.reqType);
		sysReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject sysReq = new JSONObject(sysReqHash);
		
		sendDataHash.put(SurfConstants.SYS_REQ, sysReq);
		JSONObject sendData = new JSONObject(sendDataHash);
		
    		return sendData.toString();
    		
    }
}
