import Data.Char
import System.IO

-- Pure function
min_num n arr idx = min (arr!!idx) (arr!!(n - 1 - idx))

-- Pure function
get_pallin n arr=do
    map (min_num n arr) idx_range
    where idx_range=[0..(n-1)]

-- Pure function
find_pallin arr=do
    let n=length arr
    get_pallin n arr

-- Pure function
char_diff a b=abs(ord a - ord b)

-- Pure function
sumcost arr pallindrome = sum(zipWith char_diff arr pallindrome)

-- Impure: does IO 
main=do
    putStr "Input: "
    arr<-getLine
    let pallindrome=find_pallin arr
    putStr "Output: "
    print (sumcost arr pallindrome)
    putStr "Pallindrome: "
    putStrLn pallindrome
