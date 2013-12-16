#ifndef NFA_H
#define NFA_H

#include <string>
#include <vector>
#include <map>
#include <memory>

using namespace std;

class NFA {
    public:
        struct q {
            int id;
            map<char, vector<shared_ptr<q> > > d;
        };        NFA();

        NFA(int k, int n);
        bool nearlyDiv(string num);
        int numStates() const;
        friend ostream &operator<<(ostream &outs, const NFA &nfa);
        vector<shared_ptr<q> > states() const;

    protected:
        shared_ptr<q> S;
        vector<int> F;
        vector<shared_ptr<q> > Q;

        bool run(vector<char>::iterator head, shared_ptr<q> qi, const vector<char>::iterator &end);
};

#endif // NFA_H
