import sys
from Automata import *

debug = True

class NearlyDiv(object):
    def __init__(self, k, n):
        self.k = int(k)
        self.n = int(n)
        self.alphabet = frozenset(range(0,10))

    def delta(self, q, c):
        if c not in self.alphabet:
            return frozenset()
        qn = set()
        div = int(q/self.n)
        s = q - q % self.n + ((q % self.n) * 10 + c) % self.n
        qn.add(s)
        if div < self.k:
            qn.add(q + self.n)
        return qn

    def F(self, q):
        return q % self.n == 0

    def __call__(self, q, c):
        return self.delta(q, c)

def make_dfa(k, n):
    if debug: print k, n
    ndiv = NearlyDiv(k, n)

    nfa = NFA()
    nfa.alphabet = ndiv.alphabet
    nfa.transition = lambda q, c: ndiv.delta(q, c)
    nfa.initial = frozenset({0})
    nfa.isfinal = lambda q: ndiv.F(q)

    dfa = nfa.asDFA()
    return dfa

def to_symbols(num):
    sym = [1]
    for i in range(num-1):
        sym.append(0)
    return sym

def to_symbol(num):
    sym = []
    while num > 0:
        sym.insert(0,int(num%10))
        num = int(num/10)
    return sym

def inc(num):
    l = len(num)
    for i in range(1, l+1):
        num[l - i] += 1
        if num[l - i] > 9:
            num[l - i] = 0
        else: break

def main(argv):
    k = int(argv[0])
    n = int(argv[1])
    N = int(argv[2])

    dfa = make_dfa(k, n)
    dfa = dfa.minimize().renumber()
    if debug: 
        sym = to_symbol(N)
        i = 0
        while i < 2:
            dfa(sym)
            i += 1
        print sym, ":", dfa(sym)
        return
    count = 0
    num = 10**(N-1)
    stop = 10**(N)
    sym = to_symbols(N)
    print sym
    while num < stop:
        if dfa(sym):
            count += 1
        inc(sym)
        num += 1
    print "number of nearly divisible integers of length", N, "=", count

if __name__ == '__main__':
    argv = sys.argv[1:]
    if len(argv) != 3:
        print "Usage:", sys.argv[0], "k n N"
    else:
        main(argv)
