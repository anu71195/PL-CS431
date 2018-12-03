lis(In, Out) :-
% we ask Prolog to find the longest sequence
aggregate(max(N,Is), (inc_seq(In, [], Is), length(Is, N)), max(_, Res)),
reverse(Res, Out).

% we describe the way to find increasing subsequence
inc_seq([], Current, Current).

inc_seq([H | T], Current, Final) :-
(   Current = [], inc_seq(T, [H], Final));
(   Current = [H1 | _], H1 < H,   inc_seq(T, [H | Current], Final));
inc_seq(T, Current, Final).
