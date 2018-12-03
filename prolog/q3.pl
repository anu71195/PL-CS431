%% Q3 
% Sample1 = '555555577777777199999111111'.
% Sample2 = 'hhhhhhhhhhpppppppkoooooooooooo'.

encode(S,Out) :- 
%% Convert string to list 
string_codes(S,X),
%% take out first char
[C1 | T] = X,
%% initialize counter
N is 1,
write('['), helper(C1,T,N,Out), write(']'),
!.

encode_file :-
	open('sample_input.txt',read,Stream),
	% Read a line, stripping leading and trailing white space
	read_string(Stream, "\n", "\r\t ", End, S),
	encode(S,X),
	write_to_file('encoded_output.txt',X).

%% File IO	
write_to_file(File, Text) :-
	open(File, write, Stream),
	write(Stream, Text),
	close(Stream).

%% end case
helper(C1, [],N,Out):-
char_code(Ch,C1),
(
	N is 1
	-> (
		Out = [Ch]
		, format(' ~w ',[Ch])
		)
	; (
		Out = [[N,Ch]]
		, format(' [~w, ~w] ',[N,Ch])
		)
	)
.

helper(C1, [C1 | T],N,Out) :-
%% incr counter
N1 is 1+N,
helper(C1,T,N1,Out).

helper(C1, [C2 | T],N,Out) :-
%% convert int to char
char_code(Ch,C1),
%% check if counter is 1
(N is 1 -> format(' ~w ',[Ch]) ; format(' [~w, ~w] ',[N,Ch])),
N1 is 1,
helper(C2,T,N1,Out1),
(N is 1 -> append([Ch],Out1,Out) ; append([[N,Ch]],Out1,Out))
.


%% ROUGH Extra 
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
