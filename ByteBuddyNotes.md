
#  Byte-Buddy



* A code generation and manipulation library for creating and modifying Java classes during the runtime of a Java application and without the help of a compiler. 
* Allows the creation of arbitrary classes and is not limited to implementing interfaces for the creation of runtime proxies. 
* Offers a convenient API for changing classes either manually, using a Java agent or during a build.

----
|Feature|Result|
|-|-|
|As non-instrusive as possible; doesn't leave any trace in the classes created by it| The generated classes can exist without requiring Byte Buddy on the class path|

---


Any Creation of a Java class starts with an instance of the **ByteBuddy** class which represents a configuration for creating new types:

```Java
Class<?> dynamicType = new ByteBuddy()
	.subclass(Object.class)	//The created type will extends the Object class
	.method(ElementMatchers.named("toString"))	//will override the toString() method, and identify it by a so-called ElementMatcher.
	.intercept(FixedValue.value("Hello World!"))	//will return a fixed value of "Hello World!"
	.make()
	.load(getClass().getClassLoader())
	.getLoaded();
	
assertThat(dynamicType.newInstance().toString, is("Hello World!"));
```
//A predefined element matcher **named(String)** is used which identifies **methods** by their exact names.

//In **Class ElementMatchers**, collected numerous predefined and well-tested matchers, and they can be easily composed. The create of custom matchers is as simple as implementing the functional ElementMatcher Interface.

//The **FixedValue** class defines **a constant return value** for the overriden method.

//By implementing the Implementation interface, a method could however even be defined by custom byte code

The described Java class is created and then loaded into the JVM. For this purpose, a target class loader is required which is read from the surrounding class. 


_P.S.: Class Loader:  A class loader is an object that is responsible for loading classes. The class ClassLoader is an abstract class.  Given the binary name of a class, a class loader should attempt to locate or generate data that constitutes a definition for the class.  A typical strategy is to transform the name into a file name and then read a "class file" of that name from a file system._

---


Since Java language comes with a comparatively strict type system, all variables and objects need to be giveb a specific type. Any assignment towards imcompatible types always causes an error.

Runtime code generation enables some features that are normally only accessibe when programming in a dynamic languages without discarding Java's static type checks.

Byte Buddy is not the first library for code generation on the JVM. However, we believe that Byte Buddy knows some tricks the other frameworks cannot apply. The overall objective of Byte Buddy is to work declaratively, both by focusing on its domain specific language and the use of annotations. 

* a code generation and manipulation library for creating and modifying Java classes during the runtime of a Java application and without the help of a compiler.
* allows the creation of arbitrary classes and not limited to implementing interfaces for the creation of runtime proxies. 
* offers a convenitent API for changing classes either manully, using a Java agent or during a build.

## Instrumentation: add events to code of methods
Using byte-code instrumentation can add events to code of methods.
e.g. Add a event at the beginning of a method, but not modify the state or behavior modify the state or behavior of application.

Instrumentation can simply maintain counters or can statiscally smalpe events and they can be inserted in one of:
	* Static Instrumentation(before): The class file is instrumented before it's loaded into the VM.
	* Load-Time Instrumentation(when loaded): When a class file is loaded by the VM, the raw bytes of the class file are sent for instrumentation to the agent.
	* Dynamic Instrumentation(already): A class which is already loaded(and possibly even running) is modified.

## Metrics: Use Metrics data to get a understand about the runtime behavior of program.
5 Types of metrcis:
  * Meters: measures the rate of events over time. In addition to the mean rate, meters also track 1-/5-/15-min moving 
  * Timers: measures both the rate that a particular piece of code is callsed and the distribution of its duration
  * Counters: a gauge for an atomic instance. Can increment/decrement its value.
  * Histogram: measures the statistical distribution of value in a stream of data, including min, max, mean, median, 75th, 90th, 95th, 98th, 99th, 99.9th, etc.
  * Gauges: an instantaneous measurement of a any value that needed.

---

## Creating a class

Any type that is created by Byte Buddy is emitted by an instance of the ByteBuddy class. So just simply create a new instance by calling new ByteBuddy().
* Create a first class at a Java program's runtime:
```Java
    DynamicType.Unloaded<?> dynamicType = new ByteBuddy();
    .subclass(Object.class)
    .make();
    //Creates a new class that extends the Object type.
    //The dynamically created type would be equivalent to a Java class that only extends the Object without explicitly implementing any methods, fields or constructors. 
    //Did not name the dynamically generated type.
```
     
 If wanna name it expilicity:
 ```Java
    DynamicType.Unloaded<?> dynamicType = new ByteBuddy();
    .subclass(Object.class)
    .name("example.Type")
    .make();
 ```
 
If not named explicitly. The default Byte Buddy configuration provides a NamingStrategy which randomly creates a class name based on a dynamic type's suprclass name. (e.g. example.Foo => example.Foo$$ByteBuddy$$1376491271) That is, random numeric sequence. Except for subclass types of the java.lang package, such as Object. Java's security model does not allow custom types to live in this namespace. Then such type names are prefixed with net.bytebuddy.renamed by the default naming strategy.



