package be.tarsos.dsp.example;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{

    private Socket echoSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    
    private String host;
    
    private int port;
    
    public Client(String host, int port) {
    	this.host = host;
    	this.port = port;
    }
    
    public void run() {
        try {

            System.out.println("Connecting to host " + host + " on port " + port + ".");

            try {
                echoSocket = new Socket(host, port);
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + host);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Unable to get streams from server");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void close(){
        try {
            out.close();
            in.close();
			echoSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void sendFrequency(int frequency){
    	if(out != null){
	    	JSONObject jsonObj = new JSONObject();
	    	jsonObj.put("cmd", "frequency");
	    	jsonObj.put("frequency", frequency);
	    	out.print(jsonObj.toJSONString());
	    	out.flush();
    	}
    }
    
    public void sendLength(int length){
    	if(out != null){
	    	JSONObject jsonObj = new JSONObject();
	    	jsonObj.put("cmd", "length");
	    	jsonObj.put("length", length);
	    	out.print(jsonObj.toJSONString());
	    	out.flush();
    	}
    }
    
    public void sendColor(LEDColor color){
    	if(out != null){
	    	String jsonColor = color.toJSONString();
	    	out.print(jsonColor);
	    	out.flush();
    	}
    }
}
