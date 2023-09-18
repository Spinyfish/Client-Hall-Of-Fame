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

import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptUtils
import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.modules.combat.KillAura
import kevin.utils.MinecraftInstance
import kevin.utils.RotationUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.model.TextureOffset
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemSword
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import java.util.function.Function
import javax.imageio.ImageIO
import javax.script.Invocable
import javax.script.ScriptEngine
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.cos

object Renderer : Module("Renderer","Allows you to modify some renderings.",category = ModuleCategory.RENDER) {
    val noArmor = BooleanValue("NoArmor",false)
    val noRightArm = BooleanValue("NoRightArm",false)
    private var playerModel = ListValue("PlayerModel", arrayOf("Fox","Snow_Fox","Player"),"Player")
    private val renderers = HashMap<String,RendererLivingEntity<AbstractClientPlayer>>()
    private val models = HashMap<String,ModelBase>()
    private val layers = HashMap<String,ArrayList<LayerRenderer<AbstractClientPlayer>>>()
    lateinit var renderManager: RenderManager

    fun load(){
        val files = KevinClient.fileManager.playerModels.listFiles()
        if (files.isNullOrEmpty()) return
        val pngs = files.filter { it.isFile&&it.name.endsWith(".png",true) }
        val jss = files.filter { it.isFile&&it.name.endsWith(".js",true) }
        if (pngs.isEmpty()||jss.isEmpty()) return
        val list = arrayListOf("Fox","Snow_Fox","Player")
        pngs.forEach {
            val name = it.name.replace(".png","",true)
            val jsFile = jss.find { file -> file.name.replace(".js","",true) == name }
            if (jsFile != null && (name!="Fox"&&name!="Snow_Fox"&&name!="Player")) {
                try {
                    val js = JSFile(jsFile)
                    texturesList[name] = Texture(name,ImageIO.read(it))
                    models[name] = ModelFromFile(js)
                    renderers[name] = RenderFromFile(js,name, renderManager)
                    list.add(name)
                } catch (e: Throwable) {
                    Minecraft.logger.error("[Renderer] Failed to load model $name",e)
                }
            }
        }
        playerModel = ListValue("PlayerModel", list.toTypedArray(),"Player")
    }

    class JSFile(val jsFile: File) : MinecraftInstance(){
        private val scriptEngine = NashornScriptEngineFactory().scriptEngine
        private val jsText = jsFile.readText()
        val inv:Invocable
        var setLivingAnimations = false
        var getRandomModelBox = false
        var setRotationAngles = false
        var setTextureOffset = false
        var setModelAttributes = false
        var getTextureOffset = false
        var render = false
        init {
            scriptEngine.put("mc",mc)
            scriptEngine.put("kevinClient",KevinClient)
            scriptEngine.put("Main",RegisterRender())
            scriptEngine.put("addLayer",AddLayer())
            scriptEngine.eval(jsText)
            inv = scriptEngine as Invocable
        }
        inner class AddLayer : Function<JSObject, Unit> {
            override fun apply(scriptObject: JSObject) {
                val name = jsFile.name.replace(".js","",true)
                val layerRenderer = LayerFromFile(name,
                    LayerJSFile(File(KevinClient.fileManager.playerModels,"${scriptObject.getMember("name")}.js"))
                )
                if (layers[name] == null) {
                    layers[name] = arrayListOf(layerRenderer)
                } else layers[name]!!.add(layerRenderer)
            }
        }
        inner class RegisterRender : Function<JSObject, Unit> {
            override fun apply(scriptObject: JSObject) {
                setLivingAnimations = try { scriptObject.getMember("setLivingAnimations") as Boolean } catch (e:Throwable) {false}
                getRandomModelBox = try { scriptObject.getMember("getRandomModelBox") as Boolean } catch (e:Throwable) {false}
                setRotationAngles = try { scriptObject.getMember("setRotationAngles") as Boolean } catch (e:Throwable) {false}
                setTextureOffset = try { scriptObject.getMember("setTextureOffset") as Boolean } catch (e:Throwable) {false}
                setModelAttributes = try { scriptObject.getMember("setModelAttributes") as Boolean } catch (e:Throwable) {false}
                getTextureOffset = try { scriptObject.getMember("getTextureOffset") as Boolean } catch (e:Throwable) {false}
                render = try { scriptObject.getMember("render") as Boolean } catch (e:Throwable) {false}
            }
        }
    }

