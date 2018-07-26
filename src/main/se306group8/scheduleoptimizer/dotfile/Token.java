package se306group8.scheduleoptimizer.dotfile;

import java.io.IOException;
import java.io.PushbackReader;

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
	
	Token(String s) {
		if(s.isEmpty()) {
			type = Type.EOF;
		} else if(isKeyword(s)) {
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

	static Token edgeOp(PushbackReader reader) throws IOException {
		switch(reader.read()) {
		case '>':
			return new Token("->");
		case '-':
			throw new IOException("Undirected graphs are not allowed");
		}
		
		throw new IOException("Invalid DOT File");
	}

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

	static Token EOF() {
		return new Token("");
	}
	
	String getID() throws IOException {
		if(type == Type.QUOTE) {
			return value.substring(1, value.length() - 1);
		} else if(type == Type.ID) {
			return value;
		} else {
			throw new IOException("Invalid token: " + value);
		}
	}

	static boolean isIgnored(String s) {
		return s.startsWith("/") || s.toLowerCase().equals("strict");
	}
	
	static boolean isKeyword(String s) {
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