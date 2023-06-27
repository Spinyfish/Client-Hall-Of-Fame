package lombok.launch;

import java.io.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import lombok.eclipse.*;
import org.eclipse.jdt.internal.compiler.ast.*;
import java.util.*;
import org.eclipse.jdt.internal.core.dom.rewrite.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.corext.refactoring.*;
import org.eclipse.jdt.internal.core.*;
import org.eclipse.core.resources.*;
import java.lang.reflect.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.corext.refactoring.structure.*;

final class PatchFixesHider
{
    public static final class Util
    {
        private static ClassLoader shadowLoader;
        
        public static ClassLoader getShadowLoader() {
            if (Util.shadowLoader == null) {
                try {
                    Class.forName("lombok.core.LombokNode");
                    Util.shadowLoader = Util.class.getClassLoader();
                }
                catch (ClassNotFoundException ex) {
                    Util.shadowLoader = Main.getShadowClassLoader();
                }
            }
            return Util.shadowLoader;
        }
        
        public static Class<?> shadowLoadClass(final String name) {
            try {
                return Class.forName(name, true, getShadowLoader());
            }
            catch (ClassNotFoundException e) {
                throw sneakyThrow(e);
            }
        }
        
        public static Method findMethod(final Class<?> type, final String name, final Class<?>... parameterTypes) {
            try {
                return type.getDeclaredMethod(name, parameterTypes);
            }
            catch (NoSuchMethodException e) {
                throw sneakyThrow(e);
            }
        }
        
        public static Method findMethod(final Class<?> type, final String name, final String... parameterTypes) {
            Method[] declaredMethods;
            for (int length = (declaredMethods = type.getDeclaredMethods()).length, i = 0; i < length; ++i) {
                final Method m = declaredMethods[i];
                if (name.equals(m.getName()) && sameTypes(m.getParameterTypes(), parameterTypes)) {
                    return m;
                }
            }
            throw sneakyThrow(new NoSuchMethodException(String.valueOf(type.getName()) + "::" + name));
        }
        
        public static Method findMethodAnyArgs(final Class<?> type, final String name) {
            Method[] declaredMethods;
            for (int length = (declaredMethods = type.getDeclaredMethods()).length, i = 0; i < length; ++i) {
                final Method m = declaredMethods[i];
                if (name.equals(m.getName())) {
                    return m;
                }
            }
            throw sneakyThrow(new NoSuchMethodException(String.valueOf(type.getName()) + "::" + name));
        }
        
        public static Object invokeMethod(final Method method, final Object... args) {
            try {
                return method.invoke(null, args);
            }
            catch (IllegalAccessException e) {
                throw sneakyThrow(e);
            }
            catch (InvocationTargetException e2) {
                throw sneakyThrow(e2.getCause());
            }
        }
        
        private static RuntimeException sneakyThrow(final Throwable t) {
            if (t == null) {
                throw new NullPointerException("t");
            }
            sneakyThrow0(t);
            return null;
        }
        
        private static <T extends Throwable> void sneakyThrow0(final Throwable t) throws T, Throwable {
            throw t;
        }
        
