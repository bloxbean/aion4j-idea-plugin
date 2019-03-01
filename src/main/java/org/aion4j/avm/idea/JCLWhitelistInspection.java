package org.aion4j.avm.idea;

import com.intellij.codeInspection.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import org.aion4j.avm.idea.service.AvmService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JCLWhitelistInspection extends AbstractBaseJavaLocalInspectionTool implements CustomSuppressableInspectionTool {

    private final static Logger log = Logger.getInstance(JCLWhitelistInspection.class);

    private final static String AION4j_MAVEN_PLUGIN = "aion4j-maven-plugin";

    private final static String USERLIB_PACKAGE_PREFIX = "org.aion.avm.userlib";
    private final static String AVM_API_PACKAGE_PREFIX = "org.aion.avm.api";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        PsiFile psiFile = holder.getFile();

        System.out.println(">>>>>>>>>>>>>>>> PsiFile>>>>>>>>>>>" + psiFile.getName());

        if(psiFile.getName().endsWith("Test.java")) { //If test, ignore //TODO
            return null;
        }

        return new JavaElementVisitor() {

            boolean avmProject = false;
            Project project;

            @Override
            public void visitField(PsiField field) {

                if(project == null) {
                    project = field.getProject();
                }

                AvmService service = ServiceManager.getService(project, AvmService.class);

                if(!service.isAvmProject())
                    return;

                String fqName = field.getType().getCanonicalText();

                if(log.isDebugEnabled())
                    log.debug("FQNAME : " + fqName);

                if(!isCheckedType(field.getType()))  { //TODO primitive type check. Do properly
                    return;
                }

                if(fqName.startsWith(USERLIB_PACKAGE_PREFIX) || fqName.startsWith(AVM_API_PACKAGE_PREFIX))
                    return;

                if(!service.isClassAllowed(fqName)) {
                    holder.registerProblem(field.getOriginalElement(),
                            String.format("%s is not allowed in a Avm smart contract project", fqName), ProblemHighlightType.GENERIC_ERROR);
                }

            }

            @Override
            public void visitMethod(PsiMethod method) {

            }

            @Override
            public void visitClass(PsiClass aClass) {
                project = aClass.getProject();
            }

            @Override
            public void visitTypeElement(PsiTypeElement type) {

            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
//                System.out.println("M:: " + expression.getMethodExpression().getQualifierExpression().toString());
//                System.out.println("0: " + expression.getMethodExpression().getCanonicalText());
//
//                PsiType[] arguments = expression.getArgumentList().getExpressionTypes();
//
//                if(arguments !=null && arguments.length > 0) {
//                    for(PsiType psiType: arguments) {
//                        System.out.println("Arg: " + psiType.getCanonicalText());
//                    }
//                }

               // JvmParameter[] parameters = ((PsiMethodCallExpressionImpl) expression).resolveMethod().getParameters();

               String className = null;

                try {

                    ClsClassImpl psiClassElm = ((ClsClassImpl) ((PsiMethodCallExpressionImpl) expression).resolveMethod().getParent());
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

                try {
                    if(project == null) {
                        project = expression.getProject();
                    }

                    AvmService service = ServiceManager.getService(project, AvmService.class);

                    if(!service.isClassAllowed(className)) {
                        holder.registerProblem(expression.getOriginalElement(),
                                String.format("%s is not allowed in a Avm smart contract project", className), ProblemHighlightType.GENERIC_ERROR);
                    }

//                    if(project != null) {
//                        PsiType psiType = PsiType.getTypeByName(className, project, GlobalSearchScope.everythingScope(project));
//                        System.out.println("*******"  + psiType);
//
//                    }
//
//                    Class clazz = Class.forName(className);
//                    AvmDetails.MethodDescriptor[] methodDescriptors = AvmDetails.getClassLibraryWhiteList().get(clazz);
//
//                    if(methodDescriptors == null) {
//                        holder.registerProblem(expression.getOriginalElement(),
//                                String.format("%s is not allowed in a Avm smart contract project", className), ProblemHighlightType.GENERIC_ERROR);
//                    } else {
//
//                    }

//                    PsiType objectType = PsiType.getTypeByName("java.lang.Object", expression.resolveMethod().getProject(), GlobalSearchScope.everythingScope(expression.resolveMethod().getProject()));
//
//                    PsiType stringType = PsiType.getTypeByName("java.lang.String", expression.resolveMethod().getProject(), GlobalSearchScope.everythingScope(expression.resolveMethod().getProject()));
//
//                    objectType.isAssignableFrom(stringType);

                } catch (Exception e) {
                    //e.printStackTrace();
                    if(log.isDebugEnabled()) {
                        log.debug(e);
                    }
                }
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                if(project == null) {
                    project = variable.getProject();
                }

                AvmService service = ServiceManager.getService(project, AvmService.class);

                if(!service.isAvmProject())
                    return;

                String fqName = variable.getType().getCanonicalText();

                if(log.isDebugEnabled())
                    log.debug("FQNAME : " + fqName);

                if(!isCheckedType(variable.getType())) { //TODO primitive type check. Do properly
                    return;
                }

                if(fqName.startsWith(USERLIB_PACKAGE_PREFIX) || fqName.startsWith(AVM_API_PACKAGE_PREFIX))
                    return;

                if(!service.isClassAllowed(fqName)) {
                    holder.registerProblem(variable.getOriginalElement(),
                            String.format("%s is not allowed in a Avm smart contract project", fqName), ProblemHighlightType.GENERIC_ERROR);
                }
            }
        };
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
