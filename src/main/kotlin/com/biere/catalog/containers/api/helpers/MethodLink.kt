package com.biere.catalog.containers.api.helpers

import org.springframework.hateoas.Link

class MethodLink(val method: String, link: Link) : Link(link.href, link.rel)

fun Link.withMethod(method: String): MethodLink {
    return MethodLink(method, this)
}