Since by creating a new ByteBuddy() instance,we create a default configuration. By calling methods on this configuration, we can customize it by needs.

```Java
    DynamicType,Unloaded<?>  dynamicType = new ByteBuddy();
    .with(new NamingStrategy.AbstractBase(){
      @Override
      public String subclass(TypeDescription superClass){
      	return "i.love.ByteBuddy."+superClass.getSimpleName();
      }
    })
    .subclass(Object.class)
    //By subclassing the Object type, the dynamic type is then i.love.ByteBuddy.Object
    .make();
```

Also, using Byte Buddy's built-in **NamingStrategy.SuffixingRandom** which enables customize to include a prefix that is more meaningful.

---

## Domain specific language and immutability

Fact: 
* almost every class in Byte Buddy namespace is **immutable**
* in the other few cases, could not make a type immutable, explicitly mention it in the class's javadoc.
* if implement custom features for Byte Buddy, recommended to stick with this principle.


Implication: 
must be careful when for example configuring ByteBuddy instances
**Counterexample:**
```Java
ByteBuddy byteBuddy = new ByteBuddy();
byteBuddy.withNamingStrategy(new NamingStrategy.SuffixingRandom("suffix"));
DynamicType.Unloaded<?> dynamicType = byteBuddy.subclass(Object.class).make();
```
**byteBuddy.withNamingStrategy.SuffixingRandom("suffix")** <- Instead of mutating the instance that is stored in the byteBuddy variable, the invocation of the withNamingStrategy method returns a customized ByteBuddy instance which is however lost. As a result, the dynamic type is created using the default configuration which was originally created.

---

## Redefining and rebasing existing classes

Besides creating a subclass of an existing class. The same API can be used for enhancing existing classes.

> /1. type redefinition: 
	Allows for the alteration of an existing class, either by adding fields and methods or by replacing existing method implementations. Preexising method implementations are however **lost** if they are replaced by another implementation.
	
> /2. type rebasing: 
	Retains any method implementations of the rebased class. Instead of discarding overridden methods like when performing a type redefinition, Byte Buddy copies all such method implementations into **renamed private methods with compatible signatures**. This way, no implementation is lost and rebased methods can continue to invoke original code by calling these renamed methods. 
```Java
class Foo {
String bar() {return "foo" + bar$original(); }
private String bar$original() { return "bar"; }
}
```
	
The information of the originial method is reserved and therefore remains accessible. 
When rebasing a class, Byte Buddy treats all method definitions such as if you defined a subclass, i.e. it will call the rebased method if you attempt to call a rebased method's super method implementation. But instead, it eventually flattens this hypothetical super class into the rebased type displayed above.

Any rebasing, redefinition or subclassing is performed using an identical API which is defined by the DynamicType.Builder interface. This way, it is possible to, for example, define a class as a subclass and to later alter the definition to represent a rebased class instead.

All classes could similarly be defined by redefinition or by rebasing.

## Loading a class

* A type that is created by Byte Buddy is represented by an instance of **DynamicType.Unloaded**. These types are not loaded into JVM. Instead, these types are represented in their binary form, in the Java class file format.

* DynamicType.Unloaded class allows to extract a byte array that represents the dynamic type.
  * also offers an additional method **saveIn(File)**, that allows you to store a class in a given folder.
  * allows to **inject(File)** classes into an existing __jar__ file.
  
* In Java, all classes are loaded using a ClassLoader. One example for such a class:

|BootStrap Class Loader|System Class Loader|"Byte Buddy Class Loader"|
|-|-|-|
|Loading the classes that are shipped withun the Java class library|Loading classes on the Java application's class path|For dynamic types|

Byte Buddy offers:
* Simply create a new ClassLoader which is explicitly told about the existence of a particular dynamically created class. 
	* Due to hierarchies of Java class loaders, this class loader should be defined as the child of a given class loader that already exists in the running Java application. => All types of the running Java program are visible to the dynamic type that was loaded with new ClassLoader. 
	
* Chile-first class loader: load a type by itself first, then querying its parent ClassLoader. (In case of a type with identical name) Notice: this approach doesn't override a type of a parent class loader but rather shadows this other type.

* Can use reflection to inject a type into an existent ClassLoader. Usually a class loader is asked to provide a given type by its name. Using reflection, we can turn this principle around and call a protected method to inject a new class into the class loader without the class loader actually knowing how to locate this dynamic class.

Drawbacks:

* If we create a new ClassLoader, this class loader defines a new namespace. As an implication, it's possible to load two classes with identical name as long as these classes are loaded by two different class loaders. These two classes are then never be considered as equal by a JVM, even if both classses represent an identical class implementation. 
	* This rule for equality holds also for Java packages. => A class _example.Foo_ is not able to access package-private methods of antoher class _example.Bar_ if both classes were not loaded with the same class loader。 Also, if example.Bar extended example.Foo, any overridden package-private methods would become inoperative but would delegate to the original implementations.
	
