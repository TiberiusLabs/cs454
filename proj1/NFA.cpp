#include <string>
#include <vector>
#include <map>
#include <memory>
#include <algorithm>
#include <functional>
#include <iostream>

#include "NFA.h"

using namespace std;

NFA::NFA(int k, int n) {
    Q = vector<shared_ptr<q> > (n * (k+1), shared_ptr<q>(new q));
    //cout << "number states == " << Q.size() << endl;

    for (int i = 0; i < n; ++i) {
        for (int j = 0; j <= k; ++j) {
            shared_ptr<q> qj(new q);
            qj->id = i + j * n;
            Q[qj->id] = qj;
        }
    }

    //cout << "doesn't fail at first loop\n";

    int l = -1;
    for (int i = 0; i < Q.size(); ++i) {
        if (i % n == 0) ++l;

        for (int j = 0; j < 10; ++j) {
            int qn = (i * 10 + j) % n + n * l;
            Q[i]->d[(char)(j+'0')].push_back(Q[qn]);
            if (l < k) {
                //cout << "i == " << i << ", l == " << l << ", k == " << k << ", q == " << i + n << endl;
                Q[i]->d[(char)(j+'0')].push_back(Q[i + n]);
            }
        }

    }
    
    //cout << "doesn't fail at second loop\n";

    S = Q[0];
    for (int i = 0; i < k+1; ++i) {
        //cout << "f" << i << " == q" << i*n << endl;
        F.push_back(i * n);
    }

    //cout << "doesn't fail at constructor\n";
}

bool NFA::nearlyDiv(string num) {
    cout << "checking divisibility\n";
    vector<char> n;
    for (int i = 0; i < num.length(); ++i) 
        n.push_back(num[i]);
    return run(n.begin(), S, n.end());
}

bool NFA::run(vector<char>::iterator head, shared_ptr<q> qi, const vector<char>::iterator &end) {
    cout << "input == " << *head << ", qi == " << qi->id << endl;
    string temp;
    getline(cin, temp);

    if (head == end) {
        if (find(F.begin(), F.end(), qi->id) != F.end()) {
            cout << "q" << qi->id << " is accepted\n";
            return true;
        }
        return false;
    }
    
    for (auto qn: qi->d[*head]) {
        cout << "q" << qi->id << " -> q" << qn->id << " on input == " << *head << endl;
        if (head+1 < end && run(head+1, qn, end))
            return true;
    }

    return false;
}

int NFA::numStates() const {
    return Q.size();
}

vector<shared_ptr<NFA::q> > NFA::states() const {
    return Q;
}

ostream &operator<<(ostream &outs, const NFA &nfa) {
    for (auto qn : nfa.Q) {
        outs << "q" << qn->id << ":\n";
        for (auto dn : qn->d) {
            outs << "\t" << dn.first << " -> ";
            for (auto qi : dn.second) {
                outs << "\tq" << qi->id;
            }
            outs << endl;
        }
    }

    return outs;
}