    class LayerJSFile(jsFile: File):MinecraftInstance(){
        private val scriptEngine = NashornScriptEngineFactory().scriptEngine
        private val jsText = jsFile.readText()
        val inv:Invocable
        var shouldCombineTextures = false
        init {
            scriptEngine.put("mc",mc)
            scriptEngine.put("kevinClient",KevinClient)
            scriptEngine.eval(jsText)
            inv = scriptEngine as Invocable
            shouldCombineTextures = try {
                scriptEngine.get("shouldCombineTextures") as Boolean
            } catch (e:Throwable){
                false
            }
        }
    }

    class LayerFromFile(val name: String,private val jsFile: LayerJSFile) : LayerRenderer<AbstractClientPlayer>{
        override fun doRenderLayer(
            entitylivingbaseIn: AbstractClientPlayer?,
            p_177141_2_: Float,
            p_177141_3_: Float,
            partialTicks: Float,
            p_177141_5_: Float,
            p_177141_6_: Float,
            p_177141_7_: Float,
            scale: Float
        ) {
            try {
                jsFile.inv.invokeFunction("doRenderLayer",entitylivingbaseIn,p_177141_2_,p_177141_3_,partialTicks,p_177141_5_,p_177141_6_,p_177141_7_,scale)
            } catch (e: Throwable) {
                Minecraft.logger.error("[Render] Error in Layer $name",e)
            }
        }

        override fun shouldCombineTextures() = jsFile.shouldCombineTextures
    }

    class ModelFromFile(private val jsFile: JSFile) : ModelBase() {
        private fun error(message:String,e:Throwable) {
            Minecraft.logger.error("[Renderer] Error in '$message' !", e)
        }

        override fun setTextureOffset(partName: String?, x: Int, y: Int) {
            val s = if (jsFile.setTextureOffset) {
                try {
                    jsFile.inv.invokeFunction("setTextureOffset",partName,x,y) as Boolean
                }catch (e:Throwable){
                    error("SetTextureOffset",e)
                    true
                }
            } else true
            if (s) super.setTextureOffset(partName, x, y)
        }

        override fun setModelAttributes(model: ModelBase?) {
            val s = if (jsFile.setModelAttributes) {
                try {
                    jsFile.inv.invokeFunction("setModelAttributes",model) as Boolean
                }catch (e:Throwable){
                    error("SetModelAttributes",e)
                    true
                }
            } else true
            if (s) super.setModelAttributes(model)
        }

        override fun getTextureOffset(partName: String?): TextureOffset {
            if (jsFile.getTextureOffset){
                try {
                    return jsFile.inv.invokeFunction("getTextureOffset",partName) as TextureOffset
                } catch (e:Throwable){
                    error("GetTextureOffset",e)
                }
            }
            return super.getTextureOffset(partName)
        }

        override fun render(
            entityIn: Entity?,
            p_78088_2_: Float,
            p_78088_3_: Float,
            p_78088_4_: Float,
            p_78088_5_: Float,
            p_78088_6_: Float,
            scale: Float
        ) {
            if (jsFile.render) {
                try {
                    jsFile.inv.invokeFunction("render",entityIn,p_78088_2_,p_78088_3_,p_78088_4_,p_78088_5_,p_78088_6_,scale)
                }catch (e: Throwable){
                    error("Render",e)
                }
            }
        }

        override fun setRotationAngles(
            limbSwing: Float,
            limbSwingAmount: Float,
            ageInTicks: Float,
            netHeadYaw: Float,
            headPitch: Float,
            scaleFactor: Float,
            entityIn: Entity?
        ) {
            if (jsFile.setRotationAngles) {
                try {
                    jsFile.inv.invokeFunction("setRotationAngles",limbSwing,limbSwingAmount,ageInTicks,netHeadYaw,headPitch,scaleFactor,entityIn)
                }catch (e: Throwable){
                    error("SetRotationAngles",e)
                }
            }
        }

        override fun setLivingAnimations(
            entitylivingbaseIn: EntityLivingBase?,
            p_78086_2_: Float,
            p_78086_3_: Float,
            partialTickTime: Float
        ) {
            if (jsFile.setLivingAnimations){
                try {
                    jsFile.inv.invokeFunction("setLivingAnimations",entitylivingbaseIn,p_78086_2_,p_78086_3_,partialTickTime)
                } catch (e:Throwable){
                    error("SetLivingAnimations",e)
                }
            }
        }

        override fun getRandomModelBox(rand: Random?): ModelRenderer {
            if (jsFile.getRandomModelBox) {
                try {
                    return jsFile.inv.invokeFunction("getRandomModelBox",rand) as ModelRenderer
                } catch (e:Throwable){
                    error("GetRandomModelBox",e)
                }
            }
            return super.getRandomModelBox(rand)
        }
    }

