package org.aion4j.avm.idea.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiNewExpressionImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.aion4j.avm.idea.inspection.types.AvmTypes;
import org.aion4j.avm.idea.service.AvmService;
import org.aion4j.avm.idea.service.MethodDescriptor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JCLWhitelistInspection extends AbstractBaseJavaLocalInspectionTool implements CustomSuppressableInspectionTool {

    private final static Logger log = Logger.getInstance(JCLWhitelistInspection.class);

    private final static String AION4j_MAVEN_PLUGIN = "aion4j-maven-plugin";

    private final static String USERLIB_PACKAGE_PREFIX = "org.aion.avm.userlib";
    private final static String AVM_API_PACKAGE_PREFIX = "avm";
    private final static String CALLABLE_ANNOTATION = "org.aion.avm.tooling.abi.Callable";
    private final static String FALLBACK_ANNOTATION = "org.aion.avm.tooling.abi.Fallback";
    private final static String INITIALIZABLE_ANNOTATION = "org.aion.avm.tooling.abi.Initializable";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        PsiFile psiFile = holder.getFile();

        AvmService service = ServiceManager.getService(psiFile.getProject(), AvmService.class);

        if(service != null) {
            if(!service.isInitialize()) {
                service.init(psiFile.getProject());
            }
            if(service.isUnderTestSource(psiFile.getVirtualFile())) {
                return DummyJavaVisitor.CONSTANT;
            }
        } else
            return DummyJavaVisitor.CONSTANT;

        return new JavaElementVisitor() {

            Project project;

            @Override
            public void visitField(PsiField field) {

                //check if inspection is applicable
                if(project == null) {
                    project = field.getProject();
                }

                if(project == null)
                    return;
                AvmService service = ServiceManager.getService(project, AvmService.class);
                if(service == null || !service.isAvmProject())
                    return;
                //end enable inspection check

                String fqName = field.getType().getCanonicalText();

                if(log.isDebugEnabled())
                    log.debug("FQNAME : " + fqName);

                //Abi type check for @Initializable field
                performAbiTypeCheckOnInitializableField(field);

                if(!isCheckedType(field.getType()))  { //TODO primitive type check. Do properly
                    return;
                }

                if(fqName.startsWith(USERLIB_PACKAGE_PREFIX) || fqName.startsWith(AVM_API_PACKAGE_PREFIX))
                    return;

                if(fqName.contains("<") && fqName.contains(">")) { //Seems like generic class
                    String name = getClassNameFromGenericType(field);

                    if(name != null && !name.isEmpty())
                        fqName = name;
                }

                if(!service.isClassAllowed(project, fqName)) {
                    if(log.isDebugEnabled())
                        log.debug("Not allowed class >>>> " + fqName);

                    holder.registerProblem(field.getOriginalElement(),
                            String.format("%s is not allowed in a Avm smart contract project", fqName), ProblemHighlightType.GENERIC_ERROR);
                }

            }

            private void performAbiTypeCheckOnInitializableField(PsiField field) {
                //Check Abi type check for field with Initializable annotation
                PsiAnnotation initializableAnnotation = field.getAnnotation(INITIALIZABLE_ANNOTATION);

                if (initializableAnnotation != null)  {

                    try {
                        String type = field.getType().getCanonicalText();

                        if (!AvmTypes.isAllowedType(type)) {
                            holder.registerProblem(field.getTypeElement().getOriginalElement(),
                                    String.format("%s is not an allowed Field type with @Initializable annotation in AVM smart contract", type), ProblemHighlightType.GENERIC_ERROR);
                        }

                        if (!field.getModifierList().hasModifierProperty("static")) {
                            holder.registerProblem(field.getModifierList().getOriginalElement(),
                                    "A @Initializable field should be static", ProblemHighlightType.GENERIC_ERROR);
                        }
                    } catch (Exception e) {
                        if(log.isDebugEnabled())
                            log.debug("Error checking abiTypecheck for @Initializable field", e);
                    }
                }
                //Abi check ends for @Initializable field
            }

            @Override
            public void visitClass(PsiClass aClass) {
                project = aClass.getProject();
            }


            @Override
            public void visitClassInitializer(PsiClassInitializer initializer) {

                //System.out.println("visit in class initializer level >>>>>> " + initializer.getName());
//                if(log.isDebugEnabled()) {
//                    System.out.println("visit in class initializer level >>>>>> " + initializer.getName());
//                }
            }

            @Override
            public void visitNewExpression(PsiNewExpression expression) {

                //check if inspection is applicable
                if(project == null) {
                    project = expression.getProject();
                }

                if(project == null)
                    return;
                AvmService service = ServiceManager.getService(project, AvmService.class);
                if(service == null || !service.isAvmProject())
                    return;
                //end enable inspection check;

                String className = null;
                PsiMethod psiMethod = null;

                try {

                    psiMethod = ((PsiNewExpressionImpl) expression).resolveConstructor();

//                    if(log.isDebugEnabled()) {
//                        log.debug("New object >>>>>>>>" + psiMethod.getName());
//                        log.debug("Parameters >>> " + psiMethod.getParameterList().getParameters());
//                    }

                    ClsClassImpl psiClassElm = ((ClsClassImpl) psiMethod.getParent());

                    if (psiClassElm != null) {
                        className = psiClassElm.getQualifiedName();

                        if(log.isDebugEnabled())
                            log.debug("Class: " + className);
                    }

                    if(className.startsWith(USERLIB_PACKAGE_PREFIX) || className.startsWith(AVM_API_PACKAGE_PREFIX))
                        return;
                } catch (Exception e) {
                    if(log.isDebugEnabled()) {
                        log.debug(e);
                    }
                    return;
                }

                verifyIfMethodAllowed(expression, service, className, psiMethod);
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {

                //check if inspection is applicable
                if(project == null) {
                    project = expression.getProject();
                }

                if(project == null)
                    return;
                AvmService service = ServiceManager.getService(project, AvmService.class);
                if(service == null || !service.isAvmProject())
                    return;
                //end enable inspection check;

               String className = null;
               PsiMethod psiMethod = null;

                try {

                    psiMethod = ((PsiMethodCallExpressionImpl) expression).resolveMethod();
                    ClsClassImpl psiClassElm = ((ClsClassImpl) psiMethod.getParent());

                    if (psiClassElm != null) {
                        className = psiClassElm.getQualifiedName();

                        if(log.isDebugEnabled())
                            log.debug("Class: " + className);
                    }

                    if(className.startsWith(USERLIB_PACKAGE_PREFIX) || className.startsWith(AVM_API_PACKAGE_PREFIX))
                        return;
                } catch (Exception e) {
                    if(log.isDebugEnabled()) {
                        log.debug(e);
                    }
                    return;
                }

                verifyIfMethodAllowed(expression, service, className, psiMethod);
            }

            private void verifyIfMethodAllowed(PsiElement expression, AvmService service, String className, PsiMethod psiMethod) {
                try {

                    if(!service.isClassAllowed(project, className)) {
                        holder.registerProblem(expression.getOriginalElement(),
                                String.format("%s is not allowed in a Avm smart contract project", className), ProblemHighlightType.GENERIC_ERROR);
                    }

                    String methodName = psiMethod.isConstructor()? "<init>" : psiMethod.getName();
                    List<MethodDescriptor> methodDescriptors = service.getAllowedMethodsForClass(project, className, methodName);

                    boolean isAllowed = false;
                    for(MethodDescriptor methodDescriptor: methodDescriptors) {

                       PsiParameter[] jvmParameters = psiMethod.getParameterList().getParameters();//getParameters();//getParameters();

                       if(methodDescriptor.getParams().size() == 0 && jvmParameters.length == 0)  {
                           isAllowed = true;
                           break;
                       }

                       if(methodDescriptor.getParams().size() != jvmParameters.length) {
                           continue;
                       } else {

                           if(log.isDebugEnabled()) {
                               log.debug("Matching param size >>> " + jvmParameters.length);
                           }
                       }

                        for(int i = 0; i < methodDescriptor.getParams().size(); i++) {

                            String param = methodDescriptor.getParams().get(i);

                            if(log.isDebugEnabled()) {
                                log.debug("Actual params:  " + param);
                                log.debug("Method params " + jvmParameters[i].getType().getCanonicalText());
                            }



                            if(param.equals(jvmParameters[i].getType().getCanonicalText())) {
                                isAllowed = true;
                            } else {

                                String fqName = jvmParameters[i].getType().getCanonicalText();
                                if(fqName.contains(">") && fqName.contains("<")) { //generic type
                                    fqName = getClassNameFromGenericType(jvmParameters[i]);

                                    if(fqName != null && param.equals(fqName)) {
                                        if(log.isDebugEnabled()) {
                                            log.debug("Match for fqName with generic : " + fqName); //exp java.util.Collection<?>
                                        }
                                        isAllowed = true;
                                        continue;
                                    }
                                }

                                //Check if it's a generic type
                                if(jvmParameters[i].getType().getCanonicalText().length() == 1) {
                                    String superClass = PsiUtil.resolveGenericsClassInType(jvmParameters[i].getType()).getElement().getSuperClass().getQualifiedName();

                                    if(param.equals(superClass)) {

                                        if(log.isDebugEnabled())
                                            log.debug("Super class for Generic >>> " + superClass);

                                        isAllowed = true;
                                    } else {
                                        if(jvmParameters[i].getType().isAssignableFrom(PsiType.getTypeByName(param, project, GlobalSearchScope.allScope(project)))) {

                                            if(log.isDebugEnabled())
                                                log.debug("Can be accessible >>>" + jvmParameters[i].getType());

                                            isAllowed = true;
                                        } else {
                                            isAllowed = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if(isAllowed)
                            break;
                    }

                    if(!isAllowed) {
                        holder.registerProblem(expression.getOriginalElement(),
                                String.format("%s.%s is not allowed in a Avm smart contract project", className, psiMethod.getName()), ProblemHighlightType.GENERIC_ERROR);
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                    if(log.isDebugEnabled()) {
                        log.debug(e);
                    }
                }
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {

                //check if inspection is applicable
                if(project == null) {
                    project = variable.getProject();
                }

                if(project == null)
                    return;
                AvmService service = ServiceManager.getService(project, AvmService.class);
                if(service == null || !service.isAvmProject())
                    return;
                //end enable inspection check

                String fqName = variable.getType().getCanonicalText();

                if(log.isDebugEnabled())
                    log.debug("FQNAME : " + fqName);

                if(!isCheckedType(variable.getType())) { //TODO primitive type check. Do properly
                    return;
                }

                if(fqName.startsWith(USERLIB_PACKAGE_PREFIX) || fqName.startsWith(AVM_API_PACKAGE_PREFIX))
                    return;

                if(fqName.contains("<") && fqName.contains(">")) { //Seems like generic class
                    String name = getClassNameFromGenericType(variable);

                    if(name != null && !name.isEmpty())
                        fqName = name;
                }

                if(!service.isClassAllowed(project, fqName)) {
                    if(log.isDebugEnabled())
                        log.debug("Not allowed class >>>> " + fqName);

                    holder.registerProblem(variable.getOriginalElement(),
                            String.format("%s is not allowed in a Avm smart contract project", fqName), ProblemHighlightType.GENERIC_ERROR);
                }
            }

            @Override
            public void visitMethod(PsiMethod method) {
                try {
                    PsiAnnotation callableAnnotation = method.getAnnotation(CALLABLE_ANNOTATION);

                    if (callableAnnotation != null)  {
                        //check if public
                        if(!method.getModifierList().hasModifierProperty("public")) {
                            holder.registerProblem(method.getModifierList().getOriginalElement(),
                                    "A @Callable method should be public", ProblemHighlightType.GENERIC_ERROR);
                        }

                        if(!method.getModifierList().hasModifierProperty("static")) {
                            holder.registerProblem(method.getModifierList().getOriginalElement(),
                                    "A @Callable method should be static", ProblemHighlightType.GENERIC_ERROR);
                        }

                        PsiParameter[] jvmParameters = method.getParameterList().getParameters();
                        for (PsiParameter param : jvmParameters) {
                            String type = param.getType().getCanonicalText();

                            if (!AvmTypes.isAllowedType(type)) {
                                holder.registerProblem(param.getOriginalElement(),
                                        String.format("%s is not an allowed parameter type in AVM smart contract method", type), ProblemHighlightType.GENERIC_ERROR);
                            }
                        }

                        //Return type
                        String returnType = method.getReturnType().getCanonicalText();
                        if (!"void".equals(returnType) && !AvmTypes.isAllowedType(returnType)) {
                            holder.registerProblem(method.getReturnTypeElement(),
                                    String.format("%s is not an allowed return type in AVM smart contract method", returnType), ProblemHighlightType.GENERIC_ERROR);
                        }
                    }

                    PsiAnnotation fallbackAnnotation = method.getAnnotation(FALLBACK_ANNOTATION);
                    if(fallbackAnnotation != null) {
                        if(!method.getModifierList().hasModifierProperty("static")) {
                            holder.registerProblem(method.getModifierList().getOriginalElement(),
                                    "@Fallback method should be static", ProblemHighlightType.GENERIC_ERROR);
                        }

                        PsiParameter[] jvmParameters = method.getParameterList().getParameters();
                        if(jvmParameters != null && jvmParameters.length > 0) {
                            holder.registerProblem(method.getParameterList().getOriginalElement(),
                                    "@Fallback method cannot take arguments", ProblemHighlightType.GENERIC_ERROR);
                        }

                        //Return type
                        String returnType = method.getReturnType().getCanonicalText();
                        if (!"void".equals(returnType)) {
                            holder.registerProblem(method.getReturnTypeElement(),
                                    "@Fallback method return type should be void", ProblemHighlightType.GENERIC_ERROR);
                        }
                    }

                } catch (Exception e) {
                    if(log.isDebugEnabled())
                        log.debug(e);
                }
            }
        };
    }

    private String getClassNameFromGenericType(PsiVariable element) {
        try {
            return PsiUtil.resolveGenericsClassInType(element.getType()).getElement().getQualifiedName();
        } catch (Exception e) {
            return element.getType().getCanonicalText();
        }
    }

    private boolean isCheckedType(PsiType type) {
        if (!(type instanceof PsiClassType)) return false;
        else
            return true;
    }

    @Nullable
    public ProblemDescriptor[] checkField(@NotNull PsiField field, @NotNull InspectionManager manager, boolean isOnTheFly) {
        return null;
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Aion Avm";
    }

    /*
    @NotNull
    @Override
    public String getShortName() {
        return this.getClass().getName();
    }*/

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Avm JCL Whitelist check";
    }

    @Nullable
    @Override
    public SuppressIntentionAction[] getSuppressActions(@Nullable PsiElement element) {
        return new SuppressIntentionAction[0];
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

}
