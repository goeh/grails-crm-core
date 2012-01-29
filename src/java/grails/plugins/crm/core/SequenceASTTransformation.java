package grails.plugins.crm.core;

import groovy.lang.ExpandoMetaClass;
import org.codehaus.groovy.ast.*;
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

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class SequenceASTTransformation implements ASTTransformation {

    //private static final Log LOG = LogFactory.getLog(SequenceASTTransformation.class);

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        System.out.println("Modifying source unit " + sourceUnit.getName());

        ExpandoMetaClass.disableGlobally();

        for (ASTNode astNode : nodes) {
            if (astNode instanceof ClassNode) {
                ClassNode theClass = (ClassNode) astNode;
                AnnotationNode sequenceDefinition = GrailsASTUtils.findAnnotation(ClassHelper.make(SequenceEntity.class), theClass.getAnnotations());

                Expression propertyExpr = sequenceDefinition.getMember("property");
                if(propertyExpr == null) {
                    propertyExpr = new ConstantExpression("number");
                }
                String propertyName = propertyExpr.getText();

                if (!GrailsASTUtils.hasOrInheritsProperty(theClass, propertyName)) {
                    System.out.println("Adding sequence field [" + propertyName + "] to class " + theClass.getName());

                    Expression maxSize = sequenceDefinition.getMember("maxSize");
                    Expression blank = sequenceDefinition.getMember("blank");
                    Expression unique = sequenceDefinition.getMember("unique");
                    if(unique != null) {
                        String uniqueText = unique.getText();
                        if("true".equalsIgnoreCase(uniqueText)) {
                            unique = ConstantExpression.TRUE;
                        } else if("false".equalsIgnoreCase(uniqueText)) {
                            unique = ConstantExpression.FALSE;
                        } else {
                            unique = new ConstantExpression(uniqueText);
                        }
                    }
                    theClass.addProperty(propertyName, Modifier.PUBLIC, ClassHelper.STRING_TYPE, null, null, null);
                    Statement numberConstraintExpression = createStringConstraint(propertyExpr, maxSize, blank, unique);

                    PropertyNode constraints = theClass.getProperty("constraints");
                    if (constraints != null) {
                        System.out.println("Adding sequence to existing constraints closure for class " + theClass.getName());
                        if (constraints.getInitialExpression() instanceof ClosureExpression) {
                            ClosureExpression ce = (ClosureExpression) constraints.getInitialExpression();
                            ((BlockStatement) ce.getCode()).addStatement(numberConstraintExpression);
                        } else {
                            System.out.println("Do not know how to add constraints expression to non ClosureExpression " + constraints.getInitialExpression());
                        }
                    } else {
                        System.out.println("Adding sequence and constraints closure for class " + theClass.getName());
                        Statement[] constraintsStatement = {numberConstraintExpression};
                        BlockStatement closureBlock = new BlockStatement(constraintsStatement, null);
                        ClosureExpression constraintsClosure = new ClosureExpression(null, closureBlock);
                        theClass.addProperty("constraints", Modifier.STATIC | Modifier.PUBLIC, ClassHelper.OBJECT_TYPE, constraintsClosure, null, null);

                    }
                }
                VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(sourceUnit);
                scopeVisitor.visitClass(theClass);
            }
        }

        ExpandoMetaClass.enableGlobally();
    }

    private Statement createStringConstraint(Expression propertyName, Expression maxSize, Expression blank, Expression unique) {
        NamedArgumentListExpression nale = new NamedArgumentListExpression();
        if(maxSize != null) {
            nale.addMapEntryExpression(new MapEntryExpression(new ConstantExpression("maxSize"), maxSize));
        }
        if(blank != null) {
            nale.addMapEntryExpression(new MapEntryExpression(new ConstantExpression("blank"), blank));
        }
        if(unique != null) {
            nale.addMapEntryExpression(new MapEntryExpression(new ConstantExpression("unique"), unique));
        }

        MethodCallExpression mce = new MethodCallExpression(VariableExpression.THIS_EXPRESSION, propertyName, nale);
        return new ExpressionStatement(mce);
    }
}
