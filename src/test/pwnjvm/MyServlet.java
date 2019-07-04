package test.pwnjvm;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class MyServlet implements Filter {

	
 	@Override
 	public void init(FilterConfig fConfig) throws ServletException {
 		
 		System.out.println("---Filter inits---");
 		
 	}

 	
 	@Override
 	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
          FilterChain chain) throws IOException, ServletException {
          HttpServletRequest request = (HttpServletRequest)servletRequest;
          HttpServletResponse response = (HttpServletResponse)servletResponse;
          String uri = request.getRequestURI();
          if(uri.indexOf("xxx")!=-1) {	//"xxx" here is an example word.
        	  response.getWriter().print("Request cannot be processed.");
        	  //Here we are simply processing testings on the agent method, ignore the following chain.
        	  //chain.doFilter(request, response);
          }else{
        	  //chain.doFilter(request, response);
          }
      	}
 	
 	
	 
 	/*	  //The following comment-out part of code provides a filter to filter through a series of words if needed.
 	 *	  //However, it requires using of the web.xml file to handle necessary setting-up.
 	 *
 	 *    String requestUri = request.getRequestURI();
          String contextPath = request.getContextPath();
          String url = requestUri.substring(contextPath.length());
          
          boolean isIgnored = false;
          if(ignoreContents!= null){
              for (int i = 0; i < ignoreContents.length; i++) {
                  if (url.indexOf(ignoreContents[i])==-1) {
                  	isIgnored = true;
                  	break;
                  }
              }
          }else{
              chain.doFilter(request, response);
          }
          
           if(isIgnored==true){
           	System.out.println(url+" is fine.");
            chain.doFilter(request, response);
           }else{
        	response.sendRedirect("error.jsp");	//将被拦截的内容重定向到错误页面
           	chain.doFilter(request, response);
           }
      	}
	 
 	 */
	 
 	
 	@Override
 	public void destroy() {
 		System.out.println("---Filter destroyed---");
 	}
 	
}



	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(req, resp);
	}

