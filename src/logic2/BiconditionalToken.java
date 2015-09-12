package logic2;

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

public class BiconditionalToken extends Token implements BinaryEvaluator {

    public BiconditionalToken(final String symbol, final int position) {
        type = Token.BINARY_OPERATOR;
        this.symbol = symbol;
        this.position = position;
        offset = (symbol.length() - 1)/2;
    }

    public int getPrecedence() {
        return 2;
    }

    public ValueToken evaluate(final ValueToken token1, final ValueToken token2) {
        ValueToken returnToken = null;
        if (token1.getValue() == token2.getValue()) {
            returnToken = new ValueToken(true, token1.getDisplayMethod(), position + offset);
        } else {
            returnToken = new ValueToken(false, token1.getDisplayMethod(), position + offset);
        }
        return returnToken;
    }
}