    class RenderFromFile(val jsFile: JSFile,val name: String,renderManager: RenderManager) : RendererLivingEntity<AbstractClientPlayer>(
        renderManager,
        models[name]!!,
        1F
    ) {
        init {
            layers[name]!!.forEach { this.addLayer(it) }
        }
        override fun getEntityTexture(entity: AbstractClientPlayer): ResourceLocation {
            return texturesList[name]!!.resource
        }
    }

    @EventTarget(true)
    fun onUpdate(event: UpdateEvent){
        if (texturesList["Fox"]==null){
            texturesList["Fox"] = Texture("Fox",ImageIO.read(javaClass.getResourceAsStream("/resources/entity/fox/fox.png")))
            texturesList["Fox_Sleep"] = Texture("Fox_Sleep",ImageIO.read(javaClass.getResourceAsStream("/resources/entity/fox/fox_sleep.png")))
            texturesList["Snow_Fox"] = Texture("Snow_Fox",ImageIO.read(javaClass.getResourceAsStream("/resources/entity/fox/snow_fox.png")))
            texturesList["Snow_Fox_Sleep"] = Texture("Snow_Fox_Sleep",ImageIO.read(javaClass.getResourceAsStream("/resources/entity/fox/snow_fox_sleep.png")))
        }
        if (!state) return
        when(playerModel.get()){
            "Fox" -> {
                resourceLocation = if (mc.thePlayer.isPlayerSleeping) texturesList["Fox_Sleep"]!!.resource else texturesList["Fox"]!!.resource
                fox = true
                renderer = null
            }
            "Snow_Fox" -> {
                resourceLocation = if (mc.thePlayer.isPlayerSleeping) texturesList["Snow_Fox_Sleep"]!!.resource else texturesList["Snow_Fox"]!!.resource
                fox = true
                renderer = null
            }
            else -> {
                fox = false
                val modelMode = playerModel.get()
                renderer = if (modelMode != "Player") renderers[modelMode] else null
            }
        }
    }

    override fun onDisable() {
        fox = false
    }

    private var resourceLocation: ResourceLocation? = null
    var fox = false
    var renderer: RendererLivingEntity<AbstractClientPlayer>? = null
    private val texturesList = HashMap<String,Texture>()
    override val tag: String
        get() = playerModel.get()

    class Texture(name:String,image: BufferedImage){
        val resource = ResourceLocation("kevin/texture/${name.lowercase(Locale.getDefault()).replace(" ", "_")}")
        init {
            val mc = Minecraft.getMinecraft()
            mc.addScheduledTask{mc.textureManager.loadTexture(resource, DynamicTexture(image))}
        }
    }
    class ModelFox : ModelBase(){
        var head: ModelRenderer = ModelRenderer(this,1,5)
        private var rightEar: ModelRenderer = ModelRenderer(this,8,1)
        private var leftEar: ModelRenderer = ModelRenderer(this,15,1)
        private var nose: ModelRenderer = ModelRenderer(this,6,18)
        private var body: ModelRenderer = ModelRenderer(this,24,15)
        private var rightHindLeg: ModelRenderer = ModelRenderer(this,13, 24)
        private var leftHindLeg: ModelRenderer = ModelRenderer(this,4, 24)
        private var rightFrontLeg: ModelRenderer = ModelRenderer(this,13, 24)
        private var leftFrontLeg: ModelRenderer = ModelRenderer(this,4, 24)
        private var tail: ModelRenderer = ModelRenderer(this,30, 0)
        init {
            this.head.textureWidth = 48F
            this.head.addBox(-3.0F, -2.0F, -5.0F, 8, 6, 6)
            this.head.setRotationPoint(-1.0F, 16.5F, -3.0F)

            this.rightEar.textureWidth = 48F
            this.rightEar.addBox(-3.0F, -4.0F, -4.0F, 2, 2, 1)
            this.head.addChild(this.rightEar)

            this.leftEar.textureWidth = 48F
            this.leftEar.addBox(3.0F, -4.0F, -4.0F, 2, 2, 1)
            this.head.addChild(this.leftEar)

            this.nose.textureWidth = 48F
            this.nose.addBox(-1.0F, 2.01F, -8.0F, 4, 2, 3)
            this.head.addChild(this.nose)

            this.body.textureWidth = 48F
            this.body.addBox(-3.0F, 3.999F, -3.5F, 6, 11, 6)
            this.body.setRotationPoint(0.0F, 16.0F, -6.0F)
            this.body.rotateAngleX = Math.PI.toFloat() / 2F

            this.rightHindLeg.textureWidth = 48F
            this.rightHindLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2,0.001F)
            this.rightHindLeg.setRotationPoint(-5.0F, 17.5F, 7.0F)

