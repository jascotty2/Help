/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: how to display a help entry listing
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.taylorkelly.help.enums;

public enum DisplayFormat {
    /**
     * show in old, one-line style (string is truncated)
     */
    ONE_LINE, 
    /**
     * the text is not formatted, and long strings are 
     *      automatically wrapped by minecraft
     */
    TEXT, 
    /**
     * smart line wrapping uses word breaks
     */
    WRAP, 
    /**
     * commands shown on the left, description word-wrapped on the right
     */
    COLUMN;
}
