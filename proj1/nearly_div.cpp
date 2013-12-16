#include <iostream>
#include <string>
#include <sstream>
#include "NFA.h"

using namespace std;

void inc(string &s, int pos);

int main(int argc, char **argv) {
    if (argc != 4) {
        cout << argv[0] << ": error.\n";
        cout << "\tUsage: " << argv[0] << " k n N\n";
        cout << "\twhere:\n";
        cout << "\t\tk = #digits to drop\n";
        cout << "\t\tn = the divisor\n";
        cout << "\t\tN = N-digits numbers will be checked\n";
        return false;
    }

    stringstream k, n, num;
    k << argv[1];
    n << argv[2];
    num << argv[3];

    NFA nfa(stoi(k.str()), stoi(n.str()));

    //cout << nfa;

    if (nfa.nearlyDiv(num.str()))
        cout << "true\n";
    else
        cout << "false\n";

    return true;
}

void inc(string &s, int pos) {
    if (s[pos] == '9') {
        s[pos] = '0';
        return inc(s, pos+1);
    }

    s[pos]++;
}
