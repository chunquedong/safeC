/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sric.lsp;

import java.util.ArrayList;
import java.util.HashMap;
import sric.compiler.ast.AstNode;
import sric.compiler.ast.AstNode.FieldDef;
import sric.compiler.ast.AstNode.FileUnit;
import sric.compiler.ast.Buildin;
import sric.compiler.ast.Expr;
import sric.compiler.ast.Scope;

/**
 *
 * @author yangjiandong
 */
public class CompletionFinder {

    private ArrayList<AstNode> defs = new ArrayList<AstNode>();
    private AstNode target;
    private String text;

    public ArrayList<AstNode> findSugs(FileUnit funit, AstNode node, String text) {
        defs.clear();
        this.target = node;
        this.target = null;
        this.text = text;
        
        
        if (this.target instanceof Expr e) {
            AstNode resolvedDef = e.resolvedType.id.resolvedDef;
            if (resolvedDef != null) {
                if (resolvedDef instanceof AstNode.TypeDef t) {
                    Scope scope = t.getScope();
                    addScope(scope, text);
                    if (defs.size() == 0) {
                        addScope(scope, null);
                    }
                }
            }
        }
        
        if (defs.size() > 0) {
            return defs;
        }
        
        addScope(funit.importScope, text);
        addScope(funit.module.getScope(), text);
        addScope(Buildin.getBuildinScope(), text);
        
        return defs;
    }
    
    private void addScope(Scope scope, String prefix) {
        for (HashMap.Entry<String, ArrayList<AstNode>> entry : scope.symbolTable.entrySet()) {
            String name = entry.getKey();
            if (prefix == null || name.startsWith(prefix)) {
                for (AstNode anode : entry.getValue()) {
                    defs.add(anode);
                }
            }
        }
    }

}

