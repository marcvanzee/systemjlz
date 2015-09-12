/**
 * Truth Table Constructor:
 *     generates truth tables for statements in propositional logic
 * Copyright (C) 2006, 2010, 2011  Brian S. Borowski
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Email: brian_borowski AT yahoo DOT com
 */
package logic2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class Scanner {
    private static HashMap<String, String> symbolTable =
        new HashMap<String, String>();

    /* Static initializer for HashMap of symbols */
    static {
        symbolTable.put("biconditional1",        "<=>");
        symbolTable.put("biconditional2",        "<->");
        symbolTable.put("close_parenthesis",     ")"  );
        symbolTable.put("conditional1",          "=>" );
        symbolTable.put("conditional2",          "->" );
        symbolTable.put("conjunction1",          "&"  );
        symbolTable.put("conjunction2",          "^"  );
        symbolTable.put("end",                   "$"  );
        symbolTable.put("exclusive_disjunction", "+"  );
        symbolTable.put("inclusive_disjunction", "v"  );
        symbolTable.put("negation1",             "~"  );
        symbolTable.put("negation2",             "!"  );
        symbolTable.put("open_parenthesis",      "("  );
        symbolTable.put("space",                 " "  );
        symbolTable.put("start",                 "@"  );
        symbolTable.put("constant_true",         "1"  );
        symbolTable.put("constant_false",        "0"  );
    }

    private LinkedList<Token> tokenStream;
    private int i, positionOfFirstBadChar;
    private final String statement,
                   inclusive_disjunction = symbolTable.get("inclusive_disjunction"),
                   open_parenthesis      = symbolTable.get("open_parenthesis"),
                   close_parenthesis     = symbolTable.get("close_parenthesis"),
                   negation1             = symbolTable.get("negation1"),
                   negation2             = symbolTable.get("negation2"),
                   conjunction1          = symbolTable.get("conjunction1"),
                   conjunction2          = symbolTable.get("conjunction2"),
                   exclusive_disjunction = symbolTable.get("exclusive_disjunction"),
                   conditional1          = symbolTable.get("conditional1"),
                   conditional2          = symbolTable.get("conditional2"),
                   biconditional1        = symbolTable.get("biconditional1"),
                   biconditional2        = symbolTable.get("biconditional2"),
                   space                 = symbolTable.get("space"),
                   constant_true         = symbolTable.get("constant_true"),
                   constant_false        = symbolTable.get("constant_false");

    public Scanner(final String statement) {
        this.statement = statement;
        tokenStream = new LinkedList<Token>();
    }

    public LinkedList<Token> getTokenStream() {
        return tokenStream;
    }

    public void tokenize() throws ScannerException {
        final int statementLength = statement.length();
        i = 0;
        positionOfFirstBadChar = -1;
        char c;

        tokenStream.add(new StartToken(symbolTable.get("start")));
        while (i < statementLength) {
            c = statement.charAt(i);
            if (c == inclusive_disjunction.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new InclusiveDisjunctionToken(inclusive_disjunction, i));
            } else if (c == open_parenthesis.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new OpenParenthesisToken(open_parenthesis, i));
            } else if (c == close_parenthesis.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new CloseParenthesisToken(close_parenthesis, i));
            } else if ((c == negation1.charAt(0)) || (c == negation2.charAt(0))) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new NegationToken(String.valueOf(c), i));
            } else if ((c == conjunction1.charAt(0)) || (c == conjunction2.charAt(0))) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new ConjunctionToken(String.valueOf(c), i));
            } else if (c == exclusive_disjunction.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new ExclusiveDisjunctionToken(exclusive_disjunction, i));
            } else if (c == constant_false.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new ConstantToken(constant_false, i, false));
            } else if (c == constant_true.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new ConstantToken(constant_true, i, true));
            } else if (c == conditional1.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                scanMultiCharSymbol(conditional1, new ConditionalToken(conditional1, i), true);
            } else if (c == conditional2.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                scanMultiCharSymbol(conditional2, new ConditionalToken(conditional2, i), true);
            } else if (c == biconditional1.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                final int current = i;
                final boolean isOK = scanMultiCharSymbol(biconditional1, new BiconditionalToken(biconditional1, i), false);
                if (!isOK) {
                    i = current;
                    scanMultiCharSymbol(biconditional2, new BiconditionalToken(biconditional2, i), true);
                }
            } else if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'))) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
                tokenStream.add(new PropositionToken(String.valueOf(Character.toUpperCase(c)), i));
            } else if (c == space.charAt(0)) {
                positionOfFirstBadChar = reportError(positionOfFirstBadChar, i - 1);
            } else if (positionOfFirstBadChar == -1) {
                positionOfFirstBadChar = i;
            }
            i++;
        }
        positionOfFirstBadChar = reportError(positionOfFirstBadChar, --i);
        tokenStream.add(new EndToken(symbolTable.get("end")));
    }

    public void reformat() {
        final LinkedList<Token> list = tokenStream;
        final ListIterator<Token> iterator = list.listIterator(0);
        final LinkedList<Token> reformattedtokenStream = new LinkedList<Token>();
        Token token1 = null, token2 = null;
        int position = -1;
        if (iterator.hasNext()) {
            token1 = iterator.next();
        }
        while (iterator.hasNext()) {
            token2 = iterator.next();
            final int token1Type = token1.getType(),
                token2Type = token2.getType();
            token1.setPosition(position);
            reformattedtokenStream.add(token1);
            position = position + token1.getSymbol().length();
            if (token1Type == Token.START) {
                if (token2Type == Token.BINARY_OPERATOR) {
                    final SpaceToken spaceToken = new SpaceToken(symbolTable.get("space"), position);
                    reformattedtokenStream.add(spaceToken);
                    position = position + spaceToken.getSymbol().length();
                }
            } else if (token1Type == Token.BINARY_OPERATOR) {
                final SpaceToken spaceToken = new SpaceToken(symbolTable.get("space"), position);
                reformattedtokenStream.add(spaceToken);
                position = position + spaceToken.getSymbol().length();
            } else if (token1Type == Token.UNARY_OPERATOR) {
                if (token2Type == Token.BINARY_OPERATOR) {
                    final SpaceToken spaceToken = new SpaceToken(symbolTable.get("space"), position);
                    reformattedtokenStream.add(spaceToken);
                    position = position + spaceToken.getSymbol().length();
                }
            } else if ((token1Type == Token.PROPOSITION) || (token1Type == Token.CONSTANT) ||
                       (token1Type == Token.CLOSE_PARENTHESIS)) {
                if ((token2Type != Token.CLOSE_PARENTHESIS) && (token2Type != Token.END)) {
                    final SpaceToken spaceToken = new SpaceToken(symbolTable.get("space"), position);
                    reformattedtokenStream.add(spaceToken);
                    position = position + spaceToken.getSymbol().length();
                }
            } else if (token1Type == Token.OPEN_PARENTHESIS) {
                if (token2Type == Token.CLOSE_PARENTHESIS) {
                    final SpaceToken spaceToken = new SpaceToken(symbolTable.get("space"), position);
                    reformattedtokenStream.add(spaceToken);
                    position = position + spaceToken.getSymbol().length();
                }
            }
            token1 = token2;
        }
        if (token2 != null) {
            token2.setPosition(position);
            reformattedtokenStream.add(token2);
        }
        tokenStream = reformattedtokenStream;
    }

    public String getStatement() {
        final StringBuilder builder = new StringBuilder();
        final LinkedList<Token> list = tokenStream;
        final ListIterator<Token> iterator = list.listIterator(0);
        while (iterator.hasNext()) {
            final Token token = iterator.next();
            final int tokenType = token.getType();
            if ((tokenType != Token.START) && (tokenType != Token.END)) {
                builder.append(token.getSymbol());
            }
        }
        return builder.toString();
    }

    private boolean scanMultiCharSymbol(final String symbol, final Token token, final boolean isFinalMatch) throws ScannerException {
        positionOfFirstBadChar = -1;
        int symbolPos = 1;
        final int symbolLength = symbol.length(), initialValueOfI = i;
        while (true) {
            i++;
            if (i >= statement.length()) {
                if (isFinalMatch) {
                    positionOfFirstBadChar = reportError(initialValueOfI, i - 1);
                } else {
                    return false;
                }
            }
            final char c = statement.charAt(i);
            if ((symbolPos < symbolLength) && (c == symbol.charAt(symbolPos))) {
                if (symbolPos + 1 == symbolLength) {
                    if (positionOfFirstBadChar != -1) {
                        if (isFinalMatch) {
                            positionOfFirstBadChar = reportError(initialValueOfI, i);
                        } else {
                            return false;
                        }
                    }
                    tokenStream.add(token);
                    break;
                }
                symbolPos++;
            } else {
                if (positionOfFirstBadChar == -1) {
                    positionOfFirstBadChar = i;
                }
                if ((c == negation1.charAt(0))     || (c == negation2.charAt(0))        ||
                    (c == constant_true.charAt(0)) || (c == constant_false.charAt(0))   ||
                    (c == space.charAt(0))         || (c == open_parenthesis.charAt(0)) ||
                    ((c >= 'A') && (c <= 'Z'))     || ((c >= 'a') && (c <= 'z'))) {
                    i--;
                    if (isFinalMatch) {
                        positionOfFirstBadChar = reportError(initialValueOfI, i);
                    } else {
                        return false;
                    }
                } else {
                    symbolPos++;
                }
            }
        }
        return true;
    }

    private int reportError(final int positionOfFirstBadChar,
        final int positionOfCurrentChar) throws ScannerException {
        if (positionOfFirstBadChar != -1) {
            if (positionOfCurrentChar - positionOfFirstBadChar < 1) {
                throw new ScannerException(ScannerException.ILLEGAL_SYMBOL,
                    positionOfFirstBadChar + 1);
            } else {
                throw new ScannerException(ScannerException.ILLEGAL_SYMBOLS,
                    positionOfFirstBadChar + 1, positionOfCurrentChar + 1);
            }
        }
        return -1;
    }
}
