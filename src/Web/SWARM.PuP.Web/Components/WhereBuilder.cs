using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Web;

namespace System.Linq
{
    public static class WhereBuilder
    {
        public static Expression<Func<T, bool>> Create<T>()
        {
            return null;
        }

        public static Expression<Func<T, bool>> Or<T>(this Expression<Func<T, bool>> expr1,
                                                  Expression<Func<T, bool>> expr2)
        {
            if (expr1 == null)
            {
                return expr2;
            }
            else
            {
                var invokedExpr = Expression.Invoke(expr2, expr1.Parameters.Cast<Expression>());
                return Expression.Lambda<Func<T, bool>>
                     (Expression.OrElse(expr1.Body, expr2.Body), expr1.Parameters);
            }
        }

        public static Expression<Func<T, bool>> And<T>(this Expression<Func<T, bool>> expr1,
                                                   Expression<Func<T, bool>> expr2)
        {
            if (expr1 == null)
            {
                return expr2;
            }
            else
            {
                return Expression.Lambda<Func<T, bool>>
                     (Expression.AndAlso(expr1.Body, expr2.Body), expr1.Parameters);
            }

        }
    }
}