package smtpMessage;


import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
 
public class SMTPServerImpl 
{
	private String _folderspath;
	private String _log;
	private boolean _running;
	private Random	_random;
	private int _randomInt;
	private Object _lock;
	private HashMap rerties;
//	private String _outlist[][];
	private ArrayList _discartlist;
	PrintWriter _writer;
	PrintWriter logwriter;
	Date		logdate;
	SimpleDateFormat logformat;

	private int interval;
	private int maxtry;

	public SMTPServerImpl()
	{
		_random = new Random();
		_randomInt = _random.nextInt();
		if (_randomInt < 0)
			_randomInt = -_randomInt;
		_lock = new Object();
		interval = 60000; //seconds
		maxtry = 18;  //total interval = interval*trycount*trycount*trycount
		rerties = new HashMap();
		logwriter = null;
		logdate = new Date();
		logformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		_running = false;
		_writer = null;
//		_outlist = null;
		_discartlist = new ArrayList();
		setOutPath("/pha1/smtp");
		setLogPath("/pha1/smtp/log");
	}

	public void start()
        throws Exception
	{
		synchronized(this)
		{
			if (isRunning())
				return;

			_running = true;
		}
		_lock = new Object();

		File fout = new File(_log);
		fout.mkdirs();
		int waitl = 3000;

 
		
		fout = new File(_folderspath+File.separator+"errbox");
		fout.mkdirs();

		fout = new File(_folderspath+File.separator+"outbox");
		fout.mkdirs();

		try {

//			logwriter = new PrintWriter(new FileOutputStream(_log+File.separator+"SMTP",true), true); 
			logwriter = new PrintWriter(new RolloverFileOutputStream(_log+File.separator+"SMTPyyyy_mm_dd.log",true), true); 
			
			log("START SMTP v1.9 [" + this + "] at " + (_folderspath+File.separator+"outbox") , System.currentTimeMillis());
			  
			while (_running)
				{
				String[] files;
			     synchronized(_lock)
		        {
					_lock.wait(waitl);
				}
			    synchronized(_lock)
			    { 
			    	files = fout.list();
			    }
			    waitl = 60000;
			    
//				String outlist[][] = files.length > 0? new String[files.length][]: null;
				SMTPSession smtpsession;
				
				for (int i = 0; _running && i < files.length; ++i)
				{
					String filename = _folderspath+File.separator+"outbox"+File.separator+files[i];
					String filef = files[i];
					
					long now = System.currentTimeMillis();

					Rerties a = (Rerties) rerties.get(files[i]);

					if (a == null)
					{	a = new Rerties();
						rerties.put(filef, a);
					}
					smtpsession = null;
						
					
					if (Math.abs(now-a.trylast) <  interval*(a.trycount)*(a.trycount)*(a.trycount))
						continue;

					if (!a.sendit && a.trycount < maxtry)
					{
						a.trylast = now;
						a.trycount++;

						try {
							///////////TEST READ START
							log("Reading " + filename, now);

							DataInputStream input = new DataInputStream(new FileInputStream(filename));
							String sender2 = input.readUTF();
							String recipient2 = input.readUTF();
							String msg2[] = new String[20];
					
							for (int j=0; j < 20; ++j)
								msg2[j] = input.readUTF();

							if (_discartlist.indexOf(msg2[0]) >= 0)
							{
								synchronized(_discartlist)
								{
									if (_discartlist.indexOf(msg2[0]) >= 0)
									{	a.discart = true;
										_discartlist.remove(msg2[0]);
									}
								}
							}
//							outlist[i] = msg2;
							input.close();
							
							if (!a.discart && recipient2 != null && recipient2.indexOf('@') >= 0 && sender2 != null)
							{	smtpsession = new SMTPSession(null, recipient2, sender2, msg2);
								smtpsession.setLogger(this);
								smtpsession.sendMessage();
								now = System.currentTimeMillis();
								log("SEND OK " +files[i], now);
								a.sendit = true;
							}
						}
						catch (IOException e)
						{
								
							log("SEND FAIL " + files[i] + " error:" + e, now);
							if (smtpsession != null && smtpsession._lastResponce != null && smtpsession._lastResponce.charAt(0) == '5')
								a.discart = true;
						}
					} 
					if (a.sendit || a.discart || a.trycount >= maxtry)
					{
						File f = new File(filename);
						if (!a.sendit)
						{	
							File f2 = new File(_folderspath+File.separator+"errbox"+File.separator+files[i]);
							f.renameTo(f2);
							
							if (a.discart)
								log("DISCART " +filename, now);
							else	
								log("FAIL MAXTRY " +filename, now);
						}
						else
							f.delete();

						synchronized(_lock)
					    { 
					    	files = fout.list();
					    }
//						outlist[i] = null;
					}
				}
//				_outlist = outlist;
			}
			if (!_running)
				log("STOP SMTP", System.currentTimeMillis());
		} 
		catch (Exception e)
		{ 
			_running = false;
		}
		
		if (logwriter != null)
		{	logwriter.close();
			logwriter = null;
		}
	}

    public void stop()
        throws InterruptedException
	{
    	if (_running)
    	{	_running = false;
    		synchronized(_lock)
	        {
    			_lock.notify();    
    		}
    	}
		_running = false;
	}

