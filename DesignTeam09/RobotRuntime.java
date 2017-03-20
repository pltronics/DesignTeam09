import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;

public class RobotRuntime {
    class pinout {
	String path;
	boolean forward;
	short dutyCycle;
	File direction;
	public pinout (String s) {
	
	    path = s;
	}

    }

    //class rearmotor

    public static void main(String[] args) throws InterruptedException {
	initialization();
	/*	try {
	FileWriter value = new FileWriter("/sys/class/gpio/gpio4/value");
      	Scanner s = new Scanner(System.in);
	int h=-1;
	int l=-1;
	System.out.println("Enter 1 time");
	while(h == -1)
		h = s.nextInt();
	System.out.println("Enter 0 time");
	while(l==-1)
		l = s.nextInt();
	while(true){
		value.write("1");
		value.flush();
		for(int i = 0; i < h; i++)
			Thread.sleep(1);
//		long start = System.nanoTime();
//		while(start + h >= System.nanoTime());

		value.write("0");
		value.flush();
		for(int i = 0; i < l; i++)
			Thread.sleep(1);
//		start = System.nanoTime();
//		while(start + l >= System.nanoTime());

	}


	
	} catch(IOException e){
        e.printStackTrace();
        }
	*/
	HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
	server.createContext("/test", new MyHandler());
	server.setExecutor(null); // creates a default executor
	server.start();
    }

    private static void initialization(){
	gpiostart("4");
	gpiostart("14");
	gpiostart("7");
	gpiostart("17");
	gpiostart("5");


    }

    static class MyHandler implements HttpHandler throws InterruptedException {
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
    
    private static void gpiostart(String p){
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


