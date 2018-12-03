substring(X_,S_) :-
%convert string to list
string_codes(X_,X),
string_codes(S_,S),
%% Find some suffix T of string S
append(_,T,S) ,
%% Find prefix X of string T
append(X,_,T) ,
%% If X is non empty, it is a substring of S.
X \= []
.

%% Example call: 
% substring('abcde','abcde').