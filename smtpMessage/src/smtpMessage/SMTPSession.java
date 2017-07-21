package smtpMessage;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TimeZone;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class SMTPSession
{
	public static HashMap	_staticmap1 = new HashMap();

	

    public void close()
        throws IOException
    {
        sessionSock.close();
        sessionSock = null;
    }
    public SMTPSession()
    {
    	_logger = null;
    }

    public SMTPSession(String s, String s1,String s2, String as[])
        throws IOException
    {
    	host = s == null? null: s.trim();
        recipient = s1 == null? null: s1.trim();
        sender = s2 == null? null: s2.trim();
        message = as;
        port = 25;
    	_logger = null;
    }

    public SMTPSession(String s, int i, String s1, String s2, String as[])
        throws IOException
    {
    	host = s == null? null: s.trim();
        recipient = s1 == null? null: s1.trim();
        sender = s2 == null? null: s2.trim();

        port = i;
        if(port <= 0)
            port = 25;
        message = as;
    	_logger = null;
    }

    public void setLogger(SMTPServerImpl log)
    {
    	_logger = log;
    }

    public void sendMessage()
        throws IOException
    {
    	
    	String domain  = recipient.substring(recipient.indexOf('@')+1);
    	 
    	String encoding = message[13];
        if (encoding.indexOf('"') >= 0)
        	encoding = encoding.substring(encoding.indexOf('"')+1);
        if (encoding.indexOf('"') >= 0)
        	encoding = encoding.substring(0, encoding.indexOf('"'));

        long now = System.currentTimeMillis();
        boolean sended = false;
        
        try {
        	Attribute a = doLookup(domain);
        	if (a != null)
        	{
        		for (int i = a.size()-1; i >= 0; --i)
        		{	String temp = (String) a.get(i);
        			if (temp != null)
        			{	if (temp.indexOf(' ') >= 0)
        					temp = temp.substring(temp.indexOf(' ')+1);
        				host = temp;
        				try {
        					connect();
        			        String s = getResponse();
        			        if(s.charAt(0) != '2')
        			            throw new IOException(s);
        			        s = doCommand("HELO " + domain, now);
        			        if (s.charAt(0) != '2')
        			        {
        			        	 s = doCommand("HELO " + host, now);
             			        	if (s.charAt(0) != '2')
             			        		throw new IOException(s);
        			        }
        			        s = doCommand("MAIL FROM:<" + sender+">", now);
        			        if(s.charAt(0) != '2')
        			            throw new IOException(s);
        			        s = doCommand("RCPT TO:<" + recipient+">", now);
        			       // if(s.charAt(0) != '2')
        			       //     throw new IOException(s);
        			       // s = doCommand("RCPT TO:<" + rcpt+">", now);
        			        if(s.charAt(0) != '2')
        			        {   if (s.startsWith("5"))
        			        		_lastResponce = s;
        			        	throw new IOException(s);
        			        }
        			        s = doCommand("DATA", now);
        			        if(s.charAt(0) != '3')
        			            throw new IOException(s);
        			        for(int  j = 0; j < message.length; j++)
        			        {    if(message[j].length() > 0)
        						{   if(message[j].charAt(0) == '.')
        								outStream.writeBytes(".");
        				            outStream.write(message[j].getBytes(encoding));
        						}
        						outStream.writeBytes("\r\n");
        					}
        			        s = doCommand(".", now);
        			        _lastResponce = s;
        			        
        			        if(s.charAt(0) != '2')
        			        {
        			            throw new IOException(s);
        			        } 
        					else
        			        {
        						sended = true;
        		        		close();
            					break;
        			        }
        				}
        				catch (IOException e)
        				{
        					log("SEND FAILED ",  host, now);
        				}
        			}
        		}
        	}
        } catch (Exception e)
        {
        	log("LOKUP FAILED  ",  domain, now);
            throw new IOException(e.getMessage());
        }
        if (!sended)
        {
        	log("FAILED  ",  domain, now);
            throw new IOException("CONNECT FAILED");
        }
    }

    protected String getResponse()
        throws IOException
    {
        String s = "";
        String s1;
        do
        {
            s1 = inStream.readLine();
            if(s1 == null)
                throw new IOException("Bad response from server.");
            if(s1.length() < 3)
                throw new IOException("Bad response from server.");
            s += s1 + "\r\n";
        } while(s1.length() != 3 && s1.charAt(3) == '-');
        return s;
    }

    protected String doCommand(String s, long now)
        throws IOException
    {
    	log("OUT (" + host+"): ", s, now);
        outStream.writeBytes(s + "\r\n");
        String s1 = getResponse();
    	log("IN  (" + host+"): ", s1, now);
        return s1;
    }

    protected void connect()
        throws IOException
    {
        sessionSock = new Socket(host, port);
        sessionSock.setSoTimeout(240000);
        inStream = new DataInputStream(sessionSock.getInputStream());
        outStream = new DataOutputStream(sessionSock.getOutputStream());
    }

	protected void log(String msg, String arg, long now)
	{
		if (_logger != null)
		{		if (arg != null)
					arg = arg.trim();
				_logger.log(msg + arg, now);
		}
	}

	static Attribute doLookup( String hostName ) 
		throws NamingException 
	{
		    Hashtable env = new Hashtable();
		    env.put("java.naming.factory.initial",
		            "com.sun.jndi.dns.DnsContextFactory");
		    DirContext ictx = new InitialDirContext( env );
		    Attributes attrs = ictx.getAttributes( hostName, new String[] { "MX" });
		    return attrs.get( "MX" );
	}	    

    public String host;
    public int port;
    public String recipient;
    public String sender;
    public String message[];
    protected Socket sessionSock;
    protected DataInputStream inStream;
    protected DataOutputStream outStream;
    public String _lastResponce = null;
    
    protected SMTPServerImpl _logger;
}
