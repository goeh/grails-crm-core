/*
 * Copyright 2012 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugins.crm.core;

import groovy.lang.ExpandoMetaClass;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.codehaus.groovy.transform.AbstractASTTransformUtil.*;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class UuidASTTransformation implements ASTTransformation {

    //private static final Log LOG = LogFactory.getLog(UuidASTTransformation.class);

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        //System.out.println("Modifying source unit " + sourceUnit.getName());

        ExpandoMetaClass.disableGlobally();

        for (ASTNode astNode : nodes) {
            if (astNode instanceof ClassNode) {
                ClassNode theClass = (ClassNode) astNode;

                if (!GrailsASTUtils.hasOrInheritsProperty(theClass, "guid")) {
                    System.out.println("Adding guid field to class " + theClass.getName());
                    theClass.addProperty("guid", Modifier.PUBLIC, ClassHelper.STRING_TYPE, createUUIDMethodCall(), null, null);

                    Statement guidConstraintExpression = createStringConstraint("guid", 36, false);

                    PropertyNode constraints = theClass.getProperty("constraints");
                    if (constraints != null) {
                        if (constraints.getInitialExpression() instanceof ClosureExpression) {
                            ClosureExpression ce = (ClosureExpression) constraints.getInitialExpression();
                            ((BlockStatement) ce.getCode()).addStatement(guidConstraintExpression);
                        } else {
                            System.err.println("Do not know how to add constraints expression to non ClosureExpression " + constraints.getInitialExpression());
                        }
                    } else {
                        Statement[] constraintsStatement = {guidConstraintExpression};
                        BlockStatement closureBlock = new BlockStatement(constraintsStatement, null);
                        ClosureExpression constraintsClosure = new ClosureExpression(null, closureBlock);
                        theClass.addProperty("constraints", Modifier.STATIC | Modifier.PUBLIC, ClassHelper.OBJECT_TYPE, constraintsClosure, null, null);

                    }

                    createHashCode(theClass);
                    createEquals(theClass);
                }

                VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(sourceUnit);
                scopeVisitor.visitClass(theClass);
            }
        }

        ExpandoMetaClass.enableGlobally();
    }

    // This method returns an expression that is used to initialize the newly created property
    private Expression createUUIDMethodCall() {
        Expression randomUUIDExpr = new MethodCallExpression(new ClassExpression(new ClassNode(UUID.class)), "randomUUID", ArgumentListExpression.EMPTY_ARGUMENTS);
        return new MethodCallExpression(randomUUIDExpr, "toString", ArgumentListExpression.EMPTY_ARGUMENTS);
    }

    private Statement createStringConstraint(String propertyName, int maxSize, boolean blank) {
        NamedArgumentListExpression nale = new NamedArgumentListExpression();
        nale.addMapEntryExpression(new MapEntryExpression(new ConstantExpression("maxSize"), new ConstantExpression(36)));
        nale.addMapEntryExpression(new MapEntryExpression(new ConstantExpression("blank"), ConstantExpression.FALSE));

        MethodCallExpression mce = new MethodCallExpression(VariableExpression.THIS_EXPRESSION, propertyName, nale);
        return new ExpressionStatement(mce);
    }

    private void createHashCode(ClassNode cNode) {
        // make a public method if none exists otherwise try a private method with leading underscore
        boolean hasExistingHashCode = hasDeclaredMethod(cNode, "hashCode", 0);
        if (hasExistingHashCode && hasDeclaredMethod(cNode, "_hashCode", 0)) return;

        System.out.println("Adding hashCode() to class " + cNode.getName());

        final BlockStatement body = new BlockStatement();

        body.addStatement(guidHashStatements(cNode));

        cNode.addMethod(new MethodNode(hasExistingHashCode ? "_hashCode" : "hashCode", hasExistingHashCode ? ACC_PRIVATE : ACC_PUBLIC,
                ClassHelper.int_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body));
    }

    private Statement guidHashStatements(ClassNode cNode) {
        //Expression getter = new MethodCallExpression(VariableExpression.THIS_EXPRESSION, "getGuid", MethodCallExpression.NO_ARGUMENTS);
        Expression getter = new PropertyExpression(VariableExpression.THIS_EXPRESSION, "guid");
        return new ExpressionStatement(new MethodCallExpression(getter, "hashCode", ArgumentListExpression.EMPTY_ARGUMENTS));
    }

    public static void createEquals(ClassNode cNode) {
        // make a public method if none exists otherwise try a private method with leading underscore
        boolean hasExistingEquals = hasDeclaredMethod(cNode, "equals", 1);
        if (hasExistingEquals && hasDeclaredMethod(cNode, "_equals", 1)) return;

        System.out.println("Adding equals(other) to class " + cNode.getName());

        final BlockStatement body = new BlockStatement();
        VariableExpression other = new VariableExpression("other");

        // some short circuit cases for efficiency
        body.addStatement(returnFalseIfNull(other));
        body.addStatement(returnTrueIfIdentical(VariableExpression.THIS_EXPRESSION, other));

        body.addStatement(returnFalseIfWrongType(cNode, other));

        body.addStatement(returnFalseIfPropertyNotEqual(cNode.getProperty("guid"), other));

        body.addStatement(new IfStatement(
                isTrueExpr(new MethodCallExpression(VariableExpression.SUPER_EXPRESSION, "equals", other)),
                new EmptyStatement(),
                new ReturnStatement(ConstantExpression.FALSE)
        ));

        // default
        body.addStatement(new ReturnStatement(ConstantExpression.TRUE));

        Parameter[] params = {new Parameter(ClassHelper.OBJECT_TYPE, other.getName())};
        cNode.addMethod(new MethodNode(hasExistingEquals ? "_equals" : "equals", hasExistingEquals ? ACC_PRIVATE : ACC_PUBLIC,
                ClassHelper.boolean_TYPE, params, ClassNode.EMPTY_ARRAY, body));
    }
}
