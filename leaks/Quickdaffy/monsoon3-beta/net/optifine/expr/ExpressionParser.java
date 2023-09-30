/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.expr;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.src.Config;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.FunctionBool;
import net.optifine.expr.FunctionFloat;
import net.optifine.expr.FunctionFloatArray;
import net.optifine.expr.FunctionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionResolver;
import net.optifine.expr.ParseException;
import net.optifine.expr.Token;
import net.optifine.expr.TokenParser;
import net.optifine.expr.TokenType;

public class ExpressionParser {
    private IExpressionResolver expressionResolver;

    public ExpressionParser(IExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    public IExpressionFloat parseFloat(String str) throws ParseException {
        IExpression iexpression = this.parse(str);
        if (!(iexpression instanceof IExpressionFloat)) {
            throw new ParseException("Not a float expression: " + (Object)((Object)iexpression.getExpressionType()));
        }
        return (IExpressionFloat)iexpression;
    }

    public IExpressionBool parseBool(String str) throws ParseException {
        IExpression iexpression = this.parse(str);
        if (!(iexpression instanceof IExpressionBool)) {
            throw new ParseException("Not a boolean expression: " + (Object)((Object)iexpression.getExpressionType()));
        }
        return (IExpressionBool)iexpression;
    }

    public IExpression parse(String str) throws ParseException {
        try {
            Token[] atoken = TokenParser.parse(str);
            if (atoken == null) {
                return null;
            }
            ArrayDeque<Token> deque = new ArrayDeque<Token>(Arrays.asList(atoken));
            return this.parseInfix(deque);
        }
        catch (IOException ioexception) {
            throw new ParseException(ioexception.getMessage(), ioexception);
        }
    }

    private IExpression parseInfix(Deque<Token> deque) throws ParseException {
        if (deque.isEmpty()) {
            return null;
        }
        LinkedList<IExpression> list = new LinkedList<IExpression>();
        LinkedList<Token> list1 = new LinkedList<Token>();
        IExpression iexpression = this.parseExpression(deque);
        ExpressionParser.checkNull(iexpression, "Missing expression");
        list.add(iexpression);
        Token token;
        while ((token = deque.poll()) != null) {
            if (token.getType() != TokenType.OPERATOR) {
                throw new ParseException("Invalid operator: " + token);
            }
            IExpression iexpression1 = this.parseExpression(deque);
            ExpressionParser.checkNull(iexpression1, "Missing expression");
            list1.add(token);
            list.add(iexpression1);
        }
        return this.makeInfix(list, list1);
    }

    private IExpression makeInfix(List<IExpression> listExpr, List<Token> listOper) throws ParseException {
        LinkedList<FunctionType> list = new LinkedList<FunctionType>();
        for (Token token : listOper) {
            FunctionType functiontype = FunctionType.parse(token.getText());
            ExpressionParser.checkNull((Object)functiontype, "Invalid operator: " + token);
            list.add(functiontype);
        }
        return this.makeInfixFunc(listExpr, list);
    }

    private IExpression makeInfixFunc(List<IExpression> listExpr, List<FunctionType> listFunc) throws ParseException {
        if (listExpr.size() != listFunc.size() + 1) {
            throw new ParseException("Invalid infix expression, expressions: " + listExpr.size() + ", operators: " + listFunc.size());
        }
        if (listExpr.size() == 1) {
            return listExpr.get(0);
        }
        int i = Integer.MAX_VALUE;
        int j = Integer.MIN_VALUE;
        for (FunctionType functiontype : listFunc) {
            i = Math.min(functiontype.getPrecedence(), i);
            j = Math.max(functiontype.getPrecedence(), j);
        }
        if (j >= i && j - i <= 10) {
            for (int k = j; k >= i; --k) {
                this.mergeOperators(listExpr, listFunc, k);
            }
            if (listExpr.size() == 1 && listFunc.size() == 0) {
                return listExpr.get(0);
            }
            throw new ParseException("Error merging operators, expressions: " + listExpr.size() + ", operators: " + listFunc.size());
        }
        throw new ParseException("Invalid infix precedence, min: " + i + ", max: " + j);
    }

    private void mergeOperators(List<IExpression> listExpr, List<FunctionType> listFuncs, int precedence) throws ParseException {
        for (int i = 0; i < listFuncs.size(); ++i) {
            FunctionType functiontype = listFuncs.get(i);
            if (functiontype.getPrecedence() != precedence) continue;
            listFuncs.remove(i);
            IExpression iexpression = listExpr.remove(i);
            IExpression iexpression1 = listExpr.remove(i);
            IExpression iexpression2 = ExpressionParser.makeFunction(functiontype, new IExpression[]{iexpression, iexpression1});
            listExpr.add(i, iexpression2);
            --i;
        }
    }

    private IExpression parseExpression(Deque<Token> deque) throws ParseException {
        Token token = deque.poll();
        ExpressionParser.checkNull(token, "Missing expression");
        switch (token.getType()) {
            case NUMBER: {
                return ExpressionParser.makeConstantFloat(token);
            }
            case IDENTIFIER: {
                FunctionType functiontype = this.getFunctionType(token, deque);
                if (functiontype != null) {
                    return this.makeFunction(functiontype, deque);
                }
                return this.makeVariable(token);
            }
            case BRACKET_OPEN: {
                return this.makeBracketed(token, deque);
            }
            case OPERATOR: {
                FunctionType functiontype1 = FunctionType.parse(token.getText());
                ExpressionParser.checkNull((Object)functiontype1, "Invalid operator: " + token);
                if (functiontype1 == FunctionType.PLUS) {
                    return this.parseExpression(deque);
                }
                if (functiontype1 == FunctionType.MINUS) {
                    IExpression iexpression1 = this.parseExpression(deque);
                    return ExpressionParser.makeFunction(FunctionType.NEG, new IExpression[]{iexpression1});
                }
                if (functiontype1 != FunctionType.NOT) break;
                IExpression iexpression = this.parseExpression(deque);
                return ExpressionParser.makeFunction(FunctionType.NOT, new IExpression[]{iexpression});
            }
        }
        throw new ParseException("Invalid expression: " + token);
    }

    private static IExpression makeConstantFloat(Token token) throws ParseException {
        float f = Config.parseFloat(token.getText(), Float.NaN);
        if (f == Float.NaN) {
            throw new ParseException("Invalid float value: " + token);
        }
        return new ConstantFloat(f);
    }

    private FunctionType getFunctionType(Token tokens, Deque<Token> deque) throws ParseException {
        Token token = deque.peek();
        if (tokens != null && token.getType() == TokenType.BRACKET_OPEN) {
            FunctionType functiontype1 = FunctionType.parse(token.getText());
            ExpressionParser.checkNull((Object)functiontype1, "Unknown function: " + token);
            return functiontype1;
        }
        FunctionType functiontype = FunctionType.parse(token.getText());
        if (functiontype == null) {
            return null;
        }
        if (functiontype.getParameterCount(new IExpression[0]) > 0) {
            throw new ParseException("Missing arguments: " + (Object)((Object)functiontype));
        }
        return functiontype;
    }

    private IExpression makeFunction(FunctionType type, Deque<Token> deque) throws ParseException {
        Token token;
        if (type.getParameterCount(new IExpression[0]) == 0 && ((token = deque.peek()) == null || token.getType() != TokenType.BRACKET_OPEN)) {
            return ExpressionParser.makeFunction(type, new IExpression[0]);
        }
        Token token1 = deque.poll();
        Deque<Token> deque2 = ExpressionParser.getGroup(deque, TokenType.BRACKET_CLOSE, true);
        IExpression[] aiexpression = this.parseExpressions(deque2);
        return ExpressionParser.makeFunction(type, aiexpression);
    }

    private IExpression[] parseExpressions(Deque<Token> deque) throws ParseException {
        ArrayList<IExpression> list = new ArrayList<IExpression>();
        while (true) {
            Deque<Token> deque2;
            IExpression iexpression;
            if ((iexpression = this.parseInfix(deque2 = ExpressionParser.getGroup(deque, TokenType.COMMA, false))) == null) {
                IExpression[] aiexpression = list.toArray(new IExpression[list.size()]);
                return aiexpression;
            }
            list.add(iexpression);
        }
    }

    private static IExpression makeFunction(FunctionType type, IExpression[] args) throws ParseException {
        ExpressionType[] aexpressiontype = type.getParameterTypes(args);
        if (args.length != aexpressiontype.length) {
            throw new ParseException("Invalid number of arguments, function: \"" + type.getName() + "\", count arguments: " + args.length + ", should be: " + aexpressiontype.length);
        }
        for (int i = 0; i < args.length; ++i) {
            ExpressionType expressiontype1;
            IExpression iexpression = args[i];
            ExpressionType expressiontype = iexpression.getExpressionType();
            if (expressiontype == (expressiontype1 = aexpressiontype[i])) continue;
            throw new ParseException("Invalid argument type, function: \"" + type.getName() + "\", index: " + i + ", type: " + (Object)((Object)expressiontype) + ", should be: " + (Object)((Object)expressiontype1));
        }
        if (type.getExpressionType() == ExpressionType.FLOAT) {
            return new FunctionFloat(type, args);
        }
        if (type.getExpressionType() == ExpressionType.BOOL) {
            return new FunctionBool(type, args);
        }
        if (type.getExpressionType() == ExpressionType.FLOAT_ARRAY) {
            return new FunctionFloatArray(type, args);
        }
        throw new ParseException("Unknown function type: " + (Object)((Object)type.getExpressionType()) + ", function: " + type.getName());
    }

    private IExpression makeVariable(Token token) throws ParseException {
        if (this.expressionResolver == null) {
            throw new ParseException("Model variable not found: " + token);
        }
        IExpression iexpression = this.expressionResolver.getExpression(token.getText());
        if (iexpression == null) {
            throw new ParseException("Model variable not found: " + token);
        }
        return iexpression;
    }

    private IExpression makeBracketed(Token token, Deque<Token> deque) throws ParseException {
        Deque<Token> deque2 = ExpressionParser.getGroup(deque, TokenType.BRACKET_CLOSE, true);
        return this.parseInfix(deque2);
    }

    private static Deque<Token> getGroup(Deque<Token> deque, TokenType tokenTypeEnd, boolean tokenEndRequired) throws ParseException {
        ArrayDeque<Token> deque3 = new ArrayDeque<Token>();
        int i = 0;
        Iterator<Token> iterator = deque.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            iterator.remove();
            if (i == 0 && token.getType() == tokenTypeEnd) {
                return deque3;
            }
            deque3.add(token);
            if (token.getType() == TokenType.BRACKET_OPEN) {
                ++i;
            }
            if (token.getType() != TokenType.BRACKET_CLOSE) continue;
            --i;
        }
        if (tokenEndRequired) {
            throw new ParseException("Missing end token: " + (Object)((Object)tokenTypeEnd));
        }
        return deque3;
    }

    private static void checkNull(Object obj, String message) throws ParseException {
        if (obj == null) {
            throw new ParseException(message);
        }
    }
}

