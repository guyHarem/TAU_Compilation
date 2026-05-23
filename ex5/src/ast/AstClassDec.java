package ast;

import ir.*;
import temp.*;
import types.*;
import symboltable.*;

public class AstClassDec extends AstDec {
    public String name;
    public String parent;
    public AstList<AstDec> fields;

    public AstClassDec(String name, String parent, AstList<AstDec> fields, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.parent = parent;
        this.fields = fields;
        this.line = lineNum;
    }

    public void printMe() {
        String parentStr = (parent != null) ? parent : "null";
        String output = String.format("def class (%s)\nextends (%s)", name, parentStr);
        if (fields != null) fields.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, output);
        if (fields != null) AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }

    @Override
    public Type semantMe() {
        /******************************************/
        /* [1] Check if parent class exists       */
        /******************************************/
        TypeClass parentType = null;
        if (parent != null) {
            Type t = SymbolTable.getInstance().find(parent);
            if (t == null) {
                throw new SemanticError(line, "undefined parent class: " + parent);
            }
            if (!(t instanceof TypeClass)) {
                throw new SemanticError(line, "parent must be a class: " + parent);
            }
            parentType = (TypeClass) t;
        }

        /******************************************/
        /* [1.5] Check for duplicate name         */
        /******************************************/
        Type existingType = SymbolTable.getInstance().find(name);
        if (existingType != null) {
            throw new SemanticError(line, "identifier already defined: " + name);
        }

        /******************************************/
        /* [2] Create and enter class type        */
        /******************************************/
        TypeClass classType = new TypeClass(parentType, name, null);
        SymbolTable.getInstance().enter(name, classType);

        /******************************************/
        /* [3] Set current class context          */
        /******************************************/
        SymbolTable.getInstance().setCurrentClass(classType);

        /******************************************/
        /* [4] Begin class scope                  */
        /******************************************/
        SymbolTable.getInstance().beginClassScope();

        /******************************************/
        /* [5] Process fields and methods         */
        /******************************************/
        if (fields != null) {
            for (AstList<AstDec> it = fields; it != null; it = it.tail) {
                if (it.head != null) {
                    String memberName = null;
                    int memberLine = it.head.line;
                    boolean isMethod = it.head instanceof AstFuncDec;

                    if (it.head instanceof AstVarDec) {
                        memberName = ((AstVarDec) it.head).name;
                        memberLine = ((AstVarDec) it.head).type.line;
                    } else if (isMethod) {
                        memberName = ((AstFuncDec) it.head).name;
                        AstFuncDec funcDec = (AstFuncDec) it.head;
                        memberLine = (funcDec.type != null) ? funcDec.type.line : it.head.line;
                    }

                    /******************************************/
                    /* [5.1] Check for duplicate in same class */
                    /******************************************/
                    if (memberName != null && classType.memberMap.containsKey(memberName)) {
                        // Same name already exists in this class - illegal
                        // This covers: method overloading, duplicate fields, field-method conflict
                        throw new SemanticError(memberLine, "duplicate member name in class: " + memberName);
                    }

                    /******************************************/
                    /* [5.2] Check for shadowing in superclass */
                    /******************************************/
                    if (memberName != null && parentType != null) {
                        Type parentMember = parentType.findMember(memberName);
                        if (parentMember != null) {
                            if (it.head instanceof AstVarDec) {
                                // Field cannot shadow any member (field or method) in superclass
                                throw new SemanticError(memberLine, "field shadows member in superclass: " + memberName);
                            } else if (isMethod) {
                                // Method can only override method, not shadow field
                                if (!(parentMember instanceof TypeFunction)) {
                                    throw new SemanticError(memberLine, "method shadows field in superclass: " + memberName);
                                }
                                // Method override - validate signature
                                AstFuncDec funcDec = (AstFuncDec) it.head;
                                TypeFunction parentFunc = (TypeFunction) parentMember;

                                // Check return type
                                String childReturnTypeName = (funcDec.type != null) ? funcDec.type.name : "void";
                                Type childReturnType = SymbolTable.getInstance().find(childReturnTypeName);
                                if (childReturnType != parentFunc.returnType) {
                                    throw new SemanticError(memberLine, "method override with different return type: " + memberName);
                                }

                                // Check parameter count
                                int childParamCount = 0;
                                if (funcDec.params != null) {
                                    for (AstList<AstVarDec> p = funcDec.params; p != null; p = p.tail) {
                                        if (p.head != null) childParamCount++;
                                    }
                                }
                                int parentParamCount = 0;
                                for (TypeList p = parentFunc.params; p != null; p = p.tail) {
                                    parentParamCount++;
                                }
                                if (childParamCount != parentParamCount) {
                                    throw new SemanticError(memberLine, "method override with different parameter count: " + memberName);
                                }

                                // Check parameter types
                                AstList<AstVarDec> childParams = funcDec.params;
                                TypeList parentParams = parentFunc.params;
                                while (childParams != null && parentParams != null) {
                                    if (childParams.head != null) {
                                        Type childParamType = SymbolTable.getInstance().find(childParams.head.type.name);
                                        if (childParamType != parentParams.head) {
                                            throw new SemanticError(memberLine, "method override with different parameter type: " + memberName);
                                        }
                                        parentParams = parentParams.tail;
                                    }
                                    childParams = childParams.tail;
                                }
                            }
                        }
                    }

                    Type fieldType = it.head.semantMe();
                    // Add to class member map
                    if (it.head instanceof AstVarDec) {
                        AstVarDec vd = (AstVarDec) it.head;
                        classType.addMember(vd.name, fieldType);
                        // Record any literal init so AstNewExp can write it at construction.
                        if (vd.exp != null) {
                            TypeClass.FieldInit fi = new TypeClass.FieldInit();
                            fi.offset = classType.getFieldOffset(vd.name);
                            if (vd.exp instanceof AstExpInt) {
                                fi.intValue = ((AstExpInt) vd.exp).value;
                            } else if (vd.exp instanceof AstExpString) {
                                fi.stringLabel = ((AstExpString) vd.exp).value;
                            }
                            // nil init: leave both null; malloc already zero-fills.
                            classType.fieldInits.add(fi);
                        }
                    } else if (it.head instanceof AstFuncDec) {
                        classType.addMember(((AstFuncDec)it.head).name, fieldType);
                        // Inherited overrides reuse the parent's slot; new methods append.
                        // Label uses the same `_user_` prefix as AstFuncDec.irMe.
                        String mangled = "_user_" + name + "_" + memberName;
                        if (classType.methodIndex.containsKey(memberName)) {
                            classType.methodLabel.put(memberName, mangled);
                        } else {
                            int slot = classType.methodIndex.size();
                            classType.methodIndex.put(memberName, slot);
                            classType.methodLabel.put(memberName, mangled);
                        }
                    }
                }
            }
        }

        /******************************************/
        /* [6] End class scope                    */
        /******************************************/
        SymbolTable.getInstance().endScope();

        /******************************************/
        /* [7] Clear current class context        */
        /******************************************/
        SymbolTable.getInstance().setCurrentClass(null);

        return classType;
    }

    @Override
    public Temp irMe() {
        Type t = SymbolTable.getInstance().find(name);
        if (!(t instanceof TypeClass)) return null;
        TypeClass classType = (TypeClass) t;

        // Vtable goes into .data via globalDecls.
        Ir.getInstance().AddIrGlobalDecleration(new IrCommandVtable(name, classType.getVtableLabels()));

        // Methods need currentClass set so AstFuncDec.irMe mangles labels as Class_method.
        SymbolTable.getInstance().setCurrentClass(classType);
        if (fields != null) {
            for (AstList<AstDec> it = fields; it != null; it = it.tail) {
                if (it.head instanceof AstFuncDec) {
                    it.head.irMe();
                }
            }
        }
        SymbolTable.getInstance().setCurrentClass(null);
        return null;
    }
}
