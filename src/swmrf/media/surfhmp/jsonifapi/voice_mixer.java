package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class voice_mixer {
	private String tool_type;
	private String reqType;
	private int reqId;
	private int toolId;
	private int samplingRate;
	private int hangoverPeriod;
	private int dominantSpeakers;
	
	public void ToolType(String tool_type)
	{
		this.tool_type = tool_type;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void SamplingRate(int samplingRate)
	{
		this.samplingRate = samplingRate;
	}
	
	public void HangoverPeriod(int hangoverPeriod)
	{
		this.hangoverPeriod = hangoverPeriod;
	}
	
	public void DominantSpeakers(int dominantSpeakers)
	{
		this.dominantSpeakers = dominantSpeakers;
	}
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		dataHash.put(SurfConstants.TOOL_TYPE, this.tool_type);
		dataHash.put(SurfConstants.SAMPLING_RATE, this.samplingRate);
		dataHash.put(SurfConstants.HANGOVER_PERIOD, this.hangoverPeriod);
		dataHash.put(SurfConstants.DOMINANT_SPEAKERS, this.dominantSpeakers);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String voice_mixer_msg()
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
