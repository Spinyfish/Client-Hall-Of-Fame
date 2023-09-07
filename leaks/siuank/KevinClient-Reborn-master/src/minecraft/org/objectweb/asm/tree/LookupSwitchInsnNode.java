package org.objectweb.asm.tree;

import java.util.List;
import java.util.Map;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class LookupSwitchInsnNode extends AbstractInsnNode {
  public LabelNode dflt;
  
  public List<Integer> keys;
  
  public List<LabelNode> labels;
  
  public LookupSwitchInsnNode(LabelNode dflt, int[] keys, LabelNode[] labels) {
    super(171);
    this.dflt = dflt;
    this.keys = Util.asArrayList(keys);
    this.labels = Util.asArrayList(labels);
  }
  
  public int getType() { return 12; }
  
  public void accept(MethodVisitor methodVisitor) {
    int[] keysArray = new int[this.keys.size()];
    for (int i = 0, n = keysArray.length; i < n; i++)
      keysArray[i] = ((Integer)this.keys.get(i)).intValue(); 
    Label[] labelsArray = new Label[this.labels.size()];
    for (int i = 0, n = labelsArray.length; i < n; i++)
      labelsArray[i] = ((LabelNode)this.labels.get(i)).getLabel(); 
    methodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), keysArray, labelsArray);
    acceptAnnotations(methodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> clonedLabels) {
    LookupSwitchInsnNode clone = new LookupSwitchInsnNode(clone(this.dflt, clonedLabels), null, clone(this.labels, clonedLabels));
    clone.keys.addAll(this.keys);
    return clone.cloneAnnotations(this);
  }
}
