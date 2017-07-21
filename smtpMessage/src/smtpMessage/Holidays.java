package smtpMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Holidays {
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

			String mar = null,holiday = null;
			Date date = null;

			if (rs != null)
			{
				while (rs.next())
				{
					i++;
					String ar[] = "Mar,Date,Holiday".split(",");
					mar = rs.getString(ar[0]);
					date = rs.getDate(ar[1]);
					holiday = rs.getString(ar[2]);
					
					sb.append("Market: "+mar+"\nDate: "+date+"\nHoliday: "+holiday).append("\r\n\n");
					
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