            this.leftHindLeg.textureWidth = 48F
            this.leftHindLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2,0.001F)
            this.leftHindLeg.setRotationPoint(-1.0F, 17.5F, 7.0F)

            this.rightFrontLeg.textureWidth = 48F
            this.rightFrontLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F)
            this.rightFrontLeg.setRotationPoint(-5.0F, 17.5F, 0.0F)

            this.leftFrontLeg.textureWidth = 48F
            this.leftFrontLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F)
            this.leftFrontLeg.setRotationPoint(-1.0F, 17.5F, 0.0F)

            this.tail.textureWidth = 48F
            this.tail.addBox(2.0F, 0.0F, -1.0F, 4, 9, 5)
            this.tail.setRotationPoint(-4.0F, 15.0F, -1.0F)
            this.tail.rotateAngleX = -0.05235988F
            this.body.addChild(this.tail)
        }

        override fun setLivingAnimations(
            entitylivingbaseIn: EntityLivingBase,
            p_78086_2_: Float,
            p_78086_3_: Float,
            partialTickTime: Float
        ) {
            super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime)
            body.rotateAngleX = Math.PI.toFloat() / 2f
            tail.rotateAngleX = -0.05235988f
            rightHindLeg.rotateAngleX = cos(p_78086_2_ * 0.6662f) * 1.4f * p_78086_3_
            leftHindLeg.rotateAngleX = cos(p_78086_2_ * 0.6662f + Math.PI.toFloat()) * 1.4f * p_78086_3_
            rightFrontLeg.rotateAngleX = cos(p_78086_2_ * 0.6662f + Math.PI.toFloat()) * 1.4f * p_78086_3_
            leftFrontLeg.rotateAngleX = cos(p_78086_2_ * 0.6662f) * 1.4f * p_78086_3_
            head.setRotationPoint(-1.0f, 16.5f, -3.0f)
            head.rotateAngleY = 0.0f
            head.rotateAngleZ = 0.0f
            rightHindLeg.showModel = true
            leftHindLeg.showModel = true
            rightFrontLeg.showModel = true
            leftFrontLeg.showModel = true
            body.setRotationPoint(0.0f, 16.0f, -6.0f)
            body.rotateAngleZ = 0.0f
            rightHindLeg.setRotationPoint(-5.0f, 17.5f, 7.0f)
            leftHindLeg.setRotationPoint(-1.0f, 17.5f, 7.0f)
            if (entitylivingbaseIn.isPlayerSleeping) {
                body.rotateAngleZ = (-Math.PI).toFloat() / 2f
                body.setRotationPoint(0.0f, 21.0f, -6.0f)
                tail.rotateAngleX = -2.6179938f
                if (this.isChild) {
                    tail.rotateAngleX = -2.1816616f
                    body.setRotationPoint(0.0f, 21.0f, -2.0f)
                }
                head.setRotationPoint(1.0f, 19.49f, -3.0f)
                head.rotateAngleX = 0.0f
                head.rotateAngleY = -2.0943952f
                head.rotateAngleZ = 0.0f
                rightHindLeg.showModel = false
                leftHindLeg.showModel = false
                rightFrontLeg.showModel = false
                leftFrontLeg.showModel = false
            } else if (entitylivingbaseIn.isSneaking) {
                body.rotateAngleX = Math.PI.toFloat() / 6f
                body.setRotationPoint(0.0f, 9.0f, -3.0f)
                tail.rotateAngleX = Math.PI.toFloat() / 4f
                tail.setRotationPoint(-4.0f, 15.0f, -2.0f)
                head.setRotationPoint(-1.0f, 10.0f, -0.25f)
                head.rotateAngleX = 0.0f
                head.rotateAngleY = 0.0f
                if (this.isChild) {
                    head.setRotationPoint(-1.0f, 13.0f, -3.75f)
                }

                rightHindLeg.rotateAngleX = -1.3089969f
                rightHindLeg.setRotationPoint(-5.0f, 21.5f, 6.75f)
                leftHindLeg.rotateAngleX = -1.3089969f
                leftHindLeg.setRotationPoint(-1.0f, 21.5f, 6.75f)
                rightFrontLeg.rotateAngleX = -0.2617994f
                leftFrontLeg.rotateAngleX = -0.2617994f
            }
        }

        override fun render(
            entityIn: Entity,
            p_78088_2_: Float,
            p_78088_3_: Float,
            p_78088_4_: Float,
            p_78088_5_: Float,
            p_78088_6_: Float,
            scale: Float
        ) {
            super.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale)
            setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn)
            GL11.glPushMatrix()
            this.head.renderWithRotation(scale)
            this.body.render(scale)
            this.rightHindLeg.render(scale)
            this.leftHindLeg.render(scale)
            this.rightFrontLeg.render(scale)
            this.leftFrontLeg.render(scale)
            //this.tail.renderWithRotation(scale)
            GL11.glPopMatrix()
        }

        override fun setRotationAngles(
            limbSwing: Float,
            limbSwingAmount: Float,
            ageInTicks: Float,
            netHeadYaw: Float,
            headPitch: Float,
            scaleFactor: Float,
            entityIn: Entity
        ) {
            super.setRotationAngles(
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scaleFactor,
                entityIn
            )
            if (entityIn !is EntityLivingBase) return
            if (!entityIn.isPlayerSleeping) {
                head.rotateAngleX = headPitch * (Math.PI.toFloat() / 180f)
                head.rotateAngleY = netHeadYaw * (Math.PI.toFloat() / 180f)
            }

            val killAura = KevinClient.moduleManager.getModule(KillAura::class.java)
            if (killAura.state&&(killAura.target!=null||killAura.sTarget!=null)){
                head.rotateAngleX = RotationUtils.serverRotation.pitch / (180f / Math.PI.toFloat())
            }

            if (entityIn.isPlayerSleeping) {
                head.rotateAngleX = 0.0f
                head.rotateAngleY = -2.0943952f
                head.rotateAngleZ = cos(ageInTicks * 0.027f) / 22.0f
            }
        }
    }
    class RenderFox(renderManager: RenderManager) : RendererLivingEntity<AbstractClientPlayer>(renderManager,ModelFox(),0.4F){
        init {
            this.addLayer(FoxHeldItemLayer(this.mainModel as ModelFox))
        }
        override fun getEntityTexture(entity: AbstractClientPlayer): ResourceLocation {
            return resourceLocation!!
        }
    }
    class FoxHeldItemLayer(private val modelFox: ModelFox) : LayerRenderer<AbstractClientPlayer>{
        override fun doRenderLayer(
            entitylivingbaseIn: AbstractClientPlayer,
            p_177141_2_: Float,
            p_177141_3_: Float,
            partialTicks: Float,
            p_177141_5_: Float,
            p_177141_6_: Float,
            p_177141_7_: Float,
            scale: Float
        ) {
            val flag: Boolean = entitylivingbaseIn.isPlayerSleeping
            val flag1: Boolean = entitylivingbaseIn.isChild
            GlStateManager.pushMatrix()
            if (flag1) {
                GlStateManager.scale(.75f, .75f, .75f)
                GlStateManager.translate(.0, .5, .209375)
            }

            GlStateManager.translate(
                (modelFox.head.rotationPointX / 16.0f),
                (modelFox.head.rotationPointY / 16.0f),
                (modelFox.head.rotationPointZ / 16.0f)
            )
            GlStateManager.rotate(0F,.0F,.0F,1F)
            GlStateManager.rotate(p_177141_6_,.0F,1F,.0F)
            GlStateManager.rotate(p_177141_7_,1F,.0F,.0F)
            if (entitylivingbaseIn.isChild) {
                if (flag) {
                    GlStateManager.translate(.4, .26, .15)
                } else {
                    GlStateManager.translate(.06, .26, -.3)
                }
            } else if (flag) {
                GlStateManager.translate(.46, .26, .22)
            } else {
                GlStateManager.translate(.06, .27, -.3)
            }

            GlStateManager.rotate(90.0f,1F,.0F,.0F)
            if (flag) {
                GlStateManager.rotate(90.0f,.0F,.0F,1F)
            }

            val itemstack = entitylivingbaseIn.heldItem

            if (itemstack != null&&itemstack.item != null&& itemstack.item is ItemBlock){
                val f = .25f
                GlStateManager.scale(-f, -f, f)
            }else if (itemstack != null&&itemstack.item != null&& itemstack.item !is ItemSword) {
                val f = .5f
                GlStateManager.scale(-f, -f, f)
            }

            if (itemstack != null) Minecraft.getMinecraft().itemRenderer.renderItem(
                entitylivingbaseIn,
                itemstack,
                ItemCameraTransforms.TransformType.GROUND
            )
            GlStateManager.popMatrix()
        }
        override fun shouldCombineTextures(): Boolean = false
    }
}