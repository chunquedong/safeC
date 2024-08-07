/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sc2.compiler.resolve;

import java.util.ArrayList;
import java.util.HashMap;
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
public class SlotTypeResolver implements Visitor {
    
    private ArrayList<Scope> scopes = new ArrayList<>();
    private SModule module;
    private CompilerLog log;
    private Compiler compiler;
    
    public SlotTypeResolver(SModule module, CompilerLog log, Compiler compiler) {
        this.module = module;
        this.log = log;
        this.compiler = compiler;
    }
    
    public void run() {
        module.walk(this);
    }
    
    private AstNode findSymbol(String name, Loc loc) {
        for (Scope scope : scopes) {
            AstNode node = scope.get(name, loc, log);
            if (node != null) {
                return node;
            }
        }
        err("Unknow symbol "+name, loc);
        return null;
    }
    
    private CompilerLog.CompilerErr err(String msg, Loc loc) {
        return log.err(msg, loc);
    }
    
    private void resolveImportId(IdExpr idExpr) {
        if (idExpr.namespace == null) {
            for (Depend d : module.depends) {
                if (idExpr.name.equals(d.name)) {
                    if (d.cache == null) {
                        d.cache = compiler.importModule(d.name, d.version);
                    }
                    idExpr.resolvedDef = d.cache;
                    return;
                }
            }
            err("Unknow depends "+idExpr.name, idExpr.loc);
            return;
        }
        resolveImportId(idExpr.namespace);
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
        else if (idExpr.namespace.resolvedDef instanceof StructDef m) {
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
    
    private void resolveId(IdExpr idExpr) {
        if (idExpr.namespace == null) {
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
        else if (idExpr.namespace.resolvedDef instanceof StructDef m) {
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
        
    private void resolveType(Type type, Loc loc) {
        if (type == null) {
            err("Type inference not support for top level node", loc);
            return;
        }
        resolveId(type.id);
        if (type.id.resolvedDef != null) {
            if (!(type.id.resolvedDef instanceof TypeDef)) {
                type.id.resolvedDef = null;
                err("It's not a type "+type.id.name, type.loc);
            }
            return;
        }
    }
    
    private void importToScope(AstNode.Import i, Scope importScope) {

        resolveImportId(i.id);
        
        if (i.id.resolvedDef != null) {
            if (!i.star) {
                importScope.put(i.id.name, i.id.resolvedDef);
            }
            else {
                if (i.id.resolvedDef instanceof SModule m) {
                    Scope mcope = m.getScope();
                    importScope.addAll(mcope);
                }
                else if (i.id.resolvedDef instanceof StructDef c) {
                    Scope mcope = c.getScope();
                    importScope.addAll(mcope);
                }
                else {
                    err("Unsupport ::* for "+i.id.name, i.loc);
                }
            }
        }
    }
    
    @Override
    public boolean deepLevel() {
        return false;
    }

    @Override
    public void enterUnit(AstNode.FileUnit v) {
        v.importScope = new Scope();

        for (AstNode.Import i : v.imports) {
            importToScope(i, v.importScope);
        }
        this.scopes.add(v.importScope);
        
        Scope scope2 = module.getScope();
        this.scopes.add(scope2);
        
        this.scopes.add(Buildin.getBuildinScope());
    }

    @Override
    public void exitUnit(AstNode.FileUnit v) {
        this.scopes.clear();
    }


    @Override
    public void enterField(AstNode.FieldDef v) {
        resolveType(v.fieldType, v.loc);
    }

    @Override
    public void exitField(AstNode.FieldDef v) {

    }

    @Override
    public void enterFunc(AstNode.FuncDef v) {
        resolveType(v.prototype.returnType, v.loc);
        if (v.prototype.paramDefs != null) {
            for (AstNode.ParamDef p : v.prototype.paramDefs) {
                resolveType(p.paramType, v.loc);
            }
        }
    }

    @Override
    public void exitFunc(AstNode.FuncDef v) {

    }

    @Override
    public void enterTypeDef(AstNode.TypeDef v) {

    }

    @Override
    public void exitTypeDef(AstNode.TypeDef v) {

    }

    @Override
    public void enterStmt(Stmt v) {
        
    }

    @Override
    public void exitStmt(Stmt v) {
        
    }

    @Override
    public void enterExpr(Stmt v) {
        
    }

    @Override
    public void exitExpr(Stmt v) {
        
    }
    
}
