package com.example;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

import java.util.List;
import java.util.concurrent.Callable;

public final class LibEnv {

    private static final ObjectHandles globalHandles = ObjectHandles.create();

    @CEntryPoint(name = "create_component")
    public static ObjectHandle createComponent(IsolateThread thread, CCharPointer name) {
        String componentName = CTypeConversion.toJavaString(name);
        return globalHandles.create(new Component(componentName));
    }

    @CEntryPoint(name = "get_process")
    public static ObjectHandle getProcess(IsolateThread thread, ObjectHandle componentHandle) {
        Component component = globalHandles.get(componentHandle);
        List<Callable<String>> processes = component.getProcess();
        return globalHandles.create(processes);
    }

    @CEntryPoint(name = "run_process")
    public static CCharPointer runProcess(IsolateThread thread, ObjectHandle processesHandle, int index) {
        List<Callable<String>> processes = globalHandles.get(processesHandle);
        try {
            String result = processes.get(index).call();
            return CTypeConversion.toCString(result).get();
        } catch (Exception e) {
            return CTypeConversion.toCString("Error: " + e.getMessage()).get();
        }
    }

    @CEntryPoint(name = "get_processes_size")
    public static int getProcessesSize(IsolateThread thread, ObjectHandle processesHandle) {
        List<Callable<String>> processes = globalHandles.get(processesHandle);
        return processes.size();
    }

    @CEntryPoint(name = "dispose_handle")
    public static void disposeHandle(IsolateThread thread, ObjectHandle handle) {
        globalHandles.destroy(handle);
    }


}
