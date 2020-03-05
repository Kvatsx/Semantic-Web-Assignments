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
import itertools

def checkVariable(expr):
    varSet = set()
    for i in range(len(expr)):
        if expr[i] != '(' and expr[i] != ')' and expr[i] != '+' and expr[i] != '.' and expr[i] != '*' and expr[i] != '==' and expr[i] != '~':
            varSet.add(expr[i])
    return len(varSet), sorted(list(varSet))

def andF(a1, a3, varValues, varMap):
    if type(a1) != bool:
        a1 = varValues[varMap[a1]]
    if type(a3) != bool:
        a3 = varValues[varMap[a3]]
    return a1 and a3
    

def orF(a1, a3, varValues, varMap):
    if type(a1) != bool:
        a1 = varValues[varMap[a1]]
    if type(a3) != bool:
        a3 = varValues[varMap[a3]]
    return a1 or a3
    
def impliesF(a1, a3, varValues, varMap):
    if type(a1) != bool:
        a1 = varValues[varMap[a1]]
    return orF(not a1, a3, varValues, varMap)

def biimplies(a1, a3, varvarValues, varMapMap):
    return impliesF(a1, a3, varvarValues, varMapMap) and impliesF(a3, a1, varvarValues, varMapMap)

def findExpr(listExpr):
    for i in range(len(listExpr)):
        if listExpr[i] == '+' or listExpr[i] == '.' or listExpr[i] == '*' or listExpr[i] == '==':
            return i
    return None

def handleNegation(listVar, varValues, varMap):
    if listVar[0] != '~':
        print("[Error] Negation")
        return None
    if type(listVar[1]) != bool:
        r = not (varValues[varMap[listVar[1]]])
    else:
        r = not (listVar[1])
    return r

def handleSingleVar(listVar, varValues, varMap):
    if type(listVar[0]) != bool:
        r = varValues[varMap[listVar[0]]]
    else:
        r = listVar[0]
    return r

def evaluate(varMap, expr, varValues, pl=False):
    stack = []
    for i in range(len(expr)):
        if expr[i] == ')':
            temp = []
            while(len(stack) > 0 and stack[-1] != '('):
                temp.insert(0, stack.pop())
            if pl:
                print(temp)
            stack.pop()
            if len(temp) == 2:
                res = handleNegation(temp, varValues, varMap)
            elif len(temp) == 1:
                res = handleSingleVar(temp, varValues, varMap)
            else:
                index = findExpr(temp)
                ex1 = []
                ex2 = []
                for i in range(0, index):
                    ex1.append(temp[i])
                for i in range(index+1, len(temp)):
                    ex2.append(temp[i])

                if len(ex1) == 2:
                    p1 = handleNegation(ex1, varValues, varMap)
                elif len(ex1) == 1:
                    p1 = handleSingleVar(ex1, varValues, varMap)

                if len(ex2) == 2:
                    p2 = handleNegation(ex2, varValues, varMap)
                elif len(ex2) == 1:
                    p2 = handleSingleVar(ex2, varValues, varMap)
                if pl:
                    print(p1, p2)
                if temp[index] == '+':
                    res = orF(p1, p2, varValues, varMap)
                elif temp[index] == '.':
                    res = andF(p1, p2, varValues, varMap)
                elif temp[index] == '*':
                    res = impliesF(p1, p2, varValues, varMap)
                else:
                    res = biimplies(p1, p2, varValues, varMap)
            if pl:
                print(res)
                print()
            stack.append(res)
        elif expr[i] == '~':
            if stack[-1] == '~':
                stack.pop()
            else:
                stack.append('~')
        else:
            stack.append(expr[i])
    return stack[0]

def getVarMap(expr):
    _, varSet = checkVariable(expr)
    varMap = {}
    for i in range(len(varSet)):
        varMap[varSet[i]] = i
    return varMap

def createTruthTable(truthTable, expr):
    results = [False for i in range(len(truthTable))]
    varMap = getVarMap(expr)
    for i in range(len(truthTable)):
        # if (i == 1):
            # results[i] = evaluate(varMap, expr, truthTable[i], pl=True)
        # else:
        results[i] = evaluate(varMap, expr, truthTable[i])
        # print("------------------------------------\n")
    showTruthTable(truthTable, results, varMap)

def showTruthTable(truthTable, results, varMap):
    for k in varMap.keys():
        print(k, end='\t')
    print()
    for i in range(len(truthTable)):
        for j in range(len(truthTable[i])):
            print(truthTable[i][j], end='\t')
        print(results[i])
    print("\nDisjunctive Normal Form:", disjunctionNormalForm(truthTable, results, varMap))

def disjunctionNormalForm(tTable, result, varMap):
    Ans = []
    for i in range(len(result)):
        if result[i]:
            r = []
            for j, e in enumerate(varMap.keys()):
                if tTable[i][j]:
                    r.append(e)
                else:
                    r.append("~" + e)
            s = ".".join(r)
            s = "(" + s + ")"
            Ans.append(s)
    dnf = " + ".join(Ans)
    if len(Ans) > 1:
        dnf = "(" + dnf + ")"
    return dnf

if __name__ == "__main__":
    
    expr = str(input("Enter Formula: "))
    # expr = "(((A . B) *  C) * ((A * C) + (B * C)))"
    # expr = "(~A . ~B)"
    # expr = "((A + ~B) . ~C)"
    # expr = "(A)"
    expr = re.sub(r"\s+", '', expr)
    # print(expr)
    varCount, _ = checkVariable(expr)
    # print(TruthTableSize)
    table = list(itertools.product([True, False], repeat=varCount))
    # print(table)
    createTruthTable(table, expr)

