package swmrf.media.tcpsocket;

import java.io.IOException;
import java.io.InputStream;

public class TCPSocketUtil {
	
	public static final int HEADER_LEN = 4;
	public static final int INIT_HEADER_LEN = 7;
	
	public String getSurfInitHeader(InputStream inFromServer)
	{
		
		byte [] data = new byte[1024];
		int	nRet = 0;
		int nCurrLen = 0;
		
		while(true)
		{
			try {
				nRet = inFromServer.read(data,nCurrLen,INIT_HEADER_LEN);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(nRet <= 0)
				return null;
			
			nCurrLen = nCurrLen+nRet;
			
			if(nCurrLen == INIT_HEADER_LEN)
				break;
		}
		
		String JsonData = new String(data,0,nCurrLen);
	
		return JsonData;
	}
	
	public int getSurfBodyLength(InputStream inFromServer)
	{
		int nLen = 0;
		int	nRet = 0;
		int nCurrLen = 0;
		byte [] Length = new byte[128];
		
		while(true)
		{
			try {
				nRet = inFromServer.read(Length,nCurrLen,HEADER_LEN-nCurrLen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(nRet <= 0)
				return -1;
			
			nCurrLen = nCurrLen+nRet;
			
			if(nCurrLen == HEADER_LEN)
				break;
		}
		
		nLen = this.getBigEndian(Length);
		
		return nLen;
	}
	
    public int getBigEndian(byte[] v){
		int[] arr = new int[4];
		for(int i=0;i<4;i++){
			arr[i] = (int)(v[3-i] & 0xFF);
		}
		return ((arr[0] << 24) + (arr[1] << 16) + (arr[2] << 8) + (arr[3] << 0));
	}

	public String getSurfBodyRead(InputStream inFromServer, int nLen)
	{
		int	nRet = 0;
		int nCurrLen = 0;
		String JsonData;
		
		byte [] byteJsonData = new byte[4096];
		
		while(true)
		{
			try {
				nRet = inFromServer.read(byteJsonData,nCurrLen,nLen-nCurrLen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(nRet <= 0)
				return null;
			
			nCurrLen = nCurrLen+nRet;
			if(nCurrLen == nLen)
				break;
		}
		
		JsonData = new String(byteJsonData,0,nLen);
		
		return JsonData;
	}
}