        private static boolean sameTypes(final Class<?>[] types, final String[] typeNames) {
            if (types.length != typeNames.length) {
                return false;
            }
            for (int i = 0; i < types.length; ++i) {
                if (!types[i].getName().equals(typeNames[i])) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static final class LombokDeps
    {
        public static final Method ADD_LOMBOK_NOTES;
        public static final Method POST_COMPILER_BYTES_STRING;
        public static final Method POST_COMPILER_OUTPUTSTREAM;
        public static final Method POST_COMPILER_BUFFEREDOUTPUTSTREAM_STRING_STRING;
        
        static {
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchFixesShadowLoaded");
            ADD_LOMBOK_NOTES = Util.findMethod(shadowed, "addLombokNotesToEclipseAboutDialog", String.class, String.class);
            POST_COMPILER_BYTES_STRING = Util.findMethod(shadowed, "runPostCompiler", byte[].class, String.class);
            POST_COMPILER_OUTPUTSTREAM = Util.findMethod(shadowed, "runPostCompiler", OutputStream.class);
            POST_COMPILER_BUFFEREDOUTPUTSTREAM_STRING_STRING = Util.findMethod(shadowed, "runPostCompiler", BufferedOutputStream.class, String.class, String.class);
        }
        
        public static String addLombokNotesToEclipseAboutDialog(final String origReturnValue, final String key) {
            try {
                return (String)Util.invokeMethod(LombokDeps.ADD_LOMBOK_NOTES, origReturnValue, key);
            }
            catch (Throwable t) {
                return origReturnValue;
            }
        }
        
        public static byte[] runPostCompiler(final byte[] bytes, final String fileName) {
            return (byte[])Util.invokeMethod(LombokDeps.POST_COMPILER_BYTES_STRING, bytes, fileName);
        }
        
        public static OutputStream runPostCompiler(final OutputStream out) throws IOException {
            return (OutputStream)Util.invokeMethod(LombokDeps.POST_COMPILER_OUTPUTSTREAM, out);
        }
        
        public static BufferedOutputStream runPostCompiler(final BufferedOutputStream out, final String path, final String name) throws IOException {
            return (BufferedOutputStream)Util.invokeMethod(LombokDeps.POST_COMPILER_BUFFEREDOUTPUTSTREAM_STRING_STRING, out, path, name);
        }
    }
    
    public static final class Transform
    {
        private static Method TRANSFORM;
        private static Method TRANSFORM_SWAPPED;
        
        private static synchronized void init(final ClassLoader prepend) {
            if (Transform.TRANSFORM != null) {
                return;
            }
            Main.prependClassLoader(prepend);
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.TransformEclipseAST");
            Transform.TRANSFORM = Util.findMethodAnyArgs(shadowed, "transform");
            Transform.TRANSFORM_SWAPPED = Util.findMethodAnyArgs(shadowed, "transform_swapped");
        }
        
        public static void transform(final Object parser, final Object ast) throws IOException {
            init(parser.getClass().getClassLoader());
            Util.invokeMethod(Transform.TRANSFORM, parser, ast);
        }
        
        public static void transform_swapped(final Object ast, final Object parser) throws IOException {
            init(parser.getClass().getClassLoader());
            Util.invokeMethod(Transform.TRANSFORM_SWAPPED, ast, parser);
        }
    }
    
    public static final class Delegate
    {
        private static final Method HANDLE_DELEGATE_FOR_TYPE;
        private static final Method ADD_GENERATED_DELEGATE_METHODS;
        
        static {
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchDelegatePortal");
            HANDLE_DELEGATE_FOR_TYPE = Util.findMethod(shadowed, "handleDelegateForType", Object.class);
            ADD_GENERATED_DELEGATE_METHODS = Util.findMethod(shadowed, "addGeneratedDelegateMethods", Object.class, Object.class);
        }
        
        public static boolean handleDelegateForType(final Object classScope) {
            return (boolean)Util.invokeMethod(Delegate.HANDLE_DELEGATE_FOR_TYPE, classScope);
        }
        
        public static Object[] addGeneratedDelegateMethods(final Object returnValue, final Object javaElement) {
            return (Object[])Util.invokeMethod(Delegate.ADD_GENERATED_DELEGATE_METHODS, returnValue, javaElement);
        }
    }
    
    public static final class ValPortal
    {
        private static final Method COPY_INITIALIZATION_OF_FOR_EACH_ITERABLE;
        private static final Method COPY_INITIALIZATION_OF_LOCAL_DECLARATION;
        private static final Method ADD_FINAL_AND_VAL_ANNOTATION_TO_VARIABLE_DECLARATION_STATEMENT;
        private static final Method ADD_FINAL_AND_VAL_ANNOTATION_TO_SINGLE_VARIABLE_DECLARATION;
        
        static {
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchValEclipsePortal");
            COPY_INITIALIZATION_OF_FOR_EACH_ITERABLE = Util.findMethod(shadowed, "copyInitializationOfForEachIterable", Object.class);
            COPY_INITIALIZATION_OF_LOCAL_DECLARATION = Util.findMethod(shadowed, "copyInitializationOfLocalDeclaration", Object.class);
            ADD_FINAL_AND_VAL_ANNOTATION_TO_VARIABLE_DECLARATION_STATEMENT = Util.findMethod(shadowed, "addFinalAndValAnnotationToVariableDeclarationStatement", Object.class, Object.class, Object.class);
            ADD_FINAL_AND_VAL_ANNOTATION_TO_SINGLE_VARIABLE_DECLARATION = Util.findMethod(shadowed, "addFinalAndValAnnotationToSingleVariableDeclaration", Object.class, Object.class, Object.class);
        }
        
        public static void copyInitializationOfForEachIterable(final Object parser) {
            Util.invokeMethod(ValPortal.COPY_INITIALIZATION_OF_FOR_EACH_ITERABLE, parser);
        }
        
        public static void copyInitializationOfLocalDeclaration(final Object parser) {
            Util.invokeMethod(ValPortal.COPY_INITIALIZATION_OF_LOCAL_DECLARATION, parser);
        }
        
        public static void addFinalAndValAnnotationToVariableDeclarationStatement(final Object converter, final Object out, final Object in) {
            Util.invokeMethod(ValPortal.ADD_FINAL_AND_VAL_ANNOTATION_TO_VARIABLE_DECLARATION_STATEMENT, converter, out, in);
        }
        
        public static void addFinalAndValAnnotationToSingleVariableDeclaration(final Object converter, final Object out, final Object in) {
            Util.invokeMethod(ValPortal.ADD_FINAL_AND_VAL_ANNOTATION_TO_SINGLE_VARIABLE_DECLARATION, converter, out, in);
        }
    }
    
    public static final class Val
    {
        private static final String BLOCK_SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
        private static final String LOCAL_DECLARATION_SIG = "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration";
        private static final String FOREACH_STATEMENT_SIG = "org.eclipse.jdt.internal.compiler.ast.ForeachStatement";
        private static final Method HANDLE_VAL_FOR_LOCAL_DECLARATION;
        private static final Method HANDLE_VAL_FOR_FOR_EACH;
        
        static {
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchVal");
            HANDLE_VAL_FOR_LOCAL_DECLARATION = Util.findMethod(shadowed, "handleValForLocalDeclaration", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "org.eclipse.jdt.internal.compiler.lookup.BlockScope");
            HANDLE_VAL_FOR_FOR_EACH = Util.findMethod(shadowed, "handleValForForEach", "org.eclipse.jdt.internal.compiler.ast.ForeachStatement", "org.eclipse.jdt.internal.compiler.lookup.BlockScope");
        }
        
        public static boolean handleValForLocalDeclaration(final Object local, final Object scope) {
            return (boolean)Util.invokeMethod(Val.HANDLE_VAL_FOR_LOCAL_DECLARATION, local, scope);
        }
        
        public static boolean handleValForForEach(final Object forEach, final Object scope) {
            return (boolean)Util.invokeMethod(Val.HANDLE_VAL_FOR_FOR_EACH, forEach, scope);
        }
        
        public static TypeBinding skipResolveInitializerIfAlreadyCalled(final Expression expr, final BlockScope scope) {
            if (expr.resolvedType != null) {
                return expr.resolvedType;
            }
            try {
                return expr.resolveType(scope);
            }
            catch (NullPointerException ex) {
                return null;
            }
            catch (ArrayIndexOutOfBoundsException ex2) {
                return null;
            }
        }
        
        public static TypeBinding skipResolveInitializerIfAlreadyCalled2(final Expression expr, final BlockScope scope, final LocalDeclaration decl) {
            if (decl != null && LocalDeclaration.class.equals(decl.getClass()) && expr.resolvedType != null) {
                return expr.resolvedType;
            }
            try {
                return expr.resolveType(scope);
            }
            catch (NullPointerException ex) {
                return null;
            }
            catch (ArrayIndexOutOfBoundsException ex2) {
                return null;
            }
        }
    }
    
    public static final class ExtensionMethod
    {
        private static final String MESSAGE_SEND_SIG = "org.eclipse.jdt.internal.compiler.ast.MessageSend";
        private static final String TYPE_BINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.TypeBinding";
        private static final String SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.Scope";
        private static final String BLOCK_SCOPE_SIG = "org.eclipse.jdt.internal.compiler.lookup.BlockScope";
        private static final String TYPE_BINDINGS_SIG = "[Lorg.eclipse.jdt.internal.compiler.lookup.TypeBinding;";
        private static final String PROBLEM_REPORTER_SIG = "org.eclipse.jdt.internal.compiler.problem.ProblemReporter";
        private static final String METHOD_BINDING_SIG = "org.eclipse.jdt.internal.compiler.lookup.MethodBinding";
        private static final String AST_NODE_SIG = "org.eclipse.jdt.internal.compiler.ast.ASTNode";
        private static final Method RESOLVE_TYPE;
        private static final Method ERROR_NO_METHOD_FOR;
        private static final Method INVALID_METHOD;
        private static final Method INVALID_METHOD2;
        private static final Method NON_STATIC_ACCESS_TO_STATIC_METHOD;
        
        static {
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchExtensionMethod");
            RESOLVE_TYPE = Util.findMethod(shadowed, "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.BlockScope");
            ERROR_NO_METHOD_FOR = Util.findMethod(shadowed, "errorNoMethodFor", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "[Lorg.eclipse.jdt.internal.compiler.lookup.TypeBinding;");
            INVALID_METHOD = Util.findMethod(shadowed, "invalidMethod", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding");
            INVALID_METHOD2 = Util.findMethod(shadowed, "invalidMethod", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.Scope");
            NON_STATIC_ACCESS_TO_STATIC_METHOD = Util.findMethod(shadowed, "nonStaticAccessToStaticMethod", "org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "org.eclipse.jdt.internal.compiler.ast.ASTNode", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.ast.MessageSend");
        }
        
        public static Object resolveType(final Object resolvedType, final Object methodCall, final Object scope) {
            return Util.invokeMethod(ExtensionMethod.RESOLVE_TYPE, resolvedType, methodCall, scope);
        }
        
        public static void errorNoMethodFor(final Object problemReporter, final Object messageSend, final Object recType, final Object params) {
            Util.invokeMethod(ExtensionMethod.ERROR_NO_METHOD_FOR, problemReporter, messageSend, recType, params);
        }
        
        public static void invalidMethod(final Object problemReporter, final Object messageSend, final Object method) {
            Util.invokeMethod(ExtensionMethod.INVALID_METHOD, problemReporter, messageSend, method);
        }
        
        public static void invalidMethod(final Object problemReporter, final Object messageSend, final Object method, final Object scope) {
            Util.invokeMethod(ExtensionMethod.INVALID_METHOD2, problemReporter, messageSend, method, scope);
        }
        
        public static void nonStaticAccessToStaticMethod(final Object problemReporter, final Object location, final Object method, final Object messageSend) {
            Util.invokeMethod(ExtensionMethod.NON_STATIC_ACCESS_TO_STATIC_METHOD, problemReporter, location, method, messageSend);
        }
    }
    
    public static final class Javadoc
    {
        private static final Method GET_HTML;
        private static final Method PRINT_METHOD;
        
        static {
            final Class<?> shadowed = Util.shadowLoadClass("lombok.eclipse.agent.PatchJavadoc");
            GET_HTML = Util.findMethod(shadowed, "getHTMLContentFromSource", String.class, Object.class);
            PRINT_METHOD = Util.findMethod(shadowed, "printMethod", AbstractMethodDeclaration.class, Integer.class, StringBuffer.class, TypeDeclaration.class);
        }
        
        public static String getHTMLContentFromSource(final String original, final IJavaElement member) {
            return (String)Util.invokeMethod(Javadoc.GET_HTML, original, member);
        }
        
        public static StringBuffer printMethod(final AbstractMethodDeclaration methodDeclaration, final int tab, final StringBuffer output, final TypeDeclaration type) {
            return (StringBuffer)Util.invokeMethod(Javadoc.PRINT_METHOD, methodDeclaration, tab, output, type);
        }
    }
    
    public static final class PatchFixes
    {
        public static final int ALREADY_PROCESSED_FLAG = 8388608;
        
        public static boolean isGenerated(final ASTNode node) {
            boolean result = false;
            try {
                result = (boolean)node.getClass().getField("$isGenerated").get(node);
                if (!result && node.getParent() != null && node.getParent() instanceof QualifiedName) {
                    result = isGenerated(node.getParent());
                }
            }
            catch (Exception ex) {}
            return result;
        }
        
        public static boolean isGenerated(final org.eclipse.jdt.internal.compiler.ast.ASTNode node) {
            boolean result = false;
            try {
                result = (node.getClass().getField("$generatedBy").get(node) != null);
            }
            catch (Exception ex) {}
            return result;
        }
        
        public static boolean isGenerated(final IMember member) {
            boolean result = false;
            try {
                result = (member.getNameRange().getLength() <= 0 || member.getNameRange().equals(member.getSourceRange()));
            }
            catch (JavaModelException ex) {}
            return result;
        }
        
        public static boolean isBlockedVisitorAndGenerated(final ASTNode node, final ASTVisitor visitor) {
            if (visitor == null) {
                return false;
            }
            final String className = visitor.getClass().getName();
            return (className.startsWith("org.eclipse.jdt.internal.corext.fix") || className.startsWith("org.eclipse.jdt.internal.ui.fix") || className.startsWith("org.eclipse.jdt.ls.core.internal.semantictokens.SemanticTokensVisitor")) && !className.equals("org.eclipse.jdt.internal.corext.fix.VariableDeclarationFixCore$WrittenNamesFinder") && isGenerated(node);
        }
        
        public static boolean isListRewriteOnGeneratedNode(final ListRewrite rewrite) {
            return isGenerated(rewrite.getParent());
        }
        
        public static boolean returnFalse(final Object object) {
            return false;
        }
        
        public static boolean returnTrue(final Object object) {
            return true;
        }
        
        public static List removeGeneratedNodes(final List list) {
            try {
                final List realNodes = new ArrayList(list.size());
                for (final Object node : list) {
                    if (!isGenerated((ASTNode)node)) {
                        realNodes.add(node);
                    }
                }
                return realNodes;
            }
            catch (Exception ex) {
                return list;
            }
        }
        
        public static String getRealMethodDeclarationSource(final String original, final Object processor, final MethodDeclaration declaration) throws Exception {
            if (!isGenerated((ASTNode)declaration)) {
                return original;
            }
            final List<Annotation> annotations = new ArrayList<Annotation>();
            for (final Object modifier : declaration.modifiers()) {
                if (modifier instanceof Annotation) {
                    final Annotation annotation = (Annotation)modifier;
                    final String qualifiedAnnotationName = annotation.resolveTypeBinding().getQualifiedName();
                    if ("java.lang.Override".equals(qualifiedAnnotationName) || "java.lang.SuppressWarnings".equals(qualifiedAnnotationName)) {
                        continue;
                    }
                    annotations.add(annotation);
                }
            }
            final StringBuilder signature = new StringBuilder();
            addAnnotations(annotations, signature);
            try {
                if (processor.getClass().getDeclaredField("fPublic").get(processor)) {
                    signature.append("public ");
                }
                if (processor.getClass().getDeclaredField("fAbstract").get(processor)) {
                    signature.append("abstract ");
                }
            }
            catch (Throwable t) {}
            signature.append(declaration.getReturnType2().toString()).append(" ").append(declaration.getName().getFullyQualifiedName()).append("(");
            boolean first = true;
            for (final Object parameter : declaration.parameters()) {
                if (!first) {
                    signature.append(", ");
                }
                first = false;
                signature.append(parameter);
            }
            signature.append(");");
            return signature.toString();
        }
        
        public static void addAnnotations(final List<Annotation> annotations, final StringBuilder signature) {
            for (final Annotation annotation : annotations) {
                final List<String> values = new ArrayList<String>();
                if (annotation.isSingleMemberAnnotation()) {
                    final SingleMemberAnnotation smAnn = (SingleMemberAnnotation)annotation;
                    values.add(smAnn.getValue().toString());
                }
                else if (annotation.isNormalAnnotation()) {
                    final NormalAnnotation normalAnn = (NormalAnnotation)annotation;
                    for (final Object value : normalAnn.values()) {
                        values.add(value.toString());
                    }
                }
                signature.append("@").append(annotation.getTypeName().getFullyQualifiedName());
                if (!values.isEmpty()) {
                    signature.append("(");
                    boolean first = true;
                    for (final String string : values) {
                        if (!first) {
                            signature.append(", ");
                        }
                        first = false;
                        signature.append('\"').append(string).append('\"');
                    }
                    signature.append(")");
                }
                signature.append(" ");
            }
        }
        
        public static MethodDeclaration getRealMethodDeclarationNode(final MethodDeclaration original, final IMethod sourceMethod, final CompilationUnit cuUnit) throws JavaModelException {
            if (!isGenerated((ASTNode)original)) {
                return original;
            }
            IType declaringType = sourceMethod.getDeclaringType();
            final Stack<IType> typeStack = new Stack<IType>();
            while (declaringType != null) {
                typeStack.push(declaringType);
                declaringType = declaringType.getDeclaringType();
            }
            final IType rootType = typeStack.pop();
            AbstractTypeDeclaration typeDeclaration;
            for (typeDeclaration = findTypeDeclaration(rootType, cuUnit.types()); !typeStack.isEmpty() && typeDeclaration != null; typeDeclaration = findTypeDeclaration(typeStack.pop(), typeDeclaration.bodyDeclarations())) {}
            final String targetMethodName = sourceMethod.getElementName();
            final List<String> targetMethodParameterTypes = new ArrayList<String>();
            String[] parameterTypes;
            for (int length = (parameterTypes = sourceMethod.getParameterTypes()).length, j = 0; j < length; ++j) {
                final String parameterType = parameterTypes[j];
                targetMethodParameterTypes.add(Signature.toString(parameterType));
            }
            if (typeStack.isEmpty() && typeDeclaration != null) {
                for (final Object declaration : typeDeclaration.bodyDeclarations()) {
                    if (declaration instanceof MethodDeclaration) {
                        final MethodDeclaration methodDeclaration = (MethodDeclaration)declaration;
                        if (!methodDeclaration.getName().toString().equals(targetMethodName)) {
                            continue;
                        }
                        if (methodDeclaration.parameters().size() != targetMethodParameterTypes.size()) {
                            continue;
                        }
                        if (!isGenerated((ASTNode)methodDeclaration)) {
                            continue;
                        }
                        boolean parameterTypesEquals = true;
                        for (int i = 0; i < methodDeclaration.parameters().size(); ++i) {
                            final SingleVariableDeclaration variableDeclaration = methodDeclaration.parameters().get(i);
                            if (!variableDeclaration.getType().toString().equals(targetMethodParameterTypes.get(i))) {
                                parameterTypesEquals = false;
                                break;
                            }
                        }
                        if (parameterTypesEquals) {
                            return methodDeclaration;
                        }
                        continue;
                    }
                }
            }
            return original;
        }
        
        public static AbstractTypeDeclaration findTypeDeclaration(final IType searchType, final List<?> nodes) {
            for (final Object object : nodes) {
                if (object instanceof AbstractTypeDeclaration) {
                    final AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration)object;
                    if (typeDeclaration.getName().toString().equals(searchType.getElementName())) {
                        return typeDeclaration;
                    }
                    continue;
                }
            }
            return null;
        }
        
        public static int getSourceEndFixed(final int sourceEnd, final org.eclipse.jdt.internal.compiler.ast.ASTNode node) throws Exception {
            if (sourceEnd == -1) {
                final org.eclipse.jdt.internal.compiler.ast.ASTNode object = (org.eclipse.jdt.internal.compiler.ast.ASTNode)node.getClass().getField("$generatedBy").get(node);
                if (object != null) {
                    return object.sourceEnd;
                }
            }
            return sourceEnd;
        }
        
        public static int fixRetrieveStartingCatchPosition(final int original, final int start) {
            return (original == -1) ? start : original;
        }
        
        public static int fixRetrieveIdentifierEndPosition(final int original, final int start, final int end) {
            if (original == -1) {
                return end;
            }
            if (original < start) {
                return end;
            }
            return original;
        }
        
        public static int fixRetrieveEllipsisStartPosition(final int original, final int end) {
            return (original == -1) ? end : original;
        }
        
        public static int fixRetrieveStartBlockPosition(final int original, final int start) {
            return (original == -1) ? start : original;
        }
        
        public static int fixRetrieveRightBraceOrSemiColonPosition(final int original, final int end) {
            return (original == -1) ? end : original;
        }
        
        public static int fixRetrieveRightBraceOrSemiColonPosition(final int retVal, final AbstractMethodDeclaration amd) {
            if (retVal != -1 || amd == null) {
                return retVal;
            }
            final boolean isGenerated = EcjAugments.ASTNode_generatedBy.get((Object)amd) != null;
            if (isGenerated) {
                return amd.declarationSourceEnd;
            }
            return -1;
        }
        
        public static int fixRetrieveRightBraceOrSemiColonPosition(final int retVal, final FieldDeclaration fd) {
            if (retVal != -1 || fd == null) {
                return retVal;
            }
            final boolean isGenerated = EcjAugments.ASTNode_generatedBy.get((Object)fd) != null;
            if (isGenerated) {
                return fd.declarationSourceEnd;
            }
            return -1;
        }
        
        public static int fixRetrieveProperRightBracketPosition(final int retVal, final Type type) {
            if (retVal != -1 || type == null) {
                return retVal;
            }
            if (isGenerated((ASTNode)type)) {
                return type.getStartPosition() + type.getLength() - 1;
            }
            return -1;
        }
        
        public static boolean checkBit24(final Object node) throws Exception {
            final int bits = (int)node.getClass().getField("bits").get(node);
            return (bits & 0x800000) != 0x0;
        }
        
        public static boolean skipRewritingGeneratedNodes(final ASTNode node) throws Exception {
            return (boolean)node.getClass().getField("$isGenerated").get(node);
        }
        
        public static void setIsGeneratedFlag(final ASTNode domNode, final org.eclipse.jdt.internal.compiler.ast.ASTNode internalNode) throws Exception {
            if (internalNode == null || domNode == null) {
                return;
            }
            final boolean isGenerated = EcjAugments.ASTNode_generatedBy.get((Object)internalNode) != null;
            if (isGenerated) {
                domNode.getClass().getField("$isGenerated").set(domNode, true);
            }
        }
        
        public static void setIsGeneratedFlagForName(final Name name, final Object internalNode) throws Exception {
            if (internalNode instanceof org.eclipse.jdt.internal.compiler.ast.ASTNode) {
                final boolean isGenerated = EcjAugments.ASTNode_generatedBy.get((Object)internalNode) != null;
                if (isGenerated) {
                    name.getClass().getField("$isGenerated").set(name, true);
                }
            }
        }
        
        public static RewriteEvent[] listRewriteHandleGeneratedMethods(final RewriteEvent parent) {
            final RewriteEvent[] children = parent.getChildren();
            final List<RewriteEvent> newChildren = new ArrayList<RewriteEvent>();
            final List<RewriteEvent> modifiedChildren = new ArrayList<RewriteEvent>();
            for (int i = 0; i < children.length; ++i) {
                final RewriteEvent child = children[i];
                final boolean isGenerated = isGenerated((ASTNode)child.getOriginalValue());
                if (isGenerated) {
                    final boolean isReplacedOrRemoved = child.getChangeKind() == 4 || child.getChangeKind() == 2;
                    final boolean convertingFromMethod = child.getOriginalValue() instanceof MethodDeclaration;
                    if (isReplacedOrRemoved && convertingFromMethod && child.getNewValue() != null) {
                        modifiedChildren.add((RewriteEvent)new NodeRewriteEvent((Object)null, child.getNewValue()));
                    }
                }
                else {
                    newChildren.add(child);
                }
            }
            newChildren.addAll(modifiedChildren);
            return newChildren.toArray(new RewriteEvent[0]);
        }
        
        public static int getTokenEndOffsetFixed(final TokenScanner scanner, final int token, final int startOffset, final Object domNode) throws CoreException {
            boolean isGenerated = false;
            try {
                isGenerated = (boolean)domNode.getClass().getField("$isGenerated").get(domNode);
            }
            catch (Exception ex) {}
            if (isGenerated) {
                return -1;
            }
            return scanner.getTokenEndOffset(token, startOffset);
        }
        
        public static IMethod[] removeGeneratedMethods(final IMethod[] methods) throws Exception {
            final List<IMethod> result = new ArrayList<IMethod>();
            for (final IMethod m : methods) {
                if (!isGenerated((IMember)m)) {
                    result.add(m);
                }
            }
            return (result.size() == methods.length) ? methods : result.toArray(new IMethod[0]);
        }
        
        public static SearchMatch[] removeGenerated(final SearchMatch[] returnValue) {
            final List<SearchMatch> result = new ArrayList<SearchMatch>();
            for (int j = 0; j < returnValue.length; ++j) {
                final SearchMatch searchResult = returnValue[j];
                if (searchResult.getElement() instanceof IField) {
                    final IField field = (IField)searchResult.getElement();
                    final IAnnotation annotation = field.getAnnotation("Generated");
                    if (annotation != null) {
                        continue;
                    }
                }
                result.add(searchResult);
            }
            return result.toArray(new SearchMatch[0]);
        }
        
        public static SearchResultGroup[] createFakeSearchResult(final SearchResultGroup[] returnValue, final Object processor) throws Exception {
            if (returnValue == null || returnValue.length == 0) {
                final Field declaredField = processor.getClass().getDeclaredField("fField");
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    final SourceField fField = (SourceField)declaredField.get(processor);
                    final IAnnotation dataAnnotation = fField.getDeclaringType().getAnnotation("Data");
                    if (dataAnnotation != null) {
                        return new SearchResultGroup[] { new SearchResultGroup((IResource)null, new SearchMatch[1]) };
                    }
                }
            }
            return returnValue;
        }
        
        public static SimpleName[] removeGeneratedSimpleNames(final SimpleName[] in) throws Exception {
            final Field f = SimpleName.class.getField("$isGenerated");
            int count = 0;
            for (int i = 0; i < in.length; ++i) {
                if (in[i] == null || !(boolean)f.get(in[i])) {
                    ++count;
                }
            }
            if (count == in.length) {
                return in;
            }
            final SimpleName[] newSimpleNames = new SimpleName[count];
            count = 0;
            for (int j = 0; j < in.length; ++j) {
                if (in[j] == null || !(boolean)f.get(in[j])) {
                    newSimpleNames[count++] = in[j];
                }
            }
            return newSimpleNames;
        }
        
        public static Name[] removeGeneratedNames(final Name[] in) throws Exception {
            final Field f = Name.class.getField("$isGenerated");
            int count = 0;
            for (int i = 0; i < in.length; ++i) {
                if (in[i] == null || !(boolean)f.get(in[i])) {
                    ++count;
                }
            }
            if (count == in.length) {
                return in;
            }
            final Name[] newNames = new Name[count];
            count = 0;
            for (int j = 0; j < in.length; ++j) {
                if (in[j] == null || !(boolean)f.get(in[j])) {
                    newNames[count++] = in[j];
                }
            }
            return newNames;
        }
        
        public static org.eclipse.jdt.internal.compiler.ast.Annotation[] convertAnnotations(final org.eclipse.jdt.internal.compiler.ast.Annotation[] out, final IAnnotatable annotatable) {
            IAnnotation[] in;
            try {
                in = annotatable.getAnnotations();
            }
            catch (Exception ex) {
                return out;
            }
            if (out == null) {
                return null;
            }
            int toWrite = 0;
            for (int idx = 0; idx < out.length; ++idx) {
                final String oName = new String(out[idx].type.getLastToken());
                boolean found = false;
                IAnnotation[] array;
                for (int length = (array = in).length, k = 0; k < length; ++k) {
                    final IAnnotation i = array[k];
                    String name = i.getElementName();
                    final int li = name.lastIndexOf(46);
                    if (li > -1) {
                        name = name.substring(li + 1);
                    }
                    if (name.equals(oName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    out[idx] = null;
                }
                else {
                    ++toWrite;
                }
            }
            org.eclipse.jdt.internal.compiler.ast.Annotation[] replace = out;
            if (toWrite < out.length) {
                replace = new org.eclipse.jdt.internal.compiler.ast.Annotation[toWrite];
                int idx2 = 0;
                for (int j = 0; j < out.length; ++j) {
                    if (out[j] != null) {
                        replace[idx2++] = out[j];
                    }
                }
            }
            return replace;
        }
        
        public static String getRealNodeSource(final String original, final org.eclipse.jdt.internal.compiler.ast.ASTNode node) {
            if (!isGenerated(node)) {
                return original;
            }
            return node.toString();
        }
        
        public static String getRealNodeSource(final String original, final ASTNode node) throws Exception {
            if (!isGenerated(node)) {
                return original;
            }
            return node.toString();
        }
        
        public static boolean skipRewriteVisibility(final MemberVisibilityAdjustor.IncomingMemberVisibilityAdjustment adjustment) {
            return isGenerated(adjustment.getMember());
        }
    }
}
