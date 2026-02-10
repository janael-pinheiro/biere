package com.biere.catalog.infrastructure.adapter.input.rest.helpers

import org.springframework.hateoas.Link

class MethodLink(val method: String, link: Link) : Link(link.href, link.rel)

fun Link.withMethod(method: String): MethodLink {
    return MethodLink(method, this)
}
