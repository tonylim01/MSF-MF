package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class voice_rtp_control {
	private String decoderType;
	private String encoderType;
	private String remoteIp;
	private String toolType;
	private String reqType;
	private int localUdpPort;
	private int remoteUdpPort;
	private int outPayloadType;
	private int inPayloadType;
	private int toolId;
	private int reqId;
	private int backendToolId;
	
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
	
	public void LocalUdpPort(int localUdpPort)
	{
		this.localUdpPort = localUdpPort;
	}
	
	public void RemoteUdpPort(int remoteUdpPort)
	{
		this.remoteUdpPort = remoteUdpPort;
	}
	
	public void OutPayloadType(int outPayloadType)
	{
		this.outPayloadType = outPayloadType;
	}
	
	public void InPayloadType(int inPayloadType)
	{
		this.inPayloadType = inPayloadType;
	}
	
	public void ToolId(int toolId)
	{
		this.toolId = toolId;
	}
	
	public void ReqId(int reqId)
	{
		this.reqId = reqId;
	}
	
	public void BackendToolId(int backendToolId)
	{
		this.backendToolId = backendToolId;
	}
	
	private JSONObject decoder()
    {
		HashMap<String,Object> decoder_temp_hash = new HashMap<String,Object>();
		
		decoder_temp_hash.put(SurfConstants.TYPE, this.decoderType);
		JSONObject decoder_temp = new JSONObject(decoder_temp_hash);
		
		return decoder_temp;
    }
	
	private JSONObject encoder()
    {
		HashMap<String,Object> encoder_temp_hash = new HashMap<String,Object>();
		
		encoder_temp_hash.put(SurfConstants.TYPE, this.encoderType);
		JSONObject encoder_temp = new JSONObject(encoder_temp_hash);
		
		return encoder_temp;
    }
	
	private JSONObject RTP()
    {
		HashMap<String,Object> rtpTempHash = new HashMap<String,Object>();
		
		rtpTempHash.put(SurfConstants.LOCAL_UDP_PORT, this.localUdpPort);
		rtpTempHash.put(SurfConstants.REMOTE_UDP_PORT, this.remoteUdpPort);
		rtpTempHash.put(SurfConstants.REMOTE_IP, this.remoteIp);
		rtpTempHash.put(SurfConstants.IN_PAYLOAD_TYPE, new Integer(this.inPayloadType));
		rtpTempHash.put(SurfConstants.OUT_PAYLOAD_TYPE, new Integer(this.outPayloadType));
		
		JSONObject rtpTemp = new JSONObject(rtpTempHash);
		
		return rtpTemp;
    }
	
	private JSONObject Data()
    {
		HashMap<String,Object> dataHash = new HashMap<String,Object>();
		
		dataHash.put(SurfConstants.DECODER, this.decoder());
		dataHash.put(SurfConstants.ENCODER, this.encoder());
		dataHash.put(SurfConstants.RTP, this.RTP());
		dataHash.put(SurfConstants.TOOL_TYPE, this.toolType);
		dataHash.put(SurfConstants.BACKEND_TOOL_ID, this.backendToolId);
		JSONObject data = new JSONObject(dataHash);
		
    		return data;
    		
    }
	
    public String voice_rtp_control_msg()
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
