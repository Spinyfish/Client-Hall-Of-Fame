package kevin.module.modules.misc

import kevin.module.Module
import kevin.module.ModuleCategory
import java.util.LinkedList


object ClientFriend : Module("ClientFriend", "Allow your hack don't attack your friend.", category = ModuleCategory.MISC) {
    val friendsName = LinkedList<String>()

    fun isFriend(name: String) : Boolean = friendsName.contains(name.lowercase())
}