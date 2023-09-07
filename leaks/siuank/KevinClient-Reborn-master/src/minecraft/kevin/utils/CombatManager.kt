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

import kevin.event.*
import kevin.main.KevinClient
import net.minecraft.entity.EntityLivingBase

class CombatManager : Listenable,MinecraftInstance() {
    var target: EntityLivingBase? = null
        private set
    var inCombat=false
        private set
    private val lastAttackTimer = MSTimer()
    val attackedEntityList=mutableListOf<EntityLivingBase>()
    init {
        KevinClient.eventManager.registerListener(this)
    }
    override fun handleEvents() = true
    @EventTarget fun onUpdate(event: UpdateEvent) {
        if(mc.thePlayer==null) return

        inCombat=false

        if(!lastAttackTimer.hasTimePassed(1000)){
            inCombat=true
            return
        }

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase
                && entity.getDistanceToEntity(mc.thePlayer) < 7
                && EntityUtils.isSelected(entity, true)
                && !entity.isDead) {
                inCombat = true
                break
            }
        }

        if(target!=null){
            if(mc.thePlayer.getDistanceToEntity(target)>7||!inCombat||target!!.isDead){
                target=null
            }
        }

        attackedEntityList.map { it }.forEach {
            if (it.isDead) {
                KevinClient.eventManager.callEvent(EntityKilledEvent(it))
                attackedEntityList.remove(it)
            }
        }
    }
    @EventTarget fun onAttack(event: AttackEvent){
        val target=event.targetEntity

        if(target is EntityLivingBase && EntityUtils.isSelected(target,true)){
            this.target=target
            if(!attackedEntityList.contains(target))
                attackedEntityList.add(target)
        }
        lastAttackTimer.reset()
    }
    @EventTarget fun onWorld(event: WorldEvent){
        inCombat=false
        target=null
        attackedEntityList.clear()
    }

    fun getNearByEntity(radius: Float):EntityLivingBase?{
        return try {
            mc.theWorld.loadedEntityList
                .filter { mc.thePlayer.getDistanceToEntity(it)<radius&&EntityUtils.isSelected(it,true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer) }[0] as EntityLivingBase?
        }catch (e: Exception){
            null
        }
    }
}