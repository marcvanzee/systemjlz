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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

public class Parser {
    private LinkedList<Token> tokenStream;
    private final LinkedList<Token> postfixStream;

    public Parser(final LinkedList<Token> tokenStream) {
        this.tokenStream = tokenStream;
        postfixStream = new LinkedList<Token>();
    }

    public LinkedList<Token> getPostfixStream() {
        return postfixStream;
    }

    public void parse() throws ParserException {
        final ListIterator<Token> iterator = tokenStream.listIterator(0);
        Token token1 = null, token2 = null;
        if (iterator.hasNext()) {
            token1 = iterator.next();
        }
        while (iterator.hasNext()) {
            token2 = iterator.next();
            final int token1Type = token1.getType();
            int token2Type = token2.getType();
            if (token2Type == Token.SPACE) {
                if (iterator.hasNext()) {
                    token2 = iterator.next();
                    token2Type = token2.getType();
                }
            }
            switch (token1Type) {
                case Token.PROPOSITION:
                    if ((token2Type == Token.PROPOSITION) ||
                        (token2Type == Token.CONSTANT) ||
                        (token2Type == Token.UNARY_OPERATOR) ||
                        (token2Type == Token.OPEN_PARENTHESIS)) {
                        throw new ParserException(ParserException.MISSING_CONNECTIVE,
                            token1.getPosition() + 2);
                    }
                    break;
                case Token.CONSTANT:
                    if ((token2Type == Token.PROPOSITION) ||
                        (token2Type == Token.CONSTANT) ||
                        (token2Type == Token.UNARY_OPERATOR) ||
                        (token2Type == Token.OPEN_PARENTHESIS)) {
                        throw new ParserException(ParserException.MISSING_CONNECTIVE,
                            token1.getPosition() + 2);
                    }
                    break;
                case Token.UNARY_OPERATOR:
                    if ((token2Type == Token.CLOSE_PARENTHESIS) ||
                        (token2Type == Token.BINARY_OPERATOR) ||
                        (token2Type == Token.END)) {
                        throw new ParserException(ParserException.MISSING_STATEMENT,
                            token1.getPosition() + 2);
                    }
                    break;
                case Token.OPEN_PARENTHESIS:
                    if (token2Type == Token.CLOSE_PARENTHESIS) {
                        throw new ParserException(ParserException.MISSING_STATEMENT_IN_PARENTHESES,
                            token1.getPosition() + 2);
                    } else if (token2Type == Token.BINARY_OPERATOR) {
                        throw new ParserException(ParserException.MISSING_STATEMENT,
                            token1.getPosition() + 2);
                    } else if (token2Type == Token.END) {
                        throw new ParserException(ParserException.ILLEGAL_USE_OF_PARENTHESES,
                            token1.getPosition() + 1);
                    }
                    break;
                case Token.CLOSE_PARENTHESIS:
                    if ((token2Type == Token.PROPOSITION) ||
                        (token2Type == Token.CONSTANT) ||
                        (token2Type == Token.UNARY_OPERATOR) ||
                        (token2Type == Token.OPEN_PARENTHESIS)) {
                        throw new ParserException(ParserException.MISSING_CONNECTIVE,
                            token1.getPosition() + 2);
                    }
                    break;
                case Token.BINARY_OPERATOR:
                    if ((token2Type == Token.CLOSE_PARENTHESIS) ||
                        (token2Type == Token.BINARY_OPERATOR) ||
                        (token2Type == Token.END)) {
                        throw new ParserException(ParserException.MISSING_STATEMENT,
                            token1.getPosition() + token1.getSymbol().length() + 1);
                    }
                    break;
                case Token.START:
                    if (token2Type == Token.CLOSE_PARENTHESIS) {
                        throw new ParserException(ParserException.ILLEGAL_USE_OF_PARENTHESES,
                            token1.getPosition() + 2);
                    } else if (token2Type == Token.BINARY_OPERATOR) {
                        throw new ParserException(ParserException.MISSING_STATEMENT,
                            token1.getPosition() + 2);
                    }
                    break;
                default: break;
            }
            token1 = token2;
        }
        computePostfixStream();
    }

    private void computePostfixStream() throws ParserException {
        final Stack<Token> stack = new Stack<Token>();
        final ListIterator<Token> iterator = tokenStream.listIterator(0);
        Token token, stackTop = null;

        while (iterator.hasNext()) {
            do {
                token = iterator.next();
            } while ((iterator.hasNext()) && (token.getType() == Token.SPACE));
            if (!stack.empty()) {
                stackTop = stack.peek();
            }
            final int tokenType = token.getType();
            if ((tokenType != Token.START) && (tokenType != Token.END)) {
                if ((tokenType == Token.PROPOSITION) || (tokenType == Token.CONSTANT)) {
                    postfixStream.add(token);
                } else if (tokenType == Token.OPEN_PARENTHESIS) {
                    stack.push(token);
                } else if (tokenType == Token.CLOSE_PARENTHESIS) {
                    while ((!stack.empty()) && (stackTop.getType() != Token.OPEN_PARENTHESIS)) {
                        postfixStream.add(stack.pop());
                        if (!stack.empty()) {
                            stackTop = stack.peek();
                        }
                    }
                    if (stack.empty()) {
                        throw new ParserException(ParserException.MISSING_OPEN_PARENTHESIS, true);
                    } else {
                        stack.pop();
                    }
                } else if (stack.empty()) {
                    stack.push(token);
                } else if ((tokenType == Token.UNARY_OPERATOR) &&
                           (stackTop.getPrecedence() <= token.getPrecedence())) {
                    stack.push(token);
                } else if (stackTop.getPrecedence() < token.getPrecedence()) {
                    stack.push(token);
                } else if (stackTop.isConditional() && token.isConditional()) {
                    stack.push(token);
                } else {
                    while ((!stack.empty()) && (stackTop.getPrecedence() >= token.getPrecedence())) {
                        postfixStream.add(stack.pop());
                        if (!stack.isEmpty()) {
                            stackTop = stack.peek();
                        }
                    }
                    stack.push(token);
                }
            }
        }
        while (!stack.empty()) {
            token = stack.pop();
            if (token.getType() != Token.OPEN_PARENTHESIS) {
                postfixStream.add(token);
            } else {
                throw new ParserException(ParserException.MISSING_CLOSE_PARENTHESIS, true);
            }
        }
    }

