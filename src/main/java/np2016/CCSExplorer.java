package np2016;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import np2016.ASTNodes.ASTFactory;
import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.LTS;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;
import np2016.Diagnostic.Diagnostic;
import np2016.Diagnostic.PrintDiagnostic;
import np2016.GraphSearch.ConcurrentGraphSearch;
import np2016.GraphSearch.ConcurrentLTSBuilder;
import np2016.GraphSearch.GraphSearch;
import np2016.GraphSearch.LTSBuilder;
import np2016.GraphSearch.SequentialBFSGraphSearch;
import np2016.GraphSearch.SequentialLTSBuilder;
import np2016.Parser.CCSProgram;
import np2016.Parser.Lexer;
import np2016.Parser.Parser;

/**
 * Possible exit codes.
 */
enum ExitCode {
	/**
	 * Program completed its execution without errors.
	 */
	SUCCESS(0, "Program execution finished without errors."),

	/**
	 * An error occurred while parsing the command line arguments.
	 */
	ARGUMENT_ERROR(1, "The given command line arguments were erroneous.");

	/**
	 * Stores the exit code.
	 */
	private final int code;

	/**
	 * Stores a short description for the exit code.
	 */
	private final String description;

	/**
	 * Constructs an exit code.
	 *
	 * @param code
	 *            the exit code.
	 * @param description
	 *            exit code description.
	 */
	ExitCode(final int code, final String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Returns the exit code.
	 *
	 * @return the exit code.
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Returns the exit code description.
	 *
	 * @return the exit code description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the exit code together with its description text.
	 *
	 * @return the exit code together with its description text.
	 */
	@Override
	public String toString() {
		return String.format("%d:\t%s", this.code, this.description);
	}
}

/**
 * This is the class with the main program. It parses the command line arguments
 * and input files. Furthermore it implements the overall program logic.
 * <p>
 * Additionally, it implements the {@code --view-online} functionality (i.e.
 * uploading the explored LTS and opening a browser window).
 */
public class CCSExplorer {

	/**
	 * This is the main program. It parses the command line arguments and
	 * implements a basic program logic according to the passed arguments.
	 *
	 * @param args
	 *            command line arguments.
	 */
	public static void main(final String[] args) {
		// parse command line arguments
		if (!Options.parseArguments(args)) {
			System.exit(ExitCode.ARGUMENT_ERROR.getCode());
		}

		// print help message if required
		if (Options.HELP.isSet()) {
			Options.printUsageInformation();
			System.exit(ExitCode.SUCCESS.getCode());
		}

		// execute program logic according to given arguments
		Diagnostic diagnostic = new PrintDiagnostic(System.err);

		for (String fileName : Options.getFileNames()) {
			System.out.print(fileName);
			System.out.println(":");

			CCSProgram program = parseInputFile(diagnostic, fileName);
			if (program == null) {
				continue;
			}

			assignment1(fileName, program);
			assignment2(program);
		}

		System.exit(ExitCode.SUCCESS.getCode());
	}

	/**
	 * Parses the input CCS file. This involves the following steps:
	 * <ul>
	 * <li>open the file for reading.</li>
	 * <li>lex the file (i.e. transform the sequence of characters into a
	 * sequence of tokens).</li>
	 * <li>parse the file (i.e. use the sequence of tokens to construct the AST
	 * (abstract syntax tree)).</li>
	 * </ul>
	 *
	 * @param diagnostic
	 *            helper for printing error and information messages related to
	 *            positions in the input program.
	 * @param fileName
	 *            name of the file that should be parsed.
	 * @return the parsed CCS program or {@code null} if the file could not be
	 *         found.
	 */
	private static CCSProgram parseInputFile(final Diagnostic diagnostic, final String fileName) {
		try (Reader reader = new InputStreamReader(new FileInputStream(fileName))) {
			Lexer lexer = new Lexer(diagnostic, reader, fileName);
			Parser parser = new Parser(diagnostic, lexer, new ASTFactory(diagnostic));

			return parser.parseCCSProgram();
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			return null;
		} catch (IOException e) {
			System.err.println(String.format("An error occurred while reading file %s!", fileName));
			return null;
		}
	}

