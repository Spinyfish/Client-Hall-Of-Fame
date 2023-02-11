package org.objectweb.asm.tree;

import org.objectweb.asm.ModuleVisitor;

public class ModuleRequireNode {
  public String module;
  
  public int access;
  
  public String version;
  
  public ModuleRequireNode(String module, int access, String version) {
    this.module = module;
    this.access = access;
    this.version = version;
  }
  
  public void accept(ModuleVisitor moduleVisitor) { moduleVisitor.visitRequire(this.module, this.access, this.version); }
}
