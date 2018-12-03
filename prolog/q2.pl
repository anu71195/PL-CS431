% AllPeople = ['Jatin','Payal','Pawan','Sheetal','Amit','Lucky','Priya','Suchi'].
married('Payal','Jatin').
married('Jatin','Payal').
married('Pawan','Sheetal').
married('Sheetal','Pawan').
married('Lucky','Priya').
married('Priya','Lucky').
married('Suchi','Amit').
married('Amit','Suchi').


loves('Payal','Pawan').
loves('Amit','Payal').
loves('Lucky','Payal').
loves('Jatin','Priya').
loves('Suchi','Pawan').

% Query1: A marriage is on the rocks if both its participants are in love with other people and not with each other. 
onrocks(X,Y) :-
	married(X,Y), loves(X,Z), loves(Y,W), 
	X @< Y,
	format('Marriage of ~w & ~w is on the rocks. As ~w loves ~w, and ~w loves ~w.\n',[X,Y,X,Z,Y,W]).

query1 :-
	onrocks(_,_),
	%% Flush output
	fail;true.

% Query2:  A person is jealous when a person they love is loved by a third person, or a person is jealous when married to someone loved by a third person.
:- dynamic(already_q2/1).
jealous(X,Y) :-
	married(X,Y), loves(Z,Y),
	\+ already_q2(X), asserta(already_q2(X)),
	format('~w is jealous. Because he/she is married to ~w, but ~w is loved by ~w.\n',[X,Y,Y,Z]).

query2 :-
	%% reset the predicate (on re-runs)
	retractall(already_q2(_)),
	jealous(_,_), 
	%% Flush output
	fail;true.

% Rough
% (X\=Y) not needed as loves and married cannot be with themselves.

% seen_answer(a,a).
% \+ seen_answer(X,Y), jealous(X,Y), assert(seen_answer(X,Y)), assert(seen_answer(Y,X)).

% Produce unique results in order. https://www.csee.umbc.edu/~finin/prolog/sibling/siblings.html
	%% setof((X,Y), onrocks(X,Y), Pairs),
	%% member((X,Y),Pairs).
