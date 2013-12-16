#ifndef DFA_H
#define DFA_H

#include <set>
#include "NFA.h"

using namespace std;

class DFA : NFA {
    public:
        DFA(const NFA &nfa);

    private:
        struct pwr_q {
            set<int> id;
            map<char, set<int> > d;
        };

        vector<shared_ptr<pwr_q> > Q_dfa;
        map<char, set<int> > getTrans(string sigma, set<int> q0, vector<shared_ptr<q> > Q_nfa);
};
