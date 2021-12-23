package ru.itmo.bllab1.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@Controller
class HelloController {
    @RequestMapping(value = ["/main"], method = [RequestMethod.GET])
    fun get(): String {
        return "index.html"
    }
}
