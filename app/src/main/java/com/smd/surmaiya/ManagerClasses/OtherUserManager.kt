package com.smd.surmaiya.ManagerClasses

import com.smd.surmaiya.itemClasses.User

object OtherUserManager {
    private var otherUser : User? = null
    fun addUser(user: User) {
        otherUser = user
    }
    fun getUser(): User? {
        return otherUser
    }
    fun removeUser() {
        otherUser = null
    }
}