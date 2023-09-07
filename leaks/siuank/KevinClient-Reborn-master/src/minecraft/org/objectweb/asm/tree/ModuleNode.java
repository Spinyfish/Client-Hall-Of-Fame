package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;

public class ModuleNode extends ModuleVisitor {
  public String name;
  
  public int access;
  
  public String version;
  
  public String mainClass;
  
  public List<String> packages;
  
  public List<ModuleRequireNode> requires;
  
  public List<ModuleExportNode> exports;
  
  public List<ModuleOpenNode> opens;
  
  public List<String> uses;
  
  public List<ModuleProvideNode> provides;
  
  public ModuleNode(String name, int access, String version) {
    super(458752);
    if (getClass() != ModuleNode.class)
      throw new IllegalStateException(); 
    this.name = name;
    this.access = access;
    this.version = version;
  }
  
  public ModuleNode(int api, String name, int access, String version, List<ModuleRequireNode> requires, List<ModuleExportNode> exports, List<ModuleOpenNode> opens, List<String> uses, List<ModuleProvideNode> provides) {
    super(api);
    this.name = name;
    this.access = access;
    this.version = version;
    this.requires = requires;
    this.exports = exports;
    this.opens = opens;
    this.uses = uses;
    this.provides = provides;
  }
  
  public void visitMainClass(String mainClass) { this.mainClass = mainClass; }
  
  public void visitPackage(String packaze) {
    if (this.packages == null)
      this.packages = new ArrayList(5); 
    this.packages.add(packaze);
  }
  
  public void visitRequire(String module, int access, String version) {
    if (this.requires == null)
      this.requires = new ArrayList(5); 
    this.requires.add(new ModuleRequireNode(module, access, version));
  }
  
  public void visitExport(String packaze, int access, String... modules) {
    if (this.exports == null)
      this.exports = new ArrayList(5); 
    this.exports.add(new ModuleExportNode(packaze, access, Util.asArrayList(modules)));
  }
  
  public void visitOpen(String packaze, int access, String... modules) {
    if (this.opens == null)
      this.opens = new ArrayList(5); 
    this.opens.add(new ModuleOpenNode(packaze, access, Util.asArrayList(modules)));
  }
  
  public void visitUse(String service) {
    if (this.uses == null)
      this.uses = new ArrayList(5); 
    this.uses.add(service);
  }
  
  public void visitProvide(String service, String... providers) {
    if (this.provides == null)
      this.provides = new ArrayList(5); 
    this.provides.add(new ModuleProvideNode(service, Util.asArrayList(providers)));
  }
  
  public void visitEnd() {}
  
  public void accept(ClassVisitor classVisitor) {
    ModuleVisitor moduleVisitor = classVisitor.visitModule(this.name, this.access, this.version);
    if (moduleVisitor == null)
      return; 
    if (this.mainClass != null)
      moduleVisitor.visitMainClass(this.mainClass); 
    if (this.packages != null)
      for (int i = 0, n = this.packages.size(); i < n; i++)
        moduleVisitor.visitPackage((String)this.packages.get(i));  
    if (this.requires != null)
      for (int i = 0, n = this.requires.size(); i < n; i++)
        ((ModuleRequireNode)this.requires.get(i)).accept(moduleVisitor);  
    if (this.exports != null)
      for (int i = 0, n = this.exports.size(); i < n; i++)
        ((ModuleExportNode)this.exports.get(i)).accept(moduleVisitor);  
    if (this.opens != null)
      for (int i = 0, n = this.opens.size(); i < n; i++)
        ((ModuleOpenNode)this.opens.get(i)).accept(moduleVisitor);  
    if (this.uses != null)
      for (int i = 0, n = this.uses.size(); i < n; i++)
        moduleVisitor.visitUse((String)this.uses.get(i));  
    if (this.provides != null)
      for (int i = 0, n = this.provides.size(); i < n; i++)
        ((ModuleProvideNode)this.provides.get(i)).accept(moduleVisitor);  
  }
}
