/*
 * Copyright (c) 2011 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.group;

import com.mysema.commons.lang.Pair;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.Expression;

/**
 * A pair of (Map) key and value 
 * 
 * @author sasa
 * @param <K> Map key type
 * @param <V> Map value type
 */
 final class QPair<K, V> extends ConstructorExpression<Pair<K, V>> {

    private static final long serialVersionUID = -1943990903548916056L;

    public static <K, V> QPair<K, V> create(Expression<K> key, Expression<V> value) {
        return new QPair<K, V>(key, value);
    }
    
    @SuppressWarnings({"unchecked" })
    public QPair(Expression<K> key, Expression<V> value) {
        super((Class) Pair.class, new Class[]{Object.class, Object.class}, key, value);
    }
    
    public boolean equals(Expression<?> keyExpr, Expression<?> valueExpr) {
        return getArgs().get(0).equals(keyExpr) && getArgs().get(1).equals(valueExpr);
    }
    
    public boolean equals(Expression<?> keyExpr, Class<?> valueType) {
        return getArgs().get(0).equals(keyExpr) && valueType.isAssignableFrom(getArgs().get(1).getType());
    }
    
}