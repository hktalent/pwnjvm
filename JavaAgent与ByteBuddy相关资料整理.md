

### 英文Tutorial：

---

* [非常详尽的Byte Buddy官方讲解：](http://bytebuddy.net/#/tutorial) 
其中包括了非常详尽的讲解与code示例，包括但不限利用Byte Buddy动态生成代码过程中可能遇到的：
  * 创建dynamicType动态类，rebase/redefine类等；
  * Load和Reload类；
  * 利用TypeDescription和自带的TypePool处理Unloaded Class未加载的类；
  * 创建用Buddy Byte Agent Builder创建Java agent；
  * 在安卓中的使用(简略提到)
  * Fields和methods中指定性修改某个类(delegating a method call)
 
  
  (网页顶部有提供个人翻译的中文版，但是其后半部分示例过于简略，故不推荐)
  
* [Byte Buddy的GitHub主页:](https://github.com/raphw/byte-buddy)
Byte Buddy的源代码和少部分讲解。

* [比较详细的Byte Buddy教程，不过还是更推荐官方讲解](https://medium.com/@shehan.a.perera/using-a-java-agent-to-monitor-application-runtime-behavior-using-metrics-664eb95e971c)

*  [HotSwap的GitHub主页:](https://github.com/HotswapProjects/HotswapAgent)
HotSwap的源代码。HotSwap agent主要是用于实现重加载与框架配置文件的(如Spring，Hibernate等)，通过配置-javaagent能在Byte Buddy的Rebase和Redefine中被使用。

* [java.lang.instrument的详解:](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html)
能够对Java agent有大体的了解。这里有一个[我总结的简单版本](https://github.com/NilesJiang/JavaAgent/blob/master/JavaAgent.md)






### 中文版讲解：

---

* [Byte Buddy的中文翻译版教程：](https://notes.diguage.com/byte-buddy-tutorial/#redefining-and-rebasing-existing-classeshttp://bytebuddy.net/#/tutorial)
其后半部分示例过于简略，不太推荐

* [Java Agent的一些中文讲解:](https://yangshaoxiang.github.io)
包括Java agent的静态使用等
