package designTeam09;


import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/*import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;*/

public class Main {
	private static Path fFilePath;
	private final static Charset ENCODING = StandardCharsets.UTF_8; 
	public static String direction;
	public static String speed;
	public static String special;
	public static pinout rightforward;
	public static pinout rightback;
	public static pinout leftforward;
	public static pinout leftback;
	public static pinout valve;
	public static pinout topdown;
	public static pinout topup;
	public static pinout pump;
//	gpiostart("4"); // Right Motor Forward
//	gpiostart("14"); // Right Motor Backward
//	gpiostart("7"); // Left Motor Forward
//	gpiostart("17"); // Left Motor Backward
//	gpiostart("5"); // Valve
//	gpiostart("6"); // Top Motor Down
//	gpiostart("16");// Top Motor Up
	
	
	class pinout {
		String path;
		public pinout(String s) {
			
		}
		public void main(String s) {
		
		   
		}
		public void setpath(String s) {
			 path = s;
			
		}
		public void on(){
			try {
				FileWriter value = new FileWriter(path);
					value.write("1");
					value.flush();
				   	} catch(IOException e){
				    e.printStackTrace();
				    }
		}
		public void off(){
			try {
				FileWriter value = new FileWriter(path);
					value.write("0");
					value.flush();
				   	} catch(IOException e){
				    e.printStackTrace();
				    }			
		}
	 }
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		initialization();
/*		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/test", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();*/
		
		Thread reader = new Thread () {
			public void run () {
				while (true)
					reading();
			}
		};
		Thread running = new Thread () {
			public void run (){
				runtime();
			}
		};
		reader.start();
		running.start();
	}
	
	private static void runtime(){
		while (true){
			//todo for high signal of PWM(s)
			if(direction == "Forward"){
				rightback.off();
				leftback.off();				
				rightforward.on();
				leftforward.on();
			}
			if(direction == "Backward") {
				rightforward.off();
				leftforward.off();				
				rightback.on();
				leftback.on();
			}
			if(direction == "Down") {
				topup.off();	
				topdown.on();

			}
			if(direction == "Up") {
				topdown.off();
				topup.on();
			}
			if(special == "Dive"){
				rightback.off();
				leftback.off();	
				rightforward.off();
				leftforward.off();
				topdown.off();
				topup.off();
				valve.off();
				dive();				
			}
			try {
				Thread.sleep(Integer.parseInt(speed));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			off();
			try {
				Thread.sleep(10-Integer.parseInt(speed));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private static void off() {
		// TODO Auto-generated method stub
		rightforward.off();
		leftforward.off();
		topdown.off();
		topup.off();
	}

	private static void dive() {
		// TODO Auto-generated method stub
		valve.off();
		pump.on();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pump.off();
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valve.on();
	}

	private static void reading(){
	    ReadWithScanner("/home/pi/Desktop/JsonTestFile/json/json");
	    try {
			processLineByLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	public static void ReadWithScanner(String aFileName){
		    fFilePath = Paths.get(aFileName);
	}
	public final static void processLineByLine() throws IOException {
		try (Scanner scanner =  new Scanner(fFilePath, ENCODING.name())){
			while (scanner.hasNextLine()){
				processLine(scanner.nextLine());
			}      
		}
	}
	protected static void processLine(String aLine){
		//use a second Scanner to parse the content of each line 
		Scanner scanner = new Scanner(aLine);
		scanner.useDelimiter(":");
		if (scanner.hasNext()){
			//assumes the line has a certain structure
			String function = scanner.next();
			String value = scanner.next();	
			if (function == "Direction")
				direction = value;
			else if (function == "Speed")
				speed = value;
			else if (function == "Special")
				special = value;
		}
		else {
			System.out.println("Empty or invalid line. Unable to process.");
		}
	}
	
    private static void initialization(){
    rightforward.setpath(gpiostart("4")); // Right Motor Forward
	rightback.setpath(gpiostart("14")); // Right Motor Backward
	leftforward.setpath(gpiostart("7")); // Left Motor Forward
	leftback.setpath(gpiostart("17")); // Left Motor Backward
	valve.setpath(gpiostart("5")); // Valve
	topdown.setpath(gpiostart("6")); // Top Motor Down
	topup.setpath(gpiostart("16"));// Top Motor Up
	pump.setpath(gpiostart("25"));// Pump
    }
	
/*    static class MyHandler implements HttpHandler throws InterruptedException {
        @Override
	public void handle(HttpExchange t) throws IOException {
	    String response = "This is the response";
	    t.sendResponseHeaders(200, response.length());
	    OutputStream os = t.getResponseBody();
	    os.write(response.getBytes());
	    os.close();
  	try {
	FileWriter value = new FileWriter("/sys/class/gpio/gpio4/value");
		value.write("1");
		value.flush();
		for(int i = 0; i < 5; i++)
			Thread.sleep(1);
		value.write("0");
		value.flush();
		for(int i = 0; i < 5; i++)
			Thread.sleep(1);
	   	} catch(IOException e){
	    e.printStackTrace();
	    }
	}
}
*/
   
    
    private static String gpiostart(String p){
	try {
	    FileWriter export = new FileWriter("/sys/class/gpio/export");
	    File exportFileCheck = new File("/sys/class/gpio/gpio"+p);
            if (exportFileCheck.exists()) {
                gpioend(p);
	    }
	export.write(p);
	export.flush();

	FileWriter direction = new FileWriter("/sys/class/gpio/gpio"+p+"/direction");
	direction.write("out");
	direction.flush();

	} catch(IOException e){
	    e.printStackTrace();
	}
	return "/sys/class/gpio/gpio"+p+"/value";
    }
	
    private static void gpioend(String p) {
	try {
	    FileWriter unexport = new FileWriter("/sys/class/gpio/unexport");
	    unexport.write(p);
	    unexport.flush();
	}catch(IOException e) {
	    e.printStackTrace();
	}


    } 
    
    
}
