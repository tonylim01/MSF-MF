package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class configure_voice_tool {

	private String statusType;
	private String eventsType;
	private String decoderType;
	private String encoderType;
	private String remoteIp;
	private String toolType;
	private String reqType;
	private String appInfo;
	private int period;
	private int packetDuration;
	private int localUdpPort;
	private int remoteUdpPort;
	private int outPayloadType;
	private int groupId;
	private int toolId;
	private int reqId;
	private boolean inputFromRTP;
	private boolean enabled;
	
	public void StatusType(String statusType)
	{
		this.statusType = statusType;
	}
	
	public void EventsType(String eventsType)
	{
		this.eventsType = eventsType;
	}
	
	public void DecoderType(String decoderType)
	{
		this.decoderType = decoderType;
	}
	
	public void EncoderType(String encoderType)
	{
		this.encoderType = encoderType;
	}
	
	public void RemoteIp(String remoteIp)
	{
		this.remoteIp = remoteIp;
	}
	
	public void ToolType(String toolType)
	{
		this.toolType = toolType;
	}
	
	public void ReqType(String reqType)
	{
		this.reqType = reqType;
	}
	
	public void AppInfo(String appInfo)
	{
		this.appInfo = appInfo;
	}
	
	public void Period(int period)
	{
		this.period = period;
	}
	
	public void PacketDuration(int packetDuration)
	{
		this.packetDuration = packetDuration;
	}
	
	public void LocalUdpPort(int localUdpPort)
	{
		this.localUdpPort = localUdpPort;
	}
	
	public void RemoteUdpPort(int remoteUudpPort)
	{
		this.remoteUdpPort = remoteUudpPort;
	}
	
	public void OutPayloadType(int outPayloadType)
	{
		this.outPayloadType = outPayloadType;
	}
	
	public void GroupId(int groupId)
	{
		this.groupId = groupId;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	public void InputFromRTP(boolean inputFromRTP)
	{
		this.inputFromRTP = inputFromRTP;
	}
	public void Enabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray status()
    {
		HashMap<String,Object> status_temp_hash = new HashMap<String,Object>();
		
		JSONArray statusarr = new JSONArray();
		status_temp_hash.put(SurfConstants.TYPE, this.statusType);
		status_temp_hash.put(SurfConstants.PERIOD, new Integer(this.period));
		JSONObject status_temp = new JSONObject(status_temp_hash);
		
		statusarr.add(status_temp);
		
		return statusarr;
    }
	
	@SuppressWarnings("unchecked")
	private JSONArray events()
    {
		HashMap<String,Object> status_temp_hash = new HashMap<String,Object>();
		
		JSONArray eventsArr = new JSONArray();
		
		status_temp_hash.put(SurfConstants.TYPE, this.eventsType);
		status_temp_hash.put(SurfConstants.ENABLED, this.enabled);
		
		JSONObject status_temp = new JSONObject(status_temp_hash);
		eventsArr.add(status_temp);
		
		return eventsArr;
    }
	
	private JSONObject decoder()
    {
		HashMap<String,Object> decoderTempHash = new HashMap<String,Object>();
		
		decoderTempHash.put(SurfConstants.TYPE, this.decoderType);
		JSONObject decoder_temp = new JSONObject(decoderTempHash);
		
		return decoder_temp;
    }
	
	private JSONObject encoder()
    {
		HashMap<String,Object> encoderTempHash = new HashMap<String,Object>();
		
		encoderTempHash.put(SurfConstants.TYPE, this.encoderType);
		encoderTempHash.put(SurfConstants.PACKET_DURATION, new Integer(this.packetDuration));
		JSONObject encoder_temp = new JSONObject(encoderTempHash);
		
		return encoder_temp;
    }
	
	private JSONObject RTP()
    {
		HashMap<String,Object> rtpTempHash = new HashMap<String,Object>();
		
		rtpTempHash.put(SurfConstants.LOCAL_UDP_PORT, this.localUdpPort);
		rtpTempHash.put(SurfConstants.REMOTE_UDP_PORT, this.remoteUdpPort);
		rtpTempHash.put(SurfConstants.REMOTE_IP, this.remoteIp);
		rtpTempHash.put(SurfConstants.OUT_PAYLOAD_TYPE, new Integer(this.outPayloadType));
		
		JSONObject RTP_temp = new JSONObject(rtpTempHash);
		
		return RTP_temp;
    }
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		
		dataHash.put(SurfConstants.STATUS, this.status());
		dataHash.put(SurfConstants.EVENTS, this.events());
		dataHash.put(SurfConstants.DECODER, this.decoder());
		dataHash.put(SurfConstants.ENCODER, this.encoder());
		dataHash.put(SurfConstants.RTP, this.RTP());
		dataHash.put(SurfConstants.TOOL_TYPE, this.toolType);
		dataHash.put(SurfConstants.INPUT_FROM_RTP, this.inputFromRTP);
		dataHash.put(SurfConstants.APP_INFO,this.appInfo);
		dataHash.put(SurfConstants.GROUP_ID, new Integer(this.groupId));
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    		
    }
	
    public String configure_voice_tool_msg()
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
