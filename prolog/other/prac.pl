fact(p1).
fact(p2).
fact(spl).
tauto(X).

reln(p1,p2).
reln(p2,pred3).
reln(pred3,pred4).
reln(pred4,spl).

stat_condn :- 
	fact(p1),
	fact(p2).

print_fmt(X) :-
	format('~s ~w', ["X is: ",X]).

var_rule(spl,X) :-
	write('spl inputted'),
	reln(spl,X).

var_rule(X,Z) :-
	reln(X,Y),
	reln(Y,Z).

vertical(line(point(X, Y),point(X, Y2))).
horizontal(line(point(X, Y),point(X2, Y))).

not_equal(X,Y) :-
	\+ (X = Y),
	(X \= Y).

equal(X,Y) :-
	not(not_equal(X,Y)).

recur(X,BASE) :-
	reln(X,BASE).

recur(X,Y) :-
	reln(X,Z),
	recur(Z,Y).

is_odd(X) :-
	T is mod(X,2),
	T =:= 1.

is_even(X) :-
	Y is X//2,
	X =:= 2*Y.

read_str :-
	read(X),
	write('Read '),
	X.
	
write_to_file(File, Text) :-
	open(File, write, Stream),
	write(Stream, Text),
	close(Stream).

read_file(File) :-
	open(File, read, Stream),
	\+ read_chars(Stream),
	close(Stream).

isEOF(end_of_file) :-
	!.

read_chars(Stream) :-
	get_char(Stream, Curr),
	\+ isEOF(Curr),
	write(Curr),
	read_chars(Stream).

read_char :-
	get(X),
	write('Read '),
	X.

concat_str(X,Y) :-
	name(Y, T1),
	name(X, T2),
	append(T1, T2, T),
	name(Z,T).

% checkSub(X,Y,L1,L2) :-
checkSub(X,Y) :-
	% length Y >= X
	(X == Y);
	[ H | T ] = Y,
	[ H | T ] = Y
		.
	%  need to write 2d loop


isSubstr(X,Y) :-
	%  convert to lists
	name(Y, T1),
	name(X, T2),
	% get lengths
	length(X,L1),
	length(Y,L2),
	% if-else-then from http://cs.union.edu/~striegnk/learn-prolog-now/html/node89.html
	(L1 < L2) -> checkSub(X,Y); checkSub(Y,X).
	% (L1 < L2) -> checkSub(X,Y,L1,L2); checkSub(Y,X,L2,L1).	


% getLIS(X) :-
% 	1.
	%  there should be a simpler way

% isSubset(X,Y) :-
% 	subset(X,Y).

% spl case
isSubset([], _).
% normal case
isSubSet(Substring, String) :-
    firstMatch(Substring, String, RestString),
    strings_match(Substring, RestString).


%  CHECK FOR EQUALITY VIA ARGUMENTS!

% spl case
firstMatch([], String, String).
% base case
firstMatch([H|T], [H|T1], [H|T1]).
% recursion step
firstMatch(Substring, [_|T], RestString) :-
    firstMatch(Substring, T, RestString).

% base case
strings_match([], _).
% prolog way of checking first chars ==> "FINDS MATCHING CRITERIA!"
strings_match([H|T], [H|T1]) :-
    strings_match(T, T1).	

substring(X,S) :-
  append(_,T,S) ,
  append(X,_,T) ,
  X \= []
  .

a2b([],[]).
% a2b([a|Ta],[b|Tb]) :- a2b(Ta,Tb).
a2b([A|Ta],[B|Tb]) :- A==B, a2b(Ta,Tb).

% CPD stds
% init :- Counter = 0.
% incr :- succ(Counter,CounterNext).
% getcount(V) :- getval(Counter,V).

word_reps([],Word,Word-0).
word_reps([W|Rest],Word,Word-Reps) :-
	word_reps(Rest, Word, Word-Reps0),
	( W == Word -> Reps is Reps0 + 1 ; Reps = Reps0).

countFirst(Char, [F | Rest], Count0, Output1, Output):-	
	% LHS needs to be uninstantiated for assignment to work!
	Char = F -> (Count is Count0 + 1, countFirst(F,Rest,Count, Output1, Output)) ; (Output0 = [Count,F], append(Output1,Output0,Output)).

encode(Input, Output) :-
	Count is 0,
	name(Input, X),
	[F | Rest] = X,
	Output0 = [],
	countFirst(F, Rest, Count, Output0, Output)	
	.
