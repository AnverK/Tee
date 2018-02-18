# Tee
In computing, tee is a command in command-line interpreters (shells) using standard streams which reads standard input and writes it to both standard output and one or more files, effectively duplicating its input.

# Usage
java -jar Tee.jar [-a] [-i] [File...]

ls | java -jar Tee.jar

ls | java -jar Tee.jar -a output1.txt

# Build
./app_build.sh
