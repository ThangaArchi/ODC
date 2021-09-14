package oem.edge.common.perform;

import java.text.*;

/**
   Stopwatch to measure performance

<b>Usage:</b>
<pre>
	EdgeStopwatch watch = new EdgeStopwatch;
<p />
	watch.start();
	//
	// code to be timed
	//
	watch.stop();
        //
        // code not to be timed
	//
	watch.resume();
	//
	// code to be timed
	//
	System.out.println("elapsed time at checkpoint1: " + watch.getElapsedTimeSecs() + " seconds.");
	//
	// code to be timed
	//
	System.out.println("elapsed time at checkpoint2: " + watch.getElapsedTimeSecs() + " seconds.");
	//
	// code to be timed
	//
	watch.stop();
	System.out.println("total elapsed time: " + watch.getElapsedTimeSecs() + " seconds.");

</pre>	

 */
public class EdgeStopwatch {
    private long totalTime = 0;
    private long lastTime = 0;
    private boolean running = false;

    /**
      EdgeStopwatch() {}
    */
    public EdgeStopwatch() {}
    /**
      EdgeStopwatch() {boolean start}
    */  
    public EdgeStopwatch(boolean start) {
	if (start) {
	    start();
      	}
    }

    /**
       start()
    */  
    public void start() {
	if (!running)
	    {
		totalTime = 0;	
		lastTime = System.currentTimeMillis();
		running = true;
	    }
    }

    /**
       stop()
    */  
    public void stop() {
	if (running) 
	    {
		totalTime += System.currentTimeMillis()-lastTime;
		running = false;
	    }
    }

    /**
       resume()
    */  
    public void resume() {
	if (!running) 
	    {
		lastTime = System.currentTimeMillis();
		running = true;
	    }
    }

    /**
       reset()
    */
    public void reset() {
      	totalTime = 0;
      	lastTime = 0;
      	running = false;
    }

    /**
       getElapsedTimeMillis()
       returns elapsed time so far, but does not stop the clock
    */
    public long getElapsedTimeMillis(){
      	if (running) {
	    long currentTime = System.currentTimeMillis();
	    totalTime += currentTime-lastTime;
	    lastTime = currentTime;
      	} 
      	return totalTime;
    }
    /**
       getElapsedTimeSecs()
       returns elapsed time so far, but does not stop the clock
    */
    public double getElapsedTimeSecs(){
	long millis = getElapsedTimeMillis();
	double secs = millis/1000d;
	return secs;
    }
    /**
       getElapsedTimeSecsString()
    */
    public String getElapsedTimeSecsString(){
	return NumberFormat.getInstance().format(getElapsedTimeSecs());
    }
    /**
       printMillis(String message)
    */
    public void printMillis(String message){
	String results = message + ": " + getElapsedTimeMillis() + " ms";
	System.out.println(results);
    }

    /**
       printSecs(String message)
    */
    public void printSecs(String message){
	String results = message + ": " + getElapsedTimeSecs() + " secs";
	System.out.println(results);
    }


}
