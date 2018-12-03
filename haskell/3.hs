import System.IO
import Text.Read

--pure function
minMoves x=((sum x) - (length x)*(minimum x)) 

--impure, uses IO
readSalaries = do --impure, uses impure  function
    list <- fmap ((map (read::String->Int)).words) getLine --readLs
    return list    

main = do --impure, uses IO
putStr "Enter salaries: "
salList <- readSalaries
putStr "Result: "
print (minMoves salList)