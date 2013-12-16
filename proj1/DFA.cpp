#include <set>
#include "NFA.h"
#include "DFA.h"

using namespace std;

DFA::DFA(const NFA &nfa) {
    struct q_dfa {
        vector<int> id;
        map<char, vector<vector<int> > > d;
    };

    shared_ptr<q_dfa> S_dfa;
    vector<vector<int> > F_dfa;
    vector<shared_ptr<q_dfa> > Q_dfa;
    
    auto Q_nfa = nfa.states();
    
    string E = "0123456789";
    auto t = getTrans(E, { 0 }, Q_nfa);
    map<set<int>, map<char, set<int> > > sn;
    sn[{0}] = t;
    for (auto s : sn) {
        auto ti = getTrans(E, s.second, Q_nfa);
        for (char c : E) {
            //sn[s.second[c], 
        }
    }
}

map<char, set<int> > DFA::getTrans(string E, set<int> q0, vector<shared_ptr<q> > Q_nfa) {
    map<char, set<int> > t;

    for (char c : E) {
        for (int i : q0) {
            auto ti = Q_nfa[i]->d[c];
            for (auto qn : ti) {
                t[c].insert(qn->id);
            }
        }
    }

    return move(t);
}
