package smtpMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Events
{
	public static void main(String[] args)
	{		

		try
		{
			Class.forName("cannot post code");
			Connection con = 	DriverManager.getConnection("cannot post code");
			Statement st = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder(1024);
			int i = 0, j = 0, k = 0;
			if (con != null && st == null)
				st = con.createStatement();
			if (st != null)
				rs = st.executeQuery("select cannot post code");

			String mar = null,sym = null, event = null;
			Date edate = null;
			short etype, init = 1, count = 0;
			String edata1, edata2 = null, edata3, edata4, edata5;

			if (rs != null)
				while (rs.next())
				{
					String ar[] = "Mar,Sym,EDate,EType,EData1,EData2,EData3,EData4,EData5".split(",");
					mar = rs.getString(ar[0]);
					sym = rs.getString(ar[1]);
					edate = rs.getDate(ar[2]);
					etype = (short) rs.getInt(ar[3]);
					if (etype == 2 || etype == 3)
					{
						event = "Splits";
						i++;
					}
					else if (etype == 1)
					{
						event = "Dividends";
						j++;
					}
					else if (etype == 6)
					{
						event = "Earnings";
						k++;
					}
					edata1 = rs.getString(ar[4]);
					if (rs.getString(ar[5]) != null)
					{
						edata2 = rs.getString(ar[5]);
					}
					
					if (count == 0)
					{
						sb.append("Event: "+event).append("\r\n\n");
						sb.append("Symbol: "+sym).append("\r\n");
						count=1;
					}
					
					if (etype != init)
					{
						sb.append("\n\nEvent: "+event).append("\r\n\n");
						sb.append("Symbol: "+sym).append("\r\n");
					}
					else
						sb.append("Symbol: "+sym).append("\r\n");
					
					if (rs.getString(ar[6]) != null)
					{
						edata3 = rs.getString(ar[6]);
						edata4 = rs.getString(ar[7]);
						edata5 = rs.getString(ar[8]);
					}
					
					init = etype;

				}
			else
			{
				if (rs != null)
					rs.close();
				rs = null;
			}

			if (i == 0 && j == 0 && k == 0)
				Msg.sendmail("cannot post code", "", 1);
			if (i > 0 && j == 0 && k == 0)
				Msg.sendmail("cannot post code", sb.toString(), 1);
			if (i == 0 && j > 0 && k == 0)
				Msg.sendmail("cannot post code", sb.toString(), 1);
			if (i == 0 && j == 0 && k > 0)
				Msg.sendmail("cannot post code", sb.toString(), 1);
			if (i > 0 && j == 0 && k > 0)
				Msg.sendmail("cannot post code", "test-func", sb.toString(), 1);
			if (i == 0 && j > 0 && k > 0)
				Msg.sendmail("cannot post code", sb.toString(), 1);
			if (i > 0 && j > 0 && k > 0)
				Msg.sendmail("cannot post code", sb.toString(), 1);

			Thread.sleep(15000);
			
			if (rs != null)
				rs.close();
			rs = null;

		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();	
		}
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
