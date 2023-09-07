package optifine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

public class AccessFixer
{
    public static void fixMemberAccess(ClassNode classOld, ClassNode classNew)
    {
        List<FieldNode> list = classOld.fields;
        List<FieldNode> list1 = classNew.fields;
        Map<String, FieldNode> map = getMapFields(list);

        for (FieldNode fieldnode : list1)
        {
            String s = fieldnode.name;
            FieldNode fieldnode1 = (FieldNode)map.get(s);

            if (fieldnode1 != null && fieldnode.access != fieldnode1.access)
            {
                fieldnode.access = combineAccess(fieldnode.access, fieldnode1.access);
            }
        }

        List<MethodNode> list2 = classOld.methods;
        List<MethodNode> list3 = classNew.methods;
        Map<String, MethodNode> map1 = getMapMethods(list2);

        for (MethodNode methodnode : list3)
        {
            String s1 = methodnode.name + methodnode.desc;
            MethodNode methodnode1 = (MethodNode)map1.get(s1);

            if (methodnode1 != null && methodnode.access != methodnode1.access)
            {
                methodnode.access = combineAccess(methodnode.access, methodnode1.access);
            }
        }

        List<InnerClassNode> list4 = classOld.innerClasses;
        List<InnerClassNode> list5 = classNew.innerClasses;
        Map<String, InnerClassNode> map2 = getMapInnerClasses(list4);

        for (InnerClassNode innerclassnode : list5)
        {
            String s2 = innerclassnode.name;
            InnerClassNode innerclassnode1 = (InnerClassNode)map2.get(s2);

            if (innerclassnode1 != null && innerclassnode.access != innerclassnode1.access)
            {
                int i = combineAccess(innerclassnode.access, innerclassnode1.access);
                innerclassnode.access = i;
            }
        }

        if (classNew.access != classOld.access)
        {
            int j = combineAccess(classNew.access, classOld.access);
            classNew.access = j;
        }
    }

    private static int combineAccess(int access, int access2)
    {
        if (access == access2)
        {
            return access;
        }
        else
        {
            int i = 7;
            int j = access & ~i;

            if (!isSet(access, 16) || !isSet(access2, 16))
            {
                j &= -17;
            }

            if (!isSet(access, 1) && !isSet(access2, 1))
            {
                if (!isSet(access, 4) && !isSet(access2, 4))
                {
                    return !isSet(access, 2) && !isSet(access2, 2) ? j : j | 2;
                }
                else
                {
                    return j | 4;
                }
            }
            else
            {
                return j | 1;
            }
        }
    }

    private static boolean isSet(int access, int flag)
    {
        return (access & flag) != 0;
    }

    public static Map<String, FieldNode> getMapFields(List<FieldNode> fields)
    {
        Map<String, FieldNode> map = new LinkedHashMap<>();

        for (FieldNode fieldnode : fields)
        {
            String s = fieldnode.name;
            map.put(s, fieldnode);
        }

        return map;
    }

    public static Map<String, MethodNode> getMapMethods(List<MethodNode> methods)
    {
        Map<String, MethodNode> map = new LinkedHashMap<>();

        for (MethodNode methodnode : methods)
        {
            String s = methodnode.name + methodnode.desc;
            map.put(s, methodnode);
        }

        return map;
    }

    public static Map<String, InnerClassNode> getMapInnerClasses(List<InnerClassNode> innerClasses)
    {
        Map<String, InnerClassNode> map = new LinkedHashMap<>();

        for (InnerClassNode innerclassnode : innerClasses)
        {
            String s = innerclassnode.name;
            map.put(s, innerclassnode);
        }

        return map;
    }
}
