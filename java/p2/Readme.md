## How to run

	javac DataMod.java
	java DataMod

### Problem Statement (Completed)

#### File Modification
[Read full problem statement here](https://github.com/Udayraj123/PL-CS431/blob/master/java/Concurrent%20Programming%20Assignment.pdf)

File entries are made based on the following assumptions.

Assumptions:

1. The file can be updated by TA1, TA2 or CC.

2. CC is having higher priority than TA1 and TA2.

3. TA1 and TA2 is having equal priorities, i.e. If TA1 is modifying a field of a record, it can be modified by TA2 later and vice versa.

4. If CC is modifying any data, that can only be modified by CC later and not by any TAs.

5. The file can be assessed by TA1, TA2 or CC simultaneously.

6. There can be negative marks for students.

You are asked to write a Java program for the evaluation system. The system should be able to perform the following.

a) Allow the CC, TA1 and TA2 to update the Stud_Info.txt.

b) Should be able to generate a file containing all the students data in sorted order based on the Roll No. Call this file Sorted_Roll.txt.

c) Should be able to generate a file containing all the students data in sorted order based on the Name. Call this file Sorted_Name.txt.


Your program should allow the updating of Stud_Infor.txt at two levels: record level and file level.

a) Record level modification example: If the mark of Amit Kumar Sharma is modified from 75 to 80 by TA2 and TA1 want to decrease the mark by 3, the sorted files should contain the 77 mark for Amit.

b) File level modification example: If the mark of Kunal Kishore is modified from 67 to 70 by TA1 and TA2 want to decrease the value by 5 mark for Savnam (88 to 83), both should be reflected in the sorted files.