* Whenever a class is loaded, its class loader will look up any type that is referenced in this class once a code segment referencing another type is resolved. This lookup delegates to the same class loader.

---




	





### ClassLoadingStratrgy

1.WRAPPER: creates a new wrapping ClassLoader;	//**Suitable for most cases**

2.CHILD_FIRST: creates similar class loader with child-first semantics

3.INJECTION: injects a dynamic type using reflection

P.S. For Wrapper and Injection are both available in so-called manifest versions where a type's binary format is preserved even after a class was loaded.

These versions make the binary representaiton of a class loader's classes accesssible via the _ClassLoader::getResourceAsStream_ method. However, note that this requires these class loaders to maintain a reference to the full binary representation of a class what consumes space on a JVM's heap. Therefore, you should only use the manifest versions if you plan to actually access the binary format. Since the INJECTION strategy works via reflection and without a possibility to change the semantics of the ClassLoader::getResourceAsStream method, it is naturally not available in a manifest version.

> Example
>> 
```Java
Class<?> type = new ByteBuddy();
	.subclass(Object.class)
	.make
	.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
	.getLoaded();
```
The getLoaded method returns an instance of a Java Class that represents the dynamic class which is now loaded.

When loading classes, the predefined class loading strategies are executed by applying the **ProtectionDomain** of the current execution context. Alternatively, all default strategies offer the specification of an explicit protection domain by calling the withProtectionDomain method. Defining an explicit protection domain is important when using security managers or when working with classes that are defined in signed jars.


 

## Reloading a class

Due to the HotSwap feature of JVM, existing classes can however be redefined even after they are loaded. 


**ClassReloadingStrategy**

redefine Foo to become Bar:
```Java
ByteBuddyAgent.install();
Foo foo = new Foo();
new ByteBuddy()
  .redefine(Bar.class)
  .name(Foo.class.getName())
  .make()
  .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
assertThat(foo.m(), is("bar"));
```

> **HotSwap is only accessible using a so-called Java agent.** 
* Such an agent can be installed by either:
	* Specifying it on the startup of the JVM by using the <i>-javaagent</i> parameter, where **the paramter's argument needs to be Byte Buddy's agent jar**
	* Using ByteBuddyAgent.installOnOpenJDK() if a Java agent is run from a JDK-installation of the JVM;

Notice that the JVM identifies types by their name and a class loader. By renaming Bar to Foo and applying this definition, we eventually redefine the type we renamed Bar into.

* Drawback of HotSwap: 
	* Requires the redefined classes **apply the same class schema both before and after a class redefinition.** == add methods or fields when reloading classes is not allowed. Known that class rebasing does not work for the _ClassReloadingStrategy_ since Byte Buddy defines copies of the original mehtods for any rebased class.

	* Class redefinition does not work for classes with **an explicit class initializer method (a static block within a class)** because this initializer needs to be copied into an extra method as well.


---

## Working with unloaded classes

Byte Buddy abstracts over Java's reflection API such that a Class instance is for example internally represented by an instance of a **TypeDescription**. 

As a matter of fact, Byte Buddy only knows how to process a provided Class by an adapter that implements the TypeDescription interface. The big advantage over this abstraction is that information on classes do not need to be provided by a **ClassLoader** but can be provided by any other sources.

Byte Buddy provides a canonical manner for getting hold of a class's **TypeDescription** using a **TypePool**. 

TypePool.Default implementation parses the binary format of a class and represents it as the required TypeDescription.  Similarly to a **ClassLoader** it maintains a cache for represented classes which is also customizable. Also, it normally retrieves the binary format of a class from a ClassLoader, however without instructing it to load this class.

**The JVM only loads a class on its first usage.**

Example code again!

```Java
//We can safely redefine a class such as:
package foo;
class Bar{}
```
```Java
//Right at program startup before running any other code:
class MyApplication {
  public static void main(String[] args) {
    TypePool typePool = TypePool.Default.ofClassPath();
    new ByteBuddy()
      .redefine(typePool.describe("foo.Bar").resolve(), // do not use 'Bar.class'
                ClassFileLocator.ForClassLoader.ofClassPath())
      .defineField("qux", String.class) 
      .make()
      .load(ClassLoader.getSystemClassLoader());
    assertThat(Bar.class.getDeclaredField("qux"), notNullValue());
  }
}
```

By explicitly loading the redefined class before its first use in the assertion statement, we forestall the JVM's built-in class loading. This way, the redefined definition of foo.Bar is loaded and used throughout our application's runtime.

> Note:
* Do not reference the class by a class literal when we use the TypePool to provide a description.
* When working with unloaded classes, we further need to specify a ClassFileLocator which allows to locate a class's class file. In the example above, we simply create a class file locator which scans the running application's class path for such files.

To be continue...(CREATE JAVA AGENTS)














---



## Reference:

http://bytebuddy.net/#/tutorial



