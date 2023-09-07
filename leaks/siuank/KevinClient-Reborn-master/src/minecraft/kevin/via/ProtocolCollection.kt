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
package kevin.via

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion

enum class ProtocolCollection(val protocolVersion: ProtocolVersion) {
    //1.17
    R1_17_1(ProtocolVersion(756, "1.17.1")),
    R1_17(ProtocolVersion(755, "1.17")),
    //1.16
    R1_16_5(ProtocolVersion(754, "1.16.4-1.16.5")),
    R1_16_3(ProtocolVersion(753, "1.16.3")),
    R1_16_2(ProtocolVersion(751, "1.16.2")),
    R1_16_1(ProtocolVersion(736, "1.16.1")),
    R1_16(ProtocolVersion(735, "1.16")),
    //1.15
    R1_15_2(ProtocolVersion(578, "1.15.2")),
    R1_15_1(ProtocolVersion(575, "1.15.1")),
    R1_15(ProtocolVersion(573, "1.15")),
    //1.14
    R1_14_4(ProtocolVersion(498, "1.14.4")),
    R1_14_3(ProtocolVersion(490, "1.14.3")),
    R1_14_2(ProtocolVersion(485, "1.14.2")),
    R1_14_1(ProtocolVersion(480, "1.14.1")),
    R1_14(ProtocolVersion(477, "1.14")),
    //1.13
    R1_13_2(ProtocolVersion(404, "1.13.2")),
    R1_13_1(ProtocolVersion(401, "1.13.1")),
    R1_13(ProtocolVersion(393, "1.13")),
    //1.12
    R1_12_2(ProtocolVersion(340, "1.12.2")),
    R1_12_1(ProtocolVersion(338, "1.12.1")),
    R1_12(ProtocolVersion(335, "1.12")),
    //1.11
    R1_11_1(ProtocolVersion(316, "1.11.1-1.11.2")),
    R1_11(ProtocolVersion(315, "1.11")),
    //1.10
    R1_10(ProtocolVersion(210, "1.10.x")),
    //1.9
    R1_9_4(ProtocolVersion(110, "1.9.3-1.9.4")),
    R1_9_2(ProtocolVersion(109, "1.9.2")),
    R1_9_1(ProtocolVersion(108, "1.9.1")),
    R1_9(ProtocolVersion(107, "1.9")),
    //1.8
    R1_8(ProtocolVersion(47, "1.8.x"));

    companion object{
        fun getProtocolById(id: Int) = values().find { it.protocolVersion.version == id }
    }
}