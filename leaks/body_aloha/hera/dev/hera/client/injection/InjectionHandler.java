+package dev.hera.client.injection;

import com.google.common.reflect.ClassPath;
import dev.hera.client.Client;
import dev.hera.client.injection.hooks.Hook;
import dev.hera.client.injection.hooks.impl.GuiScreenHook;
import dev.hera.client.injection.hooks.impl.MinecraftHook;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public class InjectionHandler {
    public static InjectionType type = InjectionType.VANILLA;
    public static Remapper remapper = new Remapper();
    public static List<Hook> hooks = new ArrayList<>();
    public static boolean selfDestruct = false;
    public static HashMap<String, byte[]> classes = new HashMap<>();



    public void init(){

        /* get type */
        try{
            this.getClass().getClassLoader().loadClass("net.minecraft.client.Minecraft");
            type = InjectionType.LUNAR;
        }catch (Exception ignored){}

        /* initialize hooks */

        hooks.add(new GuiScreenHook());
        hooks.add(new MinecraftHook());

        /* load the remapper */
        remapper.load("C:\\Users\\ilyas\\JNIPatchMaker\\output\\mappings.txt");

        /* remap all the classes */
        ClassLoader cl = getClass().getClassLoader();
        try {
            Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(cl).getTopLevelClassesRecursive("dev.hera.client");
            for(ClassPath.ClassInfo classe : classesInPackage) {
                System.out.println("[agent] found " + classe.getName());
                cl.loadClass(classe.getName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* initialize the client */
        new Client();
        System.out.println("[agent] loaded successfuly");

    }

    public byte[] transform(String className, byte[] classBytes){
        if(selfDestruct && classes.containsKey(className)){
            return classes.get(className);
        }
        if(className.startsWith("dev/hera/client")){

            System.out.println("[agent] remapping " + className);

            ClassReader classReader = new ClassReader(classBytes);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            if(!selfDestruct){

                if(remapper.mappings.containsKey(classNode.superName)){
                    System.out.println("[agent] " + classNode.name + " extends " + classNode.superName);
                    remapper.addChildExtending(classNode.superName, classNode.name);
                }

                SimpleRemapper remapper = new SimpleRemapper(InjectionHandler.remapper.mappings);
                ClassNode remappedClassNode = new ClassNode();
                ClassRemapper adapter = new ClassRemapper(remappedClassNode, remapper);
                classNode.accept(adapter);
                classNode = remappedClassNode;
            }else{
                return new byte[0];
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        Optional<Hook> hook = hooks.stream().filter(h -> remapper.getClassMap(h.klass).equalsIgnoreCase(className)).findFirst();
        if(!hook.isPresent())
            return classBytes;

        System.out.println("[agent] hooking " + className);

        classes.put(className, classBytes);
        ClassReader classReader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        hook.get().inject(classReader, classNode);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();

    }
}