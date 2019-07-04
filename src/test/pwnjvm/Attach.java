package test.pwnjvm;

import java.io.IOException;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/*
 * @Description: 将agent附加到指定的JVM
 * 注：Attach机制提供了一种JVM进程间通信的能力，能让一个进程传命令给另外一个进程，并让它执行内部的一些操作。
 * Attach agent to JVM.
 */

public class Attach {
    
    //需要手动输入Virtual Machine的pid值。Require manually input of pid value.
    public static void main(String[] args) throws AttachNotSupportedException,
        IOException, AgentLoadException, AgentInitializationException { 
    	
        //定义agent Path，此处应注意按情况修改
        String agentPath = "D:\\work\\workspace\\myjar\\loaded.jar";
        String pid = args[0]; 
        VirtualMachine vm = VirtualMachine.attach(pid);
        //附加到指定的JVM。Attaches to a Java virtual machine.
        vm.loadAgent(agentPath);
            
    }
}
