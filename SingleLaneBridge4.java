import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SingleLaneBridge4 {
	
	 static int RedCarCounter = 0;//metrthths gia to posa kokkina autokinhta ftanoun
	 static int BlueCarCounter = 0;//metrthths gia to posa mple autokinhta ftanoun
	
    public static void main(String[] args) 
    {
        final Bridge bridge = new Bridge();
         
        Thread thNorthbound = new Thread( new Runnable() { //dinoume to runnable sto constructor tou thread gia na ektelestei se auto to run
             
            @Override
            public void run() {
            	int j=0;
                while(j<2)
                {
                    Car car = new Car(bridge);
                    Thread th = new Thread(car);
                    car.setName("Red car : "+th.getId());
                    car.setColor("Red");
                    th.start();
                    RedCarCounter++;//au3anw ton metrhth kata ena otan erxetai ena kainourgio autokinhto
                    j++;
                    try
                    {
                        TimeUnit.SECONDS.sleep((long)(Math.random()*10));
                    }
                    catch(InterruptedException iex)
                    {
                        iex.printStackTrace();
                    }
                }
                 
            }
        });
         
        Thread thSouthbound = new Thread( new Runnable() {//dinoume to runnable sto constructor tou thread gia na ektelestei se auto to run
             
            @Override
            public void run() {
                int i =0; 
                while(i<10)
                {
                    Car car = new Car(bridge);
                    Thread th = new Thread(car);
                    car.setName("Blue Car : "+th.getId());
                    car.setColor("Blue");
                    th.start();
                    BlueCarCounter++;//au3anw ton metrhth kata ena otan erxetai ena kainourgio autokinhto
                    i++;
                    try
                    {
                        TimeUnit.SECONDS.sleep((long)(Math.random()*10));
                    }
                    catch(InterruptedException iex)
                    {
                        iex.printStackTrace();
                    }
                }
            }
        });
         
        thNorthbound.start();
        thSouthbound.start();
    }
 
}
 
class Bridge
{
	Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();
	
	boolean flag =false;//ka8orizei pianou seira einai
    public Bridge()
    {
        
    }
    public void crossBridge(Car car)
    {
        try
        {
        	//System.out.printf("%s is trying to cross the bridge.\n",car.getName());
        	lock.lock();
        	if(SingleLaneBridge4.RedCarCounter>SingleLaneBridge4.BlueCarCounter + 4) flag = false;//an den h seira twn kokkinwn alla exoun megalh oura pairnoun thn seira
            else if(SingleLaneBridge4.BlueCarCounter>SingleLaneBridge4.RedCarCounter + 4) flag = true;//an den h seira twn mple alla exoun megalh oura pairnoun thn seira
        	while(!((car.getColor().equals("Red") && flag == false) || (car.getColor().equals("Blue") && flag == true))) {
        		cond.await();   //an den einai h seira tou nhmatos pou phre proteraiothta apeule8erwnei to lock
            	cond.signalAll();//kai 3ypnaei ola ta nymata gia na paroun ayta thn protereothta
        	}
        	System.out.printf("%s is crossing the bridge.\n",car.getName());
            long duration = (long)(Math.random() * 10);
            TimeUnit.SECONDS.sleep(duration);
        	
        }
        catch(InterruptedException iex)
        {
            iex.printStackTrace();
        }
        finally
        {
            System.out.printf("%s has crossed the bridge.\n",car.getName());
            if(flag == false) {
            	flag = true;//an to prohgoumeno htan kokkino seira exoun oi mple
            	SingleLaneBridge4.RedCarCounter--;//meiwse to counter twn kokkinwn pou perimenoun

            }
            else {
            	flag = false;//an to prohgoumeno htan mple seira exoun oi kokkinoi
            	SingleLaneBridge4.BlueCarCounter--;
            }
            

            lock.unlock() ;
        }
    }
}
 
class Car implements Runnable //gia na mporei to object na ektelestei apo thread
{
    private String name;
    private String color;
    private Bridge bridge;
     
    public Car(Bridge bridge)
    {
        this.bridge = bridge;
    }
     
    public void run()
    {
        bridge.crossBridge(this);
    }
 
    public String getName() {
        return name;
    }
 
    public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setName(String name) {
        this.name = name;
    }
 
}
