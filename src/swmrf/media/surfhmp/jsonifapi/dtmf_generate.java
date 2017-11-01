package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class dtmf_generate {
	private String cmdType;
	private String reqType;
	private String rtpOrInband;
	private String namedEvent;
	private int toolId;
	private int reqId;
	private int totalDuration;
	
	public void CmdType(String cmdType)
	{
		this.cmdType = cmdType;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void RtpOrInband(String rtpOrInband)
	{
		this.rtpOrInband = rtpOrInband;
	}
	
	public void NamedEvent(String namedEvent)
	{
		this.namedEvent = namedEvent;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void TotalDuration(int totalDuration)
	{
		this.totalDuration = totalDuration;
	}
	
	private JSONObject ip_event()
    {
		HashMap<String,Object> ipEventTempHash = new HashMap<String,Object>();
		
		ipEventTempHash.put(SurfConstants.NAMED_EVENT, this.namedEvent);
		JSONObject ip_event_temp = new JSONObject(ipEventTempHash);
		
		return ip_event_temp;
    }
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		dataHash.put(SurfConstants.CMD_TYPE, this.cmdType);
		dataHash.put(SurfConstants.IP_EVENT, this.ip_event());
		dataHash.put(SurfConstants.RTP_OR_INBAND, this.rtpOrInband);
		dataHash.put(SurfConstants.TOTAl_DURATION, this.totalDuration);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String dtmf_generate_msg()
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
