# minicron

mini cron solution in Kotlin (1.6.10)

#### Running the solution

The solution accepts a single string argument and reads STDIN for config file contents.

Sample config file is available [here](sample_configs/input.txt).

Run MiniCron solution against a custom config file & arbitrary time:

```bash
# Compile the source files into a JAR
kotlinc src/* -include-runtime -d minicron.jar

# Execute solution against arbitrary time argument & configuration file
cat sample_configs/input.txt | java -jar minicron.jar 16:10

# Or execute by defining values in variables
CONFIG_FILE="sample_configs/input.txt"
SIMULATED_TIME="16:10"
cat "$CONFIG_FILE" | java -jar minicron.jar "$SIMULATED_TIME"
```

Run internal test cases with a custom argument `test`:

```bash
# Compile the source files into a JAR
kotlinc src/* -include-runtime -d minicron.jar

# Run internal tests
java -jar minicron.jar test
```