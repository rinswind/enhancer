package enhancer.examples.generator.sproxy.internal;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getArgumentTypes;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getReturnType;

import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.osgi.framework.BundleContext;

import enhancer.Generator;

public class ServiceProxyGenerator implements Generator {
  private static final String INTERNAL = ServiceProxyGenerator.class.getPackage().getName();
  
  private final String BC = getInternalName(BundleContext.class);
  private final String BC_DESC = "L" + BC + ";";
  
  private final String HANDLE = getInternalName(ServiceHandle.class);
  private final String HANDLE_DESC = "L" + HANDLE + ";";
  
  private final String HANDLE_FIELD = "handle";

  @Override
  public boolean isInternal(String name) {
    /*
     * All classes from the same package as the generator are treated as proxy
     * support classes.
     */
    return name.startsWith(INTERNAL);
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
    final String inputIntName = getInternalName(input);
    final String[] ifaces = new String[] { inputIntName };

    /* Generation start */
    final ClassWriter cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
    cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, proxyName, null, base, ifaces);

    /* Generate delegate field */
    cw.visitField(ACC_PRIVATE | ACC_FINAL, HANDLE_FIELD, HANDLE_DESC, null, null);

    generateInit(cw, proxyName, inputIntName);

    for (Method method : input.getMethods()) {
      generateMethod(cw, proxyName, method);
    }

    /* Generation end */
    cw.visitEnd();
    return cw.toByteArray();
  }

  private void generateInit(ClassWriter cw, String proxyName, String inputName) {
    /* Generation start */
    final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + BC_DESC + ")V", null,
        null);
    mv.visitCode();

    /* Call super constructor */
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

    /* Construct a service handle and store it in a field */
    mv.visitVarInsn(ALOAD, 0);
    mv.visitTypeInsn(NEW, HANDLE);
    mv.visitInsn(DUP);
    mv.visitLdcInsn(Type.getType("L" + inputName + ";"));
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESPECIAL, HANDLE, "<init>", "(Ljava/lang/Class;Lorg/osgi/framework/BundleContext;)V");
    mv.visitFieldInsn(PUTFIELD, proxyName, HANDLE_FIELD, HANDLE_DESC);

    /* Return */
    mv.visitInsn(RETURN);

    /* Generation end */
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private void generateMethod(ClassWriter cw, String proxyName, Method method) {
    final String methName = method.getName();
    final String methDescr = Type.getMethodDescriptor(method);
    final String[] methdExcs = getInternalNames(method.getExceptionTypes());

    /* Generation start */
    final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methName, methDescr, null, methdExcs);
    mv.visitCode();

    /* Dereference dynamic handle */
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, proxyName, HANDLE_FIELD, HANDLE_DESC);
    mv.visitMethodInsn(INVOKEVIRTUAL, HANDLE, "get", "()Ljava/lang/Object;");

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
