%% adj(1,2,1).
%% adj(2,3,1).
%% adj(3,4,1).
%% :- dynamic(visited/1).
%% route(X,X,CW,MW,MW1) :- 
%% 	(CW < MW -> MW1 is CW ; MW1 is MW).

%% route(X,Y,CW,MW,MW1) :- 
%% 	adj(X,N1,W1),
%% 	CW1 is CW + W1,
%% 	route(N1,Y,CW1,MW,MW1),
%% 	(CW1 < MW -> MW1 is CW1 ; MW1 is MW)
%% 	.
