package smtpMessage;

import java.util.ArrayList;

public class Msg  implements Runnable
{
	static Msg sender;
	
	static ArrayList<String> messagecheck;
	static long mesagetimecheck[];

	static ArrayList<String> messages;
	static ArrayList<String> recipients;
	static ArrayList<String> titles;
	
	static StringBuilder sb = new StringBuilder(1024);
	
	static 
	{
		messagecheck = new ArrayList<String>();
		mesagetimecheck = new long[64];

		messages = new ArrayList<String>();
		recipients= new ArrayList<String>();
		
		titles= new ArrayList<String>();
		
		
	}
	
	
	
	public Msg()
	{
	}
	
	public static void main(String[] args)
	{		
	}
	
	public static void sendmail(String recipient, String title, String app, String funcname, String msg, int minutes)
	{
		System.out.println("Enter");
		
		
		
		
		
		synchronized(messages)
		{System.out.println("stringbuilder");
			sb.delete(0, sb.length());
			//sb.append("Application: ").append(app).append("\r\n");
			//sb.append("Function: ").append(funcname).append("\r\n");
			//sb.append("Exception: ").append(msg).append("\r\n");
			//sb.append("Event: ").append(msg).append("\r\n");
			sb.append(msg).append("\r\n");
			
			String message = sb.toString();
			
			
			//STAT CHECK
			long now = System.currentTimeMillis();
			
			for (int i = 0; i < messagecheck.size(); ++i)
			{
				long oldtime = mesagetimecheck[i];
				
				if (Math.abs(oldtime-now) > 65*60*1000)
				{
					messagecheck.remove(i);
					System.arraycopy(mesagetimecheck, i+1, mesagetimecheck, i, mesagetimecheck.length-i-1);
					i --;
					continue;
				}

				if (Math.abs(oldtime-now) < minutes*60*1000)
				{
					String exist = messagecheck.get(i);
					if (message.equals(exist))
						return;
				}				
			}
			int size = messagecheck.size();
			if (size+1 >= mesagetimecheck.length)
			{
				long[] msgt = new long[mesagetimecheck.length + 256];
				System.arraycopy(mesagetimecheck, 0, msgt, 0, size);
				mesagetimecheck = msgt;
			}
			mesagetimecheck[size] = now;
			messagecheck.add(message);
			//END CHECK
			
			//ADD MESSAGE INFO
			recipients.add(recipient);
			messages.add(message);
			titles.add(title);

			if (sender == null)
			{	sender = new Msg();
				Thread th = new Thread(sender);
				th.setDaemon(true);
				th.start();
			}
			else
				messages.notify();
		}
		
		
	}
			 
	public void run()
	{
		System.out.println("Start Running");
		while (true)
		{
		
			if (messages.size() > 0)
			{System.out.println("Break");
				String receipt = null;
				String title = null;
				String msg = null;
				
				synchronized(messages)
				{
					if (messages.size() > 0)
					{
						receipt = recipients.get(0);
						title = titles.get(0);
						msg = messages.get(0);
						
						messages.remove(0);
						recipients.remove(0);
						titles.remove(0);
					}
				}
				if (msg != null)
				{
					System.out.println("sent\n");
					
					FlexiMailEnh.cannot post code}
			
			}
			synchronized(messages)
			{
				try {
					messages.wait(2000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
			 
}