/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package grails.plugins.crm.core;

import groovy.lang.ExpandoMetaClass;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.lang.reflect.Modifier;
import java.util.Date;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class AuditASTTransformation implements ASTTransformation {
    //private static final Log LOG = LogFactory.getLog(AuditASTTransformation.class);

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        System.out.println("Modifying source unit " + sourceUnit.getName());

        ExpandoMetaClass.disableGlobally();

        for (ASTNode astNode : nodes) {
            if (astNode instanceof ClassNode) {
                ClassNode theClass = (ClassNode) astNode;

                if (!GrailsASTUtils.hasOrInheritsProperty(theClass, "dateCreated")) {
                    System.out.println("Adding dateCreated field to class " + theClass.getName());
                    theClass.addProperty("dateCreated", Modifier.PUBLIC, ClassHelper.make(Date.class), null, null, null);
                }
                if (!GrailsASTUtils.hasOrInheritsProperty(theClass, "lastUpdated")) {
                    System.out.println("Adding lastUpdated field to class " + theClass.getName());
                    theClass.addProperty("lastUpdated", Modifier.PUBLIC, ClassHelper.make(Date.class), null, null, null);
                }

                Statement lastUpdatedConstraintExpression = createDateConstraint("lastUpdated", true);

                PropertyNode constraints = theClass.getProperty("constraints");
                if (constraints != null) {
                    System.out.println("Adding lastUpdated to existing constraints closure for class " + theClass.getName());
                    if (constraints.getInitialExpression() instanceof ClosureExpression) {
                        ClosureExpression ce = (ClosureExpression) constraints.getInitialExpression();
                        ((BlockStatement) ce.getCode()).addStatement(lastUpdatedConstraintExpression);
                    } else {
                        System.out.println("Do not know how to add constraints expression to non ClosureExpression " + constraints.getInitialExpression());
                    }
                } else {
                    System.out.println("Adding lastUpdated and constraints closure for class " + theClass.getName());
                    Statement[] constraintsStatement = {lastUpdatedConstraintExpression};
                    BlockStatement closureBlock = new BlockStatement(constraintsStatement, null);
                    ClosureExpression constraintsClosure = new ClosureExpression(null, closureBlock);
                    theClass.addProperty("constraints", Modifier.STATIC | Modifier.PUBLIC, ClassHelper.OBJECT_TYPE, constraintsClosure, null, null);

                }
                VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(sourceUnit);
                scopeVisitor.visitClass(theClass);
            }
        }

        ExpandoMetaClass.enableGlobally();
    }

    private Statement createDateConstraint(String propertyName, boolean nullable) {
        NamedArgumentListExpression nale = new NamedArgumentListExpression();
        nale.addMapEntryExpression(new MapEntryExpression(new ConstantExpression("nullable"), nullable ? ConstantExpression.TRUE : ConstantExpression.FALSE));

        MethodCallExpression mce = new MethodCallExpression(VariableExpression.THIS_EXPRESSION, propertyName, nale);
        return new ExpressionStatement(mce);
    }
}
