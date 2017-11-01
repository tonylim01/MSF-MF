package swmrf.media.surfhmp.jsonifapi;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SurfConnect {
	private int	majorVersion;
	private int	minorVersion;
	private int	keepAliveTimeout;
	
	public void SetConnectVersion(int majorVersion,int minorVersion)
	{
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	public void KeepAliveTimeout(int keepAliveTimeout)
	{
		this.keepAliveTimeout = keepAliveTimeout;
	}
	
	@SuppressWarnings("unchecked")
	public String SurfConnectMsg()
    {
    		HashMap<String,Object> connectHash = new HashMap<String,Object>();
    		HashMap<String,Object> senddataHash = new HashMap<String,Object>();
		
		JSONArray api_version = new JSONArray();
		api_version.add(new Integer(this.majorVersion));
		api_version.add(new Integer(this.minorVersion));
		
		connectHash.put(SurfConstants.KEEP_ALIVE_TIMEOUT, new Integer(this.keepAliveTimeout));
		connectHash.put(SurfConstants.API_VERSION, api_version);
		JSONObject connect = new JSONObject(connectHash);
		
		senddataHash.put(SurfConstants.CONNECT, connect);
		JSONObject sendData = new JSONObject(senddataHash);
		
    		return sendData.toString();
    		
    }
}
