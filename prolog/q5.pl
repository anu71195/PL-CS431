:-dynamic
	% A reversed path according to Time, Dist or Cost
	path_rev/3.

% Bus (Number, Origin, Destination Place, Departure Time, Arrival Time, Distance, Cost)
bus(1, 'Amingaon', 'Jalukbari', 14.5, 15, 10, 10).
bus(2, 'Amingaon', 'Chandmari', 16, 16.5, 7, 8).
bus(3, 'Jalukbari', 'Panbazar', 16, 16.5, 7, 8).
bus(4, 'Panbazar', 'Chandmari', 16, 16.5, 7, 8).
bus(5, 'Panbazar', 'Paltanbazar', 16, 16.5, 7, 8).
bus(5, 'Chandmari', 'Maligaon', 16, 16.5, 7, 8).
bus(5, 'Maligaon', 'Lokhra', 16, 16.5, 7, 8).

% Source and Dest would be instantiated
edge(Source, Dest, Time, 'time') :-
	bus(_, Source, Dest, T1, T2, _, _), 
	Time is T2-T1.

edge(Source, Dest, Dist, 'dist') :-
	bus(_, Source, Dest, _, _, Dist, _).

edge(Source, Dest, Cost, 'cost') :-
	bus(_, Source, Dest, _, _, _, Cost).

% edge(Source, Dest, Cost, Type).
path(From, To, Dist, Type) :- edge(To, From, Dist, Type).
path(From, To, Dist, Type) :- edge(From, To, Dist, Type).

% path < stored path? replace it
shorterPath([H|Path], Dist, Type) :-		       
	% match target node [H|_]
	path_rev([H|T], D, Type), !, Dist < D, T is T,
	retract(path_rev([H|_], _, Type)), 
	%% writef('(%w < %w) %w is closer than %w\n', [Dist, D, [H|Path], [H|T]]), 
	assert(path_rev([H|Path], Dist, Type)).

% Otherwise store a new path
shorterPath(Path, Dist, Type) :-		       
	%% writef('New path:%w\n', [Path]), 
	assert(path_rev(Path, Dist, Type)).
 
% traverse all reachable nodes
traverse(From, Path, Dist, Type) :-		    
	% For each neighbor
	path(From, T, D, Type), 		    
	% which is unvisited
	not(memberchk(T, Path)), 	    
	Dist1 is Dist+D,
	% Update shortest path and distance
	shorterPath([T, From|Path], Dist1, Type), 
	% Then traverse the neighbor
	traverse(T, [From|Path], Dist1, Type).	    
 
traverse(From, Type) :-
	% Remove solutions
	retractall(path_rev(_, _, Type)),           
	% Traverse from origin
	traverse(From, [], 0, Type).              

traverse(_, _).
 
route(From, To) :-
	routeType(From, To, 'time'), 
	routeType(From, To, 'dist'), 
	routeType(From, To, 'cost').

routeType(From, To, Type) :-
	% Find all distances
	traverse(From, Type),                   
	% If the target was reached
	path_rev([To|Rest], Dist, Type)
	-> (
	  % Report the path and distance
	  % Distance is round(Dist), 
	  reverse([To|Rest], Path),      
	  writef('Optimum path is %w with %w = %w\n', [Path, Type, Dist])
	  )
	; writef('There is no route from %w to %w\n', [From, To])
	.
