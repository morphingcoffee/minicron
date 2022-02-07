# minicron

mini cron solution in Kotlin

#### Running the solution

Tested against Kotlin 1.6.10

Experienced issues with concatenating given time STDIN argument with `cat`ted STDIN, workaround below didn't help as
well:

```bash
TEST_FILE="sample_configs/provided.txt"
GIVEN_TIME="16:10"
(echo $GIVEN_TIME ; cat $TEST_FILE) > test.txt
cat test.txt | (kotlinc src/solution.kt -include-runtime -d solution.jar) && java -jar solution.jar
```

If time value is removed from parsing & hard-coded in source code, the following can be used to execute the solution:

```bash
cat configfile.txt | (kotlinc src/solution.kt -include-runtime -d solution.jar) && java -jar solution.jar  
```