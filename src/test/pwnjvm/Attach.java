package test.pwnjvm;

import java.io.IOException;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/*
 *@Description: 将agent附加到制定的jvm 
 * Attach机制：提供一种jvm进程间通信的能力，能让一个进程传命令给另外一个进程，并让它执行内部的一些操作。
 */
public class Attach {
    public static void main(String[] args) throws AttachNotSupportedException,
        IOException, AgentLoadException, AgentInitializationException { 
    	
        String agentPath = "D:\\work\\workspace\\myjar\\loaded.jar";
        //定义agent Path
        String vid = args[0]; 
        VirtualMachine vm = VirtualMachine.attach(vid);
        //触发attach pid的关键，Attaches to a Java virtual machine.
        vm.loadAgent(agentPath);
    }
}