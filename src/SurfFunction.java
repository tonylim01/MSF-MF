import swmrf.media.surfhmp.jsonifapi.SurfCommonLog;
import swmrf.media.surfhmp.jsonifapi.SurfConnect;
import swmrf.media.surfhmp.jsonifapi.SurfDisConnect;
import swmrf.media.surfhmp.jsonifapi.configure_dtmf_tool;
import swmrf.media.surfhmp.jsonifapi.configure_voice_tool;
import swmrf.media.surfhmp.jsonifapi.create_file_reader;
import swmrf.media.surfhmp.jsonifapi.dtmf_generate;
import swmrf.media.surfhmp.jsonifapi.enable_system_status;
import swmrf.media.surfhmp.jsonifapi.voice_file_append_start;
import swmrf.media.surfhmp.jsonifapi.voice_file_play;
import swmrf.media.surfhmp.jsonifapi.voice_file_recording;
import swmrf.media.surfhmp.jsonifapi.voice_mix_participants;
import swmrf.media.surfhmp.jsonifapi.voice_mixer;
import swmrf.media.surfhmp.jsonifapi.voice_remve;
import swmrf.media.surfhmp.jsonifapi.voice_rtp_control;
import swmrf.media.tcpsocket.TCPSocketAPI;

public class SurfFunction {
	
	public int id = 0;
	public static final String SURFAPI = "surfapi";
	
