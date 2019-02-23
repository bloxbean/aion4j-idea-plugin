package org.aion4j.avm.idea;

import com.intellij.codeInspection.*;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JCLWhitelistInspection extends AbstractBaseJavaLocalInspectionTool implements CustomSuppressableInspectionTool {

    private final static String AION4j_MAVEN_PLUGIN = "aion4j-maven-plugin";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
//        Map<PsiClass, Set<PsiField>> fieldsOfClass = new HashMap<>();
//        boolean avmProject = false;
        return new JavaElementVisitor() {

            boolean avmProject = false;
            Project project;

            @Override
            public void visitField(PsiField field) {

//                String fqName = field.getType().getCanonicalText();
//
//                if(fqName.startsWith("org.aion.avm.userlib")) {
//                    holder.registerProblem(field.getOriginalElement(),
//                            "Not allowed in a Avm smart contract project", ProblemHighlightType.GENERIC_ERROR);
//                }
                /*if (isStatic(field)) {
                    return;
                }

                System.out.println("Field:: " + field.getName() +"  " + field.getType().getCanonicalText());
                if (field.getContainingClass().isInterface()) {
                    return;
                }

                Set<PsiField> fields = fieldsOfClass.computeIfAbsent(
                        field.getContainingClass(),
                        x -> new HashSet<>());
                fields.add(field);

                if (fields.size() > MAX_FIELD_COUNT) {
//                    holder.re
                    holder.registerProblem(field.getOriginalElement(), "Too many instance fields in one class.", ProblemHighlightType.ERROR);
                }*/


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

                /*if(!this.avmProject) {
                   // System.out.println("Not an avm project. ignore");
                    return;
                }

                String fqName = type.getType().getCanonicalText();

                if(fqName.startsWith("org.aion.avm.userlib")) {
                    holder.registerProblem(type.getOriginalElement(),
                            String.format("%s is not allowed in a Avm smart contract project", fqName), ProblemHighlightType.GENERIC_ERROR);
                }*/
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
                        System.out.println("Class: " + className);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //Class clazz = Class.forName(className);
                   // AvmDetails.MethodDescriptor[] methodDescriptors = AvmDetails.getClassLibraryWhiteList().get(clazz);

                    if(project == null) {
                        project = expression.getProject();
                    }

                    if(project != null) {
                        PsiType psiType = PsiType.getTypeByName(className, project, GlobalSearchScope.everythingScope(project));
                        System.out.println("*******"  + psiType);

                    }

                    Class clazz = Class.forName(className);
                    AvmDetails.MethodDescriptor[] methodDescriptors = AvmDetails.getClassLibraryWhiteList().get(clazz);

                    if(methodDescriptors == null) {
                        holder.registerProblem(expression.getOriginalElement(),
                                String.format("%s is not allowed in a Avm smart contract project", className), ProblemHighlightType.GENERIC_ERROR);
                    } else {

                    }

//                    PsiType objectType = PsiType.getTypeByName("java.lang.Object", expression.resolveMethod().getProject(), GlobalSearchScope.everythingScope(expression.resolveMethod().getProject()));
//
//                    PsiType stringType = PsiType.getTypeByName("java.lang.String", expression.resolveMethod().getProject(), GlobalSearchScope.everythingScope(expression.resolveMethod().getProject()));
//
//                    objectType.isAssignableFrom(stringType);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


               /* System.out.println("Method: " + ((PsiMethodCallExpressionImpl) expression).resolveMethod().getName());
                if(parameters != null && parameters.length > 0) {
                    for(JvmParameter parameter: parameters) {
                        System.out.println("param: " + ((ClsParameterImpl)parameter).getType().getCanonicalText());
                    }
                }*/

              //  ((ClsParameterImpl)((PsiMethodCallExpressionImpl) expression).resolveMethod().getParameters()[0]).getType().getCanonicalText()


                //String className = ((ClsClassImpl)((PsiMethodCallExpressionImpl) expression).resolveMethod().getParent()).getQualifiedName();
//                System.out.println("Clazz name: " + className);



            /*
                System.out.println("1" +  expression.getMethodExpression().getReference().getCanonicalText());
               // System.out.println("2" + expression.getMethodExpression().getType().getCanonicalText());

                System.out.println("3" + expression.getMethodExpression().getReferenceNameElement().getText());
                System.out.println("4" + expression.getMethodExpression().getQualifier().getText());
                System.out.println("5" + expression.getMethodExpression().getQualifiedName());
                System.out.println("6" + expression.getMethodExpression().getParent().getText());*/
               // System.out.println("7" + expression.getMethodExpression().getQualifier().getReference().getCanonicalText());
               // System.out.printf("8" + expression.getType().getCanonicalText());
               // System.out.println("9" + expression.getMethodExpression().getParent().getReference().getCanonicalText());
//
            }

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {

            }

            @Override
            public void visitJavaFile(PsiJavaFile file) {

                project = file.getProject();

                VirtualFile ideaFolder = file.getProject().getProjectFile().getParent();
                if(ideaFolder == null)
                    return;

                VirtualFile rootFolder = ideaFolder.getParent();
                if(rootFolder == null)
                    return;

                VirtualFile pomXml = rootFolder.findFileByRelativePath("pom.xml");

                try {
                    if (pomXml.exists()) {
                        String content = new String(pomXml.contentsToByteArray(), "UTF-8");

                        if(content.contains(AION4j_MAVEN_PLUGIN)) {
//                            System.out.println("Setting as AVM project.........." + this.toString());
                            this.avmProject = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    /*
    private boolean isStatic(PsiField field) {
        return field.getModifierList() != null && field.getModifierList().hasExplicitModifier("static");
    }*/

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