	/**
	 * Explores the CCS semantics of the given CCS program and constructs the
	 * corresponding LTS. If {@link Options#VIEW_ONLINE} is not set prints the
	 * JSON string for the explored LTS on command line else the LTS is uploaded
	 * to pseuCo.com and a browser window is opened to view the file.
	 *
	 * @param fileName
	 *            name of the file.
	 * @param program
	 *            the parsed CCS program.
	 */
	private static void assignment1(final String fileName, final CCSProgram program) {
		if (!Options.LTS.isSet()) {
			return;
		}

		CCSSemantics semantics = new CCSSemantics(program);
		LTSBuilder builder = null;
		GraphSearch<State, Transition> search = null;
		NonSense nonsense = new NonSense();

		if (Options.THREADS.getNumber() == 0) {
			// sequential solution
			builder = new SequentialLTSBuilder();
			search = new SequentialBFSGraphSearch<State, Transition>(builder);
		} else {
			builder = new ConcurrentLTSBuilder();
			search = new ConcurrentGraphSearch<State, Transition>(builder);
		}
		
		for (State state : semantics.getSources()) {
			search.search(semantics, state, nonsense);
			
			while (!search.getWatcher()) {
				synchronized (nonsense) {
					try {
						nonsense.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			LTS lts = builder.getLTS();
			JsonObject json = lts.toJSON();
			if (Options.VIEW_ONLINE.isSet()) {
				viewOnline(fileName, json);
			} else {
				System.out.println(json.toString());
			}

		}
	}

	/**
	 * Searches for a path in the LTS which violates the specified "critical
	 * section" property. Prints:
	 * <ul>
	 * <li>{@code "NOT IMPLEMENTED!"} if not implemented (and thus not
	 * attempted).</li>
	 * <li>{@code "NOT OK!"} if such a path <b>is</b> found.</li>
	 * <li>{@code "OK!"} if such a path <b>is not</b> found.</li>
	 * </ul>
	 *
	 * @param program
	 *            the parsed CCS program.
	 */
	private static void assignment2(final CCSProgram program) {
		if (!Options.CRITICAL_SECTION.isSet()) {
			return;
		}

		// TODO implement this!
		System.out.println("NOT IMPLEMENTED!");
	}

	/**
	 * Uploads the LTS file to pseuCo.com and opens a browser window to show it.
	 *
	 * @param fileName
	 *            name of the file.
	 * @param lts
	 *            LTS in form of a JSON object.
	 */
	private static void viewOnline(final String fileName, final JsonObject lts) {
		System.out.println("Submitting LTS ...");
		URI uri = submitFile(fileName, lts);
		if (uri == null) {
			return;
		}

		System.out.println("Opening in browser ...");
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			System.err.println("The browser cannot be opened!");
		}
	}

	/**
	 * Uploads the file to pseuCo.com.
	 *
	 * @param fileName
	 *            name of the file.
	 * @param lts
	 *            LTS in form of a JSON object.
	 * @return the URI where the file can be accessed.
	 */
	private static URI submitFile(final String fileName, final JsonObject lts) {
		try {
			URL url = new URL("http://pseuco.com/api/paste/add");
			URLConnection connection = url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded);charset=UTF-8");

			OutputStream output = connection.getOutputStream();

			output.write(buildPayload(fileName, lts).getBytes(Charset.forName("UTF-8")));

			Reader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
			Gson gson = new Gson();
			Response response = gson.fromJson(reader, Response.class);

			URI shareLink = new URI(response.url);

			return shareLink;
		} catch (MalformedURLException e) {
			// this should not get executed
			System.err.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println("Failed to submit file to pseuCo.com!");
			System.err.println(e.getMessage());
			return null;
		} catch (URISyntaxException e) {
			System.err.println("Received an erroneous response!");
			System.err.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Constructs the payload that is sent to pseuCo.com.
	 *
	 * @param fileName
	 *            name of the file.
	 * @param lts
	 *            LTS in form of a JSON object.
	 * @return the stringyfied payload.
	 */
	private static String buildPayload(final String fileName, final JsonObject lts) {
		File file = new File();
		file.name = fileName;
		file.content = lts;

		Payload payload = new Payload();
		payload.file = file;

		Gson gson = new Gson();
		return gson.toJson(payload);
	}

	/**
	 * Response from pseuCo.com.
	 */
	@SuppressWarnings("unused")
	private static class Response {
		/**
		 * The file id (i.e. the name under which it can be accessed globally).
		 */
		private String id;

		/**
		 * The URL that is needed to view the file.
		 */
		private String url;

		/**
		 * Is the file accessible only temporarily?
		 */
		private boolean temporary;
	}

	/**
	 * The file structure needed to communicate with pseuCo.com.
	 */
	@SuppressWarnings("unused")
	private static class File {
		/**
		 * Type of file. For our purposes only "lts" is relevant.
		 */
		private String type = "lts";

		/**
		 * The file name.
		 */
		private String name;

		/**
		 * The JSON object representing the LTS.
		 */
		private JsonObject content;
	}

	/**
	 * The payload that is sent to pseuCo.com.
	 */
	@SuppressWarnings("unused")
	private static class Payload {
		/**
		 * The file to be uploaded.
		 */
		private File file;

		/**
		 * Store and share the file only temporarily?
		 */
		private boolean temporary = true;
	}
}
