package org.aion4j.avm.idea.inspection;

import com.intellij.psi.JavaElementVisitor;

public class DummyJavaVisitor extends JavaElementVisitor {

    private DummyJavaVisitor dummyJavaVisitor;

    public static DummyJavaVisitor CONSTANT = new DummyJavaVisitor();
}
