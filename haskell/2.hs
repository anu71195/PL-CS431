import Data.Char
import System.IO
import Data.List

-- Pure function
-- recursively calculate amt obtained from tokens
token_amt c z
    | c<z = 0 -- guards
    | otherwise = curr_amt + (token_amt (curr_amt+r) z)
    where (curr_amt,r)= quotRem c z

-- Impure: does IO 
main = do
    putStr "x: "
    input1 <- getLine
    putStr "y: "
    input2 <- getLine
    putStr "z: "
    input3 <- getLine
    -- Convert str to int
    let x = (read input1 :: Int)
    let y = (read input2 :: Int)
    let z = (read input3 :: Int)    
    -- corner case
    if y==0 || z < 1 then putStrLn ("Chhotu can get infinite parathas")
    else do
        -- calculate amt obtained from cash
        let direct_amt = (quot x y)
        -- Add amt from tokens
        let ans = direct_amt + (token_amt direct_amt z)
        putStr ("Chhotu can get ")
        putStr . show $ ans
        putStrLn (" aloo parathas" )