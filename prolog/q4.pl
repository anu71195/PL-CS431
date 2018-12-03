% DP in prolog!!
:- dynamic(fibo/2).
%% Base case
fibo(1, 1) :- !.
fibo(0, 0) :- !.
%% Recursive case
fibo(N, Value) :-
A is N - 1, fibo(A, A1),
B is N - 2, fibo(B, B1),
Value is A1 + B1,
%% Store in knowledgebase
asserta((fibo(N, Value) :- !)).

% DP in prolog!!
:- dynamic(lucas/2).
%% Base case
lucas(1, 1) :- !.
lucas(0, 2) :- !.
%% Recursive case
lucas(N, Value) :-
A is N - 1, lucas(A, A1),
B is N - 2, lucas(B, B1),
Value is A1 + B1,
%% Store in knowledgebase
asserta((lucas(N, Value) :- !)).

% DP in prolog!!
:- dynamic(tribo/2).
%% Base case
tribo(2, 1) :- !.
tribo(1, 1) :- !.
tribo(0, 0) :- !.
%% Recursive case
tribo(N, Value) :-
A is N - 1, tribo(A, A1),
B is N - 2, tribo(B, B1),
C is N - 3, tribo(C, C1),
Value is A1 + B1 + C1,
%% Store in knowledgebase
asserta((tribo(N, Value) :- !)).

%% Example call: 
%% nTerm('Fibonacci',N,OUT) :-
%% The Menu for selecting Nth term -
nTerm(Seq,N,OUT) :-
(Seq = 'Fibonacci'
	-> fibo(N,OUT)
	; (Seq = 'Tribonacci'
		-> tribo(N,OUT)
		; (Seq = 'Lucas'
			-> lucas(N,OUT)
			; format('Invalid option: ~w',Seq), 
			fail;true
			)
		)
	).


%% Example call: 
%% nTerms('Fibonacci',N,OUT) :-
%% Press 'w' to expand the list if needed.
nTerms(Seq,N,OUT) :- 
	genTerms(Seq,N,OUT); true.

%% Selecting sequence upto N terms -
genTerms(_,0,[]) :- !.
genTerms(Seq,N,OUT) :-
N>0,
N1 is N-1,
genTerms(Seq,N1,OUT1),
(Seq = 'Fibonacci' -> fibo(N,Term)
	; (Seq = 'Tribonacci' -> tribo(N,Term)
		; (Seq = 'Lucas'  -> lucas(N,Term)
			; format('Invalid option: ~w',Seq),
			fail;true
			)
		)
	),
append(OUT1,[Term], OUT)
.
