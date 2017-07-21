package smtpMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class DiffFund {
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

			String sym = null, newdata = null, olddata = null, event = null;
			int type = 0;

			if (rs != null)
			{
				while (rs.next())
				{
					i++;
					String ar[] = "Sym,ChangeType,NewData,OldData".split(",");
					sym = rs.getString(ar[0]);
					type = rs.getInt(ar[1]);
					newdata = rs.getString(ar[2]);
					olddata = rs.getString(ar[3]);
					
					if (type == 30)
					{
						event = "Shares diff";
					}
					else if (type == 31)
					{
						event = "Revenue diff";
					}
					else if (type == 32)
					{
						event = "Earnings diff";
					}
					else if (type == 33)
					{
						event = "Net Position diff";
					}
					else if (type == 34)
					{
						event = "Debt diff";
					}
					else if (type == 35)
					{
						event = "EBIT diff";
					}
					
					sb.append("Sym: "+sym+"\nDifference: "+event+"\nNew: "+newdata+"\nOld: "+olddata).append("\r\n\n");
					
				}
				if (i > 0)
					Msg.sendmail("cannot post code", sb.toString(), 1);
			}
			else
			{
				if (rs != null)
					rs.close();
				rs = null;
			}

			
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
