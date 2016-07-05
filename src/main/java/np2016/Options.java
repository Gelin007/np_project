package np2016;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parser and global storage for program options. Is also capable of printing a
 * help message containing all listed program options.
 * <p>
 * Depending on the option type (flag, number or string) the value of the option
 * can accessed anywhere in the program using {@code Options.<NAME>.<getter>()}.
 * Replace {@code <NAME>} with one of the fields of this enumeration. For flags
 * substitute {@code <getter>} with {@link #isSet} to check whether the flag was
 * set. The getter for number and string options are {@link #getNumber} and
 * {@link #getString}, respectively.
 * <p>
 * If you want to access the value of {@link #THREADS} for example you use
 *
 * <pre>
 * {@code
 * Options.THREADS.getNumber()
 * }
 * </pre>
 */
public enum Options {
    /*
     * flags
     */
    /**
     * If set the program should print a help message and exit successfully
     * without any other action.
     * <p>
     * Default value: {@code false}
     */
    HELP("help",
            false,
            "Prints this message."),

    /**
     * If set the program should execute the CCS exploration (i.e. assignment 1)
     * and print the explored LTS as JSON string on the command line.
     * <p>
     * Default value: {@code false}
     */
    LTS("lts",
            false,
            "Prints the explored LTS on standard output."),

    /**
     * Requires {@link #LTS} to be set. If set alters the behavior of
     * {@link #LTS}. The results are no longer printed on the command line but
     * sent to pseuCo.com and a browser window opens to show the uploaded file.
     * <p>
     * Default value: {@code false}
     */
    VIEW_ONLINE("view-online",
            false,
            "Opens the explored LTS on pseuCo.com (does not print the LTS). " +
            "[requires --lts]"),

    /**
     * If set the program should execute the critical section exploration (i.e.
     * assignment 2) and print whether the condition (specified by the
     * assignment) is met. Prints {@code "OK!"} if the condition is met and
     * {@code "NOT OK!"} otherwise. If assignment 2 is not solved
     * {@code "NOT IMPLEMENTED!"} is printed.
     * <p>
     * Default value: {@code false}
     */
    CRITICAL_SECTION("critical-section",
            false,
            "Checks whether the \"critical section\" condition is met."),

    /**
     * Requires {@link #CRITICAL_SECTION} to be set. Additionally, prints a
     * counterexample that shows that the condition is met. The format is
     * specified by assignment 2.
     * <p>
     * Default value: {@code false}
     */
    COUNTEREXAMPLE("counterexample",
            false,
            "Prints a counterexample if the \"critical section\" condition " +
            "is not met. [requires --critical-section]"),

    /*
     * arguments expecting a number
     */
    /**
     * Specifies the number of worker threads for assignment 1. If set to
     * {@code 0} the sequential solution is executed.
     * <p>
     * Default value: {@code 2}
     */
    THREADS("threads",
            2,
            "Specifies the number of threads the program shall use."),

    /**
     * Specifies how long the CCS semantics is delayed before returning the
     * transitions for "prefix". This is especially useful for testing the
     * concurrent solution.
     * <p>
     * Default value: {@code 0}
     */
    DELAY("delay",
            0,
            "Specifies how long the CCS semantics is delayed before " +
            "returning the transitions for \"prefix\".");

    /*
     * arguments expecting a string
     *
     * NONE
     */

    /*
     * global state
     */
    /**
     * Stores all options of type {@code FLAG} in lexicographic order.
     */
    private static Map<String, Options> flags = new TreeMap<>();

    /**
     * Stores all options of type {@code NUMBER} in lexicographic order.
     */
    private static Map<String, Options> numberArguments = new TreeMap<>();

    /**
     * Stores all options of type {@code STRING} in lexicographic order.
     */
    private static Map<String, Options> stringArguments = new TreeMap<>();

    /**
     * Stores all specified input (CCS) files.
     */
    private static List<String> files = new ArrayList<>();

    /**
     * Length of the strings " &lt;NUMBER&gt;" and " &lt;STRING&gt;".
     */
    private static final int ARG_STR_LENGTH = 9;

    // static initializations
    static {
        for (final Options o : Options.values()) {
            switch (o.type) {
            case FLAG:
                Options.flags.put(o.name, o);
                break;
            case NUMBER:
                Options.numberArguments.put(o.name, o);
                break;
            case STRING:
                Options.stringArguments.put(o.name, o);
                break;
            default:
                // Make sure to change this if additional types are introduced.
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Stores the name of an option.
     */
    private final String name;

    /**
     * Stores the type of an option.
     *
     * @see Options.Type
     */
    private final Type type;

    /**
     * Stores a short description of an option.
     */
    private final String description;

    /*
     * state
     */
    /**
     * Used for options of type {@code FLAG} to store their value.
     */
    private boolean booleanValue;

    /**
     * Used for options of type {@code NUMBER} to store their value.
     */
    private int intValue;

    /**
     * Used for options of type {@code STRING} to store their value.
     */
    private String stringValue;

    /**
     * Constructs a {@code FLAG} option.
     *
     * @param name
     *            name of the option.
     * @param value
     *            default value of the option.
     * @param description
     *            short description of the option.
     */
    Options(final String name, final boolean value, final String description) {
        this.name = name;
        this.type = Type.FLAG;
        this.booleanValue = value;
        this.description = description;
    }

    /**
     * Constructs a {@code NUMBER} option.
     *
     * @param name
     *            name of the option.
     * @param value
     *            default value of the option.
     * @param description
     *            short description of the option.
     */
    Options(final String name, final int value, final String description) {
        this.name = name;
        this.type = Type.NUMBER;
        this.intValue = value;
        this.description = description;
    }

    /**
     * Constructs a {@code STRING} option.
     *
     * @param name
     *            name of the option.
     * @param value
     *            default value of the option.
     * @param description
     *            short description of the option.
     */
    Options(final String name, final String value, final String description) {
        this.name = name;
        this.type = Type.STRING;
        this.stringValue = value;
        this.description = description;
    }

    /**
     * Returns the list of the specified input (CCS) files.
     *
     * @return list of the specified input (CCS) files.
     */
    public static List<String> getFileNames() {
        return Collections.unmodifiableList(files);
    }

    /**
     * Parses the command line arguments and sets the option values accordingly.
     *
     * @param args
     *            command line arguments as given to main method.
     * @return false if an error occurred while parsing the arguments.
     */
    static boolean parseArguments(final String[] args) {
        boolean parseFileNames = false;
        for (int i = 0; i < args.length; ++i) {
            if (!parseFileNames && !args[i].startsWith("--")) {
                parseFileNames = true;
            }

            if (parseFileNames) {
                files.add(args[i]);
                continue;
            }

            String optionName = args[i].substring(2);
            if (optionName.length() == 0) {
                parseFileNames = true;
                continue;
            }

            // is flag option?
            if (flags.keySet().contains(optionName)) {
                flags.get(optionName).set(true);
                if ("help".equals(optionName)) {
                    return true;
                }
                continue;
            }

            // is number option?
            if (numberArguments.keySet().contains(optionName)) {
                try {
                    numberArguments
                    .get(optionName)
                    .set(Integer.parseInt(args[++i]));
                } catch (NumberFormatException e) {
                    System.err.println(
                            String.format("\"%s\" is not a valid number!",
                                    args[i])
                            );
                    return false;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(String.format(
                            "No argument specified (%s)!",
                            args[i - 1]
                            ));
                    return false;
                }
                continue;
            }

            // is string option?
            if (stringArguments.keySet().contains(optionName)) {
                try {
                    stringArguments.get(optionName).set(args[++i]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(String.format(
                            "No argument specified (%s)!",
                            args[i - 1]
                            ));
                    return false;
                }
                continue;
            }

            System.err.println(String.format(
                    "Unrecognized option (%s)!",
                    args[i]
                    ));
            return false;
        }

        return checkArgumentValidity();
    }

    /**
     * Verifies that all arguments are in range and all requirements are met.
     *
     * @return true if all conditions are satisfied.
     */
    private static boolean checkArgumentValidity() {
        if (THREADS.getNumber() < 0) {
            System.err.println("The number of threads must be non-negative!");
            return false;
        }

        if (DELAY.getNumber() < 0) {
            System.err.println("The delay must be non-negative!");
            return false;
        }

        if (VIEW_ONLINE.isSet() && !LTS.isSet()) {
            System.out.println("If \"--view-online\" is specified \"--lts\" " +
                    "must be given as well!");
            return false;
        }

        if (COUNTEREXAMPLE.isSet() && !CRITICAL_SECTION.isSet()) {
            System.out.println("If \"--counterexample\" is specified " +
                    "\"--critical-section\" must be given as well!");
            return false;
        }

        return true;
    }

    /**
     * Calculates the length of the longest option name. This value is used to
     * line up all option descriptions. Accounts for the fact that number and
     * string options print more text than just the name of the option.
     *
     * @return the length of the longest option name.
     */
    private static int optionsNameLength() {
        int length = 0;

        for (final Options o : Options.values()) {
            int tmp = o.name.length();

            if (o.type == Type.NUMBER || o.type == Type.STRING) {
                tmp += ARG_STR_LENGTH;
            }

            if (tmp > length) {
                length = tmp;
            }
        }

        return length;
    }

    /**
     * Prints a short overview on how to use the program and which options may
     * be specified by the user.
     */
    static void printUsageInformation() {
        StringBuilder s = new StringBuilder();
        String indent = "    ";
        int length = optionsNameLength() + 2;

        s.append("java [-ea] -jar <path to jar> [OPTION]... [FILE]...\n\n");
        s.append("Options:\n");

        if (Options.flags.size() > 0) {
            s.append(String.format("%s[FLAGS]\n", indent));
            for (final Options o : Options.flags.values()) {
                int diff = length - o.name.length();
                s.append(String.format(
                        "%s--%s%" + diff + "s %s\n",
                        indent, o.name, ":", o.description
                        ));
            }
        }

        if (Options.numberArguments.size() > 0) {
            s.append(String.format("\n%s[NUMBER ARGUMENTS]\n", indent));
            for (final Options o : Options.numberArguments.values()) {
                int diff = length - ARG_STR_LENGTH - o.name.length();
                s.append(String.format(
                        "%s--%s <number>%" + diff + "s %s\n",
                        indent, o.name, ":", o.description
                        ));
            }
        }

        if (Options.stringArguments.size() > 0) {
            s.append(String.format("\n%s[STRING ARGUMENTS]\n", indent));
            for (final Options o : Options.stringArguments.values()) {
                int diff = length - ARG_STR_LENGTH - o.name.length();
                s.append(String.format(
                        "%s--%s <string>%" + diff + "s %s\n",
                        indent, o.name, ":", o.description
                        ));
            }
        }

        System.out.print(s.toString());
    }

    /**
     * Returns the name of the option.
     *
     * @return the name of the option.
     */
    String getName() {
        return this.name;
    }

    /**
     * Returns the value of the option. The option type must be {@code FLAG}.
     *
     * @return true if the flag is set.
     */
    public boolean isSet() {
        assert this.type == Type.FLAG;

        return this.booleanValue;
    }

    /**
     * Returns the value of the option. The option type must be {@code NUMBER}.
     *
     * @return the value of the argument.
     */
    public int getNumber() {
        assert this.type == Type.NUMBER;

        return this.intValue;
    }

    /**
     * Returns the value of the option. The option type must be {@code STRING}.
     *
     * @return the value of the argument.
     */
    public String getString() {
        assert this.type == Type.STRING;

        return this.stringValue;
    }

    /**
     * Returns the description of the option.
     *
     * @return the description of the option.
     */
    String getDescription() {
        return this.description;
    }

    /**
     * Sets the value of the option as specified. The option type must be
     * {@code FLAG}.
     *
     * @param value
     *            new value for the option.
     */
    private void set(final boolean value) {
        assert this.type == Type.FLAG;

        this.booleanValue = value;
    }

    /**
     * Sets the value of the option as specified. The option type must be
     * {@code NUMBER}.
     *
     * @param value
     *            new value for the option.
     */
    private void set(final int value) {
        assert this.type == Type.NUMBER;

        this.intValue = value;
    }

    /**
     * Sets the value of the option as specified. The option type must be
     * {@code STRING}.
     *
     * @param value
     *            new value for the option.
     */
    private void set(final String value) {
        assert this.type == Type.STRING;

        this.stringValue = value;
    }

    /**
     * Possible argument types.
     */
    private enum Type {
        /**
         * Boolean option type. Options of this type are either set or not.
         */
        FLAG,

        /**
         * Number option type. Options of this type require a number to be
         * specified as their argument.
         */
        NUMBER,

        /**
         * String option type. Options of this type require a string to be
         * specified as their argument.
         */
        STRING
    }
}
