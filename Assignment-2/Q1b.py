#!/usr/bin/env python3
# -*- coding:utf-8 -*-
# ------------------------------------------------------
# @Author: Kaustav Vats (kaustav16048@iiitd.ac.in)
# @Roll-No: 2016048
# ------------------------------------------------------

import re
import itertools

def checkVariable(expr):
    varSet = set()
    for i in range(len(expr)):
        if expr[i] != '(' and expr[i] != ')' and expr[i] != '+' and expr[i] != '.' and expr[i] != '*' and expr[i] != '==' and expr[i] != '~':
            varSet.add(expr[i])
    print(varSet)
    return len(varSet), varSet

def singleF(a, varValues, varMap):
    if len(a) == 2:
        if type(a[1]) != bool:
            return not varValues[varMap[a[1]]]
        return not

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
    

def evaluate(varMap, expr, varValues):
    stack = []
    for i in range(len(expr)):
        if expr[i] == ')':
            oneF = ""
            while(stack[-1] != '('):
                oneF = stack.pop() + oneF
            stack.pop()
            if len(oneF) > 2:
                if oneF.find('+'):
                    a1, a3 = oneF.split('+')
                elif oneF.find('.'):
                    a1, a3 = oneF.split('.')
                elif oneF.find('*'):
                    a1, a3 = oneF.split('*')
                else:
                    a1, a3 = oneF.split('==')
            else:
                
            # a3 = stack.pop()
            # a2 = stack.pop()
            # a1 = stack.pop()
            # stack.pop()
            # if a2 == '+':
            #     res = andF(a1, a3, varValues, varMap)
            # elif a2 == '.':
            #     res = orF(a1, a3, varValues, varMap)
            # elif a2 == '.':
            #     res = orF(a1, a3, varValues, varMap)
            # elif a2 == '.':
            #     res = orF(a1, a3, varValues, varMap)
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
        results[i] = evaluate(varMap, expr, truthTable[i])
    showTruthTable(truthTable, results, varMap)

def showTruthTable(truthTable, results, varMap):
    for k in varMap.keys():
        print(k, end='\t')
    print()
    for i in range(len(truthTable)):
        for j in range(len(truthTable[i])):
            print(truthTable[i][j], end='\t')
        print(results[i])

if __name__ == "__main__":
    
    # expr = str(input("Enter Formula: "))
    expr = "((A + B) *  C) * ((A * C) . (B * C))"
    expr = re.sub(r"\s+", '', expr)
    # print(expr)
    varCount, _ = checkVariable(expr)
    # print(TruthTableSize)
    TruthTable = table = list(itertools.product([True, False], repeat=varCount))
    # print(TruthTable)

