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
package kevin.module.modules.render

import kevin.module.*

class Animations : Module("Animations","Changes animations.", category = ModuleCategory.RENDER) {
    val animations = ListValue(
        "Preset", arrayOf(
            "Akrien", "Avatar", "ETB", "Exhibition", "Kevin", "Push", "Reverse",
            "Shield", "SigmaNew", "SigmaOld", "Slide", "SlideDown", "HSlide", "Swong", "VisionFX",
            "Swank", "Jello", "LiquidBounce","Rotate"
        ),
        "SlideDown"
    )

    var translateX = FloatValue("TranslateX", 0.0f, 0.0f, 1.5f)
    var translateY = FloatValue("TranslateY", 0.0f, 0.0f, 0.5f)
    var translateZ = FloatValue("TranslateZ", 0.0f, 0.0f, -2.0f)
    val itemPosX = FloatValue("ItemPosX", 0.56F, -1.0F, 1.0F)
    val itemPosY = FloatValue("ItemPosY", -0.52F, -1.0F, 1.0F)
    val itemPosZ = FloatValue("ItemPosZ", -0.71999997F, -1.0F, 1.0F)
    var itemScale = FloatValue("ItemScale", 0.4f, 0.0f, 2.0f)
    val onlyOnBlock = BooleanValue("OnlyBlock",false)
    val rotationItem = BooleanValue("RotationItem",false)
    val animationSpeed = IntegerValue("AnimationSpeed",6,1,30)

    override val tag: String
        get() = animations.get()
}