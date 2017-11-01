package swmrf.media.tcpsocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.json.simple.parser.ParseException;

import swmrf.media.surfhmp.jsonifapi.JsonToolParser;

public class TCPSocketAPI extends Thread{
	Socket socket;
	DataOutputStream outToServer;
	InputStream inFromServer;
	
	public boolean processFlag;
	
    public void client(String server,int port) throws Exception
    {
    		socket = new Socket(server , port);
    		
    		outToServer = new DataOutputStream(socket.getOutputStream());
    		inFromServer = socket.getInputStream();
		
    }
    
    public void SocketWriteLen(String Input)
    {
    		try {
    			outToServer.write(getLittleEndian(Input.length()));
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    	
    		byte [] data = new byte[1024];
		data = Input.getBytes();
		
    		try {
				outToServer.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    public void SocketWrite(String Input)
    {
    		byte [] data = new byte[1024];
		data = Input.getBytes();
		
    		try {
				outToServer.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    

    public byte[] getLittleEndian(int v){
	    	byte[] buf = new byte[4];
	    	buf[3] = (byte)((v >>> 24) & 0xFF);
	    	buf[2] = (byte)((v >>> 16) & 0xFF);
	    	buf[1] = (byte)((v >>> 8) & 0xFF);
	    	buf[0] = (byte)((v >>> 0) & 0xFF);
	    	return buf;
    	}

	@Override
	public void run() {

		TCPSocketUtil socketUtil = new TCPSocketUtil();
		String initHeader = socketUtil.getSurfInitHeader(inFromServer);
		if(initHeader == null)
		{
			this.processFlag = false;
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
        while(processFlag)
        {
    			try {
				int nLen = socketUtil.getSurfBodyLength(inFromServer);
				if(nLen < 0)
				{
					this.processFlag = false;
					socket.close();
					break;
				}
				
				String JsonData = socketUtil.getSurfBodyRead(inFromServer, nLen);
				if(JsonData == null)
				{
					this.processFlag = false;
					socket.close();
					break;
				}
				JsonToolParser recv_parser = new JsonToolParser();
				recv_parser.RecvParser(JsonData);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
	}
}
