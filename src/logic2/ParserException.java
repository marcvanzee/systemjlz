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

public class ParserException extends TruthTableException {
    public static int UNKNOWN_ERROR                    = 0,
                      MISSING_CONNECTIVE               = 1,
                      MISSING_STATEMENT                = 2,
                      MISSING_STATEMENT_IN_PARENTHESES = 3,
                      ILLEGAL_USE_OF_PARENTHESES       = 4,
                      MISSING_OPEN_PARENTHESIS         = 5,
                      MISSING_CLOSE_PARENTHESIS        = 6;
    public static String[] messageTable = {
        "An unknown error occurred while parsing the expression.",
        "Missing connective at position @X.",
        "Missing statement at position @X.",
        "Missing statement inside parentheses at position @X.",
        "Illegal use of parentheses at position @X.",
        "Missing opening parenthesis.",
        "Missing closing parenthesis."
    };
    private static final long serialVersionUID = 1L;

    private int xValue = -1;
    private boolean selectAll = false;

    public ParserException() {
        messageType = UNKNOWN_ERROR;
        super.message = messageTable[0];
    }

    public ParserException(final int messageType) {
        super.message = messageTable[messageType];
        this.messageType = messageType;
    }

    public ParserException(final int messageType, final int xValue) {
        this(messageType);
        super.message = super.message.replace("@X", String.valueOf(xValue));
        this.xValue = xValue;
    }

    public ParserException(final int messageType, final boolean selectAll) {
        super.message = messageTable[messageType];
        this.messageType = messageType;
        this.selectAll = selectAll;
    }

    public int getXValue() {
        return xValue;
    }

    public boolean selectAll() {
        return selectAll;
    }
}
