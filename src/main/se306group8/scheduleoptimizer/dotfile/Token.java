package se306group8.scheduleoptimizer.dotfile;

import java.io.IOException;
import java.io.PushbackReader;

/** This is an inner class for tokenizing the .dot files. There are existing classes such as StringTokenizer that do this job
 * already, but the .dot format has multi-line strings in it's format, making it too difficult to use. This class generally takes
 * a reader and reads until the end of the token. */
class Token {
	enum Type {
		EOF,
		QUOTE,
		ID,
		KEYWORD,
		BRACKET,
		CONTROL_CHAR,
		IGNORED, 
		EDGE_OP
	}

	final String value;
	final Type type;
	
	/** Creates a token from one control character. This is not to be used for single character IDs or QUOTES */
	Token(int c) throws IOException {
		switch(c) {
		case ':':
			throw new IOException("port definitions are not allowed");
		case ';':
		case ',':
		case '=':
			type = Type.CONTROL_CHAR;
			break;
		case '[':
		case ']':
		case '{':
		case '}':
			type = Type.BRACKET;
			break;
		default:
			throw new IOException();
		}
		
		value = new String(Character.toChars(c));
	}
	
	/** Creates an EOF token. */
	Token() {
		value = "EOF";
		type = Type.EOF;
	}
	
	/** Creates a token from a string, such as a comment, ID, QUOTE, or KEYWORD.
	 * Internally used by the readComment and readKeyword methods. */
	private Token(String s) {
		if(isKeyword(s)) {
			type = Type.KEYWORD;
		} else if(isIgnored(s)) {
			type = Type.IGNORED;
		} else if(s.startsWith("\"")) {
			type = Type.QUOTE;
		} else if(s.equals("->")) {
			type = Type.EDGE_OP;
		} else {
			type = Type.ID;
		}
		
		this.value = s;
	}

	/** Reads data from the reader until the end of the quote has been reached. */
	static Token quote(PushbackReader reader) throws IOException {
		//Read until the next non-escaped quote is reached
		
		StringBuilder builder = new StringBuilder();
		builder.append('"');
		
		int c;
		while(true) {
			c = reader.read();

			switch(c) {
			case -1:
				throw new IOException("Unclosed quote");
			case '\\':
				int peek = reader.read();
				
				if(peek == -1)
					throw new IOException("Unclosed quote");
				
				if(peek == '"') {
					builder.append('"');
				} else {
					builder.append('\\');
					reader.unread(peek);
				}
				break;
			case '"':
				return new Token(builder.append('"').toString());
			default:
				builder.appendCodePoint(c);
				break;
			}
		}
	}

	/** Reads an edge-op -> from the reader */
	static Token edgeOp(PushbackReader reader) throws IOException {
		switch(reader.read()) {
		case '>':
			return new Token("->");
		case '-':
			throw new IOException("Undirected graphs are not allowed");
		}
		
		throw new IOException("Invalid DOT File");
	}

	/** Reads a comment from the reader. The reader will have already read one / */
	static Token comment(PushbackReader reader) throws IOException {
		//check if next char is a * or a / If it is neither it is an error.
		
		StringBuilder builder = new StringBuilder();
		boolean block;
		switch(reader.read()) {
		case '*':
			block = true;
			break;
		case '/':
			block = false;
			break;
		default:
			throw new IOException("Invalid DOT File");
		}
		
		//Read until the end of the comment
		if(block) {
			//Block comments
			builder.append("/*");
			
			int c;
			while(true) {
				c = reader.read();
				
				switch(c) {
				case -1:
					throw new IOException("Unclosed comment");
				case '*':
					int peek = reader.read();
					if(peek == -1)
						throw new IOException("Unclosed comment");
					
					if(peek == '/') {
						return new Token(builder.append("*/").toString());
					} else {
						reader.unread(peek);
					}
					break;
				}
				
				builder.appendCodePoint(c);
			}
		} else {
			//Line comments
			builder.append("//");
			
			int c;
			while(true) {
				c = reader.read();
				
				if(c == '\n' || c == '\r' || c == -1) {
					return new Token(builder.toString());
				} else {
					builder.appendCodePoint(c);
				}
			}
		}
	}

	/** Reads an unquoted string term such as an ID or a KEYWORD. It accepts numbers and letters. */
	static Token readIDOrKeyword(PushbackReader reader) throws IOException {
		StringBuilder builder = new StringBuilder();
		
		int c;
		while(true) {
			c = reader.read();
			
			if(Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
				builder.appendCodePoint(c);
			} else {
				if(c != -1)
					reader.unread(c);
				
				if(builder.length() == 0)
					throw new IOException("Invalid token in DOT file.");
				
				break;
			}
		}
		
		String text = builder.toString();
		if(text.toLowerCase().equals("subgraph")) {
			throw new IOException("subgraphs are not allowed");
		}
		
		return new Token(text);
	}

	/** Creates and EOF token */
	static Token EOF() {
		return new Token();
	}
	
	/** Gets an identifier from a textual token, unwrapping the string if needed. */
	String getID() throws IOException {
		if(type == Type.QUOTE) {
			return value.substring(1, value.length() - 1);
		} else if(type == Type.ID) {
			return value;
		} else {
			throw new IOException("Invalid token: " + value);
		}
	}

	/** Returns true if the string shoud be ignored. */
	private static boolean isIgnored(String s) {
		return s.startsWith("/") || s.toLowerCase().equals("strict");
	}
	
	/** Returns true if the string is a keyword. */
	private static boolean isKeyword(String s) {
		switch(s) {
		case "node":
		case "edge":
		case "graph":
		case "digraph":
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return value;
	}
}