    public void removeUnnecessaryParentheses() {
        Token[] tokenArray = tokenStream.toArray(new Token[tokenStream.size()]);
        final LinkedList<Token> newTokenStream = new LinkedList<Token>();
        ListIterator<Token> iterator = tokenStream.listIterator(0);
        int pos = 0;
        while (iterator.hasNext())
            tokenArray[pos++] = iterator.next();
        final Stack<Integer> stack = new Stack<Integer>();
        final ArrayList<Point> parensMatchings = new ArrayList<Point>();

        Token token;
        iterator = tokenStream.listIterator(0);
        pos = 0;
        while (iterator.hasNext()) {
            token = iterator.next();
            if (token.getType() == Token.OPEN_PARENTHESIS)
                stack.push(pos);
            else if (token.getType() == Token.CLOSE_PARENTHESIS)
                parensMatchings.add(new Point(stack.pop(), pos));
            pos++;
        }
        Collections.sort(parensMatchings, new Comparator<Point>() {
            public int compare(final Point a, final Point b) {
                if (a.x < b.x)
                    return -1;
                else if (a.x > b.x)
                    return 1;
                else
                    return 0;
            }
        });
        for (int i = 0; i < parensMatchings.size(); i++)
        {
            boolean removalRequired = false;
            final Point p = parensMatchings.get(i);
            final int propositionCount =
                getPropositionCountInsideParens(tokenArray, p.x, p.y);
            if (propositionCount <= 1) {
                // If there's only one proposition inside the parentheses,
                // then the parentheses are clearly not needed.
                removalRequired = true;
            } else if ((p.x == 1) && (p.y == tokenArray.length - 2)) {
                // If these parentheses surround the whole statement, then
                // they can definitely be removed.
                removalRequired = true;
            } else {
                // Check if these parentheses are duplicates, that is, they surround
                // another set of parentheses.
                if (i < parensMatchings.size() - 1) {
                    final Point pLookAhead = parensMatchings.get(i+1);
                    if (pLookAhead.y == p.y - 1) {
                        if (pLookAhead.x == p.x + 1) {
                            // Direct duplicates
                            removalRequired = true;
                        } else {
                            // Duplicates surrounding a negation
                            boolean isDuplicateParens = true;
                            for (int c = p.x + 1; c < pLookAhead.x; c++) {
                                final Token t = tokenArray[c];
                                if (t.getType() != Token.UNARY_OPERATOR) {
                                    isDuplicateParens = false;
                                    break;
                                }
                            }
                            removalRequired = isDuplicateParens;
                        }
                    }
                }
            }
            // If parentheses surround only one proposition, surround the whole
            // statement, or are duplicates, remove them.
            if (removalRequired) {
                tokenArray[p.x] = null;
                tokenArray[p.y] = null;

                for (int j = i + 1; j < parensMatchings.size(); j++) {
                    final Point p2 = parensMatchings.get(j);
                    if (p.x < p2.x) p2.x--;
                    if (p.x < p2.y) p2.y--;
                    if (p.y < p2.x) p2.x--;
                    if (p.y < p2.y) p2.y--;
                }
                for (int j = p.x + 1; j < tokenArray.length; j++) {
                    final Token t = tokenArray[j];
                    if (t != null) {
                        final int tPos = t.getPosition();
                        if (tPos >= p.x)
                            t.setPosition(tPos - 1);
                        if (tPos >= p.y)
                            t.setPosition(tPos - 2);
                    }
                }
                pos = 0;
                final Token[] tempArray = new Token[tokenArray.length - 2];
                for (int j = 0; j < tokenArray.length; j++) {
                    final Token t = tokenArray[j];
                    if (t != null)
                        tempArray[pos++] = t;
                }
                tokenArray = tempArray;
            }
        }
        for (int i = 0; i < tokenArray.length; i++)
            newTokenStream.add(tokenArray[i]);
        tokenStream = newTokenStream;
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

    private int getPropositionCountInsideParens(final Token[] tokenArray,
        final int start, final int end) {

        int propositionCount = 0, i = start;
        while (i <= end) {
            final Token token = tokenArray[i];
            final int tokenType = token.getType();
            if ((tokenType == Token.PROPOSITION) || (tokenType == Token.CONSTANT)) {
                propositionCount++;
            }
            i++;
        }
        return propositionCount;
    }
}
