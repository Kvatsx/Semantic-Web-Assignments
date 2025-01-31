#!/usr/bin/env python3
# -*- coding:utf-8 -*-
# ------------------------------------------------------
# @Author: Kaustav Vats (kaustav16048@iiitd.ac.in)
# @Roll-No: 2016048
# ------------------------------------------------------
# Assumptions- 
# 1. Given Formula should be contained within paranthesis.
# 2. Given Formula should not contains consecutive negations.
# 3. Eg- (~A + ~B), (A*~A) These type of formulas are valid.

import re

def checkVariable(expr):
    varSet = set()
    for i in range(len(expr)):
        for j in range(len(expr[i])):
            if expr[i][j] != '(' and expr[i][j] != ')' and expr[i][j] != '+' and expr[i][j] != '.' and expr[i][j] != '*' and expr[i][j] != '==' and expr[i][j] != '~':
                varSet.add(expr[i][j])
    return len(varSet), sorted(list(varSet))

def splitPoint(xp):
    stack = 0
    for i in range(len(xp)):
        if (xp[i] == '('):
            stack += 1
        elif (xp[i] == ')'):
            stack -= 1
        else:
            if stack == 1 and ( xp[i] == '+' or xp[i] == '.' or xp[i] == '*' or xp[i] == '==' ):
                return i
    return -1

def extract(formula, index):
    f1 = formula[1:index]
    f2 = formula[index+1:len(formula)-1]
    return f1, f2

def evaluate(expr, kBase):
    for i in range(len(expr)):
        form, flag = expr[i][0], expr[i][1]
        if len(form) == 1:
            if flag:
                if kBase[form] == False:
                    return True
            else:
                if kBase[form] == True:
                    return True
            kBase[form] = flag
        if len(form) == 2:
            if flag:
                if kBase[form[1]] == True:
                    return True
            else:
                if kBase[form[1]] == False:
                    return True
            kBase[form] = not flag

    # print(expr)
    # print(kBase)
    # print("--------------------")
    if len(expr) == 0:
        return False
    temp = expr.pop(0)
    formula = temp[0]
    flag = temp[1]
    index = splitPoint(formula)
    if index != -1:
        F1, F2 = extract(formula, index)
        if formula[index] == '+':
            if flag:
                kBase1 = kBase.copy()
                kBase2 = kBase.copy()
                expr1 = expr.copy()
                expr2 = expr.copy()
                expr1.append([F1, True])
                expr2.append([F2, True])
                return evaluate(expr1, kBase1) and evaluate(expr2, kBase2)
            else:
                expr.append([F1, False])
                expr.append([F2, False])
                return evaluate(expr, kBase)
        elif formula[index] == '.':
            if flag:
                expr.append([F1, True])
                expr.append([F2, True])
                return evaluate(expr, kBase)
            else:
                kBase1 = kBase.copy()
                kBase2 = kBase.copy()
                expr1 = expr.copy()
                expr2 = expr.copy()
                expr1.append([F1, False])
                expr2.append([F2, False])
                return evaluate(expr1, kBase1) and evaluate(expr2, kBase2)
        elif formula[index] == '*':
            if flag:
                kBase1 = kBase.copy()
                kBase2 = kBase.copy()
                expr1 = expr.copy()
                expr2 = expr.copy()
                expr1.append([F1, False])
                expr2.append([F2, True])
                return evaluate(expr1, kBase1) and evaluate(expr2, kBase2)
            else:
                expr.append([F1, True])
                expr.append([F2, False])
                return evaluate(expr, kBase)
        else:
            if flag:
                kBase1 = kBase.copy()
                kBase2 = kBase.copy()                
                expr1 = expr.copy()
                expr2 = expr.copy()
                expr1.append([F1, True])
                expr1.append([F2, True])
                expr2.append([F1, False])
                expr2.append([F2, False])
                return evaluate(expr1, kBase1) and evaluate(expr2, kBase2)
            else:
                kBase1 = kBase.copy()
                kBase2 = kBase.copy()                
                expr1 = expr.copy()
                expr2 = expr.copy()
                expr1.append([F1, True])
                expr1.append([F2, False])
                expr2.append([F1, False])
                expr2.append([F2, True])
                return evaluate(expr1, kBase1) and evaluate(expr2, kBase2)
    else:
        if len(formula) == 1:
            if kBase[formula] == True:
                if flag == False:
                    return True
            elif kBase[formula] == False:
                if flag == True:
                    return True
        elif len(formula) == 2:
            if kBase[formula[1]] == True:
                if flag == True:
                    return True
            elif kBase[formula[1]] == False:
                if flag == False:
                    return True
    return evaluate(expr, kBase)

                

def run(expr):
    _, varValues = checkVariable(expr)
    kBase = {}
    for i in varValues:
        kBase[i] = None
    expr[0] = [expr[0], True]
    expr[1] = [expr[1], False]
    if evaluate(expr, kBase):
        print("Yes")
    else:
        print("No")

def clean(xp):
    for i in range(len(xp)):
        xp[i] = re.sub(r"\s+", '', xp[i])
    return xp

if __name__ == "__main__":
    expr = str(input("Enter Expression:"))
    # expr = "((A * B) * (A * C)), (A * (B * C))"
    expr = expr.split(",")
    expr = clean(expr)
    # print(expr)
    run(expr)
    
