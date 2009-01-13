/*
 * Copyright (c) 2008 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.grammar;

import java.util.List;

import com.mysema.query.JoinExpression;
import com.mysema.query.grammar.types.Expr;
import com.mysema.query.grammar.types.Path;
import com.mysema.query.serialization.BaseSerializer;

/**
 * SqlSerializer provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SqlSerializer extends BaseSerializer<SqlSerializer>{
    
    private SqlOps ops;
    
    public SqlSerializer(SqlOps ops){
        super(ops);
        this.ops = ops;
    }
    
    @Override
    protected void visit(Expr.EConstant<?> expr) {
        _append("?");
        constants.add(expr.getConstant());
    }
               
    public void serialize(List<Expr<?>> select, List<JoinExpression<Object>> joins,
        Expr.EBoolean where, List<Expr<?>> groupBy, Expr.EBoolean having,
        List<OrderSpecifier<?>> orderBy, boolean forCountRow){
         if (forCountRow){
//            _append("select count(*)\n");
             _append(ops.selectCountStar());
        }else if (!select.isEmpty()){
            _append(ops.select())._append(", ", select);
        }
        _append(ops.from());
        for (int i=0; i < joins.size(); i++){
            JoinExpression<Object> je = joins.get(i);            
            if (i > 0){
                String sep = ", ";
                    switch(je.getType()){
                    case FULLJOIN:  sep = ops.fullJoin(); break;
                    case INNERJOIN: sep = ops.innerJoin(); break;
                    case JOIN:      sep = ops.join(); break;
                    case LEFTJOIN:  sep = ops.leftJoin(); break;                                
                    }    
                _append(sep);
            }
            
            // type specifier
            if (je.getTarget() instanceof Path.PEntity && ops.supportsAlias()){
                Path.PEntity<?> pe = (Path.PEntity<?>)je.getTarget();
                if (pe.getMetadata().getParent() == null){ 
                    _append(pe.getEntityName())._append(ops.aliasAs());    
                }                
            }            
            handle(je.getTarget());
            if (je.getCondition() != null){
                _append(ops.with()).handle(je.getCondition());
            }
        }
        
        if (where != null){            
            _append(ops.where()).handle(where);
        }
        if (!groupBy.isEmpty()){
            _append(ops.groupBy())._append(", ",groupBy);
        }
        if (having != null){
            if (groupBy.isEmpty()) {
                throw new IllegalArgumentException("having, but not groupBy was given");
            }                
            _append(ops.having()).handle(having);
        }
        if (!orderBy.isEmpty() && !forCountRow){
            _append(ops.orderBy());
            boolean first = true;
            for (OrderSpecifier<?> os : orderBy){            
                if (!first) builder.append(", ");
                handle(os.target);
                _append(os.order == Order.ASC ? ops.asc() : ops.desc());
                first = false;
            }
        }
    }

}