	public boolean isRunning()
	{
		return _running;
	}

	public boolean isStarted()
	{
		return _running;
	}
	
	public void setStaticResolve(String server, String ip)
	{
		SMTPSession._staticmap1.put(server, ip);
		System.err.println("static: " + server+"="+ip);
	}
	
	public void setResolve(String s)
	{
		String server = s;
		String ip = null;
		int idx = s == null? -1: s.indexOf('=');
		if (idx > 0)
		{	server = s.substring(0, idx);
			ip = s.substring(idx+1);
		}
		if (s != null)
			setStaticResolve(server, ip);
	} 

	
	public String getStaticResolve(String server)
	{
		return null;
	}


	public void setPrintWriter(PrintWriter  out)
	{
		_writer = out;
	}



	 public void destroy()
	{	
		_running = false;
	}

	void log(String s, long time)
	{
		logdate.setTime(time);
        logwriter.print(logformat.format(logdate));
		logwriter.print(' ');
		logwriter.println(s);

		if (_writer != null)
			_writer.println(s);
	}

	public void setRetryInterval(int i)
	{
		interval = i;
	}

	public int getRetryInterval()
	{
		return interval;
	}

	public void setMaxRetries(int i)
	{
		maxtry = i;
	}

	public int getMaxRetries()
	{
		return maxtry;
	}

	public void setOutPath(String path)
	{
		_folderspath = path;
	}

	public String getOutPath()
	{
		return _folderspath;
	}

	public void setLogPath(String path)
	{
		_log = path;
	}

	public String getLogPath()
	{
		return _log;
	}

	public void sendMail(String headers[])
	{
		sendMail(headers[0], headers[1], headers[2], headers[3], headers[4], headers[5], headers[6], headers[7]);
	}
	
	public void sendMail(String sender, String senderAlias, String recipients, String title, String message, String attach, String content, String charset)
    {
        try
        {
			if (content == null)
				content = "text/plain";
			if (charset == null)
				charset = "iso-8859-1";

			StringTokenizer st = new StringTokenizer(recipients, ";");
			while (st.countTokens() > 0)
			{	
				String recipient = st.nextToken();
				if (recipient.indexOf('@') < 0)
					continue;
				String randomNumber = null;
				String filename = null;
				for (int i = 0; i < 1000; ++i)	
				{	int nexti = _random.nextInt();
					nexti = (_randomInt % 10000)*1000000 +  (nexti % 1000000);
					if (nexti < 0)
						nexti = -nexti;
					synchronized (_lock)
					{
						randomNumber = ""+ nexti;
						filename = 	_folderspath +File.separator+"outbox"+ File.separator+ randomNumber+ ".smt";
						File fout = new File(filename);
						if (!fout.exists())
							break;
					}
				}

				String att = "";
	            if (attach != null)
					att = FlexiMailEnh.prepareAttachment(new FileInputStream(attach), attach.substring(attach.lastIndexOf(File.separator) + 1), randomNumber);
				String msg[] = FlexiMailEnh.initMsg(sender, senderAlias, recipient, recipients, title, message, att, content, charset, randomNumber);

				synchronized(_lock)
		        {
				DataOutputStream output = new DataOutputStream(new FileOutputStream(filename));
				
				output.writeUTF(sender);
				output.writeUTF(recipient);

				for (int i=0; i < 20; ++i)
				{
					String s = msg[i];
					if (s == null)
						s ="";
					output.writeUTF(s);
				}

				output.close();
				//System.out.println("store " + filename);
					_lock.notify();
				}
//				if (!isRunning())
//					start();
/*
///////////TEST READ START
				DataInputStream input = new DataInputStream(new FileInputStream(filename));
				String sender2 = input.readUTF();
				String recipient2 = input.readUTF();
				String msg2[] = new String[20];
		        
				for (int i=0; i < 20; ++i)
					msg2[i] = input.readUTF();

				input.close();
///////////TEST READ END
				String host = "smtp."+recipient2.substring(recipient.indexOf('@')+1);

				SMTPSession smtpsession = new SMTPSession(host, recipient2, sender2, msg2);
				smtpsession.sendMessage();
*/			}
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

	class Rerties
	{
		long trylast;
		int  trycount;
		boolean sendit;
		boolean discart;
		
		Rerties()
		{
			trylast = 0;
			trycount = 0;
			sendit = false;
			discart= false;
		}
	}


	public static void servertest()
	{

		String sender = "testmail@ztradecom.gr";
		String senderAlias = "TESTMAIL";
		String recipients = cannot post code;
		String title="MTitle";
		String message="hello tester";
		String attach = null;
		String content = null;
		String charset = null;

		try {
		SMTPServerImpl server = new SMTPServerImpl();
        server.sendMail(sender, senderAlias, recipients, title, message, attach, content, charset);
		server.start();
		}
		catch (Exception e)
		{
		
		}
	}

	public static void main(String args[])
    {
//		selftest();
		servertest();


    }

	public void discardMail(String random) 
	{
		if (random != null)
		{	synchronized(_discartlist)
			{
				_discartlist.add(FlexiMailEnh.getMessageID(random));
			}
		}
	}
	
	public String[][] getOutbox() {
		return null; //_outlist;
	}
}
