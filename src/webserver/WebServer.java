package webserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebServer {

    private ServerSocket ss;
    
    //Necessário mudar para a pasta dos arquivos home.html e kurama.jpg
    private String filesPath = "C:/dev/aaa/WebServer/src/site"; 
    
    private String response = "";

    public void setUp() {
        try {
            ss = new ServerSocket(8081);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket waitForConnections() {
        try {
            return ss.accept();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Socket Error!");
        }
    }

    public String receiveRequest(Socket s) {
        
        String command = null;
        String line;
        
        try {
            
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            command = in.readLine();
            
            while ((line = in.readLine()).length() > 0) {
                continue;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return command;
        
    }

    public byte[] processRequest(String request) {
    	String path = request != null ? request.split(" ")[1] : "/";
    	path = path.equals("/") ? "/home.html" : path;
        
        try{
        	Path file = Paths.get(filesPath + path);
        	byte[] responseData = Files.readAllBytes(file);
        	
        	response += "HTTP/1.1 200 OK\n";
        	response += "Content-Type: ";
        	response += path.endsWith(".html") ? "text/html\n" : path.endsWith(".jpg") ? "img/jpg\n" : "\n";
        	response += "Server: Kurama 9.0\n";
        	response += "Connection: close\n";
        	response += ("Content-Length: " + responseData.length + "\n\n");
            
            return responseData;
            
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		return null;
        
    }

    public void sendResponse(byte responseData[], Socket s) {        
        try{
            OutputStream out = s.getOutputStream();
            out.write(response.getBytes());
            response = "";
            out.write(responseData);
            out.flush();
            out.close();
            s.close();            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {        
        WebServer ws = new WebServer();
        ws.setUp();
        while(true){
            Socket s = ws.waitForConnections();
            String request = ws.receiveRequest(s);
            byte[] response = ws.processRequest(request);
            ws.sendResponse(response, s);
        }
        
    }
    
}



