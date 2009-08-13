package enhancer.examples.generator.aop;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;
import static org.objectweb.asm.Type.getArgumentTypes;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getReturnType;

import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import enhancer.Generator;

public class LoggingGenerator implements Generator {
  private final String DELEGATE = "delegate";
  
  @Override
  public boolean isInternal(String name) {
    /* We do not use any internal classes to support our proxy */
    return false;
  }
  
  @Override
  public byte[] generate(String inputName, String outputName, ClassLoader context) {
    final Class<?> input;
    try {
      input = context.loadClass(inputName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    
    if (!input.isInterface()) {
      throw new RuntimeException("not interface: " + inputName);
    }
    
    final String proxyName = outputName.replace('.', '/');
    final String base = getInternalName(Object.class);
    final String[] ifaces = new String[] {getInternalName(input)};
    final String fieldDesc = "L" + getInternalName(input) + ";";
    
    /* Generation start */
    final ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
    cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, proxyName, null, base, ifaces);
    
    /* Generate delegate field */
    cw.visitField(ACC_PRIVATE | ACC_FINAL, DELEGATE, fieldDesc, null, null);
    
    generateInit(cw, proxyName, fieldDesc);
    
    for (Method method : input.getMethods()) {
      generateMethod(cw, proxyName, fieldDesc, method);
    }
    
    /* Generation end */
    cw.visitEnd();
    return cw.toByteArray();
  }
  
  private void generateInit(ClassWriter cw, String proxyName, String fieldDesc) {
    /* Generation start */
    final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + fieldDesc + ")V", null, null);
    mv.visitCode();
    
    /* Call super constructor */
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
    
    /* Set delegate field */
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitFieldInsn(PUTFIELD, proxyName, DELEGATE, fieldDesc);
    
    /* Return */
    mv.visitInsn(RETURN);
    
    /* Generation end */
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private void generateMethod(ClassWriter cw, String proxyName, String fieldDesc, Method method) {
    final String methName = method.getName();
    final String methDescr = Type.getMethodDescriptor(method);
    final String[] methdExcs = getInternalNames(method.getExceptionTypes());
    
    /* Generation start */
    final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methName, methDescr, null, methdExcs);
    mv.visitCode();
    
    /* Dump "entry <methog sig>" on the screen */
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    mv.visitLdcInsn("entry " + method);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
    
    /* Load delegate */
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, proxyName, DELEGATE, fieldDesc);
    
    final Class<?> clazz = method.getDeclaringClass();
    final String subjectName = getInternalName(clazz);
    if (!clazz.isInterface()) {
      mv.visitTypeInsn(CHECKCAST, subjectName);
    }
    
    /* Load arguments */
    int i = 1;
    for (Type type : getArgumentTypes(method)) {
      mv.visitVarInsn(type.getOpcode(ILOAD), i);
      i += type.getSize();
    }
    
    /* Invoke */
    if (clazz.isInterface()) {
      mv.visitMethodInsn(INVOKEINTERFACE, subjectName, methName, methDescr);
    } else {
      mv.visitMethodInsn(INVOKEVIRTUAL, subjectName, methName, methDescr);
    }
    
    /* Return */
    mv.visitInsn(getReturnType(method).getOpcode(IRETURN));
    
    /* Generation end */
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }
  
  private static String[] getInternalNames(final Class<?>... clazzes) {
    final String[] names = new String[clazzes.length];
    for (int i = 0; i < names.length; i++) {
      names[i] = getInternalName(clazzes[i]);
    }
    return names;
  }
}
