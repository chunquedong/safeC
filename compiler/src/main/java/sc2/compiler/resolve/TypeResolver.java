//
// Copyright (c) 2024, chunquedong
// Licensed under the Academic Free License version 3.0
//
package sc2.compiler.resolve;

import java.util.ArrayList;
import java.util.HashMap;
import sc2.compiler.CompilePass;
import sc2.compiler.CompilerLog;
import sc2.compiler.Compiler;
import sc2.compiler.ast.AstNode;
import sc2.compiler.ast.AstNode.*;
import sc2.compiler.ast.Expr.IdExpr;
import sc2.compiler.ast.*;
import sc2.compiler.ast.SModule.Depend;

/**
 *
 * @author yangjiandong
 */
public abstract class TypeResolver  extends CompilePass {
    
    protected ArrayList<Scope> scopes = new ArrayList<>();
    protected SModule module;
    protected StructDef curStruct = null;
    
    public TypeResolver(CompilerLog log, SModule module) {
        super(log);
        this.module = module;
        this.log = log;
    }
        
    protected Scope pushScope() {
        Scope s = new Scope();
        scopes.add(s);
        return s;
    }
    
    protected Scope popScope() {
        return scopes.remove(scopes.size()-1);
    }
    
    private AstNode findSymbol(String name, Loc loc) {
        for (int i = scopes.size()-1; i >=0; --i) {
            Scope scope = scopes.get(i);
            AstNode node = scope.get(name, loc, log);
            if (node != null) {
                return node;
            }
        }
        err("Unknow symbol "+name, loc);
        return null;
    }
    
    protected void resolveId(Expr.IdExpr idExpr) {
        if (idExpr.namespace == null) {
            if (idExpr.name.equals("this")) {
                if (curStruct == null) {
                    err("Use this out of struct", idExpr.loc);
                    return;
                }
                Type self = new Type(curStruct.loc, curStruct.name);
                self.id.resolvedDef = curStruct;
                idExpr.resolvedDef = Type.pointerType(idExpr.loc, self, Type.PointerAttr.raw, false);
                return;
            }
            idExpr.resolvedDef = findSymbol(idExpr.name, idExpr.loc);
            return;
        }
        resolveId(idExpr.namespace);
        if (idExpr.namespace.resolvedDef == null) {
            return;
        }
        if (idExpr.namespace.resolvedDef instanceof SModule m) {
            AstNode node = m.getScope().get(idExpr.name, idExpr.loc, log);
            if (node == null) {
                err("Unknow symbol "+idExpr.name, idExpr.loc);
            }
            idExpr.resolvedDef = node;
            return;
        }
        else if (idExpr.namespace.resolvedDef instanceof TypeDef m) {
            AstNode node = m.getScope().get(idExpr.name, idExpr.loc, log);
            if (node == null) {
                err("Unknow symbol "+idExpr.name, idExpr.loc);
            }
            idExpr.resolvedDef = node;
            return;
        }
        else {
            err("Unsupport :: for "+idExpr.namespace.name, idExpr.loc);
        }
    }
    
    protected void resolveTopLevelType(Type type, Loc loc) {
        if (type == null) {
            err("Type inference not support for top level node", loc);
            return;
        }
        resolveType(type);
    }

    protected void resolveType(Type type) {
        resolveId(type.id);
        if (type.id.resolvedDef != null) {
            if (type.id.resolvedDef instanceof TypeDef) {
                //ok
                type.id.resolvedType = Type.metaType(type.loc, type);
            }
            else if (type.id.resolvedDef instanceof TypeAlias ta) {
                type.id.resolvedDef = ta.type.id.resolvedDef;
                type.id.resolvedType = Type.metaType(type.loc, type);
            }
            else {
                type.id.resolvedDef = null;
                err("It's not a type: "+type.id.name, type.loc);
            }
        }
        else {
            return;
        }
        
        if (type.isFuncType()) {
            return;
        }
        
        if (type.genericArgs != null) {
            for (int i=0; i<type.genericArgs.size(); ++i) {
                resolveType(type.genericArgs.get(i));
            }

            boolean genericOk = false;
            if (type.id.resolvedDef instanceof StructDef sd) {
                if (sd.generiParamDefs != null) {
                    if (type.genericArgs.size() == sd.generiParamDefs.size()) {
                        type.id.resolvedDef = sd.parameterize(type.genericArgs);
                        genericOk = true;
                    }
                }
            }
            if (!genericOk) {
                err("Generic args mismatch", type.loc);
            }
        }
        else if (type.id.resolvedDef instanceof StructDef sd) {
            if (sd.generiParamDefs != null) {
                err("Miss generic args", type.loc);
            }
        }
    }
}