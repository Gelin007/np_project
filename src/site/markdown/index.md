Congratulations, you managed to build and display the "maven site" for the NP
project 2016. The project is called the "CCS and Critical Section Explorer".
Below, you will find the technical project documentation.

We wish you good luck and have fun!

Your NP team 2016

## What is the CCS and Critical Section Explorer?

Well, great question! By this time you should be familiar with the functionality
of [pseuCo.com](https://pseuco.com), specifically with its ability to explore
the LTS of a given CCS or CCS-vp program. The main goal of this project is to recreate
this functionality. Because this course is all about concurrency your task is
not only the simple recreation but rather to do so using concurrency. In
contrast to pseuCo.com this project only supports CCS and not CCS-vp as its
input.

The project consists of two major parts. Only the first one is required to pass the project.

### Assignment 1: CCS Exploration

As stated above, your task is to implement an explorer for CCS programs that
constructs the corresponding LTS. The project skeleton provides you with most of
the implementation for this problem. Only the concurrent graph exploration and
construction is missing. What does this mean?

#### CCS to LTS transformation in general

Getting the LTS representation of a CCS program involves several steps:

1. Parse the CCS program (i.e. parse all recursion variable definitions and the
   CCS term). This yields an AST (abstract syntax tree) representation of the
   main CCS term and all the defining terms. Nodes in this tree correspond to
   operators in the CCS term (e.g. a "`ChoiceExpression`" node relates to a
   "`expr + expr`" term).

2. Implement a Post function for CCS expressions. The Post function is passed a CCS
   expression for which it returns a set of CCS expressions together with the
   transition labels needed to deduce the respective CCS expression.

3. Considering the resulting CCS semantics as an implicit graph representation,
   perform a graph search to explore and store all reachable CCS expressions together with the transitions connecting them.

6. If terminated the globally stored information forms an explicit
   representation of the graph – an LTS to be precise – associated with the CCS program.

#### Java concurrent implementation details

This skeleton implements all of the 6 steps above for you, but the
implementation is sequential. Your task is to make step 3 concurrent.

We use a BFS (breadth-first-search) for our sequential graph search. In
Lecture 10 you saw a concurrent graph search utilizing message passing principles. As Java has
no direct support for message passing you will need to develop a concurrent graph search working
with shared memory. Use this search to explore the implicit graph and construct the explicit one.

Your concurrent graph search must utilize the value of the `--threads` program
option. You can access that via `Options.THREADS.getNumber()` anywhere in your
program (but the search (or its constructor call) appears to be the best choice).
This value specifies how many threads are to share the load of concurrent searching (i.e.
threads that merely organize the serach, such as the main thread, do not count). Furthermore, these threads must be started before graph search starts and must terminate once the current graph search finishes.

Unfortunately, our Post function implementation is too fast to see an immediate
improvement compared to the sequential solution of this problem. Synchronization
operations consume more time than the sequential search needs to compute the
Post function. You can use the `--delay` option to overcome this issue and
artificially slow down the Post function computations. This will help you
determine whether your concurrent search is indeed faster than the sequential
search given that computing the Post function represents most of the work.

#### Output

We provide you with a data structure to store the LTS. This data structure is
able to build [a JSON representation of this LTS](https://secure.fefrei.de/redmine/projects/concurrent-programming-web/wiki/Lts-json-specification) which then can be visualized on
pseuCo.com. Normally this JSON string would be printed on your command line. If
you specify the `--view-online` option this behavior is overridden and the LTS
is directly uploaded to pseuCo.com and a browser window is opened to display the
file.

### Assignment 2: Critical Section exploration and counterexample generation

This assignment is optional and only required if you attempt to get a bonus.
Your task here is to verify a very interesting property:

> "Every critical section that was *entered* with an `enter` action is also
> *left* with an `exit` action."

For this assignment you are given a CCS program as well. You have to examine the
underlying CCS semantics (i.e. the LTS) in order to verify this property.

#### Output

There are four possible outcomes:

1. You do not attempt this assignment. Then you should print
   "`NOT␣IMPLEMENTED!`", provided the `--critical-option` is set.

2. Each `enter` action is eventually followed by an `exit` action. In this case, the 
   above property is satisfied. So you should print "`OK!`", provided the
   `--critical-option` is set.

3. There is at least one path where some `enter` action is never followed by an `exit` action. Then you should print "`NOT␣OK!`", provided the `--critical-option` is set and the `--counterexample` flag is not set.
   
4. There is at least one path where some `enter` action is never followed by an `exit` action. Then you should print "`NOT␣OK:␣<counterexample>`",
provided the `--critical-option` and the `--counterexample` flag are set.
Here `<counterexample>` should be replaced by
   the concrete counterexample that proves your statement. The exact
   counterexample output specification is listed below.

#### Counterexample output specification

Since we can assume the underlying LTS to be finite, any counterexample must be of the following form: 

- a finite path fragment originating in the initial state and  containing at least one action "`enter`" that is not followed by an action action  "`exit`"  
- followed by either a terminal state or a loop not containing action  "`exit`"  

These parts should be separated by "`.`".

##### Finite path fragment

The finite path fragment is a sequence of pairs of states and actions for which at least one action is "`enter`", and this action is not followed by any "`exit`" action.

##### Terminal states

A terminal state does not have successor states. Reaching a terminal state after entering a critical section effectively prevents leaving it. 

To indicate a terminal state, just output:

```
(expr)
```

##### Loop

A loop is a sequence of pairs of states and actions in which no "`exit`" action occurs. Repeating such a loop forever effectively prevents leaving a critical section entered before. 

##### Pairs of states and actions

A pair of states and actions represents a single transition. This transition starts at the
specified state and is labeled with the specified action. The target of the transition is enclosed in the next pair of states and actions along the sequence (to be precise: the target is the state therein, or it is the subsequent terminal state). Printed state, action pairs should look like:

```
(expr,action)
```

For achieving this output you should use

```
State s = transition.getStart();
CCSExpression e = s.getInfo();
Action a = transition.getInfo();
String.format("(%s,%s)", e.toString(), a.toString());
```
to format these pairs (`transition` is the instance of the `Transition` class
for which you want to print the pair).


##### Complete example

Here are two complete counterexamples showing the output format.

- Terminal state:

```
(enter.0,enter).(0)
```

- Loop:

```
(enter.X,enter).(X,a)
```

### Assignment 1 + 2

Your program may be called with more than one input CCS and/or flags set for
both assignments (i.e. `--lts` and `--critical-section`). We expect you to
output your results as follows:

For each file print

```
<filename>:
<results assignment 1>
<results assignment 2>
```

The provided  skeleton does exactly this. Nevertheless, here are some
abbreviated example outputs:

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts example.ccs`

```
example.ccs:
{"initialState":"[...]",[...]}
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --critical-section example.ccs`

```
example.ccs:
NOT IMPLEMENTED!
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts example1.ccs example2.ccs`

```
example1.ccs:
{"initialState":"[...]",[...]}
example2.ccs:
{"initialState":"[...]",[...]}
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts --critical-section example.ccs`

```
example.ccs:
{"initialState":"[...]",[...]}
NOT IMPLEMENTED!
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts --critical-section example_ok.ccs`

```
example_ok.ccs:
{"initialState":"[...]",[...]}
OK!
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts --critical-section example_not_ok.ccs`

```
example_not_ok.ccs:
{"initialState":"[...]",[...]}
NOT OK!
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts --critical-section --counterexample example_not_ok.ccs`

```
example_not_ok.ccs:
{"initialState":"[...]",[...]}
NOT OK: (expr,act)(expr,act)[...](expr,act).(expr,act)[...](expr,act)
```

- `java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts --critical-section example_ok.ccs example_not_ok.ccs`

```
example_ok.ccs:
{"initialState":"[...]",[...]}
OK!
example_not_ok.ccs:
{"initialState":"[...]",[...]}
NOT OK!
```

## Declare your team

Before you start any programming, please open the `pom.xml` file and adjust the
developers section:

```
...
<developers>
    <developer>
        <id>2541234</id>
        <name>Student 1</name>
        <email>s9blabla@stud.uni-saarland.de</email>
        <roles>
            <role>Developer</role>
        </roles>
    </developer>
    <developer>
      ...
    </developer>
    ...
</developers>
...
```

For each of your team members add a `developer` declaration and substitute the
appropriate values for `id` (matriculation number), `name` and `email`.

## Compilation and Usage

Now that you known what your task is and declared all your team members, let's
review how to build and run your implementation.

### Command line

Start by navigating a terminal to the root directory of the project. The site
you are looking at right now can be built using

```
mvn site:run
```

This will not only build the site but also start a little server connected to
port 8080. Use your browser and navigate to http://localhost:8080 to
view this page.

To build the project implementation itself using the command line type

```
mvn package assembly:single
```

This results in an executable JAR file. Use

```
java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --help
```

to start the execution with the `--help` flag. If everything worked so far this
should yield the following output

```
java [-ea] -jar <path to jar> [OPTION]... [FILE]...

Options:
    [FLAGS]
    --counterexample   : Prints a counterexample if the "critical section" condition is not met. [requires --critical-section]
    --critical-section : Checks whether the "critical section" condition is met.
    --help             : Prints this message.
    --lts              : Prints the explored LTS on standard output.
    --view-online      : Opens the explored LTS on pseuCo.com (does not print the LTS). [requires --lts]

    [NUMBER ARGUMENTS]
    --delay <number>   : Specifies how long the CCS semantics is delayed before returning the transitions for "prefix".
    --threads <number> : Specifies the number of threads the program shall use.
```

The message explains how to use the program:

You use `java` with the `-jar` option and a path to the executable JAR file to
execute your program. Following the JAR file you specify the options for your
program. This is done by specifying a whitespace separated list of options.
Finally, after the list of options you pass the names of the input files. You
can use `--` to end the list of options if you want to use weirdly named files:

```
java -jar target/assembly/CCSExplorer-0.1-jar-with-dependencies.jar --lts -- --argument-parser-horror-file.ccs
```

There is a `[-ea]` in the above output. This is an additional option for the JRE
and enables assertions. You are hereby encouraged to use assertions wherever
appropriate and execute your program using

```
java -ea -jar ...
```

for them to take effect. The provided skeleton uses them at several locations to
ensure that the actual and the expected state do not diverge.

#### Program option overview

| Option               | Type   | Description                                                                                                                                                                                                                                                                                                                                           |
|----------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `--help`             | Flag   | Given this option the program will print a help message with all important information on how to use it and exit.                                                                                                                                                                                                                                     |
| `--lts`              | Flag   | This flag is used to execute the solution of assignment 1. The results are printed on the console labeled with their input file name.                                                                                                                                                                                                                 |
| `--view-online`      | Flag   | Requires `--lts` to be set. Changes the output behavior of `--lts`. The results are no longer printed on the console. They are uploaded to pseuCo.com and a window is opened to display the LTS view.                                                                                                                                                 |
| `--critical-section` | Flag   | This flag is used to execute the solution of assignment 2. If you do not attempt to solve assignment 2 "`NOT IMPLEMENTED!`" should be printed. Otherwise simply print "`NOT OK!`" if there is a path where a critical section is not left and "`OK!`" if not.                                                                                        |
| `--counterexample`   | Flag   | Requires `--critical-section` to be set. Additionally, prints the counterexample in case there is one (i.e. instead of "`NOT OK!`" print "`NOT OK: <counterexample>`"). For the output format please refer to the specification of the assignment.                                                              |
| `--delay`            | Number | Requires a numerical argument. The argument is used to delay the "prefix" transition generation. We use this to artificially slow down the CCS semantics. This comes in handy in case you want to see whether you concurrent solution of assignment 1 is faster than the sequential implementation.                                                   |
| `--threads`          | Number | Requires a numerical argument. The argument is used to determine how many "worker" threads shall be used in the concurrent graph search from assignment 1. A worker thread is a thread that is actually involved in the graph search. Threads used for organization and synchronization purposes (such as the main thread) do not count. |

### Other development tools

There are many integrated-development-environments (IDEs) out there.

We have tested building and running the project in Eclipse.
Feel free to [ask us](https://discourse.pseuco.com/) if you have questions specific to developing the project in Eclipse.

You can also use any other development tool to simplify your process of developing and testing your project, although we may not be able to help you if you run into issues with your IDE. Most of them are able to handle Maven projects properly. Please consult the respective user manuals and online sources on how to integrate this Maven project.


## Project JavaDoc

As this project comes with a rather large skeleton you will find the JavaDoc
quite useful. It is located [here](apidocs/index.html). We highly recommend you
read or at least skim through it.
