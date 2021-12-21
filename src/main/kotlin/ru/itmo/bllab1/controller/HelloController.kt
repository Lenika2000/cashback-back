package ru.itmo.bllab1.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @RequestMapping(value = ["/cashback"], method = [RequestMethod.GET])
    fun get(): String {
        return "/index.html"
    }
}
