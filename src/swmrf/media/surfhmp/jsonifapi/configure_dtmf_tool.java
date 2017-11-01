package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class configure_dtmf_tool {
	private String decoderType;
	private String encodeType;
	private String remoteIp;
	private String toolType;
	private String reqType;
	private String dtmpGroup;
	private int packetDuration;
	private int localUdpPort;
	private int remoteUdpPort;
	private int toolId;
	private int reqId;
	private int dtmfInPayloadType;
	private int dtmfOutPayloadType;
	private boolean evg;
	private boolean evd;
	
	public void DecoderType(String decoderType)
	{
		this.decoderType = decoderType;
	}
	
	public void EncoderType(String encodeType)
	{
		this.encodeType = encodeType;
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
	
	public void DtmfGroup(String dtmpGroup)
	{
		this.dtmpGroup = dtmpGroup;
	}
	
	
	public void PacketDuration(int packetDuration)
	{
		this.packetDuration = packetDuration;
	}
	
	public void LocalUdpPort(int localUdpPort)
	{
		this.localUdpPort = localUdpPort;
	}
	
	public void RemoteUdpPort(int remoteUdpPort)
	{
		this.remoteUdpPort = remoteUdpPort;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void DtmfInPayloadType(int dtmfInPayloadType)
	{
		this.dtmfInPayloadType = dtmfInPayloadType;
	}
	
	public void DtmfOutPayloadType(int dtmfOutPayloadType)
	{
		this.dtmfOutPayloadType = dtmfOutPayloadType;
	}
	
	public void EVG(boolean evg)
	{
		this.evg = evg;
	}
	
	public void EVD(boolean evd)
	{
		this.evd = evd;
	}
	
	private JSONObject decoder()
    {
		HashMap<String,Object> decoderTempHash = new HashMap<String,Object>();
		
		decoderTempHash.put(SurfConstants.TYPE, this.decoderType);
		JSONObject decoder_temp = new JSONObject(decoderTempHash);
		
		return decoder_temp;
    }
	
	private JSONObject EVG()
    {
		HashMap<String,Object> decoderTempHash = new HashMap<String,Object>();
		
		decoderTempHash.put(SurfConstants.ENABLED, this.evg);
		JSONObject decoder_temp = new JSONObject(decoderTempHash);
		
		return decoder_temp;
    }
	
	@SuppressWarnings("unchecked")
	private JSONObject EVD()
    {
		HashMap<String,Object> decoderTempHash = new HashMap<String,Object>();
		JSONArray eventsarr = new JSONArray();
		eventsarr.add(this.dtmpGroup);
		decoderTempHash.put(SurfConstants.ENABLED, this.evd);
		decoderTempHash.put(SurfConstants.EVENTS, eventsarr);

		JSONObject decoder_temp = new JSONObject(decoderTempHash);
		
		return decoder_temp;
    }
	
	private JSONObject encoder()
    {
		HashMap<String,Object> encoderTempHash = new HashMap<String,Object>();
		
		encoderTempHash.put(SurfConstants.TYPE, this.encodeType);
		encoderTempHash.put(SurfConstants.PACKET_DURATION, new Integer(this.packetDuration));
		JSONObject encoder_temp = new JSONObject(encoderTempHash);
		
		return encoder_temp;
    }
	
	private JSONObject RTP()
    {
		HashMap<String,Object> rtpTtempHash = new HashMap<String,Object>();
		
		rtpTtempHash.put(SurfConstants.LOCAL_UDP_PORT, this.localUdpPort);
		rtpTtempHash.put(SurfConstants.REMOTE_UDP_PORT, this.remoteUdpPort);
		rtpTtempHash.put(SurfConstants.REMOTE_IP, this.remoteIp);
		rtpTtempHash.put(SurfConstants.DTMF_IN_PAYLOAD_TYPE, new Integer(this.dtmfInPayloadType));
		rtpTtempHash.put(SurfConstants.DTMF_OUT_PAYLOAD_TYPE, new Integer(this.dtmfOutPayloadType));
		
		JSONObject RTP_temp = new JSONObject(rtpTtempHash);
		
		return RTP_temp;
    }
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		
		dataHash.put(SurfConstants.DECODER, this.decoder());
		dataHash.put(SurfConstants.ENABLED, this.encoder());
		dataHash.put(SurfConstants.RTP, this.RTP());
		dataHash.put(SurfConstants.EVG, this.EVG());
		if(evd == true)
			dataHash.put(SurfConstants.EVD, this.EVD());
		dataHash.put(SurfConstants.TOOL_TYPE, this.toolType);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    		
    }
	
    public String configure_dtmf_tool_msg()
    {
    		HashMap<String,Object> sendDataHash = new HashMap<String,Object>();
    		HashMap<String,Object> toolReqHash = new HashMap<String,Object>();
		
    		toolReqHash.put(SurfConstants.TOOL_ID, new Integer(this.toolId));
    		toolReqHash.put(SurfConstants.REQ_ID, new Integer(this.reqId));
    		toolReqHash.put(SurfConstants.TOOL_TYPE, this.reqType);
    		toolReqHash.put(SurfConstants.DATA, this.Data());
		
		JSONObject toolReq = new JSONObject(toolReqHash);
		sendDataHash.put(SurfConstants.TOOL_REQ, toolReq);
		JSONObject sendData = new JSONObject(sendDataHash);
    		return sendData.toString();
    		
    }
}
