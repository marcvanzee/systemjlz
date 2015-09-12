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

public abstract class Token {
    public static final int
        UNARY_OPERATOR =    0,
        BINARY_OPERATOR =   1,
        PROPOSITION =       2,
        OPEN_PARENTHESIS =  3,
        CLOSE_PARENTHESIS = 4,
        SPACE =             5,
        START =             6,
        END =               7,
        VALUE =             8,
        CONSTANT =          9;
    protected int position = 0, offset = 0, type;
    protected String symbol;
    protected boolean isConditional = false;

    abstract public int getPrecedence();

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }

    public int getOffset() {
        return offset;
    }

    public int getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isConditional() {
        return isConditional;
    }
}
