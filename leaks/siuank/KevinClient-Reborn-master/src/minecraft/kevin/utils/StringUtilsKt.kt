/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.utils

val String.fixToChar: Array<String>
    get() {
        val l = this.length
        var i = 0
        val list = arrayListOf<String>()
        while (i < l) {
            val char = this[i]
            i++
            if (i >= l || !Character.isHighSurrogate(char)) {
                list.add(char.toString())
            } else {
                val char2 = this[i]
                if (Character.isLowSurrogate(char2)) {
                    i++
                    list.add(char.toString()+char2.toString())
                } else {
                    list.add(char.toString())
                }
            }
        }
        return list.toTypedArray()
    }