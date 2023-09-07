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
package kevin.script

import kevin.module.*
import org.python.core.PyDictionary
import org.python.core.PyList
import org.python.core.PyObject

object Setting {
    @JvmStatic
    fun boolean(settingInfo: PyObject): BooleanValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = settingInfo["default"] as Boolean

        return BooleanValue(name, default)
    }

    @JvmStatic
    fun integer(settingInfo: PyObject): IntegerValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = (settingInfo["default"] as Number).toInt()
        val min = (settingInfo["min"] as Number).toInt()
        val max = (settingInfo["max"] as Number).toInt()

        return IntegerValue(name, default, min, max)
    }

    @JvmStatic
    fun float(settingInfo: PyObject): FloatValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = (settingInfo["default"] as Number).toFloat()
        val min = (settingInfo["min"] as Number).toFloat()
        val max = (settingInfo["max"] as Number).toFloat()

        return FloatValue(name, default, min, max)
    }

    @JvmStatic
    fun text(settingInfo: PyObject): TextValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = settingInfo["default"] as String

        return TextValue(name, default)
    }

    @JvmStatic
    fun block(settingInfo: PyObject): BlockValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = (settingInfo["default"] as Number).toInt()

        return BlockValue(name, default)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun list(settingInfo: PyObject): ListValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val values = (settingInfo["values"] as PyList).toArray() as Array<String>
        val default = settingInfo["default"] as String

        return ListValue(name, values, default)
    }
}