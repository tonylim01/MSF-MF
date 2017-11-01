package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class create_file_reader {
	private String eventsType;
	private String toolType;
	private String reqType;
	private boolean videoEnabled;
	private boolean audioEnabled;
	private int toolId;
	private int reqId;
	private int audioDstToolIds;
	
	public void EventsType(String eventsType)
	{
		this.eventsType = eventsType;
	}
	
	public void ToolType(String toolType)
	{
		this.toolType = toolType;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void VideoEnabled(boolean videoEnabled)
	{
		this.videoEnabled = videoEnabled;
	}
	
	public void AudioEnabled(boolean audioEnabled)
	{
		this.audioEnabled = audioEnabled;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void AudioDTSToolIds(int audioDstToolIds)
	{
		this.audioDstToolIds = audioDstToolIds;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject Data()
    {
		HashMap<String,Object> eventsTempHash = new HashMap<String,Object>();
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		
		JSONArray audioDstToolIds = new JSONArray();
		JSONArray videoDstToolIds = new JSONArray();
		JSONArray events = new JSONArray();
		
		audioDstToolIds.add(new Integer(this.audioDstToolIds));
		
		eventsTempHash.put(SurfConstants.TYPE, this.eventsType);
		eventsTempHash.put(SurfConstants.ENABLED, true);
		
		JSONObject events_temp = new JSONObject(eventsTempHash);
		events.add(events_temp);
		
		dataHash.put(SurfConstants.VIDEO_ENABLED, this.videoEnabled);
		dataHash.put(SurfConstants.TOOL_TYPE, this.toolType);
		dataHash.put(SurfConstants.AUDIO_DST_TOOL_IDS, audioDstToolIds);
		dataHash.put(SurfConstants.AUDIO_ENABLED, this.audioEnabled);
		dataHash.put(SurfConstants.VIDEO_DST_TOOL_IDS, videoDstToolIds);
		dataHash.put(SurfConstants.EVENTS, events);
		
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    }
	
    public String create_file_reader_msg()
    {
		HashMap<String,Object> toolReqHash = new HashMap<String,Object>();
		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
		
		toolReqHash.put(SurfConstants.TOOL_ID, new Integer(this.toolId));
		toolReqHash.put(SurfConstants.REQ_ID, new Integer(this.reqId));
		toolReqHash.put(SurfConstants.REQ_TYPE, this.reqType);
		toolReqHash.put(SurfConstants.DATA, this.Data());
		JSONObject toolReq = new JSONObject(toolReqHash);
		
		sendDataHash.put(SurfConstants.TOOL_REQ, toolReq);
		JSONObject senddata = new JSONObject(sendDataHash);
		
    		return senddata.toString();
    		
    }
}
