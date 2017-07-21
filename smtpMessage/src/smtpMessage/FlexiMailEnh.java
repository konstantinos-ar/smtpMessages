package smtpMessage;

import java.awt.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class FlexiMailEnh
{
    public FlexiMailEnh()
    {

    }

    public static String getMessageID(String randomNumber)
    {
    	return "cannot post code";
    }

    public static String[] initMsg(String sender, String senderAlias, String recipient,String recipients, String title, String message, String attach, String content, String charset, String randomNumber)
    {
 
		String msg[] = new String[20];

        msg[0] = getMessageID(randomNumber); 
        if (senderAlias != null)
			msg[1] = "From: \"" + senderAlias +"\" <" + sender + ">";
		else
			msg[1] = "From: <" + sender + ">";
        msg[2] = "To: <" + recipient + ">";
 //       String reps = recipients.replaceAll("\"", "");
      //  StringTokenizer st = new StringTokenizer(recipients, ";");
        //String rcpt[] = new String[st.countTokens()];
      //  ArrayList<String> sb = new ArrayList<String>();
        //StringBuilder ss = new StringBuilder(st.countTokens());
  //      int i = 0;
	//	while (st.countTokens() > 0)
		//{	
			//rcpt[i] = st.nextToken();
			//ss.append(st.nextToken());
		//	String s = st.nextToken();
//			s = s.replace('[', ' ');
//			s = s.replace(']', ' ');
		//	sb.add(s);
			//if(st.countTokens() != 0)
			//	ss.append(";");
			//i++;
		//}
        msg[3] = "Cc: " +recipients;
        msg[4] = "Subject: " + title;
        Calendar calendar = Calendar.getInstance();
        msg[5] = "Date: " + weekdays[calendar.get(7) - 1] + ", " + calendar.get(5) + " " + months[calendar.get(2)] + " " + calendar.get(1) + " " + calendar.get(11) + ":" + calendar.get(12) + ":" + calendar.get(13) + " +0200";
        //Reply-To: "Neateye" <nitaigouranga@aol.com>
		msg[6] = "MIME-Version: 1.0";
        msg[7] = "Content-Type: multipart/mixed;";
        msg[8] = "\tboundary=\"Boundary=_0.0_=005410" + randomNumber + "\"";
        msg[9] = "";
        msg[10] = "";
        msg[11] = "--Boundary=_0.0_=005410" + randomNumber;
        msg[12] = "Content-Type: "+content+";";
        msg[13] = "\tcharset=\""+charset+"\"";
        msg[14] = "Content-Transfer-Encoding: 7bit";
        msg[15] = "";
        msg[16] = message == null? "": message;
		msg[17] = attach == null? "": attach;
        msg[18] = "--Boundary=_0.0_=005410" + randomNumber + "--";
        msg[19] = "";
		return msg;
    }

    public static String prepareAttachment(InputStream inputstream, String atname, String randomNumber)
    {
        StringBuffer s2 = new StringBuffer(2048);
		byte abyte0[] = new byte[57];
        try
        {
            DataInputStream datainputstream = new DataInputStream(inputstream);
            s2.append("\r\n--Boundary=_0.0_=005410").append(randomNumber).append("\r\n");
            s2.append("Content-Type: application/octet-stream;\r\n\tname=\"").append(atname).append("\"\r\n");
            s2.append("Content-Transfer-Encoding: base64\r\n");
            s2.append("Content-Disposition: attachment; filename=\"").append(atname).append("\"\r\n\r\n");
            int i = 0;
//            boolean flag = false;
            do
            {
                i = datainputstream.read(abyte0, 0, 57);
                if (i == -1)
                	break;
                
                if (i < 57)
                {
                    byte abyte1[] = new byte[i];
                    for(int j = 0; j < i; j++)
                        abyte1[j] = abyte0[j];

                    s2.append(Base64codec.encode(abyte1)).append("\r\n");
                } else
                {
                    s2.append(Base64codec.encode(abyte0)).append("\r\n");
                }
            } while(i == 57);
            datainputstream.close();
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        return s2.toString();
    }

//test only ???
	public static void sendMail(String sender, String senderAlias, String recipients, String title, String message, String attach, String content, String charset)
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
				String host = "smtp."+recipient.substring(recipient.indexOf('@')+1);
		        String randomNumber = Math.rint(Math.random() * 1000000000D) + "";
				String att = "";
	            if (attach != null)
					att = prepareAttachment(new FileInputStream(attach), attach.substring(attach.lastIndexOf(File.separator) + 1), randomNumber);
				String msg[] = initMsg(sender, senderAlias, recipient, recipients, title, message, att, content, charset, randomNumber);
	            
				SMTPSession smtpsession = new SMTPSession(host, recipient, sender, msg);
				smtpsession.sendMessage();
			}
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

	public static void selftest()
	{

		String sender = "cannot post code";
		String senderAlias = "Konstantinos";
		String recipients = "cannot post code";
		String title="TEST";
		String message="hello test";
		String attach = null;
		String content = null;
		String charset = null;

		FlexiMailEnh fleximailenh = new FlexiMailEnh();
        fleximailenh.sendMail(sender, senderAlias, recipients, title, message, attach, content, charset);
	}

	public static void main(String args[])
    {
		selftest();

    }

	static String weekdays[] = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"   };
    static String months[]   = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

}