	public void func_surf_start(TCPSocketAPI tcp_client)
    {
		tcp_client.SocketWrite(SURFAPI);
		
		SurfConnect surfCconn = new SurfConnect();
		
		surfCconn.SetConnectVersion(SurfMainConstants.MAJOR_VERSION,SurfMainConstants.MINOR_VERSION);
		surfCconn.KeepAliveTimeout(SurfMainConstants.KEEP_ALIVE_TIMEOUT);
		
		String sendData = surfCconn.SurfConnectMsg();
		
		tcp_client.SocketWriteLen(sendData);
		
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
	
	public void func_file_reader(TCPSocketAPI tcp_client)
    {
		create_file_reader fileReader = new create_file_reader();
		
		fileReader.EventsType(SurfMainConstants.ALL);
		fileReader.ToolType(SurfMainConstants.FILE_READER);
		fileReader.ReqType(SurfMainConstants.SET_CONFIG);
		fileReader.VideoEnabled(false);
		fileReader.AudioEnabled(true);
		fileReader.ToolId(id+1);
		fileReader.ReqId(id+0);
		fileReader.AudioDTSToolIds(4);
		
		String sendData = fileReader.create_file_reader_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_fileapp_start(TCPSocketAPI tcp_client)
    {
		voice_file_append_start fileAppStart = new voice_file_append_start();
		
		fileAppStart.Duration(SurfMainConstants.DURATION);
		fileAppStart.ToolId(id+1);
		fileAppStart.ReqId(id+1001);
		fileAppStart.ReqType(SurfMainConstants.COMMAND);
		fileAppStart.CmdType(SurfMainConstants.PLAY_LIST_APPEND);
		fileAppStart.FileList(0,"InputFiles/californication.wav");
		fileAppStart.FileList(1,"InputFiles/muki_short.wav");
		String sendData = fileAppStart.file_append_start_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_play(TCPSocketAPI tcp_client)
    {
		voice_file_play play = new voice_file_play();
		
		play.CmdType(SurfMainConstants.PLAY);
		play.ReqType(SurfMainConstants.COMMAND);
		play.ToolId(id+1);
		play.ReqId(id+1002);
		
		String sendData = play.file_play_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_clear_all_tools(TCPSocketAPI tcp_client)
    {
		SurfDisConnect surfDisconn = new SurfDisConnect();
		
		surfDisconn.req_id(0);;
		surfDisconn.req_type(SurfMainConstants.COMMAND);;
		surfDisconn.cmd_type(SurfMainConstants.CLEAR_ALL_TOOLS);
		
		String sendData = surfDisconn.clear_all_tools();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_system_status(TCPSocketAPI tcp_client)
    {
    		enable_system_status systemStatus = new enable_system_status();
    		
    		systemStatus.StatusType(SurfMainConstants.PERFORMANCE);
    		systemStatus.StatusPeriod(SurfMainConstants.PERIOD);
    		systemStatus.ReqId(0);
    		systemStatus.ReqType(SurfMainConstants.SET_CONFIG);
    		
    		String sendData = systemStatus.system_status();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_disconnect(TCPSocketAPI tcp_client)
    {
    		SurfDisConnect surfDisconn = new SurfDisConnect();
		
    		surfDisconn.reason(SurfMainConstants.FINISHED);;
    		surfDisconn.error_code(SurfMainConstants.ERROR_CODE_NORMAL);
    		
    		String sendData = surfDisconn.SurfDisConnectMsg();
			
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_system_status_mix(TCPSocketAPI tcp_client)
    {
    		enable_system_status systemStatus = new enable_system_status();
    		
    		systemStatus.StatusType(SurfMainConstants.PERFORMANCE);
    		systemStatus.StatusPeriod(SurfMainConstants.PERIOD);
    		systemStatus.ReqId(0);
    		systemStatus.ReqType(SurfMainConstants.SET_CONFIG);
		
    		String sendData = systemStatus.system_status();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    public void func_voice_mixer(TCPSocketAPI tcp_client)
    {
    		voice_mixer vceMix = new voice_mixer();
    		
    		vceMix.ToolType(SurfMainConstants.VOICE_MIXER);
    		vceMix.ToolId(20000);
    		vceMix.ReqId(0);
    		vceMix.ReqType(SurfMainConstants.SET_CONFIG);
    		vceMix.SamplingRate(8000);
    		vceMix.HangoverPeriod(500);
    		vceMix.DominantSpeakers(5);
		
    		String sendData = vceMix.voice_mixer_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
    }
    
    public void func_voice_tool(TCPSocketAPI tcp_client)
	{
		configure_voice_tool cfgVoiceTool = new configure_voice_tool();
		
		cfgVoiceTool.StatusType(SurfMainConstants.ALL);
		cfgVoiceTool.EventsType(SurfMainConstants.ALL);
		cfgVoiceTool.DecoderType(SurfMainConstants.G711A);
		cfgVoiceTool.EncoderType(SurfMainConstants.AMR_WB);
		cfgVoiceTool.RemoteIp(SurfMainConstants.LOCAL_IP);
		cfgVoiceTool.ToolType(SurfMainConstants.VOICE_P2P);
		cfgVoiceTool.ReqType(SurfMainConstants.SET_CONFIG);
		cfgVoiceTool.AppInfo(SurfMainConstants.APP_INFO);
		cfgVoiceTool.Period(SurfMainConstants.PERIOD);
		cfgVoiceTool.PacketDuration(SurfMainConstants.PACKET_DURATION);
		cfgVoiceTool.LocalUdpPort(SurfMainConstants.LOCAL_UDP_PORT + this.id);
		cfgVoiceTool.RemoteUdpPort(SurfMainConstants.REMOTE_UDP_PORT + this.id);
		cfgVoiceTool.OutPayloadType(SurfMainConstants.OUT_PAYLOAD_TYPE);
		cfgVoiceTool.GroupId(this.id+10000);
		cfgVoiceTool.ToolId(this.id+20000);
		cfgVoiceTool.ReqId(this.id+30000);
		
		String sendData = cfgVoiceTool.configure_voice_tool_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
	
	public void func_voice_rtp_control(TCPSocketAPI tcp_client)
	{
		voice_rtp_control vceRtpCtl = new voice_rtp_control();
		
		vceRtpCtl.DecoderType(SurfMainConstants.G711A);
		vceRtpCtl.EncoderType(SurfMainConstants.AMR_WB);
		vceRtpCtl.RemoteIp(SurfMainConstants.LOCAL_IP);
		vceRtpCtl.ToolType(SurfMainConstants.VOICE_P2P);
		vceRtpCtl.ReqType(SurfMainConstants.SET_CONFIG);
		vceRtpCtl.LocalUdpPort(SurfMainConstants.LOCAL_UDP_PORT);
		vceRtpCtl.RemoteUdpPort(SurfMainConstants.REMOTE_UDP_PORT);
		vceRtpCtl.InPayloadType(SurfMainConstants.IN_PAYLOAD_TYPE);
		vceRtpCtl.OutPayloadType(SurfMainConstants.OUT_PAYLOAD_TYPE);
		vceRtpCtl.BackendToolId(20000);
		vceRtpCtl.ToolId(4);
		vceRtpCtl.ReqId(1003);
		
		String sendData = vceRtpCtl.voice_rtp_control_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
	
	public void func_voice_mix_participants(TCPSocketAPI tcp_client)
	{
		voice_mix_participants vceMixPart = new voice_mix_participants();
		
		vceMixPart.ToolId(20000);
		vceMixPart.ReqId(0);
		vceMixPart.ReqType(SurfMainConstants.SET_CONFIG);
		vceMixPart.DataToolId(0);
		vceMixPart.ID(0);
		vceMixPart.DataType(SurfMainConstants.REGULAR);
		
		String sendData = vceMixPart.voice_mix_participants_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
	
	public void func_voice_file_recording(TCPSocketAPI tcp_client)
	{
		voice_file_recording fileRecord = new voice_file_recording();
		
		fileRecord.ToolId(3);
		fileRecord.ReqId(1002);
		fileRecord.ReqType(SurfMainConstants.COMMAND);
		fileRecord.MaxSize(SurfMainConstants.MAX_SIZE);
		fileRecord.CmdType(SurfMainConstants.RECORD);
		fileRecord.FileName("RecordFiles/californication2.wav");
		
		String sendData = fileRecord.file_record_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
	
    public void func_dtmf_tool(TCPSocketAPI tcp_client)
	{
    		configure_dtmf_tool cfgDtmfTool = new configure_dtmf_tool();
		
    		cfgDtmfTool.DecoderType(SurfMainConstants.G711A);
    		cfgDtmfTool.EncoderType(SurfMainConstants.AMR_WB);
    		cfgDtmfTool.RemoteIp(SurfMainConstants.LOCAL_IP);
    		cfgDtmfTool.ToolType(SurfMainConstants.VOICE_P2P);
    		cfgDtmfTool.ReqType(SurfMainConstants.SET_CONFIG);
    		cfgDtmfTool.PacketDuration(SurfMainConstants.PACKET_DURATION);
    		cfgDtmfTool.LocalUdpPort(SurfMainConstants.LOCAL_UDP_PORT);
    		cfgDtmfTool.RemoteUdpPort(SurfMainConstants.REMOTE_UDP_PORT);
    		cfgDtmfTool.ToolId(4);
    		cfgDtmfTool.ReqId(1003);
		cfgDtmfTool.DtmfInPayloadType(101);
		cfgDtmfTool.DtmfOutPayloadType(101);
		cfgDtmfTool.EVG(true);
		cfgDtmfTool.DtmfGroup("DTMF_GROUP");
		cfgDtmfTool.EVD(true);
		
		String sendData = cfgDtmfTool.configure_dtmf_tool_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
    
    public void func_dtmf_generate(TCPSocketAPI tcp_client)
	{
    		dtmf_generate dtmfGen = new dtmf_generate();
		
    		dtmfGen.ToolId(1);
    		dtmfGen.ReqId(0);
    		dtmfGen.ReqType(SurfMainConstants.COMMAND);
    		dtmfGen.TotalDuration(300);
    		dtmfGen.CmdType(SurfMainConstants.GENERATE_TONE);
    		dtmfGen.NamedEvent("DTMF6");
    		dtmfGen.RtpOrInband(SurfMainConstants.RTP);
		
		String sendData = dtmfGen.dtmf_generate_msg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
    
    public void func_remove(TCPSocketAPI tcp_client)
	{
    		voice_remve remove = new voice_remve();
		
		remove.tool_id(this.id+20000);
		remove.req_id(this.id+30000);
		remove.req_type("remove");
		
		String sendData = remove.SurfRemoveMsg();
		
		tcp_client.SocketWriteLen(sendData);
		SurfCommonLog.Log("Wrote Data : "+sendData);
	}
}
