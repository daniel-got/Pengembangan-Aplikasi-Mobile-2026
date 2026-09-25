package com.example.helloworld

class Greeting {
    private val platform = getPlatform()
    private val userName = User.getName()
    private val userNim = User.getNim()
    fun device(): String {
        return sayPlatform(platform.name)
    }

    fun greetUser(): String {
        return sayHello(userName, userNim)
    }
}