package net.fabricmc.fernflower.api;

import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.struct.StructField;
import org.jetbrains.java.decompiler.struct.StructMethod;

public interface IFabricJavadocProvider {
   String PROPERTY_NAME = "fabric:javadoc";

   String getClassDoc(StructClass var1);

   String getFieldDoc(StructClass var1, StructField var2);

   String getMethodDoc(StructClass var1, StructMethod var2);
}
