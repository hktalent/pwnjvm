package test.pwnjvm;


/*@Description:This agent uses Byte Buddy so that can easily attach new Classes to JVMs.  
*利用Byte Buddy创建能够快速注入类到JVM中的premain()。
*/
import java.lang.instrument.Instrumentation;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;



public class Agent {
	public Agent() {}
	
	public static void premain(String arg, Instrumentation instrumentation){
		System.out.println("--Method premain() gots executed at this position--");
		new ByteBuddy()
		  .redefine(MyServlet.class)
		  .name(javax.servlet.http.HttpServlet.class.getName())
		  .make()
		  .load(MyServlet.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
		  .getLoaded();
	}
	
	
	public static void premain(String arg){
	//do nothing	
	}
}

	
	
	
