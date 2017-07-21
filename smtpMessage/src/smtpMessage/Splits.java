package smtpMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Splits
{
	public static void main(String[] args)
	{		


		try {
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

			String sym = null, edate, edata1 = null, edata2 = null, event = null, temp = null;
			int type = 0;

			if (rs != null)
			{
				while (rs.next())
				{
					
					String ar[] = "Sym,EDate,EType,EData1,EData2".split(",");
					sym = rs.getString(ar[0]);
					edate = rs.getString(ar[1]);
					type = rs.getInt(ar[2]);
					edata1 = rs.getString(ar[3]);
					edata2 = rs.getString(ar[4]);
					
					if (sym.equals(temp))
						continue;
					
					if (type == 2)
					{
						event = "Split";
					}
					else if (type == 3)
					{
						event = "Reverse Split";
					}
					else if (type == 202)
					{
						event = "Possible Split";
						i++;
					}
					else
					{
						event = "Possible Reverse Split";
						i++;
					}
					
					sb.append("Sym: "+sym+"\nEvent: "+event+"\nEData1: "+edata1+"\nEData2: "+edata2).append("\r\n\n");
					
					temp = sym;
					
				}
				if (i > 0)
					Msg.sendmail("cannot post code", sb.toString(), 1);
				else
					Msg.sendmail("cannot post code", sb.toString(), 1);
			}
			else
			{
				if (rs != null)
					rs.close();
				rs = null;
			}

			
			Thread.sleep(30000);

	
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
