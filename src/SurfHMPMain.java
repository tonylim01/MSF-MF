import swmrf.media.surfhmp.jsonifapi.SurfCommonLog;
import swmrf.media.tcpsocket.TCPSocketAPI;

public class SurfHMPMain {
	
	public static final String SURFHMP_DISCONNECT = "disconnect";
	public static final String SURFHMP_VOICE_READ_FILE = "voice_readfile";
	public static final String SURFHMP_VOICE_MIXER = "voice_mixer";
	public static final String SURFHMP_RECORD = "record";
	public static final String SURFHMP_DTMF_GET = "dtmf_get";
	public static final String SURFHMP_DTMF_GEN = "dtmf_gen";
	
	public static final String SURFHMP_IP = "192.168.5.63";
	public static final int SURFHMP_PORT = 7788;

	public static void main(String[] args) throws InterruptedException {
		
		SurfFunction func = new SurfFunction();
		String startArgs = args[0];
		
		System.out.println("Start SurfHMP Process");
		
		TCPSocketAPI tcpClient = new TCPSocketAPI();
		
		try {
			tcpClient.client(SURFHMP_IP, SURFHMP_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tcpClient.processFlag = true;
		
		tcpClient.start();
		
		func.func_surf_start(tcpClient);
		
		switch(startArgs)
		{
			case SURFHMP_DISCONNECT:
			{
				SurfCommonLog.Log("################## disconnect ################");
				func.func_clear_all_tools(tcpClient);
				func.func_system_status(tcpClient);
				
				Thread.sleep(1000);
				
				func.func_disconnect(tcpClient);
				break;
			}
			
			case SURFHMP_VOICE_READ_FILE:
			{
				SurfCommonLog.Log("################## voice_readfile ################");
				int i = 0;
				for(i = 0;i < 400;i ++)
				{
					func.func_voice_tool(tcpClient);
					func.func_file_reader(tcpClient);
					func.func_fileapp_start(tcpClient);
					func.func_play(tcpClient);
					Thread.sleep(100);
//					Thread.sleep(50000);
//					func.func_clear_all_tools(tcp_client);
//					func.func_remove(tcpClient);
//					Thread.sleep(100);
//					func.func_clear_all_tools(tcpClient);
//					Thread.sleep(10000);
//					func.func_disconnect(tcpClient);
					func.id ++;
				}
				break;
			}
			case SURFHMP_VOICE_MIXER:
			{
				SurfCommonLog.Log("################## voice_mixer ################");
				func.func_system_status_mix(tcpClient);
				func.func_voice_mixer(tcpClient);
				func.func_voice_rtp_control(tcpClient);
				func.func_voice_mix_participants(tcpClient);
				break;
			}
			case SURFHMP_RECORD:
			{
				SurfCommonLog.Log("################## record ################");
				func.func_system_status(tcpClient);
				func.func_file_reader(tcpClient);
				break;
			}
			case SURFHMP_DTMF_GET:
			{
				SurfCommonLog.Log("################## dtmf_get ################");
				func.func_system_status(tcpClient);
				func.func_dtmf_tool(tcpClient);
				break;
			}
			case SURFHMP_DTMF_GEN:
			{
				SurfCommonLog.Log("################## dtmf_gen ################");
				func.func_system_status(tcpClient);
				func.func_dtmf_generate(tcpClient);
				break;
			}
		}
		
	}

}
